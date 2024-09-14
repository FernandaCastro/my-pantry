import React, { useState } from 'react';
import { camelCase } from '../services/Utils.js';
import { Button, Collapse, Row } from 'react-bootstrap';
import { BsArrow90DegRight } from "react-icons/bs";
import { useTranslation } from 'react-i18next';

export default function PantryTopCritical({ pantryChartData }) {

    const { t } = useTranslation(['pantry', 'common']);

    const topCritical = pantryChartData.criticalItems.length;
    const [isOpen, setIsOpen] = useState(false);

    return (
        <div className="d-flex flex-column">
            <div className="category" onClick={() => setIsOpen(!isOpen)}>
                <Button variant="link" aria-controls={pantryChartData?.id} onClick={() => setIsOpen(!isOpen)}><BsArrow90DegRight className='small-icon' /></Button>
                <h6 className='title'>{t('label-top-low-level')}{topCritical === 0 ? ` (${topCritical})` : null}</h6>
            </div>
            <Collapse in={isOpen}>
                <Row className='m-0'>
                    {pantryChartData.criticalItems?.map((item, index) => {
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