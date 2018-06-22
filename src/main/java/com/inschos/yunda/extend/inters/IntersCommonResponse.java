package com.inschos.yunda.extend.inters;

import com.inschos.yunda.model.IntersResponse;

import java.util.List;

public class IntersCommonResponse {

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
}
