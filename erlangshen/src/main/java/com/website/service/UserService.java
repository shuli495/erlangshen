package com.website.service;

import com.alibaba.fastjson.JSONObject;
import com.fastjavaframework.Setting;
import com.fastjavaframework.exception.ThrowException;
import com.fastjavaframework.exception.ThrowPrompt;
import com.fastjavaframework.page.PageResult;
import com.fastjavaframework.util.*;
import com.website.Executor.Certification;
import com.website.common.Constants;
import com.website.common.MailSender;
import com.website.common.PhoneSender;
import com.website.model.bo.ClientBO;
import com.website.model.bo.CodeBO;
import com.website.model.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fastjavaframework.base.BaseService;
import com.website.dao.UserDao;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class UserService extends BaseService<UserDao,UserVO> {

	@Autowired
	public ClientService clientService;
	@Autowired
	public ClientMailService clientMailService;
	@Autowired
	private ClientPhoneService clientPhoneService;
	@Autowired
	public CodeService codeService;
	@Autowired
	public ValidateService validateService;
	@Autowired
	public UserRecycleService userRecycleService;

	/**
	 * 该用户是否为登陆用户的子用户
	 * @param tokenUserId
	 * @param userId
     * @return
     */
	public boolean isMyUser(String tokenUserId, String userId) {
		UserVO user = this.dao.baseFind(userId);
		if(null == user) {
			throw new ThrowPrompt("用户不存在！");
		}

		return clientService.isMyClient(tokenUserId, user.getClientId());
	}

	/**
	 * 校验用户是否存在
	 * @param clientId
	 * @param userName
	 * @param pwd
	 * @return
	 */
	public List<UserVO> checkExist(String clientId, String userName, String pwd) {
		if(VerifyUtils.isNotEmpty(pwd)) {
			pwd = SecretUtil.md5(pwd);
		}

		List<UserVO> users = this.dao.check(clientId, userName, pwd);

		if(null == users || users.size() == 0) {
			if(VerifyUtils.isEmpty(pwd)) {
				throw new ThrowPrompt("账号不存在！", "072001");
			} else {
				throw new ThrowPrompt("账号或密码错误！", "072001");
			}
		}

		return users;
	}

	/**
	 * 插入
	 * @param tokenVO
	 * @param vo
	 */
	@Transactional
	public void  insert(TokenVO tokenVO, UserVO vo) {
		String clientId = vo.getClientId();
		String userName = vo.getUsername();
		String mail = vo.getMail();
		String phone = vo.getPhone();

		if(!clientService.isMyClient(tokenVO.getUserId(), clientId)) {
			throw new ThrowPrompt("client信息错误！", "072002");
		}

		if(VerifyUtils.isEmpty(userName) && VerifyUtils.isEmpty(mail) && VerifyUtils.isEmpty(phone)) {
			throw new ThrowPrompt("用户名、邮箱、手机必填一个！", "072003");
		}

		// 校验修改的信息是否已存在
		this.checkUserInfo(clientId, "", vo.getUsername(), vo.getMail(), vo.getPhone());

		// 拼接详细地址
		String address = null==vo.getAddress()?"":vo.getAddress();
		if(!VerifyUtils.isEmpty(vo.getArea())) {
			CodeBO codeBO = codeService.baseFind(vo.getArea());
			if(null != codeBO && !VerifyUtils.isEmpty(codeBO.getValue())) {
				address = codeBO.getValue() + "_" + address;
			}
		}
		if(!VerifyUtils.isEmpty(vo.getCity())) {
			CodeBO codeBO = codeService.baseFind(vo.getCity());
			if(null != codeBO && !VerifyUtils.isEmpty(codeBO.getValue())) {
				address = codeBO.getValue() + "_" + address;
			}
		}
		if(!VerifyUtils.isEmpty(vo.getProvince())) {
			CodeBO codeBO = codeService.baseFind(vo.getProvince());
			if(null != codeBO && !VerifyUtils.isEmpty(codeBO.getValue())) {
				address = codeBO.getValue() + "_" + address;
			}
		}
		vo.setAddress(address);

		vo.setPwd(SecretUtil.md5(vo.getPwd()));

		// 验证验证码
		ValidateVO validateVO = null;
		if(VerifyUtils.isNotEmpty(vo.getCode())) {
			validateVO = validateService.checkByMailOrPhone(clientId, mail, phone, vo.getCode(), null);

			if(VerifyUtils.isEmpty(vo.getStatus())) {
				if(VerifyUtils.isNotEmpty(phone)) {
					vo.setStatus(2);
				} else if (VerifyUtils.isNotEmpty(mail)) {
					vo.setStatus(3);
				}
			}
		}

		if(VerifyUtils.isEmpty(vo.getStatus())) {
			vo.setStatus(1);
		}

		vo.setCertification(0);
		this.dao.baseInsert(vo);

		// 删除验证码
		if(VerifyUtils.isNotEmpty(vo.getCode()) && null != validateVO) {
			try {
				validateService.delete(validateVO);
			} catch (Exception e){}
		}
	}

	/**
	 * 发送邮件
	 * @param token		为null则不校验权限，但userId必传
	 * @param type 		类型 mail注册链接 mailCode注册验证码 repwd重置密码
	 * @param userId	用户id	与mail二选一
	 * @param mail		注册mail 与suerId二选一
	 * @param callback	url注册链接中，回调地址
	 * @param isCheckUserExist	检查用户是否存在 null不检查 true存在抛异常 false不存在抛异常
	 */
	public void sendMail(TokenVO token, String type, String userId, String mail, String callback, Boolean isCheckUserExist) {
		if(VerifyUtils.isEmpty(type)) {
			type = "mail";
		}

		if(null == token && VerifyUtils.isEmpty(userId)) {
			throw new ThrowException("token或userId必传一个！", "072004");
		}

		UserVO user = null;
		ClientBO clientBO = null;

		// 校验用户权限
		if(VerifyUtils.isNotEmpty(userId)) {
			user = super.baseFind(userId);

			if(null == user) {
				throw new ThrowException("用户信息错误！", "072006");
			}
			if((user.getStatus() == 0 || user.getStatus() == 3) && type.startsWith("mail")) {
				throw new ThrowPrompt("邮箱已验证！", "072007");
			}
			if(null != token
					&& ((!Constants.PROJECT_NAME.equals(token.getClientId()) && !userId.equals(token.getUserId()))
						|| (Constants.PROJECT_NAME.equals(token.getClientId()) && !clientService.isMyClient(token.getUserId(), user.getClientId())))) {
				throw new ThrowPrompt("无权发送此用户邮件！", "072007");
			}

			clientBO = clientService.baseFind(user.getClientId());

			mail = user.getMail();
		} else if(VerifyUtils.isNotEmpty(mail)) {
			clientBO = clientService.baseFind(token.getClientId());

			UserVO queryUserVO = new UserVO();
			queryUserVO.setClientId(clientBO.getId());
			queryUserVO.setMail(mail);
			List<UserVO> users = super.baseQueryByAnd(queryUserVO);

			if(users.size() > 0) {
				user = users.get(0);
				userId = user.getId();
			} else {
				userId = token.getClientId() + "_" + mail;
				user = new UserVO();
				user.setId(userId);
			}
		} else {
			throw new ThrowPrompt("邮件信息错误！", "072035");
		}

		if(null == clientBO) {
			throw new ThrowPrompt("用户client信息异常！", "072004");
		}

		// 用户是否存在校验
		this.checkUserExist(userId, isCheckUserExist, "邮箱");

		// 校验操作间隔
		validateService.canReSend(userId,type);

		// 客户端邮箱信息，用于发送邮件
		ClientMailVO clientMailVO = clientMailService.find(clientBO.getId(), type);
		if(null == clientMailVO) {
			throw new ThrowException("该应用未配置"+type+"类型邮件信息！", "072009");
		}
		String clientMail = clientMailVO.getMail();
		String clientMailUsername = clientMailVO.getUsername();
		String clientMailPwd = clientMailVO.getPwd();
		String clientMailSmtp = clientMailVO.getSmtp();
		String clientMailSubject = clientMailVO.getSubject();
		String clientMailText = clientMailVO.getText();

		// 邮箱信息存在，则开始发送邮件
		if(!VerifyUtils.isEmpty(clientMail) && !VerifyUtils.isEmpty(clientMailPwd)) {

			clientMailText = this.makeTemplate(user, type, clientMailText, callback);

			// 发送邮件
			try {
				MailSender mailSender = new MailSender();

				if(VerifyUtils.isEmpty(clientMailSmtp)) {
					clientMailSmtp = "smtp." + clientMail.split("@")[1];
				}
				if(VerifyUtils.isEmpty(clientMailUsername)) {
					clientMailUsername = clientMail.split("@")[0];
				}
				// 解密邮箱密码
				clientMailPwd = SecretUtil.aes128Decrypt(clientMailPwd, Constants.AES_128_SECRET);

				mailSender.send(clientMail, clientBO.getName(), clientMailSmtp, clientMailSubject, clientMailText, mail, clientMailUsername, clientMailPwd);
			} catch (Exception e) {
				throw new ThrowException("注册邮件发送失败：" + e.getMessage(), "072012");
			}
		} else {
			throw new ThrowException("注册邮件发送失败：未配置邮件信息！", "072012");
		}
	}

	/**
	 * 组装邮件内容参数
	 * @param user
	 * @param type
	 * @param text
	 * @param callback
     * @return
     */
	private String makeTemplate(UserVO user, String type, String text, String callback) {
		if(VerifyUtils.isEmpty(text)) {
			return text;
		}

		// 替换code
		if(text.indexOf("${code") != -1) {
			String strTmp = text.substring(text.indexOf("${code")+6);
			String codeNumStr = strTmp.substring(0, strTmp.indexOf("}"));

			int codeNum = 8;
			if(VerifyUtils.isNotEmpty(codeNumStr)) {
				codeNum = Integer.valueOf(codeNumStr);
			}
			String code = CommonUtil.randomCode("strAndNum", codeNum);

			text = text.replace("${code"+codeNumStr+"}", code);

			// 记录code
			this.recordCode(user.getId(), type, code);
		}

		// 替换url
		if(text.indexOf("${url}") != -1) {
			String code = CommonUtil.randomCode("strAndNum", 16);
			String url = "userId=" + user.getId() + "&type=" + type + "&code=" + code;
			if(VerifyUtils.isNotEmpty(callback)) {
				url += "&callback=" + callback;
			}

			String secuetUrl = SecretUtil.aes128Encrypt(url, Constants.AES_128_SECRET);
			String checkMailUrl = Setting.getProperty("check.mail") + "?info=" + secuetUrl;

			text = text.replace("${url}", checkMailUrl);

			// 记录code
			this.recordCode(user.getId(), type, code);
		}

		// 替换时间
		if(text.indexOf("${date}") != -1) {
			text = text.replace("${date}", DateUtil.format("yyyy-MM-DD", new Date()));
		}
		if(text.indexOf("${time}") != -1) {
			text = text.replace("${time}", DateUtil.format("HH:ss:mm", new Date()));
		}
		if(text.indexOf("${datetime}") != -1) {
			text = text.replace("${datetime}", DateUtil.format("yyyy-MM-DD HH:ss:mm", new Date()));
		}

		// 替换用户属性
		if(text.indexOf("${nickname}") != -1) {
			text = text.replace("${nickname}", user.getNickname());
		}
		if(text.indexOf("${username}") != -1) {
			text = text.replace("${username}", user.getUsername());
		}
		if(text.indexOf("${name}") != -1) {
			text = text.replace("${name}", user.getName());
		}
		if(text.indexOf("${mail}") != -1) {
			text = text.replace("${mail}", user.getMail());
		}
		if(text.indexOf("${phone}") != -1) {
			text = text.replace("${phone}", user.getPhone());
		}

		return text;
	}

	/**
	 * 查询该用户是否在认证code表中存在记录，存在则修改，不存在添加
	 * @param userId
	 * @param type	操作类型
	 * @param code
     */
	private void recordCode(String userId, String type, String code) {
		ValidateVO validateVO = new ValidateVO();
		validateVO.setUserId(userId);
		validateVO.setType(type);
		ValidateVO findValidateVO = validateService.baseFindByAnd(validateVO);

		validateVO.setCode(code);
		validateVO.setCreatedTime(new Date());
		if(null == findValidateVO) {
			validateService.baseInsert(validateVO);
		} else {
			validateService.baseUpdate(validateVO);
		}
	}

	/**
	 * 校验认证邮件
	 * @param info aes加密的url参数
	 * @return 成功后页面跳转的url
     */
	public String checkMail(String info) {
		if(VerifyUtils.isEmpty(info)) {
			throw new ThrowPrompt("无认证信息，请重新认证！", "072013");
		}

		String url = SecretUtil.aes128Decrypt(info, Constants.AES_128_SECRET);

		if(url.indexOf("userId=") == -1 || url.indexOf("code=") == -1) {
			throw new ThrowPrompt("认证信息错误，请重新认证！", "072014");
		}

		String[] urls = url.split("&");
		String userId = urls[0];
		String type = urls[1];
		String code = urls[2];


		String callback = Setting.getProperty("default.redirect");
		if(url.indexOf("callback=") != -1) {
			callback = urls[3];
		}

		// 根据code查询认证表数据
		validateService.checkByUserId(userId, type, code);

		UserVO user = super.baseFind(userId);

		if(null == user) {
			throw new ThrowPrompt("用户信息错误！");
		}

		// code、mail都匹配，修改状态
		if(user.getStatus() == 1) {	//手机、邮箱都未认证，改为手机未认证
			user.setStatus(3);
			super.baseUpdate(user);
		} else if(user.getStatus() == 2) {	//邮箱未认证，改为正常状态
			user.setStatus(0);
			super.baseUpdate(user);
		}

		// 删除code
		try {
			ValidateVO validateVO = new ValidateVO();
			validateVO.setUserId(userId);
			validateVO.setType("mail");
			validateService.delete(validateVO);
		} catch (Exception e) {
		}

		return callback;
	}

	/**
	 * 发送短信
	 * @param token		为null则不校验权限，但userId必传
	 * @param type
	 * @param phone
	 * @param isCheckUserExist	检查用户是否存在 null不检查 true存在抛异常 false不存在抛异常
     */
	public void sendPhone(TokenVO token, String type, String userId, String phone, Boolean isCheckUserExist) {
		if(null == token && VerifyUtils.isEmpty(userId)) {
			throw new ThrowException("token或userId必传一个！", "072004");
		}

		ClientPhoneVO clientPhoneVO = null;

		// 设置code
		Map<String, Object> params = new HashMap<>();
		String code = CommonUtil.randomCode("num", 6);
		params.put("code", code);

		// 查找关联用户
		UserVO user = null;
		if(VerifyUtils.isNotEmpty(userId)) {
			user = super.baseFind(userId);
			if(null != token
					&& ((!Constants.PROJECT_NAME.equals(token.getClientId()) && !userId.equals(token.getUserId()))
						|| (Constants.PROJECT_NAME.equals(token.getClientId()) && !clientService.isMyClient(token.getUserId(), user.getClientId())))) {
				throw new ThrowPrompt("无权发送此用户短信！", "072007");
			}

			clientPhoneVO = clientPhoneService.find(user.getClientId(), type);
		} else {
			clientPhoneVO = clientPhoneService.find(token.getClientId(), type);

			UserVO queryUserVO = new UserVO();
			queryUserVO.setClientId(token.getClientId());
			queryUserVO.setPhone(phone);
			List<UserVO> users = super.baseQueryByAnd(queryUserVO);
			if(users.size() > 0) {
				user = users.get(0);
				userId = user.getId();
			}
		}

		if(null == clientPhoneVO) {
			throw new ThrowPrompt("该应用未配置短信平台！");
		}

		if(VerifyUtils.isEmpty(phone)) {
			phone = user.getPhone();
		}

		if(VerifyUtils.isEmpty(userId)) {
			userId = token.getClientId() + "_" + phone;
		}

		// 用户是否存在校验
		this.checkUserExist(userId, isCheckUserExist, "手机号码");

		// 记录code
		this.recordCode(userId, type, code);

		// 设置参数
		if(null != user) {
			if(user.getNickname().matches("([a-z]|[A-Z]|[0-9]){1,}")) {
				params.put("nickname", user.getNickname());
			}
			if(user.getUsername().matches("([a-z]|[A-Z]|[0-9]){1,}")) {
				params.put("username", user.getUsername());
			}
			if(user.getName().matches("([a-z]|[A-Z]|[0-9]){1,}")) {
				params.put("name", user.getName());
			}
			if(phone.matches("([a-z]|[A-Z]|[0-9]){1,}")) {
				params.put("phone", phone);
			}
		}

		params.put("date", DateUtil.format("yyyyMMDD", new Date()));
		params.put("time", DateUtil.format("HHssmm", new Date()));
		params.put("datetime", DateUtil.format("yyyyMMDDHHssmm", new Date()));

		// 解密sk
		String sk = SecretUtil.aes128Decrypt(clientPhoneVO.getSk(), Constants.AES_128_SECRET);

		// 发送短信
		new PhoneSender().send(clientPhoneVO.getAk(), sk, phone, clientPhoneVO.getSign(), clientPhoneVO.getTmplate(), JSONObject.toJSONString(params));
	}

	/**
	 * 检查用户是否存在
	 * @param userId
	 * @param isCheckUserExist
     */
	private void checkUserExist(String userId, Boolean isCheckUserExist, String text) {
		if(null == isCheckUserExist) {
			return;
		}

		if(VerifyUtils.isEmpty(text)) {
			text = "用户";
		}

		if(isCheckUserExist && VerifyUtils.isNotEmpty(userId) && userId.indexOf("_") == -1) {
			throw new ThrowPrompt("该" + text + "已存在！");
		} else if(!isCheckUserExist && (VerifyUtils.isEmpty(userId) || userId.indexOf("_") != -1)) {
			throw new ThrowPrompt("该" + text + "不存在！");
		}
	}

	/**
	 * 实名认证
	 * @param userId
	 * @param name
	 * @param idcard
     */
	public void certification(TokenVO token, String userId, String name, String idcard) {
		UserVO user = super.baseFind(userId);

		if(null == user) {
			throw new ThrowPrompt("无此用户！");
		}

		new Certification(userId, name, idcard);
	}

	/**
	 * 重置密码
	 * @param tokenVO	生成的token
	 * @param userId	修改密码的用户
	 * @param code		验证码
	 * @param oldPwd	旧密码
     * @param pwd		修改后的密码
	 *
	 * code 与 oldPwd 必填一个；
	 * code根据邮件验证码修改密码，不需要原密码；
	 * oldPwd根据原密码修改密码，不需邮件验证，只能修改当前登录用户密码。
     */
	public void rePwd(TokenVO tokenVO, String userId, String code, String oldPwd, String pwd) {
		UserVO oldUserVO = super.baseFind(userId);

		if(null == oldUserVO) {
			throw new ThrowPrompt("用户信息错误！");
		}

		// 根据code修改密码
		if(!VerifyUtils.isEmpty(code)) {
			validateService.checkByUserId(userId, null, code);
		} else {	// 根据原密码修改密码
			if(!tokenVO.getUserId().equals(userId)) {
				throw new ThrowPrompt("无权修改改用户的密码！", "072015");
			}

			if(!oldUserVO.getPwd().equals(SecretUtil.md5(oldPwd))) {
				throw new ThrowPrompt("原密码错误！", "072016");
			}
		}

		UserVO userVO = new UserVO();
		userVO.setId(userId);
		userVO.setPwd(SecretUtil.md5(pwd));
		super.baseUpdate(userVO);

		if(!VerifyUtils.isEmpty(code)) {
			// 删除code
			try {
				ValidateVO validateVO = new ValidateVO();
				validateVO.setUserId(userId);
				validateVO.setCode(code);
				validateService.delete(validateVO);
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 删除用户
	 * @param tokenVO
	 * @param id
     */
	@Transactional
	public void delete(TokenVO tokenVO, String id) {
		if(VerifyUtils.isEmpty(id)) {
			throw new ThrowPrompt("无此用户！", "072017");
		}
		UserVO userVO = super.baseFind(id);
		if(null == userVO) {
			throw new ThrowPrompt("无此用户！", "072018");
		}
		if(!Constants.PROJECT_NAME.equals(tokenVO.getClientId())) {
			throw new ThrowPrompt("无权删除用户！", "072019");
		}
		if(!clientService.isMyClient(tokenVO.getUserId(), userVO.getClientId())) {
			throw new ThrowPrompt("无此用户！", "072020");
		}

		//存入回收表中
		UserRecycleVO userRecycleVO = new UserRecycleVO();
		BeanUtils.copyProperties(userVO, userRecycleVO);
		userRecycleVO.setOperatorTime(new Date());

		userRecycleService.baseInsert(userRecycleVO);
		super.baseDelete(id);
	}

	/**
	 * 批量删除用户
	 * @param tokenVO
	 * @param ids
     */
	@Transactional
	public void deleteBatch(TokenVO tokenVO, List<String> ids) {
		List<UserRecycleVO> recycles = new ArrayList<>();

		for(String id : ids) {
			if(VerifyUtils.isEmpty(id)) {
				throw new ThrowPrompt("无此用户！", "072017");
			}
			UserVO userVO = super.baseFind(id);
			if(null == userVO) {
				throw new ThrowPrompt("无此用户！", "072018");
			}
			if(!Constants.PROJECT_NAME.equals(tokenVO.getClientId())) {
				throw new ThrowPrompt("无权删除用户！", "072019");
			}
			if(!clientService.isMyClient(tokenVO.getUserId(), userVO.getClientId())) {
				throw new ThrowPrompt("无此用户！", "072020");
			}

			//存入回收表中
			UserRecycleVO userRecycleVO = new UserRecycleVO();
			BeanUtils.copyProperties(userVO, userRecycleVO);
			userRecycleVO.setOperatorTime(new Date());

			recycles.add(userRecycleVO);
		}

		userRecycleService.baseInsertBatch(recycles);
		super.baseDeleteBatch(ids);
	}

	/**
	 * 按客户端查找用户
	 * @param userVO
	 * @return 用户列表
	 */
	public List<UserVO> queryByClient(UserVO userVO) {
		if(VerifyUtils.isEmpty(userVO.getCreatedBy())) {
			throw new ThrowPrompt("client创建人必传！", "072021");
		}
		return this.dao.queryByClient(userVO);
	}

	/**
	 * 按客户端查找用户
	 * @param userVO
	 * @return 用户列表
	 */
	public PageResult queryByClientPage(UserVO userVO) {
		if(VerifyUtils.isEmpty(userVO.getCreatedBy())) {
			throw new ThrowPrompt("client创建人必传！", "072022");
		}
		return this.dao.queryByClientPage(userVO);
	}

	/**
	 * 修改
	 * @param tokenVO
	 * @param vo
     */
	@Transactional
	public void update(TokenVO tokenVO, UserVO vo) {
		if(!Constants.PROJECT_NAME.equals(tokenVO.getClientId()) && !tokenVO.getUserId().equals(vo.getId())) {
			throw new ThrowPrompt("用户信息错误！", "072023");
		}

		UserVO oldUserVO = super.baseFind(vo.getId());

		if(VerifyUtils.isEmpty(vo.getClientId())) {
			vo.setClientId(oldUserVO.getClientId());
		}
		if(Constants.PROJECT_NAME.equals(tokenVO.getClientId()) && !clientService.isMyClient(tokenVO.getUserId(), vo.getClientId())) {
			throw new ThrowPrompt("client信息错误！", "072024");
		}

		if(null == oldUserVO) {
			throw new ThrowPrompt("无此用户！");
		}

		// 校验修改的信息是否已存在
		this.checkUserInfo(oldUserVO.getClientId(), oldUserVO.getId(), vo.getUsername(), vo.getMail(), vo.getPhone());

		// 实名认证通过 不能修改姓名 身份证号码
		if(oldUserVO.getCertification() == 3 && VerifyUtils.isNotEmpty(vo.getName()) && !vo.getName().equals(oldUserVO.getName())) {
			throw new ThrowPrompt("已实名通过，不能修改姓名！");
		}
		if(oldUserVO.getCertification() == 3 && VerifyUtils.isNotEmpty(vo.getIdcard()) && !vo.getIdcard().equals(oldUserVO.getIdcard())) {
			throw new ThrowPrompt("已实名通过，不能修改身份证号码！");
		}

		//设置修改值
		UserVO upVO = this.setUpdateVlaue(oldUserVO, vo);

		if(VerifyUtils.isEmpty(upVO.getUsername()) && VerifyUtils.isEmpty(upVO.getMail()) && VerifyUtils.isEmpty(upVO.getPhone())) {
			throw new ThrowPrompt("用户名、邮箱、手机必填一个！", "072026");
		}


		// 修改邮箱 改状态
		if(null != oldUserVO.getMail() && !oldUserVO.getMail().equalsIgnoreCase(vo.getMail()) && !VerifyUtils.isEmpty(vo.getMail())) {
			// 校验验证码
			if(VerifyUtils.isNotEmpty(vo.getCode())) {
				validateService.checkByMailOrPhone(oldUserVO.getClientId(), vo.getMail(), null, vo.getCode(), null);
			}

			if(upVO.getStatus() == 0 && VerifyUtils.isEmpty(vo.getCode())) {
				upVO.setStatus(2);
			} else if(upVO.getStatus() == 3 && VerifyUtils.isEmpty(vo.getCode())) {
				upVO.setStatus(1);
			}
		}

		// 修改手机 该状态
		if(null != oldUserVO.getPhone() && !oldUserVO.getPhone().equals(vo.getPhone()) && !VerifyUtils.isEmpty(vo.getPhone())) {
			// 校验验证码
			if(VerifyUtils.isNotEmpty(vo.getCode())) {
				validateService.checkByMailOrPhone(oldUserVO.getClientId(), null, vo.getPhone(), vo.getCode(), null);
			}

			if(upVO.getStatus() == 0 && VerifyUtils.isEmpty(vo.getCode())) {
				upVO.setStatus(3);
			} else if(upVO.getStatus() == 2 && VerifyUtils.isEmpty(vo.getCode())) {
				upVO.setStatus(1);
			}
		}

		//更新
		super.baseUpdate(upVO);
	}

	/**
	 * 设置修改的属性(不为null为修改)
	 * @param dbVO 库中最新bo
	 * @param upVO	修改的bo
	 * @return 修改后的bo
	 */
	private UserVO setUpdateVlaue(UserVO dbVO, UserVO upVO) {
		if(null == dbVO) {
			throw new ThrowPrompt("无此信息！", "072027");
		}

		if(null != upVO.getId()) {
			dbVO.setId(upVO.getId());
		}
		//客户端id
		if(null != upVO.getClientId()) {
			dbVO.setClientId(upVO.getClientId());
		}
		//用户名
		if(null != upVO.getUsername()) {
			dbVO.setUsername(upVO.getUsername());
		}
		//昵称
		if(null != upVO.getNickname()) {
			dbVO.setNickname(upVO.getNickname());
		}
		//邮箱
		if(null != upVO.getMail()) {
			dbVO.setMail(upVO.getMail());
		}
		//手机号码
		if(null != upVO.getPhone()) {
			dbVO.setPhone(upVO.getPhone());
		}
		//电话
		if(null != upVO.getTel()) {
			dbVO.setTel(upVO.getTel());
		}
		//QQ
		if(null != upVO.getQq()) {
			dbVO.setQq(upVO.getQq());
		}
		//微信
		if(null != upVO.getWeixin()) {
			dbVO.setWeixin(upVO.getWeixin());
		}
		//微信二维码
		if(null != upVO.getWexinImg()) {
			dbVO.setWexinImg(upVO.getWexinImg());
		}
		//新浪微博
		if(null != upVO.getWeibo()) {
			dbVO.setWeibo(upVO.getWeibo());
		}
		//头像
		if(null != upVO.getHead()) {
			dbVO.setHead(upVO.getHead());
		}
		//姓名
		if(null != upVO.getName()) {
			dbVO.setName(upVO.getName());
		}
		//性别 0女 1男
		if(null != upVO.getSex()) {
			dbVO.setSex(upVO.getSex());
		}
		//身份证号
		if(null != upVO.getIdcard()) {
			dbVO.setIdcard(upVO.getIdcard());
		}
		// 实名认证
		if(null != upVO.getCertification()) {
			dbVO.setCertification(upVO.getCertification());
		}
		//省
		if(null != upVO.getProvince()) {
			dbVO.setProvince(upVO.getProvince());
		}
		//市
		if(null != upVO.getCity()) {
			dbVO.setCity(upVO.getCity());
		}
		//区
		if(null != upVO.getArea()) {
			dbVO.setArea(upVO.getArea());
		}
		//地址
		if(null != upVO.getAddress()) {
			// 拼接详细地址
			String address = upVO.getAddress();
			if(!VerifyUtils.isEmpty(upVO.getArea())) {
				CodeBO codeBO = codeService.baseFind(upVO.getArea());
				if(null != codeBO && !VerifyUtils.isEmpty(codeBO.getValue())) {
					address = codeBO.getValue() + "_" + address;
				}
			}
			if(!VerifyUtils.isEmpty(upVO.getCity())) {
				CodeBO codeBO = codeService.baseFind(upVO.getCity());
				if(null != codeBO && !VerifyUtils.isEmpty(codeBO.getValue())) {
					address = codeBO.getValue() + "_" + address;
				}
			}
			if(!VerifyUtils.isEmpty(upVO.getProvince())) {
				CodeBO codeBO = codeService.baseFind(upVO.getProvince());
				if(null != codeBO && !VerifyUtils.isEmpty(codeBO.getValue())) {
					address = codeBO.getValue() + "_" + address;
				}
			}
			dbVO.setAddress(address);
		}
		//创建时间
		if(null != upVO.getCreatedTime()) {
			dbVO.setCreatedTime(upVO.getCreatedTime());
		}
		//状态 0正常
		if(null != upVO.getStatus()) {
			dbVO.setStatus(upVO.getStatus());
		}
		// 来源
		if(null != upVO.getSource()) {
			dbVO.setSource(upVO.getSource());
		}
		return dbVO;
	}

	/**
	 * 校验修改的信息是否已存在
	 * @param clientId
	 * @param nowUserId 要修改的用户的id，插入不需要填写
	 * @param userName	添加或修改的用户名
	 * @param mail		添加或修改的邮箱
     * @param phone		添加或修改的手机号码
     */
	public void checkUserInfo(String clientId, String nowUserId, String userName, String mail, String phone) {
		if(!VerifyUtils.isEmpty(userName)) {
			List<UserVO> users = this.dao.check(clientId, userName, null);
			for(UserVO user : users) {
				if((VerifyUtils.isEmpty(nowUserId) && user.getUsername().equals(userName)) || (!VerifyUtils.isEmpty(nowUserId) && !user.getId().equals(nowUserId))) {
					throw new ThrowPrompt("该用户名已注册，请更换后再试！", "072028");
				}
			}
		}

		if(!VerifyUtils.isEmpty(mail)) {
			List<UserVO> users = this.dao.check(clientId, mail, null);
			for(UserVO user : users) {
				if((VerifyUtils.isEmpty(nowUserId) && user.getMail().equals(mail)) || (!VerifyUtils.isEmpty(nowUserId) && !user.getId().equals(nowUserId))) {
					if(user.getStatus() == 1 || user.getStatus() == 2) {
						throw new ThrowPrompt("该邮箱已注册，但未验证！", "072029");
					} else {
						throw new ThrowPrompt("该邮箱已注册，请更换后再试！", "072030");
					}
				}
			}
		}
		if(!VerifyUtils.isEmpty(phone)) {
			List<UserVO> users = this.dao.check(clientId, phone, null);
			for(UserVO user : users) {
				if((VerifyUtils.isEmpty(nowUserId) && user.getPhone().equals(phone)) || (!VerifyUtils.isEmpty(nowUserId) && !user.getId().equals(nowUserId))) {
					if(user.getStatus() == 1 || user.getStatus() == 3) {
						throw new ThrowPrompt("该手机号码已注册，但未验证！", "072031");
					} else {
						throw new ThrowPrompt("该手机号码已注册，请更换后再试！", "072032");
					}
				}
			}
		}
	}
}
