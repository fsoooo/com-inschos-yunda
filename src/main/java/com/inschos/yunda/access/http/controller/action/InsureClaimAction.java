package com.inschos.yunda.access.http.controller.action;

import com.inschos.yunda.access.http.controller.bean.ActionBean;
import com.inschos.yunda.access.http.controller.bean.BaseResponseBean;
import com.inschos.yunda.access.http.controller.bean.InsureSetupBean;
import com.inschos.yunda.assist.kit.JsonKit;
import com.inschos.yunda.data.dao.JointLoginDao;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InsureClaimAction extends BaseAction {

    private static final Logger logger = Logger.getLogger(InsureClaimAction.class);

    /**
     * 获取理赔列表
     *
     * @param actionBean
     * @return
     */
    public String findInsureClaimList(ActionBean actionBean) {
        InsureSetupBean request = JsonKit.json2Bean(actionBean.body, InsureSetupBean.class);
        BaseResponseBean response = new BaseResponseBean();
        //判空
        if (request == null) {
            return json(BaseResponseBean.CODE_FAILURE, "参数解析失败", response);
        }
        return json(BaseResponseBean.CODE_SUCCESS, "业务完善中...", response);
    }

    /**
     * 获取理赔详情
     *
     * @param actionBean
     * @return
     */
    public String findInsureClaimInfo(ActionBean actionBean) {
        InsureSetupBean request = JsonKit.json2Bean(actionBean.body, InsureSetupBean.class);
        BaseResponseBean response = new BaseResponseBean();
        //判空
        if (request == null) {
            return json(BaseResponseBean.CODE_FAILURE, "参数解析失败", response);
        }
        return json(BaseResponseBean.CODE_SUCCESS, "业务完善中...", response);
    }

    /**
     * 获取理赔状态
     *
     * @param actionBean
     * @return
     */
    public String findInsureClaimStatus(ActionBean actionBean) {
        InsureSetupBean request = JsonKit.json2Bean(actionBean.body, InsureSetupBean.class);
        BaseResponseBean response = new BaseResponseBean();
        //判空
        if (request == null) {
            return json(BaseResponseBean.CODE_FAILURE, "参数解析失败", response);
        }
        return json(BaseResponseBean.CODE_SUCCESS, "业务完善中...", response);
    }
}
