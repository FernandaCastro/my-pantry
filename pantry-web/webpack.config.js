module.exports = {
    //...
    output: {
        hashFunction: require('xxhash64').hashFunction,
    },
    experiments: {
        futureDefaults: true,
    }
};