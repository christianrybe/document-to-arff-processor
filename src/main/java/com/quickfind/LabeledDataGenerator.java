package com.quickfind;

import com.quickfind.featurevector.Calculator;
import com.quickfind.featurevector.Generator;
import com.quickfind.util.Utils;
import org.apache.commons.cli.Option;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by krystian on 6/3/16.
 */
public class LabeledDataGenerator extends Cli {
    private static final Logger log = Logger.getLogger(LabeledDataGenerator.class);

    public LabeledDataGenerator() {
        super();
        options.addOption(Option.builder("d").argName("text file").longOpt("domains").hasArg().required()
                .desc("Domains to be labelled positively, newline separated.").build());
        options.addOption(Option.builder("l").argName("csv file").longOpt("learning").hasArg().required()
                .desc("Additional file for learning.").build());
        options.addOption(Option.builder("t").argName("csv file").longOpt("testing").hasArg().required()
                .desc("Additional file for testing.").build());
        options.addOption(Option.builder("n").longOpt("noidf")
                .desc("Do not compute IDF.").build());
    }

    private static Set<String> readPositiveDomains(String domainsFileName) {
        BufferedReader br = null;
        Set<String> positiveDomains = new HashSet<>();
        try {
            br = new BufferedReader(new FileReader(domainsFileName));
            String line;
            while ((line = br.readLine()) != null) {
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

    public static void main(String[] args) {
        PropertyConfigurator.configure("log4j.properties");
        LabeledDataGenerator generator = new LabeledDataGenerator();
        generator.parseOptions(args);

        List<Map> maps = new LinkedList<>();
        maps.add(generator.readDocuments(generator.cmd.getOptionValue("i")));
        maps.add(generator.readDocuments(generator.cmd.getOptionValue("l")));
        maps.add(generator.readDocuments(generator.cmd.getOptionValue("t")));

        Collection<String> taxonomy = Utils.getPrunedTaxonomy(taxonomyFreqs);

        Map<String, Double> idfMap = null;
        if (!generator.cmd.hasOption("n")) {
            idfMap = Calculator.calculateIdf(taxonomy, maps);
        }

        Set<String> positiveDomains = readPositiveDomains(generator.cmd.getOptionValue("d"));

        log.info("Starting tf-idf calculation...");
        Utils.saveToFile(Generator.computeTfIdf(taxonomy, idfMap, maps.get(0), positiveDomains).toString(), "training.arff");
        Utils.saveToFile(Generator.computeTfIdf(taxonomy, idfMap, maps.get(1), positiveDomains).toString(), "learning.arff");
        Utils.saveToFile(Generator.computeTfIdf(taxonomy, idfMap, maps.get(2), positiveDomains).toString(), "testing.arff");
    }

}
