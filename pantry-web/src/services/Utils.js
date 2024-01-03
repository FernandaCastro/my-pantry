export function camelCase(str) {
    // Using replace method with regEx
    return str.toLowerCase().replace(/(?:^\w|[A-Z]|\b\w)/g, function (word, index) {
        return index === 0 ? word.toUpperCase() : word.toUpperCase();
    });
}
