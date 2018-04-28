package com.website.service;

import org.springframework.stereotype.Service;
import com.fastjavaframework.base.BaseService;
import com.website.dao.LoginLogDao;
import com.website.model.vo.LoginLogVO;

import java.util.Date;

@Service
public class LoginLogService extends BaseService<LoginLogDao,LoginLogVO> {

	public void insert(String userId, String ip) {
		LoginLogVO loginLogVO = new LoginLogVO();
		loginLogVO.setUserId(userId);
		loginLogVO.setIp(ip);
		loginLogVO.setLoginTime(new Date());

		super.baseInsert(loginLogVO);
	}
}