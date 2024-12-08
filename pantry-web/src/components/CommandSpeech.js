import i18next from "i18next";
import { useEffect, useState } from "react";
import { Button, Image } from "react-bootstrap";
import { BiMicrophone, BiMicrophoneOff } from "react-icons/bi";
import useSpeechSynthesis from "../hooks/useSpeechSynthesis";
import useSpeechRecognition from "../hooks/useSpeechRecognition";
import { useTranslation } from "react-i18next";
import iconMicrophone from '../assets/images/microphone.png';
import useAlert from "../state/useAlert";
import VariantType from '../components/VariantType.js';
import { postCommand } from "../api/mypantry/account/accountService";

export default function CommandSpeech() {

    const { t } = useTranslation(['command']);
    const { showAlert } = useAlert();
    const [isLoading, setIsLoading] = useState(false);

    const { transcript, isListening, captured, processed, startListening, stopListening } = useSpeechRecognition({
        lang: i18next.language,
        continuous: false,
        interimResults: false,
    });

    const { voices, changeVoice, speak } = useSpeechSynthesis(i18next.language);

    const [commandTranscript, setCommandTranscript] = useState(null);
    const [confirmationTranscript, setConfirmationTranscript] = useState();
    const [isListenToCommand, setIsListenToCommand] = useState(true);

    useEffect(() => {

        if (processed && captured && transcript && transcript.length > 0) {

            if (isListenToCommand) {
                setCommandTranscript(transcript);
                confirmCommand(transcript);

            } else {
                setConfirmationTranscript(transcript);
                confirmation(transcript);

            }
        } else {
            if (processed) {
                nothingListened();
            }
        }

    }, [transcript, processed]);

    const processCommand = async () => {
        console.log("Send commandTranscript to backend... ", commandTranscript);

        if (isLoading) {
            console.error("Still processing previous command.");
            return;
        }

        setIsLoading(true);

        try {
            const commandSpeechDto = { transcript: commandTranscript }
            await postCommand(commandSpeechDto);
            showAlert(VariantType.SUCCESS, t("command-success"));
            await speak(t('command-success'), i18next.language)
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
            await speak(t('command-fail'), i18next.language)
        } finally {
            clear();
            setIsLoading(false);
        }
    };

    const confirmCommand = async (newTranscript) => {

        if (!newTranscript) return;

        console.log("commandTranscript: ", newTranscript);

        await speak(t("you-said", { transcript: newTranscript }), i18next.language)

        setIsListenToCommand(false);

        startListening();
    }

    const confirmation = async (newTranscript) => {

        const yes = t('yes');
        const no = t('no');
        const stop = t('stop');

        if (newTranscript.includes(yes)) {
            console.log("'Yes' listened!");

            processCommand();


        } else if (newTranscript.includes(no)) {
            console.log("'No' listened!");

            clear();

            await speak(t('repeat-please'), i18next.language)

            startListening();

        } else if (newTranscript.includes(stop)) {
            console.log("'Stop' listened!");
            clear();
            stopListening();
            
        } else {
            nothingListened();
        }

        setIsListenToCommand(!isListenToCommand);
    }

    const nothingListened = async () => {

        console.error("Nothing listened!");

        await speak(t('cannot-understand'), i18next.language)

        clear();
    }

    const clear = () => {
        setCommandTranscript("");
        setConfirmationTranscript("");
        setIsListenToCommand(true);
    }

    function handleStartStopListening() {
        !isListening ? startListening() : stopListening();
        console.log(!isListening ? "Start Listening" : "Stop Listening");
    }

    return (
        <div>
            <Button variant="link" onClick={handleStartStopListening} tooltip="teste" style={{ padding: 0, margin: 0, alignSelf: "end" }}>
                <Image src={iconMicrophone} className={isListening ? "microphone-icon active" : "microphone-icon"} />
            </Button>
        </div>
    )
}