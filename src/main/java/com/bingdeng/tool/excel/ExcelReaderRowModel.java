package com.bingdeng.tool.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;

import java.util.Date;

/**
 * @Author: Fran
 * @Date: 2019/4/23
 * @Desc:
 **/
public class ExcelReaderRowModel extends BaseRowModel {

    /**
     * ExcelProperty 中：
     * value为数组，表示表头的，有多个时，表示这一列有多个表头
     * index 表示第几列，从0开始
     * 若表头需要特殊格式的，则需要自己精心去排版了，示例只是简单测试。
     *  规则：比如 name和identifyCard，都有姓名且开头且两行，那么第一、二列的表头有两行，第一行则是合并了第一、二列，
     *      第二行则分别显示不同的两列（昵称，身份证号）
     */
    @ExcelProperty(index = 0)
    private String name;
    @ExcelProperty(index = 1)
    private String identifyCard;
    @ExcelProperty(index = 2)
    private String educationBackgroup;
    @ExcelProperty(index = 3)
    private String address;
    @ExcelProperty(index = 4)
    private String email;
    @ExcelProperty(index = 5,format = "yyyy-MM-dd HH:mm:ss")
    private Date birthday;

    public String getName() {
        return name;
    }

    public ExcelReaderRowModel setName(String name) {
        this.name = name;
        return this;
    }

    public String getIdentifyCard() {
        return identifyCard;

    }

    public ExcelReaderRowModel setIdentifyCard(String identifyCard) {
        this.identifyCard = identifyCard;
        return this;
    }

    public String getEducationBackgroup() {
        return educationBackgroup;
    }

    public ExcelReaderRowModel setEducationBackgroup(String educationBackgroup) {
        this.educationBackgroup = educationBackgroup;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public ExcelReaderRowModel setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public ExcelReaderRowModel setEmail(String email) {
        this.email = email;
        return this;
    }

    public Date getBirthday() {
        return birthday;
    }

    public ExcelReaderRowModel setBirthday(Date birthday) {
        this.birthday = birthday;
        return this;
    }
}
