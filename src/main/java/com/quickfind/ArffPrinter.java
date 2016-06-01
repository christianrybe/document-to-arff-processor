package com.quickfind;

import weka.core.Attribute;
import weka.core.Instances;
import weka.core.SparseInstance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by krystian on 5/31/16.
 */
public class ArffPrinter {

    private ArrayList<Attribute> atts = new ArrayList<Attribute>();
    private Instances data;
    private double[] vals;

    public void print(Iterable<String> taxonomy) {
        Map<String, Double> totalFreqs = new HashMap<>();

        atts.add(new Attribute("domain", (List) null));
        for (String s : taxonomy) {
            atts.add(new Attribute(s));
        }

        data = new Instances("WebsiteTexts", atts, 0);

        vals = new double[data.numAttributes()];
        vals[0] = data.attribute(0).addStringValue("testing.com");
        vals[1] = Math.random();
        vals[2] = Math.random();
        data.add(new SparseInstance(1.0, vals));

        vals = new double[data.numAttributes()];
        vals[0] = data.attribute(0).addStringValue("another.com");
        vals[1] = 0;
        vals[2] = 1;
        data.add(new SparseInstance(1.0, vals));

        System.out.println(data);
    }

}
