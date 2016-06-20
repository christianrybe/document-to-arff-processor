package com.quickfind.featurevector;

import org.apache.log4j.Logger;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.SparseInstance;

import java.util.*;

/**
 * Created by krystian on 5/31/16.
 */
public class Generator {
    private static final Logger log = Logger.getLogger(Generator.class);
    private static final int MULTIPLIER = 100000; //to increase precision of the document matrix entries

    public static Instances computeTfIdf(Collection<String> taxonomy, Map<String, Double> idfMap,
                                         Map<String, Collection<String>> domainsDocs, Set<String> positiveDomains) {
        long startTime = System.currentTimeMillis();
        ArrayList<Attribute> atts = new ArrayList<>();

        atts.add(new Attribute("_domainName_", (List) null));
        for (String s : taxonomy) {
            atts.add(new Attribute(s));
        }
        List attVals = new ArrayList<>();
        attVals.add("positive");
        attVals.add("negative");
        atts.add(new Attribute("Class", attVals));

        Instances data = new Instances("WebsiteTexts", atts, 0);
        double[] vals;

        for (Map.Entry<String, Collection<String>> entry : domainsDocs.entrySet()) {
            vals = calculateVector(taxonomy, idfMap, data, entry);
            if (positiveDomains.contains(entry.getKey())) {
                vals[vals.length - 1] = attVals.indexOf("positive");
            } else {
                vals[vals.length - 1] = attVals.indexOf("negative");
            }
            data.add(new SparseInstance(1.0, vals));
        }
        log.debug(("Total processing time: " + (System.currentTimeMillis() - startTime)));
        return data;
    }

    public static Instances computeTfIdf(Collection<String> taxonomy, Map<String, Double> idfMap, Map<String, Collection<String>> domainsDocs) {
        long startTime = System.currentTimeMillis();
        ArrayList<Attribute> atts = new ArrayList<>();

        atts.add(new Attribute("_domainName_", (List) null));
        for (String s : taxonomy) {
            atts.add(new Attribute(s));
        }
        List attVals = new ArrayList<>();
        attVals.add("positive");
        attVals.add("negative");
        atts.add(new Attribute("Class", attVals));

        Instances data = new Instances("WebsiteTexts", atts, 0);
        double[] vals;

        for (Map.Entry<String, Collection<String>> entry : domainsDocs.entrySet()) {
            vals = calculateVector(taxonomy, idfMap, data, entry);
            data.add(new SparseInstance(1.0, vals));
        }
        log.debug(("Total processing time: " + (System.currentTimeMillis() - startTime)));
        return data;
    }

    private static double[] calculateVector(Collection<String> taxonomy, Map<String, Double> idfMap, Instances data, Map.Entry<String, Collection<String>> entry) {
        double[] vals;
        log.debug(entry.getKey());
        vals = new double[data.numAttributes()];
        vals[0] = data.attribute(0).addStringValue(entry.getKey());

        int i = 1;
        for (String s : taxonomy) {
            if (idfMap == null) {
                vals[i] = Calculator.calculateTf(s, entry.getValue()) * MULTIPLIER;
            } else {
                vals[i] = Calculator.calculateTf(s, entry.getValue()) * idfMap.get(s) * MULTIPLIER;
            }
            i++;
        }
        return vals;
    }
}
