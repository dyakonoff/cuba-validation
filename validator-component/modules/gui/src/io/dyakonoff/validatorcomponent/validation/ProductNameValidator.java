package io.dyakonoff.validatorcomponent.validation;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.gui.components.Field;
import com.haulmont.cuba.gui.components.ValidationException;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProductNameValidator implements Field.Validator {

    private Logger log = LoggerFactory.getLogger(ProductNameValidator.class);

    protected String message;
    protected String messagesPack;
    protected Messages messages = AppBeans.get(Messages.NAME);

    public static String[] swearWords = {
            "f.*?u.*?k",
            "s.*?h.*?t",
            "bi.*?h",
            "bas.*?d",
            "m.*?f.*?",
            "c.*?nt",
            "as.*?s",
            "s.*?ck",
            "w.*?nk",
            "co.*?on",
            "wo.*?g",
            "ni.*?g.*?r",
            "c.*?c.*?k",
            "penis",
            "vagina",
            "c.*?um",
            "p.*?i.*?s",
            "p.*?orn",
            "ar.*?se",
            "nexon",
            "ho.*?r.*?ny",
            "dil.*?do",
            "doggystyle",
            "cl.*?it",
            "fann.*?y",
            "ho.*?re.*?",
            "kn.*?ob",
            "mastur.*?",
            "hitler",
            "n.*?uts",
            "sob.*?",
            "shag.*?",
            "sl.*?ut.*?",
            "testi.*?",
            "t.*?wa.*?t",
            "viagr.*?a",
            "wil.*?ly",
            "wil.*?lie",
            "jism",
            "dog.*?gy",
            "donkeyri.*?b",
            "breas.*?t",
            "bl.*?wjo.*?b",
            "b.*?b",
            "beastiality",
            "an.*?al",
            "cawk",
            "pus.*?s.*?",
            "rim.*?m",
            "ejaculate",
            "ejakulate",
            "er.*?ct",
            "horni",
            "horna",
            "se.*?x",
            "se.*?ck",
            "ga.*?y",
            "fk",
            "we*?nis"
    };

    public ProductNameValidator(Element element, String messagesPack) {
        message = element.attributeValue("message");
        this.messagesPack = messagesPack;
    }


    @Override
    public void validate(Object value) throws ValidationException {
        String productName = (String)value;

        for (String swearWord : swearWords) {
            Pattern pat = Pattern.compile(swearWord, Pattern.CASE_INSENSITIVE);
            Matcher mat = pat.matcher(productName);
            if (mat.find()) {
                log.warn("Bad word found in a product name: " +  productName);

                String msgFormat = message != null ? messages.getTools().loadString(messagesPack, message) : "Bad word is detected '%s'";
                String wordFound = productName.substring(mat.start(), mat.end());
                String errorMsg = String.format(msgFormat, wordFound);
                throw new ValidationException(errorMsg);
            }
        }
    }
}

