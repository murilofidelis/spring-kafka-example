package br.com.kafka.example.listener;

import br.com.kafka.example.dto.SaleDTO;
import br.com.kafka.example.service.SaleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SalesListener {

    private final SaleService saleService;

    @KafkaListener(
            topics = "${spring.kafka.topic.name}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory")
    public void salesListener(@Payload ConsumerRecord<String, SaleDTO> record, Acknowledgment ack) {

        log.info("Received Message, {}", record.offset());
        log.info("key: {}", record.key());
        process(record.value());
        ack.acknowledge();
    }

    private void process(SaleDTO sale) {
        saleService.save(sale);
    }

}
