package com.inschos.yunda.access.rpc.service;

import com.inschos.yunda.access.rpc.bean.AgentJobBean;

import java.util.List;

/**
 * Created by IceAnt on 2018/5/21.
 */
public interface AgentJobService {
    AgentJobBean getAgentInfoByPersonIdManagerUuid(String managerUuid, long personId);

    List<AgentJobBean> getAgentsByChannels(AgentJobBean jobBean) ;

    public AgentJobBean getAgentById(long agentId);


}
