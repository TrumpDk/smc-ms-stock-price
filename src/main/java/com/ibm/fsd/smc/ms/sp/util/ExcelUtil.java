package com.ibm.fsd.smc.ms.sp.util;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @description: 基于POI的Excel工具类
 **/
public class ExcelUtil {

    private static final String XML ="xls";
    private static final String XLSX ="xlsx";

    /***
     * 判断文件类型是不是2003版本
     * @param filePath
     * @return
     */
    public static boolean isExcel2003(String filePath) {
        return filePath.matches("^.+\\.(?i)(xls)$");
    }

    /**
     * 判断文件类型是不是2007版本
     * @param: filePath
     * @return
     */
    public static boolean isExcel2007(String filePath) {
        return filePath.matches("^.+\\.(?i)(xlsx)$");
    }
    /**
     * @description: // todo 读取上传的Excel文件
     * @param file 上传的文件
     * @param startRow  开始行 0开始
     * @param startCell 开始列 0开始
     * */
    public static List<String[]> readExcel(MultipartFile file, int startRow, int startCell)throws Exception{
        checkFile(file);
        String fileName = file.getOriginalFilename();
        Workbook workbook = getWorkBook(file,fileName);
        List<String []> list = new ArrayList<>();
        if(workbook != null){
            for(int sheetNum = 0;sheetNum < workbook.getNumberOfSheets();sheetNum++){
                Sheet sheet = workbook.getSheetAt(sheetNum);
                if(sheet == null){
                    continue;
                }
                int firstRowNum  = sheet.getFirstRowNum();
                int lastRowNum = sheet.getLastRowNum();
                for(int rowNum = firstRowNum+startRow;rowNum <= lastRowNum;rowNum++){
                    Row row = sheet.getRow(rowNum);
                    if(row == null){
                        continue;
                    }
                    int firstCellNum = row.getFirstCellNum();
                    int lastCellNum = row.getLastCellNum()+1;
                    String[] cells = new String[lastCellNum];
                    for(int cellNum = firstCellNum + startCell; cellNum < lastCellNum;cellNum++){
                        Cell cell = row.getCell(cellNum);
                        cells[cellNum] = getCellValue(cell);
                    }
                    list.add(cells);
                }
            }
        }
        return list;
    }


    /**
     * @description: //todo 设置行宽度自适应 列不会设置
     * @param sheet sheet
     * @param columnNumber  结束列
     * */
    private static void autoSizeColumns(Sheet sheet, int columnNumber) {
        for (int i = 0; i < columnNumber; i++) {
            int oldWidth = sheet.getColumnWidth(i);
            sheet.autoSizeColumn(i, true);
            int newWidth = (int) (sheet.getColumnWidth(i) + 100);
            if (newWidth > oldWidth) {
                sheet.setColumnWidth(i, newWidth);
            } else {
                sheet.setColumnWidth(i, oldWidth);
            }
        }
    }


    /***
     * //todo 校验文件是否正常
     * @param file 上传的文件
     * @throws Exception
     */
    private static void checkFile(MultipartFile file)throws Exception{
        if (Objects.isNull(file))
            throw new FileNotFoundException("上传文件是空");
        String fileName = file.getOriginalFilename();
        if(!fileName.endsWith(XML) && !fileName.endsWith(XLSX))
            throw new IOException(fileName + "不是excel文件");
    }

    /**
     * @description: // todo 获取单元格的值
     * @param cell 单元格
     * @return String
     */
    private static String getCellValue(Cell cell){
        String cellValue = "";
        if(cell == null){
            return cellValue;
        }
        //把数字当成String来读，避免出现1读成1.0的情况
        if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
            cell.setCellType(Cell.CELL_TYPE_STRING);
        }
        //判断数据的类型
        switch (cell.getCellType()){
            case Cell.CELL_TYPE_NUMERIC: //数字
                cellValue = String.valueOf(cell.getNumericCellValue());
                break;
            case Cell.CELL_TYPE_STRING: //字符串
                cellValue = String.valueOf(cell.getStringCellValue());
                break;
            case Cell.CELL_TYPE_BOOLEAN: //Boolean
                cellValue = String.valueOf(cell.getBooleanCellValue());
                break;
            case Cell.CELL_TYPE_FORMULA: //公式
                cellValue = String.valueOf(cell.getCellFormula());
                break;
            case Cell.CELL_TYPE_BLANK: //空值
                cellValue = "";
                break;
            case Cell.CELL_TYPE_ERROR: //故障
                cellValue = "非法字符";
                break;
            default:
                cellValue = "未知类型";
                break;
        }
        return cellValue;
    }

    /**
     * @description: //todo 获取上传文件的工作区
     * @param file 上传的文件
     * @param fileName 文件名字
     * @return Workbook
     */
    private static Workbook getWorkBook(MultipartFile file,String fileName) {
        Workbook workbook = null;
        try {
            InputStream is = file.getInputStream();
            if(fileName.endsWith(XML)){
                //2003
                workbook = new HSSFWorkbook(is);
            }else if(fileName.endsWith(XLSX)){
                //2007
                workbook = new XSSFWorkbook(is);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return workbook;
    }

    /**
     * @description: //todo 获取上传文件的工作区
     * @param file 读取到的文件
     * @return Workbook
     */
    private static Workbook getWorkBook(File file) {
        Workbook workbook = null;
        try {
            InputStream is = new FileInputStream(file);
            if(file.getName().endsWith(XML)){
                //2003
                workbook = new HSSFWorkbook(is);
            }else if(file.getName().endsWith(XLSX)){
                //2007
                workbook = new XSSFWorkbook(is);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return workbook;
    }
}
