var path = require('path')

module.exports = {
    entry: "./src/js/mdc.js",
    module: {
        rules: [
            { test: /\.js$/, loader: "babel-loader" }
        ]
    },
    output: {
        libraryTarget: "var",
        library: "mdc",
        filename: 'mdc.pack.js',
        path: path.resolve(__dirname, 'src/js/')
    }
};
