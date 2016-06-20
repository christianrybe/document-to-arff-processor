package com.quickfind.util;

import org.apache.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.*;

/**
 * Created by krystian on 6/8/16.
 */
public class Utils {
    private static final Logger log = Logger.getLogger(Utils.class);

    private Utils(){
        //Should not be created
    }


    public static void saveToFile(String data, String fileName) {
        BufferedWriter wr = null;
        try {
            wr = new BufferedWriter(new FileWriter(new File(fileName)));
            wr.write(data);
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
            if(entry.getValue() <= 2 ) {
                iter.remove();
            }
        }
        return taxonomyFreqs.keySet();
    }

    public static HashSet<String> loadTaxonomyFromFile(String fileName) {
        File file = new File(fileName);
        HashSet<String> taxonomy = null;
        try {
            ObjectInputStream stream = new ObjectInputStream(new FileInputStream(file));
            taxonomy = (HashSet<String>) stream.readObject();
            stream.close();
        } catch (IOException e) {
            log.error("Error reading taxonomy file!", e);
        } catch (ClassNotFoundException e) {
            log.error("Error loading taxonomy!", e);
        }
        return taxonomy;
    }

    public static Set<String> readPositiveDomains(String domainsFileName) throws FileNotFoundException {
        BufferedReader br = null;
        Set<String> positiveDomains = new HashSet<>();
        try {
            br = new BufferedReader(new FileReader(domainsFileName));
            String line;
            while ((line = br.readLine()) != null) {
                positiveDomains.add(line);
            }
        } catch (IOException e) {
            log.error("Error reading the domains file!");
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    log.error("Could not close the domains file!");
                }
            }
        }
        return positiveDomains;
    }

    public static void saveTaxonomyToFile(HashSet<String> taxonomy) {
        File file = new File("taxonomy");
        try {
            ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(file));
            stream.writeObject(taxonomy);
            stream.flush();
            stream.close();
        } catch (IOException e) {
            log.error("Error saving taxonomy!", e);
        }

    }
}
