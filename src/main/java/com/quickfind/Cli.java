package com.quickfind;

import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import weka.core.Instances;

import java.io.*;
import java.util.*;

/**
 * Created by krystian on 5/31/16.
 */
public class Cli {
    private static final Logger log = Logger.getLogger(Cli.class);

    private static final int LONGEST_WORD = 20; //assume no English word is longer than that
    protected static Set<String> taxonomy = new HashSet<>();

    protected Options options = new Options();
    protected CommandLine cmd = null;

    public Cli(String[] args) {
        options.addOption("h", "help", false, "Show help.");
        options.addOption("i", "input", true, "File for input to be processed.");
        options.addOption("o", "output", true, "File for output.");
        options.addOption("x", "taxonomy", true, "File with the list of words for document term matrix.");
    }

    protected static void addToTaxonomyAndDoc(String s) {
        String term = s.replaceAll("[^a-zA-Z′-]", "").toLowerCase();
        if (!term.isEmpty() && term.length() < LONGEST_WORD && !term.equals("-")) {
            if (!taxonomy.contains(term)) {
                taxonomy.add(term);
            }
//            int count = termsMap.containsKey(term) ? termsMap.get(term) : 0;
//            termsMap.put(term, count + 1);
        }
    }

    public CommandLine parseOptions(String[] args) {
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
        return cmd;
    }

    public Map<String, Collection<String>> readDocuments(String fileName) {
        BufferedReader br = null;
        Map<String, Collection<String>> domainsDocs = new HashMap<>();
        try {
            String line;
            br = new BufferedReader(new FileReader(new File(fileName)));
            while ((line = br.readLine()) != null) {
                List<String> terms = new ArrayList<>();
                String[] columns = line.split(",", 2);
                String[] tokens = columns[1].split("[\\.,\\s!;?:`‘\"]+");
                for (String token : tokens) {
                    String term = token.replaceAll("[^a-zA-Z′-]", "").toLowerCase();
                    if (!term.isEmpty() && term.length() < LONGEST_WORD && !term.equals("-")) {
                        if (!taxonomy.contains(term)) {
                            taxonomy.add(term);
                        }
                        terms.add(term);
                    }
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

    private void readTaxonomy() {
        try {
            String allTermsFile = FileUtils.readFileToString(new File(cmd.getOptionValue("x")));
            String[] terms = allTermsFile.split("\\r?\\n");
            taxonomy = new HashSet<>(Arrays.asList(terms));
        } catch (IOException e) {
            log.error("Error processing taxonomy!");
        }
    }

    public void writeArff(Instances data, String fileName) {
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

    private void help() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("data-processor", options);
        System.exit(0);

    }
}

