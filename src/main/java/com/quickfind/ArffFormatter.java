package com.quickfind;

import org.apache.log4j.Logger;
import org.jetbrains.annotations.Contract;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.SparseInstance;

import java.util.*;

/**
 * Created by krystian on 5/31/16.
 */
public class ArffFormatter {
    private static final Logger log = Logger.getLogger(ArffFormatter.class);
    private static final int MULTIPLIER = 100000; //to increase precision of the document matrix entries


    @Contract(pure = true)
    public static Instances format(Set<String> taxonomy, Map<String, Collection<String>> domainsDocs) throws InterruptedException {
        ArrayList<Attribute> atts = new ArrayList<>();

        //atts.add(new Attribute("_domainName_", (List) null));
        for (String s : taxonomy) {
            atts.add(new Attribute(s));
        }

        Instances data = new Instances("WebsiteTexts", atts, 0);
        double[] vals;

        for (Map.Entry<String, Collection<String>> entry : domainsDocs.entrySet()) {
            log.debug("Reading entry: " + entry.getKey());
            vals = new double[data.numAttributes()];
            //vals[0] = data.attribute(0).addStringValue(entry.getKey());
            int i = 0;
            for (String s : taxonomy) {
                vals[i] = Calculator.calculateTf(s, entry.getValue());
                i++;
            }
            data.add(new DenseInstance(1.0, vals));
        }
        return data;
    }

    public static Instances format(Set<String> taxonomy, Map<String, Double> idfMap, Map<String, Collection<String>> domainsDocs, Set<String> positiveDomains) throws InterruptedException {
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

//        Map<String, Double> idfMap = Calculator.calculateIdf(taxonomy, domainsDocs.values());

        for (Map.Entry<String, Collection<String>> entry : domainsDocs.entrySet()) {
            log.debug(entry.getKey());
            vals = new double[data.numAttributes()];
            vals[0] = data.attribute(0).addStringValue(entry.getKey());

            /*AtomicInteger i = new AtomicInteger(1);
            ExecutorService exec = Executors.newCachedThreadPool();
            for (String s : taxonomy) {
                Future<Double> future = exec.submit(() -> Calculator.calculateTf(s, entry.getValue()) *//** idfMap.get(s)*//* * MULTIPLIER);
                try {
                    vals[i.getAndIncrement()] = future.get();
                } catch (ExecutionException e) {
                    log.error("Could not get value of future.", e);
                }

            }
            exec.shutdown();
            exec.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
*/
            int i = 1;
            for (String s : taxonomy) {
                vals[i] = Calculator.calculateTf(s, entry.getValue()) * idfMap.get(s) * MULTIPLIER;
                i++;
            }
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
}
