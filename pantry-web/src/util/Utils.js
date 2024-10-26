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

export function maskEmail(email) {
    if (!email) return;

    const [user, domain] = email.split('@');

    // Keep first and last letter of username
    var maskedUser;
    if (user.length > 4) {
        maskedUser = user.slice(0, 3) + '*'.repeat(user.length - 4) + user.slice(-1);
    } else {
        maskedUser = user[0] + '*'.repeat(user.length - 2) + user.slice(-1);
    }

    // Keep first and last letter of domain
    const domainParts = domain.split('.');
    const maskedDomain = domainParts[0][0] + '*'.repeat(domainParts[0].length - 1) + '.' + domainParts.slice(1).join('.');

    return `${maskedUser}@${maskedDomain}`;
}

export function wait(time) {
    return new Promise(resolve => {
        setTimeout(resolve, time);
    });
}

export function truncate(string, maxSize){
    if (!string || !maxSize || maxSize < 3) return;
    if (string.length > maxSize){
        return string.slice(0, (maxSize - 3)) + "..."
    }
    return string;
}
