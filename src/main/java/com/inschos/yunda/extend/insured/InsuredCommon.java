package com.inschos.yunda.extend.insured;


public class InsuredCommon {

    private static final String SERVER_HOST = "http://";
    /**
     * 接口地址1
     */
    public static final String get_province_code = InsuredCommon.getServerHost() + "/test";

    public static String getServerHost() {
        return SERVER_HOST;
    }

}
