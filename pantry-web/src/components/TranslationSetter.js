import { useTranslation } from 'react-i18next';
import { translator } from '../state/translator';

const TranslationSetter = () => {
    const { t } = useTranslation('common');
    translator.translate = t;

    return null
};

export default TranslationSetter;