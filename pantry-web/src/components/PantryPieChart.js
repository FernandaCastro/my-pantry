import { Button, Card, Col } from 'react-bootstrap';
import PantryTopCritical from "./PantryTopCritical";
import PieChartWithNeedle from "./PieChartWithNeedle";
import { BsCardChecklist } from "react-icons/bs";
import { useTranslation } from 'react-i18next';

export default function PantryPieChart({ item, index, data, activeColor, refreshChart }) {

    const { t } = useTranslation(['pantry', 'common']);

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
                        <span className="subtitle small"> - {item.type === 'R' ? t("type-recurring") : t("type-no-recurring")}</span>
                        <span className="subtitle small">{!item.isActive ? " - " + t("inactive") : null}</span>
                    </Card.Subtitle>
                    <div className="d-flex justify-items-start align-items-top">
                        <PieChartWithNeedle key={"cahrdt-"+index} refreshChart={index + refreshChart} index={index} data={data} activeColor={activeColor} stockLevel={item.percentage}/>
                        <div className="d-none d-md-block w-50">
                            <PantryTopCritical key={"critical"+index} pantryChartData={item}/>
                        </div>
                    </div>
                    <div className="d-md-none">
                        <PantryTopCritical key={index} pantryChartData={item}/>
                    </div>
                </Card.Body>
            </Card>
        </Col>
    )
}