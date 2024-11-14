import { useEffect, useState } from 'react';
import JSEncrypt from 'jsencrypt';
import { fetchPublicKey } from '../api/mypantry/account/accountService'

const useRSAEncrypt = () => {
    const [publicKey, setPublicKey] = useState('');

    useEffect(() => {
        const loadPublicKey = async () => {
            try {
                const res = await fetchPublicKey();
                setPublicKey(res.data);
            } catch (error) {
                console.error('Error fetching public key:', error);
            }
        };

        loadPublicKey();
    }, []);

    const encrypt = new JSEncrypt();
    encrypt.setPublicKey(publicKey);

    return { encrypt };
};

export default useRSAEncrypt;