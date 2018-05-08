package com.website.service;

import com.fastjavaframework.exception.ThrowPrompt;
import com.fastjavaframework.util.SecretUtil;
import com.fastjavaframework.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fastjavaframework.base.BaseService;
import com.website.dao.KeyDao;
import com.website.model.vo.KeyVO;

import java.util.Date;

@Service
public class KeyService extends BaseService<KeyDao,KeyVO> {

    @Autowired
    private ClientService clientService;

    /**
     * 生成key
     * @param clientId
     */
    public void create(String tokenUserId, String clientId) {
        if(!clientService.isMyClient(tokenUserId, clientId)) {
            throw new ThrowPrompt("无权操作次应用！", "062001");
        }

        KeyVO keyVO = new KeyVO();
        keyVO.setStatus(1);
        keyVO.setClientId(clientId);
        keyVO.setCreatedTime(new Date());

        String accessKey = UUID.uuid64();
        String secretKey = SecretUtil.base64Encrypt(clientId + "sk" + UUID.timestamp()).replaceAll("=","");
        keyVO.setAccess(accessKey);
        keyVO.setSecret(secretKey);

        super.baseInsert(keyVO);
    }

    /**
     * 设置状态
     * @param accessKey
     * @param status
     */
    public void udpateStatus(String tokenUserId, String accessKey, int status) {
        KeyVO keyVO = super.baseFind(accessKey);

        if(null == keyVO) {
            throw new ThrowPrompt("无此key！", "062002");
        }

        if(!clientService.isMyClient(tokenUserId, keyVO.getClientId())) {
            throw new ThrowPrompt("无权操作次应用！", "062003");
        }

        if(keyVO.getStatus() == status) {
            if(status == 0) {
                throw new ThrowPrompt("已是停用状态！", "062004");
            } else {
                throw new ThrowPrompt("已是启用状态！", "062005");
            }
        }

        keyVO.setStatus(status);
        super.baseUpdate(keyVO);
    }

    /**
     * 删除
     * @param accessKey
     */
    public void del(String tokenUserId, String accessKey) {
        KeyVO keyVO = super.baseFind(accessKey);

        if(null == keyVO) {
            throw new ThrowPrompt("无此key！", "062006");
        }

        if(!clientService.isMyClient(tokenUserId, keyVO.getClientId())) {
            throw new ThrowPrompt("无权操作次应用！", "062007");
        }

        if(null == keyVO || keyVO.getStatus() == 1) {
            throw new ThrowPrompt("停用后才能删除！", "062008");
        }

        super.baseDelete(accessKey);
    }
}