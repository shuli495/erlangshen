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
        var code = "";
        if(this.refs.code) {
            code = this.refs.code.value;
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

        UserStore.login(userName, pwd, code);
    }

    componentDidUpdate() {
        // 恢复按钮状态
        var loginDiv = this.refs.loginDiv;
        loginDiv.innerHTML = "登&nbsp;录";
        loginDiv.classList.remove("loading_circle");
        this.refs.loginBut.disabled = false;
        super.reloading();
    }

    getCodeImg(userName) {
        UserStore.code(userName);
    }

    render(){
        {
            var info = this.state.info;
            var codeImage = this.state.codeImage;
            var codeDom;
            if(codeImage != "") {
                codeImage = "data:image/jpg;base64," + codeImage;
                codeDom = <div className="login_line_content code_container">
                                            <input type="text" ref="code" placeholder="验证码" data-errMsgId="errorMsg" data-empty="true" data-emptyText="验证码不能为空！"/>
                                            <img src={codeImage} onClick={this.getCodeImg.bind(this,  this.refs.userName.value)} />
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
                {codeDom}
                <div id="errorMsg" className="errorMsg">{info}</div>
                <div className="login_line_content">
                    <button  ref="loginBut" className="button button-primary button-rounded button-small" onClick={this.handleClick}><div ref="loginDiv">登&nbsp;录</div></button>
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