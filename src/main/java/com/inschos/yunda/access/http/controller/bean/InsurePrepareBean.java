package com.inschos.yunda.access.http.controller.bean;

public class InsurePrepareBean {
    public static class Requset {
        public String account_id;
        public String biz_content;
        public String sign;
        public String timestamp;
    }

    public static class Response extends BaseResponseBean {
        public ResponseData data;
    }

    public static class ResponseData {
        public String status;
        public String content;
    }

    public static class InsureRequest {
        public String channel_user_name;//姓名
        public String channel_user_phone;//手机
        public String channel_user_code;//身份证号
        public String channel_user_email;//邮件

        public String channel_user_address;//地址
        public String channel_provinces;//省
        public String channel_city;//市
        public String channel_county;//县

        public String channel_bank_code;//银行卡号
        public String channel_bank_name;//银行卡名称
        public String channel_bank_phone;//银行卡绑定手机号
        public String channel_bank_address;//银行的开户行地址

        public String courier_state;//站点地址
        public String courier_start_time;//分拣时间

        public String channel_back_url;//回调地址
        public String channel_code;//渠道代码
        public String operate_code;//操作代码
        public String is_insure;//是否投保
        public String p_code;//产品代码
    }
}
