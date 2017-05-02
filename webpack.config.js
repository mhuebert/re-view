var path = require('path').resolve(__dirname, 'src/js/');

module.exports = [{
    entry: "./src/js/pm.js",
    resolve: {
        modules: ["node_modules"]
    },
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
    output: {
        libraryTarget: "var",
        library: "pmMarkdown",
        filename: 'pmMarkdown.pack.js',
        path: path
    }
}];