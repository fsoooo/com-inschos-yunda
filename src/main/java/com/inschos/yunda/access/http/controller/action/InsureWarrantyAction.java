package com.inschos.yunda.access.http.controller.action;


import com.inschos.yunda.access.http.controller.bean.*;
import com.inschos.yunda.assist.kit.JsonKit;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class InsureWarrantyAction extends BaseAction {

    private static final Logger logger = Logger.getLogger(InsureSetupAction.class);

    /**
     * 获取保单列表
     *
     * @param actionBean
     * @return
     */
    public String findInsureWarrantyList(ActionBean actionBean) {
        InsureSetupBean request = JsonKit.json2Bean(actionBean.body, InsureSetupBean.class);
        BaseResponseBean response = new BaseResponseBean();
        //判空
        if (request == null) {
            return json(BaseResponseBean.CODE_FAILURE, "参数解析失败", response);
        }
        return json(BaseResponseBean.CODE_SUCCESS, "业务完善中...", response);
    }

    /**
     * 获取保单详情
     *
     * @param actionBean
     * @return
     */
    public String findInsureWarrantyInfo(ActionBean actionBean) {
        InsureSetupBean request = JsonKit.json2Bean(actionBean.body, InsureSetupBean.class);
        BaseResponseBean response = new BaseResponseBean();
        //判空
        if (request == null) {
            return json(BaseResponseBean.CODE_FAILURE, "参数解析失败", response);
        }
        return json(BaseResponseBean.CODE_SUCCESS, "业务完善中...", response);
    }

    /**
     * 获取保单状态
     *
     * @param actionBean
     * @return
     */
    public String findInsureWarrantyStatus(ActionBean actionBean) {
        InsureSetupBean request = JsonKit.json2Bean(actionBean.body, InsureSetupBean.class);
        BaseResponseBean response = new BaseResponseBean();
        //判空
        if (request == null) {
            return json(BaseResponseBean.CODE_FAILURE, "参数解析失败", response);
        }
        return json(BaseResponseBean.CODE_SUCCESS, "业务完善中...", response);
    }


}
