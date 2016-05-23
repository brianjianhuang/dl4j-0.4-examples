package org.deeplearning4j.examples.nlp.paragraphvectors.tools;

import au.com.bytecode.opencsv.CSVReader;
import lombok.NonNull;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentencePreProcessor;
import org.deeplearning4j.text.sentenceiterator.labelaware.LabelAwareSentenceIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class CsvSentenceterator implements SentenceIterator {

    private static final Logger log = LoggerFactory.getLogger(CsvSentenceterator.class);
    private String cachedLabelLine;

    private boolean finished = false;
    private CSVReader csvReader;

    public CsvSentenceterator(@NonNull File file) throws IOException {
        csvReader = new CSVReader(new FileReader(file), ',', '"', 1);

    }

    public static void main(String[] args) {

        try {
            ClassPathResource resource = new ClassPathResource("/NOTEEVENTS_5000.csv");
            File file = resource.getFile();

            CsvSentenceterator csvSentenceterator = new CsvSentenceterator(file);


            FileWriter fileWriter = new FileWriter( new File("presentIllness.text"));


            while (csvSentenceterator.hasNext()) {
                fileWriter.write(csvSentenceterator.nextSentence() + "\n");

            }
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    @Override
    public String currentLabel() {
        System.out.println(" getting current label " + currentLabel);
        return currentLabel;

    }

    @Override
    public List<String> currentLabels() {
        List<String> a = new ArrayList<>();
        a.add(currentLabel);
        return a;
    }
*/

    @Override
    public String nextSentence() {
        if (!this.hasNext()) {
            throw new NoSuchElementException("No more lines");
        } else {
            String currentLine = this.cachedLabelLine;
            this.cachedLabelLine = null;
            System.out.println(" getting current lines " + currentLine);
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


                String text = ioe.length > 10 ? ioe[10] : "";
                this.cachedLabelLine = presentIllness(text);
                System.out.println(" present Illness: " + cachedLabelLine);

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
}
