package com.erlangshen.dao;

import com.erlangshen.model.vo.ValidateVO;
import org.springframework.stereotype.Repository;
import com.fastjavaframework.base.BaseDao;
import com.erlangshen.model.bo.ValidateBO;

/**
 * @author https://github.com/shuli495/erlangshen
 */
@Repository
public class ValidateDao extends BaseDao<ValidateVO> {

    public void delete(ValidateBO validateBO) {
        this.sql().delete("delete", validateBO);
    }

}