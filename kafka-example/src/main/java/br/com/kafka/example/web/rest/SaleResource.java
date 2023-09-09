package br.com.kafka.example.web.rest;

import br.com.kafka.example.dto.SaleDTO;
import br.com.kafka.example.service.SaleService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("sale")
@RequiredArgsConstructor
public class SaleResource {

    private final SaleService service;
    private final KafkaTemplate<Object, Object> template;

    @GetMapping
    public ResponseEntity<List<SaleDTO>> getAll() {
        return new ResponseEntity<>(service.getAll(), HttpStatus.OK);
    }

    @SneakyThrows
    @PostMapping("/{count}/{topic}")
    public void send(@PathVariable("count") int count, @PathVariable("topic") String topic) {
        for (int i = 0; i < count; i++) {
            SaleDTO dto = new SaleDTO();
            dto.setCodUser(i);
            dto.setCodProduct("COD-" + i);
            dto.setBrand("BRAND-" + i);
            dto.setDescription("PRODUCT-" + i);
            dto.setPrice(new BigDecimal("10"));
            dto.setHour(LocalTime.now());
            template.send(topic, dto);
        }
        log.info("Publish finish...");
    }

}
