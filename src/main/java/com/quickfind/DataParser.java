package com.quickfind;

import com.quickfind.featurevector.Calculator;
import com.quickfind.featurevector.Generator;
import com.quickfind.util.Utils;
import org.apache.commons.cli.Option;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * Created by krystian on 6/15/16.
 */
public class DataParser extends Cli {
    private static final Logger log = Logger.getLogger(DataParser.class);


    public DataParser() {
        options.addOption(Option.builder("i").argName("csv file").longOpt("input").desc("Input file.")
                .hasArg().required().build());
        options.addOption(Option.builder("o").argName("arff file").longOpt("output").desc("Output file.")
                .hasArg().required().build());
        options.addOption(Option.builder("x").argName("taxonomy file").longOpt("taxonomy")
                .desc("File with external taxonomy.").hasArg().build());
    }

    public static void main(String[] args) throws FileNotFoundException {
        PropertyConfigurator.configure("log4j.properties");
        DataParser parser = new DataParser();
        parser.parseOptions(args);

        Collection<String> taxonomy;
        if (parser.options.hasOption("x")) {
            taxonomy = Utils.loadTaxonomyFromFile(parser.cmd.getOptionValue("x"));
        } else {
            taxonomy = Utils.getPrunedTaxonomy(taxonomyFreqs);
        }

        List<Map> maps = new LinkedList<>();
        maps.add(parser.readDocuments(parser.cmd.getOptionValue("i")));

        Map<String, Double> idfMap = null;
        if (!parser.cmd.hasOption("n")) {
            idfMap = Calculator.calculateIdf(taxonomy, maps);
        }

        log.info("Starting tf-idf calculation...");
        Utils.saveToFile(Generator.computeTfIdf(taxonomy, idfMap, maps.get(0)).toString(), parser.cmd.getOptionValue("o"));
    }
}
