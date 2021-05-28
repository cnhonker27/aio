package org;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Singleton {
    // 饿汉模式：即程序运行时创建
    private static Singleton load_singleton;

    static {
        load_singleton=new Singleton();
    }

    // 懒汉模式：即程序调用时创建
    static class SingletonBuilder {
       private static Singleton lazy_singleton=new Singleton();
    }

    private Singleton(){}

    public static Singleton getSingletonInstance1(){
        return load_singleton;
    }

    public static Singleton getSingletonInstance2(){
        return SingletonBuilder.lazy_singleton;
    }

    public static void main(String[] args) {
        int maxLengthStr1 = getMaxLengthStr1("112113");
        System.out.println(maxLengthStr1);
        int max = Math.max(1, 2);
        System.out.println(max);
    }


    /**
     *
     * @param str
     * @return
     */
    public static int getMaxLengthStr1(String str) {
        int max = 0;
        int tmp_length= 0;
        Map<Character, Integer> map = new HashMap<>();
        for (int i = 0; i < str.length(); i++) {
            // 判断是否存在map中
            if (map.containsKey(str.charAt(i))) {
                // 存在则重新计算当前字符所在位置
                tmp_length = Math.max(map.get(str.charAt(i)), tmp_length);
            }
            // 取最大值：当前下标-临时长度+1 是否大于上一个出现的字符的最大长度
            max = Math.max(max, i - tmp_length + 1);
            // 放入当前字符位置+1，
            map.put(str.charAt(i), i + 1);
        }
        return max;
    }
}
