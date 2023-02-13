package com.itheima.reggie.common;

/**
 * 基于ThreadLocal封装工具类，用户保存和获取当前登录用户id
 */

public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();
    public static void setCurrentId(Long id){  //设置ID
        threadLocal.set(id);
    }
    public static Long getCurrentId(){  //得到ID
        return threadLocal.get();
    }
}
