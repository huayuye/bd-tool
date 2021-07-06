package com.bingdeng.tool.excel.easypoi;

import cn.afterturn.easypoi.entity.ImageEntity;
import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.afterturn.easypoi.util.PoiMergeCellUtil;
import com.bingdeng.tool.MatrixUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

/**
 * Excel导入导出工具类
 */

public class EasypoiUtil {

    /**
     * excel 模版导出
     *
     * @param map     数据列表
     * @param outputStream
     */
    public static void exportExcelWithTemplate(String templateUrl,Map<String, Object> map, OutputStream outputStream) throws IOException {
        TemplateExportParams templateExportParams = new TemplateExportParams();
        templateExportParams.setTemplateUrl(templateUrl);
        //把数据添加到excel表格中
        Workbook workbook = ExcelExportUtil.exportExcel(templateExportParams,map);
        downLoadExcel( outputStream, workbook);
    }
    /**
     * 默认的 excel 导出
     *
     * @param list     数据列表
     * @param outputStream
     */
    private static void defaultExportWithTemplate(List<Map<String, Object>> list, OutputStream outputStream) throws IOException {
        //把数据添加到excel表格中
        Workbook workbook = ExcelExportUtil.exportExcel(list, ExcelType.HSSF);
        downLoadExcel( outputStream, workbook);
    }

    /**
     * excel 导出
     *
     * @param list     数据列表
     * @param outputStream
     */
    public static void exportExcel(List<Map<String, Object>> list, OutputStream outputStream) throws IOException {
        defaultExport(list, outputStream);
    }

    /**
     * 默认的 excel 导出
     *
     * @param list     数据列表
     * @param outputStream
     */
    private static void defaultExport(List<Map<String, Object>> list, OutputStream outputStream) throws IOException {
        //把数据添加到excel表格中
        Workbook workbook = ExcelExportUtil.exportExcel(list, ExcelType.HSSF);
        downLoadExcel( outputStream, workbook);
    }

    /**
     * excel 导出
     *
     * @param list         数据列表
     * @param pojoClass    pojo类型
     * @param outputStream
     * @param exportParams 导出参数（标题、sheet名称、是否创建表头，表格类型）
     */
    private static void defaultExport(List<?> list, Class<?> pojoClass, OutputStream outputStream, ExportParams exportParams) throws IOException {
        //把数据添加到excel表格中
        Workbook workbook = ExcelExportUtil.exportExcel(exportParams, pojoClass, list);
        downLoadExcel( outputStream, workbook);
    }

    /**
     * excel 导出
     *
     * @param list         数据列表
     * @param pojoClass    pojo类型
     * @param exportParams 导出参数（标题、sheet名称、是否创建表头，表格类型）
     * @param outputStream
     */
    public static void exportExcel(List<?> list, Class<?> pojoClass, ExportParams exportParams, OutputStream outputStream) throws IOException {
        defaultExport(list, pojoClass, outputStream, exportParams);
    }

    /**
     * excel 导出
     *
     * @param list      数据列表
     * @param title     表格内数据标题
     * @param sheetName sheet名称
     * @param pojoClass pojo类型
     * @param outputStream
     */
    public static void exportExcel(List<?> list, String title, String sheetName, Class<?> pojoClass, OutputStream outputStream) throws IOException {
        defaultExport(list, pojoClass, outputStream, new ExportParams(title, sheetName, ExcelType.XSSF));
    }



    /**
     * excel 导出
     *
     * @param list           数据列表
     * @param title          表格内数据标题
     * @param sheetName      sheet名称
     * @param pojoClass      pojo类型
     * @param isCreateHeader 是否创建表头
     * @param outputStream
     */
    public static void exportExcel(List<?> list, String title, String sheetName, Class<?> pojoClass, boolean isCreateHeader, OutputStream outputStream) throws IOException {
        ExportParams exportParams = new ExportParams(title, sheetName, ExcelType.XSSF);
        exportParams.setCreateHeadRows(isCreateHeader);
        defaultExport(list, pojoClass, outputStream, exportParams);
    }


    /**
     * excel下载
     *
     * @param outputStream
     * @param workbook excel数据
     */
    private static void downLoadExcel(OutputStream outputStream, Workbook workbook) throws IOException {
        try {
//                response.setCharacterEncoding("UTF-8");
//                response.setHeader("content-Type", "application/vnd.ms-excel");
//                response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName + ".xlsx", "UTF-8"));
            workbook.write(outputStream);
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }



    /**
     * excel 导入
     *
     * @param file      excel文件
     * @param pojoClass pojo类型
     * @param <T>
     * @return
     */
    public static <T> List<T> importExcel(MultipartFile file, Class<T> pojoClass) throws IOException {
        return importExcel(file, 1, 1, pojoClass);
    }

    /**
     * excel 导入
     *
     * @param filePath   excel文件路径
     * @param titleRows  表格内数据标题行
     * @param headerRows 表头行
     * @param pojoClass  pojo类型
     * @param <T>
     * @return
     */
    public static <T> List<T> importExcel(String filePath, Integer titleRows, Integer headerRows, Class<T> pojoClass) throws IOException {
        if (StringUtils.isBlank(filePath)) {
            return null;
        }
        ImportParams params = new ImportParams();
        params.setTitleRows(titleRows);
        params.setHeadRows(headerRows);
        params.setNeedSave(true);
        params.setSaveUrl("/excel/");
        try {
            return ExcelImportUtil.importExcel(new File(filePath), pojoClass, params);
        } catch (NoSuchElementException e) {
            throw new IOException("模板不能为空");
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }


    /**
     * excel 导入
     *
     * @param file       上传的文件
     * @param titleRows  表格内数据标题行
     * @param headerRows 表头行
     * @param pojoClass  pojo类型
     * @param <T>
     * @return
     */
    public static <T> List<T> importExcel(MultipartFile file, Integer titleRows, Integer headerRows, Class<T> pojoClass) throws IOException {
        if (file == null) {
            return null;
        }
        try {
            return importExcel(file.getInputStream(), titleRows, headerRows, pojoClass);
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    /**
     * excel 导入
     *
     * @param inputStream 文件输入流
     * @param titleRows   表格内数据标题行
     * @param headerRows  表头行
     * @param pojoClass   pojo类型
     * @param <T>
     * @return
     */
    public static <T> List<T> importExcel(InputStream inputStream, Integer titleRows, Integer headerRows, Class<T> pojoClass) throws IOException {
        if (inputStream == null) {
            return null;
        }
        ImportParams params = new ImportParams();
        params.setTitleRows(titleRows);
        params.setHeadRows(headerRows);
        params.setSaveUrl("/excel/");
        params.setNeedSave(true);
        try {
            return ExcelImportUtil.importExcel(inputStream, pojoClass, params);
        } catch (NoSuchElementException e) {
            throw new IOException("excel文件不能为空");
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    public static void main(String[] args) throws IOException {
        Map<String,Object> map = new HashMap<>();


        List<Map<String,Object>> list = new ArrayList<>();

        map.put("name","test");
        map.put("age",19);
        map.put("birthday",new Date());
        ImageEntity img =  new ImageEntity(MatrixUtil.createQRCode("33e344",300,300,"jpg"),100,100);
        map.put("barCode",img);

//        if (img.getRowspan()>1 || img.getColspan() > 1){
//            img.setHeight(0);
//            PoiMergeCellUtil.addMergedRegion(cell.getSheet(),cell.getRowIndex(),
//                    cell.getRowIndex() + img.getRowspan() - 1, cell.getColumnIndex(), cell.getColumnIndex() + img.getColspan() -1);
//        }

        Map<String,Object> tempmap = null;
        Map<String,Object> tempmap2 = null;
        List<Map<String,Object>> list2 = new ArrayList<>();

        for(int i=0;i<10;i++){
            tempmap = new HashMap<>();
            tempmap.put("name","test"+i);
            tempmap.put("age",19+i);
            tempmap.put("birthday",new Date());
//            list2 = new ArrayList<>();
//            for(int j=0;j<10;j++){
//                tempmap2 = new HashMap<>();
//                tempmap2.put("name","test2"+i);
//                tempmap2.put("age",69+i);
//                tempmap2.put("birthday",new Date());
//                list2.add(tempmap2);
//            }
//            tempmap.put("list",list2);
            list.add(tempmap);
        }
        map.put("list",list);
        exportExcelWithTemplate("/Users/bingdeng/Desktop/comma/easypoi.xlsx",map,new FileOutputStream("/Users/bingdeng/Desktop/comma/easypoi2.xlsx"));
    }
}
