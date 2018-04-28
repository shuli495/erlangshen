import BaseComponents from 'components/common/baseComponents.jsx';
import ClientStore from 'stores/client.jsx';

class CommonClientApp extends BaseComponents{
    constructor(props) {
        super(props, ClientStore);

        this.selected = this.selected.bind(this);
        this.getFirstValue = this.getFirstValue.bind(this);
        this.selectedFirst = this.selectedFirst.bind(this);
    }

    componentDidMount() {
        ClientStore.list();
        super.reloading();
    }

    // 选择应用
    selected(clientId) {
        this.refs.clientId.value=clientId;
    }

    // 获取第一个应用
    getFirstValue() {
        for(var i=0; i<this.refs.clientId.options.length; i++) {
            if(this.refs.clientId.options[i].value != "") {
                return this.refs.clientId.options[i].value;
            }
        }
        return "";
    }

    // 选择第一个应用
    selectedFirst() {
        this.selected(this.getFirstValue());
    }

    render() {
        {
            var nameDOM = [<option value ="">所属应用</option>];

            for(var i=0; i<this.state.clients.length; i++) {
                    nameDOM.push(<option value={this.state.clients[i].id}>{this.state.clients[i].name}</option>);
            }
        }

        return (
            <span className="client_component">
                    <select ref="clientId">{nameDOM}</select>
            </span>
        );
    }
}

module.exports = CommonClientApp;