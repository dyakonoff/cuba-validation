package io.dyakonoff.controllersvalidation.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service(BadWordsDetectionService.NAME)
public class BadWordsDetectionServiceBean implements BadWordsDetectionService {

    private Logger log = LoggerFactory.getLogger(BadWordsDetectionServiceBean.class);

    @Override
    public String detectBadWords(String text) {
        for (String swearWord : swearWords) {
            Pattern pat = Pattern.compile(swearWord, Pattern.CASE_INSENSITIVE);
            Matcher mat = pat.matcher(text);
            if (mat.find()) {
                log.warn("Bad word '" + mat.group(0) + "' was found in a text: " +  text);
                return mat.group(0);
            }
        }
        return null;
    }

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
}