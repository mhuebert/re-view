var path = require('path').resolve(__dirname, 'src/js/');

const moduleSettings = {
    loaders: [{
        test: /\.js$/,
        loader: 'babel-loader?presets[]=es2015'
    }]
};

module.exports = [{
    entry: "./src/js/pm.js",
    resolve: {
        modules: ["node_modules"]
    },
    module: moduleSettings,
    output: {
        libraryTarget: "var",
        library: "pm",
        filename: 'pm.pack.js',
        path: path
    }
}, {
    entry: "./src/js/pmMarkdown.js",
    externals: {
        "markdown-it": "markdownit",
        "prosemirror-model": "pm.model"
    },
    module: moduleSettings,
    output: {
        libraryTarget: "var",
        library: "pmMarkdown",
        filename: 'pmMarkdown.pack.js',
        path: path
    }
}];