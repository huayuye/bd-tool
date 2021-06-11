package com.bingdeng.tool.test.excel.easyexcel.v2;

import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.bingdeng.tool.DateUtil;
import com.bingdeng.tool.excel.v2.EasyExcelUtil;
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
            easyExcelUser = new EasyExcelUser(i%3==0?"男":"女","test"+i,i+"2",date,BigDecimal.valueOf(i+1000000.86548));
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

            EasyExcelUtil.readWithPartSheet(new FileInputStream(new File("/Users/bingdeng/Desktop/temp/easyexcelConstraint3.xlsx")),sheets);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    public static void testReadMoreSheetWithName(){
        try {
            Map<String,Map<Class,ReadListener>> sheets = new HashMap<>();

            Map<Class,ReadListener> sheet1 = new HashMap<>();
            sheet1.put(EasyExcelUser.class,new EasyExcelUserListener(excelDao));

            Map<Class,ReadListener> sheet2 = new HashMap<>();
            sheet2.put(EasyExcelArticle.class,new EasyExcelArticleListener(excelDao));

            sheets.put("test",sheet1);
            sheets.put("test2",sheet2);

            EasyExcelUtil.readWithPartSheetWithName(new FileInputStream(new File("/Users/bingdeng/Desktop/temp/easyexcelConstraint3.xlsx")),sheets);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    public static void testExplicitConstraint(){
        try {
            List<EasyExcelUser> users = new ArrayList<>(15000);
            EasyExcelUser easyExcelUser = new EasyExcelUser("请选择","请选择","请选择",DateUtil.getCurrentDate(),BigDecimal.valueOf(1000000.86548));
            easyExcelUser.setBirthday(new Date());
            EasyExcelUser easyExcelUser2 = new EasyExcelUser("安徽省","test2","2",DateUtil.getCurrentDate(),BigDecimal.valueOf(1000000.86548));
            users.add(easyExcelUser);
            users.add(easyExcelUser2);
            /**
             * 第一级数据
             * key为字段名
             */
            Map<String,List<String>> levelFirstDatas = new HashMap<>();
            levelFirstDatas.put("gender",Arrays.asList("北京","上海","广州"));
            levelFirstDatas.put("birthday",Arrays.asList("北京","上海","广州"));
            /**所有为父级的数据分别所对应的子集
             * 外层key为字段名
             * 里层：
             * key：父集
             * value: 此父级对应的子集
             */
            Map<String,List<String>> levelSecondData = new HashMap<>();
            levelSecondData.put("北京",Arrays.asList("一环","二环","三环","四环"));
            levelSecondData.put("上海",Arrays.asList("外滩","东方之珠"));
            levelSecondData.put("广州",Arrays.asList("番禺","海珠"));
            Map<String,Map<String,List<String>>> levelSecondDatas = new HashMap<>();
            levelSecondDatas.put("gender",levelSecondData);
            levelSecondDatas.put("birthday",levelSecondData);

            Map<String,List<String>> levelThreeData = new HashMap<>();
            levelThreeData.put("一环",Arrays.asList("一环1","一环2"));
            levelThreeData.put("外滩",Arrays.asList("外滩1","外滩2"));
            levelThreeData.put("番禺",Arrays.asList("番禺1","番禺2"));
            Map<String,Map<String,List<String>>> levelThreeDatas = new HashMap<>();
            levelThreeDatas.put("gender",levelThreeData);

            EasyExcelUtil.writeWithCellStype(new FileOutputStream(new File("C:\\htmlpdf\\easyexcelConstraint4.xlsx")),new EasyExcelExplicitConstraintHandler(EasyExcelUser.class,levelFirstDatas,levelSecondDatas,levelThreeDatas),"test",EasyExcelUser.class,users);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
//        testReadMoreSheet();
//        testReadMoreSheetWithName();
        testExplicitConstraint();
    }



}
