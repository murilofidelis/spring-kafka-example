package br.com.kafka.example.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class SaleDTO implements Serializable {

    private String codProduct;

    private String descrition;

    private String brand;

    private BigDecimal price;

    private Integer codUser;

}
