import BaseComponents from 'components/common/baseComponents.jsx';
import UserStore from 'stores/user.jsx';

class LoginApp extends BaseComponents {
    constructor(props) {
        super(props, UserStore);

        this.handleClick = this.handleClick.bind(this);
        this.getCodeImg = this.getCodeImg.bind(this);
    }

    // 登录
    handleClick() {
        var userName = this.refs.userName.value;
        var pwd = this.refs.pwd.value;
        var verifyCode = "";
        if(this.refs.verifyCode) {
            verifyCode = this.refs.verifyCode.value;
        }

        if(!$util_validateValue("login")) {
            return;
        }

        // 按钮加载状态
        var loginDiv = this.refs.loginDiv;
        loginDiv.innerHTML = "";
        loginDiv.style.left = "47%";
        loginDiv.classList.add("loading_circle");
        this.refs.loginBut.disabled = true;

        UserStore.login(userName, pwd, verifyCode);
    }

    componentDidUpdate() {
        // 恢复按钮状态
        var loginDiv = this.refs.loginDiv;
        loginDiv.innerHTML = "登&nbsp;录";
        loginDiv.classList.remove("loading_circle");
        this.refs.loginBut.disabled = false;
        super.reloading();
    }

    getCodeImg() {
        UserStore.code("login");
    }

    render(){
        {
            var info = this.state.info;
            var verifyCodeImage = this.state.codeImage;
            var verifyCode;
            if(verifyCodeImage != "") {
                verifyCodeImage = "data:image/jpg;base64," + verifyCodeImage;
                verifyCode = <div className="login_line_content code_container">
                                            <input type="text" ref="verifyCode" placeholder="验证码" data-errMsgId="errorMsg" data-empty="true" data-emptyText="验证码不能为空！"/>
                                            <img src={verifyCodeImage} onClick={this.getCodeImg} />
                                        </div>
            }
        }

        return (
            <div id="login" className="login_content login_input_content login_button_content">
                <div className="login_line_content"><i className="fa fa-eye eye_icon"></i></div>
                <div className="login_line_content">
                    <input type="text" ref="userName" placeholder="邮箱或手机号码" data-errMsgId="errorMsg" data-empty="true" data-emptyText="账号不能为空！" />
                </div>
                <div className="login_line_content">
                    <input type="password" ref="pwd" placeholder="密码" data-errMsgId="errorMsg" data-empty="true" data-emptyText="密码不能为空！"/>
                </div>
                {verifyCode}
                <div id="errorMsg" className="errorMsg">{info}</div>
                <div className="login_line_content">
                    <button  ref="loginBut" className="button button-primary button-rounded button-small" onClick={this.handleClick}><div ref="loginDiv">登&nbsp;录</div></button>
                </div>
                <div className="login_lint">
                    * 测试账号 guest/guest
                </div>
                <div className="login_line_content">
                    <div className="login_a_content login_a_content_left"><a href="/src/page/retrieve.html">忘记密码</a></div>
                    <div className="login_a_content login_a_content_right"><a href="/src/page/register.html">注册新账号</a></div>
                </div>
            </div>
        );
    }
}

module.exports = LoginApp;