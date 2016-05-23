package org.deeplearning4j.examples.nlp.paragraphvectors;

import org.deeplearning4j.examples.nlp.paragraphvectors.tools.CsvLabelAwareIterator;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.paragraphvectors.ParagraphVectors;
import org.deeplearning4j.models.word2vec.wordstore.inmemory.InMemoryLookupCache;
import org.deeplearning4j.text.documentiterator.LabelsSource;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.sentenceiterator.labelaware.LabelAwareSentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.File;

/**
 */
public class NoteEventsParagraphVectors {

    private static final Logger log = LoggerFactory.getLogger(NoteEventsParagraphVectors.class);

    public static void main(String[] args) throws Exception {

        ClassPathResource resource = new ClassPathResource("/NOTEEVENTS_tiny.csv");
        File file = resource.getFile();
        CsvLabelAwareIterator iter = new CsvLabelAwareIterator(file);
        InMemoryLookupCache cache = new InMemoryLookupCache();

        TokenizerFactory t = new DefaultTokenizerFactory();
        t.setTokenPreProcessor(new CommonPreprocessor());
        // LabelsSource source = new LabelsSource("DOC_");
        ParagraphVectors vec = new ParagraphVectors.Builder()
            .minWordFrequency(1)
            .iterations(3)
            .epochs(1)
            .layerSize(100)
            .learningRate(0.025)
            //   .labelsSource(source)
            .windowSize(5)
            .iterate(iter)
            .trainWordVectors(false)
            .vocabCache(cache)
            .tokenizerFactory(t)
            .sampling(0)
            .build();

        vec.fit();

        WordVectorSerializer.writeWordVectors(vec, "NoteEventParagraphVectors.txt");

        double similarity1 = vec.similarity("HADMID_139852", "HADMID_199586");
        log.info("similarity: " + similarity1);

    }
}
