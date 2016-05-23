package org.deeplearning4j.examples.nlp.word2vec;

import org.canova.api.util.ClassPathResource;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.sentenceiterator.UimaSentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.deeplearning4j.ui.UiServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

/**
 * Created by agibsonccc on 10/9/14.
 */
public class Word2VecRawTextExample {

    private static Logger log = LoggerFactory.getLogger(Word2VecRawTextExample.class);

    public static void main(String[] args) throws Exception {


       // String filePath = "C:\\Users\\Brian\\Downloads\\NOTEEVENTS.csv";
        String filePath = new ClassPathResource("raw_sentences.txt").getFile().getAbsolutePath();

        log.info("Load & Vectorize Sentences....");
        // Strip white space before and after for each line
        //SentenceIterator iter = new BasicLineIterator(filePath);

        SentenceIterator iter = UimaSentenceIterator.createWithPath(filePath);
        // Split on white spaces in the line to get words
        TokenizerFactory t = new DefaultTokenizerFactory();
        t.setTokenPreProcessor(new CommonPreprocessor());

        log.info("Building model....");
        Word2Vec vec = new Word2Vec.Builder()
            .batchSize(100)
            .useAdaGrad(false)
            .learningRate(0.025)
            .minLearningRate(1e-3)
            .negativeSample(10)
            .minWordFrequency(3)
                .iterations(3)
                .layerSize(150)
                .seed(42)
                .windowSize(5)
                .iterate(iter)
                .tokenizerFactory(t)
                .build();

        log.info("Fitting Word2Vec model....");
        vec.fit();

        log.info("Writing word vectors to text file....");

        // Write word vectors
        WordVectorSerializer.writeWordVectors(vec, "word2vecNoteEvnetsOutput.txt");

        log.info("Closest Words:");
        Collection<String> lst = vec.wordsNearest("day", 10);
        System.out.println(lst);
        UiServer server = UiServer.getInstance();
        System.out.println("Started on port " + server.getPort());
    }
}
