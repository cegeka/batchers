package be.cegeka.batchers.taxcalculator.batch.config.remotepartitioning;

import be.cegeka.batchers.taxcalculator.batch.api.events.JobProgressEvent;
import com.hazelcast.config.*;
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

//            List<String> interfacesToAdd = new ArrayList<>();
            String i = "10.162.128.113";
//            String i = "192.168.50.1";
//            interfacesToAdd.add("192.168.50.1");
              InterfacesConfig interfacesConfig = network.getInterfaces().setEnabled(true);
              interfacesConfig.addInterface(i);
              interfacesConfig.addInterface("10.162.128.113");
//            if (interfacesToAdd.size() > 0) {
//                interfacesToAdd.forEach(interfaceToAdd -> interfacesConfig.addInterface(interfaceToAdd));
//            }

            JoinConfig join = network.getJoin();
            join.getMulticastConfig().setEnabled(true);
//            join.getMulticastConfig().setMulticastGroup(MulticastConfig.DEFAULT_MULTICAST_GROUP);
            join.getMulticastConfig().getTrustedInterfaces().add("192.168.50.1");

            join.getTcpIpConfig()
//                    .addMember(i)
//                    .addMember(batchersmasterIpIfPresent)
                    .addMember("192.168.50.4")
                    .addMember("10.162.128.112")
                    .setRequiredMember(null).setEnabled(true);
        }
    }

    public static void main(String ...args) throws UnknownHostException {
//        listNetworkINterfacesIps();
//        getInterfacesToAdd().stream().forEach(mask -> LOG.info("Mask is " + mask));
        new ClusterConfig().hazelcastInstance();
    }

    private static List<String> getInterfacesToAdd(){
        List<String> interfacesToAdd = listNetworkINterfacesIps()
                .stream()
                .filter(existingInterface -> existingInterface.startsWith(NET_INTERFACE_VBOX_PREFIX))
                .distinct().collect(Collectors.toList());
        return interfacesToAdd;
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
                    }
                }
            }
        } catch (Exception e){
            LOG.error("Failed getting the list of interfaces", e);
        }
        return ipList;
    }
}
