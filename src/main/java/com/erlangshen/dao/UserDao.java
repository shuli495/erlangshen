package com.erlangshen.dao;

import com.fastjavaframework.page.PageResult;
import com.fastjavaframework.util.VerifyUtils;
import com.erlangshen.model.vo.UserVO;
import org.springframework.stereotype.Repository;
import com.fastjavaframework.base.BaseDao;

import java.util.List;

/**
 * @author https://github.com/shuli495/erlangshen
 */
@Repository
public class UserDao extends BaseDao<UserVO> {

    /**
     * 查询用户是否存在
     * @param clientId
     * @param userName userName 或 mail 或 phone
     * @param pwd
     * @return 用户信息
     */
    public List<UserVO> check(String clientId, String userName, String pwd) {
        UserVO userVO = new UserVO();
        userVO.setClientId(clientId);
        userVO.setUsername(userName);
        if(!VerifyUtils.isEmpty(pwd)) {
            userVO.setPwd(pwd);
        }
        return this.sql().selectList("check", userVO);
    }

    /**
     * 按客户端查找用户
     * @param userVO
     * @return 用户列表
     */
    public List<UserVO> queryByClient(UserVO userVO) {
        return this.sql().selectList("queryByClient", userVO);
    }

    /**
     * 按客户端查找用户
     * @param userVO
     * @return 用户分页
     */
    public PageResult queryByClientPage(UserVO userVO) {
        return this.sql().selectPage("queryByClient", userVO, userVO.getPage());
    }
}