package com.erlangshen.service;

import com.erlangshen.model.vo.PermissionRoleMenuVO;
import com.erlangshen.model.vo.PermissionRoleVO;
import com.erlangshen.model.vo.PermissionUserRoleVO;
import com.erlangshen.model.vo.TokenVO;
import com.fastjavaframework.exception.ThrowPrompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fastjavaframework.base.BaseService;
import com.erlangshen.dao.PermissionRoleDao;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;

/**
 * 权限
 * @author https://github.com/shuli495/erlangshen
 */
@Service
public class PermissionRoleService extends BaseService<PermissionRoleDao, PermissionRoleVO> {

	@Autowired
	private UserService userService;

	@Autowired
	private ClientService clientService;

	@Autowired
	private PermissionUserRoleService permissionUserRoleService;

	@Autowired
	private PermissionRoleMenuService permissionRoleMenuService;

	/**
	 * 添加角色
	 * @param token
	 * @param vo
     */
	public void insert(TokenVO token, PermissionRoleVO vo) {
		if(!clientService.isMyClient(token.getUserId() , vo.getClientId())) {
			throw new ThrowPrompt("无权操作此应用！", "102001");
		}

		super.baseInsert(vo);
	}

	/**
	 * 删除角色
	 * @param token
	 * @param id
     */
	@Transactional(rollbackFor = Exception.class)
	public void delete(TokenVO token, Integer id) {
		PermissionRoleVO vo = super.baseFind(id);
		if(!clientService.isMyClient(token.getUserId() , vo.getClientId())) {
			throw new ThrowPrompt("无权操作此应用！", "102002");
		}

		// 删除角色关联的用户
		permissionUserRoleService.baseDelete(id);
		// 删除角色关联的功能
		permissionRoleMenuService.baseDelete(id);
		// 删除角色
		super.baseDelete(id);
	}

	/**
	 * 列表查询
	 * @param vo
	 * @return
     */
	public List<PermissionRoleVO> query(TokenVO token, PermissionRoleVO vo) {
		if(!clientService.isMyClient(token.getUserId() , vo.getClientId())) {
			throw new ThrowPrompt("无权操作此应用！", "102003");
		}

		List<PermissionRoleVO> roles = super.baseQueryByAnd(vo);

		PermissionUserRoleVO permissionUserRoleVO = new PermissionUserRoleVO();
		PermissionRoleMenuVO permissionRoleMenuVO = new PermissionRoleMenuVO();
		for(PermissionRoleVO permissionRoleVO : roles) {
			// 关联用户
			permissionUserRoleVO.setRoleId(permissionRoleVO.getId());
			List<PermissionUserRoleVO> users = permissionUserRoleService.baseQueryByAnd(permissionUserRoleVO);
			List<String> userIds = new LinkedList<>();
			for(PermissionUserRoleVO userTmpVo : users) {
				userIds.add(userTmpVo.getUserId());
			}
			permissionRoleVO.setUsers(userIds);

			// 关联菜单
			permissionRoleMenuVO.setRoleId(permissionRoleVO.getId());
			List<PermissionRoleMenuVO> menus = permissionRoleMenuService.baseQueryByAnd(permissionRoleMenuVO);

			permissionRoleVO.setMenuInfo(menus);
		}

		return roles;
	}

	/**
	 * 查询用户权限
	 * @param token
	 * @param userId
	 * @return
	 */
	public List<PermissionRoleVO> queryByUser(TokenVO token, String userId) {
		if(!userService.isMyUser(token.getUserId(), userId)) {
			throw new ThrowPrompt("无权操作此用户!", "102008");
		}

		List<PermissionRoleVO> userRoles = this.dao.queryByUser(userId);

		PermissionRoleMenuVO permissionRoleMenuVO = new PermissionRoleMenuVO();
		for(PermissionRoleVO userRole : userRoles) {
			// 关联菜单
			permissionRoleMenuVO.setRoleId(userRole.getId());
			List<PermissionRoleMenuVO> menus = permissionRoleMenuService.baseQueryByAnd(permissionRoleMenuVO);

			userRole.setMenuInfo(menus);
		}
		return null;
	}

	/**
	 * 关联用户
	 * @param token
	 * @param ids
	 * @param users
     */
	@Transactional(rollbackFor = Exception.class)
	public void userInsert(TokenVO token, List<Integer> ids, List<String> users) {
		List<PermissionUserRoleVO> insertUsers = new LinkedList<>();

		for(Integer id : ids) {
			PermissionRoleVO vo = super.baseFind(id);
			if(!clientService.isMyClient(token.getUserId() , vo.getClientId())) {
				throw new ThrowPrompt("无权操作此应用！", "102004");
			}

			// 去除已关联的用户
			PermissionUserRoleVO permissionUserRoleVO = new PermissionUserRoleVO();
			permissionUserRoleVO.setRoleId(id);
			List<PermissionUserRoleVO> usersVO = permissionUserRoleService.baseQueryByAnd(permissionUserRoleVO);
			for(PermissionUserRoleVO user : usersVO) {
				if(users.indexOf(user.getUserId()) != -1) {
					users.remove(user.getUserId());
				}
			}

			// 批量关联用户
			PermissionUserRoleVO insertUserVO = null;
			for(String userId : users) {
				insertUserVO = new PermissionUserRoleVO();
				insertUserVO.setRoleId(id);
				insertUserVO.setUserId(userId);
				insertUsers.add(insertUserVO);
			}
		}

		permissionUserRoleService.baseInsertBatch(insertUsers);
	}

	/**
	 * 取消用户关联
	 * @param token
	 * @param ids
	 * @param users
     */
	@Transactional(rollbackFor = Exception.class)
	public void userDelete(TokenVO token, List<Integer> ids, List<String> users) {
		List<PermissionUserRoleVO> deleteUsers = new LinkedList<>();

		for(Integer id : ids) {
			PermissionRoleVO vo = super.baseFind(id);
			if(!clientService.isMyClient(token.getUserId() , vo.getClientId())) {
				throw new ThrowPrompt("无权操作此应用！", "102005");
			}

			// 批量取消关联
			PermissionUserRoleVO deleteUserVO = null;
			for(String userId : users) {
				deleteUserVO = new PermissionUserRoleVO();
				deleteUserVO.setRoleId(id);
				deleteUserVO.setUserId(userId);
				deleteUsers.add(deleteUserVO);
			}
		}

		permissionUserRoleService.baseDeleteBatch(deleteUsers);
	}

	/**
	 * 关联功能
	 * @param token
	 * @param id
	 * @param menus
     */
	public void menuInsert(TokenVO token, Integer id, List<Integer> menus) {
		PermissionRoleVO vo = super.baseFind(id);
		if(!clientService.isMyClient(token.getUserId() , vo.getClientId())) {
			throw new ThrowPrompt("无权操作此应用！", "102006");
		}

		PermissionRoleMenuVO permissionRoleMenuVO = new PermissionRoleMenuVO();
		permissionRoleMenuVO.setRoleId(id);
		List<PermissionRoleMenuVO> menusVO = permissionRoleMenuService.baseQueryByAnd(permissionRoleMenuVO);

		// 去除已关联的功能
		for(PermissionRoleMenuVO menu : menusVO) {
			if(menus.indexOf(menu.getMenuId()) != -1) {
				menus.remove(menu.getMenuId());
			}
		}

		// 批量关联功能
		List<PermissionRoleMenuVO> insertMenus = new LinkedList<>();
		PermissionRoleMenuVO insertMenuVO = null;
		for(Integer menuId : menus) {
			insertMenuVO = new PermissionRoleMenuVO();
			insertMenuVO.setRoleId(id);
			insertMenuVO.setMenuId(menuId);
			insertMenus.add(insertMenuVO);
		}
		permissionRoleMenuService.baseInsertBatch(insertMenus);
	}

	/**
	 * 取消功能关联
	 * @param token
	 * @param id
	 * @param menus
	 */
	public void menuDelete(TokenVO token, Integer id, List<Integer> menus) {
		PermissionRoleVO vo = super.baseFind(id);
		if(!clientService.isMyClient(token.getUserId() , vo.getClientId())) {
			throw new ThrowPrompt("无权操作此应用！", "102007");
		}

		// 批量取消关联
		List<PermissionRoleMenuVO> deleteMenus = new LinkedList<>();
		PermissionRoleMenuVO deleteFunctionVO = null;
		for(Integer menuId : menus) {
			deleteFunctionVO = new PermissionRoleMenuVO();
			deleteFunctionVO.setRoleId(id);
			deleteFunctionVO.setMenuId(menuId);
			deleteMenus.add(deleteFunctionVO);
		}

		permissionRoleMenuService.baseDeleteBatch(deleteMenus);
	}

}