package com.inschos.yunda.access.http.controller.action;

import com.fasterxml.jackson.core.type.TypeReference;
import com.inschos.yunda.access.http.controller.bean.*;
import com.inschos.yunda.assist.kit.Base64Kit;
import com.inschos.yunda.assist.kit.HttpKit;
import com.inschos.yunda.assist.kit.JsonKit;
import com.inschos.yunda.assist.kit.TimeKit;
import com.inschos.yunda.data.dao.JointLoginDao;
import com.inschos.yunda.data.dao.StaffPersonDao;
import com.inschos.yunda.data.dao.WarrantyRecordDao;
import com.inschos.yunda.model.JointLogin;
import com.inschos.yunda.model.StaffPerson;
import com.inschos.yunda.model.WarrantyRecord;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
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

    @Autowired
    private CommonAction commonAction;

    /**
     * 联合登录
     * TODO 韵达触发联合登录，获取报文信息,请求账号服务(登录/注册)，获取account_id,,user_id,login_token
     *
     * @return json
     * @params actionBean
     * @access public
     */
    public String jointLogin(HttpServletRequest httpServletRequest) {
        JointLoginBean.Requset request = JsonKit.json2Bean(HttpKit.readRequestBody(httpServletRequest), JointLoginBean.Requset.class);
        BaseResponseBean response = new BaseResponseBean();
        if (request == null) {
            return json(BaseResponseBean.CODE_FAILURE, "请检查报文格式是否正确", response);
        }
        if (request.insured_name == null || request.insured_code == null || request.insured_phone == null) {
            return json(BaseResponseBean.CODE_FAILURE, "姓名,证件号,手机号不能为空", response);
        }
        //TODO 联合登录表插入数据,先判断今天有没有插入,再插入登录记录.每天只有一个记录
        long date = new Date().getTime();
        JointLogin jointLogin = new JointLogin();
        jointLogin.login_start = date;
        jointLogin.phone = request.insured_phone;
        jointLogin.created_at = date;
        jointLogin.updated_at = date;
        jointLogin.day_start = TimeKit.getDayStartTime();//获取当天开始时间戳(毫秒值)
        jointLogin.day_end = TimeKit.getDayEndTime();//获取当天结束时间戳(毫秒值)
        long repeatRes = jointLoginDao.findLoginRecord(jointLogin);
        if (repeatRes == 0) {
            long login_id = jointLoginDao.addLoginRecord(jointLogin);
        }
        //TODO 联合登录触发账号服务
        JointLoginBean.AccountResponse loginResponse = doAccount(request);
        response.data = loginResponse.data;
        //TODO 联合登录触发投保服务
        String insureRes = doInsuredPay(request);
        if (insureRes == null) {
            return json(BaseResponseBean.CODE_FAILURE, "投保失败", response);
        }
        InsureParamsBean.Response insureResponse = JsonKit.json2Bean(insureRes, InsureParamsBean.Response.class);
        if (insureResponse == null) {
            return json(BaseResponseBean.CODE_FAILURE, "投保失败", response);
        }
        if (insureResponse.code != 200) {
            return json(BaseResponseBean.CODE_FAILURE, "投保失败", response);
        }
        response.data = insureResponse.data;
        return json(BaseResponseBean.CODE_SUCCESS, "投保成功", response);
    }

    /**
     * 授权查询
     * TODO 银行卡授权查询和微信免密查询返回参数还没定,授权状态组合判断没做
     * TODO 授权查询同样需要触发账号服务(根据用户信息获取token)
     *
     * @return json
     * @params actionBean
     * @access public
     */
    public String authorizationQuery(HttpServletRequest httpServletRequest) {
        JointLoginBean.Requset request = JsonKit.json2Bean(HttpKit.readRequestBody(httpServletRequest), JointLoginBean.Requset.class);
        BaseResponseBean response = new BaseResponseBean();
        AuthorizeQueryBean.ResponseData authorizeQueryResponseData = new AuthorizeQueryBean.ResponseData();
        if (request == null) {
            return json(BaseResponseBean.CODE_FAILURE, "请检查报文格式是否正确", response);
        }
        if (request.insured_name == null || request.insured_code == null || request.insured_phone == null) {
            return json(BaseResponseBean.CODE_FAILURE, "姓名,证件号,手机号不能为空", response);
        }
        //TODO 联合登录触发账号服务
        JointLoginBean.AccountResponse accountResponse = doAccount(request);
        if (accountResponse == null) {
            return json(BaseResponseBean.CODE_FAILURE, "账号服务调用失败", response);
        }
        if (accountResponse.code != 200) {
            return json(BaseResponseBean.CODE_FAILURE, "账号服务调用失败", response);
        }
        //TODO 查询授权/签约详情
        CommonBean.findAuthorizeResponse authorizeResponse = doAuthorizeRes(request);
        if (authorizeResponse == null) {
            return json(BaseResponseBean.CODE_FAILURE, "授权/签约查询接口调用失败", response);
        }
        if (authorizeResponse.code != 200) {
            return json(BaseResponseBean.CODE_FAILURE, "授权/签约查询接口调用失败", response);
        }
        //TODO 判断银行卡授权和微信签约情况

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
     * TODO 预投保接口跟其他接口的返回格式不同,接口返回参数还没确定
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
            String insureRes = doInsuredPay(jointLoginRequest);
            if (insureRes == null) {
                return json(BaseResponseBean.CODE_FAILURE, "投保失败", response);
            }
            InsureParamsBean.Response insureResponse = JsonKit.json2Bean(insureRes, InsureParamsBean.Response.class);
            if (insureResponse == null) {
                return json(BaseResponseBean.CODE_FAILURE, "投保失败", response);
            }
            if (insureResponse.code != 200) {
                return json(BaseResponseBean.CODE_FAILURE, "投保失败", response);
            }
        }
        //TODO 预投保接口跟其他接口的返回格式不同
        return json(BaseResponseBean.CODE_SUCCESS, "投保成功", response);
    }

    /**
     * 保险推送接口
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
        String interName = "投保信息推送韵达";
        String result = commonAction.httpRequest(toCallBackYunda, JsonKit.bean2Json(callbackYundaRequest), interName);
        return result;
    }

    /**
     * 获取授权/签约详情
     *
     * @return
     */
    private CommonBean.findAuthorizeResponse doAuthorizeRes(JointLoginBean.Requset request) {
        BaseResponseBean response = new BaseResponseBean();
        JointLoginBean.Requset jointLoginRequest = new JointLoginBean.Requset();
        jointLoginRequest.insured_name = request.insured_name;
        jointLoginRequest.insured_code = request.insured_code;
        jointLoginRequest.insured_phone = request.insured_phone;
        String interName = "授权/签约查询";
        String result = commonAction.httpRequest(toAuthorizeQuery, JsonKit.bean2Json(jointLoginRequest), interName);
        CommonBean.findAuthorizeResponse authorizeResponse = JsonKit.json2Bean(result, CommonBean.findAuthorizeResponse.class);
        return authorizeResponse;
    }

    /**
     * 调用账号服务
     *
     * @param request
     * @return
     */
    private JointLoginBean.AccountResponse doAccount(JointLoginBean.Requset request) {
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
        String interName = "账号服务";
        String result = commonAction.httpRequest(toJointLogin, JsonKit.bean2Json(jointLoginRequest), interName);
        JointLoginBean.AccountResponse accountResponse = JsonKit.json2Bean(result, JointLoginBean.AccountResponse.class);
        return accountResponse;
    }

    /**
     * 投保服务
     * todo 先判断当天有没有投过保，没有的话进行投保操作(先走英大投保，再进行泰康投保 )
     * todo 已经投过,返回投保结果和保单状态
     *
     * @return
     */
    private String doInsuredPay(JointLoginBean.Requset request) {
        BaseResponseBean response = new BaseResponseBean();
        //TODO 判断用户是否已经投保
        WarrantyRecord warrantyRecord = new WarrantyRecord();
        warrantyRecord.cust_id = doCustId(request);
        long insuredCount = warrantyRecordDao.findInsureWarrantyRes(warrantyRecord);
        if (insuredCount > 0) {
            //TODO 获取保单状态
            warrantyRecord.day_start = TimeKit.currentTimeMillis();//获取当前时间戳(毫秒值)
            warrantyRecord.day_end = TimeKit.getDayEndTime();//获取当天结束时间戳(毫秒值)
            WarrantyRecord insureResult = warrantyRecordDao.findInsureResult(warrantyRecord);
            if (insureResult == null) {
                return json(BaseResponseBean.CODE_SUCCESS, "获取保单状态失败", response);
            } else {
                InsureResultBean insureResultBean = new InsureResultBean();
                insureResultBean.id = insureResult.id;
                insureResultBean.custId = insureResult.cust_id;
                insureResultBean.warrantyUuid = insureResult.warranty_uuid;
                insureResultBean.warrantyStatus = insureResult.warranty_status;
                insureResultBean.warrantyStatusText = insureResult.warranty_status_text;
                insureResultBean.createdAt = insureResult.created_at;
                insureResultBean.updatedAt = insureResult.updated_at;
                response.data = insureResultBean;
                return json(BaseResponseBean.CODE_SUCCESS, "获取保单状态成功", response);
            }
        }
        //TODO 调用投保接口
        InsureParamsBean.Response insureResponse = doInsured(request);
        InsureParamsBean.ResponseData responseData = new InsureParamsBean.ResponseData();
        InusrePayBean.Requset inusrePayRequest = new InusrePayBean.Requset();
        //TODO 保单记录表添加/更新
        long date = new Date().getTime();
        long custId = doCustId(request);
        if (custId == 0) {
            return json(BaseResponseBean.CODE_FAILURE, "获取用户信息失败", response);
        }
        warrantyRecord.cust_id = custId;
        warrantyRecord.warranty_uuid = responseData.warrantyUuid;
        warrantyRecord.warranty_status = responseData.status;
        warrantyRecord.warranty_status_text = responseData.statusTxt;
        warrantyRecord.pay_no = responseData.payNo;
        warrantyRecord.pay_type = responseData.payType;
        warrantyRecord.pay_url = responseData.payUrl;
        warrantyRecord.created_at = date;
        warrantyRecord.updated_at = date;
        String addWarrantyRecordRes = doAddWarrantyRecord(warrantyRecord);
        if (addWarrantyRecordRes == null) {
            return json(BaseResponseBean.CODE_FAILURE, "添加投保记录失败", response);
        }
        //TODO 支付参数
        inusrePayRequest.payNo = insureResponse.data.payNo;
        inusrePayRequest.payWay = insureResponse.data.payType;
        //TODO 投保成功,调用支付
        InusrePayBean.Response payRes = doInsurePay(request, inusrePayRequest);
        if (payRes == null) {
            return json(BaseResponseBean.CODE_FAILURE, "支付接口调用失败", response);
        }
        if (payRes.code != 200) {
            return json(BaseResponseBean.CODE_FAILURE, "支付接口调用失败", response);
        }
        warrantyRecord.warranty_status = payRes.data.status;
        warrantyRecord.warranty_status_text = payRes.data.statusTxt;
        warrantyRecord.updated_at = new Date().getTime();
        String updateWarrantyRecordRes = doUpdateWarrantyRecord(warrantyRecord);
        if (updateWarrantyRecordRes == null) {
            return json(BaseResponseBean.CODE_FAILURE, "更新投保记录失败", response);
        }
        String statusText = payRes.data.statusTxt;//支付返回文案
        return json(BaseResponseBean.CODE_SUCCESS, statusText, response);
    }

    /**
     * 投保操作
     *
     * @return
     */
    private InsureParamsBean.Response doInsured(JointLoginBean.Requset request) {
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
        String interName = "交易服务-投保接口";
        String result = commonAction.httpRequest(toInsured, JsonKit.bean2Json(insuredRequest), interName);
        InsureParamsBean.Response insureResponse = JsonKit.json2Bean(result, InsureParamsBean.Response.class);
        return insureResponse;
    }

    /**
     * 支付操作
     *
     * @param inusrePayRequest
     * @return
     */
    private InusrePayBean.Response doInsurePay(JointLoginBean.Requset jointLoginRequest, InusrePayBean.Requset inusrePayRequest) {
        InusrePayBean.Response response = new InusrePayBean.Response();
        InusrePayBean.Requset insurePayRequest = new InusrePayBean.Requset();
        insurePayRequest.payNo = inusrePayRequest.payNo;
        insurePayRequest.payWay = insurePayRequest.payWay;
        if (insurePayRequest.payWay == "1") {//支付方式 1 银联 2 支付宝 3 微信 4现金  必填
            //TODO http 请求 支付银行卡服务
            InusrePayBean.payBankResponse bankResponse = doPayBank(jointLoginRequest);
            //TODO 获取支付银行卡
            insurePayRequest.bankData = bankResponse.data;
        }
        String interName = "交易服务-支付接口";
        String result = commonAction.httpRequest(toPay, JsonKit.bean2Json(insurePayRequest), interName);
        InusrePayBean.Response insureResponse = JsonKit.json2Bean(result, InusrePayBean.Response.class);
        return insureResponse;
    }

    /**
     * 获取支付银行卡信息
     *
     * @param request
     * @return
     */
    private InusrePayBean.payBankResponse doPayBank(JointLoginBean.Requset request) {
        String interName = "交易服务-银行卡服务";
        String result = commonAction.httpRequest(toPayBank, JsonKit.bean2Json(request), interName);
        InusrePayBean.payBankResponse bankResponse = JsonKit.json2Bean(result, InusrePayBean.payBankResponse.class);
        return bankResponse;
    }

    /**
     * 添加投保记录
     *
     * @param warrantyRecord
     * @return
     */
    private String doAddWarrantyRecord(WarrantyRecord warrantyRecord) {
        BaseResponseBean response = new BaseResponseBean();
        long warrantyId = warrantyRecordDao.addWarrantyRecord(warrantyRecord);
        if (warrantyId == 0) {
            return json(BaseResponseBean.CODE_FAILURE, "添加投保记录失败", response);
        }
        response.data = warrantyId;
        return json(BaseResponseBean.CODE_SUCCESS, "添加投保记录成功", response);
    }

    /**
     * 更新投保记录(保单状态等)
     *
     * @param warrantyRecord
     * @return
     * @params id
     * @params warranty_status
     * @params warranty_status_text
     * @params updated_at
     */
    private String doUpdateWarrantyRecord(WarrantyRecord warrantyRecord) {
        BaseResponseBean response = new BaseResponseBean();
        long updateRes = warrantyRecordDao.updateWarrantyRecord(warrantyRecord);
        if (updateRes == 0) {
            return json(BaseResponseBean.CODE_FAILURE, "更新投保记录失败", response);
        }
        return json(BaseResponseBean.CODE_SUCCESS, "更新投保记录成功", response);
    }

    /**
     * 获取cust_id
     *
     * @param jointLoginRequest
     * @return
     */
    private long doCustId(JointLoginBean.Requset jointLoginRequest) {
        StaffPerson staffPerson = new StaffPerson();
        staffPerson.name = jointLoginRequest.insured_name;
        staffPerson.papers_code = jointLoginRequest.insured_name;
        staffPerson.phone = jointLoginRequest.insured_phone;
        long cust_id = staffPersonDao.findStaffPersonId(staffPerson);
        long date = new Date().getTime();
        if (cust_id == 0) {
            //TODO 触发联合登录,同步操作 http 请求 账号服务
            JointLoginBean.AccountResponse accountResponse = doAccount(jointLoginRequest);
            if (accountResponse == null) {
                return 0;
            }
            if (accountResponse.code != 200) {
                return 0;
            }
            staffPerson.cust_id = accountResponse.data.custId;
            staffPerson.account_uuid = accountResponse.data.accountUuid;
            staffPerson.login_token = accountResponse.data.loginToken;
            staffPerson.created_at = date;
            staffPerson.updated_at = date;
            long addRes = staffPersonDao.addStaffPerson(staffPerson);
            if (addRes != 0) {
                cust_id = accountResponse.data.custId;
            } else {
                return 0;
            }
        }
        return cust_id;
    }
}
