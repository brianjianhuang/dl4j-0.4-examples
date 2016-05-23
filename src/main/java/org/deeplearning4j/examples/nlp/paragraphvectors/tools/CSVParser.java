package org.deeplearning4j.examples.nlp.paragraphvectors.tools;

import au.com.bytecode.opencsv.CSVReader;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.deeplearning4j.examples.nlp.paragraphvectors.NoteEventsParagraphVectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Scanner;

/**
 * Created by Brian on 5/22/2016.
 */
public class CsvParser {
    private static final Logger log = LoggerFactory.getLogger(NoteEventsParagraphVectors.class);

    public static void main(String[] args) {

        try {
            ClassPathResource resource = new ClassPathResource("/NOTEEVENTS_5000.csv");
            File file = resource.getFile();


            CSVReader reader = new CSVReader(new FileReader(file), ',', '"');

            String[] headers = {"ROW_ID", "SUBJECT_ID", "HADM_ID", "CHARTDATE", "CHARTTIME", "STORETIME", "CATEGORY", "DESCRIPTION", "CGID", "ISERROR", "TEXT"};

            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                String hadmID = nextLine[2];
                String text = nextLine.length > 10 ? nextLine[10] : "";
                String presentIllness = presentIllness(text);
                System.out.println(" hadmID " + hadmID + " present Illness" + presentIllness);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static String presentIllness(String text) {
        String presentIllness = "";
        try {

            Scanner scanner = new Scanner(text).useDelimiter("\n");
            boolean onPresentIllness = false;

            while (scanner.hasNext()) {
                String s = scanner.next();
                if (s.toLowerCase().startsWith("history of present illness:")) {
                    onPresentIllness = true;
                }

                if (s.toLowerCase().startsWith("past medical history:") ||
                    s.toLowerCase().startsWith("Review of systems:".toLowerCase())) {
                   break;

                }
                if (onPresentIllness) {
                    presentIllness += s;
                }
            }

            scanner.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return presentIllness;
    }

}
