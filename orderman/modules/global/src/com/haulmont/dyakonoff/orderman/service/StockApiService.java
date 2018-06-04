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
    List<Stock> getProductsInStock();

    @Validated
    @NotNull
    @RequiredView("stock-api-view")
    Stock getStockForProductByName(@NotNull @Length(min = 1) String productName);

    @Validated
    void addNewProduct(@RequiredView("_local") Product product, @NotNull @DecimalMin("0") @DecimalMax("1000") BigDecimal inStock, @Min(0) BigDecimal optimalLevel);

    @Validated
    void increaseQuantityByProductName(@NotNull @Length(min = 1) String productName, @NotNull @DecimalMin(value = "0", inclusive = false) BigDecimal increaseAmount);
}