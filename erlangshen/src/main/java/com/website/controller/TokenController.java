package com.website.controller;

import com.fastjavaframework.util.CommonUtil;
import com.fastjavaframework.util.VerifyUtils;
import com.website.common.BaseElsController;
import com.website.common.Constants;
import com.website.model.bo.TokenBO;
import org.springframework.web.bind.annotation.*;

import com.website.service.TokenService;

import java.util.HashMap;
import java.util.Map;

/**
 * @author https://github.com/shuli495/erlangshen
 */
@CrossOrigin
@RestController
@RequestMapping(value= Constants.URL_TOKEN)
public class TokenController extends BaseElsController<TokenService> {

	/**
	 * 获取登录验证码
	 * @return
     */
	@RequestMapping(value="/{userName}", method=RequestMethod.GET)
	public Object code(@PathVariable String userName) {
		return success(this.service.code(super.identity().getClientId(), userName));
	}

	/**
	 * 获取token
	 * @param isCheckStatus 是否校验用户激活状态
     * @return
     */
	@RequestMapping(method=RequestMethod.POST)
	public Object token(@RequestHeader(value="Is-Check-Status",required=false) boolean isCheckStatus) {
		String userName = request.getParameter("userName");
		String pwd = request.getParameter("pwd");
		String code = request.getParameter("code");
		String platform = request.getParameter("platform");
		String loginIp = request.getParameter("loginIp");

		if(VerifyUtils.isEmpty(loginIp)) {
			loginIp = CommonUtil.getIp(super.request);
		}

		return this.service.inster(response, super.identity(), isCheckStatus, loginIp, userName, pwd, platform, code);
	}

}