import BaseComponents from 'components/common/baseComponents.jsx';
import UserStore from 'stores/user.jsx';

class RegisterApp extends BaseComponents {
    constructor(props) {
        super(props, UserStore);

        this.handleRegister = this.handleRegister.bind(this);
        this.sendCode = this.sendCode.bind(this);
    }

    // 发送验证码
    sendCode() {
        var userName = this.refs.userName.value;
        //非空校验
        if(userName == "") {
            this.setData({"info":"邮箱或手机号码不能为空！"});
            return;
        }

        //格式检验
        var isMail = $util_checkMail(userName);
        var isPhone = $util_checkPhone(userName);
        if(!isMail && !isPhone) {
            this.setData({"info":"请输入邮箱或手机！"});
            return;
        }

        if(isMail) {
            UserStore.retrieveChkUser("register", userName, "", true);
        } else {
            UserStore.retrieveChkUser("register", "", userName, true);
        }

        this.refs.sendBut.setAttribute('style', 'pointer-events:none');
        this.reSendTime(30);
    }

    // 发送邮件倒计时
    reSendTime(time) {
        this.refs.sendBut.innerHTML = time + "秒后重试！";
        if(time >= 1 && this.state.info == "") {
                this.timeoutId = setTimeout(() => {this.reSendTime(time-1)}, 1000)
        } else {
            this.refs.sendBut.innerHTML = "发送验证码";
            this.refs.sendBut.removeAttribute('style');
        }
    }

    // 注册
    handleRegister() {
        var userName = this.refs.userName.value;
        var pwd = this.refs.pwd.value;
        var code = this.refs.code.value;

        if(!$util_validateValue("register")) {
            return;
        }

        // 按钮加载状态
        var registerDiv = this.refs.registerDiv;
        registerDiv.innerHTML = "";
        registerDiv.style.left = "47%";
        registerDiv.classList.add("loading_circle");
        this.refs.loginBut.disabled = true;

        UserStore.register(userName, pwd, code);
    }

    componentDidUpdate() {
        // 恢复按钮状态
        var registerDiv = this.refs.registerDiv;
        registerDiv.innerHTML = "注&nbsp;册";
        registerDiv.classList.remove("loading_circle");
        this.refs.loginBut.disabled = false;
        super.reloading();
    }

    render(){
        var info = this.state.info;
        if(info == "" && this.refs.registerDiv) {
        }

        return (
            <div id="register" className="login_content login_input_content login_button_content">
                <div className="login_line_content"><i className="fa fa-eye eye_icon"></i></div>
                <div className="login_line_content">
                    <input type="text" ref="userName" placeholder="邮箱或手机号码" data-errMsgId="errorMsg" data-mailOrPhone="true" data-empty="true" data-emptyText="账号不能为空！"  data-max="32" data-maxText="最多32个字符！"/>
                </div>
               <button ref="sendBut" className="send_but button button-tiny" onClick={this.sendCode}>发送验证码</button>
                <div className="login_line_content">
                        <input type="text" ref="code" placeholder="验证码" data-errMsgId="errorMsg" data-empty="true" data-emptyText="验证码不能为空！"/>
                </div>
                <div className="login_line_content">
                    <input type="password" id="pwd" ref="pwd" placeholder="密码" data-errMsgId="errorMsg" data-empty="true" data-emptyText="密码不能为空！" data-min="6" data-minText="密码6-16个字符！" data-max="16" data-maxText="密码6-16个字符！"/>
                </div>
                <div className="login_line_content">
                    <input type="password" ref="rePwd" placeholder="确认密码" data-errMsgId="errorMsg" data-empty="true" data-emptyText="确认密码不能为空！" data-min="6" data-minText="确认密码6-16个字符！"  data-max="16" data-maxText="确认密码6-16个字符！" data-same="pwd" data-sameText="与密码不同，请重新输入！"/>
                </div>
                <div id="errorMsg" className="errorMsg">{info}</div>
                <div className="login_line_content">
                    <button ref="loginBut" className="button button-primary button-rounded button-small" onClick={this.handleRegister}><div ref="registerDiv">注&nbsp;册</div></button>
                </div>
                <div className="login_line_content">
                	<div className="login_a_content login_a_content_left"><a href="/src/page/login.html">登录</a></div>
                	<div className="login_a_content login_a_content_right"><a href="/src/page/retrieve.html">忘记密码</a></div>
                </div>
            </div>
        );
    }
}

module.exports = RegisterApp;