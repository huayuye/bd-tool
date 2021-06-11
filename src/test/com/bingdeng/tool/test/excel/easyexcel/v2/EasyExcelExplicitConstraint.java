package com.bingdeng.tool.test.excel.easyexcel.v2;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: Fran
 * @Date: 2020/7/23
 * @Desc: 此是否作为下拉框，支持多级，多级时，只需要定义在第一级且往后级数必须是相邻的
 * 如三级联动：A 列为一级，那二级必须为B,三级必须为C
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface EasyExcelExplicitConstraint {

    /**
     * 下拉内容始于第几行有效
     * @return
     */
    int startRow() default 1;

    /**
     * 下拉内容最大有效行数
     * @return
     */
    int maxRows() default 10000;

    /**
     * 日期格式
     * @return
     */
    String dateFormat() default "";

    /**
     * 当有下拉框选择时仅用于一级下拉框内容
     * @return
     */
    String[] contents() default{};

    /***
     * 所需要用的当前文档中的名称管理器的所在sheet的名称
     * 比如:
     * 如果多列相应使用的下拉框
     * （1）内容均相同,那这里一定要是相同名称
     * （2）内容均不相同,那这里一定要是不相同名称
     * @return
     */
    String optionDataSheet() default "";
    /**
     * 多级联动数,默认0,无下拉选择框,1-一级,2-二级联动,以此类推
     * @return
     */
    int levelTandem() default 0;

}
