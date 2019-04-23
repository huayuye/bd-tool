package com.bingdeng.tool;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * @Author: Fran
 * @Date: 2019/1/31
 * @Desc:
 **/
public class RandomUtil {


    public synchronized static String getRandom() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")) + UUID.randomUUID().toString().replace("-", "").substring(26);
    }

}
