package com.quickfind;

import org.apache.log4j.Logger;
import org.jetbrains.annotations.Contract;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by krystian on 5/31/16.
 */
public class Calculator {
    private static final Logger log = Logger.getLogger(Calculator.class);

    @Contract(pure = true)
    public static double calculateTf(String term, Collection<String> docTerms) {
        double count = 0;
        for (String docTerm : docTerms) {
            if (term.equals(docTerm)) {
                count++;
            }
        }
        if (docTerms.size() != 0) {
            return count / docTerms.size();
        } else
            return 0;
    }

    @Contract(pure=true)
    public static Map<String, Double> calculateIdf(Collection<String> terms, Collection<Collection<String>> documents) {
        Map<String, Double> idfMap = new HashMap<>();
        int i = 0;

        for (String term : terms) {

            if (i % 10 == 0) {
                log.debug("Calculating IDF... " + i + "/" + terms.size());
            }
            i++;

            double count = 0.001; //to avoid division by 0
            for (Collection<String> document : documents) {
                if (document.contains(term)) {
                    count++;
                }
            }
            idfMap.put(term, Math.log10(documents.size() / count));
        }
        return idfMap;
    }
}
