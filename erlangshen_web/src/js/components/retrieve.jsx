import BaseComponents from 'components/common/baseComponents.jsx';
import UserStore from 'stores/user.jsx';

class RetrieveApp extends BaseComponents {
    constructor(props) {
        super(props, UserStore);

        this.sendCode = this.sendCode.bind(this);
        this.handleRetrieve = this.handleRetrieve.bind(this);
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
            UserStore.retrieveChkUser("retrieve", userName, "", false);
        } else {
            UserStore.retrieveChkUser("retrieve", "", userName, false);
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

    // 找回密码
    handleRetrieve() {
        var code = this.refs.code.value;
        var pwd = this.refs.pwd.value;

        if(!$util_validateValue("retrieve")) {
            return;
        }

        // 按钮加载状态
        var retrieveDiv = this.refs.retrieveDiv;
        retrieveDiv.innerHTML = "";
        retrieveDiv.style.left = "47%";
        retrieveDiv.classList.add("loading_circle");
        this.refs.retrieveBut.disabled = true;

        UserStore.retrieve(this.state.retrieveId, code, pwd);
    }

    componentDidUpdate() {
        // 恢复按钮状态
        var retrieveDiv = this.refs.retrieveDiv;
        retrieveDiv.innerHTML = "找&nbsp;回";
        retrieveDiv.classList.remove("loading_circle");
        this.refs.retrieveBut.disabled = false;
        super.reloading();
    }

    render(){
        {
            var info = this.state.info;
        }

        return (
            <div>
                <div id="retrieve" className="login_content login_input_content login_button_content">
                    <div className="login_line_content"><i className="fa fa-eye eye_icon"></i></div>
                    <div className="login_line_content">
                        <input type="text" ref="userName" placeholder="邮箱或手机号码" data-errMsgId="errorMsg" data-mailOrPhone="true" data-empty="true" data-emptyText="账号不能为空！"  data-max="32" data-maxText="最多32个字符！"/>
                    </div>
                    <button ref="sendBut" className="send_but button button-tiny" onClick={this.sendCode}>发送验证码</button>
                    <div className="login_line_content">
                        <input type="text" ref="code" placeholder="验证码" data-errMsgId="errorMsg" data-empty="true" data-emptyText="验证码不能为空！"/>
                    </div>
                    <div className="login_line_content">
                        <input type="password" id="pwd" ref="pwd" placeholder="密码（6~16个字符，区分大小写）" data-errMsgId="errorMsg" data-empty="true" data-emptyText="密码不能为空！"  data-min="6" data-minText="密码6-16个字符！"  data-max="16" data-maxText="密码最多16个字符！"/>
                    </div>
                    <div className="login_line_content">
                        <input type="password" ref="rePwd" placeholder="确认密码" data-errMsgId="errorMsg" data-empty="true" data-emptyText="确认密码不能为空！" data-min="6" data-minText="确认密码6-16个字符！"  data-max="16" data-maxText="确认密码最多16个字符！" data-same="pwd" data-sameText="与密码不同，请重新输入！"/>
                    </div>
                    <div id="errorMsg" className="errorMsg">{info}</div>
                    <div className="login_line_content">
                        <button ref="retrieveBut" className="button button-primary button-rounded button-small" onClick={this.handleRetrieve}><div ref="retrieveDiv">找&nbsp;回</div></button>
                    </div>
                    <div className="login_line_content">
                        <div className="login_a_content login_a_content_left"><a href="/src/page/login.html">登录</a></div>
                        <div className="login_a_content login_a_content_right"><a href="/src/page/register.html">注册新账号</a></div>
                    </div>
                </div>
            </div>
        );
    }
}

module.exports = RetrieveApp;