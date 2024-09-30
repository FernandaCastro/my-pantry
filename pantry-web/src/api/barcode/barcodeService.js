import { fetchOpenFoodFacts } from "./apiClientOpenFoodFacts";

export async function getBarcodeInfo(barcode) {
    return fetchOpenFoodFacts(barcode, "GET");
}