# A simple order management system

This is a simple order management system that resembles a stock of a small US based online shop that works for domestic market only.

[Back to the article](README.md)

## Data model and constraints

|   | **Customer**      |           |
|---|-------------------|-----------|
| + | name              | required, should have length >= 1, and should start with Latin letter |
| + | email             | should be a well-formed email address |
| + | phone             | should follow the US phone numbers format: `+1 NXX-NXX-XXXX` , where: `N`=digits 2–9, `X`=digits 0–9 |
|   | logoImage         |           |
| + | addressLine1      | required, should has length greater than 5 |
|   | addressLine2      |           |
|   | addressLine3      |           |
| + | postalCode        | required and should follow US ZIP codes format: `12345` or `12345-6789` or `12345 1234` |

Other constraints:

* (+) Either `phone` or `email` should be defined for a customer

----

|   | **Order**           |         |
|---|---------------------|---------|
| + | customer            | required |
| + | date                | required, should be in the past |
| + | number              | required, unique, should be:  yyyy-MM-dd-incremental_number (unique for 1 day) |
| + | status              | required, Values: New, Paid, Cancelled |
| + | orderItems          | required, should have  1 <= size <= 10 (we don't allow to make big orders), all orderItemsShould be valid |
| + | price               | required, positive |

Other constraints:

* (+) `price` should be equal to the sum of order item total prices
* (+) Order can not be committed if there are not enough products in `Stock` for any of `orderItems`

----

|   | **Order Item** |          |
|---|----------------|----------|
| + | product        | required |
| + | quantity       | required, > 0, should not be equal to 666 or 777|
| + | subTotal       | required, > 0 |

Other constraints:

* (+) `totalPrice == quantity * product.pricePerMeasure`
* (+) `quantity` should be an whole number greater than 0 if `product.measure == COUNT || product.measure == PACK` otherwise `quantity` could have a fractional part

----

|   | **Product**        |          |
|---|--------------------|----------|
| + | name               | required, should not contain swear words |
| + | description        | should not contain swear words
| + | measure            | required, Values: pound, count, pack  |
| + | pricePerMeasure    | required, > 0 |

Other constraints:

* (+) `name` and `measure` combination should be unique

----

|   | **Stock**         |          |
|---|-------------------|----------|
| + | product           | required, unique |
| + | inStock           | required, non-negative (how many/much are available) |
| + | optimalStockLevel | >=0 or null |

[Back to the article](README.md)

## Database scheme diagram

![Database structure](resources/database_scheme.png)

[Back to the article](README.md)

## Screens

* Customer: browser + editor with list of orders.
* Order: browser + editor with order items. Actions:
  * Automatically calculate total price based on order items
  * When an order is created, corresponding stock is reduced. When an order is cancelled, stock is released back.
* Order item editor. Actions:
  * Automatically calculate total price
  * Check that there is sufficient stock
* Products: browser + editor. Browser should allow search by name, measure
* Stock: master-details (browse and view details/edit/create on one screen)

[Back to the article](README.md)
