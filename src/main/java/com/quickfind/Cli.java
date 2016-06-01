package com.quickfind;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.core.stemmers.SnowballStemmer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by krystian on 5/31/16.
 */
public class Cli {
    private static final Logger log = LoggerFactory.getLogger(Cli.class);
    private Options options = new Options();

    private String[] args = null;
    private CommandLine cmd = null;
    private Set<String> allTerms = null;

    private List<List<String>> docsTerms = new ArrayList<>();
    private Map<String, List<String>> domainsDocs = new ArrayList<>();

    public Cli(String[] args) {
        this.args = args;
        options.addOption("h", "help", false, "Show help.");
        options.addOption("i", "input", true, "File for input to be processed.");
        options.addOption("o", "output", true, "File for output.");
    }

    public void parseOptions() {
        CommandLineParser parser = new BasicParser();

        try {
            cmd = parser.parse(options, args);

            if (cmd.hasOption("h")) {
                help();
                System.exit(0);
            }

            if (!(cmd.hasOption("i") && cmd.hasOption("o"))) {
                log.error("Output or input file missing.");
                help();
                System.exit(0);
            }
        } catch (ParseException e) {
            log.error("Failed to parseOptions command line arguments", e);
        }
    }

    private void readDocuments() {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(new File(cmd.getOptionValue("i"))));
            allTerms = new HashSet<>();
            String line;
            SnowballStemmer stemmer = new SnowballStemmer();
            while ((line = br.readLine()) != null) {
                String[] columns = line.split(",", 2);
                String[] tokens = columns[1].replaceAll("[^a-zA-Z\\s]", "").split("\\s");
                List<String> terms = new ArrayList<>();
//                Map<String, Double> wordFreqs = new HashMap<>();
                for (String token : tokens) {
                    if (!token.isEmpty()) {
                        String term = stemmer.stem(token.toLowerCase());
                        System.out.println(term);
                        if (!allTerms.contains(term)) {
                            allTerms.add(term);
                        }
//                        wordFreqs.compute(term, (k, v) -> (v==null) ? 1 : v++ );
                        terms.add(term);
                    }
                }
//                docWordFreqs.add(wordFreqs);
                docsTerms.add(terms);
                domainsDocs.add(columns[0]);
//                wordFreqs.forEach( (k,v) -> v = Processor.calculateTf(v, terms.size()));
            }
        } catch (IOException e) {
            log.error("There was a problem interacting with the file.",e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    log.error("Could not close the file.", e);
                }
            }

        }
    }

    private void processTerms() {
        for(List<String> docTerms : docsTerms) {
            for (String term : allTerms) {
                double tf = Processor.calculateTf(term, docTerms);
            }
        }


//                double idf = Math.log(docsTerms.size() / docCount);
//            int docCount = 0;
                /*if (docTerms.contains(term)) {
                    docCount++;
                }*/
//            Processor.calculateTf(terms.size());
//        for(Map<String, Integer> wordFreqs)
    }

    private void help() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("data-processor", options);
        System.exit(0);
    }

    public void run() {
        parseOptions();
        readDocuments();
        processTerms();
        new ArffPrinter().print(allTerms);
    }
}

/*
BufferedWriter wr = null;
            wr = new BufferedWriter(new FileWriter(new File(cmd.getOptionValue("o"))));
wr.write(String.format("%s,'%s'",columns[0], columns[1].replaceAll("[^a-zA-Z]", "")));
                wr.newLine();
                if (wr != null) {
                try {
                    wr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
 */

