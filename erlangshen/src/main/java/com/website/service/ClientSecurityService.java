package com.website.service;

import com.fastjavaframework.exception.ThrowPrompt;
import com.website.model.vo.TokenVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fastjavaframework.base.BaseService;
import com.website.dao.ClientSecurityDao;
import com.website.model.vo.ClientSecurityVO;

@Service
public class ClientSecurityService extends BaseService<ClientSecurityDao,ClientSecurityVO> {

	@Autowired
	private ClientService clientService;

	/**
	 * 详情查询
	 * @param token
	 * @param clientId
	 * @return
	 */
	public ClientSecurityVO find(TokenVO token, String clientId) {
		if(!clientService.isMyClient(token.getUserId(), clientId)) {
			throw new ThrowPrompt("无权操作次应用！", "032001");
		}

		return super.baseFind(clientId);
	}

	/**
	 * 更新
	 */
	public int update(TokenVO token, ClientSecurityVO vo) {
		if(!clientService.isMyClient(token.getUserId(), vo.getClientId())) {
			throw new ThrowPrompt("无权操作次应用！", "032002");
		}

		//设置修改值
		ClientSecurityVO upVO = this.setUpdateVlaue(super.baseFind(vo.getClientId()), vo);

		//更新
		return super.baseUpdate(upVO);
	}

	/**
	 * 设置修改的属性(不为null为修改)
	 * @param dbVO 库中最新vo
	 * @param upVO	修改的vo
	 * @return 修改后的vo
	 */
	private ClientSecurityVO setUpdateVlaue(ClientSecurityVO dbVO, ClientSecurityVO upVO) {
		if(null == dbVO) {
			throw new ThrowPrompt("无"+upVO.getClientId()+"信息！", "032003");
		}

		// 是否异地登陆检查 0不检查 1检查
		if(null != upVO.getIsCheckPlace()) {
			dbVO.setIsCheckPlace(upVO.getIsCheckPlace());
		}
		// 通知优先级 0都通知 1手机优先 2邮件优先
		if(null != upVO.getCheckPlacePriority()) {
			dbVO.setCheckPlacePriority(upVO.getCheckPlacePriority());
		}
		// 异地登陆邮件通知类型
		if(null != upVO.getCheckPlacePhoneTypeId()) {
			dbVO.setCheckPlacePhoneTypeId(upVO.getCheckPlacePhoneTypeId());
		}
		// 异地登陆手机通知类型
		if(null != upVO.getCheckPlaceMailTypeId()) {
			dbVO.setCheckPlaceMailTypeId(upVO.getCheckPlaceMailTypeId());
		}
		// 是否对登陆平台检查 0多平台多账号可同时登陆 1可以多平台登录，同一平台只能1个账号在线 2所有平台只能1个账号在线
		if(null != upVO.getIsCheckPlatform()) {
			dbVO.setIsCheckPlatform(upVO.getIsCheckPlatform());
		}
		// 登录冲突操作 0登出之前登陆的账号 1新登陆请求失败
		if(null != upVO.getCheckPlatformType()) {
			dbVO.setCheckPlatformType(upVO.getCheckPlatformType());
		}
		// 登录通知接口
		if(null != upVO.getLoginApi()) {
			dbVO.setLoginApi(upVO.getLoginApi());
		}
		return dbVO;
	}

}