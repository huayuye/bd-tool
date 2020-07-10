package com.bingdeng.tool.test.excel.easyexcel.v2;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Fran
 * @Date: 2020/7/10
 * @Desc:
 **/
public class EasyExcelUserListener extends AnalysisEventListener<EasyExcelUser> {
    private List<EasyExcelUser> users = new ArrayList<>();
    private EasyExcelDao excelDao;
    public EasyExcelUserListener(EasyExcelDao excelDao){
        this.excelDao=excelDao;
    }  public EasyExcelUserListener(){
    }

    //每一行调用一次
    @Override
    public void invoke(EasyExcelUser data, AnalysisContext context) {
        users.add(data);
        //10条存一次
        if(users.size()>=10){
        saveData();
            //处理完 记得清理list（避免重复处理）,否则继续叠加时，还会包含前面处理过的数据
            users.clear();
        }
    }
    //读完一个sheet之后 会调用一次
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        saveData();
    }

    public void saveData(){
        insertdb(users);
    }

    private void insertdb(List<EasyExcelUser> articles) {
        System.out.println();
    }
}
