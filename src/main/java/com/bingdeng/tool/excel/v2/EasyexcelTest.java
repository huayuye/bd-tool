package com.bingdeng.tool.excel.v2;

import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.bingdeng.tool.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;

/**
 * @Author: Fran
 * @Date: 2020/7/10
 * @Desc:
 **/
public class EasyexcelTest {

    @Autowired
    private static EasyExcelDao excelDao;


    public static void write() throws FileNotFoundException {
        OutputStream outputStream = new FileOutputStream("E:/ideaTestWorkSpace/easyexcel"+ExcelTypeEnum.XLSX.getValue());
        List<EasyExcelUser> users = new ArrayList<>(15000);
        EasyExcelUser easyExcelUser = null;
        Date date = DateUtil.getCurrentDate();
        for(int i=0; i<1000;i++){
            easyExcelUser = new EasyExcelUser("test"+i,i+"2",date,BigDecimal.valueOf(i+1000000.86548));
            users.add(easyExcelUser);
            easyExcelUser=null;
        }
        EasyExcelUtil.write(outputStream,"test",EasyExcelUser.class,users);
    }

    public static void testReadMoreSheet(){
        try {
            Map<Integer,Map<Class,ReadListener>> sheets = new HashMap<>();

            Map<Class,ReadListener> sheet1 = new HashMap<>();
            sheet1.put(EasyExcelUser.class,new EasyExcelUserListener(excelDao));

            Map<Class,ReadListener> sheet2 = new HashMap<>();
            sheet2.put(EasyExcelArticle.class,new EasyExcelArticleListener(excelDao));

            sheets.put(0,sheet1);
            sheets.put(1,sheet2);

            EasyExcelUtil.readWithPartSheet(new FileInputStream(new File("E:/ideaTestWorkSpace/easyexcel.xlsx")),sheets);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

//    public static void main(String[] args) {
//        testReadMoreSheet();
//    }



}
