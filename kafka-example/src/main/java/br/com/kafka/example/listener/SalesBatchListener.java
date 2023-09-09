package br.com.kafka.example.listener;

import br.com.kafka.example.dto.SaleDTO;
import br.com.kafka.example.service.SaleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

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
        process(data);
    }

    private void process(List<SaleDTO> sales) {
        saleService.save(sales);
    }

    @KafkaListener(
            topics = "${spring.kafka.topics.sales-batch-dtl.name}",
            groupId = "sales-group-batch",
            containerFactory = "kafkaListenerContainerFactory")
    public void salesBatchListenerListDlt(@Payload ConsumerRecord<String, SaleDTO> record) {
        log.warn("salesBatchListenerListDlt: {}", record.value());
    }
}
