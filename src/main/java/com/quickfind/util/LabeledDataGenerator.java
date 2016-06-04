package com.quickfind.util;

import com.quickfind.ArffFormatter;
import com.quickfind.Cli;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.*;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by krystian on 6/3/16.
 */
public class LabeledDataGenerator extends Cli {
    private static final Logger log = Logger.getLogger(LabeledDataGenerator.class);

    private Set<String> positiveDomains = new HashSet<>();

    public LabeledDataGenerator(String[] args) {
        super(args);
        options.addOption("d", "domains", true, "Domains to be labelled positively.");
    }

    public static void main(String[] args) throws MalformedURLException {
        PropertyConfigurator.configure("log4j.properties");
        LabeledDataGenerator generator = new LabeledDataGenerator(args);
        generator.parseOptions();
        generator.readDocuments();
        generator.readPositiveDomains();
        generator.writeArff(ArffFormatter.format(taxonomy, generator.domainsDocs, generator.positiveDomains));
    }

    private void readPositiveDomains() {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(new File(this.cmd.getOptionValue("d"))));
            String line;
            while((line = br.readLine()) != null) {
                positiveDomains.add(line);
            }
        } catch (FileNotFoundException e) {
            log.error("Domains file not found!");
        } catch (IOException e) {
            log.error("Error reading the domains file!");
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    log.error("Could not close the domains file!");
                }
            }
        }
    }
}
