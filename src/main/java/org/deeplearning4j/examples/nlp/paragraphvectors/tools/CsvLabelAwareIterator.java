package org.deeplearning4j.examples.nlp.paragraphvectors.tools;

import au.com.bytecode.opencsv.CSVReader;
import lombok.NonNull;
import org.deeplearning4j.examples.nlp.paragraphvectors.NoteEventsParagraphVectors;
import org.deeplearning4j.text.sentenceiterator.BaseSentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentencePreProcessor;
import org.deeplearning4j.text.sentenceiterator.labelaware.LabelAwareSentenceIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

//Use  a csv parser parese a multiline csv file
public class CsvLabelAwareIterator implements LabelAwareSentenceIterator {

    private static final Logger log = LoggerFactory.getLogger(CsvLabelAwareIterator.class);
    private String cachedLabelLine;
    private String currentLabel;
    private boolean finished = false;
    private CSVReader csvReader;

    public CsvLabelAwareIterator(@NonNull File file) throws IOException {
        csvReader = new CSVReader(new FileReader(file), ',', '"', 1);
    }
    @Override
    public String currentLabel() {
        // System.out.println(" getting current label " + currentLabel);
        return currentLabel;
    }

    @Override
    public List<String> currentLabels() {
        List<String> a = new ArrayList<>();
        a.add(currentLabel);
        return a;
    }

    @Override
    public String nextSentence() {
        if (!this.hasNext()) {
            throw new NoSuchElementException("No more lines");
        } else {
            String currentLine = this.cachedLabelLine;
            this.cachedLabelLine = null;
            // System.out.println(" getting current lines " + currentLine);
            return currentLine;
        }
    }

    @Override
    public boolean hasNext() {
        if (this.cachedLabelLine != null) {
            return true;
        } else if (this.finished) {
            return false;
        } else {
            try {
                String[] ioe;
                do {
                    ioe = csvReader.readNext();
                    if (ioe == null) {
                        this.finished = true;
                        return false;
                    }
                } while (!this.isValidLine(ioe));

                //the HADMID
                this.currentLabel = "HADMID_" + ioe[2];
                //the TEXT field
                String text = ioe.length > 10 ? ioe[10] : "";
                //extract the present illness section
                this.cachedLabelLine = presentIllness(text);
                //System.out.println(" hadmID " + currentLabel + " present Illness: " + cachedLabelLine);

                return true;
            } catch (IOException var2) {
                this.finish();
                throw new IllegalStateException(var2);
            }
        }
    }

    protected boolean isValidLine(String[] line) {
        return true;
    }

    @Override
    public void reset() {

    }

    public void finish() {
        try {
            if (this.csvReader != null) {
                this.csvReader.close();
            }

        } catch (Exception var2) {
            var2.printStackTrace();
        }

    }

    @Override
    public SentencePreProcessor getPreProcessor() {
        return null;
    }

    @Override
    public void setPreProcessor(SentencePreProcessor sentencePreProcessor) {

    }

    //the present Illness section into a string
    //
    String presentIllness(String text) {
        String presentIllness = "";
        try {

            Scanner scanner = new Scanner(text).useDelimiter("\n");
            boolean onPresentIllness = false;

            while (scanner.hasNext()) {
                String s = scanner.next();
                if (s.toLowerCase().startsWith("history of present illness")) {
                    onPresentIllness = true;
                    continue;
                }

                if (s.toLowerCase().startsWith("past medical history") ||
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

    //just a test
    public static void main(String[] args) {

        try {
            ClassPathResource resource = new ClassPathResource("/NOTEEVENTS_5000.csv");
            File file = resource.getFile();

            CsvLabelAwareIterator csvLabelAwareIterator = new CsvLabelAwareIterator(file);
            while (csvLabelAwareIterator.hasNext()) {
                System.out.println(" label : " + csvLabelAwareIterator.currentLabel());
                System.out.println(" sentence : " + csvLabelAwareIterator.nextSentence());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
