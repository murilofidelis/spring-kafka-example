package br.com.kafka.example.service;

import br.com.kafka.example.dto.SaleDTO;

import java.util.List;

public interface SaleService {

    List<SaleDTO> getAll();

    void save(SaleDTO dto);

}
