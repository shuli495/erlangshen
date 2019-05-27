package com.website.service;

import com.alibaba.fastjson.JSONObject;
import com.fastjavaframework.base.BaseService;
import com.fastjavaframework.exception.ThrowException;
import com.fastjavaframework.exception.ThrowPrompt;
import com.fastjavaframework.page.PageResult;
import com.fastjavaframework.response.ReturnJson;
import com.fastjavaframework.util.CodeUtil;
import com.fastjavaframework.util.DateUtil;
import com.fastjavaframework.util.SecretUtil;
import com.fastjavaframework.util.VerifyUtils;
import com.website.common.Constants;
import com.website.common.MailSender;
import com.website.common.PhoneSender;
import com.website.dao.UserDao;
import com.website.executor.Certification;
import com.website.model.bo.ClientBO;
import com.website.model.bo.CodeBO;
import com.website.model.vo.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 用户
 * @author https://github.com/shuli495/erlangshen
 */
@Service
public class UserService extends BaseService<UserDao,UserVO> {
	private static final Log logger=LogFactory.getLog(UserService.class);

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
	public UserInfoService userInfoService;
	@Autowired
	public UserRecycleService userRecycleService;
	@Autowired
	public TokenService tokenService;

	@Value("${aes.secret}")
	private String aesSecret;

	@Value("${check.mail}")
	private String checkMail;

	@Value("${default.redirect}")
	private String defaultRedirect;


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
	 * 根据用户名、邮箱或手机校验用户是否存在
	 * @param clientId
	 * @param userName 用户名或邮箱或手机
	 * @param pwd
	 * @return
	 */
	public List<UserVO> checkExist(String clientId, String userName, String pwd) {
		if(VerifyUtils.isEmpty(userName)) {
			throw new ThrowPrompt("请填写用户名、邮箱或手机！", "081015");
		}

		if(VerifyUtils.isNotEmpty(pwd)) {
			pwd = SecretUtil.md5(pwd);
		}

		return this.dao.check(clientId, userName, pwd);
	}

	/**
	 * 详情查询
	 * @param token
	 * @param userId
     * @return
     */
	public UserVO find(TokenVO token, String userId) {
		UserVO user = super.baseFind(userId);

		boolean isExit = null == user
				|| (Constants.PROJECT_NAME.equals(token.getClientId())
					&& !clientService.isMyClient(token.getUserId(), user.getClientId()));
		if(isExit) {
			throw new ThrowPrompt("无此用户！", "081016");
		}

		UserVO userInfo = userInfoService.baseFind(userId);

		if(null != userInfo) {
			user = this.setUpdateVlaue(userInfo, user);
		}

		if(!Constants.PROJECT_NAME.equals(token.getClientId())) {
			// 非erlangshen用户查看他人详情，置空关键项
			if(!token.getUserId().equals(userId)) {
				user.setIdcard("");
				user.setCertification(null);
				user.setCertificationFailMsg("");
				user.setAddress("");
				user.setCreatedTime(null);
				user.setMailVerify(null);
				user.setPhoneVerify(null);
				user.setStatus(null);
			}
		}

		// 密码置空
		user.setPwd("");

		return user;
	}

	/**
	 * 插入
	 * @param tokenVO
	 * @param vo
	 */
	@Transactional(rollbackFor = Exception.class)
	public void insert(TokenVO tokenVO, UserVO vo) {
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

		// 校验验机器人证码
		String validateId = tokenVO.getClientId() + "_" + vo.getLoginIp();
		validateService.checkVerifyCode(validateId, "register", vo.getVerifyCode());

		// 校验修改的信息是否已存在
		this.checkUserInfo(clientId, "", vo.getUsername(), vo.getMail(), vo.getPhone());

		// 拼接详细地址
		String address = this.joinAddress(vo);
		vo.setAddress(address);

		vo.setPwd(BCrypt.hashpw(vo.getPwd(), BCrypt.gensalt()));

		// 验证验证码
		ValidateVO validateVO = null;
		if(VerifyUtils.isNotEmpty(vo.getCode())) {
			validateVO = validateService.checkByMailOrPhone(clientId, mail, phone, vo.getCode(), null);

			if(VerifyUtils.isNotEmpty(phone)) {
				vo.setPhoneVerify(1);
			} else if(VerifyUtils.isNotEmpty(mail)) {
				vo.setMailVerify(1);
			}
		}

		if(null == vo.getMailVerify()) {
			vo.setMailVerify(0);
		}
		if(null == vo.getPhoneVerify()) {
			vo.setPhoneVerify(0);
		}
		if(VerifyUtils.isEmpty(vo.getStatus())) {
			vo.setStatus(1);
		}
		vo.setCertification(0);

		// 插入数据
		this.dao.baseInsert(vo);
		userInfoService.baseInsert(vo);

		// 删除验证码
		if(VerifyUtils.isNotEmpty(vo.getCode()) && null != validateVO) {
			try {
				validateService.delete(validateVO.getUserId(), validateVO.getType(), null);
			} catch (Exception e){
				logger.error(e.getMessage());
			}
		}

		// 删除防机器人验证码
		if(VerifyUtils.isNotEmpty(vo.getVerifyCode())) {
			try {
				validateService.delete(validateId, "register", null);
			} catch (Exception e){
				logger.error(e.getMessage());
			}
		}

		try {
			if(vo.getAutoLogin()) {
				TokenVO token = tokenService.newToken(vo, vo.getLoginIp(), vo.getPlatform());
				vo.setToken(token);
			}
		} catch (Exception e){
			logger.error(e.getMessage());
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
	public Object sendMail(TokenVO token, String type, String userId, String mail, String callback, String loginIp,
						   String verifyCode, Boolean isCheckUserExist) {
		if(null == token && VerifyUtils.isEmpty(userId)) {
			throw new ThrowException("token或userId必传一个！", "072004");
		}

		UserVO user = null;
		ClientBO clientBO = null;

		// 校验验机器人证码
		String validateId = token.getClientId() + "_" + loginIp;
		validateService.checkVerifyCode(validateId, "sendMail", verifyCode);

		// 校验用户权限
		if(VerifyUtils.isNotEmpty(userId)) {
			user = super.baseFind(userId);

			if(null == user) {
				Map<String, String> result = new HashMap<>(3);
				result.put("data", "用户信息错误！");
				result.put("code", "072006");
				result.put("image", validateService.verifyCode("sendMail", token.getClientId(), loginIp));
				return result;
			}
			if(user.getMailVerify() == Constants.USER_MAIL_VERIFY_SUCCESS && type.startsWith(Constants.SEND_MAIL_TYPE_MAIL)) {
				throw new ThrowPrompt("邮箱已验证！", "072007");
			}

			boolean isPermission = null != token
									&& ((!Constants.PROJECT_NAME.equals(token.getClientId()) && !userId.equals(token.getUserId()))
										|| (Constants.PROJECT_NAME.equals(token.getClientId()) && !clientService.isMyClient(token.getUserId(), user.getClientId())));
			if(isPermission) {
				throw new ThrowPrompt( "无权发送此用户邮件！", "072007");
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
				clientMailPwd = SecretUtil.aes128Decrypt(clientMailPwd, aesSecret);

				mailSender.send(clientMail, clientBO.getName(), clientMailSmtp, clientMailSubject, clientMailText, mail, clientMailUsername, clientMailPwd);

				// 删除防机器人验证码
				if(VerifyUtils.isNotEmpty(verifyCode)) {
					try {
						validateService.delete(validateId, "sendMail", null);
					} catch (Exception e){
						logger.error(e.getMessage());
					}
				}

				return new ReturnJson().success();
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
		if(text.indexOf(Constants.TEMPLATE_MAIL_CODE) != -1) {
			String strTmp = text.substring(text.indexOf(Constants.TEMPLATE_MAIL_CODE)+6);
			String codeNumStr = strTmp.substring(0, strTmp.indexOf("}"));

			int codeNum = 8;
			if(VerifyUtils.isNotEmpty(codeNumStr)) {
				codeNum = Integer.valueOf(codeNumStr);
			}
			String code = CodeUtil.randomCode("strAndNum", codeNum);

			text = text.replace("${code"+codeNumStr+"}", code);

			// 记录code
			this.recordCode(user.getId(), type, code);
		}

		// 替换url
		if(text.indexOf(Constants.TEMPLATE_MAIL_URL) != -1) {
			String code = CodeUtil.randomCode("strAndNum", 16);
			String url = "userId=" + user.getId() + "&type=" + type + "&code=" + code;
			if(VerifyUtils.isNotEmpty(callback)) {
				url += "&callback=" + callback;
			}

			String secuetUrl = SecretUtil.aes128Encrypt(url, aesSecret);
			String checkMailUrl = checkMail + "?info=" + secuetUrl;

			text = text.replace(Constants.TEMPLATE_MAIL_URL, checkMailUrl);

			// 记录code
			this.recordCode(user.getId(), type, code);
		}

		// 替换时间
		if(text.indexOf(Constants.TEMPLATE_MAIL_DATE) != -1) {
			text = text.replace(Constants.TEMPLATE_MAIL_DATE, DateUtil.format("yyyy-MM-DD", new Date()));
		}
		if(text.indexOf(Constants.TEMPLATE_MAIL_TIME) != -1) {
			text = text.replace(Constants.TEMPLATE_MAIL_TIME, DateUtil.format("HH:ss:mm", new Date()));
		}
		if(text.indexOf(Constants.TEMPLATE_MAIL_DATETIME) != -1) {
			text = text.replace(Constants.TEMPLATE_MAIL_DATETIME, DateUtil.format("yyyy-MM-DD HH:ss:mm", new Date()));
		}

		// 替换用户属性
		if(text.indexOf(Constants.TEMPLATE_MAIL_MAIL) != -1) {
			text = text.replace(Constants.TEMPLATE_MAIL_MAIL, user.getMail());
		}
		if(text.indexOf(Constants.TEMPLATE_MAIL_PHONE) != -1) {
			text = text.replace(Constants.TEMPLATE_MAIL_PHONE, user.getPhone());
		}
		if(text.indexOf(Constants.TEMPLATE_MAIL_NICKNAME) != -1
				|| text.indexOf(Constants.TEMPLATE_MAIL_USERNAME) != -1
				|| text.indexOf(Constants.TEMPLATE_MAIL_NAME) != -1) {
			UserVO userInfo = userInfoService.baseFind(user.getId());
			if(text.indexOf(Constants.TEMPLATE_MAIL_NICKNAME) != -1) {
				text = text.replace(Constants.TEMPLATE_MAIL_NICKNAME, userInfo.getNickname());
			}
			if(text.indexOf(Constants.TEMPLATE_MAIL_USERNAME) != -1) {
				text = text.replace(Constants.TEMPLATE_MAIL_USERNAME, userInfo.getUsername());
			}
			if(text.indexOf(Constants.TEMPLATE_MAIL_NAME) != -1) {
				text = text.replace(Constants.TEMPLATE_MAIL_NAME, userInfo.getName());
			}
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

		String url = SecretUtil.aes128Decrypt(info, aesSecret);

		if(url.indexOf(Constants.CHECK_MAIL_PARAMS_USER_ID) == -1 || url.indexOf(Constants.CHECK_MAIL_PARAMS_CODE) == -1) {
			throw new ThrowPrompt("认证信息错误，请重新认证！", "072014");
		}

		String[] urls = url.split("&");
		String userId = urls[0];
		String type = urls[1];
		String code = urls[2];


		String callback = defaultRedirect;
		if(url.indexOf(Constants.CHECK_MAIL_PARAMS_CALLBACK) != -1) {
			callback = urls[3];
		}

		// 根据code查询认证表数据
		validateService.checkByUserId(userId, type, code);

		UserVO user = super.baseFind(userId);

		if(null == user) {
			throw new ThrowPrompt("用户信息错误！");
		}

		// code、mail都匹配，修改状态
		if(user.getMailVerify() == 0) {
			user.setMailVerify(1);
			super.baseUpdate(user);
		}

		// 删除code
		try {
			validateService.delete(userId, "mail", null);
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
	public Object sendPhone(TokenVO token, String type, String userId, String phone, String loginIp, String verifyCode, Boolean isCheckUserExist) {
		if(null == token && VerifyUtils.isEmpty(userId)) {
			throw new ThrowException("token或userId必传一个！", "072004");
		}

		// 校验验机器人证码
		String validateId = token.getClientId() + "_" + loginIp;
		validateService.checkVerifyCode(validateId, "sendPhone", verifyCode);

		ClientPhoneVO clientPhoneVO = null;

		// 设置code
		Map<String, Object> params = new HashMap<>(8);
		String code = CodeUtil.randomCode("num", 6);
		params.put("code", code);

		// 查找关联用户
		UserVO user = null;
		if(VerifyUtils.isNotEmpty(userId)) {
			user = super.baseFind(userId);

			boolean isPermission = null != token
									&& ((!Constants.PROJECT_NAME.equals(token.getClientId()) && !userId.equals(token.getUserId()))
									|| (Constants.PROJECT_NAME.equals(token.getClientId()) && !clientService.isMyClient(token.getUserId(), user.getClientId())));
			if(isPermission) {
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
			throw new ThrowPrompt("该应用未配置短信平台！", "072022");
		}

		if(VerifyUtils.isEmpty(phone)) {
			phone = user.getPhone();
		}

		if(VerifyUtils.isEmpty(userId)) {
			userId = token.getClientId() + "_" + phone;
		}

		// 用户是否存在校验
		try {
			this.checkUserExist(userId, isCheckUserExist, "手机号码");
		} catch (ThrowPrompt e) {
			Map<String, String> result = new HashMap<>(3);
			result.put("data", e.getMessage());
			result.put("code", e.getCode());
			result.put("image", validateService.verifyCode("sendPhone", token.getClientId(), loginIp));
			return result;
		}

		// 记录code
		this.recordCode(userId, type, code);

		// 设置参数
		if(null != user) {
			if(phone.matches(Constants.REGEX_USERNAME)) {
				params.put("phone", phone);
			}
			UserVO userInfo = userInfoService.baseFind(user.getId());
			if(null != userInfo) {
				if(user.getNickname().matches(Constants.REGEX_USERNAME)) {
					params.put("nickname", userInfo.getNickname());
				}
				if(user.getUsername().matches(Constants.REGEX_USERNAME)) {
					params.put("username", userInfo.getUsername());
				}
				if(user.getName().matches(Constants.REGEX_USERNAME)) {
					params.put("name", userInfo.getName());
				}
			}
		}

		params.put("date", DateUtil.format("yyyyMMDD", new Date()));
		params.put("time", DateUtil.format("HHssmm", new Date()));
		params.put("datetime", DateUtil.format("yyyyMMDDHHssmm", new Date()));

		// 解密sk
		String sk = SecretUtil.aes128Decrypt(clientPhoneVO.getSk(), aesSecret);

		// 发送短信
		new PhoneSender().send(clientPhoneVO.getAk(), sk, phone, clientPhoneVO.getSign(), clientPhoneVO.getTmplate(), JSONObject.toJSONString(params));

		// 删除防机器人验证码
		if(VerifyUtils.isNotEmpty(verifyCode)) {
			try {
				validateService.delete(validateId, "sendPhone", null);
			} catch (Exception e){
				logger.error(e.getMessage());
			}
		}

		return new ReturnJson().success();
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

		// 用户id分隔符
		// clientId_phone/mail
		String userIdFlag = "_";

		if(isCheckUserExist && VerifyUtils.isNotEmpty(userId) && userId.indexOf(userIdFlag) == -1) {
			throw new ThrowPrompt("该" + text + "已存在！" , "072023");
		} else if(!isCheckUserExist && (VerifyUtils.isEmpty(userId) || userId.indexOf(userIdFlag) != -1)) {
			throw new ThrowPrompt("该" + text + "不存在！", "072024");
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
			throw new ThrowPrompt("无此用户！" ,"072025");
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
			throw new ThrowPrompt("用户信息错误！", "072026");
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
				validateService.delete(userId, null, code);
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 删除用户
	 * @param tokenVO
	 * @param id
     */
	@Transactional(rollbackFor = Exception.class)
	public void delete(TokenVO tokenVO, String id) {
		UserRecycleVO userRecycleVO = this.setRecycle(tokenVO, id);

		userRecycleService.baseInsert(userRecycleVO);
		super.baseDelete(id);
	}

	/**
	 * 批量删除用户
	 * @param tokenVO
	 * @param ids
     */
	@Transactional(rollbackFor = Exception.class)
	public void deleteBatch(TokenVO tokenVO, List<String> ids) {
		List<UserRecycleVO> recycles = new ArrayList<>();

		for(String id : ids) {
			recycles.add(this.setRecycle(tokenVO, id));
		}

		userRecycleService.baseInsertBatch(recycles);
		super.baseDeleteBatch(ids);
	}

	/**
	 * 设置回收数据
	 * @param tokenVO
	 * @param userId
     * @return
     */
	private UserRecycleVO setRecycle(TokenVO tokenVO, String userId) {
		if(VerifyUtils.isEmpty(userId)) {
			throw new ThrowPrompt("无此用户！", "072017");
		}

		UserVO userVO = super.baseFind(userId);
		if(null == userVO) {
			throw new ThrowPrompt("无此用户！", "072018");
		}
		if(!Constants.PROJECT_NAME.equals(tokenVO.getClientId())) {
			throw new ThrowPrompt("无权删除用户！", "072019");
		}
		if(!clientService.isMyClient(tokenVO.getUserId(), userVO.getClientId())) {
			throw new ThrowPrompt("无此用户！", "072020");
		}

		UserVO userInfo = userInfoService.baseFind(userId);

		//存入回收表中
		UserRecycleVO userRecycleVO = new UserRecycleVO();
		BeanUtils.copyProperties(userVO, userRecycleVO);
		if(null != userInfo) {
			BeanUtils.copyProperties(userInfo, userRecycleVO);
		}
		userRecycleVO.setOperatorTime(new Date());

		return userRecycleVO;
	}

	/**
	 * 按客户端查找用户
	 * @param userVO
	 * @return 用户列表
	 */
	public List<UserVO> queryByClient(UserVO userVO) {
		if(VerifyUtils.isEmpty(userVO.getCreatedBy())) {
			throw new ThrowPrompt("client创建人必传！", "142001");
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
			throw new ThrowPrompt("client创建人必传！", "142002");
		}
		return this.dao.queryByClientPage(userVO);
	}

	/**
	 * 修改
	 * @param tokenVO
	 * @param vo
     */
	@Transactional(rollbackFor = Exception.class)
	public void update(TokenVO tokenVO, UserVO vo) {
		if(!Constants.PROJECT_NAME.equals(tokenVO.getClientId()) && !tokenVO.getUserId().equals(vo.getId())) {
			throw new ThrowPrompt("用户信息错误！", "142003");
		}

		UserVO oldUserVO = super.baseFind(vo.getId());
		UserVO oldUserInfoVO = userInfoService.baseFind(vo.getId());

		if(VerifyUtils.isEmpty(vo.getClientId())) {
			vo.setClientId(oldUserVO.getClientId());
		}
		if(Constants.PROJECT_NAME.equals(tokenVO.getClientId()) && !clientService.isMyClient(tokenVO.getUserId(), vo.getClientId())) {
			throw new ThrowPrompt("client信息错误！", "142004");
		}

		if(null == oldUserVO) {
			throw new ThrowPrompt("无此用户！", "142005");
		}

		// 校验修改的信息是否已存在
		this.checkUserInfo(oldUserVO.getClientId(), oldUserVO.getId(), vo.getUsername(), vo.getMail(), vo.getPhone());

		// 实名认证通过 不能修改姓名 身份证号码
		if(oldUserInfoVO.getCertification() == Constants.USER_CERTIFICATION_SUCCESS
				&& VerifyUtils.isNotEmpty(vo.getName()) && !vo.getName().equals(oldUserInfoVO.getName())) {
			throw new ThrowPrompt("已实名通过，不能修改姓名！", "142006");
		}
		if(oldUserInfoVO.getCertification() == Constants.USER_CERTIFICATION_SUCCESS
				&& VerifyUtils.isNotEmpty(vo.getIdcard()) && !vo.getIdcard().equals(oldUserInfoVO.getIdcard())) {
			throw new ThrowPrompt("已实名通过，不能修改身份证号码！", "142007");
		}

		//设置user修改值
		UserVO upVO = this.setUpdateVlaue(oldUserVO, vo);

		//更新user
		if(null != upVO) {
			if(VerifyUtils.isEmpty(upVO.getUsername()) && VerifyUtils.isEmpty(upVO.getMail()) && VerifyUtils.isEmpty(upVO.getPhone())) {
				throw new ThrowPrompt("用户名、邮箱、手机必填一个！", "142008");
			}

			// 修改邮箱 改状态
			if(null != oldUserVO.getMail() && !oldUserVO.getMail().equalsIgnoreCase(vo.getMail()) && !VerifyUtils.isEmpty(vo.getMail())) {
				// 校验验证码
				if(VerifyUtils.isNotEmpty(vo.getCode())) {
					validateService.checkByMailOrPhone(oldUserVO.getClientId(), vo.getMail(), null, vo.getCode(), null);
				}

				if(upVO.getMailVerify() == 1 && VerifyUtils.isEmpty(vo.getCode())) {
					upVO.setMailVerify(0);
				}
			}

			// 修改手机 该状态
			if(null != oldUserVO.getPhone() && !oldUserVO.getPhone().equals(vo.getPhone()) && !VerifyUtils.isEmpty(vo.getPhone())) {
				// 校验验证码
				if(VerifyUtils.isNotEmpty(vo.getCode())) {
					validateService.checkByMailOrPhone(oldUserVO.getClientId(), null, vo.getPhone(), vo.getCode(), null);
				}

				if(upVO.getPhoneVerify() == 1 && VerifyUtils.isEmpty(vo.getCode())) {
					upVO.setPhoneVerify(0);
				}
			}

			super.baseUpdate(upVO);
		}

		//设置user_info修改值
		UserVO upInfoVO = this.setUpdateVlaue(oldUserInfoVO, vo);

		//更新user_info
		if(null != upInfoVO) {
			userInfoService.baseUpdate(upInfoVO);
		}
	}

	/**
	 * 设置修改的属性(不为null为修改)
	 * @param dbVO 库中最新bo
	 * @param upVO	修改的bo
	 * @return 修改后的bo 没修改数据返回null
	 */
	private UserVO setUpdateVlaue(UserVO dbVO, UserVO upVO) {
		if(null == dbVO) {
			return null;
		}

		boolean isUpdate = false;

		if(null != upVO.getId()) {
			dbVO.setId(upVO.getId());
			isUpdate = true;
		}
		//客户端id
		if(null != upVO.getClientId()) {
			dbVO.setClientId(upVO.getClientId());
			isUpdate = true;
		}
		//用户名
		if(null != upVO.getUsername()) {
			dbVO.setUsername(upVO.getUsername());
			isUpdate = true;
		}
		//邮箱
		if(null != upVO.getMail()) {
			dbVO.setMail(upVO.getMail());
			isUpdate = true;
		}
		//邮箱认证状态
		if(null != upVO.getMailVerify()) {
			dbVO.setMailVerify(upVO.getMailVerify());
			isUpdate = true;
		}
		//手机号码
		if(null != upVO.getPhone()) {
			dbVO.setPhone(upVO.getPhone());
			isUpdate = true;
		}
		//手机号码认证状态
		if(null != upVO.getPhoneVerify()) {
			dbVO.setPhoneVerify(upVO.getPhoneVerify());
			isUpdate = true;
		}
		//状态
		if(null != upVO.getStatus()) {
			dbVO.setStatus(upVO.getStatus());
			isUpdate = true;
		}


		if(null != upVO.getId()) {
			dbVO.setId(upVO.getId());
			isUpdate = true;
		}
		//昵称
		if(null != upVO.getNickname()) {
			dbVO.setNickname(upVO.getNickname());
			isUpdate = true;
		}
		//电话
		if(null != upVO.getTel()) {
			dbVO.setTel(upVO.getTel());
			isUpdate = true;
		}
		//QQ
		if(null != upVO.getQq()) {
			dbVO.setQq(upVO.getQq());
			isUpdate = true;
		}
		//微信
		if(null != upVO.getWeixin()) {
			dbVO.setWeixin(upVO.getWeixin());
			isUpdate = true;
		}
		//新浪微博
		if(null != upVO.getWeibo()) {
			dbVO.setWeibo(upVO.getWeibo());
			isUpdate = true;
		}
		//姓名
		if(null != upVO.getName()) {
			dbVO.setName(upVO.getName());
			isUpdate = true;
		}
		//性别 0女 1男
		if(null != upVO.getSex()) {
			dbVO.setSex(upVO.getSex());
			isUpdate = true;
		}
		//身份证号
		if(null != upVO.getIdcard()) {
			dbVO.setIdcard(upVO.getIdcard());
			isUpdate = true;
		}
		// 实名认证
		if(null != upVO.getCertification()) {
			dbVO.setCertification(upVO.getCertification());
			isUpdate = true;
		}
		//省
		if(null != upVO.getProvince()) {
			dbVO.setProvince(upVO.getProvince());
			isUpdate = true;
		}
		//市
		if(null != upVO.getCity()) {
			dbVO.setCity(upVO.getCity());
			isUpdate = true;
		}
		//区
		if(null != upVO.getArea()) {
			dbVO.setArea(upVO.getArea());
			isUpdate = true;
		}
		//地址
		if(null != upVO.getAddress()) {
			// 拼接详细地址
			String address = this.joinAddress(upVO);
			dbVO.setAddress(address);
			isUpdate = true;
		}
		// 来源
		if(null != upVO.getSource()) {
			dbVO.setSource(upVO.getSource());
			isUpdate = true;
		}

		if(!isUpdate) {
			return null;
		}

		return dbVO;
	}

	/**
	 * 拼接详细地址
	 * @param userVO
	 * @return
	 */
	private String joinAddress(UserVO userVO) {
		String address = VerifyUtils.isNotEmpty(userVO.getAddress())?userVO.getAddress():"";

		if(!VerifyUtils.isEmpty(userVO.getArea())) {
			CodeBO codeBO = codeService.baseFind(userVO.getArea());
			if(null != codeBO && !VerifyUtils.isEmpty(codeBO.getValue())) {
				address = codeBO.getValue() + "_" + address;
			}
		}
		if(!VerifyUtils.isEmpty(userVO.getCity())) {
			CodeBO codeBO = codeService.baseFind(userVO.getCity());
			if(null != codeBO && !VerifyUtils.isEmpty(codeBO.getValue())) {
				address = codeBO.getValue() + "_" + address;
			}
		}
		if(!VerifyUtils.isEmpty(userVO.getProvince())) {
			CodeBO codeBO = codeService.baseFind(userVO.getProvince());
			if(null != codeBO && !VerifyUtils.isEmpty(codeBO.getValue())) {
				address = codeBO.getValue() + "_" + address;
			}
		}

		return address;
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
				// 判断是否注册
				boolean isRegister = (VerifyUtils.isEmpty(nowUserId) && user.getUsername().equals(userName))
						|| (!VerifyUtils.isEmpty(nowUserId) && !user.getId().equals(nowUserId));
				if(isRegister) {
					throw new ThrowPrompt("该用户名已注册，请更换后再试！", "142010");
				}
			}
		}

		if(!VerifyUtils.isEmpty(mail)) {
			List<UserVO> users = this.dao.check(clientId, mail, null);
			for(UserVO user : users) {
				// 判断是否注册
				boolean isRegister = (VerifyUtils.isEmpty(nowUserId) && user.getMail().equals(mail))
						|| (!VerifyUtils.isEmpty(nowUserId) && !user.getId().equals(nowUserId));

				if(isRegister) {
					if(null ==  user.getMailVerify() || user.getMailVerify() == 0) {
						throw new ThrowPrompt("该邮箱已注册，但未验证！", "142011");
					} else {
						throw new ThrowPrompt("该邮箱已注册，请更换后再试！", "142012");
					}
				}
			}
		}
		if(!VerifyUtils.isEmpty(phone)) {
			List<UserVO> users = this.dao.check(clientId, phone, null);
			for(UserVO user : users) {
				// 判断是否注册
				boolean isRegister = (VerifyUtils.isEmpty(nowUserId) && user.getPhone().equals(phone))
						|| (!VerifyUtils.isEmpty(nowUserId) && !user.getId().equals(nowUserId));

				if(isRegister) {
					if(null == user.getPhoneVerify() || user.getPhoneVerify() == 0) {
						throw new ThrowPrompt("该手机号码已注册，但未验证！", "142013");
					} else {
						throw new ThrowPrompt("该手机号码已注册，请更换后再试！", "142014");
					}
				}
			}
		}
	}
}
