package com.bingdeng.tool.test.excel.easypoi;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.util.Date;

/**
 * @Author: Fran
 * @Date: 2021/7/8
 * @Desc:
 **/
@Data
public class PoiUser {
    @Excel(name = "姓名")
    private String name;
    @Excel(name = "年龄")
    private Integer age;
    @Excel(name = "出生日期",exportFormat="yyyy-MM-dd HH:mm:ss",importFormat = "yyyy-MM-dd HH:mm:ss")
    private Date birthday;
    public PoiUser() {

    }
    public PoiUser(String name, Integer age, Date birthday) {
        this.name = name;
        this.age = age;
        this.birthday = birthday;
    }
}
