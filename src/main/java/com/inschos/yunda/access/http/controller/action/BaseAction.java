package com.inschos.yunda.access.http.controller.action;

import com.inschos.yunda.access.http.controller.bean.BaseResponseBean;
import com.inschos.yunda.access.http.controller.bean.IntersCommonUrlBean;
import com.inschos.yunda.access.http.controller.bean.JointLoginBean;
import com.inschos.yunda.access.http.controller.bean.PageBean;
import com.inschos.yunda.annotation.CheckParamsKit;
import com.inschos.yunda.assist.kit.JsonKit;
import com.inschos.yunda.assist.kit.*;
import com.inschos.yunda.data.dao.StaffPersonDao;
import com.inschos.yunda.model.Page;
import com.inschos.yunda.model.StaffPerson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.inschos.yunda.access.http.controller.bean.IntersCommonUrlBean.toHttpTest;

public class BaseAction {
    public <T> T requst2Bean(String body, Class<T> clazz) {
        T bean = JsonKit.json2Bean(body, clazz);
        if (bean == null) {
            try {
                bean = clazz.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return bean;
    }

    public String json(int code, String message, BaseResponseBean response) {
        if (response == null) {
            response = new BaseResponseBean();
        }
        response.code = code;
        CheckParamsKit.Entry<String, String> defaultEntry = CheckParamsKit.getDefaultEntry();
        defaultEntry.details = message;
        List<CheckParamsKit.Entry<String, String>> list = new ArrayList<>();
        list.add(defaultEntry);
        response.message = list;

        return JsonKit.bean2Json(response);
    }

    public String json(int code, List<CheckParamsKit.Entry<String, String>> message, BaseResponseBean response) {
        if (response == null) {
            response = new BaseResponseBean();
        }

        response.code = code;
        response.message = message;

        return JsonKit.bean2Json(response);
    }

    public String json(BaseResponseBean response) {
        return JsonKit.bean2Json(response);
    }

    public List<CheckParamsKit.Entry<String, String>> checkParams(Object obj) {
        List<CheckParamsKit.Entry<String, String>> list = new ArrayList<>();

        if (obj == null) {
            CheckParamsKit.Entry<String, String> anEntry = CheckParamsKit.getDefaultEntry();
            anEntry.details = "解析失败";
            list.add(anEntry);
            return list;
        }

        CheckParamsKit.checkToArray(obj, list);
        if (list.isEmpty()) {
            return null;
        } else {
            return list;
        }
    }

    protected Page setPage(String lastId, String num, String size) {
        Page page = new Page();

        if (StringKit.isInteger(size)) {
            if (StringKit.isInteger(lastId)) {
                page.lastId = Long.valueOf(lastId);
                page.offset = Integer.valueOf(size);
            } else if (StringKit.isInteger(num)) {
                int pageSize = Integer.valueOf(size);
                int pageStart = (Integer.valueOf(num) - 1) * pageSize;

                page.start = pageStart;
                page.offset = pageSize;
            }
        }
        return page;
    }

    protected PageBean setPageBean(long lastId, String page_size, long total, int listSize) {
        PageBean pageBean = new PageBean();
        pageBean.lastId = String.valueOf(lastId);
        pageBean.pageSize = StringKit.isInteger(page_size) ? page_size : "20";
        pageBean.total = String.valueOf(total);
        pageBean.listSize = String.valueOf(listSize);

        return pageBean;
    }

    protected PageBean setPageBean(String page_num, String page_size, long total, int listSize) {
        PageBean pageBean = new PageBean();

        long pageTotal = 0;

        if (StringKit.isInteger(page_size)) {
            int pageSize = Integer.valueOf(page_size);
            if (pageSize > 0) {
                pageTotal = total / pageSize;

                if (total % pageSize > 0) {
                    pageTotal += 1;
                }
            }
        }

        pageBean.pageNum = StringKit.isInteger(page_num) ? page_num : "1";
        pageBean.pageSize = StringKit.isInteger(page_size) ? page_size : "20";
        pageBean.pageTotal = String.valueOf(pageTotal);
        pageBean.total = String.valueOf(total);
        pageBean.listSize = String.valueOf(listSize);

        return pageBean;
    }

    /**
     * 获取cust_id,并存取用户信息
     *
     * @param request
     * @return
     */
    protected long findCustId(JointLoginBean.Requset request) {
        StaffPerson staffPerson = new StaffPerson();
        staffPerson.name = request.insured_name;
        staffPerson.papers_code = request.insured_name;
        staffPerson.phone = request.insured_phone;
        StaffPersonDao staffPersonDao = new StaffPersonDao();
        long cust_id = staffPersonDao.findStaffPersonId(staffPerson);
        long date = new Date().getTime();
        if (cust_id == 0) {
            //TODO 触发联合登录,同步操作 http 请求 账号服务
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
                IntersCommonUrlBean intersCommonUrlBean = new IntersCommonUrlBean();
                String accountRes = HttpClientKit.post(intersCommonUrlBean.toJointLogin, JsonKit.bean2Json(jointLoginRequest));
                if (accountRes == null) {
                    return 0;
                }
                JointLoginBean.AccountResponse accountResponse = JsonKit.json2Bean(accountRes, JointLoginBean.AccountResponse.class);
                if (accountResponse.code == 500) {
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
            } catch (IOException e) {
                return 0;
            }
        }
        return cust_id;
    }
}
