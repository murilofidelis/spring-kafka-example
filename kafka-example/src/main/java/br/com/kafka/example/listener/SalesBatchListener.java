package br.com.kafka.example.listener;

import br.com.kafka.example.dto.SaleDTO;
import br.com.kafka.example.service.SaleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.BatchListenerFailedException;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class SalesBatchListener {

    private final SaleService saleService;

    @KafkaListener(
            topics = "${spring.kafka.topics.sales-batch.name}",
            groupId = "sales-group-batch",
            containerFactory = "kafkaListenerBatchContainerFactory")
    public void salesListenerList(@Payload List<SaleDTO> data, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) List<Integer> partitions, @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
        List<String> cods = data.stream().map(SaleDTO::getCodProduct).collect(Collectors.toList());
        int size = data.size();
        log.info("RECEIVED Message: {} size: {} ", cods, size);
        for (int i = 0; i < data.size(); i++) {
            log.info("received message='{}', with partition='{}', offset='{}'", data.get(i), partitions.get(i), offsets.get(i));
            SaleDTO value = data.get(i);
            process(value, i);
        }
        log.info("FINISH Message: {} size: {} ", cods, size);
    }

    private void process(SaleDTO sale, int recordIndex) {
        try {
            saleService.save(sale);
        } catch (Exception ex) {
            throw new BatchListenerFailedException("Failed to process", ex, recordIndex);
        }
    }

    @KafkaListener(
            topics = "${spring.kafka.topics.sales-batch-dtl.name}",
            groupId = "sales-group-batch",
            containerFactory = "kafkaListenerContainerFactory")
    public void salesBatchListenerListDlt(@Payload ConsumerRecord<String, SaleDTO> record) {
        log.warn("salesBatchListenerListDlt: {}", record.value());
    }
}
