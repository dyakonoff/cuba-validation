package com.haulmont.dyakonoff.orderman.service;

import com.haulmont.cuba.core.global.BeanValidation;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Messages;
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

    @Inject
    private Messages messages;

    @Override
    public List<Stock> getProductsInStock() {
        LoadContext<Stock> loadContext = LoadContext.create(Stock.class).setQuery(
                LoadContext.createQuery("SELECT s FROM orderman$Stock s")).setView("stock-api-view");
        List<Stock> rez =  dataManager.loadList(loadContext);
        if (rez.size() == 0)
            throw new CustomValidationException(messages.getMainMessage("StockApiService.stockIsEmpty"));

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
            throw new CustomValidationException(messages.formatMainMessage("StockApiService.cantFindProductInStock", productName));

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
        // we don't check for soft-deleted Product and Stock entities here for simplicity
        // if we'd like to do that might need to load entities and check them with isDeleted() method.
        Integer cnt = (Integer) dataManager
                .loadValue("SELECT COUNT(p) FROM orderman$Product p WHERE p.name = :productName", Integer.class)
                .parameter("productName", product.getName())
                .one();
        if (cnt > 0)
            throw new CustomValidationException(messages.formatMainMessage("StockApiService.productExists", product.getName()));

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
                .setView("stock-api-view");
        Stock stock =  dataManager.load(loadContext);

        if (stock == null)
            throw new CustomValidationException(messages.formatMainMessage("StockApiService.cantFindProductInStock", productName));

        stock.setInStock(stock.getInStock().add(increaseAmount));

        dataManager.commit(stock);

        return stock;
    }
}