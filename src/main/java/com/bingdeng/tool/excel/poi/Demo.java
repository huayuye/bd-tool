package com.bingdeng.tool.excel.poi;

import com.bingdeng.tool.MatrixUtil;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Color;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class Demo {


    public static void main(String[] args) {
        testPoi();
    }



    public static void testPoi(){
        //长度18
    String[] rowName = {"序号","订单号","客户名称","提货地址","提货说明","收货人","送货地址","送货时间","货物描述","板数","数量","毛重(KG)","体积","报关方式","报关责任方","分货标识","操作员","备注"};
    XSSFWorkbook workbook = new XSSFWorkbook();
    XSSFSheet sheet = workbook.createSheet("电子运单");
    sheet.setDefaultColumnWidth(25);
    sheet.setDefaultRowHeight((short)30);


    //图片
//    // 先把读进来的图片放到一个ByteArrayOutputStream中，以便产生ByteArray
//    ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
//    //将图片读到BufferedImage
//        BufferedImage bufferImg = null;
//        try {
////            new File("/Users/bingdeng/Desktop/qrcode.png")
//            bufferImg = ImageIO.read();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        // 将图片写入流中
//        try {
//            ImageIO.write(bufferImg, "png", byteArrayOut);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


        XSSFFont hssfFont = workbook.createFont();
          XSSFCellStyle hssfCellStyle = workbook.createCellStyle();

    //设置第一级标题行
    XSSFRow firstTitleRow = sheet.createRow(0);
    XSSFCell firstTitleCell = null;
//    for(int i=0;i<rowName.length;i++){
//        firstTitleCell = firstTitleRow.createCell(0);
        firstTitleCell = firstTitleRow.createCell(1);
//        if(i==3){
            hssfCellStyle.setAlignment(HorizontalAlignment.CENTER);//水平居中
            hssfFont.setBold(true);//粗体显示
            hssfFont.setFontHeightInPoints((short)18);
            hssfFont.setColor(Font.COLOR_RED);
            hssfCellStyle.setFont(hssfFont);
            firstTitleCell.setCellValue("电子运单\n\rPIN234255");
            firstTitleCell.setCellStyle(hssfCellStyle);

//        }
//    }
    //设置高度
    firstTitleRow.setHeight((short)(100*20));
//    XSSFRow firstTitleMergeRow2= sheet.createRow(1);
//    firstTitleCell = firstTitleMergeRow2.createCell(3);
//    firstTitleCell.setCellValue("");
//    firstTitleRow.setHeight((short)(30*20));
//    XSSFRow firstTitleMergeRow3 = sheet.createRow(2);
//    firstTitleRow.setHeight((short)(30*20));
        // 利用HSSFPatriarch将图片写入EXCEL

        XSSFDrawing patriarch = sheet.createDrawingPatriarch();
        /**
         * 该构造函数有8个参数
         * 前四个参数是控制图片在单元格的位置，分别是图片距离单元格left，top，right，bottom的像素距离
         * 后四个参数，前连个表示图片左上角所在的cellNum和 rowNum，后天个参数对应的表示图片右下角所在的cellNum和 rowNum，
         * excel中的cellNum和rowNum的index都是从0开始的
         *
         */
        //图片一导出到单元格A1中
        XSSFClientAnchor anchor = new XSSFClientAnchor(0, 0, 0, 0,
                (short) 0, 0, (short) 1, 1);
        // 插入图片
        try {
            patriarch.createPicture(anchor, workbook.addPicture(MatrixUtil.createQRCode("391398483488",80,80,"png"), HSSFWorkbook.PICTURE_TYPE_PNG));
        } catch (IOException e) {
            e.printStackTrace();
        }


        //设置固定行
        XSSFRow fixRow = sheet.createRow(3);
        XSSFCell fixCell1_4 = fixRow.createCell(0);
    fixCell1_4.setCellValue("承运方:");
        XSSFCell fixCell5_7 = fixRow.createCell(4);
    fixCell5_7.setCellValue("运输载具:");
        XSSFCell fixCell8_10 = fixRow.createCell(7);
    fixCell8_10.setCellValue("司机/联系人:");
        XSSFCell fixCell11_13 = fixRow.createCell(10);
    fixCell11_13.setCellValue("负责人:");




    //创建二级标题
        XSSFRow secondTitleRow = sheet.createRow(4);
        XSSFCell secondTitleRowCell = null;
    for(int i=0;i<rowName.length;i++){
        secondTitleRowCell = secondTitleRow.createCell(i);
        secondTitleRowCell.setCellValue(rowName[i]);
        hssfCellStyle.setAlignment(HorizontalAlignment.CENTER);//水平居中
        hssfFont.setBold(true);//粗体显示
        hssfFont.setFontHeightInPoints((short)16);
        hssfFont.setColor(Font.COLOR_NORMAL);
        hssfCellStyle.setFont(hssfFont);
        secondTitleRowCell.setCellStyle(hssfCellStyle);
    }

    //构建模拟数据
    List<List<Object>> datas= new ArrayList<>();
    List<Object> data=null;
    for(int i=0;i<100;i++){
        data = new ArrayList<>();
        data.add(i+1);
        data.add(rowName[1]+i);
        data.add(rowName[2]+i);
        data.add(rowName[3]+i);
        data.add(rowName[4]+i);
        data.add(rowName[5]+i);
        data.add(rowName[6]+i);
        data.add(new Date());
        data.add(rowName[8]+i);
        data.add(i);
        data.add(i*2.0);
        data.add(i*1.0);
        data.add(i*3.1);
        data.add(rowName[13]+i);
        data.add(rowName[14]+i);
        data.add(rowName[15]+i);
        data.add(rowName[16]+i);
        data.add(rowName[17]+i);
        datas.add(data);
    }


    //填充数据，创建数据行
        XSSFRow dataRow = null;
    int startRowNum = 5;
    XSSFCell dataRowCell = null;
    for(int i=0;i<datas.size();i++){
        dataRow = sheet.createRow(startRowNum+i);
        List<Object> tempdata=datas.get(i);
        for(int j=0;j<tempdata.size();j++){
            dataRowCell= dataRow.createCell(j);
            fillDataByType(dataRowCell,tempdata.get(j));
            hssfFont.setFontHeightInPoints((short)12);
            hssfFont.setColor(Font.COLOR_RED);
            hssfCellStyle.setFont(hssfFont);
            dataRowCell.setCellStyle(hssfCellStyle);
        }
    }

        int startStatisticRowNum  = startRowNum+datas.size()+1;
        XSSFRow statisticRow = sheet.createRow(startStatisticRowNum);
    XSSFCell statisticRowcell1_9 = statisticRow.createCell(0);
    statisticRowcell1_9.setCellValue("统计:");
        hssfCellStyle.setAlignment(HorizontalAlignment.CENTER);//水平居中
        hssfFont.setBold(true);//粗体显示
        hssfCellStyle.setFont(hssfFont);
        statisticRowcell1_9.setCellStyle(hssfCellStyle);

        XSSFCell statisticRowcell10 = statisticRow.createCell(9);
        statisticRowcell10.setCellValue(2);
        XSSFCell statisticRowcell11 = statisticRow.createCell(10);
        statisticRowcell11.setCellValue(3);
        XSSFCell statisticRowcell12 = statisticRow.createCell(11);
        statisticRowcell12.setCellValue(4);
        XSSFCell statisticRowcell13 = statisticRow.createCell(12);
        statisticRowcell13.setCellValue(5);

        int startSpecialRowNum  = startStatisticRowNum+1;
        XSSFRow specialReqRow = sheet.createRow(startSpecialRowNum);
        XSSFCell specialReqRowCell1 = specialReqRow.createCell(0);
        specialReqRowCell1.setCellValue("特殊要求:");
        XSSFCell specialReqRowCell2_3 = specialReqRow.createCell(1);
        specialReqRowCell2_3.setCellValue("特殊要求:");


        XSSFCell signCell1 = specialReqRow.createCell(6);
        signCell1.setCellValue("签字人1:");


        XSSFCell signCell2 = specialReqRow.createCell(10);
        signCell2.setCellValue("签字人2:");

        XSSFCell signCell3 = specialReqRow.createCell(14);
        signCell3.setCellValue("签字人3:");


        int startMarkRowNum  = startSpecialRowNum+1;
        XSSFRow markRow = sheet.createRow(startMarkRowNum);
        XSSFCell markRowCell1 = markRow.createCell(0);
        markRowCell1.setCellValue("备注信息:");
        XSSFCell markRowCell2_3 = markRow.createCell(1);
        markRowCell2_3.setCellValue("备注信息");





        //合并处理
        //标题行
//        CellRangeAddress firstTitleCellRange = new CellRangeAddress(0, 2, 0, 1);
//        sheet.addMergedRegion(firstTitleCellRange);
        CellRangeAddress firstTitleCellRange = new CellRangeAddress(0, 2, 1, 17);
        sheet.addMergedRegion(firstTitleCellRange);
        //固定行合并
        CellRangeAddress fixRowCellRange = new CellRangeAddress(3, 3, 0, 3);
        sheet.addMergedRegion(fixRowCellRange);
        fixRowCellRange = new CellRangeAddress(3, 3, 4, 6);
        sheet.addMergedRegion(fixRowCellRange);
        fixRowCellRange = new CellRangeAddress(3, 3, 7, 9);
        sheet.addMergedRegion(fixRowCellRange);
        fixRowCellRange = new CellRangeAddress(3, 3, 10, 12);
        sheet.addMergedRegion(fixRowCellRange);
        //统计
        CellRangeAddress statisticRowCellRange = new CellRangeAddress(startStatisticRowNum, startStatisticRowNum, 0, 8);
        sheet.addMergedRegion(statisticRowCellRange);
        //特殊要求
        CellRangeAddress    specialReqRowCellRange = new CellRangeAddress(startSpecialRowNum, startSpecialRowNum, 1, 2);
        sheet.addMergedRegion(specialReqRowCellRange);
        //签字人
        CellRangeAddress signRowCellRange = new CellRangeAddress(startSpecialRowNum, startSpecialRowNum+1, 6, 6);
        sheet.addMergedRegion(signRowCellRange);
        signRowCellRange = new CellRangeAddress(startSpecialRowNum, startSpecialRowNum+1, 7, 9);
        sheet.addMergedRegion(signRowCellRange);
        signRowCellRange = new CellRangeAddress(startSpecialRowNum, startSpecialRowNum+1, 10, 10);
        sheet.addMergedRegion(signRowCellRange);
        signRowCellRange = new CellRangeAddress(startSpecialRowNum, startSpecialRowNum+1, 11, 13);
        sheet.addMergedRegion(signRowCellRange);
        signRowCellRange = new CellRangeAddress(startSpecialRowNum, startSpecialRowNum+1, 14, 14);
        sheet.addMergedRegion(signRowCellRange);
        signRowCellRange = new CellRangeAddress(startSpecialRowNum, startSpecialRowNum+1, 15, 17);
        sheet.addMergedRegion(signRowCellRange);
        //备注
        CellRangeAddress    markRowCellRange = new CellRangeAddress(startMarkRowNum, startMarkRowNum, 1, 2);
        sheet.addMergedRegion(markRowCellRange);


        try {
        workbook.write(new FileOutputStream("/Users/bingdeng/Desktop/bill4.xlsx"));
    } catch (IOException e) {
        e.printStackTrace();
    }
    }

    private static void fillDataByType(XSSFCell dataRowCell, Object o) {
        dataRowCell.setCellValue(o.toString());
    }

}
