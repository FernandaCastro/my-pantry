import React, { useCallback, useEffect, useState } from "react";
import Quagga from "quagga";
import VariantType from './VariantType.js';
import useAlert from '../hooks/useAlert.js';
import { Button, Image, Row } from "react-bootstrap";
import food from '../assets/images/food-gradient.png';
import { CiBarcode } from "react-icons/ci";
import { RippleLoading } from "./RippleLoading.js";
import iBarcode from "../assets/images/barcode.png";
import { getBarcodeInfo } from "../api/barcode/barcodeService.js";

export default function BarcodeScanner({ active, setActive }) {

    const { showAlert } = useAlert();
    const [isInitialized, setIsInitialized] = useState(false);

    const [barcode, setBarcode] = useState();
    const [barcodeImage, setBarcodeImage] = useState();

    const [product, setProduct] = useState();
    const [processing, setProcessing] = useState(false);

    const [isLandscape, setIsLandscape] = useState(() => {
        const _width = window.innerWidth;
        const _height = window.innerHeight;

        return (_width > _height) ? true : false;
    });

    //9:5
    const width = 386;
    const height = 245;

    // Function to start QuaggaJS
    const startScanner = () => {

        if (isInitialized) return;

        Quagga.init(
            {
                numOfWorkers: navigator.hardwareConcurrency,
                locate: true,
                frequency: 5,
                multiple: false,
                inputStream: {
                    type: "LiveStream", // Uses the live video stream from the camera
                    target: document.querySelector("#scanner-camera"), // ID of the DOM element
                    constraints: {
                        width: width,
                        height: height,
                        facingMode: "environment", // Uses the back camera of mobile devices
                        aspectRatio: isLandscape ? 9 / 5 : 5 / 9
                    },
                },
                // area: { // defines rectangle of the detection/localization area
                //     top: "0%",    // top offset
                //     right: "0%",  // right offset
                //     left: "0%",   // left offset
                //     bottom: "0%"  // bottom offset
                // },
                // debug: {
                //     drawBoundingBox: true,
                //     showFrequency: true,
                //     drawScanline: true,
                //     showPattern: true
                // },
                locator: {
                    halfSample: true,
                    patchSize: "large", // x-small, small, medium, large, x-large
                    // debug: {
                    //     showCanvas: false,
                    //     showPatches: false,
                    //     showFoundPatches: false,
                    //     showSkeleton: false,
                    //     showLabels: false,
                    //     showPatchLabels: false,
                    //     showRemainingPatchLabels: false,
                    //     boxFromPatches: {
                    //         showTransformed: false,
                    //         showTransformedBox: false,
                    //         showBB: false
                    //     }
                    // }
                },
                decoder: {
                    readers: ["code_128_reader", "ean_reader", "ean_8_reader"], // Barcode types
                }
            },
            (err) => {
                if (err) {
                    console.error(err);
                    setProduct(null);
                    return;
                }
                console.log("QuaggaJS initialization succeeded");
                Quagga.start(); // Start scanning

                var drawingCtx = Quagga.canvas.ctx.overlay,
                    drawingCanvas = Quagga.canvas.dom.overlay;

                setIsInitialized(true);
            }
        );

        // Event listener when a barcode is detected
        Quagga.onDetected((data) => {

            console.log("Barcode detected and processed:", data.codeResult.code);
            Quagga.stop(); // Stop scanning once detected (optional)
            setIsInitialized(false);

            if (data.codeResult.code !== barcode) {
                setBarcode(data.codeResult.code);
            }

            const canvas = Quagga.canvas.dom.image;
            setBarcodeImage(canvas.toDataURL());
        });

        // Quagga.onProcessed(function (result) {

        //     var drawingCtx = Quagga.canvas.ctx.overlay,
        //         drawingCanvas = Quagga.canvas.dom.overlay;

        //     if (result) {
        //         // Desenhar as caixas que não são as principais
        //         if (result.boxes) {
        //             drawingCtx.clearRect(0, 0, parseInt(drawingCanvas.getAttribute("width")), parseInt(drawingCanvas.getAttribute("height")));
        //             result.boxes.filter(function (box) {
        //                 return box !== result.box;
        //             }).forEach(function (box) {
        //                 Quagga.ImageDebug.drawPath(box, { x: 0, y: 1 }, drawingCtx, { color: "green", lineWidth: 2 });
        //             });
        //         }

        //         // Desenhar a caixa principal do código de barras
        //         if (result.box) {
        //             Quagga.ImageDebug.drawPath(result.box, { x: 0, y: 1 }, drawingCtx, { color: "blue", lineWidth: 2 });
        //         }

        //         // Desenhar a linha central do código de barras
        //         if (result.codeResult && result.codeResult.code) {
        //             Quagga.ImageDebug.drawPath(result.line, { x: 'x', y: 'y' }, drawingCtx, { color: 'red', lineWidth: 3 });
        //         }
        //     }
        // });

    }

    useEffect(() => {
        if (active) {
            setProduct(null);
            setBarcode(null);

            // Start the scanner when the component mounts
            startScanner();

            return () => {
                // Clean up the scanner when the component unmounts
                Quagga.stop();
                setIsInitialized(false);
                console.log("QuaggaJS dismounted");
            };
        }
    }, [active]);

    useEffect(() => {
        if (barcode && ((product && barcode !== product.barcode) || !product)) {
            fetchBarcodeFromWeb(barcode);
        }
    }, [barcode]);

    const handleWindowResize = useCallback(event => {
        const _width = window.innerWidth;
        const _height = window.innerHeight;

        if (_width > _height) {
            setIsLandscape(true);
            console.log("Landscape mode detected!");
        } else {
            setIsLandscape(false);
            console.log("Portrait mode detected!");
        }
    }, []);

    useEffect(() => {
        window.addEventListener('resize', handleWindowResize);
        return () => {
            window.removeEventListener('resize', handleWindowResize);
        };

    }, [handleWindowResize])

    async function fetchBarcodeFromWeb(barcode) {

        if (processing) return;

        try {
            setProcessing(true);

            const res = await getBarcodeInfo(barcode);
            setProduct(res);
            console.log(res);

            showAlert(VariantType.SUCCESS, res.name);
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        } finally {
            setProcessing(false);
        }
    }

    function handleScan() {
        setProduct(null);
        setBarcode(null);
        startScanner();
    }

    return (
        <div style={{ display: active ? 'flex' : 'none' }} className="scanner-outer-box">

            <div className='m-0 p-0 scanner-box'>

                <div className="d-flex justify-content-between align-items-center">
                    <div className="d-flex align-items-center">
                        <h6 className="title ps-3 pt-0 pb-0">Scan a Barcode </h6>
                        <span className="pt-0 pb-0">{barcode ? " (" + barcode + ')' : null} </span>
                    </div>
                    <Button variant='link' onClick={handleScan} disabled={isInitialized}><CiBarcode className="big-icon" /></Button>
                </div>

                <div hidden={!isInitialized} id="scanner-camera">
                    <Image src={iBarcode} id="overlay-image" className="overlay-image" />
                    <div id="red-line"></div>
                </div>
                <div hidden={isInitialized}><Image src={barcodeImage} style={{ width: width, height: height }} /></div>
            </div>

            <div className='m-0 p-0 scanner-product-box'>
                {processing ? <RippleLoading /> :
                    <>
                        <span className='m-0 ms-3 pt-3 title'>Product Detail</span>
                        <Row className="ms-3 mb-3">
                            <span className="title mb-1 pt-3">Barcode: {product?.barcode}</span>
                            <span className="title mb-1 pt-2">Name: {product?.name}</span>
                            <span className="title mb-1 pt-2">Size: {product?.size}</span>
                        </Row>
                        <Row className="mb-2 product-image">
                            {product ? <Image src={product.imageUrl ? product.imageUrl : food} className="product-image" /> : null}
                        </Row>
                    </>
                }
            </div>
        </div>
    );
};


