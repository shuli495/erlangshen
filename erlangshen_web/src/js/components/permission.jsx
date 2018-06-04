import BaseComponents from 'components/common/baseComponents.jsx';
import CommonClientApp from 'components/common/client.jsx';
import CommonMenuApp from 'components/common/menu.jsx';
import PermissionStore from 'stores/permission.jsx';
import UserStore from 'stores/user.jsx';
import MenuStore from 'stores/menu.jsx';

class KeyApp extends BaseComponents {
    constructor(props) {
            super(props, PermissionStore);

            this.changedRoleId = "";
            this.users = {};
            this.menus = {};

            this.list = this.list.bind(this);
            this.clickRole = this.clickRole.bind(this);
            this.checkAllUser = this.checkAllUser.bind(this);
            this.checkUser = this.checkUser.bind(this);
            this.addRoleBut = this.addRoleBut.bind(this);
            this.removeAddContent = this.removeAddContent.bind(this);
            this.addRole = this.addRole.bind(this);
            this.delRole = this.delRole.bind(this);
            this.schUser = this.schUser.bind(this);
            this.checkAllMenu = this.checkAllMenu.bind(this);
            this.checkMenu = this.checkMenu.bind(this);
    }

    componentDidMount() {
        this.refs.schClientApp.selectedFirst()
        this.list();
        super.reloading();
    }

    // 查询菜单列表
    list() {
        if(this.refs.schClientApp.refs.clientId.value == "") {
            $util_alertSuccess("请选择应用！");
            return;
        }

        var roleButs = this.refs.roleContent.children;
        for(var i=0; i<roleButs.length; i++) {
            roleButs[i].className = "";
        }
        this.refs.userContent.innerHTML = "";
        this.refs.userCheckAllBut.checked = "";
        this.refs.schUser.value = "";
        this.refs.menuCheckAllBut.checked = "";
        this.refs.menusApp.setData([]);

        PermissionStore.list(this.refs.schClientApp.refs.clientId.value);
        UserStore.list("","","","","","","","","","","","","","","","","",this.refs.schClientApp.refs.clientId.value);
        MenuStore.list(this.refs.schClientApp.refs.clientId.value);
    }

    // 添加角色按钮
    addRoleBut() {
        if(document.getElementById("addRoleContent")) {
            return;
        }

        // 角色输入框
        var inputDom = document.createElement("input");
        inputDom.type = "text";
        inputDom.id = "addRoleInput";

        // 确认按钮
        var addButDom = document.createElement("button");
        addButDom.type = "button";
        addButDom.className = "button button-primary button-rounded button-small";
        addButDom.innerHTML = "添加";
        addButDom.onclick = this.addRole.bind(this);

        // 取消按钮
        var cancleButDom = document.createElement("button");
        cancleButDom.type = "button";
        cancleButDom.className = "button button-primary button-rounded button-small disabled";
        cancleButDom.innerHTML = "取消";
        cancleButDom.onclick = this.removeAddContent.bind(this);

        var spanDom = document.createElement("span");
        spanDom.id = "addRoleContent";
        spanDom.appendChild(inputDom);
        spanDom.appendChild(addButDom);
        spanDom.appendChild(document.createTextNode("  "));
        spanDom.appendChild(cancleButDom);

        this.refs.roleContent.appendChild(spanDom);
    }

    // 删除添加角色dom
    removeAddContent() {
            var removeDom = document.getElementById("addRoleContent");
            removeDom.parentNode.removeChild(removeDom);
    }

    // 添加角色
    addRole() {
        var role = document.getElementById('addRoleInput').value;
        if(role == "") {
            alert('请填写角色！');
            return;
        }

        PermissionStore.add(this.refs.schClientApp.refs.clientId.value, role);

        this.removeAddContent();
    }

    // 删除角色
    delRole(id, name) {
        if(!window.confirm('是否删除“'+name+'”角色？')) {
            return;
        }

        PermissionStore.del(this.refs.schClientApp.refs.clientId.value, id);
    }

    // 选中角色
    clickRole(id) {
        this.refs.menusApp.setData([]);
        this.changedRoleId = id;
        this.refs.userContent.innerHTML = "";

        // 设置选中角色的按钮样式
        var domId = "roleBut"+id;
        var thisDom = document.getElementById(domId);

        var doms = thisDom.parentNode.children;
        for(var i=0; i<doms.length; i++) {
            doms[i].className="";
        }

        thisDom.className = "line_but_visited";


        // 显示关联的用户
        var checkedUserNum = 0;
        for(var i=0; i<UserStore.data.users.length; i++) {
            var userId = UserStore.data.users[i].id;
            var nickname = UserStore.data.users[i].nickname?UserStore.data.users[i].nickname:"";
            var username = UserStore.data.users[i].username?UserStore.data.users[i].username:"";
            var mail = UserStore.data.users[i].mail?UserStore.data.users[i].mail:"";
            var phone = UserStore.data.users[i].phone?UserStore.data.users[i].phone:"";

            // 复选按钮
            var divDom1 = document.createElement("div");
            divDom1.className = "cell_icon";
            var inputDom = document.createElement("input");
            inputDom.type = "checkbox";
            inputDom.value = userId;
            inputDom.id = "user" + userId;
            inputDom.name = "userCheck";
            inputDom.onclick = this.checkUser.bind(this, userId);

            if(this.users[id] && this.users[id].indexOf(userId) != -1) {
                checkedUserNum++;
                inputDom.checked = "checked";
            }
            divDom1.appendChild(inputDom);

            // 昵称
            var divDom2 = document.createElement("div");
            divDom2.className = "cell_small";
            divDom2.appendChild(document.createTextNode(nickname));

            // 用户名
            var divDom3 = document.createElement("div");
            divDom3.className = "cell_small";
            divDom3.appendChild(document.createTextNode(username));

            // 邮箱
            var divDom4 = document.createElement("div");
            divDom4.className = "cell_big";
            divDom4.appendChild(document.createTextNode(mail));

            // 手机号码
            var divDom5 = document.createElement("div");
            divDom5.className = "cell_big";
            divDom5.appendChild(document.createTextNode(phone));

            var divDom = document.createElement("div");
            divDom.className = "userInfo";
            divDom.appendChild(divDom1);
            divDom.appendChild(divDom2);
            divDom.appendChild(divDom3);
            divDom.appendChild(divDom4);
            divDom.appendChild(divDom5);

            this.refs.userContent.appendChild(divDom);
        }
        // 设置用户全选按钮状态
        if(UserStore.data.users.length == checkedUserNum) {
            this.refs.userCheckAllBut.checked = "checked";
        } else {
            this.refs.userCheckAllBut.checked = "";
        }


        // 显示关联菜单
        this.refs.menusApp.setData(MenuStore.data.menus);
        // 选中已关联的菜单
        var nowMenus = this.menus[id];
        for(var i=0; i<nowMenus.length; i++) {
            var id = "chkMenu" + nowMenus[i].id;
            this.refs.menusApp.checked(id);
        }
        // 设置菜单全选按钮状态
        if(document.getElementsByName("chkMenu").length == nowMenus.length) {
            this.refs.menuCheckAllBut.checked = "checked";
        } else {
            this.refs.menuCheckAllBut.checked = "";
        }
    }

    // 查找用户
    schUser() {
        var key = this.refs.schUser.value;
        var userInfos = document.getElementsByClassName("userInfo");

        if(key == "") {
            for(var i=0; i<userInfos.length; i++) {
                userInfos[i].removeAttribute('hidden');
            }
        }

        for(var i=0; i<userInfos.length; i++) {
            var isHiden = true;
            for(var j=1; j<userInfos[i].children.length; j++) {
                if(userInfos[i].children[j].innerHTML.indexOf(key) != -1) {
                    isHiden = false;
                    break;
                }
            }

            if(isHiden) {
                userInfos[i].setAttribute("hidden","hidden");
            } else {
                userInfos[i].removeAttribute('hidden');
            }
        }
    }

    // 全选用户
    checkAllUser() {
        if(this.changedRoleId == "") {
            alert("请选择角色！");
            return;
        }

        var userIds = [];
        var users = document.getElementsByName("userCheck");

        if(this.refs.userCheckAllBut.checked) {
            // 关联用户
            for(var i=0; i<users.length; i++) {
                if(!users[i].checked && !users[i].parentNode.parentNode.hidden) {
                    userIds.push(users[i].value);
                    users[i].checked = "checked";
                }
            }

            PermissionStore.addUser(this.changedRoleId, userIds);
        } else {
            // 取消关联
            for(var i=0; i<users.length; i++) {
                if(users[i].checked && !users[i].parentNode.parentNode.hidden) {
                    userIds.push(users[i].value);
                    users[i].checked = "";
                }
            }

            PermissionStore.delUser(this.changedRoleId, userIds);
        }
    }

    // 选择用户复选框，选中添加，反选删除
    checkUser(userId) {
        if(this.changedRoleId == "") {
            alert("请选择角色！");
            return;
        }

        if(document.getElementById("user"+userId).checked) {
            // 关联用户
            PermissionStore.addUser(this.changedRoleId, [userId]);
        } else {
            // 取消关联
            PermissionStore.delUser(this.changedRoleId, [userId]);
        }
    }

    // 全选功能
    checkAllMenu() {
        var menus = document.getElementsByName("chkMenu");

        var menuIds = [];
        for(var i=0; i<menus.length; i++) {
            if(this.refs.menuCheckAllBut.checked && !menus[i].checked) {
                // 选中
                menus[i].checked = "checked";
                menuIds.push(menus[i].value);
            } else if(!this.refs.menuCheckAllBut.checked && menus[i].checked) {
                // 反选
                menus[i].checked = "";
                menuIds.push(menus[i].value);
            }
        }

        if(menuIds.length == 0) {
            return;
        }

        if(this.refs.menuCheckAllBut.checked) {
            PermissionStore.addMenu(this.changedRoleId, menuIds);
        } else {
            PermissionStore.delMenu(this.changedRoleId, menuIds);
        }
    }

    // 选择功能
    checkMenu(menuId, event) {
        if(this.changedRoleId == "") {
            alert("请选择角色！");
            return;
        }

        if(document.getElementById("chkMenu"+menuId).checked) {
            PermissionStore.addMenu(this.changedRoleId, [menuId]);
        } else {
            PermissionStore.delMenu(this.changedRoleId, [menuId]);
        }
    }

    render(){
        {
            var roleDoms = [];
            var roles = this.state.roles;

            for(var i=0; i<roles.length; i++) {
                var id = roles[i].id;
                var domId = "roleBut"+roles[i].id;

                this.users[id] = roles[i].users;
                this.menus[id] = roles[i].menus;

                roleDoms.push(
                    <div id={domId} onClick={this.clickRole.bind(this, roles[i].id)}>{roles[i].role}
                        &nbsp;
                        <a href="javascript:void(0);" className="fa fa-times errorBut" title="删除" onClick={this.delRole.bind(this,roles[i].id,roles[i].role)} />
                    </div>
                    );
            }
        }

        return (
            <div >
                <div className="content">
                    <CommonClientApp ref="schClientApp"/>
                    <button className="button button-primary button-circle button-small" onClick={this.list}><i className="fa fa-search"></i></button>
                </div>

                <div className="cell-content">
                    <div className="modal-content cell3">
                        <div className="modal-header">角色&nbsp;<a href="javascript:void(0);" className="fa fa-plus" title="添加角色" onClick={this.addRoleBut}/></div>
                        <div className="modal-body search_content add_content show_body line_but_content" ref="roleContent">
                            {roleDoms}
                        </div>
                    </div>

                    <div className="modal-content cell3">
                        <div className="modal-header search_content search2">
                            <input type="text" ref="schUser" onKeyUp={this.schUser}/>
                        </div>
                        <div className="modal-header">
                            <div className="cell_icon"><input type="checkbox" title="全/反选" ref="userCheckAllBut" onClick={this.checkAllUser} /></div>
                            <div className="cell_small">昵称</div>
                            <div className="cell_small">用户名</div>
                            <div className="cell_big">邮箱</div>
                            <div className="cell_big">手机号码</div>
                        </div>
                        <div className="modal-body show_body" ref="userContent">
                        </div>
                    </div>

                    <div className="modal-content cell3">
                        <div className="modal-header"><input type="checkbox" title="全/反选" ref="menuCheckAllBut" onClick={this.checkAllMenu} />&nbsp;菜单</div>
                        <div className="modal-body show_body tree">
                            <CommonMenuApp ref="menusApp" showCheck="true" checkMenu={this.checkMenu} />
                        </div>
                    </div>
                </div>

            </div>
        );
    }
}

module.exports = KeyApp;