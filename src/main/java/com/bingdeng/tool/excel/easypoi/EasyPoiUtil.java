package com.bingdeng.tool.excel.easypoi;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

/**
 * Excel导入导出工具类
 * @author bingdeng
 * 官方文档 http://doc.wupaas.com/docs/easypoi/easypoi-1c0u4mo8p4ro8
 */

public class EasyPoiUtil {

    /**
     * excel 模版导出
     *
     * @param map     数据列表
     * @param outputStream
     */
    public static void exportExcelWithTemplate(String templateUrl,Map<String, Object> map, OutputStream outputStream) throws IOException {
        TemplateExportParams templateExportParams = new TemplateExportParams();
        templateExportParams.setTemplateUrl(templateUrl);
        exportExcelWithTemplate(templateExportParams,map,outputStream);
    }

    /**
     * excel 导出
     * @param templateExportParams
     * @param map
     * @param outputStream
     * @throws IOException
     */
    private static void exportExcelWithTemplate(TemplateExportParams templateExportParams,Map<String, Object> map, OutputStream outputStream) throws IOException {
        //缓存中重新获取文件
//        POICacheManager.setFileLoader(new FileLoaderImpl());
        //把数据添加到excel表格中
        Workbook workbook = ExcelExportUtil.exportExcel(templateExportParams,map);
        downLoadExcel( outputStream, workbook);
    }

    /**
     * excel 导出
     *
     * @param list     数据列表
     * @param outputStream
     */
    public static void exportExcel(List<Map<String, Object>> list, OutputStream outputStream) throws IOException {
        //把数据添加到excel表格中
        Workbook workbook = ExcelExportUtil.exportExcel(list, ExcelType.XSSF);
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
        //把数据添加到excel表格中
        Workbook workbook = ExcelExportUtil.exportExcel(exportParams, pojoClass, list);
        downLoadExcel( outputStream, workbook);
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
    public static void exportExcel(List<?> list, Class<?> pojoClass, String title, String sheetName, OutputStream outputStream) throws IOException {
        exportExcel(list, pojoClass, new ExportParams(title, sheetName, ExcelType.XSSF), outputStream);
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
    public static void exportExcel(List<?> list, Class<?> pojoClass, String title, String sheetName, boolean isCreateHeader, OutputStream outputStream) throws IOException {
        ExportParams exportParams = new ExportParams(title, sheetName, ExcelType.XSSF);
        exportParams.setCreateHeadRows(isCreateHeader);
        exportExcel(list, pojoClass, exportParams, outputStream);
    }


    /**
     * excel下载
     *
     * @param outputStream
     * @param workbook excel数据
     */
    private static void downLoadExcel(OutputStream outputStream, Workbook workbook) throws IOException {
        try {
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
     * @param titleRows  表格内数据标题行数 默认0
     * @param headerRows 表头行数 默认1
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
}
