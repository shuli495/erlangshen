class CommonPageApp extends React.Component {
    constructor(props) {
        super(props);

        this.handleQuery = this.handleQuery.bind(this)
        this.handlePrevious = this.handlePrevious.bind(this)
        this.handleNext = this.handleNext.bind(this)
    }

    handleQuery(event) {
        this.props.handleQuery(event.target.innerHTML);
    }

    handlePrevious(event) {
        var previous = this.props.pageNum;
        if(previous > 1) {
            previous = previous - 1;
        }
        this.props.handleQuery(previous);
    }

    handleNext(event) {
        var next = this.props.pageNum;
        if(next < Math.ceil(this.props.total/this.props.pageSize)) {
            next = next +1;
        }
        this.props.handleQuery(next);
    }

    render() {
        {
            var max = 10;
            var lis = [<li><a href="javascript:void(0);" onClick={this.handlePrevious}>&laquo;</a></li>];

            var maxPate = Math.ceil(this.props.total/this.props.pageSize);

            if(maxPate <= max) {
                for (var i = 1; i <= maxPate; i++) {
                    if (i != this.props.pageNum) {
                        lis.push(<li><a href="javascript:void(0);" onClick={this.handleQuery}>{i}</a></li>);
                    } else {
                        lis.push(<li className="active"><a href="javascript:void(0);" onClick={this.handleQuery}>{i}</a></li>);
                    }
                }
            } else {
                var begNum = 1;
                var endNum = max-1;
                if(this.props.pageNum > max-1) {
                    begNum = Math.floor(this.props.pageNum/(max-1))*9+1;
                    endNum = Math.floor(this.props.pageNum/(max-1))*9+9;
                    endNum = endNum>maxPate?maxPate:endNum;
                }

                for (var i = begNum; i <= endNum; i++) {
                    if (i != this.props.pageNum) {
                        lis.push(<li><a href="javascript:void(0);" onClick={this.handleQuery}>{i}</a></li>);
                    } else {
                        lis.push(<li className="active"><a href="javascript:void(0);" onClick={this.handleQuery}>{i}</a></li>);
                    }
                }

                if(begNum+max-1<maxPate) {
                    lis.push(<li className="disabled"><a href="javascript:void(0);">...</a></li>);
                    lis.push(<li><a href="javascript:void(0);" onClick={this.handleQuery}>{maxPate}</a></li>);
                }
            }

            lis.push(<li><a href="javascript:void(0);" onClick={this.handleNext}>&raquo;</a></li>);
        }

        return (
            <div className="page_content">
                <div className="page">
                    <ul className="pagination">
                        {lis}
                    </ul>
                </div>
                <div className="total">共 {this.props.total} 条</div>
            </div>
        );
    }
}

module.exports = CommonPageApp;