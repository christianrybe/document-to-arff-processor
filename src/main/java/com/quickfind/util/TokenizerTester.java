package com.quickfind.util;

import com.quickfind.Cli;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.trees.PennTreebankTokenizer;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import weka.core.tokenizers.AlphabeticTokenizer;
import weka.core.tokenizers.WordTokenizer;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Created by krystian on 6/2/16.
 */
public class TokenizerTester extends Cli {
    private static final Logger log = Logger.getLogger(TokenizerTester.class);


    public TokenizerTester() {
        super();
        options.addOption("i1", "input1", true, "File1 for input to be processed.");
        options.addOption("i2", "input2", true, "File2 for input to be processed.");
    }

    public static int checkPennTreebank(Reader r) {
        String name = "PennTreebank tokenizer";
        long startTime = initAtCurrTime(name);
        PennTreebankTokenizer tokenizer = new PennTreebankTokenizer(r);
        while (tokenizer.hasNext()) {
            TokenizerTester.addToTaxonomyMap(null, tokenizer.next());
        }
        logStats(startTime, name);
        return Utils.getPrunedTaxonomy(taxonomyFreqs).size();
    }

    public static int checkStanfordTokenizer(Reader r) {
        String name = "Stanford tokenizer";
        long startTime = initAtCurrTime(name);
        Tokenizer<Word> tokenizer = PTBTokenizer.PTBTokenizerFactory.newTokenizerFactory().getTokenizer(r, "ptb3Escaping=false, tokenizePerLine=true");
        while (tokenizer.hasNext()) {
            TokenizerTester.addToTaxonomyMap(null, tokenizer.next().word());
        }
        logStats(startTime, name);
        return Utils.getPrunedTaxonomy(taxonomyFreqs).size();
    }

    public static int checkStanfordTokenizerWithStemmer(Reader r) {
        String name = "Stanford tokenizer with Stemmer";
        long startTime = initAtCurrTime(name);
        Tokenizer<Word> tokenizer = PTBTokenizer.PTBTokenizerFactory.newTokenizerFactory().getTokenizer(r, "ptb3Escaping=false, tokenizePerLine=true");
        while (tokenizer.hasNext()) {
            tokenizer.next();
            TokenizerTester.addToTaxonomyMap(null, stemmer.stem(tokenizer.next().word()));
        }
        logStats(startTime, name);
        return Utils.getPrunedTaxonomy(taxonomyFreqs).size();
    }

    public static int checkSplit(Collection<String> lines) throws IOException {
        String name = "split()";
        long startTime = initAtCurrTime(name);
        for (String line : lines) {
            String[] tokens = line.split("[\\.,\\s!;?:`‘\"]+");
            for (String token : tokens) {
                TokenizerTester.addToTaxonomyMap(null, token);
            }
        }
        logStats(startTime, name);
        return Utils.getPrunedTaxonomy(taxonomyFreqs).size();
    }

    public static int checkAlphabeticTokenizer(Collection<String> lines) {
        String name = "Alphabetic Tokenizer";
        long startTime = initAtCurrTime(name);
        weka.core.tokenizers.Tokenizer tokenizer = new AlphabeticTokenizer();
        for (String line : lines) {
            tokenizer.tokenize(line);
            while (tokenizer.hasMoreElements()) {
                TokenizerTester.addToTaxonomyMap(null, tokenizer.nextElement());
            }
        }
        logStats(startTime, name);
        return Utils.getPrunedTaxonomy(taxonomyFreqs).size();
    }

    private static int checkAlphabeticTokenizerWithStemmer(Collection<String> lines) {
        String name = "Alphabetic Tokenizer with stemmer";
        long startTime = initAtCurrTime(name);
        weka.core.tokenizers.Tokenizer tokenizer = new AlphabeticTokenizer();
        for (String line : lines) {
            tokenizer.tokenize(line);
            while (tokenizer.hasMoreElements()) {
                TokenizerTester.addToTaxonomyMap(null, stemmer.stem(tokenizer.nextElement()));
            }
        }
        logStats(startTime, name);
        return Utils.getPrunedTaxonomy(taxonomyFreqs).size();
    }

    public static int checkWordTokenizer(Collection<String> lines) {
        String name = "Word Tokenizer";
        long startTime = initAtCurrTime(name);
        weka.core.tokenizers.Tokenizer tokenizer = new WordTokenizer();
        for (String line : lines) {
            tokenizer.tokenize(line);
            while (tokenizer.hasMoreElements()) {
                TokenizerTester.addToTaxonomyMap(null, tokenizer.nextElement());
            }
        }
        logStats(startTime, name);
        return Utils.getPrunedTaxonomy(taxonomyFreqs).size();
    }

    private static int checkWordTokenizerWithStemmer(Collection<String> lines) {
        String name = "Word Tokenizer with stemmer";
        long startTime = initAtCurrTime(name);
        weka.core.tokenizers.Tokenizer tokenizer = new WordTokenizer();
        for (String line : lines) {
            tokenizer.tokenize(line);
            while (tokenizer.hasMoreElements()) {
                TokenizerTester.addToTaxonomyMap(null, stemmer.stem(tokenizer.nextElement()));
            }
        }
        logStats(startTime, name);
        return Utils.getPrunedTaxonomy(taxonomyFreqs).size();
    }

    public static int checkSplitWithStemmer(Collection<String> lines) throws IOException {
        String name = "split() with stemmer";
        long startTime = initAtCurrTime(name);
        for (String line : lines) {
            String[] tokens = line.split("[\\.,\\s!;?:`‘\"]+");

            for (String token : tokens) {
                TokenizerTester.addToTaxonomyMap(null, stemmer.stem(token));
            }
        }
        logStats(startTime, name);
        return Utils.getPrunedTaxonomy(taxonomyFreqs).size();
    }

    private static long initAtCurrTime(String name) {
        log.info("Executing " + name);
        taxonomyFreqs = new HashMap<>();
        return System.currentTimeMillis();
    }

    private static void logStats(long startTime, String fileName) {
        log.info("Processing time: " + (System.currentTimeMillis() - startTime));
        log.info("Unique terms: " + Utils.getPrunedTaxonomy(taxonomyFreqs).size());
        Utils.saveToFile(taxonomyFreqs.keySet().stream().sorted().reduce("", (x,y) -> x + "\n" + y), fileName + ".txt");
    }

    private static Collection<String> readFileIntoBuffer(String fileName) throws IOException {
        Collection<String> lines = new ArrayList<>();
        String line;
        BufferedReader br = new BufferedReader(new FileReader(new File(fileName)));
        try {
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            log.error("Error reading from file!", e);
            throw e;
        }
        return lines;
    }

    public static void main(String[] args) throws IOException {
        PropertyConfigurator.configure("log4j.properties");
        TokenizerTester tester = new TokenizerTester();
        tester.parseOptions(args);
        String fileName = tester.cmd.getOptionValue("i");

        Collection<String> lines = readFileIntoBuffer(fileName);
        checkWordTokenizer(lines);
        checkAlphabeticTokenizer(lines);
        checkSplit(lines);

        checkPennTreebank(new FileReader(new File(fileName)));
        checkStanfordTokenizer(new FileReader(new File(fileName)));

        checkWordTokenizerWithStemmer(lines);
        checkAlphabeticTokenizerWithStemmer(lines);
        checkStanfordTokenizerWithStemmer(new FileReader(new File(fileName)));
        checkSplitWithStemmer(lines);
    }
}
