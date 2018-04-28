package com.website.service;

import java.util.*;

import com.fastjavaframework.exception.ThrowPrompt;
import com.website.model.vo.TokenVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fastjavaframework.base.BaseService;
import com.website.dao.PermissionMenuDao;
import com.website.model.vo.PermissionMenuVO;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PermissionMenuService extends BaseService<PermissionMenuDao,PermissionMenuVO> {

	@Autowired
	private ClientService clientService;

	/**
	 * 插入
	 * @param token
	 * @param vo
     */
	@Transactional
	public void insert(TokenVO token, PermissionMenuVO vo) {
		if(!clientService.isMyClient(token.getUserId() , vo.getClientId())) {
			throw new ThrowPrompt("无权操作此应用！");
		}

		if(null != vo.getParentId() && vo.getParentId() != 0) {
			PermissionMenuVO parentVO = this.baseFind(vo.getParentId());
			if(null != parentVO && !parentVO.getClientId().equals(vo.getClientId())) {
				throw new ThrowPrompt("与父节点不在同一个应用下！");
			}
		}

		this.baseInsert(vo);
	}

	/**
	 * 删除菜单，其子菜单和功能点
	 * @param id
     */
	@Transactional
	public void delete(TokenVO token, Integer id) {
		PermissionMenuVO permissionMenuVO = super.baseFind(id);
		if(null == permissionMenuVO) {
			throw new ThrowPrompt("无此菜单！");
		}
		if(!clientService.isMyClient(token.getUserId() , permissionMenuVO.getClientId())) {
			throw new ThrowPrompt("无权操作此应用！");
		}

		// 要删除的菜单及其子菜单
		List<Integer> deleteIds = this.getDeleteBranchIds(id);
		deleteIds.add(id);

		// 删除
		this.baseDeleteBatch(deleteIds);
	}

	/**
	 * 获取要删除子菜单的id列表
	 * @param topId
	 * @return
     */
	private List<Integer> getDeleteBranchIds(Integer topId) {
		List<Integer> deleteIds = new LinkedList<>();

		PermissionMenuVO permissionMenuVO = new PermissionMenuVO();
		permissionMenuVO.setParentId(topId);

		for(PermissionMenuVO branch : this.baseQueryByAnd(permissionMenuVO)) {
			deleteIds.addAll(this.getDeleteBranchIds(branch.getId()));

			deleteIds.add(branch.getId());
		}

		return deleteIds;
	}

	/**
	 * 菜单列表
	 * @param permissionMenuVO
	 * @return
     */
	public List<PermissionMenuVO> query(TokenVO token, PermissionMenuVO permissionMenuVO) {
		if(!clientService.isMyClient(token.getUserId() , permissionMenuVO.getClientId())) {
			throw new ThrowPrompt("无权操作此应用！");
		}

		List<PermissionMenuVO> queryPermissionMenuVO = super.baseQueryByAnd(permissionMenuVO);

		Map<Integer, PermissionMenuVO> top = new HashMap<>();
		Map<Integer, PermissionMenuVO> branch = new HashMap<>();

		for(PermissionMenuVO tmp : queryPermissionMenuVO) {
			if(tmp.getParentId() == 0) {
				top.put(tmp.getId(), tmp);
			} else {
				branch.put(tmp.getId(), tmp);
			}
		}

		List<Integer> removeIds = new ArrayList<>();
		for(Integer id : branch.keySet()) {
			PermissionMenuVO value = branch.get(id);
			Integer parentId = value.getParentId();
			if(null == branch.get(parentId) && null == top.get(parentId)) {
				top.put(id, value);
				removeIds.add(id);
			}
		}

		removeIds.forEach(branch::remove);

		List<PermissionMenuVO> fromat = new LinkedList<>();
		for(PermissionMenuVO topVO : top.values()) {
			this.makeTree(topVO, branch);

			fromat.add(topVO);
		}

		return fromat;
	}

	private void makeTree(PermissionMenuVO topVO, Map<Integer, PermissionMenuVO> branch) {
		PermissionMenuVO branchVO = null;
		Map<Integer, PermissionMenuVO> tmp = new HashMap<>();

		for(Integer branchId : branch.keySet()) {
			branchVO = branch.get(branchId);
			Integer parentId = branchVO.getParentId();

			if(topVO.getId() == parentId) {
				tmp.putAll(branch);
				tmp.remove(branchVO);

				this.makeTree(branchVO, tmp);
				topVO.getMenus().add(branchVO);
			}
		}
	}

	/**
	 * 更新
	 */
	public int update(TokenVO token,PermissionMenuVO vo) {
		//设置修改值
		PermissionMenuVO upVO = this.setUpdateVlaue(super.baseFind(vo.getId()), vo);

		if(!clientService.isMyClient(token.getUserId() , upVO.getClientId())) {
			throw new ThrowPrompt("无权操作此应用！");
		}

		//更新
		return super.baseUpdate(upVO);
	}

	/**
	 * 设置修改的属性(不为null为修改)
	 * @param dbVO 库中最新vo
	 * @param upVO	修改的vo
	 * @return 修改后的vo
	 */
	private PermissionMenuVO setUpdateVlaue(PermissionMenuVO dbVO, PermissionMenuVO upVO) {
		if(null == dbVO) {
			throw new ThrowPrompt("无"+upVO.getId()+"信息！");
		}

		// 菜单名称
		if(null != upVO.getName()) {
			dbVO.setName(upVO.getName());
		}
		// 菜单url
		if(null != upVO.getUrl()) {
			dbVO.setUrl(upVO.getUrl());
		}
		// tag
		if(null != upVO.getTag()) {
			dbVO.setTag(upVO.getTag());
		}
		return dbVO;
	}

}