import React, { useState, useEffect, useCallback, useContext, useRef } from 'react';
import { Row } from 'react-bootstrap';
import VariantType from '../components/VariantType.js';
import useAlert from '../hooks/useAlert.js';
import { getPantryChartData } from '../services/apis/mypantry/requests/PantryRequests.js';
import { useTranslation } from 'react-i18next';
import i18n from 'i18next';
import { ProfileContext } from '../services/context/AppContext.js';
import { useLoading } from '../hooks/useLoading.js';
import PantryPieChart from '../components/PantryPieChart.js';

export default function PantryPieCharts({ chartData }) {

    const { t } = useTranslation(['pantry', 'common']);
    const { profileCtx } = useContext(ProfileContext);
    const pieRef = useRef(null);

    const colors = [
        { theme: "root", colors: ['#94A6E3', '#928BD2', '#8F6FC2'], labels: '#404040', needle: '#404040' },
        { theme: "theme-dark", colors: ['#94A6E3', '#928BD2', '#8F6FC2'], labels: '#404040', needle: '#D0CFCF' },
        { theme: "theme-mono-light", colors: ['#D76567', '#FFBB28', '#82ca9d'], labels: '#404040', needle: '#2E2E2E' },
        { theme: "theme-mono-dark", colors: ['#D76567', '#FFBB28', '#82ca9d'], labels: '#404040', needle: 'white' }];

    const [data, setData] = useState([
        { key: 'empty', name: t('empty', { ns: 'common' }), detail: '0 - 30%', value: 30 },
        { key: 'good', name: t('good', { ns: 'common' }), detail: '31 - 70%', value: 40 },
        { key: 'full', name: t('full', { ns: 'common' }), detail: '70 - 100%', value: 30 },
    ]);

    const [refreshChart, setRefreshChart] = useState(true);
    const [activeColor, setActiveColor] = useState(colors.findIndex(c => c.theme === profileCtx.theme, 0));
    const { showAlert } = useAlert();
    const { isLoading, setIsLoading } = useLoading();

    const [pantries, setPantries] = useState(chartData);

    useEffect(() => {
        if (!chartData) {
            fetchPantriesChartData();
        }
    }, [])

    useEffect(() => {
        updateChartLabels();
    }, [i18n.language])

    const handleWindowResize = useCallback(event => {
        setRefreshChart(!refreshChart);
    }, []);

    useEffect(() => {
        setActiveColor(colors.findIndex(c => c.theme === profileCtx.theme, 0));
    }, [profileCtx.theme]);

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

    async function fetchPantriesChartData() {
        setIsLoading(true);
        try {
            const res = await getPantryChartData();
            setPantries(res);
            return res;
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        } finally {
            setIsLoading(false);
        }
    }

    return (
        <Row xs={1} xxl={2}>
            {/* {renderPieCharts()} */}
            {pantries?.map((item, index) => <PantryPieChart key={index} item={item} index={index} data={data} activeColor={activeColor} refreshChart={refreshChart}/>)}
        </Row >
    )
}

