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

@Getter
@Setter
@Entity
@Table(name = "TAB_SALE_DETAIL", schema = "sales")
public class SaleDetail {

    @Id
    @SequenceGenerator(name = "sales_detail_seq", schema = "sales", sequenceName = "sales_detail_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sales_detail_seq")
    @Column(name = "ID")
    private Long id;

    @Column(name = "COD_PRODUCT")
    private String codProduct;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "ERROR")
    private String error;

}
