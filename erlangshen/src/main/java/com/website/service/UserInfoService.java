package com.website.service;

import com.website.model.vo.UserVO;
import org.springframework.stereotype.Service;
import com.fastjavaframework.base.BaseService;
import com.website.dao.UserInfoDao;

@Service
public class UserInfoService extends BaseService<UserInfoDao,UserVO> {
}