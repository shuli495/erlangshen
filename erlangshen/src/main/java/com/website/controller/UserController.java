package com.website.controller;

import com.fastjavaframework.Setting;
import com.website.common.Constants;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

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
import org.springframework.web.multipart.MultipartFile;

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
		if(VerifyUtils.isEmpty(mail) && VerifyUtils.isEmpty(phone) && VerifyUtils.isEmpty(userId)) {
			throw new ThrowPrompt("邮箱或手机或userId必须填一个！", "081001");
		}
		if(VerifyUtils.isEmpty(code)) {
			throw new ThrowPrompt("验证码必填！", "081002");
		}
		if(VerifyUtils.isEmpty(type)) {
			throw new ThrowPrompt("类型必填！", "081003");
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
	 * 查找身份证
	 * @param id
	 * @return
     */
	@RequestMapping(value="/{id}/certification", method=RequestMethod.GET)
	public Object findCertification(@PathVariable String id) {
		if(!this.service.isMyUser(super.identity().getUserId(), id) && !super.identity().getUserId().equals(id)) {
			throw new ThrowPrompt("无权操作此用户!", "081004");
		}

		Map<String, String> result = new HashMap<>();
		String realPath = request.getSession().getServletContext().getRealPath("");
		String imagePath = Setting.getProperty("idcard.image.path");

		String forntPath = imagePath + id + "_0.jpg";
		if(new File(realPath + forntPath).exists()) {
			result.put("fornt", forntPath.replaceAll("\\\\","/"));
		} else {
			result.put("fornt", "");
		}

		String backPath = imagePath + id + "_1.jpg";
		if(new File(realPath + backPath).exists()) {
			result.put("back", backPath.replaceAll("\\\\","/"));
		} else {
			result.put("back", "");
		}

		return success(result);
	}

	/**
	 * 实名认证
	 * @param id
	 * @param name	 姓名
	 * @param idcard 身份证号
	 * @param forntFile	 身份证正面图片
	 * @param backFile	 身份证反面图片
     * @return
     */
	@RequestMapping(value="/{id}/certification", method=RequestMethod.POST)
	public Object certification(@PathVariable String id, String name, String idcard,
								@RequestParam(value="forntFile",required=false) MultipartFile forntFile,
								@RequestParam(value="backFile",required=false) MultipartFile backFile) {
		if(!this.service.isMyUser(super.identity().getUserId(), id) && !super.identity().getUserId().equals(id)) {
			throw new ThrowPrompt("无权操作此用户!", "081005");
		}

		if(VerifyUtils.isEmpty(id)) {
			throw new ThrowPrompt("id不能为空！", "081006");
		}
		if(forntFile.isEmpty()) {
			throw new ThrowPrompt("请上传身份证正面！", "081007");
		}
		if(backFile.isEmpty()) {
			throw new ThrowPrompt("请上传身份证反面！", "081008");
		}

		String pathRoot = request.getSession().getServletContext().getRealPath("");

		// 保存身份证正面照片
		if(forntFile.getContentType().indexOf("jpg") == -1
				&& forntFile.getContentType().indexOf("jpeg") == -1
				&& forntFile.getContentType().indexOf("png") == -1
				&& forntFile.getContentType().indexOf("bmp") == -1) {
			throw new ThrowPrompt("上传文件类型只能是jpg/png/bmp格式", "081009");
		}
		try {
			String forntPath = new StringBuilder(pathRoot)
					.append(Setting.getProperty("idcard.image.path").replaceAll("\\\\", Matcher.quoteReplacement(File.separator)))
					.append(id)
					.append("_0.jpg").toString();
			forntFile.transferTo(new File(forntPath));
		} catch (IOException e) {
			throw new ThrowException("身份证上传错误：" + e.getMessage());
		}

		// 保存身份证反面照片
		if(backFile.getContentType().indexOf("jpg") == -1
				&& backFile.getContentType().indexOf("jpeg") == -1
				&& backFile.getContentType().indexOf("png") == -1
				&& backFile.getContentType().indexOf("bmp") == -1) {
			throw new ThrowPrompt("上传文件类型只能是jpg/png/bmp格式", "081010");
		}
		try {
			String backPath = new StringBuilder(pathRoot)
					.append(Setting.getProperty("idcard.image.path").replaceAll("\\\\", Matcher.quoteReplacement(File.separator)))
					.append(id)
					.append("_1.jpg").toString();
			backFile.transferTo(new File(backPath));
		} catch (IOException e) {
			throw new ThrowException("身份证上传错误：" + e.getMessage(), "081011");
		}

		// 实名认证
		this.service.certification(super.identity(), id, name, idcard);

		return success();
	}

	/**
	 * 实名认证手动确认接口
	 * certification 0未实名 1认证中 2认证失败 3认证成功
	 * @param id
	 * @param vo
     * @return
     */
	@RequestMapping(value="/{id}/certification", method=RequestMethod.PUT)
	public Object certificationConfirm(@PathVariable String id, @RequestBody UserVO vo) {
		if(!this.service.isMyUser(super.identity().getUserId(), id) || super.identity().getUserId().equals(id)) {
			throw new ThrowPrompt("无权进行此操作!");
		}

		if(VerifyUtils.isEmpty(vo.getCertification())) {
			throw new ThrowPrompt("认证状态不能为空！", "081012");
		}
		if(vo.getCertification() != 2) {
			vo.setCertificationFailMsg(null);
		}

		UserVO user = new UserVO();
		user.setId(id);
		user.setCertification(vo.getCertification());
		user.setCertificationFailMsg(vo.getCertificationFailMsg());

		this.service.update(super.identity(), user);

		return success();
	}

	/**
	 * 修改密码
	 */
	@RequestMapping(value="/{id}/rePwd", method=RequestMethod.POST)
	public Object rePwd(@PathVariable String id, @RequestBody UserVO vo) {
		if(VerifyUtils.isEmpty(id)) {
			throw new ThrowPrompt("id必传!", "081013");
		}
		if(VerifyUtils.isEmpty(vo.getPwd())) {
			throw new ThrowPrompt("pwd必传!", "081014");
		}
		if(VerifyUtils.isEmpty(vo.getCode()) && VerifyUtils.isEmpty(vo.getOldPwd())) {
			throw new ThrowPrompt("code或oldPwd必传一个!", "081015");
		}

		vo.setId(id);
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
			throw new ThrowPrompt("无此用户！", "081016");
		}

		if(!Constants.PROJECT_NAME.equals(super.identity().getClientId())) {
			// 非erlangshen用户查看他人详情，置空关键项
			if(!super.identity().getUserId().equals(id)) {
				user.setIdcard("");
				user.setCertification(null);
				user.setCertificationFailMsg("");
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
