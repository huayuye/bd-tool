package com.bingdeng.tool.test.excel.easyexcel;

import com.bingdeng.tool.excel.ExcelUtil;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * @Author: Fran
 * @Date: 2020/7/10
 * @Desc:
 **/
public class ExcelTest {
    public static void main(String[] args) {


        //write

//        1、
//        ExcelRowModel excelRowModel = null;
//        List<ExcelRowModel> list = new ArrayList<>(1001);
//        for (int i = 0; i < 1000; i++) {
//            excelRowModel = new ExcelRowModel();
//            excelRowModel
//                    .setName("fsq" + i)
//                    .setIdentifyCard("4505235436346436")
//                    .setEducationBackgroup("本科")
//                    .setAddress("深圳市白石洲上白石")
//                    .setEmail("3423952395@qq.com")
//                    .setBirthday(new Date());
//            list.add(excelRowModel);
//        }
//        try {
//            //含多sheet的示例，实际用时，需要自己写
//            writeFile("测试", ExcelRowModel.class, list, "E:/ideaTestWorkSpace/2007-demo.xlsx", ExcelTypeEnum.XLSX);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }

//        2、
//        ExcelRowModel excelRowModel = null;
//        List<ExcelRowModel> list = new ArrayList<>(1001);
//        List<ExcelMoreSheetModel> moreshseetlist = new ArrayList<>(10);
//        ExcelMoreSheetModel excelMoreSheetModel = null;
//        for (int j = 0; j < 5; j++) {
//            list=new ArrayList<>();
//            excelMoreSheetModel = new ExcelMoreSheetModel();
//            for (int i = 0; i < 1000; i++) {
//                excelRowModel = new ExcelRowModel();
//                excelRowModel
//                        .setName("fsq-"+j + i)
//                        .setIdentifyCard("4505235436346436")
//                        .setEducationBackgroup("本科")
//                        .setAddress("深圳市白石洲上白石")
//                        .setEmail("3423952395@qq.com")
//                        .setBirthday(new Date());
//                list.add(excelRowModel);
//            }
//            excelMoreSheetModel.setDatas(list);
//            excelMoreSheetModel.setSheetName("sheet"+j);
//            excelMoreSheetModel.setHeaders(ExcelRowModel.class);
//            moreshseetlist.add(excelMoreSheetModel);
//        }
//        try {
//            //含多sheet的示例，实际用时，需要自己写
//            writeFile(moreshseetlist, "E:/ideaTestWorkSpace/2007-more-sheet-demo.xlsx", ExcelTypeEnum.XLSX);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }


        //reader

        ExcelListener excelListener = new ExcelListener();
        try {
            ExcelUtil.readFile(new BufferedInputStream(new FileInputStream("E:/ideaTestWorkSpace/2007-demo.xlsx")), ExcelReaderRowModel.class, excelListener);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Object excelRowModel2 : excelListener.getDatas()) {
            for (int i = 0; i < 6; i++) {
                System.out.println(((List) excelRowModel2).get(i));
            }
        }
    }

}
