import React, { useState, useEffect, useCallback, useContext, useRef } from 'react';
import { Row } from 'react-bootstrap';
import VariantType from '../components/VariantType.js';
import useAlert from '../hooks/useAlert.js';
import { getPantryChartData } from '../api/mypantry/pantry/pantryService.js';
import { useTranslation } from 'react-i18next';
import i18n from 'i18next';
import { useLoading } from '../hooks/useGlobalLoading.js';
import PantryPieChart from '../components/PantryPieChart.js';
import useProfile from '../hooks/useProfile.js';

export default function PantryPieCharts({ chartData }) {

    const { t } = useTranslation(['pantry', 'common']);
    //const { profileCtx } = useContext(ProfileContext);
    // const pieRef = useRef(null);
    const { profile, DEFAULT_THEME } = useProfile();

    const colors = [
        { theme: "theme-lila-light", colors: ['#94A6E3', '#928BD2', '#8F6FC2'], labels: '#404040', needle: '#404040' },
        { theme: "theme-lila-dark", colors: ['#94A6E3', '#928BD2', '#8F6FC2'], labels: '#404040', needle: '#D0CFCF' },
        { theme: "theme-mono-light", colors: ['#D76567', '#FFBB28', '#82ca9d'], labels: '#404040', needle: '#2E2E2E' },
        { theme: "theme-mono-dark", colors: ['#D76567', '#FFBB28', '#82ca9d'], labels: '#404040', needle: 'white' }];

    const [data, setData] = useState([
        { key: 'empty', name: t('empty', { ns: 'common' }), detail: '0 - 30%', value: 30 },
        { key: 'good', name: t('good', { ns: 'common' }), detail: '31 - 70%', value: 40 },
        { key: 'full', name: t('full', { ns: 'common' }), detail: '70 - 100%', value: 30 },
    ]);

    const [refreshChart, setRefreshChart] = useState(true);
    const [activeColor, setActiveColor] = useState(() => {
        const theme = profile?.theme ? profile.theme : DEFAULT_THEME;
        return colors.findIndex(c => c.theme === theme, 0);
    });

    // const { showAlert } = useAlert();
    // const { isLoading, setIsLoading } = useLoading();

    // const [pantries, setPantries] = useState(chartData);

    // useEffect(() => {
    //     if (!chartData) {
    //         fetchPantriesChartData();
    //     }
    // }, [])

    useEffect(() => {
        updateChartLabels();
    }, [i18n.language])

    const handleWindowResize = useCallback(event => {
        setRefreshChart(!refreshChart);
    }, []);

    useEffect(() => {
        if (profile && profile.theme) {
            setActiveColor(colors.findIndex(c => c.theme === profile.theme, 0));
        } else {
            setActiveColor(colors.findIndex(c => c.theme === DEFAULT_THEME));
        }
    }, [profile.theme]);

    useEffect(() => {
        window.addEventListener('resize', handleWindowResize);
        return () => {
            window.removeEventListener('resize', handleWindowResize);
        };

    }, [handleWindowResize])

    function updateChartLabels() {
        const _data = data.map(i =>
        ({
            ...i,
            name: t(i.key, { ns: 'common' })
        })
        );

        setData(() => _data);
    }

    // async function fetchPantriesChartData() {
    //     setIsLoading(true);
    //     try {
    //         const res = await getPantryChartData();
    //         setPantries(res);
    //         return res;
    //     } catch (error) {
    //         showAlert(VariantType.DANGER, error.message);
    //     } finally {
    //         setIsLoading(false);
    //     }
    // }

    return (
        <Row xs={1} xxl={2}>
            {/* {renderPieCharts()} */}
            {chartData?.map((item, index) => <PantryPieChart key={index} item={item} index={index} data={data} activeColor={activeColor} refreshChart={refreshChart} />)}
        </Row >
    )
}

