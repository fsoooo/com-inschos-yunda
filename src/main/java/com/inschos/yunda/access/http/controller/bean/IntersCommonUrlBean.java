package com.inschos.yunda.access.http.controller.bean;

/**
 * 接口地址公共信息
 */
public class IntersCommonUrlBean {

    private static final String SERVER_HOST_ACCOUNT = "http://122.14.202.146:9200/account";

    private static final String SERVER_HOST_INSURE = "http://122.14.202.146:9200/trading";

    private static final String SERVER_HOST_YUNDA = "http://yunda.com/test";

    private static final String SERVER_HOST_TEST = "https://api-yunda.inschos.com/webapi";

    public static String getServerHostAccount() {
        return SERVER_HOST_ACCOUNT;
    }

    public static String getServerHostInsure() {
        return SERVER_HOST_INSURE;
    }

    public static String getServerHostCallBackYunda() {
        return SERVER_HOST_YUNDA;
    }

    public static String getServerHostTest() {
        return SERVER_HOST_TEST;
    }

    //1.联合登录接口,获取user_id,account_id,manager_id,token
    public static final String toJointLogin = IntersCommonUrlBean.getServerHostAccount() + "/";

    //2.交易服务-投保流程接口
    //投保(包括预投保流程)
    public static final String toInsured = IntersCommonUrlBean.getServerHostInsure() + "/insure";
    //支付(银行卡/微信)
    public static final String toPay = IntersCommonUrlBean.getServerHostInsure() + "/pay";

    //3.交易服务-授权签约接口
    //授权查询(银行卡)
    public static final String toAuthorizeQueryBank = IntersCommonUrlBean.getServerHostInsure() + "/";
    //获取支付银行卡信息
    public static final String toPayBank = IntersCommonUrlBean.getServerHostInsure() + "/";
    //执行银行卡授权
    public static final String toDoAuthorize = IntersCommonUrlBean.getServerHostInsure() + "/";
    //授权查询(微信免密签约)
    public static final String toAuthorizeQueryWechat = IntersCommonUrlBean.getServerHostInsure() + "/";
    //微信签约
    public static final String toWechatContract = IntersCommonUrlBean.getServerHostInsure() + "/";

    //4.交易服务-保单接口
    //获取投保结果
    public static final String toInsureResult = IntersCommonUrlBean.getServerHostCallBackYunda() + "/";
    //获取保单列表
    public static final String toWarrantyList = IntersCommonUrlBean.getServerHostCallBackYunda() + "/";
    //获取保单详情
    public static final String toWarrantyInfo = IntersCommonUrlBean.getServerHostCallBackYunda() + "/";

    //4.交易服务-银行卡接口
    //添加银行卡
    public static final String toAddBank = IntersCommonUrlBean.getServerHostCallBackYunda() + "/";
    //获取银行卡列表
    public static final String toBankList = IntersCommonUrlBean.getServerHostCallBackYunda() + "/";
    //获取银行卡详情
    public static final String toBankInfo = IntersCommonUrlBean.getServerHostCallBackYunda() + "/";
    //更改银行卡状态
    public static final String toUpdateBank = IntersCommonUrlBean.getServerHostCallBackYunda() + "/";

    //5.交易服务-理赔服务接口(英大/泰康)

    //6.投保推送韵达
    public static final String toCallBackYunda = IntersCommonUrlBean.getServerHostCallBackYunda() + "/";

    //TODO 测试http请求接口
    public static final String toHttpTest = IntersCommonUrlBean.getServerHostTest() + "/joint_login?bank_code=6217001210078544622 &bank_address=11&channel_code=YD&insured_province=320000&insured_county=320982&insured_code=342225199504065369&insured_phone=15856218334&insured_city=320900&insured_name=曹桥桥&insured_address=11&bank_phone=111&bank_name=11&insured_email=111&channel_order_code=1111";

}
