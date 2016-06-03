package com.quickfind;

import com.quickfind.util.TokenizerTester;
import org.apache.commons.cli.CommandLine;

import java.io.*;

/**
 * Created by krystian on 5/31/16.
 */
public class Main {

    public static void main(String[] args) throws IOException {
        CommandLine cmd = new Cli(args).parseOptions();

        String[] optionChars = {"i", "i1", "i2"};

        for (String optionChar : optionChars) {
            TokenizerTester.checkPennTreebank(new FileReader(new File(cmd.getOptionValue(optionChar))));
            TokenizerTester.checkPTB(new FileReader(new File(cmd.getOptionValue(optionChar))));
            TokenizerTester.checkTokenizer(new FileReader(new File(cmd.getOptionValue(optionChar))));
            TokenizerTester.checkSplit(new FileReader(new File(cmd.getOptionValue(optionChar))));
        }

        /*TokenizerTester.checkPennTreebank(new FileReader(new File(cmd.getOptionValue("i"))));
        TokenizerTester.checkPTB(new FileReader(new File(cmd.getOptionValue("i"))));
        TokenizerTester.checkTokenizer(new FileReader(new File(cmd.getOptionValue("i"))));
        TokenizerTester.checkSplit(new FileReader(new File(cmd.getOptionValue("i"))));*/

        //Weka Tokenizers
        //Java tokenizer
        //Java tokenizer with Stemmer
        //Compare with the list of English words

    }
}
