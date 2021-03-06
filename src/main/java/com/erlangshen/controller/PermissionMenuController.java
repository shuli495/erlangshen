package com.erlangshen.controller;


import com.erlangshen.common.BaseElsController;
import com.erlangshen.common.Constants;
import com.erlangshen.model.vo.PermissionMenuVO;
import com.erlangshen.service.PermissionMenuService;
import com.fastjavaframework.exception.ThrowPrompt;
import com.fastjavaframework.util.VerifyUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 菜单权限
 * @author https://github.com/shuli495/erlangshen
 */
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
			throw new ThrowPrompt("应用不能为空！", "051001");
		}
		if(VerifyUtils.isEmpty(vo.getName())) {
			throw new ThrowPrompt("名称不能为空！", "051002");
		}
		if(VerifyUtils.isEmpty(vo.getType()) || vo.getType() > 1) {
			throw new ThrowPrompt("类型错误！", "051003");
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
			throw new ThrowPrompt("名称不能为空！", "051004");
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
			throw new ThrowPrompt("应用不能为空！", "051005");
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