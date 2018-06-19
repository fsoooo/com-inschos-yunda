package com.inschos.yunda.access.http.controller.action;


import com.inschos.yunda.access.http.controller.bean.*;
import com.inschos.yunda.access.rpc.bean.AccountBean;
import com.inschos.yunda.access.rpc.bean.AgentJobBean;
import com.inschos.yunda.access.rpc.client.AccountClient;
import com.inschos.yunda.access.rpc.client.AgentJobClient;
import com.inschos.yunda.access.rpc.client.ChannelClient;
import com.inschos.yunda.assist.kit.ListKit;
import com.inschos.yunda.assist.kit.TimeKit;
import com.inschos.yunda.data.dao.MsgInboxDAO;
import com.inschos.yunda.data.dao.MsgIndexDAO;
import com.inschos.yunda.assist.kit.JsonKit;
import com.inschos.yunda.model.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class MsgIndexAction extends BaseAction {
    private static final Logger logger = Logger.getLogger(MsgIndexAction.class);
    @Autowired
    private MsgIndexDAO msgIndexDAO;
    @Autowired
    private MsgInboxDAO msgInboxDAO;

    @Autowired
    private ChannelClient channelClient;
    @Autowired
    private AgentJobClient agentJobClient;
    @Autowired
    private AccountClient accountClient;

    /**
     * 发送消息
     *
     * @paramss title|标题
     * @paramss content|内容
     * @paramss attachment|附件:上传附件的URL,可为空
     * @paramss type|消息                            类型:系统通知1/保单助手2/理赔进度3/最新任务4/客户消息5/活动消息6/顾问消息7/
     * @paramss fromId|发件人ID
     * @paramss fromType|发件人类型:用户类型:个人用户           1/企业用户 2/代理人 3/业管用户 4
     * @paramss toId|收件人id
     * @paramss toType|收件人类型:用户类型:个人用户             1/企业用户 2/代理人 3/业管用户 4
     * @paramss status|读取状态:标识消息                   是否已被读取,未读0/已读1.避免重复向收件箱表插入数据,默认为0
     * @paramss sendTime|发送时间:默认为空。需要延时发送的，发送时间不为空
     * @paramss parentId|消息父级id
     * @return json
     * @access public
     * <p>
     * TODO 消息 要素判断-05.14
     * TODO 群发消息 判断-05.15 业管可以发送所有类型的消息，代理人可以给自己的客户群发消息，企业用户可以给自己的员工群发消息，个人用户只能发送私信
     * TODO 延时发送判断-05.15  如果要延时发送消息 ，定时触发机制？？？
     * TODO 定时任务,用来处理定时发送的消息。Spring 自带的定时任务执行@Scheduled注解，可以定时的、周期性的执行一些任务。
     * TODO 上传文件-邮件附件-05.15 前端请求->请求文件服务->上传文件,返回key;前端消息要素(key)->发送消息接口->发送消息
     * TODO 判断是否重复插入？
     * TODO 附件格式，只存储，不操作。(端上获取key,传参,存储)
     * TODO 个人用户可以跟代理人（顾问）联系。待确定用户之间的联系方式
     */
    public String addMessage(ActionBean actionBean){
        MsgIndexBean request = JsonKit.json2Bean(actionBean.body, MsgIndexBean.class);
        BaseResponse response = new BaseResponse();
        //判空
        if (request == null) {
            return json(BaseResponse.CODE_FAILURE, "params is empty", response);
        }
        if (request.title.isEmpty() || request.content.isEmpty()) {
            return json(BaseResponse.CODE_FAILURE, "title or content is empty", response);
        }
        if(request.type == 0){
            return json(BaseResponse.CODE_FAILURE, "type is empty", response);
        }
        if (request.toUser == null || request.toUser.size() == 0) {
            return json(BaseResponse.CODE_FAILURE, "to_user is empty", response);
        }
        //消息配置
        MsgStatus msgStatus = new MsgStatus();
        //TODO 权限判断 个人1/企业2/代理人3/业管4
        //只有个人用户不能发送多条和系统消息
        if (request.toUser.size() > 1 && request.fromType == msgStatus.USER_PERSON) {
            return json(BaseResponse.CODE_FAILURE, "no permission", response);
        }
        long date = new Date().getTime();
        if(actionBean.managerUuid==null){
            actionBean.managerUuid = "-1";
        }
        MsgSys msgSys = new MsgSys();
        msgSys.manager_uuid = actionBean.managerUuid;
        msgSys.account_uuid = actionBean.accountUuid;
        msgSys.business_id = request.businessId;
        msgSys.title = request.title;
        msgSys.content = request.content;
        msgSys.type = request.type;
        if (request.attachment == null) {
            request.attachment = "";
        }
        msgSys.attachment = request.attachment;
        msgSys.send_time = request.sendTime;
        msgSys.from_id =  Integer.parseInt(actionBean.managerUuid);
        msgSys.from_type = actionBean.userType;
        msgSys.created_at = date;
        msgSys.updated_at = date;
        int send_result = msgIndexDAO.addMessage(msgSys);
        AddMsgRecord addMsgRecord = new AddMsgRecord();
        if(send_result>0){
            addMsgRecord.toUser = request.toUser;
            addMsgRecord.messageId = msgSys.id;
            String add_record =  addMsgRecord(addMsgRecord,actionBean.sysId,actionBean.managerUuid);
            if(add_record!=null){
                return json(BaseResponse.CODE_SUCCESS, "操作成功", response);
            }else{
                return json(BaseResponse.CODE_FAILURE, "操作失败", response);
            }
        }else{
            return json(BaseResponse.CODE_FAILURE, "操作失败", response);
        }
    }

    /**
     * 处理没有收件人的发送操作
     * @param addMsgRecord
     * @return
     */
    public String addMsgRecord(AddMsgRecord addMsgRecord,int sysId,String managerUuid){
        BaseResponse response = new BaseResponse();
        long date = new Date().getTime();
        List<Long> personIds = new ArrayList<>();
        for (AddMsgToBean addMsgToBean : addMsgRecord.toUser) {
            MsgRecord msgRecord = new MsgRecord();
            if(addMsgToBean.toId==0||addMsgToBean.toType==0){//没有代理人，只有渠道id

                msgRecord.msg_id = addMsgRecord.messageId;
                msgRecord.rec_id = addMsgToBean.channelId;
                msgRecord.type = 5;
                msgRecord.state = 1;
                msgRecord.status = 1;
                msgRecord.created_at = date;
                msgRecord.updated_at = date;
                RepeatCount msgRecordRepeat = msgIndexDAO.findAddMsgRecordRepeat(msgRecord);//发件记录表
                if(msgRecordRepeat.count==0){
                    int addMsgRec = msgIndexDAO.addMessageRecord(msgRecord);//发件记录表
                }

                List<String> childrenId = channelClient.getChildrenId(String.valueOf(addMsgToBean.channelId), true);
                AgentJobBean searchAgents = new AgentJobBean();
                searchAgents.channelIdList = childrenId;
                searchAgents.manager_uuid = managerUuid;
                searchAgents.search_cur_time = TimeKit.curTimeMillis2Str();
                List<AgentJobBean> agents = agentJobClient.getAgentsByChannels(searchAgents);
                if(agents!=null){
                    personIds.addAll(ListKit.toColumnList(agents,v->v.person_id));
                }
            }else if(addMsgToBean.toType!=0){
                msgRecord.msg_id = addMsgRecord.messageId;
                msgRecord.rec_id = addMsgToBean.channelId;
                msgRecord.type = 5;
                msgRecord.state = 1;
                msgRecord.status = 1;
                msgRecord.created_at = date;
                msgRecord.updated_at = date;
                RepeatCount repeatCounts = msgIndexDAO.findAddMsgRecordRepeat(msgRecord);//发件记录表
                if(repeatCounts.count==0){
                    int addMsgRec = msgIndexDAO.addMessageRecord(msgRecord);//发件记录表
                }
                msgRecord.msg_id = addMsgRecord.messageId;
                msgRecord.rec_id = addMsgToBean.toId;
                msgRecord.type = addMsgToBean.toType;
                msgRecord.state = 1;
                msgRecord.status = 1;
                msgRecord.created_at = date;
                msgRecord.updated_at = date;
                RepeatCount repeatCount = msgIndexDAO.findAddMsgRecordRepeat(msgRecord);//发件记录表
                if(repeatCount.count==0){
                    int addMsgRec = msgIndexDAO.addMessageRecord(msgRecord);//发件记录表
                }
                AgentJobBean agentJobBean = agentJobClient.getAgentById(addMsgToBean.toId);
                if(agentJobBean!=null){
                    personIds.add(agentJobBean.person_id);
                }
            }
        }
        List<Long> uniquePList = ListKit.toUnique(personIds);
        List<String> accountUuids = new ArrayList<>();
        for (Long personId : uniquePList) {
            AccountBean accountBean = accountClient.findByUser(sysId, AccountBean.USER_TYPE_AGENT, String.valueOf(personId));
            if(accountBean!=null){
                accountUuids.add(accountBean.accountUuid);
            }
        }
        for (String accountUuid : accountUuids) {
            MsgToRecord msgToRecord = new MsgToRecord();
            msgToRecord.account_uuid = accountUuid;
            msgToRecord.manager_uuid = managerUuid;
            msgToRecord.msg_id = addMsgRecord.messageId;
            msgToRecord.status = 1;
            msgToRecord.state = 1;
            msgToRecord.created_at = date;
            msgToRecord.updated_at = date;
            RepeatCount repeatCountToRecord = msgIndexDAO.findMessageToRecordRepeat(msgToRecord);//用户记录表
            if(repeatCountToRecord.count==0){
                int addToRec = msgIndexDAO.addMessageToRecord(msgToRecord);//用户记录表
            }
        }
        return json(BaseResponse.CODE_FAILURE, "操作失败", response);
    }

    /**
     * 操作消息 （收件箱 读取和删除）
     *
     * @paramss messageId   消息 id
     * @paramss operateId   操作代码:默认为1（删除/已读），2（还原/未读）
     * @paramss operateType 操作类型:read 更改读取状态，del 更改删除状态
     * @return json
     * @access public
     */
    public String updateMsgRec(ActionBean actionBean) {
        MsgInboxBean.MsgUpdateRequest request = JsonKit.json2Bean(actionBean.body, MsgInboxBean.MsgUpdateRequest.class);
        BaseResponse response = new BaseResponse();
        //判空
        if (request == null) {
            return json(BaseResponse.CODE_FAILURE, "params is empty", response);
        }
        if(request.operateAll==0){//批量操作
            if (request.messageIds == null || request.messageIds.size() == 0) {
                return json(BaseResponse.CODE_FAILURE, "messageIds is empty", response);
            }
            List<MsgUpdateResBean> msgUpdateResList = new ArrayList<>();
            MsgUpdateResBean msgUpdateRes = new MsgUpdateResBean();
            for (MsgUpdateBean messageId : request.messageIds) {
                MsgUpdate msgUpdate = new MsgUpdate();
                msgUpdate.msg_id = messageId.messageId;
                msgUpdate.operate_id = request.operateId;
                msgUpdate.type = request.messageType;
                msgUpdate.manager_uuid = actionBean.managerUuid;
                msgUpdate.account_uuid = actionBean.accountUuid;
                switch (request.operateType){
                    case "read":
                        msgUpdate.operate_type = "sys_status";
                        int updateStatusRes = msgInboxDAO.updateMsgRecStatus(msgUpdate);
                        if(updateStatusRes==1){
                            msgUpdateRes.updateRes = "更新成功";
                        }else{
                            msgUpdateRes.updateRes = "更新失败";
                        }
                        msgUpdateRes.messageId = messageId.messageId;
                        msgUpdateResList.add(msgUpdateRes);
                        break;
                    case "del":
                        msgUpdate.operate_type = "state";
                        int updateStateRes = msgInboxDAO.updateMsgRecState(msgUpdate);
                        if(updateStateRes==1){
                            msgUpdateRes.updateRes = "更新成功";
                        }else{
                            msgUpdateRes.updateRes = "更新失败";
                        }
                        msgUpdateRes.messageId = messageId.messageId;
                        msgUpdateResList.add(msgUpdateRes);
                        break;
                }
            }
            response.data = msgUpdateResList;
        }else if(request.operateAll==1){//处理全部
            MsgUpdate msgUpdate = new MsgUpdate();
            msgUpdate.operate_id = request.operateId;
            msgUpdate.manager_uuid = actionBean.managerUuid;
            msgUpdate.account_uuid = actionBean.accountUuid;
            msgUpdate.type = request.messageType;
            switch (request.operateType){
                case "read":
                    msgUpdate.operate_type = "sys_status";
                    int updateStatusRes = msgInboxDAO.updateAllMsgRecStatus(msgUpdate);
                    response.data = updateStatusRes;
                    break;
                case "del":
                    msgUpdate.operate_type = "state";
                    int updateStateRes = msgInboxDAO.updateAllMsgRecState(msgUpdate);
                    response.data = updateStateRes;
                    break;
            }
        }
        if(response.data!=null&&response.data!="0"){
            return json(BaseResponse.CODE_SUCCESS, "操作成功", response);
        }else{
            return json(BaseResponse.CODE_FAILURE, "操作失败", response);
        }
    }

}
