package com.inschos.yunda.access.http.controller.bean;

public class CallbackYundaBean {

    public static class Requset {
        public String token;
        public String ordersId;//保单号 256
        public String payTime;//支付时间 64
        public String effectiveTime;//生效时段 64
        public String type;//套餐类型,当日订单，长效订单 64
        public String status;//保单状态 10
        public String ordersName;//保单名称 32
        public String companyName;//保险公司名称 32
    }

    public static class Response extends BaseResponseBean {
        public InsureParamsBean.ResponseData data;
    }

    public static class ResponseData {
        public String code;//错误编码
        public String remark;//结果描述
        public String data;//返回数据 可为空
        public Boolean result;//接口调用结果
    }
}
