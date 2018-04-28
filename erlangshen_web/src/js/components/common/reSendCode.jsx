import BaseComponents from 'components/common/baseComponents.jsx';
import UserStore from 'stores/user.jsx';

class CommonReSendCodeApp extends BaseComponents {
    constructor(props) {
            super(props, UserStore);

            this.handleSendCode = this.handleSendCode.bind(this);
            this.reSendTime = this.reSendTime.bind(this);
            this.hidden = this.hidden.bind(this);
            this.show = this.show.bind(this);
    }

    handleSendCode() {
        UserStore.sendActivationMail("mail", this.props.userName, this.props.userId);

        this.refs.sendCodeBut.setAttribute('style', 'pointer-events:none');
        this.reSendTime(30);
    }

    reSendTime(time) {
        this.refs.sendCodeBut.innerHTML = "&nbsp;" + time + "秒后重试！";
        if(time >= 1) {
                this.timeoutId = setTimeout(() => {this.reSendTime(time-1)}, 1000)
        } else {
            this.refs.sendCodeBut.innerHTML = this.props.text;
            this.refs.sendCodeBut.removeAttribute('style');
        }
    }

    hidden() {
        this.refs.sendCodeBut.style.display = "none";
    }

    show() {
        this.refs.sendCodeBut.style.display = "";
    }

    render() {
        {
        return (
            <a ref="sendCodeBut" className={this.props.clsName} href="javascript:void(0);" onClick={this.handleSendCode}>{this.props.text}</a>
        );
        }
    }
}

module.exports = CommonReSendCodeApp;