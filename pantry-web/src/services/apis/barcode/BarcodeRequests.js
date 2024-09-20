import { fetchOpenFoodFacts } from "./OpenFoodFacts";

export async function getBarcodeInfo(barcode) {
    return fetchOpenFoodFacts(barcode, "GET");
}