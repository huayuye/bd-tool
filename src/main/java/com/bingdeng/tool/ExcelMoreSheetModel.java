package com.bingdeng.tool;

import com.alibaba.excel.metadata.BaseRowModel;

import java.util.List;

/**
 * @Author: Fran
 * @Date: 2019/4/23
 * @Desc:
 **/
public class ExcelMoreSheetModel {
   private String sheetName;
   private Class<? extends BaseRowModel> headers;
   private List<? extends BaseRowModel> datas;

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public Class<? extends BaseRowModel> getHeaders() {
        return headers;
    }

    public void setHeaders(Class<? extends BaseRowModel> headers) {
        this.headers = headers;
    }

    public List<? extends BaseRowModel> getDatas() {
        return datas;
    }

    public void setDatas(List<? extends BaseRowModel> datas) {
        this.datas = datas;
    }
}
