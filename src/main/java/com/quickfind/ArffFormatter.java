package com.quickfind;

import org.jetbrains.annotations.Contract;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.SparseInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by krystian on 5/31/16.
 */
public class ArffFormatter {

    @Contract(pure = true)
    public static Instances format(Set<String> taxonomy, Map<String, List<String>> domainsDocs) {
        ArrayList<Attribute> atts = new ArrayList<>();

        atts.add(new Attribute("_domainName_", (List) null));
        for (String s : taxonomy) {
            atts.add(new Attribute(s));
        }

        Instances data = new Instances("WebsiteTexts", atts, 0);
        double[] vals;

        for(Map.Entry<String, List<String>> entry : domainsDocs.entrySet()) {
            vals = new double[data.numAttributes()];
            vals[0] = data.attribute(0).addStringValue(entry.getKey());
            int i = 1;
            for (String s : taxonomy) {
                vals[i] = Processor.calculateTf(s, entry.getValue());
            }
            /*for (int i = 0; i < vals.length - 1; i++) {
                vals[i+1] = taxonomy.get(i), entry.getValue());

            };*/
            data.add(new SparseInstance(1.0, vals));
        }
        return data;
    }

}
