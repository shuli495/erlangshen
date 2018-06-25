class CommonMenuApp extends React.Component {
    constructor(props) {
            super(props);

            this.setData = this.setData.bind(this);
            this.packageMenuDom = this.packageMenuDom.bind(this);
            this.checked = this.checked.bind(this);
    }

    // 设置数据并显示
    setData(menus) {
        if(menus && menus.length > 0) {
            var menuDom = this.packageMenuDom(menus);

            ReactDOM.render(
                    menuDom,
                    this.refs.menuContent
            );
        } else {
            ReactDOM.render(
                    <div/>,
                    this.refs.menuContent
            );
        }
    }

    // 设置菜单格式
    packageMenuDom(data) {
        if(!data) {
            return;
        }

        var showCheck = this.props.showCheck;   // 是否展现复选框
        var showClick = this.props.showClick;       // 是否可以点击
        var showDel = this.props.showDel;           // 是否展现删除按钮

        // 菜单图标
        var menuIcon = React.createElement("ol", {href:"javascript:void(0);", className:"fa fa-bars", title:"菜单"}, null);
        // 功能图标
        var functionIcon = React.createElement("ol", {href:"javascript:void(0);", className:"fa fa-bookmark", title:"功能"}, null);

        var menuLis = [];
        for (var i = 0; i < data.length; i++) {
            var menuId = data[i].id;
            var menuType = data[i].type;    //0菜单 1功能
            var menuName = data[i].name;
            var menuUrl = data[i].url;
            var menuTag = data[i].tag?data[i].tag:"";

            var menuChild = [];

            // 图标
            if(menuType == 0) {
                menuChild.push(menuIcon);
            } else {
                menuChild.push(functionIcon);
            }

            // 菜单复选框
            if(showCheck) {
                menuChild.push(
                    React.createElement("input",
                        {type: "checkbox",name: "chkMenu", id: "chkMenu"+menuId, value: menuId, onClick:this.props.checkMenu.bind(this,menuId)},
                        null)
                    );
            }

            // 当前菜单按钮
            if(showClick && menuType == 0) {
                var menuBut = React.createElement("a",
                    {href:"javascript:void(0);", title:data[i].name, onClick:this.props.changeMenu.bind(this, menuId, menuName, menuUrl, menuTag)},
                    menuName);
                menuChild.push(menuBut);
            } else {
                menuChild.push(data[i].name);
            }

            // 是否显示删除 按钮
            if(showDel && menuType == 1) {
                var delIcon = React.createElement("i", {className:"fa fa-times"}, null);
                menuChild.push(
                    React.createElement("a",
                        {href:"javascript:void(0);", className:"errorBut", title:"删除"+menuName, onClick:this.props.delMenu.bind(this, 1, menuId)},
                        delIcon)
                    );
            }

            // 当前菜单的下级菜单
            if(data[i].menus && data[i].menus.length > 0) {
                menuChild.push(this.packageMenuDom(data[i].menus));
            }

            menuLis.push(
                React.createElement("li",
                    {id:"menu_" + menuId, value:menuUrl},
                     menuChild));
         }

         return React.createElement("ul", null, menuLis);
    }

    checked(id) {
        var dom = document.getElementById(id);
        if(dom) {
            dom.checked = "checked";
        }
    }

    render() {
    {
            var menusDOM = this.packageMenuDom(this.props.menus);
    }
    return (
        <div ref="menuContent">
            {menusDOM}
        </div>
    );
    }
}

module.exports = CommonMenuApp;