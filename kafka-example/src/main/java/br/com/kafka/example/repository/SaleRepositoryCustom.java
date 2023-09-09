package br.com.kafka.example.repository;

import br.com.kafka.example.dto.SaleDTO;

import java.util.List;

public interface SaleRepositoryCustom {

    void insertBatch(List<SaleDTO> sale);
}
