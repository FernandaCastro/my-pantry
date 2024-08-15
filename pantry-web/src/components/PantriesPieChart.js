import React, { useState, useEffect, useCallback, useContext } from 'react';
import { PieChart, Pie, Cell, Sector, ResponsiveContainer } from 'recharts';
import { Button, Card, Col, Collapse, Row } from 'react-bootstrap';
import VariantType from '../components/VariantType.js';
import useAlert from '../hooks/useAlert.js';
import { getPantryChartData } from '../services/apis/mypantry/requests/PantryRequests.js';
import { camelCase } from '../services/Utils.js';
import { BsArrow90DegRight, BsCardChecklist } from "react-icons/bs";
import { useTranslation } from 'react-i18next';
import i18n from 'i18next';
import { ProfileContext } from '../services/context/AppContext';

export default function PantriesPieChart() {

    const { t } = useTranslation(['pantry', 'common']);
    const { profileCtx } = useContext(ProfileContext);

    //['#a83238', '#FFBB28', '#82ca9d']
    //['#B5B4B4', '#8F8F8F', '#6A6969'

    const [data, setData] = useState([
        { key: 'empty', name: t('empty', { ns: 'common' }), detail: '0 - 30%',   value: 30 },
        { key: 'good',  name: t('good',  { ns: 'common' }), detail: '31 - 70%',  value: 40 },
        { key: 'full',  name: t('full',  { ns: 'common' }), detail: '70 - 100%', value: 30},
    ]);

    const colors = [
        { theme: "root", colors: ['#94A6E3', '#928BD2', '#8F6FC2'], labels: '#404040', needle: '#404040' },
        { theme: "theme-dark", colors: ['#94A6E3', '#928BD2', '#8F6FC2'], labels: '#404040', needle: '#D0CFCF' },
        { theme: "theme-mono-light", colors: ['#D76567', '#FFBB28', '#82ca9d'], labels: '#404040', needle: '#2E2E2E' },
        { theme: "theme-mono-dark", colors: ['#D76567', '#FFBB28', '#82ca9d'], labels: '#404040', needle: 'white' }];
    
    //  { theme: "theme-mono-light", colors: ['#94A6E3', '#928BD2', '#8F6FC2'], labels: ['#404040', '#404040', '#404040'], needle: '#2E2E2E' },
    //  { theme: "theme-mono-dark", colors: ['#94A6E3', '#928BD2', '#8F6FC2'], labels: ['#404040', '#404040', '#4C4B4B'], needle: '#D0CFCF' }];

    const RADIAN = Math.PI / 180;

    const cy = 140;
    const iR = 50;
    const oR = 100;
    const [cx, setCx] = useState(calculateCX());

    const [activeIndex, setActiveIndex] = useState(-1);
    const [activeColor, setActiveColor] = useState(colors.findIndex(c => c.theme === profileCtx.theme, 0));
    const { showAlert } = useAlert();
    const [isLoading, setIsLoading] = useState(true);
    const [pantries, setPantries] = useState([]);
    const [topCriticals, setTopCriticals] = useState([]);

    const renderNeedle = (value) => {
        let total = 0;
        data.forEach((v) => {
            total += v.value;
        });
        const _ang = 180.0 * (1 - value / total);
        const ang = _ang < 0 ? 0 : _ang;
        const length = (iR + 2 * oR) / 3;
        const sin = Math.sin(-RADIAN * ang);
        const cos = Math.cos(-RADIAN * ang);
        const r = 5;
        const x0 = cx + 5;
        const y0 = cy + 5;
        const xba = x0 + r * sin;
        const yba = y0 - r * cos;
        const xbb = x0 - r * sin;
        const ybb = y0 + r * cos;
        const xp = x0 + length * cos;
        const yp = y0 + length * sin;

        return [
            <circle key={`circle-${cx}`} cx={x0} cy={y0} r={r} fill={colors[activeColor].needle} stroke="none" />,
            <path key={`path-${cx}`} d={`M${xba} ${yba}L${xbb} ${ybb} L${xp} ${yp} L${xba} ${yba}`} stroke="#none" fill={colors[activeColor].needle} />,
        ];
    };

    const renderActiveShape = (props) => {
        const { cx, cy, midAngle, innerRadius, outerRadius, startAngle, endAngle, fill, payload, percent, value } = props;
        const sin = Math.sin(-RADIAN * midAngle);
        const cos = Math.cos(-RADIAN * midAngle);
        const sx = cx + (outerRadius + 10) * cos;
        const sy = cy + (outerRadius + 10) * sin;
        const mx = cx + (outerRadius + 10) * cos;
        const my = cy + (outerRadius + 20) * sin;
        const ex = mx + (cos >= 0 ? 1 : -1) * 10;
        const ey = my;
        const textAnchor = cos >= 0 ? 'start' : 'end';

        return (
            <g>
                <Sector
                    cx={cx}
                    cy={cy}
                    innerRadius={innerRadius}
                    outerRadius={outerRadius}
                    startAngle={startAngle}
                    endAngle={endAngle}
                    fill={fill}
                />
                <Sector
                    cx={cx}
                    cy={cy}
                    startAngle={startAngle}
                    endAngle={endAngle}
                    innerRadius={outerRadius + 6}
                    outerRadius={outerRadius + 10}
                    fill={fill}
                />
                <path d={`M${sx},${sy}L${mx},${my}L${ex},${ey}`} stroke={fill} fill="none" />
                <circle cx={ex} cy={ey} r={2} fill={fill} stroke="none" />
                <text className="chart-text" x={ex + (cos >= 0 ? 1 : -1) * 2} y={ey} textAnchor={textAnchor}>{payload.detail}</text>
                {/* <text className="chart-text" x={ex + (cos >= 0 ? 1 : -1) * 5} y={ey} textAnchor={textAnchor} fill="#333">{payload.name}</text>
            <text className="chart-subtext" x={ex + (cos >= 0 ? 1 : -1) * 5} y={ey} dy={18} textAnchor={textAnchor} >
                {payload.detail}
            </text> */}
            </g>
        );
    };

    const customPieLabel = (props) => {
        const outerRadius = props.outerRadius;
        const cx = props.cx;
        const cy = props.cy;
        const midAngle = props.midAngle;

        const radius = iR + (oR - iR) * 0.5;

        return (
            <text
                style={{ fontWeight: "semibold", fontSize: "small" }}
                x={cx + radius * Math.cos(-midAngle * RADIAN)}
                y={cy + radius * Math.sin(-midAngle * RADIAN)}
                textAnchor="middle"
                dominantBaseline="middle"
                fill={colors[activeColor].labels}
            >
                {props.payload.name}
            </text>
        );
    };

    useEffect(() => {
        fetchPantriesChartData();
    }, [])

    const handleWindowResize = useCallback(event => {
        setCx(calculateCX());
    }, []);

    useEffect(() => {
        updateChartLabels();
    }, [i18n.language])

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

    function calculateCX() {
        const elements = document.getElementsByClassName("recharts-surface");
        if (elements.length) {
            const newCX = elements[0].getAttribute("width") / 2;
            return newCX;
        }
        return 180;
    }

    function onPieEnter(_, index) {
        setActiveIndex(index);
    };

    async function fetchPantriesChartData() {
        setIsLoading(true);
        try {
            const res = await getPantryChartData();
            setPantries(res);
            setIsLoading(false);
            setCx(calculateCX());
            populateTopCritical(res);
            return res;
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        }
    }

    function populateTopCritical(data) {
        var list = [];
        var id = 0;

        data.forEach((p) => {

            id = p.id
            var found = topCriticals.find(c => c.id == id);

            list = [...list,
            {
                id: p.id,
                isOpen: found != null ? found.isOpen : false
            }
            ]

        });
        setTopCriticals(list);
    }

    function handleExpansion(id) {

        var newList = topCriticals.map((c) => {
            return (c.id === id) ?
                c = { ...c, isOpen: !c.isOpen } : c;
        });

        setTopCriticals(newList);
    }

    function getOpen(id) {
        var found = topCriticals.find(c => c.id === id);
        return found ? found.isOpen : false;
    }

    function renderPieChart(index, stockLevel) {

        return (
            <ResponsiveContainer key={i18n.language} width="100%" minHeight={200}>
                <PieChart key={`${index}-${cx}`} width={400} height={200}>
                    <Pie key={index}
                        activeIndex={activeIndex}
                        activeShape={renderActiveShape}
                        onPointerOver={onPieEnter}
                        onPointerOut={() => setActiveIndex(-1)}
                        onAnimationEnd={() => setCx(calculateCX())}
                        onClick={onPieEnter}
                        startAngle={180}
                        endAngle={0}
                        data={data}
                        // cx={cx}
                        cy={cy}
                        innerRadius={iR}
                        outerRadius={oR}
                        label={customPieLabel}
                        labelLine={false}
                        stroke="none"

                    >
                        {
                            data.map((entry, index) => (
                                <Cell key={`cell-${index}`} fill={colors[activeColor].colors[index]} />
                            ))}
                    </Pie>
                    {renderNeedle(stockLevel)}
                    <text className="chart-text" x={cx} y={cy} dx={10} dy={30} textAnchor="middle">
                        {stockLevel}%
                    </text>
                </PieChart >
            </ResponsiveContainer>
        );
    }

    function renderTopCritical(pantryChart) {
        const topCritical = pantryChart.criticalItems.length;
        return (
            <div className="d-flex flex-column">
                <div className="category" onClick={() => handleExpansion(pantryChart?.id)}>
                    <Button variant="link" aria-controls={pantryChart?.id} onClick={() => handleExpansion(pantryChart.id)}><BsArrow90DegRight className='small-icon' /></Button>
                    <h6 className='title'>{t('label-top-low-level')}{topCritical === 0 ? ` (${topCritical})` : null}</h6>
                </div>
                <Collapse in={getOpen(pantryChart?.id)}>
                    <Row className='m-0'>
                        {pantryChart.criticalItems?.map((item, index) => {
                            return (
                                <li key={index} className="top-critical-item">
                                    <span className="small">{camelCase(item.productCode)} </span>
                                    <span className="small critical">({item.currentQty})</span>
                                    <span className="small" >: {item.percentage}%</span></li>
                            )
                        })}
                    </Row>
                </Collapse>
            </div>
        )
    }

    return (
        <Row xs={1} xxl={2}>
            {pantries?.map((item, index) => {
                return (
                    <Col key={item.id} className="d-flex flex-column g-3">
                        <Card className="card1">
                            <Card.Body className='d-flex flex-column'>
                                <Card.Title className="d-flex align-items-center pb-0 mb-0">
                                    <span className="title" disabled={!item.isActive}>{item.name}</span>
                                    <Button href={"/pantries/" + item.id + "/items"} variant="link"><BsCardChecklist className='icon' /></Button>
                                </Card.Title>
                                <Card.Subtitle >
                                    <span className="subtitle small">{item.accountGroup?.name}</span>
                                    <span className="subtitle small"> - {item.type === 'R'? t("type-recurring") : t("type-no-recurring")}</span>
                                    <span className="subtitle small">{!item.isActive ? " - " + t("inactive") : null}</span>
                                </Card.Subtitle>
                                <div className="d-flex justify-items-start align-items-top">
                                    {renderPieChart(index, item.percentage)}
                                    <div className="d-none d-md-block w-50">
                                        {renderTopCritical(item)}
                                    </div>
                                </div>
                                <div className="d-md-none">
                                    {renderTopCritical(item)}
                                </div>
                            </Card.Body>
                        </Card>
                    </Col>
                )
            })}
        </Row >
    )
}
