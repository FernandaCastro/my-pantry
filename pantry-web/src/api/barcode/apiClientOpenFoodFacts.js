import getResponseContent from '../mypantry/getResponseContent.js';
import RequestError from '../mypantry/RequestError.js';

export async function fetchOpenFoodFacts(barcode, method) {

    //V2 : getting cors error
    //get /api/v2/product/{barcode}
    //const URL = "https://world.openfoodfacts.org/api/v2/product/" + barcode;

    //V0
    //get /api/v0/product/{barcode}.json
    const URL = "https://world.openfoodfacts.org/api/v0/product/" + barcode + ".json";

    try {

        const res = await fetch(URL, {
            "method": method,
            "headers": {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
            }
        })

        if (!res) {
            console.log("Fetch API Open Food Facts : response is null");
            return;
        }

        const content = await getResponseContent(res)

        if (res.ok) {
            if (content.status > 0) {
                const product = {
                    barcode: content.code,
                    name: content.product.product_name,
                    size: content.product.quantity,
                    imageUrl: content.product.image_front_small_url
                }
                return product;
            } else {
                throw new RequestError(content.status_verbose, 404, content);
            }
        }

        console.log("Fetch API Open Food Facts: $s - $s", res.status, content.status_verbose);
        throw new RequestError(content.status_verbose, res.status, content)
    }
    catch (error) {
        throw new RequestError(error.message, error.status)
    }
}
