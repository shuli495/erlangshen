class BaseComponents extends React.Component {
    constructor(props,store) {
        super(props);
        this.store = store;
        this.state = this.store.data;
        this.getData = this.getData.bind(this);
        this.setData = this.setData.bind(this);
        this.componentDidMount = this.componentDidMount.bind(this);
        this.componentWillUnmount = this.componentWillUnmount.bind(this);
        this.reloading = this.reloading.bind(this);
        this._onChange = this._onChange.bind(this);
        this.rechange = this.rechange.bind(this);
    }

    getData() {
        return this.store.data;
    }

    // 设置store中的data，kv为键值对
    setData(kv) {
        for(var k in kv) {
            this.store.data[k] = kv[k];
        }
        this.rechange();
    }

    componentDidMount() {
        this.reloading();
    }

    componentWillUnmount() {
        this.store.removeListener('change', this._onChange);
    }

    reloading() {
        this.store.on('change', this._onChange);
    }

    rechange() {
        this.store.emit('change');
    }

    _onChange() {
        this.setState(this.store.data);
    }
}

module.exports = BaseComponents;