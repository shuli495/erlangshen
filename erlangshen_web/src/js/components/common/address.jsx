import BaseComponents from 'components/common/baseComponents.jsx';
import CodeStore from 'stores/code.jsx';

class CommonAddressApp extends BaseComponents {
    constructor(props) {
            super(props, CodeStore);

            this.handleSelectProvince = this.handleSelectProvince.bind(this);
            this.handleSelectCity = this.handleSelectCity.bind(this);
    }

    handleSelectProvince(selectedCity, selectedArea) {
        if(this.refs.province.value != "") {
            this.state.selectedCity = selectedCity;
            this.state.selectedArea = selectedArea;

            CodeStore.list(this.refs.province.value,'city');
            CodeStore.list(this.refs.province.value,'area');
            super.reloading();
        }
    }

    handleSelectCity(selectedArea) {
        if(this.refs.city.value != "") {
            this.state.selectedArea = selectedArea;

            CodeStore.list(this.refs.city.value,'area');
            super.reloading();
        }
    }

    componentDidMount() {
            CodeStore.list('','province');
            super.reloading();
    }

    render() {
        {
            var provinceDOM = [<option value="">省</option>];
            if(typeof(this.state.codes.province) != 'undefined') {
                for(var i=0; i<this.state.codes.province.length; i++) {
                    provinceDOM.push(<option value={this.state.codes.province[i].id}>{this.state.codes.province[i].value}</option>)
                }
            }

            var cityDOM = [<option value="">市</option>];
            if(typeof(this.state.codes.city) != 'undefined') {
                for(var i=0; i<this.state.codes.city.length; i++) {
                    if(this.state.selectedCity == this.state.codes.city[i].id) {
                        cityDOM.push(<option selected value={this.state.codes.city[i].id}>{this.state.codes.city[i].value}</option>)
                    } else {
                        cityDOM.push(<option value={this.state.codes.city[i].id}>{this.state.codes.city[i].value}</option>)
                    }
                }
            }

            var areaDOM = [<option value="">区县</option>];
            if(typeof(this.state.codes.area) != 'undefined') {
                for(var i=0; i<this.state.codes.area.length; i++) {
                    if(this.state.selectedArea == this.state.codes.area[i].id) {
                        areaDOM.push(<option selected value={this.state.codes.area[i].id}>{this.state.codes.area[i].value}</option>)
                    } else {
                        areaDOM.push(<option value={this.state.codes.area[i].id}>{this.state.codes.area[i].value}</option>)
                    }
                }
            }
        }

        return (
            <span className="address_component">
                    <select ref="province" onChange={this.handleSelectProvince}>{provinceDOM}</select>
                    <select ref="city" onChange={this.handleSelectCity}>{cityDOM}</select>
                    <select ref="area">{areaDOM}</select>
                    <input type="text" placeholder="详细地址" ref="address" data-errMsgId="addressError" data-max="250" data-maxText="详细地址小于250个字符" />
            </span>
        );
    }
}

module.exports = CommonAddressApp;