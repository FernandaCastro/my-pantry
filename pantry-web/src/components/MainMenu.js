import { Image, Navbar, Nav, OverlayTrigger, Tooltip } from "react-bootstrap";
import useProfile from "../hooks/useProfile";
import iconConsume from '../assets/images/cook-gradient.png';
import iconPurchase from '../assets/images/shoppingcart-gradient.png';
import iconProduct from '../assets/images/food-gradient.png';
import iconPantry from '../assets/images/cupboard-gradient.png';
import iconSupermarket from '../assets/images/supermarket-gradient.png';
import { useTranslation } from "react-i18next";
import { Loading } from "./Loading";

export default function MainMenu({profile, t}) {

    return (
        <>
            {profile && Object.keys(profile).length > 0 ?
                <Navbar className="p-0" >
                    <div className="menu">
                        <OverlayTrigger placement="bottom" delay={{ show: 250, hide: 400 }} overlay={<Tooltip className="custom-tooltip">{t("tooltip-consume")}</Tooltip>}>
                            <Nav.Item><Nav.Link href={"/pantries/consume"} eventKey="link-consume" className="menuItem" disabled={!profile || !profile.email} >
                                <div className="gradient-icon-box-header"><Image src={iconConsume} className="menu-icon" /></div></Nav.Link>
                            </Nav.Item>
                        </OverlayTrigger>
                        <OverlayTrigger placement="bottom" delay={{ show: 250, hide: 400 }} overlay={<Tooltip className="custom-tooltip">{t("tooltip-purchase")}</Tooltip>}>
                            <Nav.Item>
                                <Nav.Link href="/purchase" eventKey="link-purchases" className="menuItem" disabled={!profile?.email}>
                                    <div className="gradient-icon-box-header"><Image src={iconPurchase} className="menu-icon" /></div>
                                </Nav.Link>
                            </Nav.Item>
                        </OverlayTrigger>

                        <OverlayTrigger placement="bottom" delay={{ show: 250, hide: 400 }} overlay={<Tooltip className="custom-tooltip">{t("tooltip-pantries")}</Tooltip>}>
                            <Nav.Item><Nav.Link href="/pantries" eventKey="link-pantries" className="menuItem" disabled={!profile?.email}>
                                <Image src={iconPantry} className="menu-icon" />
                            </Nav.Link>
                            </Nav.Item>
                        </OverlayTrigger>
                        <OverlayTrigger placement="bottom" delay={{ show: 250, hide: 400 }} overlay={<Tooltip className="custom-tooltip">{t("tooltip-products")}</Tooltip>}>
                            <Nav.Item><Nav.Link href="/product" eventKey="link-products" className="menuItem" disabled={!profile?.email}>
                                <Image src={iconProduct} className="menu-icon" />
                            </Nav.Link>
                            </Nav.Item>
                        </OverlayTrigger>
                        <OverlayTrigger placement="bottom" delay={{ show: 250, hide: 400 }} overlay={<Tooltip className="custom-tooltip">{t("tooltip-supermarkets")}</Tooltip>}>
                            <Nav.Item><Nav.Link href="/supermarkets" eventKey="link-supermarkets" className="menuItem" disabled={!profile?.email}>
                                <Image src={iconSupermarket} className="menu-icon" />
                            </Nav.Link>
                            </Nav.Item>
                        </OverlayTrigger>
                    </div>
                </Navbar> :
                <Loading />
            }</>
    )
}        
