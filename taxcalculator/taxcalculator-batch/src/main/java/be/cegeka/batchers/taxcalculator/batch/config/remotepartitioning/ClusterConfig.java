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
    public static final String NET_INTERFACE_DOCKER_PREFIX = "172.17.0";
    public static final String NET_INTERFACE_INTERNAL_LAN_PREFIX = "10.162.128";

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

    private static String getBatchersmasterIpIfPresent() {
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
        //if batchersmaster in /etc/hosts points to a IP, try to add it to the cluster
        //if it does not, the cluster will just broadcast on UDP
        buildBatchersmasterTCPConfigIfNeeded(cfg);
        HazelcastInstance hz = Hazelcast.newHazelcastInstance(cfg);
        return hz;
    }

    private void buildBatchersmasterTCPConfigIfNeeded(Config cfg) {
        String batchersmasterIpIfPresent = getBatchersmasterIpIfPresent();
        LOG.info("Batchers master Ip is " + batchersmasterIpIfPresent);
        if (batchersmasterIpIfPresent != null && !batchersmasterIpIfPresent.equals("127.0.0.1")) {
            NetworkConfig network = cfg.getNetworkConfig();

            List<String> interfacesToAdd = getInterfacesToAdd();

            InterfacesConfig interfacesConfig = network.getInterfaces().setEnabled(true);
            interfacesToAdd.stream().forEach(inter -> {
                LOG.info("Added interface: " + inter);
                interfacesConfig.addInterface(inter);
            });

            JoinConfig join = network.getJoin();
            join.getMulticastConfig().setEnabled(true);

            LOG.info("Added batchersmaster IP to the cluster " + batchersmasterIpIfPresent);
            join.getTcpIpConfig()
                    .addMember(batchersmasterIpIfPresent)
                    .setRequiredMember(null).setEnabled(true);
        }
    }

    public static void main(String... args) throws UnknownHostException {
        new ClusterConfig().hazelcastInstance();
    }

    private static List<String> getInterfacesToAdd() {
        List<String> interestingINterfacesPrefixes = new ArrayList<>();

        String batchersmasterInterfacePrefix = getBatchersmasterInterfacePrefix();
        LOG.info("BatchersmasterInterfacePrefix is " + batchersmasterInterfacePrefix);
        if (batchersmasterInterfacePrefix != null) {
            interestingINterfacesPrefixes.add(batchersmasterInterfacePrefix);
        } else {
            interestingINterfacesPrefixes.add(NET_INTERFACE_INTERNAL_LAN_PREFIX);
            interestingINterfacesPrefixes.add(NET_INTERFACE_VBOX_PREFIX);
            interestingINterfacesPrefixes.add(NET_INTERFACE_DOCKER_PREFIX);
        }

        List<String> interfacesToAdd = listNetworkINterfacesIps()
                .stream()
                .filter(existingInterface ->
                                interestingINterfacesPrefixes.stream()
                                        .filter(interestingINterfacePrefix ->
                                                existingInterface.startsWith(interestingINterfacePrefix)).count() > 0
                )
                .distinct().collect(Collectors.toList());
        return interfacesToAdd;
    }

    public static List<String> listNetworkINterfacesIps() {
        List<String> ipList = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface netint : Collections.list(nets)) {
                Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
                for (InetAddress inetAddress : Collections.list(inetAddresses)) {
                    if (inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress()) {
                        String ip = inetAddress.getHostAddress();
                        ipList.add(ip);
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("Failed getting the list of interfaces", e);
        }
        return ipList;
    }

    public static String getBatchersmasterInterfacePrefix() {
        String batchersmasterIpIfPresent = getBatchersmasterIpIfPresent();
        if (batchersmasterIpIfPresent != null) {
            String[] ipParts = batchersmasterIpIfPresent.split("\\.");
            if (ipParts.length == 4) {
                //return only the first 3 parts of the IP for example 192.168.1.
                return ipParts[0] + "." + ipParts[1]  + "." + ipParts[2] + ".";
            }
        }
        return null;
    }

}
