package com.bingdeng.tool.excel.v2;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.excel.write.handler.WriteHandler;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import org.apache.poi.ss.usermodel.IndexedColors;

import javax.validation.constraints.NotNull;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author: Fran
 * @Date: 2020/7/7
 * @Desc:
 **/
public class EasyExcelUtil {
    /**
     * 写入excel文件
     * @param outputStream
     * @param sheetName
     * @param clazz header
     * @param datas
     */
    public static void write(OutputStream outputStream, String sheetName, Class clazz, List<?> datas){
        //重置默认表头样式
        HorizontalCellStyleStrategy horizontalCellStyleStrategy = resetHeaderCellStyleStrategy();
        EasyExcel.write(outputStream,clazz).registerWriteHandler(horizontalCellStyleStrategy).sheet(sheetName).doWrite(datas);
    }


    /**
     * 写入excel文件，支持自定义样式
     * @param outputStream
     * @param writeHandler 自定义样式
     * @param sheetName
     * @param clazz
     * @param datas
     */
    public static void writeWithCellStype(OutputStream outputStream, WriteHandler writeHandler, String sheetName, Class clazz, List<?> datas){
        EasyExcel
                .write(outputStream,clazz)
                .registerWriteHandler(resetHeaderCellStyleStrategy())
                .registerWriteHandler(writeHandler)
                .sheet(sheetName)
                .doWrite(datas);
    }


    /**
     * eventListener 必须每次调用时，以new的方式创建，不允许被spring管理。若其里面用到spring时，通过构造函数传进去
     * @param inputStream
     * @param clazz
     * @param eventListener : 可以直接继承 AnalysisEventListener 即可
     */
    public static void readWithSingleSheet(InputStream inputStream, Class clazz, ReadListener eventListener) {
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
        EasyExcel.read(inputStream, clazz, eventListener).sheet().doRead();
    }

    /**
     * 读取全部sheet
     * @param inputStream
     * @param clazz
     * @param eventListener
     */
    public static void readWithAllSheet(InputStream inputStream, Class clazz, ReadListener eventListener) {
        // 读取全部sheet
        // 这里需要注意 DemoDataListener的doAfterAllAnalysed 会在每个sheet读取完毕后调用一次。然后所有sheet都会往同一个DemoDataListener里面写
        EasyExcel.read(inputStream, clazz, eventListener).doReadAll();
    }

    /**
     * 读取部分sheet
     * @param inputStream
     * @param sheetMaps [指定需要的sheet及每个sheet的header和Listener]
     */
    public static void readWithPartSheet(InputStream inputStream, Map<Integer,Map<Class,ReadListener>> sheetMaps) {
        //
        ExcelReader excelReader = null;
        try {
            excelReader = EasyExcel.read(inputStream).build();
            // 允许每一个sheet 对应不同的Head和Listener
            List<ReadSheet> readSheets= new ArrayList<>(sheetMaps.size());
            for(Map.Entry<Integer,Map<Class,ReadListener>> sheetEntry : sheetMaps.entrySet()){
                Map.Entry<Class,ReadListener> headerEntry= sheetEntry.getValue().entrySet().iterator().next();
                readSheets.add(EasyExcel.readSheet(sheetEntry.getKey()).head(headerEntry.getKey()).registerReadListener(headerEntry.getValue()).build());
            }
            // 这里注意 一定要把sheet1 sheet2,... 一起传进去，不然有个问题就是03版的excel 会读取多次，浪费性能
            excelReader.read(readSheets);
        } finally {
            if (excelReader != null) {
                // 这里千万别忘记关闭，读的时候会创建临时文件，到时磁盘会崩的
                excelReader.finish();
            }
        }
    }

    /**
     * 读取部分sheet
     * @param inputStream
     * @param sheetMaps [指定需要的sheet及每个sheet的header和Listener]
     */
    public static void readWithPartSheetWithName(InputStream inputStream, Map<String,Map<Class,ReadListener>> sheetMaps) {
        //
        ExcelReader excelReader = null;
        try {
            excelReader = EasyExcel.read(inputStream).build();
            // 允许每一个sheet 对应不同的Head和Listener
            List<ReadSheet> readSheets= new ArrayList<>(sheetMaps.size());
            for(Map.Entry<String,Map<Class,ReadListener>> sheetEntry : sheetMaps.entrySet()){
                Map.Entry<Class,ReadListener> headerEntry= sheetEntry.getValue().entrySet().iterator().next();
                readSheets.add(EasyExcel.readSheet(sheetEntry.getKey()).head(headerEntry.getKey()).registerReadListener(headerEntry.getValue()).build());
            }
            // 这里注意 一定要把sheet1 sheet2,... 一起传进去，不然有个问题就是03版的excel 会读取多次，浪费性能
            excelReader.read(readSheets);
        } finally {
            if (excelReader != null) {
                // 这里千万别忘记关闭，读的时候会创建临时文件，到时磁盘会崩的
                excelReader.finish();
            }
        }
    }








    @NotNull
    private static HorizontalCellStyleStrategy resetHeaderCellStyleStrategy() {
        // 头的策略
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        // 背景色
        headWriteCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        headWriteCellStyle.setBottomBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headWriteCellStyle.setRightBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headWriteCellStyle.setLeftBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        WriteFont headWriteFont = new WriteFont();
        headWriteFont.setFontHeightInPoints((short) 11);
        headWriteCellStyle.setWriteFont(headWriteFont);
        // 内容的策略
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        // 这个策略是 头是头的样式 内容是内容的样式 其他的策略可以自己实现
        return new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);
    }
}
