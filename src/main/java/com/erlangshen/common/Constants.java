package com.erlangshen.common;

/**
 * @author https://github.com/shuli495/erlangshen
 */
public class Constants {

	public static final String PROJECT_NAME = "erlangshen";

	public static final String URL_CLIENT = "/client";
	public static final String URL_CODE = "/code";
	public static final String URL_KEY = "/key";
	public static final String URL_LOGIN_LOG = "/loginLog";
	public static final String URL_PERMISSION_MENU = "/permissionMenu";
	public static final String URL_PERMISSION_ROLE = "/permissionRole";
	public static final String URL_TOKEN = "/token";
	public static final String URL_USER = "/user";

	public static final String URL_USER_SENDMAIL = "/sendMail";
	public static final String URL_USER_SENDPHONE = "/sendPhone";
	public static final String URL_USER_CHECKCODE = "/checkCode";
	public static final String URL_USER_CHECKMAIL = "/checkMail";

	public static final String ADMIN_TOKEN = "adminToken";

	/**
	 * 接口身份认证 认证类型
	 * key as、sk； token
	 */
	public static final String IDENTITY_TYPE_KEY = "KEY";
	public static final String IDENTITY_TYPE_TOKEN = "TOKEN";

	public static final String REGEX_USERNAME = "([a-z]|[A-Z]|[0-9]){1,}";

	public static final String TEMPLATE_MAIL_CODE = "${code";
	public static final String TEMPLATE_MAIL_URL = "${url}";
	public static final String TEMPLATE_MAIL_DATE = "${date}";
	public static final String TEMPLATE_MAIL_TIME = "${time}";
	public static final String TEMPLATE_MAIL_DATETIME = "${datetime}";
	public static final String TEMPLATE_MAIL_MAIL = "${mail}";
	public static final String TEMPLATE_MAIL_PHONE = "${phone}";
	public static final String TEMPLATE_MAIL_NICKNAME = "${nickname}";
	public static final String TEMPLATE_MAIL_USERNAME = "${username}";
	public static final String TEMPLATE_MAIL_NAME = "${name}";

	/**
	 * 接口最大qps
	 */
	public static final int QPS_MAX_NUM = 2;
	public static final String QPS_BAIDU_YUN = "bdyun";
	public static final String QPS_BAIDU_MAP = "bdmap";

	/**
	 * 百度识图返回字段
	 */
	public static final String BAIDUYUN_OCR_KEY_NAME = "姓名";
	public static final String BAIDUYUN_OCR_KEY_WORDS = "words";
	public static final String BAIDUYUN_OCR_KEY_IDCARD = "公民身份号码";
	public static final String BAIDUYUN_OCR_KEY_EDIT_TOOL = "edit_tool";

	/**
	 * 百度地图，根据ip获取地址接口返回字段
	 */
	public static final String BAIDUMAP_IP_KEY_CONTENT = "content";
	public static final String BAIDUMAP_IP_KEY_ADDRESS_DETAIL = "address_detail";
	public static final String BAIDUMAP_IP_KEY_CITY = "city";
	public static final String BAIDUMAP_IP_KEY_STATUS = "status";

	/**
	 * 阿里云，发送短信接口返回字段
	 */
	public static final String ALIYUN_SMS_KEY_OK = "OK";
	public static final String ALIYUN_SMS_KEY_ERR_LIMIT = "isv.BUSINESS_LIMIT_CONTROL";

	/**
	 * 用户表
	 * 认证状态
	 * 0未认证 1认证中 2认证失败 3认证完成
	 */
	public static final int USER_CERTIFICATION_NO = 0;
	public static final int USER_CERTIFICATION_ING = 1;
	public static final int USER_CERTIFICATION_FAIL = 2;
	public static final int USER_CERTIFICATION_SUCCESS = 3;

	/**
	 * 用户表
	 * 邮箱认证状态
	 * 0未认证 1已认证
	 */
	public static final int USER_MAIL_VERIFY_SUCCESS = 1;

	/**
	 * 发送验证码类型
	 * login登录验证码 register注册验证码
	 */
	public static final String CODE_TYPE_LOGIN = "login";
	public static final String CODE_TYPE_REGISTER = "register";

	/**
	 * 发送邮件类型
	 * mail注册链接 mailCode注册验证码 repwd重置密码
	 */
	public static final String SEND_MAIL_TYPE_MAIL = "mail";

	/**
	 * 邮件校验url参数
	 */
	public static final String CHECK_MAIL_PARAMS_USER_ID = "userId=";
	public static final String CHECK_MAIL_PARAMS_CODE = "code=";
	public static final String CHECK_MAIL_PARAMS_CALLBACK = "callback=";

	/**
	 * 身份证上传最大大小
	 */
	public static final long IDCARD_UP_MAX = 4194304;
}
