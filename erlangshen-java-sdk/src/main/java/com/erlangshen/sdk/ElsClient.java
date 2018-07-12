package com.erlangshen.sdk;

import com.alibaba.fastjson.JSONObject;
import com.erlangshen.sdk.result.*;
import com.erlangshen.sdk.result.model.CertificationDetail;
import com.erlangshen.sdk.result.model.User;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wsl on 6/22 0022.
 */
public class ElsClient {

//    private final String host = "https://erlangshen.pighand.com/service";
    private final String host = "http://localhost:8081/erlangshen";
    private String ak;
    private String sk;

    public ElsClient(String ak, String sk) {
        this.ak = ak;
        this.sk = sk;
    }

    /**
     * 用户是否已存在
     * @param name
     * @return true存在 false不存在
     * @throws Exception
     */
    public boolean isUserExit(String name) throws Exception {
        if(null == name || "".equals(name)) {
            throw new Exception("请填写用户名、邮箱或手机");
        }

        String result = new Http(host, "/user/checkUser/" + name, null, null, ak, sk).get();

        BooleanResult booleanResult =  JSONObject.parseObject(result, BooleanResult.class);

        if("success".equals(booleanResult.getStatus())) {
            return booleanResult.getData();
        } else {
            throw new Exception(booleanResult.getMessage());
        }
    }

    /**
     * 邮箱是否已存在
     * @param mail
     * @return true存在 false不存在
     * @throws Exception
     */
    public boolean isMailExit(String mail) throws Exception {
        if(null == mail || "".equals(mail)) {
            throw new Exception("邮箱必填");
        }

        UserList users = this.userList(mail, "", "", "");

        if(users.getData().size() > 0) {
         return true;
        }

        return false;
    }

    /**
     * 手机是否已存在
     * @param phone
     * @return true存在 false不存在
     * @throws Exception
     */
    public boolean isPhoneExit(String phone) throws Exception {
        if(null == phone || "".equals(phone)) {
            throw new Exception("手机号码必填");
        }

        UserList users = this.userList("", phone, "", "");

        if(users.getData().size() > 0) {
            return true;
        }

        return false;
    }

    /**
     * 用户名是否已存在
     * @param username
     * @return true存在 false不存在
     * @throws Exception
     */
    public boolean isUsernameExit(String username) throws Exception {
        if(null == username || "".equals(username)) {
            throw new Exception("用户名必填");
        }

        UserList users = this.userList("", "", username, "");

        if(users.getData().size() > 0) {
            return true;
        }

        return false;
    }

    /**
     * 昵称是否已存在
     * @param nikename
     * @return true存在 false不存在
     * @throws Exception
     */
    public boolean isNikenameExit(String nikename) throws Exception {
        if(null == nikename || "".equals(nikename)) {
            throw new Exception("昵称必填");
        }

        UserList users = this.userList("", "", "", nikename);

        if(users.getData().size() > 0) {
            return true;
        }

        return false;
    }

    /**
     * 发送邮件
     * @param type      二郎神系统配置的邮件类型
     * @param mail      接收邮件的账号 与userId二选一
     * @param userId    与邮件二选一
     * @param callback  如果是url注册链接，此参数发送邮件后跳转到此参数的url
     * @param isCheckUserExist 检查用户是否存在 null不检查 true存在抛异常 false不存在抛异常
     * @return Result
     * @throws Exception
     */
    public Result sendMail(String type, String mail, String userId, String callback, Boolean isCheckUserExist) throws Exception {
        if(null == type || "".equals(type)) {
            throw new Exception("type必填");
        }
        if((null == mail || "".equals(mail)) && (null == userId || "".equals(userId))) {
            throw new Exception("mail或userId至少传一个");
        }

        Map<String, Object> params = new HashMap<>();
        params.put("type", type);

        if(null != mail && !"".equals(mail)) {
            params.put("mail", mail);
        }
        if(null != userId && !"".equals(userId)) {
            params.put("userId", userId);
        }
        if(null != callback && !"".equals(callback)) {
            params.put("callback", callback);
        }
        if(null != isCheckUserExist && !"".equals(isCheckUserExist)) {
            params.put("isCheckUserExist", isCheckUserExist);
        }

        String result = new Http(host, "/user/sendMail", null, params, ak, sk).get();

        return JSONObject.parseObject(result, Result.class);
    }

    /**
     * 发送短信
     * @param type      二郎神系统配置的邮件类型
     * @param phone      接收邮件的账号 与userId二选一
     * @param userId    与邮件二选一
     * @param callback  如果是url注册链接，此参数发送邮件后跳转到此参数的url
     * @param isCheckUserExist 检查用户是否存在 null不检查 true存在抛异常 false不存在抛异常
     * @return Result
     * @throws Exception
     */
    public Result sendPhone(String type, String phone, String userId, String callback, Boolean isCheckUserExist) throws Exception {
        if(null == type || "".equals(type)) {
            throw new Exception("type必填");
        }
        if((null == phone || "".equals(phone)) && (null == userId || "".equals(userId))) {
            throw new Exception("phone或userId至少传一个");
        }

        Map<String, Object> params = new HashMap<>();
        params.put("type", type);

        if(null != phone && !"".equals(phone)) {
            params.put("phone", phone);
        }
        if(null != userId && !"".equals(userId)) {
            params.put("userId", userId);
        }
        if(null != callback && !"".equals(callback)) {
            params.put("callback", callback);
        }
        if(null != isCheckUserExist && !"".equals(isCheckUserExist)) {
            params.put("isCheckUserExist", isCheckUserExist);
        }

        String result = new Http(host, "/user/sendPhone", null, params, ak, sk).get();

        return JSONObject.parseObject(result, Result.class);
    }

    /**
     * 校验验证码
     * @param code      验证码
     * @param type      二郎神系统配置的邮件类型
     * @param userId    与mail、phone三选一
     * @param mail      与userId、phone三选一
     * @param phone     与userId、mail三选一
     * @return Result
     * @throws Exception
     */
    public Result checkCode(String code, String type, String userId, String mail, String phone) throws Exception {
        if(null == code || "".equals(code)) {
            throw new Exception("code必填");
        }
        if(null == type || "".equals(type)) {
            throw new Exception("type必填");
        }
        if((null == phone || "".equals(phone)) && (null == mail || "".equals(mail)) && (null == userId || "".equals(userId))) {
            throw new Exception("邮箱或手机或userId必须填一个");
        }

        Map<String, Object> params = new HashMap<>();
        params.put("code", code);
        params.put("type", type);

        if(null != userId && !"".equals(userId)) {
            params.put("userId", userId);
        }
        if(null != phone && !"".equals(mail)) {
            params.put("mail", mail);
        }
        if(null != phone && !"".equals(phone)) {
            params.put("phone", phone);
        }

        String result = new Http(host, "/user/checkCode", null, params, ak, sk).get();

        return JSONObject.parseObject(result, Result.class);
    }

    /**
     * 用户注册
     * @param user
     * @return Result
     * @throws Exception
     */
    public RegisterResult register(User user) throws Exception {
        if((null == user.getUsername() || "".equals(user.getUsername()))
                && (null == user.getPhone() || "".equals(user.getPhone()))
                && (null == user.getMail() || "".equals(user.getMail()))) {
            throw new Exception("用户名、手机或邮箱必须填一个");
        }
        if(null == user.getPwd() || "".equals(user.getPwd())) {
            throw new Exception("密码不能为空");
        }

        String result = new Http(host, "/user", null, user, ak, sk).post();
        return JSONObject.parseObject(result, RegisterResult.class);
    }

    /**
     * 用户登录、获取token
     * @param username
     * @param pwd
     * @param code      登录验证码
     * @param platform  登录平台
     * @param loginIp   登录客户端IP
     * @return UserDetail
     * @throws Exception
     */
    public UserDetail login(String username, String pwd, String code, String platform, String loginIp) throws Exception {
        if(null == username || "".equals(username)) {
            throw new Exception("用户名不能为空");
        }
        if(null == pwd || "".equals(pwd)) {
            throw new Exception("密码不能为空");
        }
        if(null == loginIp || "".equals(loginIp)) {
            throw new Exception("登录IP不能为空");
        }

        Map<String, Object> params = new HashMap<>();
        params.put("username", username);
        params.put("pwd", pwd);
        params.put("loginIp", loginIp);

        if(null != code && !"".equals(code)) {
            params.put("code", code);
        }
        if(null != platform && !"".equals(platform)) {
            params.put("platform", platform);
        }

        String result = new Http(host, "/token", null, params, ak, sk).post();
        return JSONObject.parseObject(result, UserDetail.class);
    }

    /**
     * 获取登录验证码
     * @param loginIp
     * @return Result
     * @throws Exception
     */
    public Result getLoginCode(String loginIp) throws Exception {
        if(null == loginIp || "".equals(loginIp)) {
            throw new Exception("登录IP不能为空");
        }

        Map<String, Object> params = new HashMap<>();
        params.put("loginIp", loginIp);

        String result = new Http(host, "/token", null, params, ak, sk).get();
        return JSONObject.parseObject(result, Result.class);
    }

    /**
     * 用户详细信息
     * @param userId
     * @return UserDetail
     * @throws Exception
     */
    public UserDetail detail(String userId) throws Exception {
        if(null == userId || "".equals(userId)) {
            throw new Exception("用户ID不能为空");
        }

        String result = new Http(host, "/user/"+userId, null, null, ak, sk).get();
        return JSONObject.parseObject(result, UserDetail.class);
    }

    /**
     * 修改用户信息
     * @param user
     * @return Result
     * @throws Exception
     */
    public Result updateDetail(User user) throws Exception {
        if(null == user.getId() || "".equals(user.getId())) {
            throw new Exception("用户ID不能为空");
        }

        String result = new Http(host, "/user/"+user.getId(), null, user, ak, sk).get();
        return JSONObject.parseObject(result, UserDetail.class);
    }

    /**
     * 用户列表
     * @param mail      邮箱
     * @param phone     手机号码
     * @param username  用户名
     * @param nikename  昵称
     * @return UserList
     */
    public UserList userList(String mail, String phone, String username, String nikename) throws Exception {
        Map<String, Object> params = new HashMap<>();

        if(null != mail && !"".equals(mail)) {
            params.put("mail", mail);
        }

        if(null != phone && !"".equals(phone)) {
            params.put("phone", phone);
        }

        if(null != username && !"".equals(username)) {
            params.put("username", username);
        }

        if(null != nikename && !"".equals(nikename)) {
            params.put("nikename", nikename);
        }

        String result = new Http(host, "/user", null, params, ak, sk).get();
        return JSONObject.parseObject(result, UserList.class);
    }

    /**
     * 实名认证
     * @param userId    用户id
     * @param name      姓名
     * @param idcard    身份证号
     * @param forntFile 身份证正面照片
     * @param backFile  身份证反面照片
     * @param holdForntFile 手持身份证正面照片
     * @param holdBackFile  手持身份证反面照片
     * @return Result
     * @throws Exception
     */
    public Result certification(String userId, String name, String idcard,
                                FileInputStream forntFile, FileInputStream backFile,
                                FileInputStream holdForntFile, FileInputStream holdBackFile) throws Exception {
        if(null == userId || "".equals(userId)) {
            throw new Exception("用户ID不能为空");
        }

        Map<String, Object> params = new HashMap<>();

        if(null != name && !"".equals(name)) {
            params.put("name", name);
        }
        if(null != idcard && !"".equals(idcard)) {
            params.put("idcard", idcard);
        }

        if(forntFile.available() > 0 && holdBackFile.available() > 0) {
            params.put("holdForntFile", holdForntFile);
            params.put("holdBackFile", holdBackFile);
        } else if(forntFile.available() > 0 && backFile.available() > 0) {
            params.put("forntFile", forntFile);
            params.put("backFile", backFile);
        } else {
            throw new Exception("请上传正确身份证照片");
        }

        String result = new Http(host, "/user/" + userId + "/certification", null, params, ak, sk).post();
        return JSONObject.parseObject(result, Result.class);
    }

    /**
     * 实名认证详情
     * @param userId
     * @return CertificationDetail
     * @throws Exception
     */
    public CertificationDetail certificationDetail(String userId) throws Exception {
        if(null == userId || "".equals(userId)) {
            throw new Exception("用户ID不能为空");
        }

        String result = new Http(host, "/user/" + userId + "/certification", null, null, ak, sk).get();
        CertificationDetail certificationDetail = JSONObject.parseObject(result, CertificationDetail.class);

        // 用户详情
        UserDetail userDetail = this.detail(userId);
        if(!"success".equals(userDetail.getStatus())) {
            throw new Exception("用户不存在");
        }

        User user = userDetail.getData();
        certificationDetail.getData().setName(user.getName());
        certificationDetail.getData().setIdcard(user.getIdcard());
        certificationDetail.getData().setCertification(user.getCertification());
        certificationDetail.getData().setCertificationFailMsg(user.getCertificationFailMsg());

        return certificationDetail;
    }

    /**
     * 当前应用已配置的权限列表
     * @param role
     * @return PermissionList
     * @throws Exception
     */
    public PermissionList permissionListByClient(String role) throws Exception {
        Map<String, Object> params = new HashMap<>();

        if(null != role && !"".equals(role)) {
            params.put("role", role);
        }

        String result = new Http(host, "/permissionRole", null, params, ak, sk).get();
        return JSONObject.parseObject(result, PermissionList.class);
    }

    /**
     * 查询用户权限
     * @param userId
     * @return PermissionList
     * @throws Exception
     */
    public PermissionList permissionList(String userId) throws Exception {
        if(null == userId || "".equals(userId)) {
            throw new Exception("用户ID不能为空");
        }

        String result = new Http(host, "/permissionRole/user/" + userId, null, null, ak, sk).get();
        return JSONObject.parseObject(result, PermissionList.class);
    }

    /**
     * 关联用户权限
     * @param userId
     * @param roleIds
     * @return Result
     * @throws Exception
     */
    public Result createPermission(String userId, String[] roleIds) throws Exception {
        if(null == userId || "".equals(userId)) {
            throw new Exception("用户ID不能为空");
        }
        if(roleIds.length == 0) {
            throw new Exception("用户权限不能为空");
        }

        Map<String, Object> params = new HashMap<>();
        params.put("roles", roleIds);

        String result = new Http(host, "/permissionRole/user/" + userId, null, params, ak, sk).post();
        return JSONObject.parseObject(result, Result.class);
    }

    /**
     * 取消关联用户权限
     * @param userId
     * @param roleIds
     * @return Result
     * @throws Exception
     */
    public Result delPermission(String userId, String[] roleIds) throws Exception {
        if(null == userId || "".equals(userId)) {
            throw new Exception("用户ID不能为空");
        }
        if(roleIds.length == 0) {
            throw new Exception("用户权限不能为空");
        }

        Map<String, Object> params = new HashMap<>();
        params.put("roles", roleIds);

        String result = new Http(host, "/permissionRole/user/" + userId, null, params, ak, sk).delete();
        return JSONObject.parseObject(result, Result.class);
    }

    /**
     * 获取字典表
     * @param groupId
     * @param parentId
     * @return Result
     * @throws Exception
     */
    public Result sysCode(String groupId, String parentId) throws Exception {
        if((null == groupId || "".equals(groupId)) && (null == parentId || "".equals(parentId))) {
            throw new Exception("groupId或parentId至少传一个");
        }

        Map<String, Object> params = new HashMap<>();
        if(null != groupId && !"".equals(groupId)) {
            params.put("groupId", groupId);
        }
        if(null != parentId && !"".equals(parentId)) {
            params.put("parentId", parentId);
        }

        String result = new Http(host, "/code", null, params, ak, sk).get();
        return JSONObject.parseObject(result, Result.class);
    }

}
