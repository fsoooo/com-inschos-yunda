package com.inschos.yunda.extend.inters;

import com.inschos.yunda.model.IntersResponse;

import java.util.List;

public class InsureResponse {

    public static final int RESULT_OK = 1;
    public static final int RESULT_FAIL = 0;
    public static final String ERROR_SI100100000063 = "SI100100000063";

    public String sendTime;
    public String sign;
    public int state;
    public String msg;
    public String msgCode;

    // 验证签名用
    public boolean verify;

    public String code;
    public List<IntersResponse.Message> message;
    public IntersResponse.Data data;

    public class Message {
        public String digest;//default
        public String details;//成功
    }

    public class Data {
        public String status;//保单状态
        public String statusTxt;//保单状态文案
        public String warrantyUuid;//保单UUID
    }

}
