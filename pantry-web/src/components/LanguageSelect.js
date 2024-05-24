import { Navbar } from 'react-bootstrap';
import brFlag from '../assets/images/flags/br.svg'
import ukFlag from '../assets/images/flags/uk.svg'
import { useState, useEffect } from 'react';
import Dropdown from 'react-bootstrap/Dropdown';

export default function LanguageSelect({ language, onChange }) {
    const [languages] = useState([
        { code: "en", flag: (<img src={ukFlag} width="21" alt="" />) },
        { code: "pt", flag: (<img src={brFlag} width="21" alt="" />) }
    ]);

    const [selectedLanguage, setSelectedLanguage] = useState();

    useEffect(() => {
        if (language) {
            const found = languages.find(({ code }) => language === code);
            setSelectedLanguage(found);
        }
    }, []);

    function handleOnSelect(eventKey) {
        const found = languages.find(({ code }) => eventKey === code);
        if (found) {
            setSelectedLanguage(found);
            onChange(found.code)
        }
    }

    return (

        <Dropdown bsPrefix='language-dropdown' onSelect={handleOnSelect}>
            <Dropdown.Toggle >
                {selectedLanguage?.flag}
            </Dropdown.Toggle>
            <Dropdown.Menu>
                {languages.map(({ code, flag }) => (
                    <Dropdown.Item key={code} eventKey={code}>
                        {flag}
                    </Dropdown.Item>
                ))}
            </Dropdown.Menu>
        </Dropdown>
    );

}