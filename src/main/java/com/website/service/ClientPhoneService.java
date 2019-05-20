package com.website.service;

import com.fastjavaframework.exception.ThrowException;
import com.fastjavaframework.exception.ThrowPrompt;
import com.fastjavaframework.util.SecretUtil;
import com.fastjavaframework.util.VerifyUtils;
import com.website.model.vo.ClientSecurityVO;
import com.website.model.vo.TokenVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.fastjavaframework.base.BaseService;
import com.website.dao.ClientPhoneDao;
import com.website.model.vo.ClientPhoneVO;

import java.util.List;

/**
 * 应用发送短信平台
 * @author https://github.com/shuli495/erlangshen
 */
@Service
public class ClientPhoneService extends BaseService<ClientPhoneDao,ClientPhoneVO> {

	@Autowired
	private ClientService clientService;
	@Autowired
	private ClientSecurityService clientSecurityService;

	@Value("${aes.secret}")
	private String aesSecret;

	/**
	 * 根据clidentId、type查询
	 * @param clientId
	 * @param type
	 * @return
	 */
	public ClientPhoneVO find(String clientId, String type) {

		if(VerifyUtils.isEmpty(clientId)) {
			throw new ThrowException("clientId必填", "022001");
		}
		if(VerifyUtils.isEmpty(type)) {
			throw new ThrowException("type必填", "022002");
		}

		return this.dao.find(clientId, type);
	}


	/**
	 * 根据clientId删除
	 * @param clientId
	 */
	public void deleteByClientId(String clientId) {
		if(VerifyUtils.isEmpty(clientId)) {
			throw new ThrowException("clientId必填", "022003");
		}

		this.dao.deleteByClientId(clientId);
	}

	/**
	 * 添加应用短信发送平台信息
	 * @param clientPhoneVO
     */
	public void insert(TokenVO token, ClientPhoneVO clientPhoneVO) {
		if(!clientService.isMyClient(token.getUserId(), clientPhoneVO.getClientId())) {
			throw new ThrowPrompt("无权操作次应用！", "022004");
		}

		ClientPhoneVO queryClientPhoneVO = new ClientPhoneVO();
		queryClientPhoneVO.setClientId(clientPhoneVO.getClientId());
		queryClientPhoneVO.setType(clientPhoneVO.getType());
		List<ClientPhoneVO> clientPhones = super.baseQueryByAnd(queryClientPhoneVO);
		if(clientPhones.size() > 0) {
			throw new ThrowPrompt("该类型短信已存在！", "022005");
		}

		// 加密sk
		if(VerifyUtils.isNotEmpty(clientPhoneVO.getSk())) {
			clientPhoneVO.setSk(SecretUtil.aes128Encrypt(clientPhoneVO.getSk(), aesSecret));
		}

		super.baseInsert(clientPhoneVO);
	}

	/**
	 * 删除
	 * @param token
	 * @param id
     */
	public void delete(TokenVO token, Integer id) {
		ClientPhoneVO clientPhoneVO = super.baseFind(id);
		if(null == clientPhoneVO) {
			return;
		}

		if(!clientService.isMyClient(token.getUserId(), clientPhoneVO.getClientId())) {
			throw new ThrowPrompt("无权操作次应用！", "022006");
		}

		// 校验是否关联安全设置好
		ClientSecurityVO clientSecurityVO = new ClientSecurityVO();
		clientSecurityVO.setClientId(clientPhoneVO.getClientId());
		clientSecurityVO.setCheckPlaceMailTypeId(id);
		if(clientSecurityService.baseQueryByAnd(clientSecurityVO).size() > 0) {
			throw new ThrowPrompt("该邮件已关联安全设置，请取消关联后删除！", "022007");
		}


		super.baseDelete(id);
	}

	/**
	 * 列表查询
	 * @param token
	 * @param clientId
     * @return
     */
	public List<ClientPhoneVO> list(TokenVO token, String clientId) {
		if(!clientService.isMyClient(token.getUserId(), clientId)) {
			throw new ThrowPrompt("无权操作次应用！", "022008");
		}

		ClientPhoneVO queryClientPhoneVO = new ClientPhoneVO();
		queryClientPhoneVO.setClientId(clientId);
		return super.baseQueryByAnd(queryClientPhoneVO);
	}
}