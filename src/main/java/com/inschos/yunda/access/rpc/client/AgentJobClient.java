package com.inschos.yunda.access.rpc.client;

import com.inschos.yunda.access.rpc.bean.AgentJobBean;
import com.inschos.yunda.access.rpc.service.AgentJobService;
import com.inschos.yunda.assist.kit.L;
import hprose.client.HproseHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by IceAnt on 2018/5/21.
 */
@Component
public class AgentJobClient {

    @Value("${rpc.remote.customer.host}")
    private String host;

    private final String uri = "/rpc/agent";


    private AgentJobService getService(){
        return new HproseHttpClient(host + uri).useService(AgentJobService.class);
    }

    public AgentJobBean getAgent(String managerUuid, long personId){
        try {
            AgentJobService service = getService();
            return service!=null?service.getAgentInfoByPersonIdManagerUuid(managerUuid,personId):null;

        }catch (Exception e){
            L.log.error("remote fail {}",e.getMessage(),e);
            return null;
        }
    }

    public List<AgentJobBean> getAgentsByChannels(AgentJobBean jobBean) {
        try {
            AgentJobService service = getService();
            return service!=null?service.getAgentsByChannels(jobBean):null;

        }catch (Exception e){
            L.log.error("remote fail {}",e.getMessage(),e);
            return null;
        }
    }
    public AgentJobBean getAgentById(long agentId){
        try {
            AgentJobService service = getService();
            return service!=null?service.getAgentById(agentId):null;
        }catch (Exception e){
            L.log.error("remote fail {}",e.getMessage(),e);
            return null;
        }
    }

}
