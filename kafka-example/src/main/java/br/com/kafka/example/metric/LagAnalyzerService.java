package br.com.kafka.example.metric;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListConsumerGroupOffsetsOptions;
import org.apache.kafka.clients.admin.ListConsumerGroupOffsetsResult;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LagAnalyzerService {

    private final AdminClient adminClient;
    private final KafkaConsumer<String, String> consumer;

    @SneakyThrows
    public long getLag(String groupId, String topicName) {
        Map<TopicPartition, Long> topicPartitionLongMap = analyzeLag(groupId, topicName);
        return topicPartitionLongMap.values().stream().mapToLong(lag -> lag).sum();
    }

    public Map<TopicPartition, Long> analyzeLag(String groupId, String topicName) throws ExecutionException, InterruptedException {
        Map<TopicPartition, Long> consumerGrpOffsets = getConsumerGrpOffsets(groupId, topicName);
        Map<TopicPartition, Long> producerOffsets = getProducerOffsets(consumerGrpOffsets);
        return computeLags(consumerGrpOffsets, producerOffsets);
    }

    private Map<TopicPartition, Long> getConsumerGrpOffsets(String groupId, String topicName) throws ExecutionException, InterruptedException {
        ListConsumerGroupOffsetsOptions topicPartitions = getListConsumerGroupOffsetsOptions(topicName);
        ListConsumerGroupOffsetsResult info = adminClient.listConsumerGroupOffsets(groupId, topicPartitions);
        Map<TopicPartition, OffsetAndMetadata> metadataMap = info.partitionsToOffsetAndMetadata().get();
        Map<TopicPartition, Long> groupOffset = new HashMap<>();
        for (Map.Entry<TopicPartition, OffsetAndMetadata> entry : metadataMap.entrySet()) {
            if (entry.getKey() != null && entry.getValue() != null) {
                TopicPartition key = entry.getKey();
                OffsetAndMetadata metadata = entry.getValue();
                groupOffset.putIfAbsent(new TopicPartition(key.topic(), key.partition()), metadata.offset());
            }
        }
        return groupOffset;
    }

    private ListConsumerGroupOffsetsOptions getListConsumerGroupOffsetsOptions(String topicName) {
        List<PartitionInfo> partitions = consumer.partitionsFor(topicName);
        ListConsumerGroupOffsetsOptions listConsumerGroupOffsetsOptions = new ListConsumerGroupOffsetsOptions();
        List<TopicPartition> topicPartitions = new ArrayList<>();
        for (int partition = 0; partition < partitions.size(); partition++) {
            topicPartitions.add(new TopicPartition(topicName, partition));
        }
        listConsumerGroupOffsetsOptions.topicPartitions(topicPartitions);
        return listConsumerGroupOffsetsOptions;
    }

    private Map<TopicPartition, Long> getProducerOffsets(Map<TopicPartition, Long> consumerGrpOffset) {
        List<TopicPartition> topicPartitions = new LinkedList<>();
        for (Map.Entry<TopicPartition, Long> entry : consumerGrpOffset.entrySet()) {
            TopicPartition key = entry.getKey();
            topicPartitions.add(new TopicPartition(key.topic(), key.partition()));
        }
        return consumer.endOffsets(topicPartitions);
    }

    private Map<TopicPartition, Long> computeLags(Map<TopicPartition, Long> consumerGrpOffsets, Map<TopicPartition, Long> producerOffsets) {
        Map<TopicPartition, Long> lags = new HashMap<>();
        for (Map.Entry<TopicPartition, Long> entry : consumerGrpOffsets.entrySet()) {
            Long producerOffset = producerOffsets.get(entry.getKey());
            Long consumerOffset = consumerGrpOffsets.get(entry.getKey());
            long lag = Math.abs(Math.max(0, producerOffset) - Math.max(0, consumerOffset));
            lags.putIfAbsent(entry.getKey(), lag);
        }
        return lags;
    }

    public Long getTotalNumberOfMessagesInATopic(String topic) {
        List<TopicPartition> partitions = consumer.partitionsFor(topic).stream()
                .map(p -> new TopicPartition(topic, p.partition()))
                .collect(Collectors.toList());
        consumer.assign(partitions);
        consumer.seekToEnd(Collections.emptySet());
        Map<TopicPartition, Long> endPartitions = partitions.stream()
                .collect(Collectors.toMap(Function.identity(), consumer::position));
        return partitions.stream().mapToLong(endPartitions::get).sum();
    }

}
