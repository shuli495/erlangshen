package com.erlangshen.model.bo;

import com.fastjavaframework.base.BaseBean;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author https://github.com/shuli495/erlangshen
 */
public class ClientSecurityBO extends BaseBean {
	private static final long serialVersionUID = 1L;

	@NotNull(message="{client_security.clientId.null}")
	@Size(max=64, min=0, message="{client_security.clientId.size}")
	private String clientId;

	/**
	 * 是否异地登陆检查 0不检查 1检查
	 */
	private Boolean isCheckPlace;

	/**
	 * 通知优先级 0都通知 1手机优先 2邮件优先
	 */
	@Max(value=99999999, message="{client_security.checkPlacePriority.max}")
	private Integer checkPlacePriority;

	/**
	 * 异地登陆邮件通知类型
	 */
	@Size(max=10, min=0, message="{client_security.checkPlacePhoneTypeId.size}")
	private Integer checkPlacePhoneTypeId;

	/**
	 * 异地登陆手机通知类型
	 */
	@Size(max=10, min=0, message="{client_security.checkPlaceMailTypeId.size}")
	private Integer checkPlaceMailTypeId;

	/**
	 *  是否对登陆平台检查
	 *  0多平台多账号可同时登陆
	 *  1可以多平台登录，同一平台只能1个账号在线
	 *  2所有平台只能1个账号在线
	 */
	@Max(value=99999999, message="{client_security.isCheckPlatform.max}")
	private Integer isCheckPlatform;

	/**
	 * 登录冲突操作
	 * 0登出之前登陆的账号
	 * 1新登陆请求失败
	 */
	@Max(value=99999999, message="{client_security.checkPlatformType.max}")
	private Integer checkPlatformType;

	/**
	 * 登录通知接口
	 */
	@Size(max=255, min=0, message="{client_security.loginApi.size}")
	private String loginApi;


	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public Boolean getIsCheckPlace() {
		return isCheckPlace;
	}

	public void setIsCheckPlace(Boolean isCheckPlace) {
		this.isCheckPlace = isCheckPlace;
	}

	public Integer getCheckPlacePriority() {
		return checkPlacePriority;
	}

	public void setCheckPlacePriority(Integer checkPlacePriority) {
		this.checkPlacePriority = checkPlacePriority;
	}

	public Integer getCheckPlacePhoneTypeId() {
		return checkPlacePhoneTypeId;
	}

	public void setCheckPlacePhoneTypeId(Integer checkPlacePhoneTypeId) {
		this.checkPlacePhoneTypeId = checkPlacePhoneTypeId;
	}

	public Integer getCheckPlaceMailTypeId() {
		return checkPlaceMailTypeId;
	}

	public void setCheckPlaceMailTypeId(Integer checkPlaceMailTypeId) {
		this.checkPlaceMailTypeId = checkPlaceMailTypeId;
	}

	public Integer getIsCheckPlatform() {
		return isCheckPlatform;
	}

	public void setIsCheckPlatform(Integer isCheckPlatform) {
		this.isCheckPlatform = isCheckPlatform;
	}

	public Integer getCheckPlatformType() {
		return checkPlatformType;
	}

	public void setCheckPlatformType(Integer checkPlatformType) {
		this.checkPlatformType = checkPlatformType;
	}

	public String getLoginApi() {
		return loginApi;
	}

	public void setLoginApi(String loginApi) {
		this.loginApi = loginApi;
	}

	@Override
	public String toString() {
		return super.toString() + " ClientSecurityBO{\"clientId\": \""+clientId+"\", \"isCheckPlace\": \""+isCheckPlace+"\", "
				+ "\"checkPlacePriority\": \""+checkPlacePriority+"\", \"checkPlacePhoneTypeId\": \""+checkPlacePhoneTypeId+"\", "
				+ "\"checkPlaceMailTypeId\": \""+checkPlaceMailTypeId+"\", \"isCheckPlatform\": \""+isCheckPlatform+"\", "
				+ "\"checkPlatformType\": \""+checkPlatformType+"\", \"loginApi\": \""+loginApi+"\"}";
	}
}