import 'dotenv/config';
const dotEnv = require('dotenv').config();

const path = require('path');
// const webpack = require('webpack');

// const NodePolyfillPlugin = require('node-polyfill-webpack-plugin')

module.exports = {
    mode: 'development',
    entry: './src/index.ts',
    output: {
        filename: 'bundle.js',
        path: path.resolve(__dirname, 'dist'),
    },
    plugins: [
        // new NodePolyfillPlugin(),
        // new webpack.DefinePlugin({
        //     'process.env': {
        //         REACT_APP_API_URL_PANTRY: JSON.stringify(process.env.REACT_APP_API_URL_PANTRY)
        //     }
        // }),
    ],
    output: {
        hashFunction: require('xxhash64').hashFunction,
    },
    experiments: {
        futureDefaults: true,
    },
};