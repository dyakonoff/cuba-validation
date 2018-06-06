package com.haulmont.dyakonoff.orderman.service;

import com.haulmont.cuba.core.global.validation.RequiredView;
import com.haulmont.dyakonoff.orderman.entity.Product;
import com.haulmont.dyakonoff.orderman.entity.Stock;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

public interface StockApiService {
    String NAME = "orderman_StockApiService";

    @Validated
    @NotNull
    @RequiredView("stock-api-view")
    List<Stock> getProductsInStock();

    @Validated
    @NotNull
    @RequiredView("stock-api-view")
    Stock getStockForProductByName(@NotNull(message = "{msg://com.haulmont.dyakonoff.orderman.service/StockApiService.productNameMissing)")
                                   @Length(min = 1, max = 255, message = "{msg://com.haulmont.dyakonoff.orderman.service/StockApiService.productName}")
                                           String productName);

    @Validated
    @NotNull
    @RequiredView("_local")
    Stock addNewProduct(@RequiredView("_local")
                                Product product,
                        @NotNull
                        @DecimalMin("0")
                        @DecimalMax(value = "1000", message = "{msg://com.haulmont.dyakonoff.orderman.service/StockApiService.inStockLimit}")
                                BigDecimal inStock,
                        @Min(0)
                                BigDecimal optimalLevel);

    @Validated
    @NotNull
    @RequiredView("stock-api-view")
    Stock increaseQuantityByProductName(@NotNull(message = "{msg://com.haulmont.dyakonoff.orderman.service/StockApiService.productNameMissing)")
                                        @Length(min = 1, max = 255, message = "{msg://com.haulmont.dyakonoff.orderman.service/StockApiService.productName}")
                                                String productName,
                                        @NotNull
                                        @DecimalMin(value = "0", inclusive = false)
                                        @DecimalMax(value = "1000")
                                                BigDecimal increaseAmount);
}