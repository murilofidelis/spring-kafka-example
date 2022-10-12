package br.com.kafka.example.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalTime;

@Getter
@Setter
public class SaleDTO implements Serializable {

    private String codProduct;

    private String description;

    private String brand;

    private BigDecimal price;

    private Integer codUser;

    private LocalTime hour;

    @Override
    public String toString() {
        return "SaleDTO{" +
                "codProduct='" + codProduct + '\'' +
                ", description='" + description + '\'' +
                ", hour=" + hour +
                '}';
    }
}
