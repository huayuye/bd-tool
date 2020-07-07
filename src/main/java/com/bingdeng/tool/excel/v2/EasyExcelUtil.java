package com.bingdeng.tool.excel.v2;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.bingdeng.tool.DateUtil;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: Fran
 * @Date: 2020/7/7
 * @Desc:
 **/
public class EasyExcelUtil {
    public static void write(OutputStream outputStream, String sheetName, Class clazz, List<?> datas){
//        EasyExcel.write(outputStream,clazz).registerWriteHandler(new ExcelUserCellHandler()).excelType(null).sheet(sheetName).doWrite(datas);
        EasyExcel.write(outputStream,clazz).excelType(null).sheet(sheetName).doWrite(datas);
    }


//    public static void main(String[] args) throws FileNotFoundException {
//        OutputStream outputStream = new FileOutputStream("E:/ideaTestWorkSpace/easyexcel"+ExcelTypeEnum.XLSX.getValue());
//
//        List<EasyExcelUser> users = new ArrayList<>(15000);
//        EasyExcelUser easyExcelUser = null;
//        Date date = DateUtil.getCurrentDate();
//        for(int i=0; i<100000;i++){
//            easyExcelUser = new EasyExcelUser("test"+i,date,i+1,"南山区"+i,i+2622102121.1424D,"remarks"+i,BigDecimal.valueOf(i+10000000000.86548));
//            users.add(easyExcelUser);
//            easyExcelUser=null;
//        }
//        write(outputStream,"test",EasyExcelUser.class,users);
//    }
}
