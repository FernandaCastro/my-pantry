export function camelCase(str) {
    if (!str) return;
    // Using replace method with regEx
    return str.toLowerCase().replace(/(?:^\w|[A-Z]|\b\w)/g, function (word, index) {
        return index === 0 ? word.toUpperCase() : word.toLowerCase();
    });

}

export function fullCamelCase(str) {
    if (!str) return;

    // Using replace method with regEx
    return str.toLowerCase()
        .replace(/(?:^\w|[A-Z]|\b\w)/g, function (word) {
            return word.toUpperCase();
        });

}
