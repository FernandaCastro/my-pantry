import { useEffect, useState } from "react";

export default function useSpeechSynthesis(lang) {

    const synth = window.speechSynthesis;

    const [voices, setVoices] = useState([]);
    const [voice, setVoice] = useState();

    const populateVoices = () => {

        return new Promise((resolve, reject) => {

            let id;
            id = setInterval(() => {

                const vs = synth.getVoices();
                if (synth.getVoices().length !== 0) {
                    let v = vs.sort(function (a, b) {
                        const aname = a.lang.toUpperCase();
                        const bname = b.lang.toUpperCase();

                        if (aname < bname) {
                            return -1;
                        } else if (aname === bname) {
                            return 0;
                        } else {
                            return +1;
                        }
                    })
                    v = v.filter((e) => e.lang === "pt-BR" || e.lang.includes("en"));

                    resolve(v);
                    clearInterval(id);
                }
            }, 10);
        });
    }

    const setVoiceData = (vs) => {
        setVoices(vs);

        if (lang) {
            const defaultVoice = getDefaultVoice(vs, lang);
            setVoice(defaultVoice);
        }
    }

    useEffect(() => {
        populateVoices().then((vs) => setVoiceData(vs));
    }, []);

    const getDefaultVoice = (vs, lang) => {
        let defaultVoice = "Samantha";
        switch (lang) {
            case 'pt-BR':
                defaultVoice = "Luciana";
                break;
            case 'en-US':
                defaultVoice = "Samantha";
                break;
            case 'en-GB':
                defaultVoice = "Arthur";
                break;
        }

        const found = vs.find((v) => v.name === defaultVoice);
        return found;
    }


    const speak = (text, lang) => {

        let defaultVoice = voice;
        if (lang) {
            defaultVoice = getDefaultVoice(voices, lang);
        }

        return new Promise((resolve, reject) => {
            const utterance = new SpeechSynthesisUtterance(text);

            utterance.voice = defaultVoice;
            // utterance.lang = voice.lang;

            utterance.pitch = 0; // voice
            utterance.rate = 1; // speed

            utterance.onend = () => {
                resolve();
            };

            utterance.onerror = (event) => {
                console.error("Speech synthesis error:", event.error);
                reject(event.error);
            };

            synth.speak(utterance); //play
        });
    }

    const changeVoice = (voiceName) => {

        const found = voices.find((v) => v.name === voiceName);
        if (found) setVoice(() => found);
    }

    return { voices, changeVoice, speak }
}