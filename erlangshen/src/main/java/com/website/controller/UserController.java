package com.website.controller;

import com.website.common.Constants;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Date;

import com.fastjavaframework.exception.ThrowException;
import com.fastjavaframework.util.VerifyUtils;
import com.website.common.BaseElsController;
import com.website.model.vo.UserVO;
import com.website.service.ClientService;
import com.website.service.ValidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.fastjavaframework.util.UUID;
import com.fastjavaframework.page.Page;
import com.fastjavaframework.exception.ThrowPrompt;
import com.website.model.bo.UserBO;
import com.website.service.UserService;

/**
 * 用户
 */
@CrossOrigin
@RestController
@RequestMapping(value= Constants.URL_USER)
public class UserController extends BaseElsController<UserService> {

	@Autowired
	public ClientService clientService;
	@Autowired
	public ValidateService validateService;

	/**
	 * 创建
	 */
	@RequestMapping(method=RequestMethod.POST)
	public Object create(@RequestBody UserVO vo) {
		vo.setId(UUID.uuid());
		vo.setCreatedTime(new Date());

		if(VerifyUtils.isEmpty(vo.getClientId())) {
			vo.setClientId(super.identity().getClientId());
		}

		this.service.insert(super.identity(), vo);
		return success(vo.getId());
	}

	/**
	 * 发送邮件
	 * @param type 		类型
	 * @param userId	用户id	与mail二选一
	 * @param mail		注册mail 与suerId二选一
	 * @param callback	如果是url注册链接，此参数发送邮件后跳转到此参数的url
	 * @param isCheckUserExist	检查用户是否存在 null不检查 true存在抛异常 false不存在抛异常
	 */
	@RequestMapping(value=Constants.URL_USER_SENDMAIL, method=RequestMethod.GET)
	public Object sendMail(@RequestParam String type,
						   @RequestParam(required = false) String mail,
						   @RequestParam(required = false) String userId,
						   @RequestParam(required = false) String callback,
						   @RequestParam(required = false) Boolean isCheckUserExist) {
		this.service.sendMail(super.identity(), type, userId, mail, callback, isCheckUserExist);
		return success();
	}

	/**
	 * 发送短信
	 * @param type
	 * @param userId
	 * @param phone
	 * @param isCheckUserExist	检查用户是否存在 null不检查 true存在抛异常 false不存在抛异常
     * @return
     */
	@RequestMapping(value=Constants.URL_USER_SENDPHONE, method=RequestMethod.GET)
	public Object sendPhone(@RequestParam String type,
						   @RequestParam(required = false) String userId,
							@RequestParam(required = false) String phone,
							@RequestParam(required = false) Boolean isCheckUserExist) {
		this.service.sendPhone(super.identity(), type, userId, phone, isCheckUserExist);
		return success();
	}

	/**
	 * 验证码验证
	 * @param code	验证码
	 * @param userId 与phone、mail三选一
	 * @param mail	与phone、userId三选一
	 * @param phone 与mail、userId三选一
     */
	@RequestMapping(value=Constants.URL_USER_CHECKCODE, method=RequestMethod.GET)
	public Object checkCode(@RequestParam(required = false) String code,@RequestParam(required = false) String type,
							@RequestParam(required = false) String userId,
						  @RequestParam(required = false) String mail, @RequestParam(required = false) String phone) {
		if(VerifyUtils.isEmpty(mail) && VerifyUtils.isEmpty(phone)) {
			throw new ThrowException("邮箱或手机必须填一个！", "071001");
		}
		if(VerifyUtils.isEmpty(code)) {
			throw new ThrowException("验证码必填！", "071002");
		}
		if(VerifyUtils.isEmpty(type)) {
			throw new ThrowException("类型必填！", "071002");
		}

		if(VerifyUtils.isNotEmpty(userId)) {
			validateService.checkByUserId(userId, type, code);
		} else {
			validateService.checkByMailOrPhone(super.identity().getClientId(), mail, phone, code, type);
		}

		return success();
	}

	/**
	 * 验证邮箱url
	 * @param info
	 * @return
	 */
	@RequestMapping(value=Constants.URL_USER_CHECKMAIL, method=RequestMethod.GET)
	public String checkMail(@RequestParam String info) {
		return "redirect:" + this.service.checkMail(info);
	}

	/**
	 * 修改密码
	 */
	@RequestMapping(value="/rePwd", method=RequestMethod.POST)
	public Object rePwd(@RequestBody UserVO vo) {
		if(VerifyUtils.isEmpty(vo.getId())) {
			throw new ThrowException("id必传!", "071003");
		}
		if(VerifyUtils.isEmpty(vo.getPwd())) {
			throw new ThrowException("pwd必传!", "071004");
		}
		if(VerifyUtils.isEmpty(vo.getCode()) && VerifyUtils.isEmpty(vo.getOldPwd())) {
			throw new ThrowException("code或oldPwd必传一个!", "071005");
		}

		this.service.rePwd(super.identity(), vo.getId(), vo.getCode(), vo.getOldPwd(), vo.getPwd());
		return success();
	}

	/**
	 * 更新
	 */
	@RequestMapping(value="/{id}", method=RequestMethod.PUT)
	public Object update(@PathVariable String id, @RequestBody UserVO vo) {
		vo.setId(id);
		this.service.update(super.identity(), vo);
		return success();
	}

	/**
	 * id查询详情
	 */
	@RequestMapping(value="/{id}",method=RequestMethod.GET)
	public Object findById(@PathVariable String id) {
		UserBO user = this.service.baseFind(id);
		if(null == user
				|| (Constants.PROJECT_NAME.equals(super.identity().getClientId())
					&& !clientService.isMyClient(super.identity().getUserId(), user.getClientId()))) {
			throw new ThrowPrompt("无此用户！", "071006");
		}

		if(!Constants.PROJECT_NAME.equals(super.identity().getClientId())) {
			// 非erlangshen用户查看他人详情，置空关键项
			if(!super.identity().getUserId().equals(id)) {
				user.setIdcard("");
				user.setIdcardImg("");
				user.setAddress("");
				user.setCreatedTime(null);
				user.setStatus(null);
			}
		}

		// 密码置空
		user.setPwd("");
		return success(user);
	}

	/**
	 * 列表查询 and条件
	 */
	@RequestMapping(method=RequestMethod.GET)
	public Object query(@RequestParam(required = false) Integer pageSize, @RequestParam(required = false) Integer pageNum,
						@RequestParam(required = false) String sort, @RequestParam(required = false) String order,
						@RequestParam(required = false) String clientId, @RequestParam(required = false) String source,
						@RequestParam(required = false) String username, @RequestParam(required = false) String nickname,
						@RequestParam(required = false) String mail, @RequestParam(required = false) String phone,
						@RequestParam(required = false) String tel, @RequestParam(required = false) String qq,
						@RequestParam(required = false) String weixin, @RequestParam(required = false) String weibo,
						@RequestParam(required = false) String name, @RequestParam(required = false) Boolean sex,
						@RequestParam(required = false) String idcard, @RequestParam(required = false) String province,
						@RequestParam(required = false) String city, @RequestParam(required = false) String area,
						@RequestParam(required = false) String address, @RequestParam(required = false) Integer status) {
		UserVO vo = new UserVO();
		vo.setCreatedBy(super.identity().getUserId());

		//客户端名称
		if(null != clientId) {
			vo.setClientId(clientId);
		}
		// 来源
		if(null != source) {
			vo.setSource(source);
		}
		//用户名
		if(null != username) {
			vo.setUsername(username);
		}
		//昵称
		if(null != nickname) {
			vo.setNickname(nickname);
		}
		//邮箱
		if(null != mail) {
			vo.setMail(mail);
		}
		//手机号码
		if(null != phone) {
			vo.setPhone(phone);
		}
		//电话
		if(null != tel) {
			vo.setTel(tel);
		}
		//QQ
		if(null != qq) {
			vo.setQq(qq);
		}
		//微信
		if(null != weixin) {
			vo.setWeixin(weixin);
		}
		//新浪微博
		if(null != weibo) {
			vo.setWeibo(weibo);
		}
		//姓名
		if(null != name) {
			vo.setName(name);
		}
		//性别 0女 1男
		if(null != sex) {
			vo.setSex(sex);
		}
		//身份证号
		if(null != idcard) {
			vo.setIdcard(idcard);
		}
		//省
		if(null != province) {
			vo.setProvince(province);
		}
		//市
		if(null != city) {
			vo.setCity(city);
		}
		//区
		if(null != area) {
			vo.setArea(area);
		}
		//地址
		if(null != address) {
			vo.setAddress(address);
		}
		//状态 0正常
		if(null != status) {
			vo.setStatus(status);
		}

		vo.setOrderBy("CREATED_TIME");

		if(pageSize != null && pageNum != null && pageSize != 0 && pageNum != 0) {	//分页查询
			Page page = new Page();
			page.setPageSize(pageSize);
			page.setPageNum(pageNum);
			vo.setPage(page);

			return success(this.service.queryByClientPage(vo));
		} else {	//列表查询
			return success(this.service.queryByClient(vo));
		}
	}

	/**
	 * 删除
	 */
	@RequestMapping(value="/{id}", method=RequestMethod.DELETE)
	public Object delete(@PathVariable String id) {
		this.service.delete(super.identity(), id);
		return success();
	}

}
