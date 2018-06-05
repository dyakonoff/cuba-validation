package com.haulmont.dyakonoff.orderman.service;

import com.haulmont.cuba.core.global.BeanValidation;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.validation.CustomValidationException;
import com.haulmont.dyakonoff.orderman.entity.Product;
import com.haulmont.dyakonoff.orderman.entity.Stock;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;


@Service(StockApiService.NAME)
public class StockApiServiceBean implements StockApiService {

    @Inject
    private DataManager dataManager;

    @Inject
    private BeanValidation beanValidation;

    @Override
    public List<Stock> getProductsInStock() {
        LoadContext<Stock> loadContext = LoadContext.create(Stock.class).setQuery(
                LoadContext.createQuery("SELECT s FROM orderman$Stock s")).setView("stock-api-view");
        List<Stock> rez =  dataManager.loadList(loadContext);
        if (rez.size() == 0)
            throw new CustomValidationException("Stock is empty");

        return rez;
    }

    @Override
    public Stock getStockForProductByName(String productName) {
        LoadContext<Stock> loadContext = LoadContext.create(Stock.class)
                .setQuery(
                    LoadContext.createQuery("SELECT s FROM orderman$Stock s WHERE s.product.name = :productName")
                        .setParameter("productName", productName))
                .setView("stock-api-view");
        Stock rez =  dataManager.load(loadContext);
        if (rez == null)
            throw new CustomValidationException("Can't find product '" + productName + "' in Stock");

        return rez;
    }

    @Override
    public Stock addNewProduct(Product product, BigDecimal inStock, BigDecimal optimalLevel) {
        // validate the product provided
        Validator validator = beanValidation.getValidator();
        Set<ConstraintViolation<Product>> violations = validator.validate(product);
        if (violations.size() > 0) {
            StringBuilder strBuilder = new StringBuilder();
            for (ConstraintViolation<Product> violation : violations) {
                strBuilder.append(violation.getMessage());
                strBuilder.append("; ");
            }
            throw new CustomValidationException(strBuilder.toString());
        }

        // check if product already exist in the db
        Integer cnt = (Integer) dataManager
                .loadValue("SELECT COUNT(p) FROM orderman$Product p WHERE p.name = :productName", Integer.class)
                .parameter("productName", product.getName())
                .one();
        if (cnt > 0)
            throw new CustomValidationException("Product '" + product.getName() + "' already exists in the DB");

        Product savedProduct = dataManager.commit(product);

        Stock stock = new Stock();
        stock.setInStock(inStock);
        stock.setOptimalStockLevel(optimalLevel);
        stock.setProduct(savedProduct);
        return dataManager.commit(stock);
    }

    @Override
    public Stock increaseQuantityByProductName(String productName, BigDecimal increaseAmount) {
        LoadContext<Stock> loadContext = LoadContext.create(Stock.class)
                .setQuery(
                        LoadContext.createQuery("SELECT s FROM orderman$Stock s WHERE s.product.name = :productName")
                                .setParameter("productName", productName))
                .setView("_local");
        Stock stock =  dataManager.load(loadContext);

        if (stock == null)
            throw new CustomValidationException("Cant find product with name '" + productName + "'");

        stock.setInStock(stock.getInStock().add(increaseAmount));

        return stock;
    }
}