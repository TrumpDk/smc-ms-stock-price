package com.ibm.fsd.smc.ms.sp.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Stock Price Entity
 * Mapping with db table `t_stock_price`
 */
@Entity
@Table(name = "t_stock_price")
@Getter
@Setter
@ToString
public class StockPriceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "stock_exchange")
    private String stockExchange;

    @Column(name = "company_stock_code")
    private String companyStockCode;

    @Column(name = "current_price")
    private double currentPrice;

    @Column(name = "date_time")
    private Timestamp dateTime;

}
