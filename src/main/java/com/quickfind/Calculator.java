package com.quickfind;

import org.jetbrains.annotations.Contract;

import java.util.List;

/**
 * Created by krystian on 5/31/16.
 */
public class Calculator {

    @Contract(pure = true)
    public static double calculateTf(String term, List<String> docTerms) {
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
}
