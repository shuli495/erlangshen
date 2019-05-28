package com.erlangshen.service;

import com.erlangshen.model.vo.LoginLogVO;
import org.springframework.stereotype.Service;
import com.fastjavaframework.base.BaseService;
import com.erlangshen.dao.LoginLogDao;

import java.util.Date;

/**
 * 登录日志
 * @author https://github.com/shuli495/erlangshen
 */
@Service
public class LoginLogService extends BaseService<LoginLogDao, LoginLogVO> {

	public void insert(String userId, String ip) {
		LoginLogVO loginLogVO = new LoginLogVO();
		loginLogVO.setUserId(userId);
		loginLogVO.setIp(ip);
		loginLogVO.setLoginTime(new Date());

		super.baseInsert(loginLogVO);
	}
}