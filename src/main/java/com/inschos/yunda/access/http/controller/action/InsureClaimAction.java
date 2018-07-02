package com.inschos.yunda.access.http.controller.action;


import com.inschos.yunda.access.http.controller.bean.*;
import com.inschos.yunda.assist.kit.JsonKit;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class InsureClaimAction extends BaseAction {

    private static final Logger logger = Logger.getLogger(InsureClaimAction.class);

    /**
     * 获取理赔进度列表
     *
     * @param actionBean
     * @return
     */
    public String findClaimProgressList(ActionBean actionBean) {
        InsureClaimBean.claimProgressListRequest request = JsonKit.json2Bean(actionBean.body, InsureClaimBean.claimProgressListRequest.class);
        BaseResponseBean response = new BaseResponseBean();
        //判空
        if (request == null) {
            return json(BaseResponseBean.CODE_FAILURE, "参数解析失败", response);
        }

        return json(BaseResponseBean.CODE_SUCCESS, "业务完善中...", response);
    }

    /**
     * 获取理赔进度详情
     *
     * @param actionBean
     * @return
     */
    public String findClaimProgressInfo(ActionBean actionBean) {
        InsureSetupBean request = JsonKit.json2Bean(actionBean.body, InsureSetupBean.class);
        BaseResponseBean response = new BaseResponseBean();
        //判空
        if (request == null) {
            return json(BaseResponseBean.CODE_FAILURE, "参数解析失败", response);
        }
        return json(BaseResponseBean.CODE_SUCCESS, "业务完善中...", response);
    }

}
