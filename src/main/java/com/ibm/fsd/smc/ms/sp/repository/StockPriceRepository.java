package com.ibm.fsd.smc.ms.sp.repository;

import com.ibm.fsd.smc.ms.sp.domain.StockPriceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Stock Price Repository
 */
public interface StockPriceRepository extends JpaRepository<StockPriceEntity, Integer> {
}
