package com.website.common;

import com.fastjavaframework.base.BaseController;
import com.fastjavaframework.base.BaseService;
import com.website.model.vo.TokenVO;

/**
 * Created by wsl on 1/23 0023.
 */
public class BaseElsController<B extends BaseService<?, ?>> extends BaseController<B> {

    public TokenVO identity() {
        try{
            return (TokenVO)super.request.getAttribute("identity");
        } catch (Exception e) {
            return null;
        }
    }
}
