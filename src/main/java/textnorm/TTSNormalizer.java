package textnorm;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Text normalizer for TTS.
 * Relies on unicode normalized input text, see TTSUnicodeNormalizer.
 */

public class TTSNormalizer {

    private Map<String, String> mRegexMap;
    // Possible replacement patterns - INT=group index, STR=string replacement
    private String INT = "INT";
    private String STR = "STR";
    private String STRINT = "STRINT";
    private String STRINTSTRINT = "STRINTSTRINT";
    private String INTSTRINT = "INTSTRINT";
    private String INTSTRINTINT = "INTSTRINTINT";
    private String INTINTSTRINT = "INTINTSTRINT";
    private String INTINTSTRINTINT = "INTINTSTRINTINT";

    /*
    public TTSNormalizer(Context context) {
        this.mContext = context;
        this.mRegexMap = readAbbreviations();
    }*/

    public TTSNormalizer() {

    }

    public String preNormalize(String text) {
        String normalized = text;
        String domain = ""; //we will need to determine this from "text" in real life!

        if (normalized.matches(".*\\d.*")) {
            normalized = replaceFromDict(normalized, NormalizationDictionaries.preHelpDict);
        }
        if (normalized.contains("-")) {
            normalized = replaceFromDict(normalized, NormalizationDictionaries.directionDict);
            normalized = replaceFromDict(normalized, NormalizationDictionaries.hyphenDict);
        }
        if (normalized.contains(".")) {
            normalized = replaceFromDict(normalized, NormalizationDictionaries.abbreviationDict);
        }
        if (normalized.contains("/")) {
            normalized = replaceFromDict(normalized, NormalizationDictionaries.denominatorDict);
        }
        normalized = replaceFromDict(normalized, NormalizationDictionaries.weightDict);
        if (normalized.matches(".*\\b([pnµmcsdkN]?m|ft)\\.?\\b.*")) {
            normalized = replaceFromDict(normalized, NormalizationDictionaries.getDistanceDict());
        }
        if (normalized.matches(".*(\\bha\\.?\\b).*|([pnµmcsdk]?m\\b\\.?)|([pnµmcsdk]?m[²2³3]).*")) {
            normalized = replaceFromDict(normalized, NormalizationDictionaries.getAreaDict());
        }
        if (normalized.matches(".*\\b[dcmµ]?[Ll]\\.?\\b.*")) {
            normalized = replaceFromDict(normalized, NormalizationDictionaries.getVolumeDict());
        }
        if (normalized.matches(".*\\b(klst|mín|m?s(ek)?)\\b.*")) {
            normalized = replaceFromDict(normalized, NormalizationDictionaries.getTimeDict());
        }
        if (normalized.matches(".*(\\W|^)((ma?\\.?)?[Kk]r\\.?-?|C(HF|AD|ZK)|(DK|SE|NO)K|EUR|GBP|I[NS]K|JPY|PTE|(AU|US)D|mlj[óa]\\.?)((\\W|$)|[$£¥])(.*)")) {
            normalized = replaceFromDict(normalized, NormalizationDictionaries.getCurrencyDict());
        }
        if (normalized.matches(".*\\b([kMGT]?(V|Hz|B|W|W\\.?(st|h)))\\.?\\b.*")) {
            normalized = replaceFromDict(normalized, NormalizationDictionaries.getElectronicDict());
        }
        if (normalized.matches(".*(%|\\b(stk|[Kk][Cc]al)\\.?\\b).*")) {
            normalized = replaceFromDict(normalized, NormalizationDictionaries.restDict);
        }
        if (normalized.matches(".*-.*")) {
            normalized = replaceHyphen(normalized, domain);
        }
        return normalized;
    }

    public String postNormalize(String[] tokens, String[] tags) {
        if (tokens.length != tags.length)
            return "";

        String token;
        String nextTag;
        String lastToken = "";
        StringBuilder sb = new StringBuilder();
        String links_pattern = NormalizationDictionaries.links.get(NormalizationDictionaries.LINK_PTRN_ALL);
        for (int i=0; i < tags.length - 1; i++) {
            token = tokens[i];
            nextTag = tags[i+1];
            if (token.matches(".*\\d.*")) {
                token = normalizeNumber(token, nextTag);
            }
            // add space between upper case letters, if they do not build known Acronyms like "RÚV"
            else if (token.matches(NumberHelper.LETTERS_PTRN)) {
                token = token.replaceAll(".", "$0 ").trim();
            }
            else if (token.length() > 1 && token.charAt(0) == token.charAt(1)) {
                token = token.replaceAll(".", "$0 ").trim();
            }
            else if (token.matches(links_pattern))
                token = normalizeURL(token, links_pattern);
            else if (token.matches(NormalizationDictionaries.NOT_LETTERS))
                token = normalizeSymbols(token);

            sb.append(token.trim()).append(" ");
            lastToken = tokens[i+1];
        }
        sb.append(lastToken); //what if this is a digit or something that needs normalizing?
        String result = sb.toString();
        return result.replaceAll("\\s+", " ");

    }

    private String replaceHyphen(String text, String domain) {
        String replacedText = text;
        boolean didReplace = false;
        String[] textArr = text.split(" ");
        for (int i = 2; i < textArr.length - 1; i++) {
            // pattern: "digit - digit"
            if (textArr[i].equals("-") && textArr[i-1].matches("\\d+\\.?(\\d+)?") && textArr[i+1].matches("\\d+\\.?(\\d+)?")) {
                if (domain.equals("sport"))
                    textArr[i] = "";
                else
                    textArr[i] = "til";
                didReplace = true;
            }
        }
        if (didReplace) {
            StringBuilder sb = new StringBuilder();
            for (String s : textArr) {
                sb.append(s);
                sb.append(" ");
            }
            replacedText = sb.toString().trim();
        }
        return replacedText;
    }

    private String replaceFromDict(String text, Map<String, String> dict) {
        for (String regex : dict.keySet()) {
            Pattern pattern = Pattern.compile(regex);
            text = replacePattern(text, pattern, getExpression(dict.get(regex)));
        }
        return text;
    }

    // Replace a given regex with a
    private String replacePattern(String text, Pattern regex, Function<Matcher, String> converter) {
        int lastIndex = 0;
        StringBuilder normalized = new StringBuilder();
        Matcher matcher = regex.matcher(text);

            while (matcher.find()) {
                System.out.println(text);
                System.out.println(regex.toString());
                normalized.append(text, lastIndex, matcher.start())
                        .append(converter.apply(matcher));
                lastIndex = matcher.end();
            }

        if (lastIndex < text.length()) {
            normalized.append(text, lastIndex, text.length());
        }
        return normalized.toString();
    }

    // Build the lambda expression for the replacement pattern.
    // The replacement pattern consists of group indices and replacement strings, indicating
    // how the normalization for a given pattern should be performed.
    // Example:
    // replacement: "1xsome-replacementx2
    // lambda expression: match -> match.group(1) + "some-replacement" + match.group(2)
    private Function<Matcher, String> getExpression(String replacement) {
        String[] elements = replacement.split("x");
        // map of group indices, map index indicating where in the lambda expression the group index should be
        Map<Integer,Integer> groupMap = new HashMap<>();
        // map of string replacements, map index indicating where in the lambda expression the group index should be
        Map<Integer,String> replacementMap = new HashMap<>();
        StringBuilder replacementPattern = new StringBuilder();
        // extract group indices and replacement strings and
        // build the replacement pattern
        for (int i = 0; i < elements.length; i++) {
            if (elements[i].trim().matches("\\d+")) {
                groupMap.put(i, Integer.parseInt(elements[i].trim()));
                replacementPattern.append(INT);
            }
            else {
                replacementMap.put(i, elements[i]);
                replacementPattern.append(STR);
            }
        }
        if (replacementPattern.toString().equals(INTSTRINT))
            return match -> match.group(groupMap.get(0)) + replacementMap.get(1) + match.group(groupMap.get(2));
        if (replacementPattern.toString().equals(STRINT))
            return match -> replacementMap.get(0) + match.group(groupMap.get(1));
        if (replacementPattern.toString().equals(INTSTRINTINT))
            return match -> match.group(groupMap.get(0)) + replacementMap.get(1)
                    + match.group(groupMap.get(2)) + match.group(groupMap.get(3));
        if (replacementPattern.toString().equals(INTINTSTRINT))
            return match -> match.group(groupMap.get(0)) + match.group(groupMap.get(1)) + replacementMap.get(2)
                    + match.group(groupMap.get(3));
        if (replacementPattern.toString().equals(INTINTSTRINTINT))
            return match -> match.group(groupMap.get(0)) + match.group(groupMap.get(1)) + replacementMap.get(2)
                    + match.group(groupMap.get(3)) + match.group(groupMap.get(4));
        else
            return match -> replacement;
    }

    private String normalizeNumber(String numberToken, String nextTag) {
        String normalized = numberToken;
        if (numberToken.matches(NumberHelper.ORDINAL_THOUSAND_PTRN)) {
            Map<String, Map<String, String>> ordinalThousandDict = makeDict(numberToken, NumberHelper.INT_COLS_THOUSAND); // should look like: {token: {thousands: "", hundreds: "", dozens: "", ones: ""}}
            List<OrdinalTuple> mergedTupleList = Stream.of(OrdinalOnesTuples.getTuples(),OrdinalThousandTuples.getTuples(),
                    CardinalThousandTuples.getTuples())
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
            normalized = fillDict(numberToken, nextTag, mergedTupleList, ordinalThousandDict, NumberHelper.INT_COLS_THOUSAND);
        }
        else if (numberToken.matches(NumberHelper.CARDINAL_THOUSAND_PTRN)) {
            Map<String, Map<String, String>> cardinalThousandDict = makeDict(numberToken, NumberHelper.INT_COLS_THOUSAND); // should look like: {token: {thousands: "", hundreds: "", dozens: "", ones: ""}}
            List<OrdinalTuple> mergedTupleList = Stream.of(CardinalOnesTuples.getTuples(), CardinalThousandTuples.getTuples())
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
            normalized = fillDict(numberToken, nextTag, mergedTupleList, cardinalThousandDict, NumberHelper.INT_COLS_THOUSAND);
        }
        else if (numberToken.matches(NumberHelper.CARDINAL_MILLION_PTRN)) {
            Map<String, Map<String, String>> cardinalMillionDict = makeDict(numberToken, NumberHelper.INT_COLS_MILLION); // should look like: {token: {thousands: "", hundreds: "", dozens: "", ones: ""}}
            List<OrdinalTuple> mergedTupleList = Stream.of(CardinalThousandTuples.getTuples(), CardinalMillionTuples.getTuples())
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
            normalized = fillDict(numberToken, nextTag, mergedTupleList, cardinalMillionDict, NumberHelper.INT_COLS_MILLION);
        }
        else if (numberToken.matches(NumberHelper.DECIMAL_THOUSAND_PTRN)) {
            Map<String, Map<String, String>> decimalDict = makeDict(numberToken, NumberHelper.DECIMAL_COLS_THOUSAND); // should look like: {token: {"first_ten", "first_one","between_teams","second_ten", "second_one"}}

            List<OrdinalTuple> mergedCardinalTupleList = Stream.of(CardinalOnesTuples.getTuples(), CardinalThousandTuples.getTuples())
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
            List<OrdinalTuple> mergedTupleList = Stream.of(mergedCardinalTupleList, DecimalThousandTuples.getTuples())
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
            normalized = fillDict(numberToken, nextTag, mergedTupleList, decimalDict, NumberHelper.DECIMAL_COLS_THOUSAND);
        }
        else if (numberToken.matches(NumberHelper.TIME_PTRN)) {
            Map<String, Map<String, String>> timeDict = makeDict(numberToken, NumberHelper.TIME_SPORT_COLS); // should look like: {token: {"first_ten", "first_one","between_teams","second_ten", "second_one"}}
            normalized = fillDict(numberToken, nextTag, TimeTuples.getTuples(), timeDict, NumberHelper.TIME_SPORT_COLS);
        }
        else if (numberToken.matches(NumberHelper.FRACTION_PTRN)) {
            // if domain == "other" - do other things, below is the handling for sport results:
            Map<String, Map<String, String>> sportsDict = makeDict(numberToken, NumberHelper.TIME_SPORT_COLS); // should look like: {token: {"first_ten", "first_one","between_teams","second_ten", "second_one"}}
            normalized = fillDict(numberToken, nextTag, SportTuples.getTuples(), sportsDict, NumberHelper.TIME_SPORT_COLS);
        }
        else if (numberToken.matches("^0\\d\\.$")) {
            normalized = normalizeDigitOrdinal(numberToken);
        }
        else {
            normalized = normalizeDigits(numberToken);
        }
        return normalized;
    }

    private String normalizeURL(String token, String pattern) {
        int ind = token.indexOf('.');
        String prefix = token.substring(0, ind);
        String suffix = token.substring(ind + 1);
        // how can we choose which words to keep as words and which to separate?
        prefix = prefix.replaceAll(".", "$0 ").trim();

        for (String symbol : NumberHelper.WLINK_NUMBERS.keySet()) {
            prefix = prefix.replaceAll(symbol, NumberHelper.WLINK_NUMBERS.get(symbol));
        }
        if (suffix.indexOf('/') > 0) {
            String postSuffix = suffix.substring(suffix.indexOf('/'));
            postSuffix = postSuffix.replaceAll(".", "$0 ").trim();
            for (String symbol : NumberHelper.WLINK_NUMBERS.keySet()) {
                postSuffix = postSuffix.replaceAll(symbol, NumberHelper.WLINK_NUMBERS.get(symbol));
            }
            suffix = suffix.substring(0, suffix.indexOf('/')) + " " + postSuffix;
        }
        return prefix + " punktur " + suffix;
    }

    private String normalizeSymbols(String token) {
        for (String symbol : NumberHelper.DIGIT_NUMBERS.keySet()) {
            token = token.replaceAll(symbol, NumberHelper.DIGIT_NUMBERS.get(symbol));
        }
        return token;
    }

    private String normalizeDigitOrdinal(String token) {
        for (String digit : NumberHelper.DIGITS_ORD.keySet())
            token = token.replaceAll("^0" + digit + "\\.$", "núll " + NumberHelper.DIGITS_ORD.get(digit));
        return token;
    }
    private String normalizeDigits(String token) {
        token = token.replaceAll(" ", "<sil> ");
        for (String digit : NumberHelper.DIGIT_NUMBERS.keySet()) {
            token = token.replaceAll(digit, NumberHelper.DIGIT_NUMBERS.get(digit));
        }
        return token;
    }

    private Map<String, Map<String, String>> makeDict(String token, String[] columns) {
        Map<String, Map<String, String>> valueDict = new HashMap<>();
        Map<String, String> innerMap = new HashMap<>();
        for (String s : columns)
            innerMap.put(s, "");
        valueDict.put(token, innerMap);
        return valueDict;
    }

    private String fillDict(String token, String tag, List<OrdinalTuple> tuples, Map<String, Map<String, String>> typeDict, String[] columns) {
        String result = "";

        for (int i=0; i < tuples.size(); i++) {
            if (token.matches(".*" + tuples.get(i).getNumberPattern() + ".*") && tag.matches(".*" + tuples.get(i).getRule())) {
                if (typeDict.containsKey(token)) {
                    if (typeDict.get(token).containsKey(tuples.get(i).getCategorie())) {
                        Map<String, String> tmp = typeDict.get(token);
                        tmp.put(tuples.get(i).getCategorie(), tuples.get(i).getExpansion());
                        typeDict.put(token, tmp); // not really necessary, since the previous assignment updates the map in typeDict, but this is more clear
                    }
                }
            }
        }
        for (String s : columns)
            result += typeDict.get(token).get(s);

        return result;
    }

}
