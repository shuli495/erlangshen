package com.website.dao;

import com.website.model.vo.PermissionRoleVO;
import org.springframework.stereotype.Repository;
import com.fastjavaframework.base.BaseDao;
import com.website.model.bo.PermissionRoleBO;

import java.util.List;

/**
 * @author https://github.com/shuli495/erlangshen
 */
@Repository
public class PermissionRoleDao extends BaseDao<PermissionRoleVO> {

    /**
     * 按用户id查询角色
     * @return
     */
    public List<PermissionRoleVO> queryByUser(String userId) {
        return this.sql().selectList("queryByUser", userId);
    }
}