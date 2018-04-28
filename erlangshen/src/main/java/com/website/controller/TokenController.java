package com.website.controller;

import com.fastjavaframework.util.CommonUtil;
import com.fastjavaframework.util.VerifyUtils;
import com.website.common.BaseElsController;
import com.website.common.Constants;
import org.springframework.web.bind.annotation.*;

import com.website.service.TokenService;

@CrossOrigin
@RestController
@RequestMapping(value= Constants.URL_TOKEN)
public class TokenController extends BaseElsController<TokenService> {

	/**
	 * 获取token
	 * @param isCheckStatus 是否校验用户激活状态
	 * @param isRepeatLogin 是否允许重复登录 不允许将设置以前的token失效
     * @return
     */
	@RequestMapping(method=RequestMethod.POST)
	public Object token(@RequestHeader(value="Is-Check-Status",required=false) boolean isCheckStatus,
						@RequestHeader(value="Is-Repeat-Login",required=false) boolean isRepeatLogin) {
		String userName = request.getParameter("userName");
		String pwd = request.getParameter("pwd");
		String platform = request.getParameter("platform");
		String loginIp = request.getParameter("loginIp");

		if(VerifyUtils.isEmpty(loginIp)) {
			loginIp = CommonUtil.getIp(super.request);
		}

		return success(this.service.inster(super.identity(), isCheckStatus, isRepeatLogin, loginIp, userName, pwd, platform));
	}

}