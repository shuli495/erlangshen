package com.website.dao;

import com.website.model.vo.ClientMailVO;
import org.springframework.stereotype.Repository;
import com.fastjavaframework.base.BaseDao;

@Repository
public class ClientMailDao extends BaseDao<ClientMailVO> {

    /**
     * 根据clidentId、type查询
     * @param clientId
     * @param type
     * @return
     */
    public ClientMailVO find(String clientId, String type) {
        ClientMailVO clientMailVO = new ClientMailVO();
        clientMailVO.setClientId(clientId);
        clientMailVO.setType(type);
        return this.sql().selectOne("find", clientMailVO);
    }

    /**
     * 根据clientId删除
     * @param clientId
     */
    public void deleteByClientId(String clientId) {
        this.sql().delete("deleteByClientId", clientId);
    }
}