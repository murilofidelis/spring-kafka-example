package br.com.kafka.example.mapper;

import br.com.kafka.example.dto.SaleDTO;
import br.com.kafka.example.domain.Sale;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SaleMapper extends AbstractMapper<Sale, SaleDTO> {

}
