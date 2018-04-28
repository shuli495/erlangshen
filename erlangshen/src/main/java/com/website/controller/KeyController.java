package com.website.controller;

import com.fastjavaframework.exception.ThrowException;
import com.website.common.BaseElsController;
import com.website.common.Constants;
import org.springframework.web.bind.annotation.*;

import com.fastjavaframework.page.Page;
import com.website.model.vo.KeyVO;
import com.website.service.KeyService;

@CrossOrigin
@RestController
@RequestMapping(value= Constants.URL_KEY)
public class KeyController extends BaseElsController<KeyService> {

	/**
	 * 创建
	 */
	@RequestMapping(value="/{clientId}",method=RequestMethod.POST)
	public Object create(@PathVariable String clientId) {
		if(null == clientId) {
			throw new ThrowException("应用不能为空！");
		}

		this.service.create(super.identity().getUserId(), clientId);
		return success();
	}

	/**
	 * 启用
	 */
	@RequestMapping(value="/start/{id}",method=RequestMethod.PUT)
	public Object start(@PathVariable String id) {
		this.service.udpateStatus(super.identity().getUserId(), id, 1);
		return success();
	}

	/**
	 * 停用
	 */
	@RequestMapping(value="/stop/{id}",method=RequestMethod.PUT)
	public Object stop(@PathVariable String id) {
		this.service.udpateStatus(super.identity().getUserId(), id, 0);
		return success();
	}

	/**
	 * 物理删除
	 */
	@RequestMapping(value="/{id}",method=RequestMethod.DELETE)
	public Object delete(@PathVariable String id) {
		this.service.del(super.identity().getUserId(), id);
		return success();
	}

	/**
	 * 列表查询 and条件
	 */
	@RequestMapping(method=RequestMethod.GET)
	public Object query(@RequestParam(required = false) Integer pageSize, @RequestParam(required = false) Integer pageNum,
						@RequestParam(required = false) String orderBy, @RequestParam(required = false) String orderSort,
						@RequestParam(required = false) String secret, @RequestParam(required = false) String clientId,
						@RequestParam(required = false) Integer status) {
		KeyVO vo = new KeyVO();
		vo.setCreatedBy(super.identity().getUserId());

		if(null != secret) {
			vo.setSecret(secret);
		}
		if(null != clientId) {
			vo.setClientId(clientId);
		}
		// 1正常
		if(null != status) {
			vo.setStatus(status);
		}
		// 排序
		if(null != orderBy) {
			vo.setOrderBy(orderBy);
		}
		if(null != orderSort) {
			vo.setOrderSort(orderSort);
		}

		if(null != pageSize && null != pageNum && pageSize != 0 && pageNum != 0) {	//分页查询
			Page page = new Page();
			page.setPageSize(pageSize);
			page.setPageNum(pageNum);
			vo.setPage(page);

			return success(this.service.baseQueryPageByAnd(vo));
		} else {	//列表查询
			return success(this.service.baseQueryByAnd(vo));
		}
	}

}