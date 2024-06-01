export function camelCase(str) {
    // Using replace method with regEx
    return str.toLowerCase().replace(/(?:^\w|[A-Z]|\b\w)/g, function (word, index) {
        return index === 0 ? word.toUpperCase() : word.toLowerCase();
    });
}

export function fullCamelCase(str) {
    // Using replace method with regEx
    return str.toLowerCase()
        .replace(/(?:^\w|[A-Z]|\b\w)/g, function (word) {
            return  word.toUpperCase();
        });

}
