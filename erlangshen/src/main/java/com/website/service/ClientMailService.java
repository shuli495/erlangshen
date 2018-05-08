package com.website.service;

import com.fastjavaframework.exception.ThrowException;
import com.fastjavaframework.exception.ThrowPrompt;
import com.fastjavaframework.util.SecretUtil;
import com.fastjavaframework.util.VerifyUtils;
import com.website.common.Constants;
import com.website.model.vo.ClientSecurityVO;
import com.website.model.vo.TokenVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fastjavaframework.base.BaseService;
import com.website.dao.ClientMailDao;
import com.website.model.vo.ClientMailVO;

import java.util.List;

@Service
public class ClientMailService extends BaseService<ClientMailDao,ClientMailVO> {

	@Autowired
	private ClientService clientService;
	@Autowired
	private ClientSecurityService clientSecurityService;

	/**
	 * 根据clidentId、type查询
	 * @param clientId
	 * @param type
	 * @return
	 */
	public ClientMailVO find(String clientId, String type) {
		if(VerifyUtils.isEmpty(clientId)) {
			throw new ThrowException("clientId必填", "012001");
		}
		if(VerifyUtils.isEmpty(type)) {
			throw new ThrowException("type必填", "012002");
		}

		return this.dao.find(clientId, type);
	}

	/**
	 * 根据clientId删除
	 * @param clientId
	 */
	public void deleteByClientId(String clientId) {
		if(VerifyUtils.isEmpty(clientId)) {
			throw new ThrowException("clientId必填", "012003");
		}

		this.dao.deleteByClientId(clientId);
	}

	/**
	 * 添加应用邮箱信息
	 * @param clientMailVO
     */
	public void insert(TokenVO token, ClientMailVO clientMailVO) {
		if(!clientService.isMyClient(token.getUserId(), clientMailVO.getClientId())) {
			throw new ThrowPrompt("无权操作次应用！", "012004");
		}

		ClientMailVO queryClientMailVO = new ClientMailVO();
		queryClientMailVO.setClientId(clientMailVO.getClientId());
		queryClientMailVO.setType(clientMailVO.getType());
		List<ClientMailVO> clientMails = super.baseQueryByAnd(queryClientMailVO);
		if(clientMails.size() > 0) {
			throw new ThrowPrompt("该类型邮件已存在！", "012005");
		}

		// 加密密码
		if(VerifyUtils.isNotEmpty(clientMailVO.getPwd())) {
			clientMailVO.setPwd(SecretUtil.aes128Encrypt(clientMailVO.getPwd(), Constants.AES_128_SECRET));
		}

		super.baseInsert(clientMailVO);
	}

	/**
	 * 删除
	 * @param token
	 * @param id
     */
	public void delete(TokenVO token, Integer id) {
		ClientMailVO clientMailVO = super.baseFind(id);
		if(null == clientMailVO) {
			return;
		}
		if(!clientService.isMyClient(token.getUserId(), clientMailVO.getClientId())) {
			throw new ThrowPrompt("无权操作次应用！", "012006");
		}

		// 校验是否关联安全设置好
		ClientSecurityVO clientSecurityVO = new ClientSecurityVO();
		clientSecurityVO.setClientId(clientMailVO.getClientId());
		clientSecurityVO.setCheckPlaceMailTypeId(id);
		if(clientSecurityService.baseQueryByAnd(clientSecurityVO).size() > 0) {
			throw new ThrowPrompt("该邮件已关联安全设置，请取消关联后删除！", "012007");
		}

		super.baseDelete(id);
	}

	/**
	 * 列表查询
	 * @param token
	 * @param clientId
     * @return
     */
	public List<ClientMailVO> list(TokenVO token, String clientId) {
		if(!clientService.isMyClient(token.getUserId(), clientId)) {
			throw new ThrowPrompt("无权操作次应用！", "012008");
		}

		ClientMailVO queryClientMailVO = new ClientMailVO();
		queryClientMailVO.setClientId(clientId);
		return super.baseQueryByAnd(queryClientMailVO);
	}

}