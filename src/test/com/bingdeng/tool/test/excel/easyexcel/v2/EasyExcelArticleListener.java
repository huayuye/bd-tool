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
public class EasyExcelArticleListener extends AnalysisEventListener<EasyExcelArticle> {

    private List<EasyExcelArticle> articles = new ArrayList<>();
    private EasyExcelDao excelDao;
    public EasyExcelArticleListener(EasyExcelDao excelDao){
        this.excelDao=excelDao;
    }

    public EasyExcelArticleListener(){
    }

    //每一行调用一次
    @Override
    public void invoke(EasyExcelArticle data, AnalysisContext context) {
        articles.add(data);
        //10条处理一次
        if(articles.size()>=10){
            saveData();
            //处理完 记得清理list（避免重复处理）,否则继续叠加时，还会包含前面处理过的数据
            articles.clear();
        }
    }

    //读完一个sheet之后 会调用一次
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        //最后再一次性处理，也可以在这里或在invoke分批次处理业务（在invoke分批次后,记得最后还要在doAfterAllAnalysed调用一次业务处理方法（处理剩余数据））
        saveData();
    }

    public void saveData(){
        insertdb(articles);
    }

    private void insertdb(List<EasyExcelArticle> articles) {
        System.out.println();
    }
}
