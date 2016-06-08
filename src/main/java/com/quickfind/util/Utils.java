package com.quickfind.util;

import org.apache.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import weka.core.Instances;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by krystian on 6/8/16.
 */
public class Utils {
    private static final Logger log = Logger.getLogger(Utils.class);

    private Utils(){
        //Should not be created
    }


    public static void saveArff(Instances data, String fileName) {
        BufferedWriter wr = null;
        try {
            wr = new BufferedWriter(new FileWriter(new File(fileName)));
            wr.write(data.toString());
            wr.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (wr != null) {
                try {
                    wr.close();
                } catch (IOException e) {
                    log.error("Could not close the output file!");
                }
            }
        }
    }

    @Contract(pure=true)
    public static Collection<String> getPrunedTaxonomy(@NotNull Map<String, Integer> taxonomyFreqs) {
        Iterator<Map.Entry<String,Integer>> iter = taxonomyFreqs.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, Integer> entry = iter.next();
            if(entry.getValue() <=2 ) {
                iter.remove();
            }
        }
        return taxonomyFreqs.keySet();
    }
}