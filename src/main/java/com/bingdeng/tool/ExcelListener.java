package com.bingdeng.tool;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Fran
 * @Date: 2019/4/23
 * @Desc:
 **/
public class ExcelListener extends AnalysisEventListener {

    private List<Object> datas = new ArrayList<>();

    @Override
    public void invoke(Object o, AnalysisContext analysisContext) {
        datas.add(o);
        //do something
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }

    public List<Object> getDatas() {
        return datas;
    }
}
