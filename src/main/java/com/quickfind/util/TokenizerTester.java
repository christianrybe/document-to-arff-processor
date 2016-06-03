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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Created by krystian on 6/2/16.
 */
public class TokenizerTester extends Cli {
    private static final Logger log = Logger.getLogger(TokenizerTester.class);

    private static Map<String, Integer> termsMap;

    public TokenizerTester(String[] args) {
        super(args);
        options.addOption("i1", "input1", true, "File1 for input to be processed.");
        options.addOption("i2", "input2", true, "File2 for input to be processed.");
    }

    public static int checkPennTreebank(Reader r) {
        log.info("Executing PennTreebank");
        long startTime = initialiseCheckerAtCurTime();
        PennTreebankTokenizer tokenizer = new PennTreebankTokenizer(r);
        while (tokenizer.hasNext()) {
            addToTaxonomyAndDoc(tokenizer.next());
        }
        logStats(startTime);
        return taxonomy.size();
    }

    public static int checkTokenizer(Reader r) {
        log.info("Executing Tokenizer");
        long startTime = initialiseCheckerAtCurTime();
        Tokenizer<Word> tokenizer = PTBTokenizer.PTBTokenizerFactory.newTokenizerFactory().getTokenizer(r, "ptb3Escaping=false, tokenizePerLine=true");
        while (tokenizer.hasNext()) {
            List<Word> words = tokenizer.tokenize();
            for (Word word : words) {
                addToTaxonomyAndDoc(word.word());
            }
        }
        logStats(startTime);
        return taxonomy.size();
    }

    public static int checkSplit(Reader r) throws IOException {
        log.info("Executing split()");
        long startTime = initialiseCheckerAtCurTime();
        String line;
        BufferedReader br = new BufferedReader(r);
        while ((line = br.readLine()) != null) {
            String[] tokens = line.split("[\\.,\\s!;?:`‘\"]+");

            for (String token : tokens) {
                addToTaxonomyAndDoc(token);
            }
        }
        logStats(startTime);
        return taxonomy.size();
    }

    public static int checkAlphabeticTokenizer() {
        log.info("Executing Alphabetic Tokenizer");
        long startTime = initialiseCheckerAtCurTime();
        weka.core.tokenizers.Tokenizer tokenizer = new AlphabeticTokenizer();
        while (tokenizer.hasMoreElements()) {
            addToTaxonomyAndDoc(tokenizer.nextElement());
        }
        logStats(startTime);
        return taxonomy.size();
    }

    public static int checkWordTokenizer() {
        log.info("Executing Alphabetic Tokenizer");
        long startTime = initialiseCheckerAtCurTime();
        weka.core.tokenizers.Tokenizer tokenizer = new WordTokenizer();
        while (tokenizer.hasMoreElements()) {
            addToTaxonomyAndDoc(tokenizer.nextElement());
        }
        logStats(startTime);
        return taxonomy.size();
    }

    public static int checkTokenizerWithStemmer(Reader r) {
        log.info("Executing Tokenizer with stemmer");
        long startTime = initialiseCheckerAtCurTime();
        SnowballStemmer stemmer = new SnowballStemmer();
        Tokenizer<Word> tokenizer = PTBTokenizer.PTBTokenizerFactory.newTokenizerFactory().getTokenizer(r, "ptb3Escaping=false, tokenizePerLine=true");
        while (tokenizer.hasNext()) {
            addToTaxonomyAndDoc(stemmer.stem(tokenizer.next().word()));
        }
        logStats(startTime);
        return taxonomy.size();
    }

    public static int checkSplitWithStemmer(Reader r) throws IOException {
        log.info("Executing split() with Snowball stemmer");
        long startTime = initialiseCheckerAtCurTime();
        SnowballStemmer stemmer = new SnowballStemmer();
        String line;
        BufferedReader br = new BufferedReader(r);
        while ((line = br.readLine()) != null) {
            String[] tokens = line.split("[\\.,\\s!;?:`‘\"]+");

            for (String token : tokens) {
                addToTaxonomyAndDoc(stemmer.stem(token));
            }
        }
        logStats(startTime);
        return taxonomy.size();
    }

    private static void logStats(long startTime) {
        log.info("Processing time: " + (System.currentTimeMillis() - startTime));
        log.info("Unique terms: " + taxonomy.size());
//        termsMap.entrySet().stream()
//                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).limit(100).forEach(System.out::println);
//        log.info(sorted);
    }

    private static long initialiseCheckerAtCurTime() {
        long startTime = System.currentTimeMillis();
        taxonomy = new HashSet<>();
        termsMap = new HashMap<>();
        return startTime;
    }

}
