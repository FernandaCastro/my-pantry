const Translator = {
    translate: null,
    push: (text, ...options) => Translator.translate(text, ...options),
}

export default Translator