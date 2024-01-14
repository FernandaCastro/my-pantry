const getResponseContent = function (response) {
    if (!response) return;
    const contentType = response.headers.get('Content-Type')

    if (contentType) {
        if (contentType.match('image')) {
            return response.blob()
        }

        if (contentType.match('application/json')) {
            return response.json()
        }

        if (contentType.match('application/zip')) {
            return response.blob()
        }
    }
}

export default getResponseContent;