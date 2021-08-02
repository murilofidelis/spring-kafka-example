package br.com.kafka.example.web.rest;

import br.com.kafka.example.dto.SaleDTO;
import br.com.kafka.example.service.SaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("sale")
@RequiredArgsConstructor
public class SaleResource {

    private final SaleService service;

    @GetMapping
    public ResponseEntity<List<SaleDTO>> getAll() {
        return new ResponseEntity<>(service.getAll(), HttpStatus.OK);
    }

}
