package com.bingdeng.tool.test.excel.easyexcel.v2;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: Fran
 * @Date: 2020/7/24
 * @Desc:
 **/
public class EasyExcelExplicitConstraintImpl implements IEasyExcelExplicitConstraint {
    @Override
    public String[] contents() {
        return new String[]{"江苏省2","安徽省2"};
    }
    @Override
    public Map<String, List<String>> levelContents(){
        //依次列出各省的市、各市的县
        List<String> cityJiangSu = Arrays.asList("南京市","苏州市","盐城市");
        List<String> cityAnHui = Arrays.asList("合肥市","安庆市");
        List<String> countyNanjing = Arrays.asList("六合县","江宁县");
        List<String> countySuzhou = Arrays.asList("姑苏区","园区");
        List<String> countyYancheng = Arrays.asList("响水县","射阳县");
        List<String> countyLiuhe = Arrays.asList("瑶海区","庐阳区");
        List<String> countyAnQing = Arrays.asList("迎江区","大观区");
        //将有子区域的父区域放到一个数组中
//        String[] areaFatherNameArr ={"江苏省","安徽省","南京市","苏州市","盐城市","合肥市","安庆市"};
        Map<String,List<String>> areaMap = new HashMap<String, List<String>>();
        areaMap.put("苏州市", countySuzhou);
        areaMap.put("盐城市",countyYancheng);
        areaMap.put("江苏省", cityJiangSu);
        areaMap.put("安徽省",cityAnHui);
        areaMap.put("南京市",countyNanjing);
        areaMap.put("合肥市",countyYancheng);
        areaMap.put("合肥市", countyLiuhe);
        areaMap.put("安庆市",countyAnQing);
        return areaMap;
    }
}
