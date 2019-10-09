package com.erlangshen.controller;

import com.erlangshen.common.BaseElsController;
import com.erlangshen.common.Constants;
import com.erlangshen.model.vo.LoginVO;
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
     * @return
     */
	@Authority(role = Constants.ADMIN_TOKEN)
	@RequestMapping(method=RequestMethod.POST)
	public Object token(@RequestBody LoginVO loginVO) {
		String loginIp = loginVO.getLoginIp();
		if(Constants.IDENTITY_TYPE_KEY.equals(super.identity().getAuthenticationMethod()) && VerifyUtils.isEmpty(loginIp)) {
			throw new ThrowException("AK/SK方式loginIp参数必传！", "071002");
		}

		if(VerifyUtils.isEmpty(loginIp)) {
			loginIp = CommonUtil.getIp(super.request);
		}

		return this.service.inster(super.identity(), loginVO.isCheckStatus(), loginIp, loginVO.getUserName(),
				loginVO.getPwd(), loginVO.getPlatform(), loginVO.getRobotCode());
	}

	/**
	 * 查询token是否有效
	 * @return
	 */
	@RequestMapping(method=RequestMethod.PUT)
	public Object check(@RequestBody LoginVO loginVO) {
		String loginIp = loginVO.getLoginIp();

		// 检查token
		TokenVO tokenVO = this.service.check(loginVO.getToken(), loginIp);

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