var path = require('path');
var glob = require('glob');
var webpack = require('webpack');

module.exports = {
  entry: getEntries('./src/js/*.js'),
  output: {
    path: path.resolve(__dirname, './build'),
    publicPath: "/build/",
    filename: '[name].js'
  },
  module: {
    loaders: [
    {
      test: /\.css$/,
      loader: "style-loader!css-loader"
    },
    {
      test: /\.jsx?$/,
      loader: "babel-loader"
    }
    ]
  },
  resolve: {
    alias: {
      js: path.join(__dirname, "./src/js"),
      commonJs: path.join(__dirname, "./src/js/common"),
      components: path.join(__dirname, "./src/js/components"),
      stores: path.join(__dirname, "./src/js/stores")
    }
  },
  plugins: [
    new webpack.optimize.CommonsChunkPlugin('common.js')
  ]
};

function getEntries(globPath) {
  var files = glob.sync(globPath);
  var entries = {}, entry, basename;

  for (var i = 0; i < files.length; i++) {
    entry = files[i];

    basename = path.basename(entry, '.js');
    entries[basename] = './' + entry;
  }

  return entries;
}