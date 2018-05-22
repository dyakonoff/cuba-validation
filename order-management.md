# A simple order management system

This is a simple order management system that resembles a stock of a small US based online shop that works for domestic market only.

## Data model and constraints

| **Customer**      |           |
|-------------------|-----------|
| Name              | required, should have length >= 1, and should start with Latin letter |
| Email             | required, unique, should be a well-formed email address |
| Phone             | should follow the US phone numbers format: `+1 NXX-NXX-XXXX` , where: `N`=digits 2–9, `X`=digits 0–9 |
| Logo image        |           |
| Address Line 1    | required, should has length greater than 5 |
| Address Line 2    |           |
| Address Line 3    |           |
| Postal code       | required and should follow US ZIP codes format: `12345` or `12345-6789` or `12345 1234` |

Other constraints:
1. Either `name` or `email` should be defined for a customer

----

| **Order**           |         |
|---------------------|---------|
| Customer            | required |
| Date                | required, should be in the past |
| number              | required, unique, should be:  yyyy-MM-dd-incremental_number (unique for 1 day) |
| Status              | required, Values: New, Paid, Cancelled |
| Order items         | required |
| Price               | required, positive |
| Responsible manager | required, (User) |

Other constraints:
1. `price` should be equal to the sum of order item total prices
1. Order can not be committed if there are not enough products in `Stock` for any of `orderItems`

-----

| **Order Item** |          |
|----------------|----------|
| Product        | required |
| Quantity       | required, > 0 |
| Total price    | required, > 0 |

Other constraints:
1. `totalPrice == quantity * product.pricePerMeasure`

-----

| **Product**        |          |
|--------------------|----------|
| Name               | required, should not contain swear words |
| Description        | should not contain swear words
| Measure            | Values: pound, count, pack  |
| Price per measure  | required, > 0 |

Other constraints:
1. `name` and `measure` combination should be unique

-----

| **Stock**                           |          |
|-------------------------------------|----------|
| Product                             | required |
| Stock (how many/much are available) | required, non-negative |
| Optimal stock level                 | >=0 or null |


## Database scheme diagram

![Database structure](resources/database_scheme.png)

## Screens

* Customer: browser + editor with list of orders.
* Order: browser + editor with order items. Actions:
  * Automatically assign responsible manager to the user who creates an order.
  * Automatically calculate total price based on order items
  * When an order is created, corresponding stock is reduced. When an order is cancelled, stock is released back.
* Order item editor. Actions:
  * Automatically calculate total price
  * Check that there is sufficient stock
* Products: browser + editor. Browser should allow search by name, measure
* Stock: master-details (browse and view details/edit/create on one screen)
* Users management: browser and editor

[Back to article](README.md)
