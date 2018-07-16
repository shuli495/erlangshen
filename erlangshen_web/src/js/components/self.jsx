import BaseComponents from 'components/common/baseComponents.jsx';
import UserStore from 'stores/user.jsx';

class SelfApp extends BaseComponents {
    constructor(props) {
        super(props, UserStore);

        this.handleRepwd = this.handleRepwd.bind(this);
        this.handleLoginOut = this.handleLoginOut.bind(this);
    }

    handleRepwd() {
        if(!$util_validateValue("self")) {
            return;
        }

        UserStore.repwd($.cookie('userId'), "", this.refs.pwd.value, this.refs.newPwd.value);
    }

    handleLoginOut() {
        $util_removeCookie();
    }

    render(){
        return (
            <div id="self" className="login_content login_input_content login_button_content">
                <div className="login_line_content">
                    <input type="password" ref="pwd" placeholder="密码" data-errMsgId="error" data-empty="true" data-emptyText="密码不能为空！" data-min="6" data-minText="密码6-16个字符！"  data-max="16" data-maxText="密码最多16个字符！"/>
                </div>
                <div className="login_line_content">
                    <input type="password" id="newPwd" ref="newPwd" placeholder="新密码" data-errMsgId="error" data-empty="true" data-emptyText="新密码不能为空！" data-min="6" data-minText="新密码6-16个字符！"  data-max="16" data-maxText="新密码最多16个字符！"/>
                </div>
                <div className="login_line_content">
                    <input type="password" ref="rePwd" placeholder="确认密码" data-errMsgId="error" data-empty="true" data-emptyText="确认密码不能为空！" data-min="6" data-minText="确认密码6-16个字符！"  data-max="16" data-maxText="确认密码最多16个字符！" data-same="newPwd" data-sameText="与新密码不同，请重新输入！"/>
                </div>

                <div id="error" className="errorMsg"></div>
                <div className="login_line_content"><button className="button button-primary button-rounded button-small" onClick={this.handleRepwd}>修&nbsp;改</button></div>
                <div className="login_line_content"><a href="javascript:void(0);" onClick={this.handleLoginOut}>退出登录</a></div>
            </div>
        );
    }
}

module.exports = SelfApp;