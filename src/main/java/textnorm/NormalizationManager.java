package textnorm;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * The NormalizationManager controls the normalization process from raw input text to normalized text. It contains:
 *      - a pre-normalization step to clean unicode, i.e. reduce the number of characters by deleting irrelevant
 *        characters and reducing similar characters to one (e.g. dash and hyphen variations to hypen-minus (\u202d or 45 decimal))
 *      - a tokenizing step
 *      - the core normalization step composed of pre-normalization, pos-tagging and post-normalization
 */

public class NormalizationManager {
    private final static Logger LOGGER = Logger.getLogger(NormalizationManager.class.getName());
    private final static String POS_MODEL = "is-pos-maxent.bin";
    TTSUnicodeNormalizer mUnicodeNormalizer;
    Tokenizer mTokenizer;
    TTSNormalizer mTTSNormalizer;

    public NormalizationManager() {
        mUnicodeNormalizer = new TTSUnicodeNormalizer();
        mTokenizer = new Tokenizer();
        mTTSNormalizer = new TTSNormalizer();
    }

    /**
     * Processes the input text according to the defined steps: unicode cleaning, tokenizing, normalizing
     * @param text
     * @return
     */
    public String process(String text) {

        String cleaned = mUnicodeNormalizer.normalizeEncoding(text);
        List<String> tokenized = mTokenizer.detectSentences(cleaned);
        List<String> normalizedSentences = normalize(tokenized);

        return list2string(normalizedSentences);
    }

    // pre-normalization, tagging and final normalization of the sentences in 'tokenized'
    private List<String> normalize(List<String> tokenized) {
        String preNormalized;
        List<String> normalized = new ArrayList<>();
        int counter = 0;
        for (String sentence : tokenized) {
            if (counter % 100 == 0) {
                LOGGER.info("processing sentence no. " + counter + " ...");
            }
            preNormalized = mTTSNormalizer.preNormalize(sentence);
            String[] tags = tagText(preNormalized);
            // preNormalized is tokenized as string, so we know splitting on whitespace will give
            // us the correct tokens according to the tokenizer
            normalized.add(mTTSNormalizer.postNormalize(preNormalized.split(" "), tags));
            counter++;
        }
        return normalized;
    }

    private String list2string(List<String> normalizedSentences) {
        StringBuilder sb = new StringBuilder();
        for (String sentence : normalizedSentences) {
            sb.append(" ");
            sb.append(sentence);
        }
        return sb.toString().trim();
    }

    private String[] tagText(String text) {
        String[] tags = {};
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream(POS_MODEL);
            POSModel posModel = new POSModel(is);
            POSTaggerME posTagger = new POSTaggerME(posModel);
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
