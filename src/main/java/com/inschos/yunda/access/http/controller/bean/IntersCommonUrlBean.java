package com.inschos.yunda.access.http.controller.bean;

/**
 * 接口地址公共信息
 */
public class IntersCommonUrlBean {

    public static final String platform =  "YUNDA";

    public static final String origin = "1000";

    private static final String SERVER_HOST_ACCOUNT = "http://122.14.202.146:9200/api/account/web";

    private static final String SERVER_HOST_CUSTOMER = "http://122.14.202.146:9200/api/customer/web";

    private static final String SERVER_HOST_INSURE = "http://122.14.202.146:9200/api/trading/web";

    private static final String SERVER_HOST_TEST = "https://api-yunda.inschos.com/webapi";

    public static String getServerHostAccount() {
        return SERVER_HOST_ACCOUNT;
    }

    public static String getServerHostCustomer() {
        return SERVER_HOST_CUSTOMER;
    }

    public static String getServerHostInsure() {
        return SERVER_HOST_INSURE;
    }

    public static String getServerHostTest() {
        return SERVER_HOST_TEST;
    }

    //1.账号服务-个人信息接口
    //联合登录接口,获取user_id,account_id,manager_id,token
    public static final String toJointLogin = IntersCommonUrlBean.getServerHostAccount() + "/account/jointLogin";
    //根据userId,accountUuid获取个人详细信息
    public static final String toAccountInfo = IntersCommonUrlBean.getServerHostCustomer() + "/person/selfInfo";

    //2.交易服务-投保流程接口
    //投保(包括预投保流程)
    public static final String toInsured = IntersCommonUrlBean.getServerHostInsure() + "/insure";
    //支付(银行卡/微信)
    public static final String toPay = IntersCommonUrlBean.getServerHostInsure() + "/pay";

    //3.交易服务-授权签约接口
    //授权查询(银行卡+微信)
    public static final String toAuthorizeQuery = IntersCommonUrlBean.getServerHostInsure() + "/bank/getUsedPayInfo";
    //获取支付银行卡信息
    public static final String toPayBank = IntersCommonUrlBean.getServerHostInsure() + "/";
    //执行银行卡授权
    public static final String toBankAuthorize = IntersCommonUrlBean.getServerHostInsure() + "/";
    //微信签约
    public static final String toWechatContract = IntersCommonUrlBean.getServerHostInsure() + "/";

    //4.交易服务-保单接口
    //获取投保结果
    public static final String toWarrantyResult = IntersCommonUrlBean.getServerHostInsure() + "/query";
    //获取保单列表
    public static final String toWarrantyList = IntersCommonUrlBean.getServerHostInsure() + "/trade/get_insurance_policy_list_for_online_store";
    //获取保单详情
    public static final String toWarrantyInfo = IntersCommonUrlBean.getServerHostInsure() + "/trade/get_insurance_policy_detail_for_online_store";

    //4.交易服务-银行卡接口
    //添加银行卡
    public static final String toAddBank = IntersCommonUrlBean.getServerHostInsure() + "/bank/add";
    //获取银行卡列表
    public static final String toBankList = IntersCommonUrlBean.getServerHostInsure() + "/bank/list";
    //获取银行卡详情
    public static final String toBankInfo = IntersCommonUrlBean.getServerHostInsure() + "/bank/detail";
    //更改银行卡状态
    public static final String toUpdateBank = IntersCommonUrlBean.getServerHostInsure() + "/bank/modify";
    //删除银行卡
    public static final String toDeleteBank = IntersCommonUrlBean.getServerHostInsure() + "/bank/remove";
    //获取银行卡短信验证码
    public static final String toBankSms = IntersCommonUrlBean.getServerHostInsure() + "/bank/applyAuth";
    //校验银行卡短信验证码
    public static final String toVerifyBankSms = IntersCommonUrlBean.getServerHostInsure() + "/bank/confirmAuth";

    //5.交易服务-理赔服务接口(英大/泰康)

    //6.投保推送韵达
    public static final String toCallBackYunda = "https://apptest.yundasys.com/ywy-insurance/interface/ywy/insurance/pushinsuranceinfo.do";

    //7.联合登录入口URL
    public static final String toInsureInfo = "https://api-yunda.inschos.com/webapi/ins_info";

    public static final String toInsureCenter = "https://api-yunda.inschos.com/webapi/ins_center";

    public static final String toDoInsured = "https://api-yunda.inschos.com/webapi/do_insured";

    public static final String toDOBankAuthorize = "https://api-yunda.inschos.com/webapi/bank_authorize";
}
