package com.ibm.fsd.smc.ms.sp.service.impl;

import com.ibm.fsd.smc.ms.sp.domain.StockPriceEntity;
import com.ibm.fsd.smc.ms.sp.repository.StockPriceRepository;
import com.ibm.fsd.smc.ms.sp.service.StockPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Stock Price Service Implement Class
 */
@Service
public class StockPriceServiceImpl implements StockPriceService {

    @Autowired
    private StockPriceRepository companyRepo;

    @Override
    public List<StockPriceEntity> findAll() {
        return companyRepo.findAll();
    }

    @Override
    public void save(StockPriceEntity stockExchange) {
        companyRepo.save(stockExchange);
    }

    @Override
    public StockPriceEntity findById(Integer id) {
         Optional<StockPriceEntity> result = companyRepo.findById(id);
         if(result.isPresent()){
             return result.get();
         }else {
             return null;
         }
    }

    @Override
    public void deleteById(Integer id) {
        companyRepo.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(List<StockPriceEntity> list) {
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                /*if (i % 2 == 1) {
                    throw new RuntimeException();
                }*/
                companyRepo.save(list.get(i));
            }
        }
    }
}
