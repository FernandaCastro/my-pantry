import PantriesPieChart from './PantryPieCharts.js';
import Login from './Login.js';
import { ProfileContext } from '../context/AppContext.js';
import { getPantryChartData } from '../api/mypantry/pantry/pantryService.js';
import { useContext, useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Button, OverlayTrigger, Stack, Tooltip } from 'react-bootstrap';
import iconMagicWand from '../assets/images/magic-wand.png';
import Image from 'react-bootstrap/Image';
import VariantType from '../components/VariantType.js';
import useAlert from '../hooks/useAlert.js';
import { useLoading } from '../hooks/useLoading.js';

export default function Home() {

    const { t } = useTranslation(['pantry', 'common']);
    const { profileCtx } = useContext(ProfileContext);
    const [chartData, setChartData] = useState([]);
    const { showAlert } = useAlert();
    const { setIsLoading } = useLoading();


    useEffect(() => {
        if (profileCtx && Object.keys(profileCtx).length > 1) {
            fetchPantryChartData();
        }
    }, [profileCtx])

    async function fetchPantryChartData() {
        setIsLoading(true);
        try {
            const res = await getPantryChartData();
            setChartData(res);
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        } finally {
            setIsLoading(false);
        }
    }

    function renderNoPantryYet() {
        return (
            <Stack gap={3}>
                <div className='d-flex justify-content-start mt-4'>
                    <h6 className="title mt-4">{t('welcome-no-pantry')}</h6>
                </div>
                <div>
                    <span>{t('no-pantry-message')}</span>

                    <OverlayTrigger placement="bottom" delay={{ show: 250, hide: 400 }} overlay={<Tooltip className="custom-tooltip">{t("tooltip-pantry-wizard")}</Tooltip>}>
                        <Button variant="link" href={"/pantries/new-wizard"} className="pt-0 pb-0 ms-auto"><div className="bigger-icon gradient-icon-box-body"><Image src={iconMagicWand} className="bigger-icon" /></div></Button>
                    </OverlayTrigger>
                </div>
            </Stack>
        )
    }

    return (
        <>
            {profileCtx && Object.keys(profileCtx).length > 1 ?
                chartData.length === 0 ? renderNoPantryYet() : <PantriesPieChart chartData={chartData} />
                : <Login />}
        </>
    )
}