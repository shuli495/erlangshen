package com.website.controller;

import java.util.Arrays;
import java.util.List;

import com.fastjavaframework.page.Page;
import com.fastjavaframework.util.VerifyUtils;
import com.website.common.BaseElsController;
import com.website.common.Constants;
import com.website.model.vo.ClientMailVO;
import com.website.model.vo.ClientPhoneVO;
import com.website.model.vo.ClientSecurityVO;
import com.website.model.vo.ClientVO;
import com.website.service.ClientMailService;
import com.website.service.ClientPhoneService;
import com.website.service.ClientSecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.fastjavaframework.exception.ThrowPrompt;
import com.website.service.ClientService;

@CrossOrigin
@RestController
@RequestMapping(value= Constants.URL_CLIENT)
public class ClientController extends BaseElsController<ClientService> {

	@Autowired
	private ClientMailService clientMailService;
	@Autowired
	private ClientPhoneService clientPhoneService;
	@Autowired
	private ClientSecurityService clientSecurityService;

	/**
	 * 创建
	 */
	@RequestMapping(method=RequestMethod.POST)
	public Object create(@RequestBody ClientVO vo) {
		vo.setCreatedBy(super.identity().getUserId());
		this.service.inster(vo);
		return success(vo.getId());
	}

	/**
	 * 修改
	 */
	@RequestMapping(value="/{id}",method=RequestMethod.PUT)
	public Object update(@PathVariable String id, @RequestBody ClientVO vo) {
		vo.setId(id);
		this.service.update(super.identity().getUserId(), vo);
		return success(vo.getId());
	}

	/**
	 * 删除
	 */
	@RequestMapping(value="/{id}",method=RequestMethod.DELETE)
	public Object delete(@PathVariable String id) {
		this.service.delete(super.identity(), Arrays.asList(id));
		return success();
	}

	/**
	 * 批量删除
	 */
	@RequestMapping(method=RequestMethod.DELETE)
	public Object deleteBatch(@RequestBody List<String> idList) {
		if(idList.size() == 0) {
			throw new ThrowPrompt("无删除内容！", "011001");
		}

		this.service.delete(super.identity(), idList);
		return success();
	}

	/**
	 * id查询详情
	 */
	@RequestMapping(value="/{id}",method=RequestMethod.GET)
	public Object findById(@PathVariable String id) {
		return success(this.service.find(super.identity().getUserId(), id));
	}

	/**
	 * 列表查询 and条件
	 */
	@RequestMapping(method=RequestMethod.GET)
	public Object query(@RequestParam(required = false) Integer pageSize, @RequestParam(required = false) Integer pageNum,
						@RequestParam(required = false) String name) {
		ClientVO vo = new ClientVO();
		vo.setCreatedBy(super.identity().getUserId());
		vo.setDeleted(false);

		//client名称
		if(!VerifyUtils.isEmpty(name)) {
			vo.setName(name);
		}

		vo.setOrder(Arrays.asList("`NAME`"));

		if(pageSize != null && pageNum != null && pageSize != 0 && pageNum != 0) {
			Page page = new Page();
			page.setPageSize(pageSize);
			page.setPageNum(pageNum);
			vo.setPage(page);

			return success(this.service.baseQueryPageByAnd(vo));
		} else {
			return success(this.service.baseQueryByAnd(vo));
		}
	}

	/**
	 * 添加应用邮箱信息
	 * @param clientMailVO
	 * @return
     */
	@RequestMapping(value="/mail",method=RequestMethod.POST)
	public Object mailInsert(@RequestBody ClientMailVO clientMailVO) {
		if(VerifyUtils.isEmpty(clientMailVO.getClientId())) {
			throw new ThrowPrompt("应用不能为空！", "011002");
		}
		if(VerifyUtils.isEmpty(clientMailVO.getMail())) {
			throw new ThrowPrompt("邮箱不能为空！", "011003");
		}
		if(VerifyUtils.isEmpty(clientMailVO.getPwd())) {
			throw new ThrowPrompt("邮箱密码不能为空！", "011004");
		}
		if(VerifyUtils.isEmpty(clientMailVO.getType())) {
			throw new ThrowPrompt("类型不能为空！", "011005");
		}
		if(VerifyUtils.isEmpty(clientMailVO.getText())) {
			throw new ThrowPrompt("发送内容不能为空！", "011006");
		}

		clientMailService.insert(super.identity(), clientMailVO);
		return success();
	}

	/**
	 * 删除应用邮箱信息
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/mail/{id}",method=RequestMethod.DELETE)
	public Object mailDel(@PathVariable Integer id) {
		clientMailService.delete(super.identity(), id);
		return success();
	}

	/**
	 * 查询应用邮箱信息
	 * @param clientId
	 * @return
	 */
	@RequestMapping(value="/mail/{clientId}",method=RequestMethod.GET)
	public Object maillist(@PathVariable String clientId) {
		if(VerifyUtils.isEmpty(clientId)) {
			throw new ThrowPrompt("应用不能为空！", "011007");
		}

		return success(clientMailService.list(super.identity(), clientId));
	}

	/**
	 * 添加应用短信发送平台信息
	 * @param clientPhoneVO
	 * @return
	 */
	@RequestMapping(value="/phone",method=RequestMethod.POST)
	public Object phoneInsert(@RequestBody ClientPhoneVO clientPhoneVO) {
		if(VerifyUtils.isEmpty(clientPhoneVO.getClientId())) {
			throw new ThrowPrompt("应用不能为空！", "011008");
		}
		if(VerifyUtils.isEmpty(clientPhoneVO.getPlatform())) {
			throw new ThrowPrompt("短信平台不能为空！", "011009");
		}
		if(VerifyUtils.isEmpty(clientPhoneVO.getAk())) {
			throw new ThrowPrompt("AK不能为空！", "011010");
		}
		if(VerifyUtils.isEmpty(clientPhoneVO.getSk())) {
			throw new ThrowPrompt("SK不能为空！", "011011");
		}
		if(VerifyUtils.isEmpty(clientPhoneVO.getSign())) {
			throw new ThrowPrompt("签名不能为空！", "011012");
		}
		if(VerifyUtils.isEmpty(clientPhoneVO.getTmplate())) {
			throw new ThrowPrompt("模板不能为空！", "011013");
		}
		if(VerifyUtils.isEmpty(clientPhoneVO.getType())) {
			throw new ThrowPrompt("类型不能为空！", "011014");
		}

		clientPhoneService.insert(super.identity(), clientPhoneVO);
		return success();
	}

	/**
	 * 删除应用短信发送平台信息
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/phone/{id}",method=RequestMethod.DELETE)
	public Object phoneDel(@PathVariable Integer id) {
		clientPhoneService.delete(super.identity(), id);
		return success();
	}

	/**
	 * 查询应用短信发送平台信息
	 * @param clientId
	 * @return
	 */
	@RequestMapping(value="/phone/{clientId}",method=RequestMethod.GET)
	public Object phonelist(@PathVariable String clientId) {
		if(VerifyUtils.isEmpty(clientId)) {
			throw new ThrowPrompt("应用不能为空！", "011015");
		}

		return success(clientPhoneService.list(super.identity(), clientId));
	}

	/**
	 * 应用安全设置查询
	 * @param clientId
	 * @return
	 */
	@RequestMapping(value="/security/{clientId}",method=RequestMethod.GET)
	public Object security(@PathVariable String clientId) {
		if(VerifyUtils.isEmpty(clientId)) {
			throw new ThrowPrompt("应用不能为空！", "011016");
		}

		return success(clientSecurityService.find(super.identity(), clientId));
	}

	/**
	 * 应用安全设置更新
	 * @param clientId
	 * @return
	 */
	@RequestMapping(value="/security/{clientId}",method=RequestMethod.PUT)
	public Object securityUpdate(@PathVariable String clientId, @RequestBody ClientSecurityVO vo) {
		if(VerifyUtils.isEmpty(clientId)) {
			throw new ThrowPrompt("应用不能为空！", "011017");
		}

		vo.setClientId(clientId);

		return success(clientSecurityService.update(super.identity(), vo));
	}

}