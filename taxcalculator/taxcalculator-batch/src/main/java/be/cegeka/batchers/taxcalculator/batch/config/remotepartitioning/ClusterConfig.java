package be.cegeka.batchers.taxcalculator.batch.config.remotepartitioning;

import be.cegeka.batchers.taxcalculator.batch.api.events.JobProgressEvent;
import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.MulticastConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.Message;

import java.net.*;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

@Configuration
@Profile(value = {"remotePartitioningMaster", "remotePartitioningSlave", "testRemotePartitioning"})
public class ClusterConfig {

    private static final Logger LOG = LoggerFactory.getLogger(ClusterConfig.class);

    private static final String NET_INTERFACE_VBOX_PREFIX = "192.168.50";

    public int getClusterSize() {
        return hazelcastInstance().getCluster().getMembers().size();
    }

    @Bean
    public BlockingQueue<Message<?>> requests() {
        return hazelcastInstance().getQueue("requests");
    }

    @Bean
    public BlockingQueue<Message<?>> results() {
        return hazelcastInstance().getQueue("results");
    }

    @Bean
    public ITopic<JobProgressEvent> jobProgressEventsTopic() {
        return hazelcastInstance().getTopic("jobProgress");
    }

    private String getBatchersmasterIpIfPresent(){
        String hostAddress = null;
        try {
            InetAddress address = InetAddress.getByName("batchersmaster");
            hostAddress = address.getHostAddress();
            System.out.println(hostAddress);
        } catch (UnknownHostException e) {
            LOG.warn("Did not find an Ip for batchersmaster");
        }
        return hostAddress;
    }

    @Bean
    public HazelcastInstance hazelcastInstance() {
        Config cfg = new Config();
        buildBatchersmasterTCPConfigIfNeeded(cfg);
        HazelcastInstance hz = Hazelcast.newHazelcastInstance(cfg);
        return hz;
    }

    private void buildBatchersmasterTCPConfigIfNeeded(Config cfg) {
        String batchersmasterIpIfPresent = getBatchersmasterIpIfPresent();
        if (batchersmasterIpIfPresent != null && !batchersmasterIpIfPresent.equals("127.0.0.1")) {
            NetworkConfig network = cfg.getNetworkConfig();

            network.getInterfaces().setEnabled(true).addInterface("192.168.51.*");
            JoinConfig join = network.getJoin();
            join.getMulticastConfig().setEnabled(true);
            join.getMulticastConfig().setMulticastGroup(MulticastConfig.DEFAULT_MULTICAST_GROUP);
            LOG.error("getMulticastGroup" + join.getMulticastConfig().getMulticastGroup());

            join.getTcpIpConfig().addMember(batchersmasterIpIfPresent).setRequiredMember(null).setEnabled(true);
        }
    }

    public static void main(String ...args) throws UnknownHostException {
//        listNetworkINterfacesIps();
        getInterfacesToAdd().stream().forEach(mask -> LOG.info("Mask is " + mask));
    }

    private static List<String> getInterfacesToAdd(){
        Set<String> interfacesToSearchFor = new TreeSet(){{
            add(NET_INTERFACE_VBOX_PREFIX);
        }};
        List<String> interfacesToAdd = listNetworkINterfacesIps()
                .stream()
                .filter(existingInterface -> interfacesToSearchFor.contains(existingInterface))
                .map(foundINterfacePrefix -> foundINterfacePrefix + ".*")
                .distinct().collect(Collectors.toList());
    }

    public static List<String> listNetworkINterfacesIps() {
        List<String> ipList = new ArrayList<>();
        try{
            Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface netint : Collections.list(nets)){
                Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
                for (InetAddress inetAddress : Collections.list(inetAddresses)) {
                    if (inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress()){
                        String ip = inetAddress.getHostAddress();
                        ipList.add(ip);
                        LOG.info("InetAddress: " + ip);
                    }
                }
            }
            LOG.info("" + ipList.size());
        } catch (Exception e){
            LOG.error("Failed getting the list of interfaces", e);
        }
        return ipList;
    }
}
