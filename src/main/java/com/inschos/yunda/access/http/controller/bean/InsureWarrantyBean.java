package com.inschos.yunda.access.http.controller.bean;

import java.util.List;

public class InsureWarrantyBean {
    public static class warrantyRecordRequest {
        public long id;//用户id
        public long custId;//用户id
        public String warrantyUuid;//'warranty_uuid内部保单唯一标识'
        public String warrantyStatus;//保单状态 待支付/保障中/已失效
        public String warrantyStatusText;//保单状态文案
        public long createdAt;
        public long updatedAt;
    }

    public static class warrantyListRequest {
        public String token;
        public String warrantyStatus;//非必传，保单状态，1-待支付 2-待生效 3-保障中 4-已失效 90-业绩
        public String searchKey;//非必传，搜索关键字
        public String searchType;//非必传，搜索关键字类型，1-保单号 2-代理人 3-投保人 4-车牌号 5-保险公司 6-保险产品 7-被保人
        public String insuranceProductKey;//非必传，保险产品名称
        public String insuranceCompanyKey;//非必传，保险公司名称
        public String timeType;//非必传，1-签单时间（下单时间），2-起保时间，3-缴费时间
        public String startTime;//非必传，开始时间
        public String endTime;//非必传，结束时间
        public String warrantyType;//非必传，保单类型，1-个人保单，2-团险保单，3-车险保单
        public String queryWay;//非必传,查询方式：默认账户，1-代理人
    }

    public static class warrantyListResponse extends BaseResponseBean {
        public List<warrantyListResponseData> data;
        public PageBean page;
    }

    public static class warrantyListResponseData {
        public Long id;//主键
        public String warrantyUuid;//内部保单唯一标识
        public String prePolicyNo;//投保单号
        public String warrantyCode;//保单号
        public String accountUuid;//归属账号uuid
        public String buyerAuuid;//买家uuid
        public String agentAuuid;//代理人ID为null则为用户自主购买
        public String channelId;//渠道ID为0则为用户自主购买
        public String planId;//计划书ID为0则为用户自主购买
        public String productId;//产品ID
        public String premium;//保单价格
        public String premiumText;//保单价格(显示用)
        public String payMoney;//付款金额
        public String payMoneyText;//付款金额(显示用)
        public String taxMoney;//税费
        public String taxMoneyText;//税费(显示用)
        public String startTime;//起保时间
        public String startTimeText;//起保时间(显示用)
        public String endTime;//结束时间
        public String endTimeText;//结束时间(显示用)
        public String term;//保障区间
        public String insCompanyId;//保险公司ID
        public String count;//购买份数
        public String payTime;//支付时间
        public String payTimeText;//支付时间(显示用)
        public String payWay;//支付方式 1 银联 2 支付宝 3 微信 4现金
        public String payWayText;//支付方式 1 银联 2 支付宝 3 微信 4现金(显示用)
        public String payCategoryId;//分期方式id
        public String payCategoryName;//分期方式（显示用）
        public String isSettlement;//佣金 0表示未结算，1表示已结算
        public String isSettlementText;//佣金 0表示未结算，1表示已结算(显示用)
        public String warrantyUrl;//电子保单下载地址
        public String warrantyFrom;//保单来源 1 自购 2线上成交 3线下成交 4导入
        public String warrantyFromText;//保单来源 1 自购 2线上成交 3线下成交 4导入(显示用)
        public String type;//保单类型1表示个人保单，2表示团险保单，3表示车险保单
        public String payStatus;//支付状态 201-核保中 202-核保失败 203-待支付 204-支付中 205-支付取消 206-支付成功
        public String warrantyStatus;//保单状态 1-投保中，2-待生效，3-保障中，4-可续保，5-已过保，6-已失效
        public String warrantyStatusText;//保单状态(显示用)
    }

    public static class warrantyInfoRequest {
        public String token;//用户account_uuid
        public String warrantyUuid;//'warranty_uuid内部保单唯一标识'
    }

    public static class warrantyInfoResponse extends BaseResponseBean {
        public warrantyInfoResponseData data;
    }

    public static class warrantyInfoResponseData {
        public Long id;//主键
        public String warrantyUuid;//内部保单唯一标识
        public String prePolicyNo;//投保单号
        public String warrantyCode;//保单号
        public String accountUuid;//归属账号uuid
        public String buyerAuuid;//买家uuid
        public String agentAuuid;//代理人ID为null则为用户自主购买
        public String channelId;//渠道ID为0则为用户自主购买
        public String planId;//计划书ID为0则为用户自主购买
        public String productId;//产品ID
        public String premium;//保单价格
        public String premiumText;//保单价格(显示用)
        public String payMoney;//付款金额
        public String payMoneyText;//付款金额(显示用)
        public String taxMoney;//税费
        public String taxMoneyText;//税费(显示用)
        public String startTime;//起保时间
        public String startTimeText;//起保时间(显示用)
        public String endTime;//结束时间
        public String endTimeText;//结束时间(显示用)
        public String term;//保障区间
        public String insCompanyId;//保险公司ID
        public String count;//购买份数
        public String payTime;//支付时间
        public String payTimeText;//支付时间(显示用)
        public String payWay;//支付方式 1 银联 2 支付宝 3 微信 4现金
        public String payWayText;//支付方式 1 银联 2 支付宝 3 微信 4现金(显示用)
        public String payCategoryId;//分期方式id
        public String payCategoryName;//分期方式（显示用）
        public String isSettlement;//佣金 0表示未结算，1表示已结算
        public String isSettlementText;//佣金 0表示未结算，1表示已结算(显示用)
        public String warrantyUrl;//电子保单下载地址
        public String warrantyFrom;//保单来源 1 自购 2线上成交 3线下成交 4导入
        public String warrantyFromText;//保单来源 1 自购 2线上成交 3线下成交 4导入(显示用)
        public String type;//保单类型1表示个人保单，2表示团险保单，3表示车险保单
        public String payStatus;//支付状态 201-核保中 202-核保失败 203-待支付 204-支付中 205-支付取消 206-支付成功
        public String warrantyStatus;//保单状态 1-投保中，2-待生效，3-保障中，4-可续保，5-已过保，6-已失效
        public String warrantyStatusText;//保单状态(显示用)

        public PolicyHolder policyHolder;//投保人
        public List<InsuredList> insuredList;//被保险人
        public List<BeneficiaryList> beneficiaryList;//受益人
        public List<CostList> costList;//付款记录
        public List<BrokerageList> brokerageList;//佣金记录
    }

    public static class PolicyHolder{
        public String warrantyUuid;//内部保单唯一标识",
        public String type;//人员类型: 1投保人 2被保人 3受益人",
        public String typeText;//人员类型: 1投保人 2被保人 3受益人(显示用)",
        public String relationName;//被保人 投保人的（关系）",
        public String outOrderNo;//被保人单号",
        public String name;//姓名",
        public String cardType;//证件类型（1为身份证，2为护照，3为军官证）",
        public String cardTypeText;//证件类型（1为身份证，2为护照，3为军官证）(显示用)",
        public String cardCode;//证件号",
        public String phone;//手机号",
        public String occupation;//职业",
        public String birthday;//生日",
        public String birthdayText;//生日(显示用)",
        public String sex;//性别 1 男 2 女",
        public String sexText;//性别 1 男 2 女(显示用)",
        public String age;//年龄",
        public String email;//邮箱",
        public String nationality;//国籍",
        public String annualIncome;//年收入",
        public String height;//身高",
        public String weight;//体重",
        public String area;//地区",
        public String address;//详细地址",
        public String startTime;//开始时间",
        public String startTimeText;//开始时间(显示用)",
        public String endTime;//结束时间",
        public String endTimeText;//结束时间(显示用)"
    }

    public static class InsuredList{
        public String warrantyUuid;//内部保单唯一标识",
        public String type;//人员类型: 1投保人 2被保人 3受益人",
        public String typeText;//人员类型: 1投保人 2被保人 3受益人(显示用)",
        public String relationName;//被保人 投保人的（关系）",
        public String outOrderNo;//被保人单号",
        public String name;//姓名",
        public String cardType;//证件类型（1为身份证，2为护照，3为军官证）",
        public String cardTypeText;//证件类型（1为身份证，2为护照，3为军官证）(显示用)",
        public String cardCode;//证件号",
        public String phone;//手机号",
        public String occupation;//职业",
        public String birthday;//生日",
        public String birthdayText;//生日(显示用)",
        public String sex;//性别 1 男 2 女",
        public String sexText;//性别 1 男 2 女(显示用)",
        public String age;//年龄",
        public String email;//邮箱",
        public String nationality;//国籍",
        public String annualIncome;//年收入",
        public String height;//身高",
        public String weight;//体重",
        public String area;//地区",
        public String address;//详细地址",
        public String startTime;//开始时间",
        public String startTimeText;//开始时间(显示用)",
        public String endTime;//结束时间",
        public String endTimeText;//结束时间(显示用)"
    }

    public static class BeneficiaryList{
        public String warrantyUuid;//内部保单唯一标识",
        public String type;//人员类型: 1投保人 2被保人 3受益人",
        public String typeText;//人员类型: 1投保人 2被保人 3受益人(显示用)",
        public String relationName;//被保人 投保人的（关系）",
        public String outOrderNo;//被保人单号",
        public String name;//姓名",
        public String cardType;//证件类型（1为身份证，2为护照，3为军官证）",
        public String cardTypeText;//证件类型（1为身份证，2为护照，3为军官证）(显示用)",
        public String cardCode;//证件号",
        public String phone;//手机号",
        public String occupation;//职业",
        public String birthday;//生日",
        public String birthdayText;//生日(显示用)",
        public String sex;//性别 1 男 2 女",
        public String sexText;//性别 1 男 2 女(显示用)",
        public String age;//年龄",
        public String email;//邮箱",
        public String nationality;//国籍",
        public String annualIncome;//年收入",
        public String height;//身高",
        public String weight;//体重",
        public String area;//地区",
        public String address;//详细地址",
        public String startTime;//开始时间",
        public String startTimeText;//开始时间(显示用)",
        public String endTime;//结束时间",
        public String endTimeText;//结束时间(显示用)"
    }

    public static class CostList{
        public Long id;//主键
        public String warrantyUuid;//内部保单唯一标识
        public String payTime;//应支付时间
        public String payTimeText;//应支付时间（显示用）
        public String phase;//第几期
        public String premium;//保单价格
        public String premiumText;//保单价格（显示用）
        public String taxMoney;//税费
        public String taxMoneyText;//税费（显示用）
        public String actualPayTime;//实际支付时间
        public String actualPayTimeText;//实际支付时间（显示用）
        public String payWay;//支付方式 1 银联 2 支付宝 3 微信 4现金
        public String payWayText;//支付方式 1 银联 2 支付宝 3 微信 4现金（显示用）
        public String payMoney;//付款金额
        public String payMoneyText;//付款金额（显示用）
        public String payStatus;//支付状态 201-核保中 202-核保失败 203-待支付 204-支付中 205-支付取消 206-支付成功
        public String payStatusText;//支付状态 201-核保中 202-核保失败 203-待支付 204-支付中 205-支付取消 206-支付成功（显示用）
        public String isSettlement;//结算状态，0-未结算，1-已结算
        public String isSettlementText;//结算状态，0-未结算，1-已结算（显示用）
        public String billUuid;//结算单uuid
        public String createdAt;//创建时间
        public String createdAtText;//创建时间（显示用）
        public String updatedAt;//结束时间
        public String updatedAtText;//结束时间（显示用
    }

    public static class  BrokerageList{
        public String id;//唯一标识
        public String warrantyUuid;//内部保单唯一标识
        public String managerUuid;//业管唯一标识
        public String costId;//支付id
        public String channelId;//机构id
        public String agentId;//代理人id
        public String warrantyMoney;//保单佣金
        public String warrantyMoneyText;//保单佣金(显示用)
        public String insMoney;//天眼佣金
        public String insMoneyText;//天眼佣金(显示用)
        public String managerMoney;//业管佣金
        public String managerMoneyText;//业管佣金(显示用)
        public String channelMoney;//渠道佣金
        public String channelMoneyText;//渠道佣金(显示用)
        public String agentMoney;//代理人佣金
        public String agentMoneyText;//代理人佣金(显示用)
        public String warrantyRate;//保单佣金比例
        public String warrantyRateText;//保单佣金比例(显示用)
        public String insRate;//天眼佣金比例
        public String insRateText;//天眼佣金比例(显示用)
        public String managerRate;//业管佣金比例
        public String managerRateText;//业管佣金比例(显示用)
        public String channelRate;//渠道佣金比例
        public String channelRateText;//渠道佣金比例(显示用)
        public String agentRate;//代理人佣金比例
        public String agentRateText;//代理人佣金比例(显示用)
        public String createdAt;//记录创建时间
        public String createdAtText;//记录创建时间(显示用)
        public String updatedAt;//记录更新时间
        public String updatedAtText;//记录更新时间(显示用
    }

    public static class insureResultRequest {
        public String token;
    }

    public static class insureResultResponse extends BaseResponseBean {
        public insureResultResponseData data;
    }

    public static class insureResultResponseData {

    }

    public static class warrantyStatusRequest{
        public String warrantyUuid;
    }

    public static class warrantyStatusResponse extends BaseResponseBean{
        public warrantyStatusResponseData data;
    }

    public static class warrantyStatusResponseData{
        public String warrantyUuid;
        public String policyNo;
        public String proposalNo;
        public String status;
        public String statusTxt;
        public String remark;
    }
}
