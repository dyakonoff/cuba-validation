package io.dyakonoff.controllersvalidation.web.product;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.FieldGroup;
import com.haulmont.cuba.gui.components.ValidationErrors;
import com.haulmont.cuba.gui.components.ValidationException;
import io.dyakonoff.controllersvalidation.entity.Product;
import io.dyakonoff.controllersvalidation.service.BadWordsDetectionService;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Map;

public class ProductEdit extends AbstractEditor<Product> {
    @Inject
    private FieldGroup fieldGroup;

    @Inject
    private BadWordsDetectionService badWordsDetectionService;


    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        // Adding validator manually
        // for more examples see:
        // https://www.cuba-platform.com/discuss/t/how-to-implement-error-display-when-using-custom-validator/2870/6

        fieldGroup.getField("nameField").addValidator( value -> {
            String productName = (String) value;
            String badWord = badWordsDetectionService.detectBadWords(productName);
            if (badWord != null) {
                throw new ValidationException("Product name should not contain a word '" + badWord + "'");
            }
        });

    }


    @Override
    protected void postValidate(ValidationErrors errors) {
        super.postValidate(errors);
        Product product = getItem();
        if (product.getRetail() && product.getPrice().compareTo(new BigDecimal(10000)) > 0) {
            errors.add("Retail product can not have price greater than 10,000");
        }
    }
}