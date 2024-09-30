import { useTranslation } from 'react-i18next';
import Translator from './Translator'

const TranslationSetter = () => {
    const { t } = useTranslation('common');
    Translator.translate = t;
    
    return null
};

export default TranslationSetter;