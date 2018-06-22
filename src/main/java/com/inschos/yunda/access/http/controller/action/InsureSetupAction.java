package com.inschos.yunda.access.http.controller.action;

import com.inschos.yunda.access.http.controller.bean.*;
import com.inschos.yunda.assist.kit.HttpKit;
import com.inschos.yunda.assist.kit.JsonKit;
import com.inschos.yunda.data.dao.*;
import com.inschos.yunda.extend.inters.IntersCommonParams;
import com.inschos.yunda.extend.inters.IntersHttpRequest;
import com.inschos.yunda.model.IntersResponse;
import com.inschos.yunda.model.JointLogin;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.inschos.yunda.extend.inters.IntersCommonUrls.*;

@Component
public class InsureSetupAction extends BaseAction {

    private static final Logger logger = Logger.getLogger(InsureSetupAction.class);

    @Autowired
    private JointLoginDao jointLoginDao;

    /**
     * 获取投保设置详情
     *
     * @param actionBean
     * @return
     */
    public String findInsureAutoStatus(ActionBean actionBean) {
        InsureSetupBean request = JsonKit.json2Bean(actionBean.body, InsureSetupBean.class);
        BaseResponseBean response = new BaseResponseBean();
        //判空
        if (request == null) {
            return json(BaseResponseBean.CODE_FAILURE, "参数解析失败", response);
        }
        return json(BaseResponseBean.CODE_SUCCESS, "业务完善中...", response);
    }

    /**
     * 更改投保设置
     *
     * @param actionBean
     * @return
     */
    public String updateInsureAutoStatus(ActionBean actionBean) {
        InsureSetupBean request = JsonKit.json2Bean(actionBean.body, InsureSetupBean.class);
        BaseResponseBean response = new BaseResponseBean();
        //判空
        if (request == null) {
            return json(BaseResponseBean.CODE_FAILURE, "参数解析失败", response);
        }
        return json(BaseResponseBean.CODE_SUCCESS, "业务完善中...", response);
    }

}
