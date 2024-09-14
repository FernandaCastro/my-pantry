import { PieChart, Pie, Cell, Sector, ResponsiveContainer } from 'recharts';
import React, { useState, useCallback } from 'react';
import { useTranslation } from 'react-i18next';

const PieChartWithNeedle = React.memo(props => {
    const { index, data, activeColor, stockLevel, refreshChart } = props;

    const { t } = useTranslation(['pantry', 'common']);

    const [activeIndex, setActiveIndex] = useState(-1),
        onMouseEnter = useCallback((_, i) => setActiveIndex(i), []),
        onMouseOut = useCallback((_, i) => setActiveIndex(-1), []);

    const RADIAN = Math.PI / 180;

    const cx = 170;
    const cy = 140;
    const iR = 50;
    const oR = 100;

    const colors = [
        { theme: "root", colors: ['#94A6E3', '#928BD2', '#8F6FC2'], labels: '#404040', needle: '#404040' },
        { theme: "theme-dark", colors: ['#94A6E3', '#928BD2', '#8F6FC2'], labels: '#404040', needle: '#D0CFCF' },
        { theme: "theme-mono-light", colors: ['#D76567', '#FFBB28', '#82ca9d'], labels: '#404040', needle: '#2E2E2E' },
        { theme: "theme-mono-dark", colors: ['#D76567', '#FFBB28', '#82ca9d'], labels: '#404040', needle: 'white' }];

    const renderActiveShape = (props) => {
        const { cx, cy, midAngle, innerRadius, outerRadius, startAngle, endAngle, fill, payload } = props;

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
                style={{ fontWeight: "semibold", fontSize: "small", pointerEvents: 'none' }}
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


    function renderPieChartNeedle(stockLevel) {

        let total = 0;
        props.data.forEach((v) => {
            total += v.value;
        });
        const _ang = 180.0 * (1 - stockLevel / total);
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
            <circle key={`circle-${cx}`} cx={x0} cy={y0} r={r} fill={colors[props.activeColor].needle} stroke="none" style={{ pointerEvents: 'none' }} />,
            <path key={`path-${cx}`} d={`M${xba} ${yba}L${xbb} ${ybb} L${xp} ${yp} L${xba} ${yba}`} stroke="#none" fill={colors[props.activeColor].needle} style={{ pointerEvents: 'none' }} />,
            <text key={`stockLevel-${cx}`} className="chart-text" x={cx} y={cy} dx={10} dy={30} textAnchor="middle" style={{ pointerEvents: 'none' }}>
                {stockLevel !== 'NaN' ? stockLevel + '%' : t('no-items-defined')}
            </text>
        ];

    };

    return (
        <ResponsiveContainer key={refreshChart} width="100%" minHeight={200}>
            <PieChart key={"piechardt-"+index} width={cx} height={200}>
                <Pie key={"pie-"+index}
                    activeIndex={activeIndex}
                    activeShape={renderActiveShape}
                    isAnimationActive={true}
                    // isAnimationActive={animate}
                    // onAnimationStart={onAnimationStart}
                    onClick={onMouseEnter}
                    onMouseOut={onMouseOut}
                    onMouseEnter={onMouseEnter}
                    startAngle={180}
                    endAngle={0}
                    data={data}
                    cx={cx}
                    cy={cy}
                    innerRadius={iR}
                    outerRadius={oR}
                    label={customPieLabel}
                    labelLine={false}
                    stroke="none"
                >
                    {props.data.map((entry, index) => (
                        <Cell key={`cell-${index}`} style={{ outline: 'none' }} fill={colors[activeColor].colors[index]} />
                    ))}
                </Pie>
                {renderPieChartNeedle(stockLevel)}
            </PieChart >
        </ResponsiveContainer>
    );
})

export default PieChartWithNeedle;



