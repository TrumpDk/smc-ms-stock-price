package com.ibm.fsd.smc.ms.sp.controller;

import com.ibm.fsd.smc.ms.sp.domain.StockPriceEntity;
import com.ibm.fsd.smc.ms.sp.service.StockPriceService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Stock Price Controller
 */
@CrossOrigin
@RestController
//@RequestMapping("/sp")
@Slf4j
public class StockPriceController {

    @Autowired
    private StockPriceService stockPriceSvc;

    @GetMapping("/stock_prices")
    public List<StockPriceEntity> findAll() {
        return stockPriceSvc.findAll();
    }

    @GetMapping("/{id}")
    public StockPriceEntity findStockPrice(@PathVariable("id") Integer id) {
        return stockPriceSvc.findById(id);
    }

    @PostMapping("/addition")
    public ResponseEntity<Boolean> addStockPrice(@RequestBody StockPriceEntity stockPrice){
        Boolean result = Boolean.FALSE;
        stockPriceSvc.save(stockPrice);
        result = Boolean.TRUE;
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Boolean> updateStockPrice(@RequestBody StockPriceEntity stockPrice){
        Boolean result = Boolean.FALSE;
        stockPriceSvc.save(stockPrice);
        result = Boolean.TRUE;
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteStockPrice(@PathVariable("id") Integer id){
        Boolean result = Boolean.FALSE;
        stockPriceSvc.deleteById(id);
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @GetMapping("/sample_excel")
    public void downloadSampleExcel(HttpServletResponse response) throws Exception {
        // get the download file
        ClassPathResource classPathResource = new ClassPathResource("static/file/sample_stock_data.xlsx");
        InputStream fis =  classPathResource.getInputStream();

        /*
        Resource resource = new ClassPathResource("static/file/sample_stock_data.xlsx");
        File file = resource.getFile();
        FileInputStream fis = new FileInputStream(file);
        */

        // set response
        response.setContentType("application/force-download");
        response.addHeader("Content-disposition", "attachment;fileName=" + "sample_stock_data.xlsx");

        // download the file to client browser
        OutputStream os = response.getOutputStream();
        byte[] buf = new byte[1024];
        int len = 0;
        while((len = fis.read(buf)) != -1) {
            os.write(buf, 0, len);
        }
        fis.close();
    }

    @RequestMapping("/upload")
    public ResponseEntity<String> uploadStockPrice (@RequestParam("file") MultipartFile file) {
        log.debug("upload method....");
        try {
            if(!ObjectUtils.isEmpty(file)) {
                InputStream inputStream = file.getInputStream();
                Workbook book = book = new XSSFWorkbook(inputStream);;
//                if (ExcelUtil.isExcel2003(file.getName())) {
//                    book = new HSSFWorkbook(inputStream);
//                }
//                if (ExcelUtil.isExcel2007(file.getName())) {
//                    book = new XSSFWorkbook(inputStream);
//                }
                int sheetsNumber = book.getNumberOfSheets();
                Sheet sheet = book.getSheetAt(0);
                int allRowNum = sheet.getLastRowNum();
                if (allRowNum == 0) {
                    return new ResponseEntity("The content is empty in the file!", HttpStatus.EXPECTATION_FAILED);
                }

                List<StockPriceEntity> stockPrices = new ArrayList<>();

                /**Read file content
                 * Because the 1st line is header,so get the content data from 2nd row.
                 */
                for (int i = 1; i <= allRowNum; i++) {
                    StockPriceEntity stockPrice = new StockPriceEntity();
                    //Get row i data
                    Row row = sheet.getRow(i);
                    if (row != null) {
                        Cell cell1 = row.getCell(0); //value of cell1: company code
                        Cell cell2 = row.getCell(1); //value of cell2: stock exchange
                        Cell cell3 = row.getCell(2); //value of cell3: stock price
                        Cell cell4 = row.getCell(3); //value of cell4: date
                        Cell cell5 = row.getCell(4); //value of cell4: time

                        // verify & read company code
                        if (cell1 != null) {
                            cell1.setCellType(CellType.STRING);
                            stockPrice.setCompanyStockCode(cell1.getStringCellValue());
                        } else {
                            return new ResponseEntity("The data of line" + i + ",column 1 can't empty", HttpStatus.EXPECTATION_FAILED);
                        }

                        // verify & read stock exchange
                        if (cell2 != null) {
                            cell2.setCellType(CellType.STRING);
                            stockPrice.setStockExchange(cell2.getStringCellValue());
                        } else {
                            return new ResponseEntity("The data of line" + i + ",column 2 can't empty", HttpStatus.EXPECTATION_FAILED);
                        }

                        // verify & read stock price
                        if (cell3 != null) {
                            cell3.setCellType(CellType.NUMERIC);
                            stockPrice.setCurrentPrice(cell3.getNumericCellValue());
                        } else {
                            return new ResponseEntity("The data of line" + i + ",column 3 can't empty", HttpStatus.EXPECTATION_FAILED);
                        }

                        // verify & read date
                        if (cell4 != null) {
                            //cell4.setCellType(CellType.STRING);
                            Date dateCellValue = cell4.getDateCellValue();

                        } else {
                            return new ResponseEntity("The data of line" + i + ",column 4 can't empty", HttpStatus.EXPECTATION_FAILED);
                        }

                        // verify & read time
                        if (cell5 != null) {
                            cell5.setCellType(CellType.STRING);
                            String strCell5Value = cell5.getStringCellValue();
                        } else {
                            return new ResponseEntity("The data of line" + i + ",column 5 can't empty", HttpStatus.EXPECTATION_FAILED);
                        }
                        Date dateCellValue = cell4.getDateCellValue();
                        SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");
                        String strDateValue = sdfDate.format(dateCellValue);

                        SimpleDateFormat sdfDT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                        String dateTime = strDateValue + " "+ cell5.getStringCellValue();
                        Timestamp ts = new Timestamp(sdfDT.parse(dateTime).getTime());
                        stockPrice.setDateTime(ts);

                        // add the stock price to the stock price list
                        stockPrices.add(stockPrice);

                    }else{
                        break;
                    }
                }
                // save all the stock price data from the file into database
                stockPriceSvc.save(stockPrices);

            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity("success", HttpStatus.OK);
    }

}
