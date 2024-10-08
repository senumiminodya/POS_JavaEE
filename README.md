## API Documentation

### Base URL

All endpoints are accessible via the base URL:

http://localhost:8080

## Overview
The POS System API allows clients to manage orders, customers, and items in a point-of-sale system. The API supports operations such as creating, retrieving, updating, and deleting orders, as well as managing items and customers.

---

## Authentication
This API does not require authentication. Ensure that appropriate security measures are implemented in a production environment.

---
### Endpoints

#### 1. Customer Management

##### *POST /customer*

*Description:* Create a new customer.

*Request:*
- *Content-Type:* application/json
- *Body:*
  json
  {
  "nic": "200071255814",
  "name": "A B C Perera",
  "phoneNo": "07635698521"
  }


*Response:*
- *Status Code:* 201 Created if successful
- *Status Code:* 400 Bad Request if the request is invalid

*Error Response:*
json
{
"error": "Failed to save customer"
}

##### *PUT /customer*

*Description:* Update an existing customer.

*Request:*
- *Content-Type:* application/json
- *Query Parameter:* id - ID of the customer to update
- *Body:*
  json
  {
  "nic": "200071255814",
  "name": "A B C Perera",
  "phoneNo": "0775278965"
  }

*Response:*
- *Status Code:* 200 OK if successful
- *Status Code:* 400 Bad Request if the request is invalid
- *Status Code:* 500 Internal Server Error if the update fails

*Error Response:*
json
{
"error": "Failed to update customer with ID: " + customerID"
}

##### *DELETE /customer*

*Description:* Delete a customer.

*Request:*
- *Query Parameter:* id - ID of the customer to delete

*Response:*
- *Status Code:* 204 No Content if successful
- *Status Code:* 400 Bad Request if the ID is missing
- *Status Code:* 500 Internal Server Error if the deletion fails

*Error Response:*
json
{
"error": "Failed to delete customer with ID: " + customerId"
}

##### *GET /customer*

*Description:* Retrieve all customers.

*Response:*
- *Status Code:* 200 OK with JSON array of customers if successful
- *Status Code:* 204 No Content if no customers are found

*Example Response:*
json
[
{
"id": C-001,
"nic": "200071255814",
"name": "A B C Perera",
"phoneNo": "0775278965"
},
{
"id": C-002,
"nic": "199925892589",
"name": "A B C Silva",
"phoneNo": "0789632584"
}
]

#### 2. Item Management

##### *POST /item*

*Description:* Create a new item.

*Request:*
- *Content-Type:* application/json
- *Body:*
  json
  {
  "name": "Cake",
  "price": 1500.00,
  "qty": 100
  }

*Response:*
- *Status Code:* 201 Created if successful
- *Status Code:* 400 Bad Request if the request is invalid
- *Status Code:* 500 Internal Server Error if the item creation fails

*Error Response:*
json
{
"error": "Save item failed"
}

##### *PUT /item*

*Description:* Update an existing item.

*Request:*
- *Content-Type:* application/json
- *Query Parameter:* id - ID of the item to update
- *Body:*
  json
  {
  "name": "Cake",
  "price": 2000.00,
  "qty": 100
  }

*Response:*
- *Status Code:* 200 OK if successful
- *Status Code:* 400 Bad Request if the request is invalid
- *Status Code:* 500 Internal Server Error if the update fails

*Error Response:*
json
{
"error": "Update failed"
}

##### *DELETE /item*

*Description:* Delete an item.

*Request:*
- *Query Parameter:* id - ID of the item to delete

*Response:*
- *Status Code:* 204 No Content if successful
- *Status Code:* 400 Bad Request if the ID is missing
- *Status Code:* 500 Internal Server Error if the deletion fails

*Error Response:*
json
{
"error": "Delete Failed"
}

##### *GET /item*

*Description:* Retrieve all items.

*Response:*
- *Status Code:* 200 OK with JSON array of items if successful
- *Status Code:* 204 No Content if no items are found

*Example Response:*
json
[
{
"id": I-001,
"name": "cake",
"price": 2000.00,
"qty": 100
},
{
"id": I-002,
"name": "Cup-Cake",
"price": 300.00,
"qty": 60
}
]


#### 3. Order Management

##### *POST /order*

*Description:* Create a new order.

*Request:*
- *Content-Type:* application/json
- *Body:*
  json
  {
  "customerId": C-001,
  "total": 5000.00
  }


*Response:*
- *Status Code:* 200 OK if successful
- *Status Code:* 400 Bad Request if the request is invalid
- *Status Code:* 500 Internal Server Error if the order creation fails

*Error Response:*
json
{
"error": "Order creation failed"
}

##### *GET /order*

*Description:* Retrieve an order by ID or list all orders.

*Request:*
- *Query Parameter:* orderId - (Optional) ID of the order to retrieve

*Response:*
- *Status Code:* 200 OK with JSON representation of the order if successful
- *Status Code:* 404 Not Found if the order is not found
- *Status Code:* 400 Bad Request if the ID is invalid

*Example Response (Single Order):*
json
{
"orderId": 1,
"customerId": C-001,
"total": 5000.00,
"date": "2024-08-26T11:35:45Z"
}


*Example Response (All Orders):*
json
[
{
"orderId": 1,
"customerId": C-001,
"total": 5000.00,
"date": "2024-08-26T11:35:45Z"
}
]

### Error Codes

- *400 Bad Request:* The request could not be understood or was missing required parameters.
- *404 Not Found:* The requested resource could not be found.
- *500 Internal Server Error:* An error occurred on the server.

---

### Front End for this Back End

- *refer this link:* [https://github.com/senumiminodya/POS_JavaEE_Frontend.git](https://github.com/senumiminodya/POS_JavaEE_Frontend.git)

---

