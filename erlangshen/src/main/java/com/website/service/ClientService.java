package com.website.service;

import com.fastjavaframework.exception.ThrowPrompt;
import com.fastjavaframework.util.UUID;
import com.fastjavaframework.util.VerifyUtils;
import com.website.model.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fastjavaframework.base.BaseService;
import com.website.dao.ClientDao;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ClientService extends BaseService<ClientDao,ClientVO> {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRecycleService userRecycleService;
    @Autowired
    private ClientPhoneService clientPhoneService;
    @Autowired
    private ClientMailService clientMailService;
    @Autowired
    private ClientSecurityService clientSecurityService;

    /**
     * 创建client
     * @param clientVO
     * @return
     */
    public String inster(ClientVO clientVO) {
        String id = UUID.uuid();
        clientVO.setId(id);
        clientVO.setDeleted(false);

        // 校验name group
        this.checkName(id, clientVO.getCreatedBy(), clientVO.getName());

        super.baseInsert(clientVO);

        // 添加默认安全项
        ClientSecurityVO clientSecurityVO = new ClientSecurityVO();
        clientSecurityVO.setClientId(id);
        clientSecurityVO.setIsCheckPlace(false);
        clientSecurityVO.setCheckPlacePriority(1);
        clientSecurityVO.setIsCheckPlatform(1);
        clientSecurityVO.setCheckPlatformType(0);
        clientSecurityService.baseInsert(clientSecurityVO);

        return id;
    }

    /**
     * 修改client
     * @param tokenUserId
     * @param clientVO
     */
    public void update(String tokenUserId, ClientVO clientVO) {
        if(VerifyUtils.isEmpty(clientVO.getId())) {
            throw new ThrowPrompt("id不能为空！");
        }

        ClientVO nowBO = super.baseFind(clientVO.getId());

        if(null == nowBO) {
            throw new ThrowPrompt("无此应用");
        }

        if(!tokenUserId.equals(nowBO.getCreatedBy())) {
            throw new ThrowPrompt("无权修改此数据！");
        }

        // 校验name group
        this.checkName(clientVO.getId(), nowBO.getCreatedBy(), clientVO.getName());

        if(!VerifyUtils.isEmpty(clientVO.getName())) {
            nowBO.setName(clientVO.getName());
        }

        super.baseUpdate(clientVO);
    }

    /**
     * 检查name group是否重复
     * @param id
     * @param userId
     * @param name
     */
    private void checkName(String id, String userId, String name) {
        // 查询group
        ClientVO queryClientVO = new ClientVO();
        queryClientVO.setCreatedBy(userId);
        queryClientVO.setDeleted(false);
        List<ClientVO> clientList = super.baseQueryByAnd(queryClientVO);

        // name不能重复
        for (ClientVO client : clientList) {
            if(!id.equals(client.getId()) && client.getName().equals(name)) {
                throw new ThrowPrompt("名称"+name + "已存在！");
            }
        }
    }

    /**
     * 删除client
     * @param tokenVO
     * @param ids
     */
    @Transactional
    public void delete(TokenVO tokenVO, List<String> ids) {
        List<ClientVO> delClients = new ArrayList<>();

        for (String id : ids) {
            ClientVO clientVO = super.baseFind(id);
            if(null == clientVO) {
                throw new ThrowPrompt("无此应用");
            }
            if(!tokenVO.getUserId().equals(clientVO.getCreatedBy())) {
                throw new ThrowPrompt("无权删除此数据！");
            }

            //删除用户
            this.deleteUser(tokenVO, id);
            //删除邮件平台
            clientMailService.deleteByClientId(id);
            //删除短信平台
            clientPhoneService.deleteByClientId(id);

            clientVO.setDeleted(true);
            delClients.add(clientVO);
        }

        // 删除安全设置
        clientSecurityService.baseDeleteBatch(ids);

        super.baseDeleteLogicBatch(delClients);
    }

    /**
     * 删除client下的用户
     * @param tokenVO
     * @param clientId
     * @return 删除的用户数
     */
    private int deleteUser(TokenVO tokenVO, String clientId) {
        List<String> userIds = new ArrayList<>();   //删除用户id

        // 当前client下的用户
        UserVO userVO = new UserVO();
        userVO.setClientId(clientId);
        List<UserVO> userList = userService.baseQueryByAnd(userVO);

        for(UserVO user : userList) {
            userIds.add(user.getId());
        }

        userService.deleteBatch(tokenVO, userIds);
        return userList.size();
    }

    /**
     * 查询客户端详情
     * @param tokenUserId
     * @param id
     * @return 客户端详情、客户端下未删除用户和已删除用户
     */
    public ClientVO find(String tokenUserId, String id) {
        ClientVO clientVO = super.baseFind(id);

        if(null == clientVO) {
            throw new ThrowPrompt("无此应用");
        }
        if(!tokenUserId.equals(clientVO.getCreatedBy())) {
            throw new ThrowPrompt("无权删除此数据！");
        }

        UserVO userVO = new UserVO();
        userVO.setClientId(clientVO.getId());
        List<UserVO> userList = userService.baseQueryByAnd(userVO);

        UserRecycleVO userRecycleVO = new UserRecycleVO();
        userRecycleVO.setClientId(clientVO.getId());
        List<UserRecycleVO> userRecycleList = userRecycleService.baseQueryByAnd(userRecycleVO);

        clientVO.setUsers(userList);
        clientVO.setDelUsers(userRecycleList);

        return clientVO;
    }

    /**
     * 操作的client是否为token用户所创建的client
     * @param tokenUserId 生成token的用户
     * @param clientId	要修改用的clientId或用户所在clientId
     */
    public boolean isMyClient(String tokenUserId, String clientId) {
        if(VerifyUtils.isEmpty(tokenUserId)) {
            throw new ThrowPrompt("token不能为空！");
        }

        ClientVO client = super.baseFind(clientId);

        if(null == client) {
            throw new ThrowPrompt("无此应用");
        }

        if(client.getCreatedBy().equals(tokenUserId)) {
            return true;
        }
        return false;
    }
}