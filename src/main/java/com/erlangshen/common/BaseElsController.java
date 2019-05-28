package com.erlangshen.common;

import com.erlangshen.model.vo.TokenVO;
import com.fastjavaframework.base.BaseController;
import com.fastjavaframework.base.BaseService;

/**
 * controller父类
 * @author https://github.com/shuli495/erlangshen
 */
public class BaseElsController<B extends BaseService<?, ?>> extends BaseController<B> {

    /**
     * 用户登录信息
     * @return
     */
    public TokenVO identity() {
        try{
            return (TokenVO)super.request.getAttribute("identity");
        } catch (Exception e) {
            return null;
        }
    }
}
