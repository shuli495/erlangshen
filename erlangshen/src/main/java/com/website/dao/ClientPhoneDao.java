package com.website.dao;

import com.website.model.vo.ClientPhoneVO;
import org.springframework.stereotype.Repository;
import com.fastjavaframework.base.BaseDao;

/**
 * @author https://github.com/shuli495/erlangshen
 */
@Repository
public class ClientPhoneDao extends BaseDao<ClientPhoneVO> {

    /**
     * 根据clidentId、type查询
     * @param clientId
     * @param type
     * @return
     */
    public ClientPhoneVO find(String clientId, String type) {
        ClientPhoneVO clientPhoneVO = new ClientPhoneVO();
        clientPhoneVO.setClientId(clientId);
        clientPhoneVO.setType(type);
        return this.sql().selectOne("find", clientPhoneVO);
    }

    /**
     * 根据clientId删除
     * @param clientId
     */
    public void deleteByClientId(String clientId) {
        this.sql().delete("deleteByClientId", clientId);
    }

}