import BaseComponents from 'components/common/baseComponents.jsx';
import CommonPageApp from 'components/common/page.jsx';
import CommonClientApp from 'components/common/client.jsx';
import CommonAddressApp from 'components/common/address.jsx';
import CommonReSendCodeApp from 'components/common/reSendCode.jsx';
import UserStore from 'stores/user.jsx';

class UserApp extends BaseComponents {
    constructor(props) {
            super(props, UserStore);

            this.selectedUserId = "";

            this.handleQuery = this.handleQuery.bind(this);
            this.handleInfo = this.handleInfo.bind(this);
            this.handleUpdate = this.handleUpdate.bind(this);
            this.handleDel = this.handleDel.bind(this);
            this.queryCertification = this.queryCertification.bind(this);
            this.certificationUpload = this.certificationUpload.bind(this);
            this.certificationConfirm = this.certificationConfirm.bind(this);
    }

    componentDidMount() {
        this.handleQuery(1);
        super.reloading();
    }

    handleQuery(pageNum) {
        UserStore.page(pageNum,this.refs.schSource.value,this.refs.schUsername.value,this.refs.schNickname.value,this.refs.schMail.value,this.refs.schPhone.value,this.refs.schTel.value,
            this.refs.schQQ.value,this.refs.schWeixin.value,this.refs.schWeibo.value,this.refs.schName.value,this.refs.schIdcard.value,this.refs.schSex.value,this.refs.schStatus.value,
            this.refs.schAddressApp.refs.province.value,this.refs.schAddressApp.refs.city.value,this.refs.schAddressApp.refs.area.value,this.refs.schAddressApp.refs.address.value,
            this.refs.schClientApp.refs.clientId.value);
    }

    handleInfo(id,status,source,username,nickname,mail,phone,tel,qq,weixin,weibo,name,idcard,clientId,province,city,area,address) {
        if(typeof(id) == "undefined" || id == "undefined" || typeof(id) == "object" || id == "") {
            this.selectedUserId = "";
            this.selectedUserStatus = "";

            this.refs.pwd.value= "";
            this.refs.source.value = "";
            this.refs.username.value = "";
            this.refs.nickname.value = "";
            this.refs.mail.value = "";
            this.refs.phone.value = "";
            this.refs.tel.value = "";
            this.refs.qq.value = "";
            this.refs.weixin.value = "";
            this.refs.weibo.value = "";
            this.refs.name.value = "";
            this.refs.idcard.value = "";
            this.refs.address.refs.province.value = "";
            this.refs.address.refs.city.value = "";
            this.refs.address.refs.area.value = "";
            this.refs.address.refs.address.value = "";
            this.refs.client.refs.clientId.value = "";

            this.refs.addBut.style.display = "";
            this.refs.sendMailBut.hidden();
            this.refs.updateBut.style.display = "none";

            this.refs.pwdContainer.style.display = "";
            this.refs.pwd.setAttribute("data-disabled","false");

            $("#pwdContainer").show();
        } else {
            this.selectedUserId = id;
            this.selectedUserStatus = status;

            this.refs.source.value = source;
            this.refs.username.value = username;
            this.refs.nickname.value = nickname;
            this.refs.mail.value = mail;
            this.refs.phone.value = phone;
            this.refs.tel.value = tel;
            this.refs.qq.value = qq;
            this.refs.weixin.value = weixin;
            this.refs.weibo.value = weibo;
            this.refs.name.value = name;
            this.refs.idcard.value = idcard;
            this.refs.address.refs.province.value = province;
            this.refs.address.handleSelectProvince(city, area);
            this.refs.address.handleSelectCity(area);
            this.refs.address.refs.address.value = address;
            this.refs.client.selected(clientId);

            this.refs.addBut.style.display = "none";
            this.refs.sendMailBut.show();
            this.refs.updateBut.style.display = "";

            this.refs.pwdContainer.style.display = "none";
            this.refs.pwd.setAttribute("data-disabled","true");

            $("#pwdContainer").hide();
        }
    }

    handleUpdate() {
        var source = this.refs.source.value;
        var username = this.refs.username.value;
        var nickname = this.refs.nickname.value;
        var mail = this.refs.mail.value;
        var phone = this.refs.phone.value;
        var tel = this.refs.tel.value;
        var qq = this.refs.qq.value;
        var weixin = this.refs.weixin.value;
        var weibo = this.refs.weibo.value;
        var name = this.refs.name.value;
        var idcard = this.refs.idcard.value;
        var sex = this.refs.sex.value;
        var province = this.refs.address.refs.province.value;
        var city = this.refs.address.refs.city.value;
        var area = this.refs.address.refs.area.value;
        var address = this.refs.address.refs.address.value;
        var clientId = this.refs.client.refs.clientId.value;

        if(!$util_validateValue("user_info")) {
            return;
        }
        if(clientId == "") {
            $("#clientError").html("所属应用必选");
            return;
        } else {
            $("#clientError").html("");
        }

        var id = this.selectedUserId;
        if(typeof(id) == "undefined" || id == "undefined" || id == "") {
            var pwd = this.refs.pwd.value;
            if(pwd == "") {
                $("#pwdError").html("密码不能为空");
                return;
            } else {
                $("#pwdError").html("");
            }
            UserStore.add(source,username,pwd,nickname,mail,phone,tel,qq,weixin,weibo,name,idcard,sex,clientId,province,city,area,address);
        } else {
            UserStore.update(id,source,username,nickname,mail,phone,tel,qq,weixin,weibo,name,idcard,sex,clientId,province,city,area,address);
        }
    }

    handleDel(id) {
        if(!window.confirm('是否确认删除？')) {
            return;
        }

        UserStore.del(id);
    }

    queryCertification(id,name,idcard,certificationStr,certificationFailMsg) {
        this.selectedUserId = id;
        this.refs.certificationName.value = name;
        this.refs.certificationIdcard.value = idcard;

        if(certificationFailMsg && certificationFailMsg != "") {
            certificationStr +=  "(<span class='errorMsg'>" + certificationFailMsg + "</span>)";
        }
        this.refs.certificationDiv.innerHTML = certificationStr;

        UserStore.findCertification(id);
    }

    certificationUpload() {
        if(!$util_validateValue("user_certification")) {
            return;
        }

        UserStore.uploadIdcard("certificationForm", this.selectedUserId);
    }

    certificationConfirm(certification) {
        UserStore.confirmCertification(this.selectedUserId, certification);
    }

    render(){
        {
            // 关闭详情框
            if(document.getElementById("info_close")) {
                document.getElementById("info_close").click();
            }

            var usersDOM = [];
            for (var i = 0; i < this.state.users.length; i++) {
                var sex = "女";
                if(this.state.users[i].sex == 1) {
                    sex = "男";
                }

                var statusStr = "正常";
                var status = this.state.users[i].status;
                if(status == 1) {
                    statusStr = "邮箱手机未验证";
                } else if(status == 2) {
                    statusStr = "邮箱未验证";
                } else if(status == 3) {
                    statusStr = "手机未验证";
                }

                var id = typeof(this.state.users[i].id) == "undefined" ? "" : this.state.users[i].id;
                var source = typeof(this.state.users[i].source) == "undefined" ? "" : this.state.users[i].source;
                var username = typeof(this.state.users[i].username) == "undefined" ? "" : this.state.users[i].username;
                var nickname = typeof(this.state.users[i].nickname) == "undefined" ? "" : this.state.users[i].nickname;
                var mail = typeof(this.state.users[i].mail) == "undefined" ? "" : this.state.users[i].mail;
                var phone = typeof(this.state.users[i].phone) == "undefined" ? "" : this.state.users[i].phone;
                var tel = typeof(this.state.users[i].tel) == "undefined" ? "" : this.state.users[i].tel;
                var qq = typeof(this.state.users[i].qq) == "undefined" ? "" : this.state.users[i].qq;
                var weixin = typeof(this.state.users[i].weixin) == "undefined" ? "" : this.state.users[i].weixin;
                var weibo = typeof(this.state.users[i].weibo) == "undefined" ? "" : this.state.users[i].weibo;
                var name = typeof(this.state.users[i].name) == "undefined" ? "" : this.state.users[i].name;
                var idcard = typeof(this.state.users[i].idcard) == "undefined" ? "" : this.state.users[i].idcard;
                var certification = typeof(this.state.users[i].certification) == "undefined" ? "" : this.state.users[i].certification;
                var certificationFailMsg = typeof(this.state.users[i].certificationFailMsg) == "undefined" ? "" : this.state.users[i].certificationFailMsg;
                var province = typeof(this.state.users[i].province) == "undefined" ? "" : this.state.users[i].province;
                var city = typeof(this.state.users[i].city) == "undefined" ? "" : this.state.users[i].city;
                var area = typeof(this.state.users[i].area) == "undefined" ? "" : this.state.users[i].area;
                var clientId = typeof(this.state.users[i].clientId) == "undefined" ? "" : this.state.users[i].clientId;
                var clientName = typeof(this.state.users[i].clientName) == "undefined" ? "" : this.state.users[i].clientName;

                var certificationStr = "未认证";
                if(certification == 1) {
                    certificationStr = "认证中";
                } else if(certification == 2) {
                    certificationStr = "认证失败";
                } else if(certification == 3) {
                    certificationStr = "认证通过";
                }

                var address = this.state.users[i].address;
                var addresses = [];
                if(typeof(address) != "undefined") {
                    addresses = address.split('_');
                }
                var newAddres = "";
                for(var j=0; j<addresses.length; j++) {
                    if(newAddres != "") {
                        newAddres += " ";
                    }
                    newAddres += addresses[j];
                }

                usersDOM.push(
                                        <tr>
                                            <td>
                                                <a href="javascript:void(0);" className="fa fa-pencil-square-o" data-toggle="modal" data-target="#user_info" title="修改"
                                                onClick={this.handleInfo.bind(this,id,status,source,username,nickname,mail,phone,tel,qq,weixin,weibo,name,idcard,clientId,province,city,area,addresses[addresses.length-1])}></a>&nbsp;
                                                <a href="javascript:void(0);" className="fa fa-id-card" data-toggle="modal" data-target="#user_certification" title="实名认证"
                                                onClick={this.queryCertification.bind(this,id,name,idcard,certificationStr, certificationFailMsg)}></a>&nbsp;
                                                <a href="javascript:void(0);" className="fa fa-times errorBut" title="删除" onClick={this.handleDel.bind(this,id)}></a>
                                            </td>
                                            <td>{id}</td>
                                            <td>{username}</td>
                                            <td>{nickname}</td>
                                            <td>{mail}</td>
                                            <td>{phone}</td>
                                            <td>{tel}</td>
                                            <td>{qq}</td>
                                            <td>{weixin}</td>
                                            <td>{weibo}</td>
                                            <td>{name}</td>
                                            <td>{sex}</td>
                                            <td>{idcard}</td>
                                            <td>{certificationStr}</td>
                                            <td>{source}</td>
                                            <td>{clientName}</td>
                                            <td>{newAddres}</td>
                                            <td>{statusStr}</td>
                                        </tr>
                                      );
            }

            var forntImgHidn = "hidden";
            var forntDomHidn = "hidden";
            var forntDom;
            var backImgHidn = "hidden";
            var backDomHidn = "hidden";
            var backDom;
            if(this.state.idcardImage != "") {
                if(this.state.idcardImage.fornt && this.state.idcardImage.fornt != "") {
                    forntImgHidn = "";
                    forntDomHidn = "hidden";
                    var imgUrl = $setting.serverUrl + this.state.idcardImage.fornt;
                    forntDom = <div className="file-preview">
                                                <img src={imgUrl} className="kv-preview-data file-preview-image width0" />
                                            </div>;
                } else {
                    forntImgHidn = "hidden";
                    forntDomHidn = "";
                }
                if(this.state.idcardImage.back && this.state.idcardImage.back != "") {
                    backImgHidn = "";
                    backDomHidn = "hidden";
                    var imgUrl = $setting.serverUrl + this.state.idcardImage.back;
                    backDom = <div className="file-preview">
                                                <img src={imgUrl} className="kv-preview-data file-preview-image width0" />
                                            </div>;
                } else {
                    backImgHidn = "hidden";
                    backDomHidn = "";
                }

                if(this.state.idcardImage.fornt && this.state.idcardImage.fornt != "" && this.state.idcardImage.back && this.state.idcardImage.back != "") {
                    this.refs.certificationUploadBut.disabled = "disabled";
                    this.refs.certificationUploadBut.setAttribute("data-disabled","true");
                    this.refs.certificationYBut.disabled = "";
                    this.refs.certificationYBut.removeAttribute("data-disabled");
                    this.refs.certificationNBut.disabled = "";
                    this.refs.certificationNBut.removeAttribute("data-disabled");
                } else {
                    this.refs.certificationUploadBut.disabled = "";
                    this.refs.certificationUploadBut.removeAttribute("data-disabled");
                    this.refs.certificationYBut.disabled = "disabled";
                    this.refs.certificationYBut.setAttribute("data-disabled","true");
                    this.refs.certificationNBut.disabled = "disabled";
                    this.refs.certificationNBut.setAttribute("data-disabled","true");
                }
            }
        }

        return (
            <div className="content table-responsive">
                <div className="search_content">
                    <input type="text" placeholder="用户名" ref="schUsername" />
                    <input type="text" placeholder="昵称" ref="schNickname" />
                    <input type="text" placeholder="邮箱" ref="schMail" />
                    <input type="text" placeholder="手机号" ref="schPhone" />
                    <input type="text" placeholder="电话" ref="schTel" />
                    <input type="text" placeholder="QQ" ref="schQQ" />
                    <input type="text" placeholder="微信" ref="schWeixin" />
                    <input type="text" placeholder="微博" ref="schWeibo" />
                    <input type="text" placeholder="姓名" ref="schName" />
                    <input type="text" placeholder="身份证" ref="schIdcard" />
                    <input type="text" placeholder="实名认证" ref="schIdcard" />
                    <select ref="schSex"><option value ="">性别</option><option value ="1">男</option><option value ="0">女</option></select>
                    <select ref="schStatus"><option value ="">状态</option><option value ="0">正常</option><option value ="1">手机邮箱未验证</option><option value ="2">邮箱未验证</option><option value ="3">手机未验证</option></select>
                    <input type="text" placeholder="来源" ref="schSource" />
                    <CommonClientApp ref="schClientApp"/>
                    <CommonAddressApp ref="schAddressApp"/>
                    <button className="button button-primary button-circle button-small" onClick={this.handleQuery.bind(this,1)}><i className="fa fa-search"></i></button>
                </div>
                <table className="table table-bordered table-hover">
                    <thead>
                        <tr>
                            <th className="width70"><a href="javascript:void(0);" className="fa fa-plus" data-toggle="modal" data-target="#user_info" onClick={this.handleInfo} title="添加"></a></th>
                            <th>ID</th>
                            <th>用户名</th>
                            <th>昵称</th>
                            <th>邮箱</th>
                            <th>手机号</th>
                            <th>电话</th>
                            <th>QQ</th>
                            <th>微信</th>
                            <th>微博</th>
                            <th>姓名</th>
                            <th>性别</th>
                            <th>身份证</th>
                            <th>实名认证</th>
                            <th>来源</th>
                            <th>所属应用</th>
                            <th>地址</th>
                            <th>状态</th>
                        </tr>
                    </thead>
                    <tbody>
                        {usersDOM}
                    </tbody>
                </table>

                <CommonPageApp total={this.state.total} pageSize={this.state.pageSize} pageNum={this.state.pageNum} handleQuery={this.handleQuery} />

                <div className="modal fade" id="user_info" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
                    <div className="modal-dialog">
                        <div className="modal-content">
                            <div className="modal-header">
                                <button type="button" className="close" id="info_close" data-dismiss="modal" aria-hidden="true">&times;</button>
                            </div>
                            <div className="modal-body search_content add_content show_body">
                                <form className="form-horizontal">
                                    <div className="form-group">
                                        <label class="col-sm-2 control-label">用户名</label>
                                        <div className="col-sm-9">
                                          <input type="text" placeholder="1-32个字符" ref="username" data-errMsgId="usernameError" data-max="32" data-maxText="1-32个字符"/>
                                           <div id="usernameError" className="errorMsg"></div>
                                        </div>
                                    </div>
                                    <div className="form-group" id="pwdContainer" ref="pwdContainer">
                                        <label class="col-sm-2 control-label"><span className="errorMsg">*&nbsp;</span>密码</label>
                                        <div className="col-sm-9">
                                          <input type="text" placeholder="6-16个字符" ref="pwd" data-errMsgId="pwdError" data-min="6" data-max="16" data-maxText="6-16个字符"/>
                                           <div id="pwdError" className="errorMsg"></div>
                                        </div>
                                    </div>
                                    <div className="form-group">
                                        <label class="col-sm-2 control-label">昵称</label>
                                        <div className="col-sm-9">
                                          <input type="text" placeholder="1-8个汉字或字母" ref="nickname" data-errMsgId="nicknameError" data-max="8" data-maxText="1-32个字符"/>
                                           <div id="nicknameError" className="errorMsg"></div>
                                        </div>
                                    </div>
                                    <div className="form-group">
                                        <label class="col-sm-2 control-label"><span className="errorMsg">*&nbsp;</span>邮箱</label>
                                        <div className="col-sm-9">
                                          <input type="text" placeholder="1-32个字符" ref="mail" data-errMsgId="mailError"  data-mail="true" data-empty="true" data-emptyText="不能为空" data-max="32" data-maxText="1-32个字符"/>
                                           <div id="mailError" className="errorMsg"></div>
                                        </div>
                                    </div>
                                    <div className="form-group">
                                        <label class="col-sm-2 control-label">手机号</label>
                                        <div className="col-sm-9">
                                          <input type="text" placeholder="11位手机号码" ref="phone" data-errMsgId="phoneError"  data-phone="true" data-max="11" data-maxText="11位手机号码"/>
                                           <div id="phoneError" className="errorMsg"></div>
                                        </div>
                                    </div>
                                    <div className="form-group">
                                        <label class="col-sm-2 control-label">电话</label>
                                        <div className="col-sm-9">
                                          <input type="text" placeholder="电话号码" ref="tel" data-errMsgId="telError" data-max="16" data-maxText="16个字符以内"/>
                                           <div id="telError" className="errorMsg"></div>
                                        </div>
                                    </div>
                                    <div className="form-group">
                                        <label class="col-sm-2 control-label">QQ</label>
                                        <div className="col-sm-9">
                                          <input type="text" placeholder="QQ" ref="qq" data-errMsgId="qqError" data-maxText="1-32个字符"/>
                                           <div id="qqError" className="errorMsg"></div>
                                        </div>
                                    </div>
                                    <div className="form-group">
                                        <label class="col-sm-2 control-label">微信</label>
                                        <div className="col-sm-9">
                                          <input type="text" placeholder="微信" ref="weixin" data-errMsgId="weixinError" data-max="32" data-maxText="1-32个字符"/>
                                           <div id="weixinError" className="errorMsg"></div>
                                        </div>
                                    </div>
                                    <div className="form-group">
                                        <label class="col-sm-2 control-label">微博</label>
                                        <div className="col-sm-9">
                                          <input type="text" placeholder="微博" ref="weibo" data-errMsgId="weiboError" data-max="32" data-maxText="1-32个字符"/>
                                           <div id="weiboError" className="errorMsg"></div>
                                        </div>
                                    </div>
                                    <div className="form-group">
                                        <label class="col-sm-2 control-label">姓名</label>
                                        <div className="col-sm-9">
                                          <input type="text" placeholder="1-32个汉字" ref="name" data-errMsgId="nameError" data-max="32" data-maxText="1-32个字符"/>
                                           <div id="nameError" className="errorMsg"></div>
                                        </div>
                                    </div>
                                    <div className="form-group">
                                        <label class="col-sm-2 control-label">性别</label>
                                        <div className="col-sm-9">
                                          <select ref="sex"><option value ="1">男</option><option value ="0">女</option></select>
                                        </div>
                                    </div>
                                    <div className="form-group">
                                        <label class="col-sm-2 control-label">身份证</label>
                                        <div className="col-sm-9">
                                          <input type="text" placeholder="18位身份证号码" ref="idcard" data-errMsgId="idcardError"  data-idcard="true" data-length="18" data-lengthText="18位身份证号码"/>
                                           <div id="idcardError" className="errorMsg"></div>
                                        </div>
                                    </div>
                                    <div className="form-group">
                                        <label class="col-sm-2 control-label">地址</label>
                                        <div className="col-sm-9">
                                           <CommonAddressApp ref="address"/>
                                           <div id="addressError" className="errorMsg"></div>
                                        </div>
                                    </div>
                                    <div className="form-group">
                                        <label class="col-sm-2 control-label">来源</label>
                                        <div className="col-sm-9">
                                          <input type="text" placeholder="1-255个汉字或字母" ref="source" data-errMsgId="sourceError" data-max="255" data-maxText="1-255个汉字或字母"/>
                                           <div id="sourceError" className="errorMsg"></div>
                                        </div>
                                    </div>
                                    <div className="form-group">
                                        <label class="col-sm-2 control-label"><span className="errorMsg">*&nbsp;</span>所属应用</label>
                                        <div className="col-sm-9">
                                          <CommonClientApp ref="client"/>
                                           <div id="clientError" className="errorMsg"></div>
                                        </div>
                                    </div>
                                </form>
                            </div>
                            <div className="modal-footer">
                                <button type="button" className="button button-primary button-rounded button-small" ref="addBut" onClick={this.handleUpdate}>添加</button>&nbsp;
                                <CommonReSendCodeApp ref="sendMailBut" clsName="button button-primary button-rounded button-small resend_but" userId={id} text="重发验证邮件" />&nbsp;
                                <button type="button" className="button button-primary button-rounded button-small" ref="updateBut" onClick={this.handleUpdate}>修改</button>&nbsp;
                            </div>
                        </div>
                    </div>
                </div>

                <div className="modal fade" id="user_certification" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
                    <div className="modal-dialog">
                        <div className="modal-content">
                            <div className="modal-header">
                                <button type="button" className="close" id="certification_close" data-dismiss="modal" aria-hidden="true">&times;</button>
                            </div>
                            <div className="modal-body search_content add_content show_body">
                                <form className="form-horizontal" id="certificationForm">
                                    <div className="form-group">
                                        <label class="col-sm-2 control-label">状态</label>
                                        <div className="col-sm-9">
                                            <label className="text-align_left width200" ref="certificationDiv"></label>
                                        </div>
                                    </div>
                                    <div className="form-group">
                                        <label>姓名</label>
                                        <div className="col-sm-9">
                                          <input type="text" name="name" placeholder="1-32个汉字" ref="certificationName" data-empty="true" data-emptyText="姓名不能为空" data-errMsgId="certificationNameError" data-max="32" data-maxText="1-32个字符"/>
                                           <div id="certificationNameError" className="errorMsg"></div>
                                        </div>
                                    </div>
                                    <div className="form-group">
                                        <label>身份证</label>
                                        <div className="col-sm-9">
                                          <input type="text" name="idcard" placeholder="18位身份证号码" ref="certificationIdcard" data-empty="true" data-emptyText="身份证号码不能为空" data-errMsgId="certificationIdcardError"  data-idcard="true" data-length="18" data-lengthText="18位身份证号码"/>
                                           <div id="certificationIdcardError" className="errorMsg"></div>
                                        </div>
                                    </div>
                                    <div className="form-group">
                                        <label>身份证正面图片</label>
                                        <div className="col-sm-9 padding_top15" hidden={forntImgHidn}>
                                            {forntDom}
                                        </div>
                                        <div className="col-sm-9 padding_top15" hidden={forntDomHidn}>
                                            <input name="forntFile" ref="forntFile" type="file" className="file"
                                                data-errMsgId="certificationForntError" data-empty="true" data-emptyText="请上传身份证正面图片"
                                                data-drop-zone-enabled="false" data-show-caption="false" data-show-remove="false" data-show-upload="false"
                                                data-language="zh" data-min-image-width="15" data-max-image-width="4096" data-max-file-size="4096" data-max-file-count="1" data-auto-replace="true"
                                                data-layout-templates='{"footer":""}'
                                                data-preview-templates='{"image":"<div class=\"krajee-default kv-preview-thumb file-preview-error\" id=\"{previewId}\" data-fileindex=\"{fileindex}\" data-template=\"{template}\"><div class=\"kv-file-content\"><p/><img src=\"{data}\" class=\"kv-preview-data file-preview-image\" title=\"{caption}\" alt=\"{caption}\" {style}></div></div>"}'
                                                data-allowed-file-extensions='["jpg","jpeg","bmp","png"]' />
                                             <div id="certificationForntError" className="errorMsg"></div>
                                        </div>
                                    </div>
                                    <div className="form-group">
                                        <label>身份证反面图片</label>
                                        <div className="col-sm-9 padding_top15" hidden={backImgHidn}>
                                            {backDom}
                                        </div>
                                        <div className="col-sm-9 padding_top15" hidden={backDomHidn}>
                                            <input name="backFile" ref="backFile" type="file" className="file"
                                                data-errMsgId="certificationBackError" data-empty="true" data-emptyText="请上传身份证反面图片"
                                                data-drop-zone-enabled="false" data-show-caption="false" data-show-remove="false" data-show-upload="false"
                                                data-language="zh" data-min-image-width="15" data-max-image-width="4096" data-max-file-size="4096" data-max-file-count="1" data-auto-replace="true"
                                                data-layout-templates='{"footer":""}'
                                                data-preview-templates='{"image":"<div class=\"krajee-default kv-preview-thumb file-preview-error\" id=\"{previewId}\" data-fileindex=\"{fileindex}\" data-template=\"{template}\"><div class=\"kv-file-content\"><p/><img src=\"{data}\" class=\"kv-preview-data file-preview-image\" title=\"{caption}\" alt=\"{caption}\" {style}></div></div>"}'
                                                data-allowed-file-extensions='["jpg","jpeg","bmp","png"]' />
                                             <div id="certificationBackError" className="errorMsg"></div>
                                        </div>
                                    </div>
                                </form>
                            </div>
                            <div className="modal-footer">
                                <button type="button" className="button button-primary button-rounded button-small" ref="certificationUploadBut" onClick={this.certificationUpload}>上传并认证</button>&nbsp;
                                <button type="button" className="button button-primary button-rounded button-small" ref="certificationYBut" onClick={this.certificationConfirm.bind(this, 3)}>通过</button>&nbsp;
                                <button type="button" className="button button-primary button-rounded button-small" ref="certificationNBut" onClick={this.certificationConfirm.bind(this, 2)}>不通过</button>&nbsp;
                            </div>
                        </div>
                    </div>
                </div>

            </div>
        );
    }
}

module.exports = UserApp;