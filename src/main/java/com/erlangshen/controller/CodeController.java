package com.erlangshen.controller;

import com.erlangshen.common.BaseElsController;
import com.erlangshen.common.Constants;
import com.erlangshen.model.bo.CodeBO;
import com.erlangshen.service.CodeService;
import com.erlangshen.service.ValidateService;
import com.fastjavaframework.annotation.Authority;
import com.fastjavaframework.exception.ThrowException;
import com.fastjavaframework.exception.ThrowPrompt;
import com.fastjavaframework.util.CommonUtil;
import com.fastjavaframework.util.UUID;
import com.fastjavaframework.util.VerifyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典表
 * @author https://github.com/shuli495/erlangshen
 */
@CrossOrigin
@RestController
@RequestMapping(value= Constants.URL_CODE)
public class CodeController extends BaseElsController<CodeService> {

	@Autowired
	private ValidateService validateService;

	/**
	 * 获取验证码
	 * @return
	 */
	@Authority(role = Constants.ADMIN_TOKEN)
	@RequestMapping(value="/verify", method=RequestMethod.GET)
	public Object code(@RequestParam(required = false) String loginIp, @RequestParam(required = true) String type) {
		if(Constants.IDENTITY_TYPE_KEY.equals(super.identity().getAuthenticationMethod()) && VerifyUtils.isEmpty(loginIp)) {
			throw new ThrowException("AK/SK方式loginIp参数必传！", "071001");
		}

		if(VerifyUtils.isEmpty(loginIp)) {
			loginIp = CommonUtil.getIp(super.request);
		}

		if(!Constants.CODE_TYPE_LOGIN.equals(type) && !Constants.CODE_TYPE_REGISTER.equals(type)) {
			throw new ThrowException("类型只能是login或register！", "071003");
		}

		return success(validateService.verifyCode(type, super.identity().getClientId(), loginIp));
	}

	/**
	 * 创建
	 */
	@RequestMapping(method=RequestMethod.POST)
	public Object create(@RequestBody CodeBO bo) {
		if(VerifyUtils.isEmpty(bo.getId())) {
			bo.setId(UUID.uuid());
		}

		this.service.insert(bo);
		return success(bo.getId());
	}

	/**
	 * 批量创建
	 */
	@RequestMapping(value="/batch",method=RequestMethod.POST)
	public Object createBatch(@RequestBody List<CodeBO> boList) {
		if(boList.size() == 0) {
			throw new ThrowPrompt("无创建内容！", "021001");
		}

		for(CodeBO bo : boList) {
		bo.setId(UUID.uuid());
		}

		this.service.baseInsertBatch(boList);
		return success();
	}

	/**
	 * 更新
	 */
	@RequestMapping(method=RequestMethod.PUT)
	public Object update(@RequestBody CodeBO bo) {
		this.service.baseUpdate(bo);
		return success();
	}

	/**
	 * 批量更新
	 */
	@RequestMapping(value="/batch",method=RequestMethod.PUT)
	public Object updateBatch(@RequestBody List<CodeBO> boList) {
		if(boList.size() == 0) {
			throw new ThrowPrompt("无修改内容！", "021002");
		}

		this.service.baseUpdateBatch(boList);
		return success();
	}

	/**
	 * 物理删除
	 */
	@RequestMapping(value="/{id}",method=RequestMethod.DELETE)
	public Object delete(@PathVariable String id) {
		this.service.delete(id);
		return success();
	}

	/**
	 * 批量物理删除
	 */
	@RequestMapping(value="/batch",method=RequestMethod.DELETE)
	public Object deleteBatch(@RequestBody List<String> idList) {
		if(idList.size() == 0) {
			throw new ThrowPrompt("无删除内容！", "021003");
		}

		this.service.baseDeleteBatch(idList);
		return success();
	}

	/**
	 * id查询详情
	 */
	@RequestMapping(value="/{id}",method=RequestMethod.GET)
	public Object findById(@PathVariable String id) {
		return success(this.service.baseFind(id));
	}

	/**
	 * 列表查询 and条件
	 */
	@RequestMapping(method=RequestMethod.GET)
	public Object query(@RequestParam(value="parentId", required=false) String parentId,
						@RequestParam(value="groupId", required=false) String groupId) {
		CodeBO bo = new CodeBO();
		if(!VerifyUtils.isEmpty(parentId)) {
			bo.setParentId(parentId);
		}
		if(!VerifyUtils.isEmpty(groupId)) {
			bo.setGroupId(groupId);
		}
		
		return success(this.service.baseQueryByAnd(bo));
	}

}