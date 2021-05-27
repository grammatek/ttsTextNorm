package textnorm;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;

import java.io.IOException;
import java.io.InputStream;
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
        String cleaned = mUnicodeNormalizer.normalize_encoding(text);
        List<String> tokenized = mTokenizer.detectSentences(cleaned);
        StringBuilder sb = new StringBuilder();
        for (String sentence : tokenized) {
            sb.append(" ");
            sb.append(mTTSNormalizer.preNormalize(sentence));
        }
        String normalized = sb.toString().trim();
        String[] tags = tagText(normalized);
        normalized = mTTSNormalizer.postNormalize(normalized.split(" "), tags);

        return normalized;
    }

    private String[] tagText(String text) {
        String[] tags = {};
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("is-pos-maxent.bin");
            POSModel posModel = new POSModel(is);
            POSTaggerME posTagger = new POSTaggerME(posModel);

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
