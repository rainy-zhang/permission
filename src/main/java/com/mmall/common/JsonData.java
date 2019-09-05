package com.mmall.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: zhangyu
 * @Description: 用来处理controller返回json数据时的实体类
 * @Date: in 2019/8/25 13:48
 */
@Getter
@Setter
@NoArgsConstructor  //没有参数的构造器
public class JsonData {

    /**
     * 标识返回值是否正常
     */
    private boolean ret;

    private String msg;

    private Object data;

    public JsonData(boolean ret) {
        this.ret = ret;
    }

    /**
     * 请求成功时
     */
    public static JsonData success(Object data, String msg){
        JsonData jsonData = new JsonData();
        jsonData.ret = true;
        jsonData.setData(data);
        jsonData.setMsg(msg);
        return jsonData;
    }
    public static JsonData success(Object data){
        JsonData jsonData = new JsonData();
        jsonData.ret = true;
        jsonData.setData(data);
        return jsonData;
    }
    public static JsonData success(){
        JsonData jsonData = new JsonData();
        jsonData.ret = true;
        return jsonData;
    }

    /**
     * 请求失败时
     */
    public static JsonData failed(String msg){
        JsonData jsonData = new JsonData();
        jsonData.setMsg(msg);
        jsonData.setRet(false);
        return jsonData;
    }
    public static JsonData failed(){
        JsonData jsonData = new JsonData();
        jsonData.setRet(false);
        return jsonData;
    }

    /**
     * 将JsonData对象封装成一个map
     */
    public Map<String, Object> toMap(){
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("ret", ret);
        map.put("msg", msg);
        map.put("data", data);
        return map;
    }
}
