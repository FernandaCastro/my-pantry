import { useEffect, useState } from 'react';
import JSEncrypt from 'jsencrypt';
import { getPublicKey } from '../services/apis/mypantry/requests/AccountRequests'

const useRSAEncrypt = () => {
    const [publicKey, setPublicKey] = useState('');

    useEffect(() => {
        const fetchPublicKey = async () => {
            try {
                const res = await getPublicKey();
                setPublicKey(res.data);
            } catch (error) {
                console.error('Error fetching public key:', error);
            }
        };

        fetchPublicKey();
    }, []);

    const encrypt = new JSEncrypt();
    encrypt.setPublicKey(publicKey);

    return { encrypt };
};

export default useRSAEncrypt;