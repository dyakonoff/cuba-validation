# Using REST for CUBA application calls

This examples use [HTTPie client](https://httpie.org/).

CUBA REST v2 API documentation: http://files.cuba-platform.com/swagger/6.8/

## Getting OAuth2 token

Run command:

```bash
http --form --auth client:secret  POST localhost:8080/app/rest/v2/oauth/token grant_type=password username=admin password=admin
```

It will return something like that:

```http
HTTP/1.1 200
Content-Type: application/json;charset=UTF-8

{
    "access_token": "819fe70b-7881-43ca-98ef-b5749f417f49",
    "expires_in": 43199,
    "refresh_token": "abb067ad-26fb-4ad9-96f2-6d424a438f12",
    "scope": "rest-api",
    "token_type": "bearer"
}
```

Copy the `access_token` and use it in the `''Authorization:Bearer  '` section in your `http` commands.

## Working with Generic REST API v2

### HTTP GET: Getting list of Customers

```bash
http localhost:8080/app/rest/v2/entities/orderman\$Customer 'Authorization:Bearer 819fe70b-7881-43ca-98ef-b5749f417f49'
```

### HTTP POST: Creating new Customer

```bash
http --json POST localhost:8080/app/rest/v2/entities/orderman\$Customer 'Authorization:Bearer 819fe70b-7881-43ca-98ef-b5749f417f49'  @rest/customer_john_smith.json
```

## Further reading

* [Improvements in CUBAs REST API v2](https://www.road-to-cuba-and-beyond.com/improvements-in-cubas-rest-api-v2/) by Mario David
* [HTTPie documentation](https://httpie.org/doc#request-items)
