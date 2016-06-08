package com.quickfind;

import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by krystian on 5/31/16.
 */
public class Cli {
    private static final Logger log = Logger.getLogger(Cli.class);

    private static final int MAX_DOCUMENT_SIZE = 100000; //do not process websites bigger than this
    public static Map<String, Integer> taxonomyFreqs = new HashMap<>();
    public static final int LONGEST_WORD = 20; //assume no English word is longer than that

    protected Options options = new Options();
    protected CommandLine cmd = null;

    public Cli() {
        options.addOption(Option.builder("h").longOpt("help").desc("Show help.")
                .build());
        options.addOption(Option.builder("i").argName("csv file").longOpt("input").desc("Input file.")
                .hasArg().required().build());
        options.addOption(Option.builder("o").argName("arff file").longOpt("output").desc("Output file.")
                .hasArg().build());
        options.addOption(Option.builder("x").argName("csv file").longOpt("taxonomy").desc("File with the list of words for document term matrix.")
                .hasArg().build());
    }

    protected static String getOptionsList(@NotNull CommandLine cmd) {
        StringBuilder options = new StringBuilder();
        for (Option option : cmd.getOptions()) {
            options.append(" -" + option.getOpt() + " " + option.getValue());
        }
        return options.toString();
    }

    public void parseOptions(String[] args) {
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        try {
            cmd = parser.parse(options, args);

        } catch (ParseException e) {
            log.error("Failed to parseOptions command line arguments", e);
            formatter.printHelp("data-parser", options);
            System.exit(0);
        }
    }

    public Map<String, Collection<String>> readDocuments(String fileName) {
        log.debug("Reading document " + fileName);
        BufferedReader br = null;
        Map<String, Collection<String>> domainsDocs = new HashMap<>();
        try {
            String line;
            br = new BufferedReader(new FileReader(new File(fileName)));
            while ((line = br.readLine()) != null) {
                Collection<String> terms = new ArrayList<>();
                String[] columns = line.split(",", 2);
                String[] tokens = columns[1].split("[\\.,\\s!;?:`‘\"]+");
                if (tokens.length < MAX_DOCUMENT_SIZE) {
                    for (String token : tokens) {
                        addToTaxonomyMap(terms, token);
                    }
                    domainsDocs.put(columns[0], terms);
                }
            }
        } catch (IOException e) {
            log.error("There was a problem interacting with the file.", e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    log.error("Could not close the input file.", e);
                }
            }

        }
        return domainsDocs;
    }

    protected static Collection<String> addToTaxonomyMap(Collection<String> terms, String token) {
        String term = token.replaceAll("[^a-zA-Z′-]", "").toLowerCase();
        if (!term.isEmpty() && term.length() < LONGEST_WORD && !term.equals("-")) {
            int count = taxonomyFreqs.containsKey(term) ? taxonomyFreqs.get(term) : 0;
            taxonomyFreqs.put(term, count + 1);
            if (terms != null) {
                terms.add(term);
            }
        }
        return terms;
    }

    private Collection<String> readTaxonomy() {
        Collection<String> taxonomy = null;
        try {
            String allTermsFile = FileUtils.readFileToString(new File(cmd.getOptionValue("x")));
            String[] terms = allTermsFile.split("\\r?\\n");
            taxonomy = new HashSet<>(Arrays.asList(terms));
        } catch (IOException e) {
            log.error("Error processing taxonomy!");
        }
        return taxonomy;
    }

}

