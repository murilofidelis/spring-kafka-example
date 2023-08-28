package br.com.kafka.example.domain;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "TAB_SALE", schema = "sales")
public class Sale {

    @Id
    @SequenceGenerator(name = "sales_seq", schema = "sales", sequenceName = "sales_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sales_seq")
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
