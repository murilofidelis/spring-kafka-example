package br.com.kafka.example.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicsConfig {

    @Bean
    public NewTopic salesTopic(@Value("${spring.kafka.topics.sales.name}") String topic,
                               @Value("${spring.kafka.topics.sales.partitions}") Integer partitions,
                               @Value("${spring.kafka.replications}") Integer replications) {
        return TopicBuilder.name(topic)
                .partitions(partitions)
                .replicas(replications)
                .configs(getConfigs())
                .build();
    }

    @Bean
    public NewTopic salesDltTopic(@Value("${spring.kafka.topics.sales-dlt.name}") String topic,
                                  @Value("${spring.kafka.default-partitions}") Integer partitions,
                                  @Value("${spring.kafka.replications}") Integer replications) {
        return TopicBuilder.name(topic)
                .partitions(partitions)
                .replicas(replications)
                .build();
    }

    @Bean
    public NewTopic salesBatchTopic(@Value("${spring.kafka.topics.sales-batch.name}") String topic,
                                    @Value("${spring.kafka.default-partitions}") Integer partitions,
                                    @Value("${spring.kafka.replications}") Integer replications) {
        return TopicBuilder.name(topic)
                .partitions(partitions)
                .replicas(replications)
                .configs(getConfigs())
                .build();
    }

    @Bean
    public NewTopic salesBatchDltTopic(@Value("${spring.kafka.topics.sales-batch-dtl.name}") String topic,
                                       @Value("${spring.kafka.default-partitions}") Integer partitions,
                                       @Value("${spring.kafka.replications}") Integer replications) {
        return TopicBuilder.name(topic)
                .partitions(partitions)
                .replicas(replications)
                .build();
    }

    private Map<String, String> getConfigs() {
        Map<String, String> configs = new HashMap<>();
        configs.put("retention.ms", "10800000");
        return configs;
    }
}
