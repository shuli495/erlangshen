package com.erlangshen.model.vo;

import com.alibaba.fastjson.JSONArray;
import com.erlangshen.model.bo.ClientBO;

import java.util.List;

/**
 * @author https://github.com/shuli495/erlangshen
 */
public class ClientVO extends ClientBO {
	private static final long serialVersionUID = 1L;

	/**
	 * 已设置邮箱数量
	 */
	private Integer setMailNum;

	/**
	 * 已设置手机信息数量
	 */
	private Integer setPhoneNum;

	/**
	 * 用户列表
	 */
	private List<UserVO> users;

	/**
	 * 已删除用户列表
	 */
	private List<UserRecycleVO> delUsers;

	public int getSetMailNum() {
		return setMailNum;
	}

	public void setSetMailNum(int setMailNum) {
		this.setMailNum = setMailNum;
	}

	public int getSetPhoneNum() {
		return setPhoneNum;
	}

	public void setSetPhoneNum(int setPhoneNum) {
		this.setPhoneNum = setPhoneNum;
	}

	public List<UserVO> getUsers() {
		return users;
	}

	public void setUsers(List<UserVO> users) {
		this.users = users;
	}

	public List<UserRecycleVO> getDelUsers() {
		return delUsers;
	}

	public void setDelUsers(List<UserRecycleVO> delUsers) {
		this.delUsers = delUsers;
	}


	@Override
	public String toString() {
		return super.toString() + " ClientVO{\"setMailNum\": \""+setMailNum+"\", \"setPhoneNum\": \""+setPhoneNum+"\", "
				+ "\"users\": \""+ JSONArray.toJSONString(users) +"\", "
				+ "\"delUsers\": \""+JSONArray.toJSONString(delUsers)+"\"}";
	}
}