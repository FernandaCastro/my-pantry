import PantriesPieChart from './PantryPieCharts.js';
import Login from './Login.js';
import { useEffect, useMemo, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Button, OverlayTrigger, Stack, Tooltip } from 'react-bootstrap';
import iconMagicWand from '../assets/images/magic-wand.png';
import Image from 'react-bootstrap/Image';
import VariantType from '../components/VariantType.js';
import useAlert from '../hooks/useAlert.js';
import { useGetPantryCharts } from '../hooks/useApiPantry.js';
import { Loading } from '../components/Loading.js';
import useProfile from '../hooks/useProfile.js';

export default function Home() {

    const { t } = useTranslation(['pantry', 'common']);
    const { profile, isLoading: isLoadingProfile } = useProfile();
    const { showAlert } = useAlert();

    const { data: chartData, isLoading: isLoadingCharts } = useGetPantryCharts(
        { email: profile?.email },
        {
            onSuccess: () => {
                showAlert(VariantType.SUCCESS, "ChartData loaded!")
            },
            onError: (error) => showAlert(VariantType.DANGER, error.message)
        }
    );

    function renderNoPantryYet() {
        return (
            <Stack gap={3} >
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
            {(isLoadingProfile || isLoadingCharts) ? <Loading /> :
                (!profile || Object.keys(profile).length === 1) ? <Login /> :
                    (chartData?.length === 0) ? renderNoPantryYet() :
                        <PantriesPieChart key={chartData?.length} chartData={chartData} />
            }
        </>
    )
}