import React, { useState } from 'react';
import { camelCase, truncate } from '../util/Utils.js';
import { Button, Collapse, Stack } from 'react-bootstrap';
import { BsArrow90DegRight } from "react-icons/bs";
import { useTranslation } from 'react-i18next';
import useBreakpoint from '../hooks/useBreakpoint.js';

export default function PantryTopCritical({ pantryChartData }) {

    const { t } = useTranslation(['pantry', 'common']);
    const isXXL = useBreakpoint(1400);

    const topCritical = pantryChartData.criticalItems.length;
    const [isOpen, setIsOpen] = useState(false);

    return (
        <div className="d-flex flex-column">
            <div className="category" onClick={() => setIsOpen(!isOpen)}>
                <Button variant="link" aria-controls={pantryChartData?.id} onClick={() => setIsOpen(!isOpen)}><BsArrow90DegRight className='small-icon' /></Button>
                <h6 className='title'>{t('label-top-low-level')} ({topCritical})</h6>
            </div>
            <Collapse in={isOpen}>
                <div className='scroll-top-critical'>
                    {pantryChartData.criticalItems?.map((item, index) => {
                        return (
                            <Stack key={index} direction="horizontal" className={index % 2 === 0 ? "highlight-background" : null}>
                                <div ><span className="small">{camelCase(truncate(item.productCode, isXXL ? 15 : 30))} </span></div>
                                <div className="ms-auto"><span className="small critical">({item.currentQty})</span></div>
                                <div ><span className="small" >: {item.percentage}%</span></div>
                            </Stack>
                        )
                    })}
                </div>
            </Collapse>
        </div>
    )
}