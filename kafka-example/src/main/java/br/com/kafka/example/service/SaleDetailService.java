package br.com.kafka.example.service;

import br.com.kafka.example.dto.SaleDTO;

public interface SaleDetailService {

    void saveDetail(SaleDTO sale, Throwable error);

}
