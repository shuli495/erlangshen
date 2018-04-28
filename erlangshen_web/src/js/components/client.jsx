import BaseComponents from 'components/common/baseComponents.jsx';
import CommonPageApp from 'components/common/page.jsx';
import ClientStore from 'stores/client.jsx';

class ClientApp extends BaseComponents {
    constructor(props) {
        	super(props, ClientStore);

            this.selectedId = "";

            this.handleQuery = this.handleQuery.bind(this);
            this.handleInfo = this.handleInfo.bind(this);
            this.handleUpdate = this.handleUpdate.bind(this);
            this.handleDel = this.handleDel.bind(this);
            this.mailList = this.mailList.bind(this);
            this.mailAdd = this.mailAdd.bind(this);
            this.mailDel = this.mailDel.bind(this);
            this.phoneList = this.phoneList.bind(this);
            this.phoneAdd = this.phoneAdd.bind(this);
            this.phoneDel = this.phoneDel.bind(this);
            this.securityInfo = this.securityInfo.bind(this);
            this.securityUpdate = this.securityUpdate.bind(this);
    }

    componentDidMount() {
    	this.handleQuery(1);
    	super.reloading();
    }

    handleQuery(pageNum) {
        	ClientStore.page(pageNum, this.refs.schName.value);
    }

    handleInfo(id, name) {
        if(typeof(id) == "undefined" || id == "undefined" || typeof(id) == "object" || id == "") {
            this.selectedId= "";
            this.refs.name.value = "";

            this.refs.addBut.style.display = "";
            this.refs.updateBut.style.display = "none";
        } else {
            this.selectedId= id;
            this.refs.name.value = name;

            this.refs.addBut.style.display = "none";
            this.refs.updateBut.style.display = "";
        }
    }

    handleUpdate() {
        var name = this.refs.name.value;

        if(!$util_validateValue("client_info")) {
            return;
        }

        if(this.selectedId == "") {
            ClientStore.add(name);
        } else {
            ClientStore.update(this.selectedId, name);
        }
    }

    handleDel(id) {
        if(!window.confirm('是否确认删除？')) {
            return;
        }

        ClientStore.del(id);
    }

    mailList(clientId) {
        this.selectedId = clientId;
        this.refs.mail.value = "";
        this.refs.username.value = "";
        this.refs.pwd.value = "";
        this.refs.smtp.value = "";
        this.refs.mailType.value = "";
        this.refs.mailText.value = "";

        ClientStore.mailList(clientId);
    }

    mailAdd() {
        if(!$util_validateValue("mail_info")) {
            return;
        }

        ClientStore.mailAdd(this.selectedId, this.refs.mail.value, this.refs.username.value, this.refs.pwd.value, this.refs.smtp.value, this.refs.mailType.value, this.refs.subject.value, this.refs.mailText.value);
    }

    mailDel(clientId, id) {
        ClientStore.mailDel(clientId, id);
    }

    phoneList(clientId) {
        this.selectedId = clientId;
        this.refs.ak.value = "";
        this.refs.sk.value = "";
        this.refs.sign.value = "";
        this.refs.tmplate.value = "";
        this.refs.phoneType.value = "";

        ClientStore.phoneList(clientId);
    }

    phoneAdd() {
        if(!$util_validateValue("phone_info")) {
            return;
        }

        ClientStore.phoneAdd(this.selectedId, this.refs.platform.value, this.refs.ak.value, this.refs.sk.value, this.refs.sign.value, this.refs.tmplate.value, this.refs.phoneType.value);
    }

    phoneDel(clientId, id) {
        ClientStore.phoneDel(clientId, id);
    }

    securityInfo(clientId) {
        this.selectedId = clientId;
        ClientStore.mailList(clientId);
        ClientStore.phoneList(clientId);
        ClientStore.security(clientId);
    }

    securityUpdate() {
        var isCheckPlace;
        var checkPlacePriority;
        var isCheckPlatform;
        var checkPlatformType;
        var checkPlaceMailTypeId = this.refs.checkPlaceMailTypeId.value;
        var checkPlacePhoneTypeId = this.refs.checkPlacePhoneTypeId.value;
        var loginApi = this.refs.loginApi.value;

        for(var i=0; i<document.getElementsByName("isCheckPlace").length; i++) {
            if(document.getElementsByName("isCheckPlace")[i].checked) {
                isCheckPlace = document.getElementsByName("isCheckPlace")[i].value;
            }
        }
        for(var i=0; i<document.getElementsByName("checkPlacePriority").length; i++) {
            if(document.getElementsByName("checkPlacePriority")[i].checked) {
                checkPlacePriority = document.getElementsByName("checkPlacePriority")[i].value;
            }
        }
        for(var i=0; i<document.getElementsByName("isCheckPlatform").length; i++) {
            if(document.getElementsByName("isCheckPlatform")[i].checked) {
                isCheckPlatform = document.getElementsByName("isCheckPlatform")[i].value;
            }
        }
        for(var i=0; i<document.getElementsByName("checkPlatformType").length; i++) {
            if(document.getElementsByName("checkPlatformType")[i].checked) {
                checkPlatformType = document.getElementsByName("checkPlatformType")[i].value;
            }
        }
        ClientStore.securityUpdate(this.selectedId, isCheckPlace, checkPlacePriority, checkPlacePhoneTypeId, checkPlaceMailTypeId, isCheckPlatform, checkPlatformType, loginApi);
    }

    render(){
        {
            var clientsDOM = [];
            for (var i = 0; i < this.state.clients.length; i++) {

                var id = typeof(this.state.clients[i].id) == "undefined" ? "" : this.state.clients[i].id;
                var name = typeof(this.state.clients[i].name) == "undefined" ? "" : this.state.clients[i].name;
                var setMail = this.state.clients[i].setMailNum>0 ? "是" : <div className="errorMsg">否</div>;
                var setPhone = this.state.clients[i].setPhoneNum>0 ? "是" : <div className="errorMsg">否</div>;

                clientsDOM.push(
                                        <tr>
                                            <td>
                                                <a href="javascript:void(0);" className="fa fa-pencil-square-o" data-toggle="modal" data-target="#client_info" title="修改" onClick={this.handleInfo.bind(this,id,name)}></a>&nbsp;
                                                <a href="javascript:void(0);" className="fa fa-envelope" data-toggle="modal" data-target="#mail_info" title="设置邮件信息" onClick={this.mailList.bind(this,id)}></a>&nbsp;
                                                <a href="javascript:void(0);" className="fa fa-comments" data-toggle="modal" data-target="#phone_info" title="设置短信发送平台" onClick={this.phoneList.bind(this,id)}></a>&nbsp;
                                                <a href="javascript:void(0);" className="fa fa-shield" data-toggle="modal" data-target="#security_info" title="安全设置" onClick={this.securityInfo.bind(this,id)}></a>&nbsp;
                                                <a href="javascript:void(0);" className="fa fa-times errorBut" title="删除" onClick={this.handleDel.bind(this,id)}></a>
                                            </td>
                                            <td>{id}</td>
                                            <td>{name}</td>
                                            <td>{setMail}</td>
                                            <td>{setPhone}</td>
                                        </tr>
                                      );
            }

            // mail
            var mailsDom = [];
            var securityMails = []; //安全页面邮件类型列表
            for(var i = 0; i < this.state.mails.length; i++) {
                var mailData = this.state.mails[i];
                mailsDom.push(
                    <tr>
                        <td>
                            <a href="javascript:void(0);" className="fa fa-times errorBut" title="删除" onClick={this.mailDel.bind(this,mailData.clientId,mailData.id)}></a>
                        </td>
                        <td >{mailData.mail}</td>
                        <td>{mailData.username}</td>
                        <td>{mailData.smtp}</td>
                        <td>{mailData.type}</td>
                        <td><div  className="cell_text">{mailData.text}</div></td>
                    </tr>
                    );
                securityMails.push(
                    <option value={mailData.id}>{mailData.type}</option>
                    );
            }

            // phone
            var phonesDom = [];
            var securityPhones = []; //安全页面手机类型列表
            for(var i = 0; i < this.state.phones.length; i++) {
                var phoneData = this.state.phones[i];
                phonesDom.push(
                    <tr>
                        <td>
                            <a href="javascript:void(0);" className="fa fa-times errorBut" title="删除" onClick={this.phoneDel.bind(this,phoneData.clientId,phoneData.id)}></a>
                        </td>
                        <td >{phoneData.platform}</td>
                        <td >{phoneData.ak}</td>
                        <td>{phoneData.sign}</td>
                        <td>{phoneData.tmplate}</td>
                        <td>{phoneData.type}</td>
                    </tr>
                    );
                securityPhones.push(
                    <option value={phoneData.id}>{phoneData.type}</option>
                    );
            }

            // security
            var security = this.state.security;
            if(security.isCheckPlace && this.refs.isCheckPlaceTrue) {
                this.refs.isCheckPlaceTrue.checked = "checked";
            } else if(!security.isCheckPlace && this.refs.isCheckPlaceFalse) {
                this.refs.isCheckPlaceFalse.checked = "checked";
            }
            for(var i=0; i<document.getElementsByName("checkPlacePriority").length; i++) {
                if(document.getElementsByName("checkPlacePriority")[i].value == security.checkPlacePriority) {
                    document.getElementsByName("checkPlacePriority")[i].checked = "checked";
                }
            }
            for(var i=0; i<document.getElementsByName("isCheckPlatform").length; i++) {
                if(document.getElementsByName("isCheckPlatform")[i].value == security.isCheckPlatform) {
                    document.getElementsByName("isCheckPlatform")[i].checked = "checked";
                }
            }
            for(var i=0; i<document.getElementsByName("checkPlatformType").length; i++) {
                if(document.getElementsByName("checkPlatformType")[i].value == security.checkPlatformType) {
                    document.getElementsByName("checkPlatformType")[i].checked = "checked";
                }
            }
            if(security.loginApi) {
                this.refs.loginApi.value = security.loginApi;
            }
            if(this.refs.checkPlaceMailTypeId && security.checkPlaceMailTypeId) {
                this.refs.checkPlaceMailTypeId.value = security.checkPlaceMailTypeId;
            }
            if(this.refs.checkPlacePhoneTypeId && security.checkPlacePhoneTypeId) {
                this.refs.checkPlacePhoneTypeId.value = security.checkPlacePhoneTypeId;
            }
        }

        return (
            <div className="content table-responsive">
                <div className="search_content">
                    <input type="text" placeholder="名称" ref="schName"/>
                    <button className="button button-primary button-circle button-small" onClick={this.handleQuery.bind(this,1)}><i className="fa fa-search"></i></button>
                </div>
                <table className="table table-bordered table-hover">
                    <thead>
                        <tr>
                            <th className="width105"><a href="javascript:void(0);" className="fa fa-plus" data-toggle="modal" data-target="#client_info" onClick={this.handleInfo} title="添加"></a></th>
                            <th>ID</th>
                            <th>名称</th>
                            <th>是否设置邮箱</th>
                            <th>是否设置短信</th>
                        </tr>
                    </thead>
                    <tbody>
                        {clientsDOM}
                    </tbody>
                </table>

                <CommonPageApp total={this.state.total} pageSize={this.state.pageSize} pageNum={this.state.pageNum} handleQuery={this.handleQuery} />
                <div className="modal fade" id="client_info" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
                    <div className="modal-dialog">
                        <div className="modal-content">
                            <div className="modal-header">
                                <button type="button" className="close" id="info_close" data-dismiss="modal" aria-hidden="true">&times;</button>
                            </div>
                            <div className="modal-body search_content add_content show_body">
                                <form className="form-horizontal">
                                    <div className="form-from">
                                        <label class="col-sm-2 control-label"><span className="errorMsg">*&nbsp;</span>名称</label>
                                        <div className="col-sm-9">
                                          <input type="text" placeholder="1-16个汉字或字母" ref="name" data-errMsgId="nameError" data-empty="true" data-emptyText="名称不能为空" data-max="16" data-maxText="1-16个字符"/>
                                           <div id="nameError" className="errorMsg"></div>
                                        </div>
                                    </div>
                                </form>
                            </div>
                            <div className="modal-footer">
                                <button type="button" className="button button-primary button-rounded button-small" ref="addBut" onClick={this.handleUpdate}>添加</button>&nbsp;
                                <button type="button" className="button button-primary button-rounded button-small" ref="updateBut" onClick={this.handleUpdate}>修改</button>
                            </div>
                        </div>
                    </div>
                </div>

                <div className="modal fade" id="mail_info" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
                    <div className="modal-dialog dialog">
                        <div className="modal-content">
                            <div className="modal-header">
                                <button type="button" className="close" id="info_close" data-dismiss="modal" aria-hidden="true">&times;</button>
                            </div>
                            <div className="modal-body search_content add_content show_body">
                                <form className="form-horizontal">
                                    <div className="form-from">
                                        <label class="col-sm-2 control-label">邮箱</label>
                                        <div className="col-sm-9">
                                          <input type="text" placeholder="发送认证邮件的邮箱" ref="mail" data-errMsgId="mailError" data-empty="true" data-emptyText="邮箱不能为空" data-max="32" data-maxText="1-32个字符" data-mail="true"/>
                                           <div id="mailError" className="errorMsg"></div>
                                        </div>
                                    </div>
                                    <div className="form-from">
                                        <label class="col-sm-2 control-label">邮箱账号</label>
                                        <div className="col-sm-9">
                                          <input type="text" placeholder="邮箱账号" ref="username" data-errMsgId="usernameError" data-max="64" data-maxText="1-64个字符"/>
                                           <div id="usernameError" className="errorMsg"></div>
                                        </div>
                                    </div>
                                    <div className="form-from">
                                        <label class="col-sm-2 control-label">邮箱密码</label>
                                        <div className="col-sm-9">
                                          <input type="password" placeholder="邮箱密码" ref="pwd" data-errMsgId="pwdError" data-empty="true" data-emptyText="邮箱密码不能为空" data-max="64" data-maxText="1-64个字符"/>
                                           <div id="pwdError" className="errorMsg"></div>
                                        </div>
                                    </div>
                                    <div className="form-from">
                                        <label class="col-sm-2 control-label">邮箱smtp</label>
                                        <div className="col-sm-9">
                                          <input type="text" placeholder="邮箱smtp" ref="smtp" data-errMsgId="smtpError" data-max="64" data-maxText="1-64个字符"/>
                                           <div id="smtpError" className="errorMsg"></div>
                                        </div>
                                    </div>
                                    <div className="form-from">
                                        <label class="col-sm-2 control-label">类型</label>
                                        <div className="col-sm-9">
                                          <input type="text" ref="mailType" data-errMsgId="mailTypeError" data-empty="true" data-emptyText="类型不能为空" data-max="10" data-maxText="1-10个字符"/>
                                           <div id="mailTypeError" className="errorMsg"></div>
                                        </div>
                                    </div>
                                    <div className="form-from">
                                        <label class="col-sm-2 control-label">标题</label>
                                        <div className="col-sm-9">
                                          <input type="text" ref="subject" data-errMsgId="subjectError" data-empty="true" data-emptyText="标题不能为空" data-max="10" data-maxText="1-10个字符"/>
                                           <div id="subjectError" className="errorMsg"></div>
                                        </div>
                                    </div>
                                    <div className="form-from">
                                        <label class="col-sm-2 control-label">邮件内容</label>
                                        <div className="col-sm-9">
                                          <textarea ref="mailText" data-errMsgId="mailTextError" data-empty="true" data-emptyText="邮件内容不能为空" data-max="21000" data-maxText="1-21000个字符"/>
                                           <div id="mailTextError" className="errorMsg"></div>
                                        </div>
                                    </div>
                                </form>
                            </div>
                            <table className="table table-bordered table-hover">
                                <thead>
                                    <tr>
                                        <th className="width5"></th>
                                        <th className="width200">邮箱</th>
                                        <th className="width200">账号</th>
                                        <th className="width200">SMTP</th>
                                        <th className="width50">类型</th>
                                        <th>邮件内容</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {mailsDom}
                                </tbody>
                            </table>
                            <div className="modal-footer">
                                <button type="button" className="button button-primary button-rounded button-small" ref="addMailBut" onClick={this.mailAdd}>添加</button>&nbsp;
                            </div>
                        </div>
                    </div>
                </div>

                <div className="modal fade" id="phone_info" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
                    <div className="modal-dialog dialog">
                        <div className="modal-content">
                            <div className="modal-header">
                                <button type="button" className="close" id="info_close" data-dismiss="modal" aria-hidden="true">&times;</button>
                            </div>
                            <div className="modal-body search_content add_content show_body">
                                <form className="form-horizontal">
                                    <div className="form-from">
                                        <label class="col-sm-2 control-label">平台</label>
                                        <div className="col-sm-9">
                                            <select ref="platform">
                                                <option value="aliyun" selected>阿里云</option>
                                            </select>
                                        </div>
                                    </div>
                                    <div className="form-from">
                                        <label class="col-sm-2 control-label">AK</label>
                                        <div className="col-sm-9">
                                          <input type="text" ref="ak" data-errMsgId="akError" data-empty="true" data-emptyText="AK不能为空" data-max="64" data-maxText="1-64个字符"/>
                                           <div id="akError" className="errorMsg"></div>
                                        </div>
                                    </div>
                                    <div className="form-from">
                                        <label class="col-sm-2 control-label">SK</label>
                                        <div className="col-sm-9">
                                          <input type="text" ref="sk" data-errMsgId="skError" data-empty="true" data-emptyText="SK不能为空" data-max="64" data-maxText="1-64个字符"/>
                                           <div id="skError" className="errorMsg"></div>
                                        </div>
                                    </div>
                                    <div className="form-from">
                                        <label class="col-sm-2 control-label">签名</label>
                                        <div className="col-sm-9">
                                          <input type="text" ref="sign" data-errMsgId="signError" data-empty="true" data-emptyText="签名不能为空" data-max="64" data-maxText="1-64个字符"/>
                                           <div id="signError" className="errorMsg"></div>
                                        </div>
                                    </div>
                                    <div className="form-from">
                                        <label class="col-sm-2 control-label">模板</label>
                                        <div className="col-sm-9">
                                          <input type="text" ref="tmplate" data-errMsgId="tmplateError" data-empty="true" data-emptyText="模板不能为空" data-max="64" data-maxText="1-64个字符"/>
                                           <div id="tmplateError" className="errorMsg"></div>
                                        </div>
                                    </div>
                                    <div className="form-from">
                                        <label class="col-sm-2 control-label">类型</label>
                                        <div className="col-sm-9">
                                          <input type="text" ref="phoneType" data-errMsgId="phoneTypeError" data-empty="true" data-emptyText="类型不能为空" data-max="10" data-maxText="1-10个字符"/>
                                           <div id="phoneTypeError" className="errorMsg"></div>
                                        </div>
                                    </div>
                                    <div className="form-from">
                                        <label class="col-sm-2 control-label">短信内容</label>
                                        <div className="col-sm-9">
                                          <textarea ref="phoneText" data-errMsgId="phoneTextError" data-empty="true" data-emptyText="短信内容不能为空" data-max="70" data-maxText="1-70个字符"/>
                                           <div id="phoneTextError" className="errorMsg"></div>
                                        </div>
                                    </div>
                                </form>
                            </div>
                            <table className="table table-bordered table-hover">
                                <thead>
                                    <tr>
                                        <th className="width5"></th>
                                        <th className="width100">平台</th>
                                        <th className="width200">AK</th>
                                        <th className="width100">签名</th>
                                        <th className="width100">模板</th>
                                        <th className="width50">类型</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {phonesDom}
                                </tbody>
                            </table>
                            <div className="modal-footer">
                                <button type="button" className="button button-primary button-rounded button-small" ref="addPhoneBut" onClick={this.phoneAdd}>添加</button>&nbsp;
                            </div>
                        </div>
                    </div>
                </div>

                <div className="modal fade" id="security_info" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
                    <div className="modal-dialog dialog">
                        <div className="modal-content">
                            <div className="modal-header">
                                <button type="button" className="close" id="info_close" data-dismiss="modal" aria-hidden="true">&times;</button>
                            </div>
                            <div className="modal-body search_content add_content show_body">
                                <form className="form-horizontal">
                                    <div className="form-from">
                                        <label class="col-sm-2 control-label">异地登录告警</label>
                                        <div className="col-sm-9 cell_radio">
                                            <input  type="radio" value="true" name="isCheckPlace" ref="isCheckPlaceTrue"/>开启
                                            <p><input type="radio" value="false" name="isCheckPlace" ref="isCheckPlaceFalse"/>关闭</p>
                                        </div>
                                    </div>
                                    <div className="form-from">
                                        <label class="col-sm-2 control-label">异地登录告警 -邮件- 通知类型</label>
                                        <div className="col-sm-9">
                                            <select ref="checkPlaceMailTypeId">
                                                <option value=""></option>
                                                {securityMails}
                                            </select>
                                        </div>
                                    </div>
                                    <div className="form-from">
                                        <label class="col-sm-2 control-label">异地登录告警 -短信- 通知类型</label>
                                        <div className="col-sm-9">
                                            <select ref="checkPlacePhoneTypeId">
                                                <option value=""></option>
                                                {securityPhones}
                                            </select>
                                        </div>
                                    </div>
                                    <div className="form-from">
                                        <label class="col-sm-2 control-label">登录通知接口</label>
                                        <div className="col-sm-9">
                                            <input type="text" className="width700"  ref="loginApi"/>
                                        </div>
                                    </div>
                                    <div className="form-from">
                                        <label class="col-sm-2 control-label">通知优先级</label>
                                        <div className="col-sm-9 cell_radio">
                                            <input type="radio" value="0" name="checkPlacePriority"/>都通知
                                            <p><input type="radio" value="1" name="checkPlacePriority"/>手机优先，如果失败邮件通知</p>
                                            <p><input type="radio" value="2" name="checkPlacePriority"/>邮件优先，如果失败手机通知</p>
                                        </div>
                                    </div>
                                    <div className="form-from">
                                        <label class="col-sm-2 control-label">登录平台检查</label>
                                        <div className="col-sm-9 cell_radio">
                                            <input type="radio" value="0" name="isCheckPlatform"/>多平台多账号可同时登陆
                                            <p><input type="radio" value="1" name="isCheckPlatform"/>可以多平台登录，同一平台只能1个账号在线</p>
                                            <p><input type="radio" value="2" name="isCheckPlatform"/>所有平台只能1个账号在线</p>
                                        </div>
                                    </div>
                                    <div className="form-from">
                                        <label class="col-sm-2 control-label">登录冲突操作</label>
                                        <div className="col-sm-9 cell_radio">
                                            <input type="radio" value="0" name="checkPlatformType"/>登出之前登陆的账号
                                            <p><input type="radio" value="1" name="checkPlatformType"/>新登陆请求失败</p>
                                        </div>
                                    </div>
                                </form>
                            </div>
                            <div className="modal-footer">
                                <button type="button" className="button button-primary button-rounded button-small" ref="addPhoneBut" onClick={this.securityUpdate}>保存</button>&nbsp;
                            </div>
                        </div>
                    </div>
                </div>

            </div>
        );
    }
}

module.exports = ClientApp;