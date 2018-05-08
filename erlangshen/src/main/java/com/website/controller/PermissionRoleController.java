package com.website.controller;


import com.fastjavaframework.exception.ThrowPrompt;
import com.fastjavaframework.util.VerifyUtils;
import com.website.common.BaseElsController;
import com.website.common.Constants;
import org.springframework.web.bind.annotation.*;

import com.website.model.vo.PermissionRoleVO;
import com.website.service.PermissionRoleService;

@CrossOrigin
@RestController
@RequestMapping(value= Constants.URL_PERMISSION_ROLE)
public class PermissionRoleController extends BaseElsController<PermissionRoleService> {

	/**
	 * 创建
	 */
	@RequestMapping(method=RequestMethod.POST)
	public Object create(@RequestBody PermissionRoleVO vo) {
		this.service.insert(super.identity(), vo);
		return success(vo.getId());
	}

	/**
	 * 物理删除
	 */
	@RequestMapping(value="/{id}",method=RequestMethod.DELETE)
	public Object delete(@PathVariable Integer id) {
		this.service.delete(super.identity(), id);
		return success();
	}

	/**
	 * 列表查询 and条件
	 */
	@RequestMapping(method=RequestMethod.GET)
	public Object query(@RequestParam(required = false) String orderBy, @RequestParam(required = false) String orderSort,
						@RequestParam(required = false) String clientId, @RequestParam(required = false) String role) {
		PermissionRoleVO vo = new PermissionRoleVO();
		if(VerifyUtils.isEmpty(clientId)) {
			throw new ThrowPrompt("应用不能为空！", "061001");
		}

		vo.setClientId(clientId);

		// 角色
		if(null != role) {
			vo.setRole(role);
		}
		// 排序
		if(null != orderBy) {
			vo.setOrderBy(orderBy);
		}
		if(null != orderSort) {
			vo.setOrderSort(orderSort);
		}

		return success(this.service.query(super.identity(), vo));
	}

	/**
	 * 关联用户
	 * @param id
	 * @param vo
     * @return
     */
	@RequestMapping(value="/user/{id}",method=RequestMethod.POST)
	public Object userInsert(@PathVariable Integer id, @RequestBody PermissionRoleVO vo) {
		if(vo.getUsers().size() > 0) {
			this.service.userInsert(super.identity(), id, vo.getUsers());
		}
		return success();
	}

	/**
	 * 取消用户关联
	 * @param id
	 * @param vo
     * @return
     */
	@RequestMapping(value="/user/{id}",method=RequestMethod.DELETE)
	public Object userDelete(@PathVariable Integer id, @RequestBody PermissionRoleVO vo) {
		if(vo.getUsers().size() > 0) {
			this.service.userDelete(super.identity(), id, vo.getUsers());
		}
		return success();
	}

	/**
	 * 关联功能
	 * @param id
	 * @param vo
     * @return
     */
	@RequestMapping(value="/menu/{id}",method=RequestMethod.POST)
	public Object functionInsert(@PathVariable Integer id, @RequestBody PermissionRoleVO vo) {
		if(vo.getMenus().size() > 0) {
			this.service.menuInsert(super.identity(), id, vo.getMenus());
		}
		return success();
	}

	/**
	 * 取消功能关联
	 * @param id
	 * @param vo
     * @return
     */
	@RequestMapping(value="/menu/{id}",method=RequestMethod.DELETE)
	public Object functionDelete(@PathVariable Integer id, @RequestBody PermissionRoleVO vo) {
		if(vo.getMenus().size() > 0) {
			this.service.menuDelete(super.identity(), id, vo.getMenus());
		}
		return success();
	}

}