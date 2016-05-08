package net.zyexpress.site.util;

/**
 * Created by lumengyu on 2016/3/4.
 */

import org.apache.commons.lang3.StringUtils;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class ExcelReader {

    /**
     * 创建EXCEL
     * @param filePath
     * @return
     * @throws IOException
     */
    public static final Workbook createWb(String filePath) throws IOException {
        if(StringUtils.isBlank(filePath)) {
            throw new IllegalArgumentException("文件不存在!!!") ;
        }
        if(filePath.trim().toLowerCase().endsWith("xls")) {
            return new HSSFWorkbook(new FileInputStream(filePath)) ;
        } else if(filePath.trim().toLowerCase().endsWith("xlsx")) {
            return new XSSFWorkbook(new FileInputStream(filePath)) ;
        } else {
            throw new IllegalArgumentException("上传文件以xlsx结尾!!!") ;
        }
    }

    public static final Sheet getSheet(Workbook wb ,String sheetName) {
        return wb.getSheet(sheetName) ;
    }

    public static final Sheet getSheet(Workbook wb ,int index) {
        return wb.getSheetAt(index) ;
    }

    public static final List<Object[]> listFromSheet(Sheet sheet) {

        int rowTotal = sheet.getPhysicalNumberOfRows() ;

        List<Object[]> list = new ArrayList<Object[]>() ;
        for(int r = sheet.getFirstRowNum() ; r <= sheet.getLastRowNum() ; r ++) {
            Row row = sheet.getRow(r) ;
            if(row == null)continue ;
            if(row.getFirstCellNum()==-1)continue;
            Object[] cells = new Object[row.getLastCellNum()] ;
            for(int c = row.getFirstCellNum() ; c <= row.getLastCellNum()-1 ; c++) {
                Cell cell = row.getCell(c) ;
                if(cell == null)continue ;
                cells[c] = getValueFromCell(cell) ;
            }
            list.add(cells) ;
        }

        return list ;
    }


    /**
     * 获取单元格
     * @param cell
     * @return
     */
    public static final String getValueFromCell(Cell cell) {
        if(cell == null) {
            System.out.println("Cell is null !!!") ;
            return null ;
        }
        String value = null ;
        switch(cell.getCellType()) {
            case Cell.CELL_TYPE_NUMERIC :
                if(HSSFDateUtil.isCellDateFormatted(cell)) {
                    //value = new SimpleDateFormat(DatePattern.LOCALE_ZH_DATE.getValue()).format(cell.getDateCellValue()) ;
                } else  value = String.valueOf(cell.getNumericCellValue()) ;
                break ;
            case Cell.CELL_TYPE_STRING:
                value = cell.getStringCellValue() ;
                break ;
            case Cell.CELL_TYPE_FORMULA:
                double numericValue = cell.getNumericCellValue() ;
                if(HSSFDateUtil.isValidExcelDate(numericValue)) {
                    //value = new SimpleDateFormat(DatePattern.LOCALE_ZH_DATE.getValue()).format(cell.getDateCellValue()) ;
                } else  value = String.valueOf(numericValue) ;
                break ;
            case Cell.CELL_TYPE_BLANK:
                //value = ExcelConstants.EMPTY_CELL_VALUE ;
                value = "";
                break ;
            case Cell.CELL_TYPE_BOOLEAN:            // Boolean
                value = String.valueOf(cell.getBooleanCellValue()) ;
                break ;
            case Cell.CELL_TYPE_ERROR:              // Error
                value = String.valueOf(cell.getErrorCellValue()) ;
                break ;
            default:value = StringUtils.EMPTY ;break ;
        }

        return value;
        //return value + "["+cell.getRowIndex()+","+cell.getColumnIndex()+"]" ;
    }

}


