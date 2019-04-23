package com.bingdeng.tool;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.Iterator;

/**
 * @Author: Fran
 * @Date: 2019/1/18
 * @Desc:
 **/
public class JsonUtil {


    public static void analyzeJson(Object objJson) {
        //        如果obj为json数组
        if (objJson instanceof JSONArray) {
            JSONArray objArray = (JSONArray) objJson;
            for (int i = 0; i < objArray.size(); i++) {
                analyzeJson(objArray.get(i));
            }
        }
        //        如果为json对象
        else if (objJson instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) objJson;
            /*---------------------------------------------此段属于自己的业务逻辑1，视具体情况而定-----------------------------*/
            //每个对象中的id的值可以在这里获取（这是为了将这里的id赋值给pid）：
//            Long id = 0L;
//            Iterator itTepm = jsonObject.keys();
//            while(itTepm.hasNext()){
//                if( ("id").equals(itTepm.next().toString())){
//                    id = jsonObject.getInt("id");
//                }
//            }
            // 这里独立执行一次while(it.hasNext()){}即可获取到，不能在下面的逻辑获取，否则会有属性顺序的隐患，导致获取到的所有id一样
            /*---------------------------------------------此段属于自己的业务逻辑1，视具体情况而定---结束--------------------------*/
            Iterator it = jsonObject.keys();
            while (it.hasNext()) {
                String key = it.next().toString();
                Object object = jsonObject.get(key);
                // 如果得到的是数组
                if (object instanceof JSONArray) {
                    JSONArray objArray = (JSONArray) object;
                    /*---------------------------------------------此段属于自己的业务逻辑2，视具体情况而定-----------------------------*/
                    //如有给pid等类似赋值的，可以在这里实现
//                    for (int i = 0; i < objArray.size(); i++) {
//                        JSONObject jsonObject2 = (JSONObject)objArray.get(i);
//                        jsonObject2.put("pid",id);
//                    }
                    /*---------------------------------------------此段属于自己的业务逻辑2，视具体情况而定----结束-------------------------*/
                    analyzeJson(objArray);
                }
                // 如果key中是一个json对象
                else if (object instanceof JSONObject) {
                    analyzeJson((JSONObject) object);
                }
                // 如果key中是其他
                else {
                    System.out.println("[" + key + "]:" + object.toString() + " ");
                }
            }
        }
    }


    /**
     *注意：这里面的Object是对应自己的菜单类：必须含有类似上下级关系的id,pid，子集合(如setSubMenus:List)
     * @param list 水平结构的所有菜单
     * @param topCode 最顶层菜单的pid
     * @return
     */
//    public static List<Object> createMenuTree(List<Object> list, Long topCode) {
//        List<Object> parentList = new ArrayList<>(list.size());
//        //获取顶层元素集合
//        long parentCode;
//        for (Object entity : list) {
//            parentCode = entity.getPid();
//            //顶层元素的parentCode==null或者为0
//            if (topCode==parentCode) {
//                parentList.add(entity);
//            }
//        }
//
//        //获取每个顶层元素的子数据集合
//        for (Object entity : parentList) {
//            entity.setSubMenus(getSubList(entity.getId(), list));
//        }
//        return parentList;
//    }
//
//    private static List<Object> getSubList(Long id, List<Object> entityList) {
//        List<Object> childList = new ArrayList<>();
//        Long parentId;
//        //子集的直接子对象
//        for (Object entity : entityList) {
//            parentId = entity.getPid();
//            if (id.equals(parentId)) {
//                childList.add(entity);
//            }
//        }
//        //子集的间接子对象
//        for (Object entity : childList) {
//            entity.setSubMenus(getSubList(entity.getId(), entityList));
//        }
//        //递归退出条件
//        if (childList.size() == 0) {
//            return Collections.EMPTY_LIST;
//        }
//        return childList;
//    }


}
