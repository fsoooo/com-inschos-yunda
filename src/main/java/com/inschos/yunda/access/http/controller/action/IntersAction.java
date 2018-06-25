package com.inschos.yunda.access.http.controller.action;

import com.inschos.yunda.access.http.controller.bean.*;
import com.inschos.yunda.assist.kit.*;
import com.inschos.yunda.data.dao.*;
import com.inschos.yunda.model.*;
import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.inschos.yunda.access.http.controller.bean.IntersCommonUrlBean.*;

@Component
public class IntersAction extends BaseAction {

    private static final Logger logger = Logger.getLogger(IntersAction.class);

    @Autowired
    private JointLoginDao jointLoginDao;

    @Autowired
    private WarrantyRecordDao warrantyRecordDao;

    @Autowired
    private StaffPersonDao staffPersonDao;

    /**
     * 联合登录
     *
     * @return json
     * @params actionBean
     * @access public
     * <p>
     * 韵达触发联合登录，获取报文信息请求账号服务(登录/注册)，获取account_id,manager_id,user_id,token
     */
    public String jointLogin(HttpServletRequest httpServletRequest) {
        JointLoginBean.Requset request = JsonKit.json2Bean(HttpKit.readRequestBody(httpServletRequest), JointLoginBean.Requset.class);
        BaseResponseBean response = new BaseResponseBean();
        //判空
        if (request == null) {
            return json(BaseResponseBean.CODE_FAILURE, "请检查报文格式是否正确", response);
        }
        if (request.insured_name == null || request.insured_code == null || request.insured_phone == null) {
            return json(BaseResponseBean.CODE_FAILURE, "姓名,证件号,手机号不能为空", response);
        }
        //TODO 联合登录表插入数据,先判断今天有没有插入,再插入登录记录.每天只有一个记录
        long date = new Date().getTime();
        Calendar calendar = Calendar.getInstance();
        long date_start = calendar.getTimeInMillis();
        JointLogin jointLogin = new JointLogin();
        jointLogin.login_start = date;
        jointLogin.phone = request.insured_phone;
        jointLogin.day_start = date_start;
        jointLogin.day_end = date_start + 24 * 60 * 60 * 1000;
        long repeatRes = jointLoginDao.findLoginRecord(jointLogin);
        if (repeatRes == 0) {
            long login_id = jointLoginDao.addLoginRecord(jointLogin);
        }
        //TODO 触发联合登录,同步操作 http 请求 账号服务
        String loginRes = doAccount(request);
        if (loginRes == null) {
            return json(BaseResponseBean.CODE_FAILURE, "账号服务调用失败", response);
        }
        JointLoginBean.Response loginResponse = JsonKit.json2Bean(loginRes, JointLoginBean.Response.class);
        if (loginResponse == null) {
            return json(BaseResponseBean.CODE_FAILURE, "账号服务调用失败", response);
        }
        if (loginResponse.code == 500) {
            return json(BaseResponseBean.CODE_FAILURE, "账号服务调用失败", response);
        }
        response.data = loginResponse.data;
        //return json(BaseResponseBean.CODE_SUCCESS, "操作成功", response);
        //TODO 触发联合登录,异步操作 http 请求 投保服务
        String insureRes = doInsured(request);
        if (insureRes == null) {
            return json(BaseResponseBean.CODE_FAILURE, "投保接口调用失败", response);
        }
        InsureParamsBean.Response insureResponse = JsonKit.json2Bean(insureRes, InsureParamsBean.Response.class);
        if (insureResponse == null) {
            return json(BaseResponseBean.CODE_FAILURE, "投保接口调用失败", response);
        }
        if (insureResponse.code != 200) {
            return json(BaseResponseBean.CODE_FAILURE, "投保接口调用失败", response);
        }
        return json(BaseResponseBean.CODE_SUCCESS, "操作成功", response);
    }

    /**
     * 授权查询
     *
     * @return json
     * @params actionBean
     * @access public
     * 授权查询同样需要触发联合登录
     */
    public String authorizationQuery(HttpServletRequest httpServletRequest) {
        JointLoginBean.Requset request = JsonKit.json2Bean(HttpKit.readRequestBody(httpServletRequest), JointLoginBean.Requset.class);
        BaseResponseBean response = new BaseResponseBean();
        AuthorizeQueryBean.ResponseData authorizeQueryResponseData = new AuthorizeQueryBean.ResponseData();
        //判空
        if (request == null) {
            return json(BaseResponseBean.CODE_FAILURE, "请检查报文格式是否正确", response);
        }
        if (request.insured_name == null || request.insured_code == null || request.insured_phone == null) {
            return json(BaseResponseBean.CODE_FAILURE, "姓名,证件号,手机号不能为空", response);
        }
        //TODO 触发联合登录,同步操作 http 请求 账号服务
        String loginRes = doAccount(request);
        if (loginRes == null) {
            return json(BaseResponseBean.CODE_FAILURE, "账号服务调用失败", response);
        }
        JointLoginBean.Response loginResponse = JsonKit.json2Bean(loginRes, JointLoginBean.Response.class);
        if (loginResponse == null) {
            return json(BaseResponseBean.CODE_FAILURE, "账号服务调用失败", response);
        }
        if (loginResponse.code != 200) {
            return json(BaseResponseBean.CODE_FAILURE, "账号服务调用失败", response);
        }
        //TODO 查询授权详情
        String authorizeRes = doAuthorizeRes(request);
        if (authorizeRes == null) {
            return json(BaseResponseBean.CODE_FAILURE, "授权查询接口调用失败", response);
        }
        AuthorizeQueryBean.Response authorizeResponse = JsonKit.json2Bean(authorizeRes, AuthorizeQueryBean.Response.class);
        if (authorizeResponse == null) {
            return json(BaseResponseBean.CODE_FAILURE, "授权查询接口调用失败", response);
        }
        if (authorizeResponse.code != 200) {
            return json(BaseResponseBean.CODE_FAILURE, "授权查询接口调用失败", response);
        }
        //TODO 返回参数
        authorizeQueryResponseData.status = "";
        authorizeQueryResponseData.url = "";
        response.data = authorizeQueryResponseData;
        if (authorizeQueryResponseData.status == "01") {
            String responseText = "未授权";
            return json(BaseResponseBean.CODE_FAILURE, responseText, response);
        } else if (authorizeQueryResponseData.status == "02") {
            String responseText = "已授权";
            return json(BaseResponseBean.CODE_FAILURE, responseText, response);
        } else {
            return json(BaseResponseBean.CODE_FAILURE, "", response);
        }
    }

    /**
     * 预投保
     *
     * @return
     * @params account_id
     * @params biz_content
     * @params sign
     * @params timestamp
     */
    public String prepareInusre(HttpServletRequest httpServletRequest) {
        InsurePrepareBean.Requset request = JsonKit.json2Bean(HttpKit.readRequestBody(httpServletRequest), InsurePrepareBean.Requset.class);
        BaseResponseBean response = new BaseResponseBean();
        if (request == null) {
            return json(BaseResponseBean.CODE_FAILURE, "参数解析失败", response);
        }
        if (request.biz_content == null) {
            return json(BaseResponseBean.CODE_FAILURE, "必要参数为空", response);
        }
        Base64Kit base64Kit = new Base64Kit();
        String biz_content = base64Kit.getFromBase64(request.biz_content);
        if (biz_content == null) {
            return json(BaseResponseBean.CODE_FAILURE, "预投保参数解析失败", response);
        }
        List<InsurePrepareBean.InsureRequest> insureRequests = JsonKit.json2Bean(biz_content, new TypeReference<List<InsurePrepareBean.InsureRequest>>() {
        });
        for (InsurePrepareBean.InsureRequest insureRequest : insureRequests) {
            JointLoginBean.Requset jointLoginRequest = new JointLoginBean.Requset();
            jointLoginRequest.channel_code = insureRequest.channel_code;
            jointLoginRequest.insured_name = insureRequest.channel_user_name;
            jointLoginRequest.insured_code = insureRequest.channel_user_code;
            jointLoginRequest.insured_phone = insureRequest.channel_user_phone;
            jointLoginRequest.insured_email = insureRequest.channel_user_email;
            jointLoginRequest.insured_province = insureRequest.channel_provinces;
            jointLoginRequest.insured_city = insureRequest.channel_city;
            jointLoginRequest.insured_county = insureRequest.channel_county;
            jointLoginRequest.insured_address = insureRequest.channel_user_address;
            jointLoginRequest.bank_name = insureRequest.channel_bank_name;
            jointLoginRequest.bank_code = insureRequest.channel_bank_code;
            jointLoginRequest.bank_phone = insureRequest.channel_bank_phone;
            jointLoginRequest.bank_address = insureRequest.channel_bank_address;
            jointLoginRequest.channel_order_code = "";
            //TODO  http 请求 投保服务
            String insureRes = doInsured(jointLoginRequest);
            if (insureRes == null) {
                return json(BaseResponseBean.CODE_FAILURE, "投保接口调用失败", response);
            }
            InsureParamsBean.Response insureResponse = JsonKit.json2Bean(insureRes, InsureParamsBean.Response.class);
            if (insureResponse == null) {
                return json(BaseResponseBean.CODE_FAILURE, "投保接口调用失败", response);
            }
            if (insureResponse.code != 200) {
                return json(BaseResponseBean.CODE_FAILURE, "投保接口调用失败", response);
            }
        }
        //TODO 预投保接口跟其他接口的返回格式不同
        return json(BaseResponseBean.CODE_SUCCESS, "操作成功", response);
    }

    /**
     * 保险推送接口,做异步处理
     *
     * @param httpServletRequest
     * @return
     */
    public String CallBackYunda(HttpServletRequest httpServletRequest) {
        CallbackYundaBean.Requset request = JsonKit.json2Bean(HttpKit.readRequestBody(httpServletRequest), CallbackYundaBean.Requset.class);
        BaseResponseBean response = new BaseResponseBean();
        if (request == null) {
            return json(BaseResponseBean.CODE_FAILURE, "参数解析失败", response);
        }
        if (request.ordersId == null || request.payTime == null || request.effectiveTime == null || request.type == null || request.status == null || request.ordersName == null || request.companyName == null) {
            return json(BaseResponseBean.CODE_FAILURE, "参数解析失败", response);
        }
        CallbackYundaBean.Requset callbackYundaRequest = new CallbackYundaBean.Requset();
        callbackYundaRequest.ordersId = request.ordersId;
        callbackYundaRequest.payTime = request.payTime;
        callbackYundaRequest.effectiveTime = request.effectiveTime;
        callbackYundaRequest.type = request.type;
        callbackYundaRequest.status = request.status;
        callbackYundaRequest.ordersName = request.ordersName;
        callbackYundaRequest.companyName = request.companyName;
        try {
            //TODO 请求http
            String result = HttpClientKit.post(toCallBackYunda, JsonKit.bean2Json(callbackYundaRequest));
            if (result == null) {
                return json(BaseResponseBean.CODE_FAILURE, "投保推送韵达请求失败", response);
            }
            CallbackYundaBean.Response responseData = JsonKit.json2Bean(result, CallbackYundaBean.Response.class);
            if (response.code != 200) {
                return json(BaseResponseBean.CODE_FAILURE, "投保推送韵达请求失败", response);
            }
            response.data = responseData;
            return json(BaseResponseBean.CODE_SUCCESS, "接口请求成功", response);
        } catch (IOException e) {
            return json(BaseResponseBean.CODE_FAILURE, "投保推送韵达请求失败", response);
        }
    }

    /**
     * 获取授权详情(微信+银行卡)
     *
     * @return
     */
    private String doAuthorizeRes(JointLoginBean.Requset request) {
        BaseResponseBean response = new BaseResponseBean();
        JointLoginBean.Requset jointLoginRequest = new JointLoginBean.Requset();
        jointLoginRequest.insured_name = request.insured_name;
        jointLoginRequest.insured_code = request.insured_code;
        jointLoginRequest.insured_phone = request.insured_phone;
        try {
            //TODO 请求http
            //String authorizeRes = HttpClientKit.post(toAuthorizeQuery, JsonKit.bean2Json(jointLoginRequest));
            String authorizeRes = HttpClientKit.post(toHttpTest, JsonKit.bean2Json(jointLoginRequest));
            if (authorizeRes == null) {
                return json(BaseResponseBean.CODE_FAILURE, "授权查询请求失败", response);
            }
            AuthorizeQueryBean.Response authorizeResponse = JsonKit.json2Bean(authorizeRes, AuthorizeQueryBean.Response.class);
            if (authorizeResponse.code != 200) {
                return json(BaseResponseBean.CODE_FAILURE, "授权查询请求失败", response);
            }
            response.data = authorizeResponse;
            return json(BaseResponseBean.CODE_SUCCESS, "接口请求成功", response);
        } catch (IOException e) {
            return json(BaseResponseBean.CODE_FAILURE, "授权查询请求失败", response);
        }
    }

    /**
     * 调用账号服务
     *
     * @param request
     * @return
     */
    private String doAccount(JointLoginBean.Requset request) {
        BaseResponseBean response = new BaseResponseBean();
        JointLoginBean.Requset jointLoginRequest = new JointLoginBean.Requset();
        jointLoginRequest.channel_code = request.channel_code;
        jointLoginRequest.insured_name = request.insured_name;
        jointLoginRequest.insured_code = request.insured_code;
        jointLoginRequest.insured_phone = request.insured_phone;
        jointLoginRequest.insured_email = request.insured_email;
        jointLoginRequest.insured_province = request.insured_province;
        jointLoginRequest.insured_city = request.insured_city;
        jointLoginRequest.insured_county = request.insured_county;
        jointLoginRequest.insured_address = request.insured_address;
        jointLoginRequest.bank_name = request.bank_name;
        jointLoginRequest.bank_code = request.bank_code;
        jointLoginRequest.bank_phone = request.bank_phone;
        jointLoginRequest.bank_address = request.bank_address;
        jointLoginRequest.channel_order_code = request.channel_order_code;
        try {
            //TODO 请求http
//            String accountRes = HttpClientKit.post(toJointLogin,JsonKit.bean2Json(jointLoginRequest));
            String accountRes = HttpClientKit.post(toHttpTest, JsonKit.bean2Json(jointLoginRequest));
            if (accountRes == null) {
                return json(BaseResponseBean.CODE_FAILURE, "账号服务接口请求失败", response);
            }
            JointLoginBean.AccountResponse accountResponse = JsonKit.json2Bean(accountRes, JointLoginBean.AccountResponse.class);
            if (accountResponse.code == 500) {
                return json(BaseResponseBean.CODE_FAILURE, "账号服务接口请求失败", response);
            }
            response.data = accountResponse.data;
            return json(BaseResponseBean.CODE_SUCCESS, "接口请求成功", response);
        } catch (IOException e) {
            return json(BaseResponseBean.CODE_FAILURE, "账号服务接口请求失败", response);
        }
    }

    /**
     * 投保操作，先走英大投保，再进行泰康投保
     *
     * @return
     */
    private String doInsured(JointLoginBean.Requset request) {
        BaseResponseBean response = new BaseResponseBean();
        //投保人基础信息
        InsureParamsBean.PolicyHolder policyHolder = new InsureParamsBean.PolicyHolder();
        policyHolder.name = request.insured_name;
        policyHolder.cardType = "1";
        policyHolder.cardCode = request.insured_code;
        policyHolder.phone = request.insured_phone;
        policyHolder.relationName = "本人";
        //以下数据 按业务选填
        policyHolder.occupation = "";//职业
        policyHolder.birthday = "";//生日 时间戳
        policyHolder.sex = "";//性别 1 男 2 女
        policyHolder.age = "";//年龄
        policyHolder.email = "";//邮箱
        policyHolder.nationality = "";//国籍
        policyHolder.annualIncome = "";//年收入
        policyHolder.height = "";//身高
        policyHolder.weight = "";//体重
        policyHolder.area = "";//地区
        policyHolder.address = "";//详细地址
        policyHolder.courier_state = "";//站点地址
        policyHolder.courier_start_time = "";//分拣开始时间
        policyHolder.province = "";//省
        policyHolder.city = "";//市
        policyHolder.county = "";//县
        policyHolder.bank_code = "";//银行卡号
        policyHolder.bank_name = "";//银行卡名字
        policyHolder.bank_phone = "";//银行卡绑定手机
        policyHolder.insure_days = "";//购保天数
        policyHolder.price = "";//价格
        //TODO 投保人被保人和受益人是本人
        List<InsureParamsBean.PolicyHolder> recognizees = new ArrayList<>();
        recognizees.add(policyHolder);
        InsureParamsBean.Requset insuredRequest = new InsureParamsBean.Requset();
        insuredRequest.productId = 90;//产品id:英大90,泰康91
        insuredRequest.startTime = "";
        insuredRequest.endTime = "";
        insuredRequest.count = "";
        insuredRequest.businessNo = "";
        insuredRequest.payCategoryId = "";
        insuredRequest.businessNo = "";
        insuredRequest.policyholder = policyHolder;
        insuredRequest.recognizees = recognizees;
        insuredRequest.beneficiary = policyHolder;
        try {
            //TODO 请求http
            String insureRes = HttpClientKit.post(toInsured, JsonKit.bean2Json(insuredRequest));
            if (insureRes == null) {
                return json(BaseResponseBean.CODE_FAILURE, "投保接口请求失败", response);
            }
            InsureParamsBean.Response insureResponse = JsonKit.json2Bean(insureRes, InsureParamsBean.Response.class);
            if (insureResponse.code != 200) {
                return json(BaseResponseBean.CODE_FAILURE, "投保接口请求失败", response);
            }
            InsureParamsBean.ResponseData responseData = new InsureParamsBean.ResponseData();
            InusrePayBean.Requset inusrePayRequest = new InusrePayBean.Requset();
            //TODO 保单记录表添加/更新
            //TODO 支付参数
            inusrePayRequest.payNo = insureResponse.data.payNo;
            inusrePayRequest.payWay = insureResponse.data.payType;
            //TODO 投保成功,调用支付
            String payRes = doInsurePay(request, inusrePayRequest);
            if (payRes == null) {
                return json(BaseResponseBean.CODE_FAILURE, "支付接口调用失败", response);
            }
            InusrePayBean.Response payResponse = JsonKit.json2Bean(payRes, InusrePayBean.Response.class);
            if (payResponse == null) {
                return json(BaseResponseBean.CODE_FAILURE, "支付接口调用失败", response);
            }
            if (payResponse.code != 200) {
                return json(BaseResponseBean.CODE_FAILURE, "支付接口调用失败", response);
            }
            String statusText = payResponse.data.statusTxt;//支付返回文案
            return json(BaseResponseBean.CODE_SUCCESS, statusText, response);
        } catch (IOException e) {
            return json(BaseResponseBean.CODE_FAILURE, "投保接口请求失败", response);
        }
    }

    /**
     * 投保支付操作
     *
     * @param inusrePayRequest
     * @return
     */
    private String doInsurePay(JointLoginBean.Requset jointLoginRequest, InusrePayBean.Requset inusrePayRequest) {
        BaseResponseBean response = new BaseResponseBean();
        InusrePayBean.Requset insurePayRequest = new InusrePayBean.Requset();
        insurePayRequest.payNo = inusrePayRequest.payNo;
        insurePayRequest.payWay = insurePayRequest.payWay;
        if (insurePayRequest.payWay == "1") {//支付方式 1 银联 2 支付宝 3 微信 4现金  必填
            //TODO http 请求 支付银行卡服务
            String bankRes = doPayBank(jointLoginRequest);
            if (bankRes == null) {
                return json(BaseResponseBean.CODE_FAILURE, "银行卡服务调用失败", response);
            }
            JointLoginBean.BankResponse bankResponse = JsonKit.json2Bean(bankRes, JointLoginBean.BankResponse.class);
            if (bankResponse == null) {
                return json(BaseResponseBean.CODE_FAILURE, "银行卡服务调用失败", response);
            }
            if (bankResponse.code != 200) {
                return json(BaseResponseBean.CODE_FAILURE, "银行卡服务调用失败", response);
            }
            //TODO 获取支付银行卡
            //insurePayRequest.bankData = bankResponse.data;
        }
        try {
            //TODO 请求http
            String payRes = HttpClientKit.post(toPay, JsonKit.bean2Json(insurePayRequest));
            if (payRes == null) {
                return json(BaseResponseBean.CODE_FAILURE, "支付接口请求失败", response);
            }
            BaseResponseBean payResponse = JsonKit.json2Bean(payRes, BaseResponseBean.class);
            if (payResponse.code != 200) {
                return json(BaseResponseBean.CODE_FAILURE, "支付接口请求失败", response);
            }
            response.data = payResponse;
            return json(BaseResponseBean.CODE_SUCCESS, "接口请求成功", response);
        } catch (IOException e) {
            return json(BaseResponseBean.CODE_FAILURE, "支付接口请求失败", response);
        }
    }

    /**
     * 获取支付银行卡信息
     *
     * @param request
     * @return
     */
    private String doPayBank(JointLoginBean.Requset request) {
        BaseResponseBean response = new BaseResponseBean();
        try {
            String bankRes = HttpClientKit.post(toPayBank, JsonKit.bean2Json(request));
            if (bankRes == null) {
                return json(BaseResponseBean.CODE_FAILURE, "银行卡服务接口请求失败", response);
            }
            JointLoginBean.Response bankResponse = JsonKit.json2Bean(bankRes, JointLoginBean.Response.class);
            if (bankResponse.code != 200) {
                return json(BaseResponseBean.CODE_FAILURE, "银行卡服务接口请求失败", response);
            }
            response.data = bankResponse;
            return json(BaseResponseBean.CODE_SUCCESS, "接口请求成功", response);
        } catch (IOException e) {
            return json(BaseResponseBean.CODE_FAILURE, "银行卡服务接口请求失败", response);
        }
    }

    /**
     * 调用微信签约
     *
     * @param jointLoginRequest
     * @param wechatContractRequest
     * @return
     */
    private String doWechatContract(JointLoginBean.Requset jointLoginRequest, WechatContractBean.Requset wechatContractRequest) {
        BaseResponseBean response = new BaseResponseBean();
        //先判断前一天是否进行预投保
        StaffPerson staffPerson = new StaffPerson();
        staffPerson.name = jointLoginRequest.insured_name;
        staffPerson.papers_code = jointLoginRequest.insured_name;
        staffPerson.phone = jointLoginRequest.insured_phone;
        long cust_id = staffPersonDao.findStaffPersonId(staffPerson);
        long date = new Date().getTime();
        if (cust_id == 0) {
            //TODO 触发联合登录,同步操作 http 请求 账号服务
            String loginRes = doAccount(jointLoginRequest);
            if (loginRes == null) {
                return json(BaseResponseBean.CODE_FAILURE, "账号服务调用失败", response);
            }
            JointLoginBean.AccountResponse loginResponse = JsonKit.json2Bean(loginRes, JointLoginBean.AccountResponse.class);
            if (loginResponse == null) {
                return json(BaseResponseBean.CODE_FAILURE, "账号服务调用失败", response);
            }
            if (loginResponse.code != 200) {
                return json(BaseResponseBean.CODE_FAILURE, "账号服务调用失败", response);
            }
            staffPerson.cust_id = loginResponse.data.custId;
            staffPerson.account_uuid = loginResponse.data.accountUuid;
            staffPerson.login_token = loginResponse.data.loginToken;
            staffPerson.created_at = date;
            staffPerson.updated_at = date;
            long addRes = staffPersonDao.addStaffPerson(staffPerson);
            if (addRes != 0) {
                cust_id = loginResponse.data.custId;
            } else {
                return json(BaseResponseBean.CODE_FAILURE, "没有此用户，请重新联合登录", response);
            }
        }
        WarrantyRecord warrantyRecord = new WarrantyRecord();
        warrantyRecord.cust_id = cust_id;
        Calendar calendar = Calendar.getInstance();
        long day_start = calendar.getTimeInMillis();
        warrantyRecord.day_start = day_start;
        warrantyRecord.day_end = day_start + 24 * 60 * 60 * 1000;
        WarrantyRecord warrantyRecoedRes = warrantyRecordDao.findLastDayWarrantyRecord(warrantyRecord);
        if (warrantyRecoedRes == null || warrantyRecoedRes.warranty_uuid == null) {
            return json(BaseResponseBean.CODE_FAILURE, "您没有进行预投保，不能进行微信签约", response);
        }
        WechatContractBean.Requset wechatContractRequests = new WechatContractBean.Requset();
        wechatContractRequests.warrantyUuid = "";
        wechatContractRequests.warrantyCode = "";
        wechatContractRequests.payNo = "";
        wechatContractRequests.clientIp = "";
        wechatContractRequests.insuredName = "";
        wechatContractRequests.insuredCode = "";
        wechatContractRequests.insuredPhone = "";
        try {
            //TODO 请求http
            String wechatContractRes = HttpClientKit.post(toWechatContract, JsonKit.bean2Json(wechatContractRequests));
            if (wechatContractRes == null) {
                return json(BaseResponseBean.CODE_FAILURE, "微信签约接口请求失败", response);
            }
            WechatContractBean.Response wechatContractResponse = JsonKit.json2Bean(wechatContractRes, WechatContractBean.Response.class);
            if (wechatContractResponse.code != 200) {
                return json(BaseResponseBean.CODE_FAILURE, "微信签约接口请求失败", response);
            }
            response.data = wechatContractResponse;
            return json(BaseResponseBean.CODE_SUCCESS, "接口请求成功", response);
        } catch (IOException e) {
            return json(BaseResponseBean.CODE_FAILURE, "微信签约接口请求失败", response);
        }
    }

    /**
     * 添加投保记录
     * @param warrantyRecord
     * @return
     */
    private String doAddWarrantyRecord(WarrantyRecord warrantyRecord) {
        BaseResponseBean response = new BaseResponseBean();

        return json(BaseResponseBean.CODE_FAILURE, "接口请求失败", response);

    }

    /**
     * 更新投保记录(保单状态等)
     * @param warrantyRecord
     * @return
     */
    private String doUpdateWarrantyRecord(WarrantyRecord warrantyRecord) {
        BaseResponseBean response = new BaseResponseBean();

        return json(BaseResponseBean.CODE_FAILURE, "接口请求失败", response);

    }
}
