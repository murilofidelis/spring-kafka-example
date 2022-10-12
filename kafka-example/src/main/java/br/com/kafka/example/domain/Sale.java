package br.com.kafka.example.domain;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "TAB_SALE", schema = "sales")
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "COD_PRODUCT")
    private String codProduct;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "BRAND")
    private String brand;

    @Column(name = "PRICE")
    private BigDecimal price;

    @Column(name = "COD_USER")
    private Integer codUser;
}
