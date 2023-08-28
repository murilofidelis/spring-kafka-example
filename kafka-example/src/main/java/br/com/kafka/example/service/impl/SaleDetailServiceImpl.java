package br.com.kafka.example.service.impl;

import br.com.kafka.example.domain.SaleDetail;
import br.com.kafka.example.dto.SaleDTO;
import br.com.kafka.example.repository.SaleDetailRepository;
import br.com.kafka.example.service.SaleDetailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.CommitFailedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SaleDetailServiceImpl implements SaleDetailService {

    private final SaleDetailRepository saleDetailRepository;

    @Override
    @Transactional(rollbackFor = CommitFailedException.class)
    public void saveDetail(SaleDTO sale, Throwable error) {
        SaleDetail detail = new SaleDetail();
        detail.setCodProduct(sale.getCodProduct());
        detail.setStatus(getStatus(error));
        detail.setError(getError(error));
        saleDetailRepository.save(detail);
    }

    private String getError(Throwable error) {
        if (error != null) {
            String err = error.getMessage();
            return StringUtils.abbreviate(err, 500);
        }
        return null;
    }

    private String getStatus(Throwable error) {
        if (error == null) {
            return "SUCCESS";
        }
        return "ERROR";
    }
}
