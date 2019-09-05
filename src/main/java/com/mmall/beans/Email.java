package com.mmall.beans;

import lombok.*;

import java.util.Set;

/**
 * @Author: zhangyu
 * @Description: 封装一些邮件的信息
 * @Date: in 2019/9/1 13:26
 */
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Email {
    //主题
    private String subject;
    //信息
    private String message;
    //接收的邮箱
    private Set<String> receivers;

}
