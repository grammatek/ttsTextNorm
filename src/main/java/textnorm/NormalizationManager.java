package textnorm;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class NormalizationManager {


   TTSUnicodeNormalizer mUnicodeNormalizer;
   Tokenizer mTokenizer;
   TTSNormalizer mTTSNormalizer;

   public NormalizationManager() {
       mUnicodeNormalizer = new TTSUnicodeNormalizer();
       mTokenizer = new Tokenizer();
       mTTSNormalizer = new TTSNormalizer();
   }

    public String process(String text) {
       Instant start = Instant.now();
        String cleaned = mUnicodeNormalizer.normalize_encoding(text);
        List<String> tokenized = mTokenizer.detectSentences(cleaned);
        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toSeconds();
        System.out.println("unicode normalizing and tokenizing: " + timeElapsed + " seconds");
        StringBuilder sb = new StringBuilder();
       String preNormalized = "";
       List<String> normalized = new ArrayList<>();
       System.out.println("starting tagging and normalizing ...");
       int counter = 0;
       start = Instant.now();
       for (String sentence : tokenized) {
           if (counter % 100 == 0) {
               System.out.println("processing sentence no. " + counter + " ...");
           }
            preNormalized = mTTSNormalizer.preNormalize(sentence);
            String[] tags = tagText(preNormalized);
            normalized.add(mTTSNormalizer.postNormalize(preNormalized.split(" "), tags));
            counter++;
        }

        for (String sentence : normalized) {
            sb.append(" ");
            sb.append(sentence);
        }
        finish = Instant.now();
        timeElapsed = Duration.between(start, finish).toSeconds();
        System.out.println("Tagging and normalizing: " + timeElapsed + " seconds");
        return sb.toString().trim();
    }

    private String[] tagText(String text) {
        String[] tags = {};
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("is-pos-maxent.bin");
            POSModel posModel = new POSModel(is);
            POSTaggerME posTagger = new POSTaggerME(posModel);
            //System.out.println("Tagging: " + text + " ...");
            // Tagger tagging the tokens
            String[] tokens = text.split(" ");
            tags = posTagger.tag(tokens);
            // Getting the probabilities of the tags given to the tokens to inspect
            //double probs[] = posTagger.probs();
            //System.out.println("Token\t:\tTag\t:\tProbability\n---------------------------------------------");
            //for(int i=0;i<tokens.length;i++){
            //    System.out.println(tokens[i]+"\t:\t"+tags[i]+"\t:\t"+probs[i]);
            //}

        } catch (IOException e) {
            e.printStackTrace();
        }
        return tags;
    }
}
