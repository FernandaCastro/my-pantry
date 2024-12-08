import { useState, useEffect, useRef } from "react";

const useSpeechRecognition = (options = {}) => {
    const { lang = "en-UK", continuous = false, interimResults = false } = options;

    const [transcript, setTranscript] = useState("");
    const [isListening, setIsListening] = useState(false);
    const [captured, setCaptured] = useState(false);
    const [processed, setProcessed] = useState(false);


    const recognitionRef = useRef(null);

    useEffect(() => {

        // Check for SpeechRecognition support
        const SpeechRecognition = window.webkitSpeechRecognition || window.SpeechRecognition;
        if (!SpeechRecognition) {
            console.error("SpeechRecognition API not supported in this browser.");
            return;
        }

        const recognition = new SpeechRecognition();

        recognition.lang = lang;
        recognition.continuous = continuous;
        recognition.interimResults = interimResults;
        recognition.maxAlternatives = 1;

        recognition.onstart = () => setIsListening(true);

        recognition.onend = () => {
            setIsListening(false);
            setProcessed(true);
        }

        recognition.onresult = (event) => {
            const transcriptArray = Array.from(event.results)
                .map((result) => result[0].transcript)
                .join("");
            setTranscript(transcriptArray);
            setCaptured(true);
            console.info("Speech recognition captured: ", transcriptArray);
        };

        recognition.onerror = (event) => {
            setCaptured(false);
            setProcessed(true);
            console.error("Speech recognition error:", event.error);
        };

        recognitionRef.current = recognition;

        return () => {
            recognition.stop();
            recognitionRef.current = null;
        };

    }, [lang, continuous, interimResults]);

    const startListening = () => {
        if (recognitionRef.current) {
            setCaptured(false);
            setProcessed(false);
            recognitionRef.current.stop();
            recognitionRef.current.start();
            setIsListening(true);
            console.info("SpeechRecognition started");
        }
    };

    const stopListening = () => {
        if (recognitionRef.current) {
            recognitionRef.current.stop();
            setIsListening(false);

            console.info("SpeechRecognition stopped");
        }
    };

    const resetTranscript = () => {
        setTranscript("");
    }

    const changeLang = (lang) => {
        recognitionRef.current.lang = lang;
    }

    return {
        transcript,
        isListening,
        captured,
        processed,
        startListening,
        stopListening,
        hasRecognitionSupport: !!recognitionRef.current,
        changeLang,
        resetTranscript
    };
};

export default useSpeechRecognition;
