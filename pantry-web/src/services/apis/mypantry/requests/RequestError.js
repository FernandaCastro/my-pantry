export default class RequestError extends Error {
    status;
    body;
    constructor(message, status, body) {
        super(message)
        this.status = status;
        this.body = body;
    }
}