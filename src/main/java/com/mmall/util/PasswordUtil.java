package com.mmall.util;

import java.util.Date;
import java.util.Random;

/**
 * @Author: zhangyu
 * @Description: 生成密码的工具类
 * @Date: in 2019/8/27 22:07
 */
public class PasswordUtil {

    public final static String[] word = {
            "a", "b", "c", "d", "e", "f", "g",
            "h", "j", "k", "m", "n",
            "p", "q", "r", "s", "t",
            "u", "v", "w", "x", "y", "z",
            "A", "B", "C", "D", "E", "F", "G",
            "H", "J", "K", "M", "N",
            "P", "Q", "R", "S", "T",
            "U", "V", "W", "X", "Y", "Z"
    };

    public final static String[] num = {
            "2", "3", "4", "5", "6", "7", "8", "9"
    };

    /**
     * 返回一个长度在8-11位的随机密码
     */
    public static String randomPassword(){
        StringBuffer password = new StringBuffer();
        Random random = new Random(new Date().getTime());
        boolean flag = false;
        int length = random.nextInt(3) + 8;
        for (int i = 0; i < length; i++) {
            if(flag){
                password.append(word[random.nextInt(word.length)]);
            }else{
                password.append(num[random.nextInt(num.length)]);
            }
            flag = !flag;
        }
        return password.toString();
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println(randomPassword());
        Thread.sleep(100);
        System.out.println(randomPassword());
        Thread.sleep(100);
        System.out.println(randomPassword());
    }

}
