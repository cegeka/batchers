package be.cegeka.batchers.taxcalculator.batch.config.remotepartitioning;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.messaging.Message;

import java.util.concurrent.BlockingQueue;

@Configuration
@Profile(value = {"remotePartitioningMaster", "remotePartitioningSlave", "testRemotePartitioning"})
public class ClusterConfig {

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
    public HazelcastInstance hazelcastInstance() {
        Config cfg = new Config();
        HazelcastInstance hz = Hazelcast.newHazelcastInstance(cfg);
        return hz;
    }
}
