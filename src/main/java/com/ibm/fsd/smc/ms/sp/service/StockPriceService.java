package com.ibm.fsd.smc.ms.sp.service;

import com.ibm.fsd.smc.ms.sp.domain.StockPriceEntity;

import java.util.List;

/**
 * Stock Price Service
 */
public interface StockPriceService {

    List<StockPriceEntity> findAll();

    void save(StockPriceEntity user);

    void save(List<StockPriceEntity> list);

    StockPriceEntity findById(Integer id);

    void deleteById(Integer id);
}
