package com.quickfind;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Created by krystian on 5/31/16.
 */
public class Main {

    public static void main(String[] args) {
        new Cli(args).run();
    }

    private static void writeArffHeader(BufferedWriter wr) throws IOException {
        wr.write("@RELATION WebsiteTexts");
        wr.newLine();
        wr.newLine();
        wr.write("@ATTRIBUTE domain string");
        wr.newLine();
        wr.write("@ATTRIBUTE text string");
        wr.newLine();
        wr.newLine();
        wr.write("@DATA");
        wr.newLine();
    }
}
