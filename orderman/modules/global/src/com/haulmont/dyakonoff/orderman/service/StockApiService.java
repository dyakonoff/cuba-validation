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
    Stock getStockForProductByName(@NotNull @Length(min = 1, max = 255) String productName);

    @Validated
    @NotNull
    @RequiredView("_local")
    Stock addNewProduct(@RequiredView("_local") Product product, 
                        @NotNull @DecimalMin("0") @DecimalMax("1000") BigDecimal inStock,
                        @Min(0) BigDecimal optimalLevel);

    @Validated
    @NotNull
    @RequiredView("stock-api-view")
    Stock increaseQuantityByProductName(@NotNull @Length(min = 1, max = 255) String productName,
                                        @NotNull @DecimalMin(value = "0", inclusive = false) @DecimalMax(value = "1000000000") BigDecimal increaseAmount);
}