package br.com.kafka.example.service.impl;

import br.com.kafka.example.dto.SaleDTO;
import br.com.kafka.example.mapper.SaleMapper;
import br.com.kafka.example.service.SaleService;
import br.com.kafka.example.exception.AppException;
import br.com.kafka.example.repository.SaleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SaleServiceImpl implements SaleService {

    private final SaleMapper mapper;
    private final SaleRepository repository;

    @Override
    public List<SaleDTO> getAll() {
        return mapper.listDTO(repository.findAll());
    }

    @Override
    public void save(SaleDTO dto) {
        validated(dto);
        repository.save(mapper.toEntity(dto));
    }

    private void validated(SaleDTO dto) {
        if (anyIsNull(
                dto.getCodProduct(),
                dto.getDescrition(),
                dto.getBrand(),
                dto.getCodUser(),
                dto.getPrice())) {
            throw new AppException("invalid data");
        }
    }

    private boolean anyIsNull(Object... data) {
        for (Object datum : data) {
            if (datum == null) {
                return true;
            }
        }
        return false;
    }

}
