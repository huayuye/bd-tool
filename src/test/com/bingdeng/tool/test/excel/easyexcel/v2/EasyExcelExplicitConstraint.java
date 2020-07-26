package com.bingdeng.tool.test.excel.easyexcel.v2;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: Fran
 * @Date: 2020/7/23
 * @Desc: 此是否作为下拉框，支持多级，多级时，只需要定义在第一级且往后级数必须是相邻的
 * 如三级联动：A 列为一级，那🎧必须为B,三级必须为C
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface EasyExcelExplicitConstraint {
    /**
     * 下拉内容放于第几列:默认为实体对象属性顺序，以0开始
     * @return
     */
//    int column() default 0;

    /**
     * 多级联动数,默认1 无联动
     * @return
     */
    int levelTandem() default 1;

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
     * 下拉内容
     * @return
     */
    String[] contents() default{};

    /**
     * 支持动态查询
     * @return
     */
    Class[] contentClass() default{};
}
