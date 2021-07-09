package com.bingdeng.tool.test.excel.easypoi;

import cn.afterturn.easypoi.entity.ImageEntity;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import com.bingdeng.tool.MatrixUtil;
import com.bingdeng.tool.excel.easypoi.EasyPoiUtil;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * @Author: Fran
 * @Date: 2021/7/8
 * @Desc:
 **/
public class EasyPoiTest {
    public static void exportWithTemplate() throws IOException {
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
            list.add(tempmap);
        }
        map.put("list",list);
        EasyPoiUtil.exportExcelWithTemplate("C:\\htmlpdf\\SixUnionSingleNumber.xlsx",map,new FileOutputStream("C:\\htmlpdf\\easypoi3.xlsx"));

    }

    public static void main(String[] args) throws IOException {
        PoiUser poiUser = null;
        List<PoiUser> list = new ArrayList<>();
        for(int i=0;i<10;i++){
            poiUser = new PoiUser("test1"+i,12+i,new Date());
            list.add(poiUser);
        }
        EasyPoiUtil.exportExcel(list,PoiUser.class,new ExportParams("tesy","test", ExcelType.XSSF),new FileOutputStream("C:\\htmlpdf\\easypoi4.xlsx"));


        list =  EasyPoiUtil.importExcel("C:\\htmlpdf\\easypoi4.xlsx",1,1,PoiUser.class);
        System.out.println("args = " + list.toString());
    }
}
