import BaseComponents from 'components/common/baseComponents.jsx';
import CommonPageApp from 'components/common/page.jsx';
import CommonClientApp from 'components/common/client.jsx';
import KeyStore from 'stores/key.jsx';

class KeyApp extends BaseComponents {
    constructor(props) {
            super(props, KeyStore);

            this.handleQuery = this.handleQuery.bind(this);
            this.handleAdd = this.handleAdd.bind(this);
            this.handleDel = this.handleDel.bind(this);
            this.handleUpStatus = this.handleUpStatus.bind(this);
    }

    componentDidMount() {
        this.handleQuery(1);
        super.reloading();
    }

    handleQuery(pageNum) {
        KeyStore.page(pageNum, this.refs.schClientApp.refs.clientId.value);
    }

    handleAdd() {
        var clientId = this.refs.client.refs.clientId.value;

        if(!$util_validateValue("key_info")) {
            return;
        }

        if(clientId == "") {
            $("#clientError").html("所属应用必选");
            return;
        } else {
            $("#clientError").html("");
        }

        KeyStore.add(clientId);
    }

    handleDel(access, status) {
        if(!window.confirm('是否确认删除？')) {
            return;
        }

        if(status == 1) {
              $util_alertError('停用后才能删除！');
              return;
        }
        KeyStore.del(access);
    }

    handleUpStatus(access, status) {
        var statusStr = "停用";
        if(status == 0) {
            statusStr = "启用"
        }
        if(!window.confirm('是否'+statusStr+'？')) {
            return;
        }

        if(status == 0) {
            KeyStore.start(access);
        } else {
            KeyStore.stop(access);
        }
    }

    render(){
        {
            var keysDOM = [];
            for (var i = 0; i < this.state.keys.length; i++) {
                var statusStr = "启用";
                var status = this.state.keys[i].status;
                if(status == 0) {
                    statusStr = "停用";
                }

                var access = typeof(this.state.keys[i].access) == "undefined" ? "" : this.state.keys[i].access;
                var secret = typeof(this.state.keys[i].secret) == "undefined" ? "" : this.state.keys[i].secret;
                var clientId = typeof(this.state.keys[i].clientId) == "undefined" ? "" : this.state.keys[i].clientId;
                var clientName = typeof(this.state.keys[i].clientName) == "undefined" ? "" : this.state.keys[i].clientName;

                keysDOM.push(
                                        <tr>
                                            <td>
                                                <a href="javascript:void(0);" className="fa fa-times errorBut" title="删除" onClick={this.handleDel.bind(this, access, status)}/>&nbsp;&nbsp;
                                                <a href="javascript:void(0);" className="fa fa-retweet" title="启用/停用" onClick={this.handleUpStatus.bind(this, access, status)}/>
                                            </td>
                                            <td>{access}</td>
                                            <td>{secret}</td>
                                            <td>{clientName}</td>
                                            <td>{statusStr}</td>
                                        </tr>
                                      );
            }
        }

        return (
            <div className="content table-responsive">
                <div className="search_content">
                    <CommonClientApp ref="schClientApp"/>
                    <button className="button button-primary button-circle button-small" onClick={this.handleQuery.bind(this,1)}><i className="fa fa-search"></i></button>
                </div>
                <table className="table table-bordered table-hover">
                    <thead>
                        <tr>
                            <th className="width55"><a href="javascript:void(0);" className="fa fa-plus" data-toggle="modal" data-target="#key_info" title="添加"></a></th>
                            <th>access key</th>
                            <th>secret key</th>
                            <th>应用</th>
                            <th>状态</th>
                        </tr>
                    </thead>
                    <tbody>
                        {keysDOM}
                    </tbody>
                </table>

                <CommonPageApp total={this.state.total} pageSize={this.state.pageSize} pageNum={this.state.pageNum} handleQuery={this.handleQuery} />

                <div className="modal fade" id="key_info" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
                    <div className="modal-dialog">
                        <div className="modal-content">
                            <div className="modal-header">
                                <button type="button" className="close" id="info_close" data-dismiss="modal" aria-hidden="true">&times;</button>
                            </div>
                            <div className="modal-body search_content add_content show_body">
                                <form className="form-horizontal">
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
                                <button type="button" className="button button-primary button-rounded button-small" ref="addBut" onClick={this.handleAdd}>添加</button>&nbsp;
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        );
    }
}

module.exports = KeyApp;