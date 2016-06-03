package com.quickfind;

import edu.stanford.nlp.process.PTBTokenizer;
import org.apache.commons.cli.*;
import org.apache.log4j.Logger;
import weka.core.Instances;
import weka.core.stemmers.SnowballStemmer;

import java.io.*;
import java.util.*;

/**
 * Created by krystian on 5/31/16.
 */
public class Cli {
    private static final Logger log = Logger.getLogger(Cli.class);

    private Options options = new Options();

    private String[] args = null;
    private CommandLine cmd = null;
    private Set<String> allTerms = null;
    private Set<String> allTerms1 = null;
    private Map<String, Integer> otherTerms = null;

    private List<List<String>> docsTerms = new ArrayList<>();
    private Map<String, List<String>> domainsDocs = new HashMap<>();

    public Cli(String[] args) {
        this.args = args;
        options.addOption("h", "help", false, "Show help.");
        options.addOption("i", "input", true, "File for input to be processed.");
        options.addOption("o", "output", true, "File for output.");
        options.addOption("t", "taxonomy", true, "File with the list of words for document term matrix.");
        options.addOption("i1", "input1", true, "File1 for input to be processed.");
        options.addOption("i2", "input2", true, "File2 for input to be processed.");
    }

    public CommandLine parseOptions() {
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

            if (!(cmd.hasOption("t"))) {
                log.error("Taxonomy file missing.");
                help();
                System.exit(0);
            }

        } catch (ParseException e) {
            log.error("Failed to parseOptions command line arguments", e);
        }
        return cmd;
    }

    private void readDocuments() {
        BufferedReader br = null;
        try {
            SnowballStemmer stemmer = new SnowballStemmer();
            allTerms = new HashSet<>();
            allTerms1 = new HashSet<>();
//            new FileReader(new File(cmd.getOptionValue("i"))));
            PTBTokenizer tokenizer1 = PTBTokenizer.newPTBTokenizer(new FileReader(new File(cmd.getOptionValue("i"))));
            /*while (tokenizer.hasNext()) {
                String term = tokenizer.next().replaceAll("[^a-zA-Z′-]", "").toLowerCase();
                if (!term.isEmpty() && term.length() < LONGEST_WORD) {
                    if (!allTerms.contains(term)) {
                        allTerms.add(term);
                    }
                }
            }*/
/*            while (tokenizer1.hasNext()) {
                String term1 = ((Word) tokenizer1.next()).word().toLowerCase();
                if (!term1.isEmpty() && term1.length() < LONGEST_WORD) {
                    String term2 = term1.replaceAll("[^a-zA-Z′-]", "");
                    if (!allTerms.contains(term2)) {
                        allTerms1.add(term2);
                    }
                }
            }*/
            int count = 9;

//            log.info("Opening file for reading.");
//            br = new BufferedReader(new FileReader(new File(cmd.getOptionValue("i"))));
//            String line;
//            otherTerms = new HashMap<>();
//            int i = 0;
//            while ((line = br.readLine()) != null) {
//                log.debug("Reading document from file... " + i++);
//
//                List<String> terms = new ArrayList<>();
//                String[] columns = line.split(",", 2);
//                String[] tokens = columns[1].split("[\\.,\\s!;?:`‘\"]+");
//
////                Map<String, Double> wordFreqs = new HashMap<>();
//                for (String token : tokens) {
//                    String term = token.toLowerCase().replaceAll("[^a-zA-Z′-]", ""); //stemmer.stem(token.toLowerCase());
//                    if (!term.isEmpty() && term.length() < LONGEST_WORD) {
//                        if (!(allTerms.contains(term))) {
//                            allTerms.add(term);
//                            otherTerms.compute(term, (k,v) -> (v==null) ? 1 : v++ );
//                        }
////                        wordFreqs.compute(term, (k, v) -> (v==null) ? 1 : v++ );
//                        terms.add(term);
//                    }
//                }
////                docWordFreqs.add(wordFreqs);
//                docsTerms.add(terms);
//                domainsDocs.put(columns[0], terms);
////                wordFreqs.forEach( (k,v) -> v = Processor.calculateTf(v, terms.size()));
//            }
        } catch (IOException e) {
            log.error("There was a problem interacting with the file.", e);
        } finally {
/*            Set relevantTerms = new HashSet<>();
            for (Map.Entry<String, Integer> entry : otherTerms.entrySet()) {
                if (entry.getValue() > 1) {
                    if (!relevantTerms.contains(entry.getKey())) {
                        relevantTerms.add(entry.getKey());
                    }
                }
            }*/
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    log.error("Could not close the input file.", e);
                }
            }

        }
    }


/*    private void readTaxonomy() {
        try {
            String allTermsFile = FileUtils.readFileToString(new File(cmd.getOptionValue("t")));
            String[] terms = allTermsFile.split("\\r?\\n");
            allTerms = new HashSet<>(Arrays.asList(terms));
        } catch (IOException e) {
            log.error("Error processing taxonomy!");
        }
    }*/

    private void writeArff(Instances data) {
        BufferedWriter wr = null;
        try {
            wr = new BufferedWriter(new FileWriter(new File(cmd.getOptionValue("o"))));
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

    public void run() {
        parseOptions();
        //readTaxonomy();
        readDocuments();
        //writeArff(ArffFormatter.format(allTerms, domainsDocs));
    }
}

//    private void processTerms() {
//        for(List<String> docTerms : docsTerms) {
//            for (String term : allTerms) {
//                double tf = Processor.calculateTf(term, docTerms);
//            }
//        }


//                double idf = Math.log(docsTerms.size() / docCount);
//            int docCount = 0;
                /*if (docTerms.contains(term)) {
                    docCount++;
                }*/
//            Processor.calculateTf(terms.size());
//        for(Map<String, Integer> wordFreqs)
//    }

//    List<String> test = new ArrayList<>();
//otherTerms.forEach((k,v) -> {if(v > 1) {
//        test.add(k);
//        }});
//        System.out.println(test);