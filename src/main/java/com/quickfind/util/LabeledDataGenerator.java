package com.quickfind.util;

import com.quickfind.ArffFormatter;
import com.quickfind.Calculator;
import com.quickfind.Cli;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;

/**
 * Created by krystian on 6/3/16.
 */
public class LabeledDataGenerator extends Cli {
    private static final Logger log = Logger.getLogger(LabeledDataGenerator.class);

    public LabeledDataGenerator(String[] args) {
        super(args);
        options.addOption("d", "domains", true, "Domains to be labelled positively.");
        options.addOption("l", "learning", true, "Additional file for learning.");
        options.addOption("t", "testing", true, "Additional file for testing.");
    }

    public static void main(String[] args) throws MalformedURLException, InterruptedException {
        PropertyConfigurator.configure("log4j.properties");
        LabeledDataGenerator generator = new LabeledDataGenerator(args);
        generator.parseOptions(args);

        List<Map> maps = new LinkedList<>();
        maps.add(generator.readDocuments(generator.cmd.getOptionValue("i")));
        maps.add(generator.readDocuments(generator.cmd.getOptionValue("l")));
        maps.add(generator.readDocuments(generator.cmd.getOptionValue("t")));

        Map<String, Double> idfMap = Calculator.calculateIdf(taxonomy, maps);
        Set<String> positiveDomains = readPositiveDomains(generator.cmd.getOptionValue("d"));

        generator.writeArff(ArffFormatter.format(taxonomy, idfMap, maps.get(0), positiveDomains), "training.arff");
        generator.writeArff(ArffFormatter.format(taxonomy, idfMap, maps.get(1), positiveDomains), "learning.arff");
        generator.writeArff(ArffFormatter.format(taxonomy, idfMap, maps.get(2), positiveDomains), "testing.arff");
    }

    private static Set<String> readPositiveDomains(String domainsFileName) {
        BufferedReader br = null;
        Set<String> positiveDomains = new HashSet<>();
        try {
            br = new BufferedReader(new FileReader(domainsFileName));
            String line;
            while((line = br.readLine()) != null) {
                positiveDomains.add(line);
            }
        } catch (FileNotFoundException e) {
            LabeledDataGenerator.log.error("Domains file not found!");
        } catch (IOException e) {
            LabeledDataGenerator.log.error("Error reading the domains file!");
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    LabeledDataGenerator.log.error("Could not close the domains file!");
                }
            }
        }
        return positiveDomains;
    }
}
