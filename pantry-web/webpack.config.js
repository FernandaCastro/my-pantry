import 'dotenv/config';
import Quagga from 'quagga'; // ES6

const dotEnv = require('dotenv').config();
const Quagga = require('quagga').default; // Common JS (important: default)
const path = require('path');

module.exports = {
    mode: 'development',
    entry: './src/index.js',
    output: {
        filename: 'bundle.js',
        path: path.resolve(__dirname, 'dist'),
    },
    plugins: [
    ],
    output: {
        hashFunction: require('xxhash64').hashFunction,
    },
    experiments: {
        futureDefaults: true,
    },
};