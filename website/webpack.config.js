const path = require("path");
const ExtractTextPlugin = require("extract-text-webpack-plugin");

const extractSass = new ExtractTextPlugin({
    filename: "app.css"
});

module.exports = {
    entry: "./src/styles/app.scss",
    output: {
        path: path.join(__dirname, "./public/"),
        filename: "app.css"
    },
    module: {
        rules: [{
            test: /\.scss$/,
            use: extractSass.extract(
                {
                    use: [{
                        loader: "css-loader"
                    },
                        {
                            loader: "postcss-loader"
                        }, {
                            loader: "sass-loader",
                            options: {
                                includePaths: ["node_modules"]
                            }
                        }]
                })
        }]
    },
    plugins: [
        extractSass
    ],
};