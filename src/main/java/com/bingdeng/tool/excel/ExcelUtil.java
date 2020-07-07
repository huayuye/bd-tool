package com.bingdeng.tool.excel;

import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.BaseRowModel;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;

import java.io.*;
import java.util.List;

/**
 * @Author: Fran
 * @Date: 2019/4/23
 * @Desc: 此工具类基于 easyexcel 1.1.1
 **/
public class ExcelUtil {

    private ExcelUtil() {

    }

    /**
     * @param sheetName     sheet name
     * @param headers       里层List表示每列的表头行数，外层List表示有n列表头
     * @param datas         里层List表示每行对应的每列列的值，外层List表示一共N行数据
     * @param out
     * @param excelTypeEnum excel版本:xls,xlsx
     */
    public static void writeFile(String sheetName, List<List<String>> headers, List<List<Object>> datas, OutputStream out, ExcelTypeEnum excelTypeEnum) {
        ExcelWriter writer = new ExcelWriter(out, excelTypeEnum);
        Sheet sheet = new Sheet(1, 0);
        sheet.setSheetName(sheetName);
        sheet.setHead(headers);
        writer.write1(datas, sheet);
        writer.finish();
    }


    /**
     * @param sheetName     sheet name
     * @param headers       里层List表示每列的表头行数，外层List表示有n列表头
     * @param datas         里层List表示每行对应的每列列的值，外层List表示一共N行数据
     * @param destUrl
     * @param excelTypeEnum excelTypeEnum excel版本:xls,xlsx
     * @throws FileNotFoundException
     */
    public static void writeFile(String sheetName, List<List<String>> headers, List<List<Object>> datas, String destUrl, ExcelTypeEnum excelTypeEnum) throws FileNotFoundException {
        writeFile(sheetName, headers, datas, new FileOutputStream(destUrl), excelTypeEnum);
    }

    /**
     * @param sheetName     sheet name
     * @param clazz         模板型excel表头，baseRowModel类及子类中的每个属性都对应excel中每一行的每一列
     * @param datas         List表示一共有N行数据，BaseRowModel 中的属性对应着每行中每列的值
     * @param destUrl
     * @param excelTypeEnum
     * @throws FileNotFoundException
     */
    public static void writeFile(String sheetName, Class<? extends BaseRowModel> clazz, List<? extends BaseRowModel> datas, String destUrl, ExcelTypeEnum excelTypeEnum) throws FileNotFoundException {
        writeFile(sheetName, clazz, datas, new FileOutputStream(destUrl), excelTypeEnum);
    }

    /**
     * @param sheetName     sheet name
     * @param clazz         模板型excel表头，baseRowModel类及子类中的每个属性都对应excel中每一行的每一列：列值对应属性值
     * @param datas         List表示excel一共有N行数据，泛型 BaseRowModel，其每个属性对应着每行中每列的值
     * @param out
     * @param excelTypeEnum
     */
    public static void writeFile(String sheetName, Class<? extends BaseRowModel> clazz, List<? extends BaseRowModel> datas, OutputStream out, ExcelTypeEnum excelTypeEnum) {
        ExcelWriter writer = new ExcelWriter(out, excelTypeEnum);
        Sheet sheet = new Sheet(1, 0, clazz);
        sheet.setSheetName(sheetName);
        writer.write(datas, sheet);

        /**
         * 多sheet的示例
         */
//        TableStyle tableStyle2 = new TableStyle();
//        tableStyle2.setTableHeadBackGroundColor(IndexedColors.BLUE_GREY);
//        tableStyle2.setTableContentBackGroundColor(IndexedColors.BLACK);
//        Sheet sheet2 = new Sheet(2,0,baseRowModel.getClass());
//        sheet2.setTableStyle(tableStyle2);
//        sheet2.setSheetName(sheetName+2);
//        writer.write(datas,sheet2);
//
//        TableStyle tableStyle3 = new TableStyle();
//        tableStyle3.setTableHeadBackGroundColor(IndexedColors.BLUE);
//        tableStyle3.setTableContentBackGroundColor(IndexedColors.DARK_BLUE);
//        Sheet sheet3 = new Sheet(3,0,baseRowModel.getClass());
//        sheet3.setTableStyle(tableStyle3);
//        sheet3.setSheetName(sheetName+3);
//        writer.write(datas,sheet3);


        writer.finish();
    }


    /**
     * @param moreSheetModels
     * @param excelTypeEnum
     */
    public static void writeFile(List<ExcelMoreSheetModel> moreSheetModels, String destUrl, ExcelTypeEnum excelTypeEnum) throws FileNotFoundException {
        writeFile(moreSheetModels, new FileOutputStream(destUrl), excelTypeEnum);
    }

    public static void writeFile(List<ExcelMoreSheetModel> moreSheetModels, OutputStream out, ExcelTypeEnum excelTypeEnum) {
        ExcelWriter writer = new ExcelWriter(out, excelTypeEnum);
        Sheet sheet = null;
        int i = 0;
        for (ExcelMoreSheetModel model : moreSheetModels) {
            sheet = new Sheet(i++, 0, model.getHeaders());
            sheet.setSheetName(model.getSheetName());
            writer.write(model.getDatas(), sheet);
        }
        writer.finish();
    }


    public static void readFile(InputStream in, Class<? extends BaseRowModel> clazz, AnalysisEventListener excelListener) throws IOException {

        ExcelReader excelReader = new ExcelReader(in, null, excelListener);
        excelReader.read();
//        List<Sheet> sheets = excelReader.getSheets();
//        for (Sheet sheet:sheets) {
//            sheet.setClazz(clazz);
//            excelReader.read(sheet);
//            if(sheet.getSheetNo() ==1) {
//                excelReader.read(sheet);
//            }else if(sheet.getSheetNo() ==2){
//                sheet.setHeadLineMun(1);
//                sheet.setClazz(clazz);
//                excelReader.read(sheet);
//            }else if(sheet.getSheetNo() ==3){
//                sheet.setHeadLineMun(1);
//                sheet.setClazz(clazz);
//                excelReader.read(sheet);
//            }
//        }
        in.close();
    }


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
            readFile(new BufferedInputStream(new FileInputStream("E:/ideaTestWorkSpace/2007-demo.xlsx")), ExcelReaderRowModel.class, excelListener);
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
