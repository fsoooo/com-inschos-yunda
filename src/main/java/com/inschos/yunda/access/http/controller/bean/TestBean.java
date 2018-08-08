package com.inschos.yunda.access.http.controller.bean;

import java.util.List;

public class TestBean {

    public static class Requset {
        public String ordersId;
        public String payTime;
        public String effectiveTime;
        public String type;
        public String status;
        public String ordersName;
        public String companyName;
    }

    public static class Response {
        public String code;
        public String remark;
        public String data;
        public String result;
    }
}
