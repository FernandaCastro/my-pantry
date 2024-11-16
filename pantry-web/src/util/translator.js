export const translator = {
    translate: null,
    push: (text, ...options) => translator.translate(text, ...options),
}