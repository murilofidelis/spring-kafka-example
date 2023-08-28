package br.com.kafka.example.service.impl;

import br.com.kafka.example.dto.SaleDTO;
import br.com.kafka.example.exception.InvalidDataException;
import br.com.kafka.example.mapper.SaleMapper;
import br.com.kafka.example.repository.SaleRepository;
import br.com.kafka.example.service.SaleDetailService;
import br.com.kafka.example.service.SaleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SaleServiceImpl implements SaleService {

    private final SaleMapper mapper;
    private final SaleRepository repository;
    private final SaleDetailService saleDetailService;

    @Override
    public List<SaleDTO> getAll() {
        return mapper.listDTO(repository.findAll());
    }

    @Override
    public void save(SaleDTO dto) {
        Throwable error = null;
        try {
            validated(dto);
            repository.save(mapper.toEntity(dto));
        } catch (Exception ex) {
            log.error("ERROR SaleServiceImpl: {}", ex.getMessage());
            error = ex;
        } finally {
            saleDetailService.saveDetail(dto, error);
        }
    }

    private void validated(SaleDTO dto) {
        if (anyIsNull(
                dto.getCodProduct(),
                dto.getDescription(),
                dto.getBrand(),
                dto.getCodUser(),
                dto.getPrice()) || isaProductMock(dto)) {
            throw new InvalidDataException("invalid data");
        }
    }

    private boolean isaProductMock(SaleDTO dto) {
        return "COD-100".equals(dto.getCodProduct()) || "COD-150".equals(dto.getCodProduct()) || "COD-200".equals(dto.getCodProduct());
    }

    private boolean anyIsNull(Object... data) {
        waitTime(1);
        for (Object datum : data) {
            if (datum == null) {
                return true;
            }
        }
        return false;
    }

    private void waitTime(long time) {
        LocalTime now = LocalTime.now();
        LocalTime before = LocalTime.now().plusSeconds(time);
        while (now.isBefore(before)) {
            now = LocalTime.now();
        }
    }

}
