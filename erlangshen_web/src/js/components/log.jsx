import BaseComponents from 'components/common/baseComponents.jsx';
import CommonClientApp from 'components/common/client.jsx';
import LogStore from 'stores/log.jsx';

class LogApp extends BaseComponents {
    constructor(props) {
        super(props, LogStore);

        this.handleQuery = this.handleQuery.bind(this);
    }

    componentDidMount() {
        this.handleQuery(1);
        super.reloading();
    }

    handleQuery(pageNum) {
        LogStore.page(pageNum,this.refs.schUsername.value, this.refs.schNickname.value, this.refs.schMail.value, this.refs.schPhone.value, this.refs.schClientApp.refs.clientId.value);
    }

    render(){
        {
            var logsDOM = [];
            for (var i = 0; i < this.state.logs.length; i++) {
                var username = typeof(this.state.logs[i].username) == "undefined" ? "" : this.state.logs[i].username;
                var nickname = typeof(this.state.logs[i].nickname) == "undefined" ? "" : this.state.logs[i].nickname;
                var mail = typeof(this.state.logs[i].mail) == "undefined" ? "" : this.state.logs[i].mail;
                var phone = typeof(this.state.logs[i].phone) == "undefined" ? "" : this.state.logs[i].phone;
                var clientName = typeof(this.state.logs[i].clientName) == "undefined" ? "" : this.state.logs[i].clientName;
                var ip = typeof(this.state.logs[i].ip) == "undefined" ? "" : this.state.logs[i].ip;
                var loginTime = typeof(this.state.logs[i].loginTime) == "undefined" ? "" : this.state.logs[i].loginTime;
                loginTime = $util_timestampToTime(loginTime)

                logsDOM.push(
                                        <tr>
                                            <td>{username}</td>
                                            <td>{nickname}</td>
                                            <td>{mail}</td>
                                            <td>{phone}</td>
                                            <td>{clientName}</td>
                                            <td>{ip}</td>
                                            <td>{loginTime}</td>
                                        </tr>
                                      );
            }
        }

        return (
            <div className="content">
                <div className="search_content">
                    <input type="text" placeholder="用户名" ref="schUsername" />
                    <input type="text" placeholder="昵称" ref="schNickname" />
                    <input type="text" placeholder="邮箱" ref="schMail" />
                    <input type="text" placeholder="手机号" ref="schPhone" />
                    <CommonClientApp ref="schClientApp"/>
                    <button className="button button-primary button-circle button-small" onClick={this.handleQuery.bind(this,1)}><i className="fa fa-search"></i></button>
                </div>

                <table className="table table-bordered table-hover">
                    <thead>
                        <tr>
                            <th>用户名</th>
                            <th>昵称</th>
                            <th>邮箱</th>
                            <th>手机号</th>
                            <th>应用</th>
                            <th>IP</th>
                            <th>登录时间</th>
                        </tr>
                    </thead>
                    <tbody>
                        {logsDOM}
                    </tbody>
                </table>

            </div>
        );
    }
}

module.exports = LogApp;