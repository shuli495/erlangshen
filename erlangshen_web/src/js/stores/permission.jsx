var EventEmitter = require('events').EventEmitter;
var assign = require('object-assign');

var PermissionStore = assign({}, EventEmitter.prototype, {
  data: {
       roles: []
  },

  // 角色列表查询
  list: function (clientId) {
    var url = $setting.serverUrl + 'permissionRole?clientId=' + clientId;

    $.ajax({
        type : "GET",
        dataType: "json",
        url: url,
        headers : {"Token": $.cookie('token')},
        success: function(result) {
              this.data.roles = result.data;
              this.emit('change');
        }.bind(this),
        error: function(xhr, type, exception) {
                $util_alertMsg(xhr);
        }.bind(this)
    });
  },

  // 添加角色
  add: function (clientId, role) {
    var params = {"clientId":clientId, "role":role};

    $.ajax({
        type : "POST",
        dataType: "json",
        contentType: "application/json; charset=utf-8",
        url: $setting.serverUrl + "permissionRole",
        headers : {"Token": $.cookie('token')},
        data: JSON.stringify(params),
        success: function(result) {
              $util_alertMsg('', '添加成功！');

             this.list(clientId);
        }.bind(this),
        error: function(xhr, type, exception) {
              $util_alertMsg(xhr);
        }.bind(this)
    });
  },

  // 删除角色
  del: function (clientId, id) {
    $.ajax({
        type : "DELETE",
        contentType: "application/json; charset=utf-8",
        url: $setting.serverUrl + "permissionRole/" + id,
        headers : {"Token": $.cookie('token')},
        success: function(result) {
              $util_alertMsg('', '删除成功！');

             this.list(clientId);
        }.bind(this),
        error: function(xhr, type, exception) {
              $util_alertMsg(xhr);
        }.bind(this)
    });
  },

  // 关联用户
  addUser: function (id, users) {
    var params = {"users":users};

    $.ajax({
        type : "POST",
        contentType: "application/json; charset=utf-8",
        url: $setting.serverUrl + "permissionRole/" + id + "/user",
        headers : {"Token": $.cookie('token')},
        data: JSON.stringify(params),
        success: function(result) {
        }.bind(this),
        error: function(xhr, type, exception) {
              $util_alertMsg(xhr);
        }.bind(this)
    });
  },

  // 取消用户关联
  delUser: function (id, users) {
    var params = {"users":users};

    $.ajax({
        type : "DELETE",
        contentType: "application/json; charset=utf-8",
        url: $setting.serverUrl + "permissionRole/" + id + "/user",
        headers : {"Token": $.cookie('token')},
        data: JSON.stringify(params),
        success: function(result) {
        }.bind(this),
        error: function(xhr, type, exception) {
              $util_alertMsg(xhr);
        }.bind(this)
    });
  },

  // 关联功能
  addMenu: function (id, menus) {
    var params = {"menus":menus};

    $.ajax({
        type : "POST",
        contentType: "application/json; charset=utf-8",
        url: $setting.serverUrl + "permissionRole/menu/" + id,
        headers : {"Token": $.cookie('token')},
        data: JSON.stringify(params),
        success: function(result) {
        }.bind(this),
        error: function(xhr, type, exception) {
              $util_alertMsg(xhr);
        }.bind(this)
    });
  },

  // 取消功能关联
  delMenu: function (id, menus) {
    var params = {"menus":menus};

    $.ajax({
        type : "DELETE",
        contentType: "application/json; charset=utf-8",
        url: $setting.serverUrl + "permissionRole/menu/" + id,
        headers : {"Token": $.cookie('token')},
        data: JSON.stringify(params),
        success: function(result) {
        }.bind(this),
        error: function(xhr, type, exception) {
              $util_alertMsg(xhr);
        }.bind(this)
    });
  },

});

module.exports = PermissionStore;
