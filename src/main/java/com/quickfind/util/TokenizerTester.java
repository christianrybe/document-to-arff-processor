package com.quickfind.util;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.trees.PennTreebankTokenizer;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;

/**
 * Created by krystian on 6/2/16.
 */
public class TokenizerTester {
    private static final Logger log = Logger.getLogger(TokenizerTester.class);

    private static final int LONGEST_WORD = 20; //assume no English word is longer than that
    private static Set<String> allTerms;
    private static Map<String, Integer> termsMap;

    public static int checkPennTreebank(Reader r) {
        log.info("Executing PennTreebank");
        long startTime = initialiseCheckerAtCurTime();
        PennTreebankTokenizer tokenizer = new PennTreebankTokenizer(r);
        while (tokenizer.hasNext()) {
            addToTerms(tokenizer.next());
        }
        logStats(startTime);
        return allTerms.size();
    }

    public static int checkPTB(Reader r) {
        log.info("Executing PTB");
        long startTime = initialiseCheckerAtCurTime();
        Tokenizer<CoreLabel> tokenizer = PTBTokenizer.PTBTokenizerFactory.newPTBTokenizerFactory(false, false).getTokenizer(r, "ptb3Escaping=false");
        while (tokenizer.hasNext()) {
            addToTerms(tokenizer.next().word());
        }
        logStats(startTime);
        return allTerms.size();
    }

    public static int checkTokenizer(Reader r) {
        log.info("Executing Tokenizer");
        long startTime = initialiseCheckerAtCurTime();
        Tokenizer<Word> tokenizer = PTBTokenizer.PTBTokenizerFactory.newTokenizerFactory().getTokenizer(r, "ptb3Escaping=false");
        while (tokenizer.hasNext()) {
            addToTerms(tokenizer.next().word());
        }
        logStats(startTime);
        return allTerms.size();
    }

    public static int checkSplit(Reader r) throws IOException {
        log.info("Executing split()");
        long startTime = initialiseCheckerAtCurTime();
        String line;
        BufferedReader br = new BufferedReader(r);
        while ((line = br.readLine()) != null) {
            String[] tokens = line.split("[\\.,\\s!;?:`‘\"]+");

            for (String token : tokens) {
                addToTerms(token);
            }
        }
        logStats(startTime);
        return allTerms.size();
    }

    private static void logStats(long startTime) {
        log.info("Processing time: " + (System.currentTimeMillis() - startTime));
        log.info("Unique terms: " + allTerms.size());
//        termsMap.entrySet().stream()
//                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).limit(100).forEach(System.out::println);
//        log.info(sorted);
    }

    private static long initialiseCheckerAtCurTime() {
        long startTime = System.currentTimeMillis();
        allTerms = new HashSet<>();
        termsMap = new HashMap<>();
        return startTime;
    }

    private static void addToTerms(String s) {
        String term = s.replaceAll("[^a-zA-Z′-]", "").toLowerCase();
        if (!term.isEmpty() && term.length() < LONGEST_WORD && !term.equals("-")) {
            if (!allTerms.contains(term)) {
                allTerms.add(term);
            }
            int count = termsMap.containsKey(term) ? termsMap.get(term) : 0;
            termsMap.put(term, count + 1);
//            termsMap.putIfAbsent(term, new AtomicInteger(0));
//            termsMap.get(term).incrementAndGet();
        }

    }
}
