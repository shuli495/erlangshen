import BaseComponents from 'components/common/baseComponents.jsx';
import CommonClientApp from 'components/common/client.jsx';
import CommonMenuApp from 'components/common/menu.jsx';
import MenuStore from 'stores/menu.jsx';

class MenuApp extends BaseComponents {
    constructor(props) {
            super(props, MenuStore);

            this.list = this.list.bind(this);
            this.addBut = this.addBut.bind(this);
            this.addMenu = this.addMenu.bind(this);
            this.changeMenu = this.changeMenu.bind(this);
            this.updateMenu = this.updateMenu.bind(this);
            this.delMenu = this.delMenu.bind(this);
    }

    componentDidMount() {
        this.refs.upMenuBut.setAttribute('disabled', 'disabled');
        this.refs.delMenuBut.setAttribute('disabled', 'disabled');
        this.refs.addFunctionBut.setAttribute('disabled', 'disabled');

        this.refs.schClientApp.selectedFirst()
        this.list();
    }

    // 查询菜单列表
    list() {
        if(this.refs.schClientApp.refs.clientId.value == "") {
            $util_alertSuccess("请选择应用！");
            return;
        }

        MenuStore.list(this.refs.schClientApp.refs.clientId.value);

        this.addBut();
    }

    // 点击左侧 “添加菜单” 按钮
    addBut() {
        if(document.getElementById("menuErrorTmp")) {
            document.getElementById("menuErrorTmp").innerHTML = "";
        }
        this.refs.menuError.innerHTML = "";
        this.refs.functionError.innerHTML = "";

        this.refs.id.value = "";
        this.refs.name.value = "";
        this.refs.url.value = "";
        this.refs.tag.value = "";
        this.refs.parentId.value = "";
        this.refs.functionName.value = "";

        this.refs.addMenuBut.innerHTML = "添加菜单";
        this.refs.addFunctionBut.innerHTML = "添加功能";

        this.refs.upMenuBut.setAttribute('disabled', 'disabled');
        this.refs.delMenuBut.setAttribute('disabled', 'disabled');
        this.refs.addFunctionBut.setAttribute('disabled', 'disabled');

        super.reloading();
    }

    // 新增菜单
    addMenu(type) {
        this.refs.menuError.innerHTML = "";
        this.refs.functionError.innerHTML = "";

        if(!$util_validateValue("menu_info")) {
            return;
        }

        if(type == 0) {
            MenuStore.add(this.refs.schClientApp.refs.clientId.value, this.refs.parentId.value, this.refs.name.value, this.refs.url.value, this.refs.tag.value, type);
        } else {
            MenuStore.add(this.refs.schClientApp.refs.clientId.value, this.refs.parentId.value, this.refs.functionName.value, "", "", type);
        }

        this.addBut();
    }

    // 选择左侧菜单
    changeMenu(id, name, url, tag) {
        this.refs.menuError.innerHTML = "";
        this.refs.functionError.innerHTML = "";

        this.refs.id.value = id;
        this.refs.name.value = name;
        this.refs.url.value = url;
        this.refs.tag.value = tag;
        this.refs.parentId.value = id;

        this.refs.addMenuBut.innerHTML = "添加菜单至 “" + name + "”";
        this.refs.addFunctionBut.innerHTML = "添加功能至 “" + name + "”";

        this.refs.upMenuBut.removeAttribute('disabled');
        this.refs.delMenuBut.removeAttribute('disabled');
        this.refs.addFunctionBut.removeAttribute('disabled');
    }

    // 更新菜单
    updateMenu() {
        var id = this.refs.id.value;
        var name = this.refs.name.value;
        var url = this.refs.url.value;
        var tag = this.refs.tag.value;

        this.refs.menuError.innerHTML = "";
        this.refs.functionError.innerHTML = "";

        if(id == "") {
            this.refs.menuError.innerHTML = "请选择菜单！";
            return;
        }

        MenuStore.update(this.refs.schClientApp.refs.clientId.value, id, name, url ,tag);
    }

    // 删除菜单
    // type 类型 0菜单 1功能
    // id 功能id
    delMenu(type, id) {
        // 菜单id
        if(type == 0) {
            id = this.refs.id.value;
        }

        var name = this.refs.name.value;

        this.refs.menuError.innerHTML = "";
        this.refs.functionError.innerHTML = "";

        if(type == 0 && id == "") {
            this.refs.menuError.innerHTML = "请选择菜单！";
            return;
        }

        var delAlertTxt = "是否删除"+name+"功能？";
        if(type == 0) {
            delAlertTxt = '删除菜单将删除所有子菜单及功能，是否删除“'+name+'”菜单？';
        }

        if(!window.confirm(delAlertTxt)) {
            return;
        }

        MenuStore.del(this.refs.schClientApp.refs.clientId.value, id);

        this.addBut();
    }

    render(){
        {
            var meunDom = <CommonMenuApp ref="menusApp" menus={this.state.menus} showClick="true" changeMenu={this.changeMenu} showDel="true" delMenu={this.delMenu}/>;
        }

        return (
            <div>
                <div className="content">
                    <CommonClientApp ref="schClientApp"/>
                    <button className="button button-primary button-circle button-small" onClick={this.list}><i className="fa fa-search"></i></button>
                </div>

                <div className="cell-content">
                    <div className="modal-content cell2">
                        <div className="modal-header">菜单&nbsp;<a href="javascript:void(0);" className="fa fa-plus" data-toggle="modal" data-target="#key_info" title="添加菜单" onClick={this.addBut}/></div>
                        <div className="modal-body search_content add_content show_body tree">
                            {meunDom}
                        </div>
                    </div>

                    <div className="modal-content cell2">
                        <div className="modal-header">信息</div>
                        <div id="menu_info" className="modal-body search_content add_content show_body">
                            <input type="hidden" ref="parentId" />
                            <p>ID：&nbsp;&nbsp;&nbsp;<input type="text" ref="id" readOnly /></p>
                            <p>名称：<input type="text" ref="name" data-errMsgId="menuError" data-empty="true" data-emptyText="名称不能为空" data-max="255" data-maxText="名称1-255个字符"/></p>
                            <p>URL：<input type="text" ref="url" className="url_input" data-errMsgId="menuError" data-max="255" data-maxText="URL 1-255个字符" /></p>
                            <p>标签：<input type="text" ref="tag" className="url_input" data-errMsgId="menuError" data-max="255" data-maxText="标签 1-255个字符" /></p>
                            <p><div id="menuError" ref="menuError" className="errorMsg"></div></p>
                            <div>
                                <button type="button" className="button button-primary button-rounded button-small" ref="addMenuBut" onClick={this.addMenu.bind(this, 0)}>添加菜单</button>&nbsp;
                                <button type="button" className="button button-primary button-rounded button-small" ref="upMenuBut" onClick={this.updateMenu}>修改菜单</button>&nbsp;
                                <button type="button" className="button button-caution button-rounded button-small" ref="delMenuBut" onClick={this.delMenu.bind(this, 0)}>删除菜单</button>&nbsp;
                            </div>
                            <hr/>
                            <p>功能名称：<input type="text" ref="functionName" /></p>
                            <p><div id="functionError" ref="functionError" className="errorMsg"></div></p>
                            <div>
                                <button type="button" className="button button-primary button-rounded button-small" ref="addFunctionBut" onClick={this.addMenu.bind(this, 1)}>添加功能</button>&nbsp;
                            </div>
                        </div>
                    </div>
                </div>

            </div>
        );
    }
}

module.exports = MenuApp;