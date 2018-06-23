package com.inschos.yunda.access.http.controller.bean;

public class AuthorizeQueryBean {

    public static class Requset {
        public String channel_code;
        public String insured_name;
        public String insured_code;
        public String insured_phone;
    }

    public static class Response {
        public String status;//授权状态:01未授权/02已授权
        public String url;//授权页面地址
    }
}
