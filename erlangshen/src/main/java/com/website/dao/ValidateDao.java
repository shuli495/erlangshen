package com.website.dao;

import org.springframework.stereotype.Repository;
import com.fastjavaframework.base.BaseDao;
import com.website.model.bo.ValidateBO;

@Repository
public class ValidateDao extends BaseDao<ValidateBO> {

    public void delete(ValidateBO validateBO) {
        this.sql().delete("delete", validateBO);
    }

}