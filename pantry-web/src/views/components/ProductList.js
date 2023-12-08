import React from 'react';
import Table from 'react-bootstrap/Table';

function ProductList({ productList }) {

    function renderItems() {
        if (productList && productList.length > 0) return productList.map((item) => renderItem(item))
    }

    function renderItem(item) {
        return (
            <tr key={item.pantryId + ":" + item.productId} className="border border-primary-subtle align-middle">
                <td><span>{item.code}</span></td>
                <td><span>{item.description}</span></td>
                <td><span>{item.size}</span></td>
            </tr >
        )
    }

    return (
        <Table variant="primary" hover>
            <tbody>
                <tr key="0:0" className="border border-primary-subtle align-middle">
                    <th scope="col"><span>Code</span></th>
                    <th scope="col"><span>Description</span></th>
                    <th scope="col"><span>Size</span></th>
                </tr>
                {renderItems()}
            </tbody>
        </Table>
    );
}

export default ProductList;