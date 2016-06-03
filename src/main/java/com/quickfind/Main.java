package com.quickfind;

import java.io.IOException;

/**
 * Created by krystian on 5/31/16.
 */
public class Main {

    public static void main(String[] args) throws IOException {
        new Cli(args).run();

        String[] optionChars = {"i", "i1", "i2"};

//        for (String optionChar : optionChars) {
//            TokenizerTester.checkPennTreebank(new FileReader(new File(cmd.getOptionValue(optionChar))));
//            TokenizerTester.checkTokenizer(new FileReader(new File(cmd.getOptionValue(optionChar))));
//            TokenizerTester.checkSplit(new FileReader(new File(cmd.getOptionValue(optionChar))));
//            TokenizerTester.checkSplitWithStemmer(new FileReader(new File(cmd.getOptionValue(optionChar))));
//        }

        /*TokenizerTester.checkPennTreebank(new FileReader(new File(cmd.getOptionValue("i"))));
        TokenizerTester.checkTokenizer(new FileReader(new File(cmd.getOptionValue("i"))));
        TokenizerTester.checkSplit(new FileReader(new File(cmd.getOptionValue("i"))));*/

        //Weka Tokenizers
        //Java tokenizer +
        //Java tokenizer with Stemmer +
        //Compare with the list of English words

    }
}
