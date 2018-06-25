class SDKApp extends React.Component {

    render(){
        { }

        return (
            <div className="sdk_content">
                <div className="content">
                    <a href="javascript:void(0);"><b>Python-SDK</b></a>
                </div>
                <div className="content">
                    <h4>Git：</h4>
                    <pre>https://github.com/shuli495/erlangshen/tree/master/erlangshen-java-sdk</pre>
                    <h4>Maven：</h4>
                    <pre>https://github.com/shuli495/erlangshen/tree/master/erlangshen-java-sdk</pre>
                    <h4>调用：</h4>
                    <pre>
                        <b>ElsClient elsClient = new ElsClient(String ak, String sk);</b>
                        <br/><br/>
                        /**<br/>
                         * 邮箱是否已存在<br/>
                         * @param mail<br/>
                         * @return true存在 false不存在<br/>
                         */<br/>
                        <b>elsClient.isMailExit(String mail);</b>
                        <br/><br/>
                        /**<br/>
                         * 手机是否已存在<br/>
                         * @param phone<br/>
                         * @return true存在 false不存在<br/>
                         */<br/>
                        <b>elsClient.isPhoneExit(String phone);</b>
                        <br/><br/>
                        /**<br/>
                         * 用户名是否已存在<br/>
                         * @param username<br/>
                         * @return true存在 false不存在<br/>
                         */<br/>
                        <b>elsClient.isUsernameExit(String username);</b>
                        <br/><br/>
                        /**<br/>
                         * 昵称是否已存在<br/>
                         * @param nikename<br/>
                         * @return true存在 false不存在<br/>
                         */<br/>
                        <b>elsClient.isNikenameExit(String nikename);</b>
                        <br/><br/>
                        /**<br/>
                         * 发送邮件<br/>
                         * @param type      二郎神系统配置的邮件类型<br/>
                         * @param mail      接收邮件的账号 与userId二选一<br/>
                         * @param userId    与邮件二选一<br/>
                         * @param callback  如果是url注册链接，此参数发送邮件后跳转到此参数的url<br/>
                         * @param isCheckUserExist 检查用户是否存在 null不检查 true存在抛异常 false不存在抛异常<br/>
                         * @return Result<br/>
                         */<br/>
                        <b>elsClient.sendMail(String type, String mail, String userId, String callback, Boolean isCheckUserExist);</b>
                        <br/><br/>
                        /**<br/>
                         * 发送短信<br/>
                         * @param type      二郎神系统配置的邮件类型<br/>
                         * @param phone      接收邮件的账号 与userId二选一<br/>
                         * @param userId    与邮件二选一<br/>
                         * @param callback  如果是url注册链接，此参数发送邮件后跳转到此参数的url<br/>
                         * @param isCheckUserExist 检查用户是否存在 null不检查 true存在抛异常 false不存在抛异常<br/>
                         * @return Result<br/>
                         */<br/>
                        <b>elsClient.sendPhone(String type, String phone, String userId, String callback, Boolean isCheckUserExist);</b>
                        <br/><br/>
                        /**<br/>
                         * 校验验证码<br/>
                         * @param code      验证码<br/>
                         * @param type      二郎神系统配置的邮件类型<br/>
                         * @param userId    与mail、phone三选一<br/>
                         * @param mail      与userId、phone三选一<br/>
                         * @param phone     与userId、mail三选一<br/>
                         * @return Result<br/>
                         */<br/>
                        <b>elsClient.checkCode(String code, String type, String userId, String mail, String phone);</b>
                        <br/><br/>
                        /**<br/>
                         * 用户注册<br/>
                         * @param user<br/>
                         * @return Result<br/>
                         * @throws Exception<br/>
                         */<br/>
                        <b>elsClient.register(User user);</b>
                        <br/><br/>
                        /**<br/>
                         * 用户登录、获取token<br/>
                         * @param username<br/>
                         * @param pwd<br/>
                         * @param code      登录验证码<br/>
                         * @param platform  登录平台<br/>
                         * @param loginIp   登录客户端IP<br/>
                         * @return UserDetail<br/>
                         */<br/>
                        <b>elsClient.login(String username, String pwd, String code, String platform, String loginIp);</b>
                        <br/><br/>
                        /**<br/>
                         * 获取登录验证码<br/>
                         * @param loginIp<br/>
                         * @return Result<br/>
                         */<br/>
                        <b>elsClient.getLoginCode(String loginIp);</b>
                        <br/><br/>
                        /**<br/>
                         * 用户详细信息<br/>
                         * @param userId<br/>
                         * @return UserDetail<br/>
                         */<br/>
                        <b>elsClient.detail(String userId);</b>
                        <br/><br/>
                        /**<br/>
                         * 修改用户信息<br/>
                         * @param user<br/>
                         * @return Result<br/>
                         */<br/>
                        <b>elsClient.updateDetail(User user);</b>
                        <br/><br/>
                        /**<br/>
                         * 用户列表<br/>
                         * @param mail      邮箱<br/>
                         * @param phone     手机号码<br/>
                         * @param username  用户名<br/>
                         * @param nikename  昵称<br/>
                         * @return UserList<br/>
                         */<br/>
                        <b>elsClient.userList(String mail, String phone, String username, String nikename);</b>
                        <br/><br/>
                        /**<br/>
                         * 实名认证<br/>
                         * @param userId    用户id<br/>
                         * @param name      姓名<br/>
                         * @param idcard    身份证号<br/>
                         * @param forntFile 身份证正面照片<br/>
                         * @param backFile  身份证反面照片<br/>
                         * @param holdForntFile 手持身份证正面照片<br/>
                         * @param holdBackFile  手持身份证反面照片<br/>
                         * @return Result<br/>
                         */<br/>
                        <b>elsClient.certification(String userId, String name, String idcard, MultipartFile forntFile, MultipartFile backFile, MultipartFile holdForntFile, MultipartFile holdBackFile);</b>
                        <br/><br/>
                        /**<br/>
                         * 实名认证详情<br/>
                         * @param userId<br/>
                         * @return CertificationDetail<br/>
                         */<br/>
                        <b>elsClient.certificationDetail(String userId);</b>
                        <br/><br/>
                        /**<br/>
                         * 当前应用已配置的权限列表<br/>
                         * @param role<br/>
                         * @return PermissionList<br/>
                         */<br/>
                        <b>elsClient.permissionListByClient(String role);</b>
                        <br/><br/>
                        /**<br/>
                         * 查询用户权限<br/>
                         * @param userId<br/>
                         * @return PermissionList<br/>
                         */<br/>
                        <b>elsClient.permissionList(String userId);</b>
                        <br/><br/>
                        /**<br/>
                         * 关联用户权限<br/>
                         * @param userId<br/>
                         * @param roleIds<br/>
                         * @return Result<br/>
                         */<br/>
                        <b>elsClient.createPermission(String userId, String[] roleIds);</b>
                        <br/><br/>
                        /**<br/>
                         * 取消关联用户权限<br/>
                         * @param userId<br/>
                         * @param roleIds<br/>
                         * @return Result<br/>
                         */<br/>
                        <b>elsClient.delPermission(String userId, String[] roleIds);</b>
                        <br/><br/>
                        /**<br/>
                         * 获取字典表<br/>
                         * @param groupId<br/>
                         * @param parentId<br/>
                         * @return Result<br/>
                         */<br/>
                        <b>elsClient.sysCode(String groupId, String parentId);</b>
                    </pre>
                </div>
            </div>
        );
    }
}

module.exports = SDKApp;