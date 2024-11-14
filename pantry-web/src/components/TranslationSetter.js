import { useTranslation } from 'react-i18next';
import { translator } from '../util/translator'

const TranslationSetter = () => {
    const { t } = useTranslation('common');
    translator.translate = t;

    return null
};

export default TranslationSetter;