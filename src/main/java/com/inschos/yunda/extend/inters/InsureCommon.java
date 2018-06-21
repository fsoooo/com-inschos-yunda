package com.inschos.yunda.extend.inters;

/**
 * 接口信息
 */
public class InsureCommon {

    private static final String SERVER_HOST_ACCOUNT = "http://122.14.202.146:9200/account";

    private static final String SERVER_HOST_INSURE = "http://122.14.202.146:9200/trading";

    public static String getServerHostAccount() {
        return SERVER_HOST_ACCOUNT;
    }

    public static String getServerHostInsure() {
        return SERVER_HOST_INSURE;
    }

    /**
     * 联合登录
     */
    public static final String toJointLogin = InsureCommon.getServerHostAccount() + "/jointLogin";

    /**
     * 投保
     */
    public static final String toInsured = InsureCommon.getServerHostInsure() + "/insure";

    /**
     * 支付
     */
    public static final String toPay = InsureCommon.getServerHostInsure() + "/pay";

    /**
     * 微信签约
     */
    public static final String toWechatContract = InsureCommon.getServerHostInsure() + "/wechatContract";

    /**
     * 微信代扣
     */
    public static final String toWechatPay = InsureCommon.getServerHostInsure() + "/wechatPay";

    /**
     * 出单
     */
    public static final String toIssue = InsureCommon.getServerHostInsure() + "/issue";

}
