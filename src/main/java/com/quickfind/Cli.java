package com.quickfind;

import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import weka.core.stemmers.SnowballStemmer;
import weka.core.tokenizers.Tokenizer;
import weka.core.tokenizers.WordTokenizer;

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
    protected static SnowballStemmer stemmer = new SnowballStemmer();

    public static Map<String, Integer> taxonomyFreqs = new HashMap<>();
    public static final int LONGEST_WORD = 20; //assume no English word is longer than that

    protected Options options = new Options();
    protected CommandLine cmd = null;

    public Cli() {
        options.addOption(Option.builder("h").longOpt("help").desc("Show help.")
                .build());
        options.addOption(Option.builder("n").longOpt("noidf").desc("Do not compute IDF.")
                .build());
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
            log.info("Starting processing with options: " + getOptionsList(cmd));
        } catch (ParseException e) {
            log.error("Invalid arguments passed!");
            formatter.printHelp("data-parser", options);
            System.exit(0);
        }
    }

    public Map<String, List<String>> readDocuments(String fileName) {
        log.debug("Reading document " + fileName);
        BufferedReader br = null;
        Map<String, List<String>> domainsDocs = new HashMap<>();
        try {
            Tokenizer tokenizer = new WordTokenizer();
            String line;
            br = new BufferedReader(new FileReader(new File(fileName)));
            while ((line = br.readLine()) != null) {
                List<String> terms = new ArrayList<>();

                int tokenCount = 0;
                String[] columns = line.split(",", 2);
                tokenizer.tokenize(columns[1]);
                while (tokenizer.hasMoreElements() && tokenCount < MAX_DOCUMENT_SIZE) {
                    terms = addToTaxonomyMap(terms, tokenizer.nextElement());
                    tokenCount++;
                }
                domainsDocs.put(columns[0], terms);
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

    protected static List<String> addToTaxonomyMap(List<String> terms, String token) {
            String term = stemmer.stem(token.replace("^-|(?<=\\s)-\\w+", "").replaceAll("[^a-zA-Z′-]", "").toLowerCase());
        if (!term.isEmpty() && term.length() < LONGEST_WORD && term.length() > 1) {
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

