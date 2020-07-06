package br.imd.aqueducte.utils;

import java.text.Normalizer;

public class FormatterUtils {

    public static String treatPrimaryField(String str) {
        NGSILDUtils utils = new NGSILDUtils();
        String strTreated = utils.treatIdOrType(str.replace(" ", "_"));
        return Normalizer.normalize(strTreated, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "");
    }

}
