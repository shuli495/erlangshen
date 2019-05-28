package com.erlangshen.controller;

import com.erlangshen.common.BaseElsController;
import com.erlangshen.common.Constants;
import com.erlangshen.model.vo.TokenVO;
import com.erlangshen.service.TokenService;
import com.erlangshen.service.UserService;
import com.fastjavaframework.annotation.Authority;
import com.fastjavaframework.exception.ThrowException;
import com.fastjavaframework.util.CommonUtil;
import com.fastjavaframework.util.VerifyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


/**
 * @author https://github.com/shuli495/erlangshen
 */
@CrossOrigin
@RestController
@RequestMapping(value= Constants.URL_TOKEN)
public class TokenController extends BaseElsController<TokenService> {

	@Autowired
	private UserService userService;

	/**
	 * 获取token
	 * @param isCheckStatus 是否校验用户激活状态
     * @return
     */
	@Authority(role = Constants.ADMIN_TOKEN)
	@RequestMapping(method=RequestMethod.POST)
	public Object token(@RequestHeader(value="Is-Check-Status",required=false) boolean isCheckStatus) {
		String userName = request.getParameter("userName");
		String pwd = request.getParameter("pwd");
		String verifyCode = request.getParameter("verifyCode");
		String platform = request.getParameter("platform");
		String loginIp = request.getParameter("loginIp");

		if(Constants.IDENTITY_TYPE_KEY.equals(super.identity().getAuthenticationMethod()) && VerifyUtils.isEmpty(loginIp)) {
			throw new ThrowException("AK/SK方式loginIp参数必传！", "071002");
		}

		if(VerifyUtils.isEmpty(loginIp)) {
			loginIp = CommonUtil.getIp(super.request);
		}

		return this.service.inster(response, super.identity(), isCheckStatus, loginIp, userName, pwd, platform, verifyCode);
	}

	/**
	 * 查询token是否有效
	 * @param token
	 * @return
	 */
	@RequestMapping(value = "/{token}", method=RequestMethod.POST)
	public Object check(@PathVariable String token) {
		String loginIp = request.getParameter("loginIp");

		// 检查token
		TokenVO tokenVO = this.service.check(token, loginIp);

		Map<String, Object> result = new HashMap<>(2);
		if(userService.isMyUser(super.identity().getUserId(), tokenVO.getUserId())) {
			result.put("valid", true);
			result.put("token", tokenVO);
		} else {
			result.put("valid", false);
		}

		return success(result);
	}

}