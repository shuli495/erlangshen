package com.website.controller;

import com.fastjavaframework.exception.ThrowException;
import com.fastjavaframework.util.CommonUtil;
import com.fastjavaframework.util.VerifyUtils;
import com.website.common.BaseElsController;
import com.website.common.Constants;
import org.springframework.web.bind.annotation.*;

import com.website.service.TokenService;


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
	@RequestMapping(method=RequestMethod.GET)
	public Object code(@RequestParam(required = false) String loginIp) {
		if(super.identity().getAuthenticationMethod().equals("KEY") && VerifyUtils.isEmpty(loginIp)) {
			throw new ThrowException("AK/SK方式loginIp参数必传！", "071001");
		}

		if(VerifyUtils.isEmpty(loginIp)) {
			loginIp = CommonUtil.getIp(super.request);
		}
		return success(this.service.code(super.identity().getClientId(), loginIp));
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

		if(super.identity().getAuthenticationMethod().equals("KEY") && VerifyUtils.isEmpty(loginIp)) {
			throw new ThrowException("AK/SK方式loginIp参数必传！", "071002");
		}

		if(VerifyUtils.isEmpty(loginIp)) {
			loginIp = CommonUtil.getIp(super.request);
		}

		return this.service.inster(response, super.identity(), isCheckStatus, loginIp, userName, pwd, platform, code);
	}

}