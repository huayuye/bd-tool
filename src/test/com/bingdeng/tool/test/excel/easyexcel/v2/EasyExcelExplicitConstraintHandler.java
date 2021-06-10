package com.bingdeng.tool.test.excel.easyexcel.v2;

import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import com.bingdeng.tool.StringUtil;
import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @Author: Fran
 * @Date: 2020/7/23
 * @Desc:
 **/
public class EasyExcelExplicitConstraintHandler implements SheetWriteHandler {

    /**
     * 导出数据对应的实体类
     */
    private Class clazz;
    /**
     * 第一级数据
     */
    private String[] levelFirstDatas;
    /**所有为父级的数据分别所对应的子集
     * key：父集
     * value: 此父级对应的子集
     */
    private Map<String,List<String>> levelDatas;
    public EasyExcelExplicitConstraintHandler() {
    }
    public EasyExcelExplicitConstraintHandler(Class clazz) {
        this.clazz=clazz;
    }

    public EasyExcelExplicitConstraintHandler(Class clazz,String[] levelFirstDatas) {
        this.clazz=clazz;
        this.levelFirstDatas=levelFirstDatas;
    }

    public EasyExcelExplicitConstraintHandler(Class clazz,String[] levelFirstDatas,Map<String,List<String>> levelDatas) {
        this.clazz=clazz;
        this.levelFirstDatas=levelFirstDatas;
        this.levelDatas=levelDatas;
    }

    @Override
    public void beforeSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {

    }

    @Override
    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        //结合注解-多级联动设置
        Sheet sheet = writeSheetHolder.getSheet();
        DataValidationHelper helper = sheet.getDataValidationHelper();
        Field[] fields = clazz.getDeclaredFields();
        if(fields==null && fields.length<=0){return;}
        for(int columnIndex=0;columnIndex<fields.length;columnIndex++){
            //一级内容设置
            EasyExcelExplicitConstraint dropDownList = fields[columnIndex].getAnnotation(EasyExcelExplicitConstraint.class);
            if(dropDownList==null)continue;
            if(dropDownList.hasSelect()){
                handleSelect(writeWorkbookHolder, sheet, helper, columnIndex, dropDownList);
            }
            handleDateFormat(dropDownList.dateFormat(),dropDownList.startRow(), dropDownList.maxRows(), columnIndex, columnIndex,sheet, helper);
        }
    }

    private void handleDateFormat(String dateFormat,Integer startRow, Integer maxRows, Integer startColumn, Integer endColumn, Sheet sheet, DataValidationHelper helper) {
        if(dateFormat==null || dateFormat.isEmpty()){return;}
        DataValidationConstraint dvConstraint = helper.createDateConstraint(DataValidationConstraint.OperatorType.BETWEEN, "Date(1900,1,1)",
                "Date(5099,1,1)", dateFormat);
        CellRangeAddressList cellRangeAddressList = new CellRangeAddressList();
        CellRangeAddress rangeAddress = new CellRangeAddress(startRow, maxRows, startColumn, endColumn);
        cellRangeAddressList.addCellRangeAddress(rangeAddress);
        DataValidation dataValidation = helper.createValidation(dvConstraint,cellRangeAddressList);
//        dataValidation.setSuppressDropDownArrow(false);
        dataValidation.setEmptyCellAllowed(true);
//        dataValidation.createPromptBox("输入提示", "请填写日期格式");
        // 设置输入错误提示信息
        dataValidation.createErrorBox("日期格式错误","请输入格式为 "+dateFormat+" 的日期!");
//        dataValidation.setShowPromptBox(true);
        if (dataValidation instanceof XSSFDataValidation) {
            dataValidation.setSuppressDropDownArrow(true);
            dataValidation.setShowErrorBox(true);
        } else {
            dataValidation.setSuppressDropDownArrow(false);
        }
        sheet.addValidationData(dataValidation);
    }

    private void handleSelect(WriteWorkbookHolder writeWorkbookHolder, Sheet sheet, DataValidationHelper helper, int columnIndex, EasyExcelExplicitConstraint dropDownList) {
        //            hashmap.put(dropDownList.column(),resolveAnnatotion(dropDownList));
        CellRangeAddressList cellRangeAddressList = new CellRangeAddressList();
        //指定起始行,起始列
        CellRangeAddress rangeAddress = new CellRangeAddress(dropDownList.startRow(), dropDownList.maxRows(), columnIndex, columnIndex);
        cellRangeAddressList.addCellRangeAddress(rangeAddress);
        //添加对应的数据
        String[] leveTop = this.levelFirstDatas;
        if(leveTop==null || leveTop.length<0){
            leveTop = resolveContents(dropDownList);
        }
        DataValidationConstraint constraint = helper.createExplicitListConstraint(leveTop);
        //在指定的行列加入数据列表
        DataValidation dataValidation = helper.createValidation(constraint,cellRangeAddressList);
        //添加数据的校验
        //允许为空
        dataValidation.setEmptyCellAllowed(true);
        dataValidation.createErrorBox("输入有误", "请选择下拉内容");
        if (dataValidation instanceof XSSFDataValidation) {
            dataValidation.setSuppressDropDownArrow(true);
            dataValidation.setShowErrorBox(true);
        } else {
            dataValidation.setSuppressDropDownArrow(false);
        }
        sheet.addValidationData(dataValidation);
        //是否有联动
        if(dropDownList.levelTandem()>1){
           Map<String,List<String>> leveDatas = this.levelDatas;
            if(leveDatas==null || leveDatas.isEmpty()){
                leveDatas = resolveLevelContents(dropDownList);
            }
            levelTandemSheet(writeWorkbookHolder.getWorkbook(), sheet, Arrays.asList(leveTop) ,leveDatas, columnIndex +1, dropDownList.levelTandem(), dropDownList.maxRows());
//                switch (dropDownList.levelTandem()){
//                    case 2:
//                        TwoLevelTandemSheet(writeWorkbookHolder, sheet, Arrays.asList(leveTop) ,leveDatas,columnIndex+1,dropDownList.levelTandem(),dropDownList.maxRows());
//                        break;
//                    case 3:
//                        ThreeLevelTandemSheet(writeWorkbookHolder, sheet,Arrays.asList(leveTop),leveDatas,columnIndex+1,dropDownList.levelTandem(),dropDownList.maxRows());
//                        break;
//                }
        }
    }

    /**
     *
     * @param workbook
     * @param currentSheet
     * @param firstLevelTandemColumn 列从1起，第一列为1
     * @param levelTandem
     * @param maxEffectiveRows
     */
    private void levelTandemSheet(Workbook workbook, Sheet currentSheet,List<String> levelTopData,Map<String,List<String>> levelDatas, int firstLevelTandemColumn, int levelTandem, int maxEffectiveRows) {
        setHiddenSheetData(currentSheet, levelTopData, levelDatas, firstLevelTandemColumn, levelTandem, maxEffectiveRows, workbook);
    }

    private void setHiddenSheetData(Sheet sheet, List<String> levelTopData, Map<String, List<String>> levelDatas, int firstLevelTandemColumn, int levelTandem, int maxEffectiveRows, Workbook workbook) {
        Sheet hideSheet = workbook.createSheet("levelHiddenAreaSheet");
        //隐藏sheet
        workbook.setSheetHidden(workbook.getSheetIndex(hideSheet),true);
        int rowId = 0;
        // 设置第一行，存一级的信息
        Row levelOneRow = hideSheet.createRow(rowId++);
        levelOneRow.createCell(0).setCellValue("一级列表");
        for (int i = 0; i < levelTopData.size(); i++) {
            Cell levelOneCell = levelOneRow.createCell(i + 1);
            levelOneCell.setCellValue(levelTopData.get(i));
        }
        // 将具体的数据写入到每一行中，行开头为父级区域，后面是子区域。
        List<String> keys = new ArrayList<String>(levelDatas.keySet());
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            List<String> sonLevels = levelDatas.get(key);
            Row row = hideSheet.createRow(rowId++);
            row.createCell(0).setCellValue(key);
            for (int j = 0; j < sonLevels.size(); j++) {
                Cell cell = row.createCell(j + 1);
                cell.setCellValue(sonLevels.get(j));
            }
            // 添加名称管理器
            String range = getRange(1, rowId, sonLevels.size());
            Name name = workbook.createName();
            //key不可重复
            name.setNameName(key);
            String formula = hideSheet.getSheetName()+"!" + range;
            name.setRefersToFormula(formula);
        }

        //对前n行设置有效性
        for (int i = 2; i < maxEffectiveRows; i++) {
            //二级 i行 A+1 列 由 i行 A 列 触发 即： 后一列由前一列触发
            //往后levelTandem+firstLevelTandemColumn-1列
            for (int j = firstLevelTandemColumn; j < levelTandem + firstLevelTandemColumn - 1; j++) {
                setDataValidation(((char) ('A' + j - 1)) + "", sheet, i, j + 1);
            }
        }
    }



    private String[] resolveContents(EasyExcelExplicitConstraint dropDownList) {
        if(dropDownList==null)return null;
        if(dropDownList.contents()!=null && dropDownList.contents().length>0){
            return dropDownList.contents();
        }
             Class<? extends IEasyExcelExplicitConstraint>[] clazz1 =  dropDownList.contentClass();
        if(clazz1!=null && clazz1.length>0){
            try {
              return   clazz1[0].newInstance().contents();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private Map<String,List<String>> resolveLevelContents(EasyExcelExplicitConstraint dropDownList) {
        if(dropDownList==null)return null;
        Class<? extends IEasyExcelExplicitConstraint>[] clazz1 =  dropDownList.contentClass();
        if(clazz1!=null && clazz1.length>0){
            try {
              return   clazz1[0].newInstance().levelContents();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return null;
    }






    /**
     * 设置有效性
     * @param offset 主影响单元格所在列，即此单元格由哪个单元格影响联动
     * @param sheet
     * @param rowNum 行数 第一行从1起
     * @param colNum 列数  第一列从1起
     */
    private static void setDataValidation(String offset,Sheet sheet, int rowNum,int colNum) {
        DataValidationHelper dvHelper = sheet.getDataValidationHelper();
        sheet.addValidationData(
                getDataValidationByFormula(
                        "INDIRECT($" + offset +"$"+ (rowNum) + ")", rowNum, colNum,dvHelper)
        );
    }

    /**
     * 加载下拉列表内容
     * @param formulaString
     * @param naturalRowIndex
     * @param naturalColumnIndex
     * @param dvHelper
     * @return
     */
    private static  DataValidation getDataValidationByFormula(
            String formulaString, int naturalRowIndex, int naturalColumnIndex,DataValidationHelper dvHelper) {
        // 加载下拉列表内容
        // 举例：若formulaString = "INDIRECT($A$2)" 表示规则数据会从名称管理器中获取key与单元格 A2 值相同的数据，
        //如果A2是江苏省，那么此处就是江苏省下的市信息。
        DataValidationConstraint dvConstraint =  dvHelper.createFormulaListConstraint(formulaString);
        // 设置数据有效性加载在哪个单元格上。
        // 四个参数分别是：起始行、终止行、起始列、终止列
        int firstRow = naturalRowIndex -1;
        int lastRow = naturalRowIndex - 1;
        int firstCol = naturalColumnIndex - 1;
        int lastCol = naturalColumnIndex - 1;
        //将数据(dvConstraint)作用于此单元格上
        //行列CellRangeAddressList中的参数从0起的，因此需要减1
        CellRangeAddressList regions = new CellRangeAddressList(firstRow,
                lastRow, firstCol, lastCol);
        // 数据有效性对象
        // 绑定
        DataValidation dataValidations =  dvHelper.createValidation(dvConstraint, regions);
        dataValidations.setEmptyCellAllowed(true);
        // 设置输入信息提示信息
        dataValidations.createErrorBox("输入有误", "请选择下拉内容");
        if (dataValidations instanceof XSSFDataValidation) {
            dataValidations.setSuppressDropDownArrow(true);
            dataValidations.setShowErrorBox(true);
        } else {
            dataValidations.setSuppressDropDownArrow(false);
        }

        return dataValidations;
    }

    /**
     *  计算formula
     * @param offset 偏移量，如果给0，表示从A列开始，1，就是从B列
     * @param rowId 第几行
     * @param colCount 一共多少列
     * @return 如果给入参 1,1,10. 表示从B1-K1。最终返回 $B$1:$K$1
     *
     */
    private static String getRange(int offset, int rowId, int colCount) {
//        char start = (char) ('A' + offset);
//        StringBuffer end = new StringBuffer();
//        int a = (colCount-1) / 26;
//        int b = (colCount-1) % 26;
//        for (int i = 0; i < a; i++) {
//            end.append("A");
//        }
//        end.append((char)('A'+b));
//
//        return "$" + start + "$" + rowId + ":$" + end.toString() + "$" + rowId;
        char start = (char)('A' + offset);
        if (colCount <= 25) {
            char end = (char)(start + colCount - 1);
            return "$" + start + "$" + rowId + ":$" + end + "$" + rowId;
        } else {
            char endPrefix = 'A';
            char endSuffix = 'A';
            if ((colCount - 25) / 26 == 0 || colCount == 51) {// 26-51之间，包括边界（两次字母表计算）
                if ((colCount - 25) % 26 == 0) {// 边界值
                    endSuffix = (char)('A' + 25);
                } else {
                    endSuffix = (char)('A' + (colCount - 25) % 26 - 1);
                }
            } else {// 51以上
                if ((colCount - 25) % 26 == 0) {
                    endSuffix = (char)('A' + 25);
                    endPrefix = (char)(endPrefix + (colCount - 25) / 26 - 1);
                } else {
                    endSuffix = (char)('A' + (colCount - 25) % 26 - 1);
                    endPrefix = (char)(endPrefix + (colCount - 25) / 26);
                }
            }
            return "$" + start + "$" + rowId + ":$" + endPrefix + endSuffix + "$" + rowId;
        }
    }




    //
//    private void threeLevelTandemSheet(WriteWorkbookHolder writeWorkbookHolder, Sheet currentSheet,List<String> levelTopData, Map<String,List<String>> levelDatas, int firstLevelTandemColumn, int levelTandem, int maxEffectiveRows) {
//        Workbook workbook= writeWorkbookHolder.getWorkbook();
//        setHiddenSheetData(currentSheet, levelTopData, levelDatas, firstLevelTandemColumn, levelTandem, maxEffectiveRows, workbook);
//    }

    //原始参考代码
//    private void LevelTandemSheet(WriteWorkbookHolder writeWorkbookHolder, Sheet sheet, int maxEffectiveRows) {
//        Workbook workbook= writeWorkbookHolder.getWorkbook();
//        Sheet hideSheet = workbook.createSheet("area");
//        //隐藏sheet
////        hideSheet.setColumnHidden(workbook.getSheetIndex(hideSheet),true);
//
//        //得到第一级省名称，放在列表里
//        String[] provinceArr = {"江苏省","安徽省"};
//        //依次列出各省的市、各市的县
//        String[] cityJiangSu = {"南京市","苏州市","盐城市"};
//        String[] cityAnHui = {"合肥市","安庆市"};
//        String[] countyNanjing = {"六合县","江宁县"};
//        String[] countySuzhou = {"姑苏区","园区"};
//        String[] countyYancheng = {"响水县","射阳县"};
//        String[] countyLiuhe = {"瑶海区","庐阳区"};
//        String[] countyAnQing = {"迎江区","大观区"};
//        //将有子区域的父区域放到一个数组中
//        String[] areaFatherNameArr ={"江苏省","安徽省","南京市","苏州市","盐城市","合肥市","安庆市"};
//        Map<String,String[]> areaMap = new HashMap<String, String[]>();
//        areaMap.put("江苏省", cityJiangSu);
//        areaMap.put("安徽省",cityAnHui);
//        areaMap.put("南京市",countyNanjing);
//        areaMap.put("苏州市", countySuzhou);
//        areaMap.put("盐城市",countyYancheng);
//        areaMap.put("合肥市",countyYancheng);
//        areaMap.put("合肥市", countyLiuhe);
//        areaMap.put("安庆市",countyAnQing);
//
//        int rowId = 0;
//        // 设置第一行，存省的信息
//        Row provinceRow = hideSheet.createRow(rowId++);
//        provinceRow.createCell(0).setCellValue("省列表");
//        for(int i = 0; i < provinceArr.length; i ++){
//            Cell provinceCell = provinceRow.createCell(i + 1);
//            provinceCell.setCellValue(provinceArr[i]);
//        }
//        // 将具体的数据写入到每一行中，行开头为父级区域，后面是子区域。
//        for(int i = 0;i < areaFatherNameArr.length;i++){
//            String key = areaFatherNameArr[i];
//            String[] son = areaMap.get(key);
//            Row row = hideSheet.createRow(rowId++);
//            row.createCell(0).setCellValue(key);
//            for(int j = 0; j < son.length; j ++){
//                Cell cell = row.createCell(j + 1);
//                cell.setCellValue(son[j]);
//            }
//
//            // 添加名称管理器
//            String range = getRange(1, rowId, son.length);
//            Name name = workbook.createName();
//            //key不可重复
//            name.setNameName(key);
//            String formula = "area!" + range;
//            name.setRefersToFormula(formula);
//        }
//
//        //对前20行设置有效性
//        for(int i = 2;i < maxEffectiveRows;i++){
//            setDataValidation("A" ,sheet,i,2);
//            setDataValidation("B" ,sheet,i,3);
//        }
//    }

//    /**
//     *
//     * @param writeWorkbookHolder
//     * @param currentSheet
//     * @param firstLevelTandemColumn 列从1起，第一列为1
//     * @param levelTandem
//     * @param maxEffectiveRows
//     */
//    private void twoLevelTandemSheet(WriteWorkbookHolder writeWorkbookHolder, Sheet currentSheet,List<String> levelTopData,Map<String,List<String>> levelDatas, int firstLevelTandemColumn, int levelTandem, int maxEffectiveRows) {
//        Workbook workbook= writeWorkbookHolder.getWorkbook();
//        setHiddenSheetData(currentSheet, levelTopData, levelDatas, firstLevelTandemColumn, levelTandem, maxEffectiveRows, workbook);
//    }

    //    private Map<Integer,String[]> resolveContents(){
//        Map<Integer,String[]> hashmap = new HashMap<>();
//       Field[] fields = clazz.getDeclaredFields();
//       if(fields==null && fields.length<=0){return Collections.EMPTY_MAP; }
//       for(int i = 0;i<fields.length;i++){
//            EasyExcelExplicitConstraint dropDownList = fields[i].getAnnotation(EasyExcelExplicitConstraint.class);
//           if(dropDownList==null)continue;
//            hashmap.put(i,resolveContents(dropDownList));
//       }
//       return hashmap;
//    }

}
