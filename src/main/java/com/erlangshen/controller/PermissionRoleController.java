package com.erlangshen.controller;


import com.erlangshen.common.BaseElsController;
import com.erlangshen.common.Constants;
import com.erlangshen.model.vo.PermissionRoleVO;
import com.erlangshen.service.PermissionRoleService;
import com.fastjavaframework.util.VerifyUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * 权限规则
 * @author https://github.com/shuli495/erlangshen
 */
@CrossOrigin
@RestController
@RequestMapping(value= Constants.URL_PERMISSION_ROLE)
public class PermissionRoleController extends BaseElsController<PermissionRoleService> {

	/**
	 * 创建
	 */
	@RequestMapping(method=RequestMethod.POST)
	public Object create(@RequestBody PermissionRoleVO vo) {
		if(VerifyUtils.isEmpty(vo.getClientId())) {
			vo.setClientId(super.identity().getClientId());
		}

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
			clientId = super.identity().getClientId();
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
	 * 查询用户权限
	 * @param id userId
	 * @return
	 */
	@RequestMapping(value="/user/{id}",method=RequestMethod.GET)
	public Object queryByUser(@PathVariable String id) {
		return success(this.service.queryByUser(super.identity(), id));
	}

	/**
	 * 用户关联角色
	 * @param id userId
	 * @param vo
	 * @return
	 */
	@RequestMapping(value="/user/{id}",method=RequestMethod.POST)
	public Object insertByUser(@PathVariable String id, @RequestBody PermissionRoleVO vo) {
		if(vo.getRoles().size() > 0) {
			this.service.userInsert(super.identity(), vo.getRoles(), Arrays.asList(id));
		}
		return success();
	}

	/**
	 * 用户取消关联角色
	 * @param id userId
	 * @param vo
	 * @return
	 */
	@RequestMapping(value="/user/{id}",method=RequestMethod.DELETE)
	public Object deleteByUser(@PathVariable String id, @RequestBody PermissionRoleVO vo) {
		if(vo.getRoles().size() > 0) {
			this.service.userDelete(super.identity(), vo.getRoles(), Arrays.asList(id));
		}
		return success();
	}

	/**
	 * 角色关联用户
	 * @param id roleId
	 * @param vo
     * @return
     */
	@RequestMapping(value="/{id}/user",method=RequestMethod.POST)
	public Object userInsert(@PathVariable Integer id, @RequestBody PermissionRoleVO vo) {
		if(vo.getUsers().size() > 0) {
			this.service.userInsert(super.identity(), Arrays.asList(id), vo.getUsers());
		}
		return success();
	}

	/**
	 * 角色取消用户关联
	 * @param id roleId
	 * @param vo
     * @return
     */
	@RequestMapping(value="/{id}/user",method=RequestMethod.DELETE)
	public Object userDelete(@PathVariable Integer id, @RequestBody PermissionRoleVO vo) {
		if(vo.getUsers().size() > 0) {
			this.service.userDelete(super.identity(), Arrays.asList(id), vo.getUsers());
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