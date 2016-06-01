package com.quickfind;

import org.jetbrains.annotations.Contract;

import java.util.List;

/**
 * Created by krystian on 5/31/16.
 */
public class Processor {

    @Contract(pure = true)
    public static double calculateTf(String term, List<String> docTerms) {
        int count = 0;
        for (String docTerm : docTerms) {
            if (term.equals(docTerm)) {
                count++;
            }
        }
        return 0; //count / docTerms.size();
    }
}
