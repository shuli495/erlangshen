package com.website.controller;


import com.fastjavaframework.exception.ThrowException;
import com.fastjavaframework.util.VerifyUtils;
import com.website.common.BaseElsController;
import com.website.common.Constants;
import org.springframework.web.bind.annotation.*;

import com.website.model.vo.PermissionMenuVO;
import com.website.service.PermissionMenuService;

@CrossOrigin
@RestController
@RequestMapping(value= Constants.URL_PERMISSION_MENU)
public class PermissionMenuController extends BaseElsController<PermissionMenuService> {

	/**
	 * 创建
	 */
	@RequestMapping(method=RequestMethod.POST)
	public Object create(@RequestBody PermissionMenuVO vo) {
		if(VerifyUtils.isEmpty(vo.getClientId())) {
			throw new ThrowException("应用不能为空！");
		}
		if(VerifyUtils.isEmpty(vo.getName())) {
			throw new ThrowException("名称不能为空！");
		}
		if(VerifyUtils.isEmpty(vo.getType()) || vo.getType() > 1) {
			throw new ThrowException("类型错误！");
		}

		if(VerifyUtils.isEmpty(vo.getParentId())) {
			vo.setParentId(0);
		}

		this.service.insert(super.identity(), vo);
		return success(vo.getId());
	}

	/**
	 * 更新
	 */
	@RequestMapping(value="/{id}",method=RequestMethod.PUT)
	public Object update(@PathVariable Integer id, @RequestBody PermissionMenuVO vo) {
		if(VerifyUtils.isEmpty(vo.getName())) {
			throw new ThrowException("名称不能为空！");
		}

		vo.setId(id);
		this.service.update(super.identity(), vo);
		return success();
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
	public Object query(@RequestParam(required = false) String clientId, @RequestParam(required = false) Integer parentId,
						@RequestParam(required = false) String name, @RequestParam(required = false) String url,
						@RequestParam(required = false) String tag) {
		PermissionMenuVO vo = new PermissionMenuVO();
		if(VerifyUtils.isEmpty(clientId)) {
			throw new ThrowException("应用不能为空！");
		}

		vo.setClientId(clientId);
		vo.setOrderBy("parent_id");

		if(null != parentId) {
			vo.setParentId(parentId);
		}
		// 菜单名称
		if(null != name) {
			vo.setName(name);
		}
		// 菜单url
		if(null != url) {
			vo.setUrl(url);
		}
		// 菜单url
		if(null != tag) {
			vo.setTag(tag);
		}

		return success(this.service.query(super.identity(), vo));
	}

}