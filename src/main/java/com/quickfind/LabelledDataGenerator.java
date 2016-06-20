package com.quickfind;

import com.quickfind.featurevector.Calculator;
import com.quickfind.featurevector.Generator;
import com.quickfind.util.Utils;
import org.apache.commons.cli.Option;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.FileNotFoundException;
import java.util.*;

/**
 * Created by krystian on 6/3/16.
 */
public class LabelledDataGenerator extends Cli {
    private static final Logger log = Logger.getLogger(LabelledDataGenerator.class);

    public LabelledDataGenerator() {
        options.addOption(Option.builder("x").argName("csv file").longOpt("taxonomy").desc("File with the list of words for document term matrix (not used yet).")
                .hasArg().build());
        options.addOption(Option.builder("d").argName("text file").longOpt("domains").hasArg().required()
                .desc("List of domains that should be labelled positively, newline separated.").build());
        options.addOption(Option.builder("t").argName("csv file").longOpt("training").hasArg().required()
                .desc("CSV file for the training set.").build());
        options.addOption(Option.builder("l").argName("csv file").longOpt("learning").hasArg().required()
                .desc("CSV file for the learning set.").build());
        options.addOption(Option.builder("s").argName("csv file").longOpt("testing").hasArg().required()
                .desc("CSV file for the testing set.").build());
        options.addOption(Option.builder("x").argName("taxonomy file").longOpt("taxonomy")
                .desc("File with external taxonomy.").hasArg().build());
    }

    public static void main(String[] args) throws FileNotFoundException {
        PropertyConfigurator.configure("log4j.properties");
        LabelledDataGenerator generator = new LabelledDataGenerator();
        generator.parseOptions(args);

        List<Map> maps = new LinkedList<>();
        maps.add(generator.readDocuments(generator.cmd.getOptionValue("t")));
        maps.add(generator.readDocuments(generator.cmd.getOptionValue("l")));
        maps.add(generator.readDocuments(generator.cmd.getOptionValue("s")));

        Collection<String> taxonomy;
        if (generator.options.hasOption("x")) {
            taxonomy = Utils.loadTaxonomyFromFile(generator.cmd.getOptionValue("x"));
        } else {
            taxonomy = Utils.getPrunedTaxonomy(taxonomyFreqs);
        }

        Map<String, Double> idfMap = null;
        if (!generator.cmd.hasOption("n")) {
            idfMap = Calculator.calculateIdf(taxonomy, maps);
        }

        Set<String> positiveDomains = Utils.readPositiveDomains(generator.cmd.getOptionValue("d"));

        log.info("Starting tf-idf calculation...");
        log.info("Generating training set...");
        Utils.saveToFile(Generator.computeTfIdf(taxonomy, idfMap, maps.get(0), positiveDomains).toString(), "training.arff");
        log.info("Generating learning set...");
        Utils.saveToFile(Generator.computeTfIdf(taxonomy, idfMap, maps.get(1), positiveDomains).toString(), "learning.arff");
        log.info("Generating testing set...");
        Utils.saveToFile(Generator.computeTfIdf(taxonomy, idfMap, maps.get(2), positiveDomains).toString(), "testing.arff");
    }

}
