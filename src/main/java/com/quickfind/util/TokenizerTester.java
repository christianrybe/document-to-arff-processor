package com.quickfind.util;

import com.quickfind.Cli;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.trees.PennTreebankTokenizer;
import org.apache.log4j.Logger;
import weka.core.stemmers.SnowballStemmer;
import weka.core.tokenizers.AlphabeticTokenizer;
import weka.core.tokenizers.WordTokenizer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

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
        log.info("Executing PennTreebank");
        long startTime = System.currentTimeMillis();
        PennTreebankTokenizer tokenizer = new PennTreebankTokenizer(r);
        while (tokenizer.hasNext()) {
            TokenizerTester.addToTaxonomyMap(null, tokenizer.next());
        }

        logStats(startTime);
        return Utils.getPrunedTaxonomy(taxonomyFreqs).size();
    }

    public static int checkTokenizer(Reader r) {
        log.info("Executing Tokenizer");
        long startTime = System.currentTimeMillis();
        Tokenizer<Word> tokenizer = PTBTokenizer.PTBTokenizerFactory.newTokenizerFactory().getTokenizer(r, "ptb3Escaping=false, tokenizePerLine=true");
        while (tokenizer.hasNext()) {
            List<Word> words = tokenizer.tokenize();
            for (Word word : words) {
                TokenizerTester.addToTaxonomyMap(null, word.word());
            }
        }
        logStats(startTime);
        return Utils.getPrunedTaxonomy(taxonomyFreqs).size();
    }

    public static int checkSplit(Reader r) throws IOException {
        log.info("Executing split()");
        long startTime = System.currentTimeMillis();
        String line;
        BufferedReader br = new BufferedReader(r);
        while ((line = br.readLine()) != null) {
            String[] tokens = line.split("[\\.,\\s!;?:`‘\"]+");

            for (String token : tokens) {
                TokenizerTester.addToTaxonomyMap(null, token);
            }
        }
        logStats(startTime);
        return Utils.getPrunedTaxonomy(taxonomyFreqs).size();
    }

    public static int checkAlphabeticTokenizer() {
        log.info("Executing Alphabetic Tokenizer");
        long startTime = System.currentTimeMillis();
        weka.core.tokenizers.Tokenizer tokenizer = new AlphabeticTokenizer();
        while (tokenizer.hasMoreElements()) {
            TokenizerTester.addToTaxonomyMap(null, tokenizer.nextElement());
        }
        logStats(startTime);
        return Utils.getPrunedTaxonomy(taxonomyFreqs).size();
    }

    public static int checkWordTokenizer() {
        log.info("Executing Alphabetic Tokenizer");
        long startTime = System.currentTimeMillis();
        weka.core.tokenizers.Tokenizer tokenizer = new WordTokenizer();
        while (tokenizer.hasMoreElements()) {
            TokenizerTester.addToTaxonomyMap(null, tokenizer.nextElement());
        }
        logStats(startTime);
        return Utils.getPrunedTaxonomy(taxonomyFreqs).size();
    }

    public static int checkTokenizerWithStemmer(Reader r) {
        log.info("Executing Tokenizer with stemmer");
        long startTime = System.currentTimeMillis();
        SnowballStemmer stemmer = new SnowballStemmer();
        Tokenizer<Word> tokenizer = PTBTokenizer.PTBTokenizerFactory.newTokenizerFactory().getTokenizer(r, "ptb3Escaping=false, tokenizePerLine=true");
        while (tokenizer.hasNext()) {
            TokenizerTester.addToTaxonomyMap(null, stemmer.stem(tokenizer.next().word()));
        }
        logStats(startTime);
        return Utils.getPrunedTaxonomy(taxonomyFreqs).size();
    }

    public static int checkSplitWithStemmer(Reader r) throws IOException {
        log.info("Executing split() with Snowball stemmer");
        long startTime = System.currentTimeMillis();
        SnowballStemmer stemmer = new SnowballStemmer();
        String line;
        BufferedReader br = new BufferedReader(r);
        while ((line = br.readLine()) != null) {
            String[] tokens = line.split("[\\.,\\s!;?:`‘\"]+");

            for (String token : tokens) {
                TokenizerTester.addToTaxonomyMap(null, stemmer.stem(token));
            }
        }
        logStats(startTime);
        return Utils.getPrunedTaxonomy(taxonomyFreqs).size();
    }

    private static void logStats(long startTime) {
        log.info("Processing time: " + (System.currentTimeMillis() - startTime));
        log.info("Unique terms: " + Utils.getPrunedTaxonomy(taxonomyFreqs).size());
    }

}
