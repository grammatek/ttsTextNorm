package textnorm;

import org.apache.commons.collections4.OrderedMap;
import org.apache.commons.collections4.map.LinkedMap;
import org.apache.commons.collections4.map.ListOrderedMap;

import java.util.HashMap;
import java.util.Map;

/**
 * Normalization dictionaries for abbreviations, digits and other non-standard-words.
 * The final dictionaries will be stored in external text files, but for preliminary unit-testing
 * having a selection of the patterns here helps.
 */

public class NormalizationDictionaries {

    public static final String ACC = "ACC"; // always acc
    public static final String DAT = "DAT"; // always dat
    public static final String GEN = "GEN"; // always gen
    public static final String ACC_DAT = "ACC_DAT"; // acc or dat
    public static final String ACC_GEN = "ACC_GEN"; // ACC + GEN
    public static final String ACC_DAT_COMB = "ACC_DAT_COMB"; // ACC + DAT + ACC_DAT
    public static final String ACC_DAT_GEN_COMB = "ACC_DAT_GEN_COMB"; // ACC + DAT + ACC_DAT + GEN
    public static final String AMOUNT = "AMOUNT";
    public static final String LINK_PTRN_EXTERNAL = "LINK_PTRN_EXTERNAL";
    public static final String LINK_PTRN_INTERNAL = "LINK_PTRN_INTERNAL";
    public static final String LINK_PTRN_MAIL = "LINK_PTRN_MAIL";
    public static final String LINK_PTRN_HASHTAG = "LINK_PTRN_HASHTAG";
    public static final String LINK_PTRN_ALL = "LINK_PTRN_ALL";


    //regex bits:
    public static final String MATCH_ANY = ".*";
    public static final String BOS = "([^\\wÁÉÍÓÚÝÐÞÆÖáéíóúýðþæö]|^)"; // non-word char OR beginning of string
    public static final String EOS = "([^\\wÁÉÍÓÚÝÐÞÆÖáéíóúýðþæö]|$)"; // non-word char OR end of string, note that \\w and \\W are ascii-based! //TODO: check with Helga
    public static final String DOT = "\\."; // escaped dot
    public static final String DOT_ONE_NONE = "\\.?"; // escaped dot
    public static final String LETTERS = "[A-ZÁÉÍÓÚÝÐÞÆÖa-záéíóúýðþæö]";
    public static final String LETTER_OR_DIGIT = "[A-ZÁÉÍÓÚÝÐÞÆÖa-záéíóúýðþæö\\d]";
    public static final String NOT_LETTERS = "[^A-ZÁÉÍÓÚÝÐÞÆÖa-záéíóúýðþæö]";
    public static final String ALL_MONTHS = "jan(úar)?|feb(rúar)?|mars?|apr(íl)?|maí|jú[nl]í?|ág(úst)?|sep(t(ember)?)?|okt(óber)?|nóv(ember)?|des(ember)?";
    public static final String THE_CLOCK = "(núll|eitt|tvö|þrjú|fjögur|fimm|sex|sjö|átta|níu|tíu|ellefu|tólf" +
            "|((þret|fjór|fimm|sex)tán)|((sau|á|ní)tján)|tuttugu( og (eitt|tvö|þrjú|fjögur))?)";
    public static final String MEASURE_PREFIX_DIGITS = "(\\d{1,2}" + DOT + ")?(\\d{3}" + DOT + "?)*\\d+(,\\d+)?";
    public static final String MEASURE_PREFIX_WORDS = "([Hh]undr[au]ð|HUNDR[AU]Ð|[Þþ]úsund|ÞÚSUND|[Mm]illjón(ir)?|MILLJÓN(IR)?) ";

    // any number, (large, small, with or without thousand separators, with or without a decimal point) that does NOT end with a "1"
    public static final String NUMBER_EOS_NOT_1 = "(\\d{1,2}\\.)?(\\d{3}\\.?)*(\\d*[02-9]|\\d,\\d*[02-9]))";
    // any number, (large, small, with or without thousand separators, with or without a decimal point) that DOES end with a "1"
    // TODO: ask Helga: this does not quite match, since a larger number than one digit plus decimal point does not match
    // e.g. 21,1 does not match, whereas 1,1 matches
    public static final String NUMBER_EOS_1 = "(\\d{1,2}\\.)?(\\d{3}\\.?)*(\\d*1|\\d,\\d*1)";
    // any number (large, small, with or without thousand separators, with or without a decimal point)
    public static final String NUMBER_ANY = "(((\\d{1,2}\\.)?(\\d{3}\\.?)*|\\d+)(,\\d+)?)?";

    private NormalizationDictionaries() {}

    public static Map<String, String> prepositions = new HashMap<String, String>() {{
        put(ACC, "um(fram|hverfis)|um|gegnum|kringum|við|í|á");
        put(DAT, "frá|a[ðf]|ásamt|gagnvart|gegnt?|handa|hjá|með(fram)?|móti?|undan|nálægt");
        put(GEN, "til|auk|án|handan|innan|meðal|megin|milli|ofan|sakir|sökum|utan|vegna");
        put(ACC_DAT, "eftir|fyrir|með|undir|við|yfir");
        put(ACC_GEN, "um(fram|hverfis)|um|gegnum|kringum|við|í|á|til|auk|án|handan|innan|meðal|megin|milli|ofan|sakir|sökum|utan|vegna");
        put(ACC_DAT_COMB, "um(fram|hverfis)|um|gegnum|kringum|við|í|á|frá|a[ðf]|ásamt|gagnvart|gegnt?|handa|hjá|með(fram)?|móti?|undan|nálægt|eftir|fyrir|með|undir|við|yfir");
        put(ACC_DAT_GEN_COMB, "um(fram|hverfis)|um|gegnum|kringum|við|í|á|frá|a[ðf]|ásamt|gagnvart|gegnt?" +
                "|handa|hjá|með(fram)?|móti?|undan|nálægt|eftir|fyrir|með|undir|við|yfir|til|auk|án|handan|innan|meðal|megin|milli|ofan|sakir|sökum|utan|vegna");
    }};

    //The link patterns handle external patterns like https://mbl.is/innlent, internal patterns like https://localholst:8888,
    //mail patterns like name@address.com, twitter handles like @handle and hashtags, e.g. #thisrules2021
    public static Map<String, String> patternSelection = new HashMap<>();
    static {
        patternSelection.put(AMOUNT, "(hundr[au]ð|þúsund|milljón(ir)?)");
        patternSelection.put(LINK_PTRN_EXTERNAL, "((https?:\\/\\/)?(www\\.)?([A-ZÁÐÉÍÓÚÝÞÆÖa-záðéíóúýþæö\\d\\-_\\.\\/]+)?\\.[A-ZÁÐÉÍÓÚÝÞÆÖa-záðéíóúýþæö\\d\\-_\\.\\/]+)");
        patternSelection.put(LINK_PTRN_INTERNAL, "((file|(https?:\\/\\/)?localhost):[A-ZÁÐÉÍÓÚÝÞÆÖa-záðéíóúýþæö\\d_\\?\\/\\.=\\-\\&\\%\\#]+)");
        patternSelection.put(LINK_PTRN_MAIL, "([A-ZÁÐÉÍÓÚÝÞÆÖa-záðéíóúýþæö\\d\\-_\\.]*@[A-ZÁÐÉÍÓÚÝÞÆÖa-záðéíóúýþæö\\d\\-_\\.]+(\\.[A-Za-z])?)");
        patternSelection.put(LINK_PTRN_HASHTAG, "(# ?[A-ZÁÐÉÍÓÚÝÞÆÖa-záðéíóúýþæö\\d\\-_]+)");
    }
    public static Map<String, String> links = new HashMap<>();
    static {
        links.put(LINK_PTRN_ALL, "^(" + patternSelection.get(LINK_PTRN_EXTERNAL) + "|" + patternSelection.get(LINK_PTRN_INTERNAL)
                + "|" + patternSelection.get(LINK_PTRN_MAIL) + "|" + patternSelection.get(LINK_PTRN_HASHTAG) + ")$");
    }

    public static Map<String, String> preHelpDict = new HashMap<String, String>() {{
        put("(\\W|^)(?i)2ja(\\W|$)", "1xtveggjax2");
        put("(\\W|^)(?i)3ja(\\W|$)", "1xþriggjax2");
        // original has no 'u', why?
        put("(\\W|^)(?i)4ð(a|i|u)(\\W|$)", "1xfjórðx2x3");
        put("(\\W|^)(?i)5t(a|i|u)(\\W|$)", "1xfimmtx2x3");
        put("(\\W|^)(?i)6t(a|i|u)(\\W|$)", "1xsjöttx2x3");
        put("(\\W|^)(?i)7d(a|i|u)(\\W|$)", "1xsjöundx2x3");
        put("(\\W|^)(?i)8d(a|i|u)(\\W|$)", "1xáttundx2x3");
        put("(\\W|^)(?i)9d(a|i|u)(\\W|$)", "1xníundx2x3");

        put("(?i)([a-záðéíóúýþæö]+)(\\d+)", "1x x2");
        put("(?i)(\\d+)([a-záðéíóúýþæö]+)", "1x x2");
        // the following two patterns are originally in the prehelpdict - extract because they
        // don't contain any digits. let's check the digits first.
        //put("(\\W|^)([A-ZÁÐÉÍÓÚÝÞÆÖ]+)(\\-[A-ZÁÐÉÍÓÚÝÞÆÖa-záðéíóúýþæö]+)(\\W|$)", "1x2x x3x4");
        //put("(\\W|^)([A-ZÁÐÉÍÓÚÝÞÆÖa-záðéíóúýþæö]+\\-)([A-ZÁÐÉÍÓÚÝÞÆÖ]+)(\\W|$)", "1x2x x3x4");
        // what are these? degrees and percent with letters?
        //put("(?i)([\\da-záðéíóúýþæö]+)(°)", "1x x2");
        //put("(?i)([\\da-záðéíóúýþæö]+)(%)", "1x x2");
        // dates: why would we handle that here already?
        //put("(\\W|^)(0?[1-9]|[12]\\d|3[01])\\.(0?[1-9]|1[012])\\.(\\d{3,4})(\\W|$)", " x1x2x. x3x. x4x5");
        //put("(\\W|^)(0?[1-9]|[12]\\d|3[01])\\.(0?[1-9]|1[012])\\.(\\W|$)", " x1x2x. x3x.x4");
        // what does that stand for?
        //put("(\\d{3})( )(\\d{4})", " x1x-x3x");
    }};

    public static Map<String, String> hyphenDict = new HashMap<String, String>() {{
        put("(\\W|^)([A-ZÁÐÉÍÓÚÝÞÆÖ]+)(\\-[A-ZÁÐÉÍÓÚÝÞÆÖa-záðéíóúýþæö]+)(\\W|$)", "1x2x x3x4");
        put("(\\W|^)([A-ZÁÐÉÍÓÚÝÞÆÖa-záðéíóúýþæö]+\\-)([A-ZÁÐÉÍÓÚÝÞÆÖ]+)(\\W|$)", "1x2x x3x4");
    }};

    public static final Map<String, String> abbreviationDict = new HashMap<>();
    static {
        abbreviationDict.put("(\\d+\\.) gr" + DOT + EOS, "1x greinx2");
        abbreviationDict.put("(\\d+\\.) mgr" + DOT + EOS, "1x málsgrein2");
        abbreviationDict.put(BOS + "[Ii]nnsk" + DOT + "(blm" + DOT + "|blaðamanns)" + EOS, "1xinnskot x2");
        abbreviationDict.put(BOS + "([Ii]nnsk(" + DOT + "|ot) )(blm" + DOT + ")" + EOS, "1x2xblaðamanns x5");
        abbreviationDict.put("([Ff]" + DOT + "[Kk]r" + DOT + "?)" + EOS, "fyrir Kristx2");
        abbreviationDict.put("([Ee]" + DOT + "[Kk]r" + DOT + "?)" + EOS, "eftir Kristx2");
        abbreviationDict.put(BOS + "([Cc]a|CA)" + DOT_ONE_NONE + EOS, "1xsirkax3");
        abbreviationDict.put("(\\d+" + DOT + ") [Ss]ek" + DOT_ONE_NONE + EOS, "1x sekúndax2");
        abbreviationDict.put("(\\d+" + DOT + ") [Mm]ín" + DOT_ONE_NONE + EOS, "1x mínútax2");

        abbreviationDict.put(BOS + "(\\d{1,2}\\/\\d{1,2} )frák" + DOT_ONE_NONE + EOS, "1x2x fráköstx3");
        abbreviationDict.put(BOS + "(\\d{1,2}\\/\\d{1,2} )stoðs" + DOT_ONE_NONE + EOS, "1x2x stoðsendingarx3");
        abbreviationDict.put(BOS + "([Nn]" + DOT_ONE_NONE + "k|N" + DOT_ONE_NONE + "K)" + DOT_ONE_NONE + EOS, "1xnæstkomandix3");
        abbreviationDict.put(BOS + "(?i)(atr" + DOT_ONE_NONE + ")" + EOS, "1x atriðix3");
        abbreviationDict.put(BOS + "(?i)(ath" + DOT_ONE_NONE + ")" + EOS, "1x athugiðx3");
        abbreviationDict.put(BOS + "(?i)(aths" + DOT_ONE_NONE + ")" + EOS, "1x athugasemdx3");
        abbreviationDict.put(BOS + "([Ff]" + DOT_ONE_NONE + "hl|F" + DOT + "HL)" + DOT_ONE_NONE + EOS, "1x fyrri hlutix3");
        abbreviationDict.put(BOS + "([Ss]" + DOT_ONE_NONE + "hl|S" + DOT + "HL)" + DOT_ONE_NONE + EOS, "1x síðari hlutix3");

        abbreviationDict.put("(?i)" + BOS + "(e" + DOT_ONE_NONE + "h" + DOT_ONE_NONE + "f" + DOT_ONE_NONE + ")" + EOS, "1xE H Fx3");
        abbreviationDict.put("(?i)" + BOS + "(o" + DOT_ONE_NONE + "h" + DOT_ONE_NONE + "f" + DOT_ONE_NONE + ")" + EOS, "1xO H Fx3");
        abbreviationDict.put("(?i)" + BOS + "(h" + DOT_ONE_NONE + "f" + DOT_ONE_NONE + ")" + EOS, "1xH Fx3");
        abbreviationDict.put("(?i)" + BOS + "(s" + DOT_ONE_NONE + "f" + DOT_ONE_NONE + ")" + EOS, "1xS Fx3");
        abbreviationDict.put("(?i)" + BOS + "(q" + DOT_ONE_NONE + "e" + DOT_ONE_NONE + "d" + DOT_ONE_NONE + ")" + EOS, "1xQ E Dx3");
        abbreviationDict.put(BOS + "([Pp]" + DOT_ONE_NONE + "s" + DOT_ONE_NONE + ")" + EOS, "1xP Sx3");

        abbreviationDict.put(BOS + "([Aa]" + DOT_ONE_NONE + "m" + DOT_ONE_NONE + "k|A" + DOT_ONE_NONE + "M" + DOT_ONE_NONE + "K)" + DOT_ONE_NONE + EOS, "1xað minnsta kostix3");
        abbreviationDict.put(BOS + "([Aa]" + DOT + "m" + DOT + "m" + DOT_ONE_NONE + ")" + EOS, "1xað mínu matix3");
        abbreviationDict.put(BOS + "([Aa]" + DOT + "n" + DOT + "l" + DOT_ONE_NONE + ")" + EOS, "1xað nokkru leytix3");
        abbreviationDict.put(BOS + "([Aa]lþm" + DOT_ONE_NONE + ")" + EOS, "1xalþingismaðurx3");
        abbreviationDict.put(BOS + "([Aa]lm|ALM)" + DOT_ONE_NONE + EOS, "1xalmenntx3");
        abbreviationDict.put(BOS + "(bls" + DOT_ONE_NONE + ") (" + NUMBER_ANY + "|" + MEASURE_PREFIX_WORDS +
                "( )?)" + EOS, "1xblaðsíða x3x12"); // realistic? million pages? 45,3 pages?
        abbreviationDict.put("(?i)" + BOS + "(B" + DOT + "S[Cc]" + DOT + "?)" + EOS, "1xB S Cx3");
        abbreviationDict.put("(?i)" + BOS + "(M" + DOT + "S[Cc]" + DOT + "?)" + EOS, "1xM S Cx3");

        abbreviationDict.put(BOS + "([Dd]r" + DOT_ONE_NONE + ")" + EOS, "1xdoktorx3");
        abbreviationDict.put("(" + BOS + "\\() ?(e" + DOT + ")" + EOS, "1xenskax4");
        abbreviationDict.put(BOS + "([Ee]" + DOT + "k|E" + DOT + "K)" + DOT_ONE_NONE + EOS, "1x einhvers konarx3");
        abbreviationDict.put(BOS + "([Ee]\\-s konar)" + DOT_ONE_NONE+ EOS, "1x einhvers konarx3");
        abbreviationDict.put(BOS + "([Ee]" + DOT + "t" + DOT + "v" + DOT_ONE_NONE + ")" + EOS, "1x ef til villx3");

        abbreviationDict.put(BOS + "([Ee]\\-ð)" + EOS, "1xeitthvaðx3");
        abbreviationDict.put(BOS + "([Ee]\\-ju)" + EOS, "1xeinhverjux3");
        abbreviationDict.put(BOS + "([Ee]\\-s)" + EOS, "1xeinhversx3");
        abbreviationDict.put(BOS + "([Ee]\\-r)" + EOS, "1xeinhversx3");
        abbreviationDict.put(BOS + "([Ee]\\-n)" + EOS, "1xeinhvernx3");
        abbreviationDict.put(BOS + "([Ee]\\-um)" + EOS, "1xeinhverjumx3");
        // do we have "fædd" somewhere or how do we know this should be "fæddur"?
        abbreviationDict.put(BOS + "(f" + DOT + ") (^((([012]?[1-9]|3[01])" + DOT + " ?)?(" + ALL_MONTHS + ") )\\d{2,4}$)" + EOS, "1xfæddur x3x4");
        abbreviationDict.put(BOS + "([Ff]él" + DOT_ONE_NONE + ")" + EOS, "1xfélagx3");
        abbreviationDict.put(BOS + "([Ff]rh|FRH)" + DOT_ONE_NONE + EOS, "1xframhaldx3");
        abbreviationDict.put(BOS + "([Ff]rt|FRT)" + DOT_ONE_NONE + EOS, "1xframtíðx3");
        abbreviationDict.put(BOS + "([Ff]" + DOT + "o" + DOT + "t" + DOT +")" + EOS, "1xfyrir okkar tímatalx3");
        abbreviationDict.put(BOS + "([Ff]" + DOT + "h" + DOT_ONE_NONE + ")" + EOS, "1xfyrir höndx3");
        abbreviationDict.put(BOS + "([Gg]" + DOT_ONE_NONE + "r" + DOT_ONE_NONE + "f|G" + DOT_ONE_NONE +"R" + DOT_ONE_NONE + "F)"
                + DOT_ONE_NONE + EOS, "1xgerum ráð fyrirx3");
        abbreviationDict.put(BOS + "([Gg]" + DOT_ONE_NONE + "m" + DOT_ONE_NONE + "g|G" + DOT_ONE_NONE +"M" + DOT_ONE_NONE + "G)"
                + DOT_ONE_NONE + EOS, "1xguð minn góðurx3");

        abbreviationDict.put(BOS + "([Hh]dl" + DOT_ONE_NONE + ")" + EOS, "1xhéraðsdómslögmaðurx3");
        abbreviationDict.put(BOS + "([Hh]rl" + DOT_ONE_NONE + ")" + EOS, "1xhæstarréttarlögmaðurx3");
        abbreviationDict.put(BOS + "([Hh]öf|HÖF)" + DOT_ONE_NONE + EOS, "1xhöfundurx3");
        abbreviationDict.put(BOS + "([Hh]v?k)" + EOS, "1xhvorugkynx3");
        abbreviationDict.put(BOS + "([Hh]r" + DOT_ONE_NONE + ")" + EOS, "1xherrax3");
        abbreviationDict.put(BOS + "([Hh]v" + DOT_ONE_NONE + ")" + EOS, "1xhæstvirturx3"); //case?
        abbreviationDict.put(BOS + "([Hh]" + DOT + "u" + DOT + "b|H" + DOT + "U" + DOT + "B)" + DOT_ONE_NONE + EOS, "1xhér um bilx3");

        abbreviationDict.put("(" + LETTERS + "+ )([Jj]r)" + DOT_ONE_NONE + EOS, "1xjuniorx3");

        abbreviationDict.put(BOS + "([Kk]k)" + EOS, "1xkarlkynx3");
        abbreviationDict.put(BOS + "([Kk]vk)" + EOS, "1xkvenkynx3");
        abbreviationDict.put(BOS + "([Kk]t" + DOT_ONE_NONE + ")(:| \\d{6}\\-?\\d{4})" + EOS, "1xkennitalax3");
        abbreviationDict.put(BOS + "([Kk]l ?(" + DOT + "|\\:)?)(\\s?\\d{2}([:" + DOT + "]\\d{2})?)", "1xklukkanx4");
        // how do we get this, ef number normalizing is done after abbreviation normalizing?
        abbreviationDict.put("(?i)" + BOS + "kl ?(" + DOT + "|\\:)? ?(" + THE_CLOCK + " )", "klukkanx2");

        abbreviationDict.put(BOS + "([Kk]höfn|[Kk]bh|KBH)" + EOS, "1xKaupmannahöfnx2"); // inflections?
        abbreviationDict.put(BOS + "([Ll]h" + DOT_ONE_NONE + "nt" + DOT_ONE_NONE + ")" + EOS, "1xlýsingarháttur nútíðarx3");
        abbreviationDict.put(BOS + "([Ll]h" + DOT_ONE_NONE + "þt" + DOT_ONE_NONE + ")" + EOS, "1xlýsingarháttur þátíðarx3");
        abbreviationDict.put(BOS + "([Ll]td|LTD)" + DOT_ONE_NONE + EOS, "1xlimitedx3");

        abbreviationDict.put(BOS + "([Mm]" + DOT + "a" + DOT + ")" + EOS, "1xmeðal annarsx3");
        abbreviationDict.put(BOS + "([Mm]" + DOT + "a" + DOT + "s|M" + DOT + "A" + DOT + "S)" + DOT_ONE_NONE + EOS, "1xmeira að segjax3");
        abbreviationDict.put(BOS + "([Mm]" + DOT + "a" + DOT + "o|M" + DOT + "A" + DOT + "O)" + DOT_ONE_NONE + EOS, "1xmeðal annarra orðax3");
        abbreviationDict.put("(" + MEASURE_PREFIX_DIGITS + "|" + MEASURE_PREFIX_WORDS + ")?([Mm]" + DOT + "y" + DOT + "s" + DOT_ONE_NONE + ")" + EOS, "1xmetra yfir sjávarmálix9"); // do we really need the millions here? million metres above n.n.?
        abbreviationDict.put(BOS + "([Mm]" + DOT_ONE_NONE + "v" + DOT_ONE_NONE + ")" + EOS, "1xmiðað viðx3");
        abbreviationDict.put(BOS + "([Mm]" + DOT_ONE_NONE + "t" + DOT_ONE_NONE + "t|M" + DOT_ONE_NONE + "T" + DOT_ONE_NONE + "T)"
                + DOT_ONE_NONE + EOS, "1xmeð tilliti tilx3");
        abbreviationDict.put(BOS +  "([Mm]" + DOT_ONE_NONE + "ö" + DOT_ONE_NONE + "o|M" + DOT_ONE_NONE + "Ö" + DOT_ONE_NONE + "O)"
                + DOT_ONE_NONE + EOS, "1xmeð öðrum orðumx3");
        abbreviationDict.put(BOS + "([Mm]fl|MFL)" + DOT_ONE_NONE + EOS, "1xmeistaraflokkurx3");
        abbreviationDict.put("(" + MEASURE_PREFIX_DIGITS + "|" + MEASURE_PREFIX_WORDS + ")?(m ?\\^ ?2)" + EOS, "1x fermetrarx9");
        abbreviationDict.put("(" + MEASURE_PREFIX_DIGITS + "|" + MEASURE_PREFIX_WORDS + ")?(m ?\\^ ?3)" + EOS, "1x rúmmetrarx9");

        abbreviationDict.put(BOS + "([Nn]úv" + DOT_ONE_NONE + ")" + EOS, "1xnúverandix3");
        abbreviationDict.put(BOS + "([Nn]" + DOT_ONE_NONE + "t" + DOT_ONE_NONE + "t|N" + DOT_ONE_NONE + "T" + DOT + "T)"
                + DOT_ONE_NONE + "($)" + EOS, "1xnánar tiltekiðx3");
        abbreviationDict.put(BOS + "([Nn]kl|NKL)" + EOS, "nákvæmlega");
        abbreviationDict.put(BOS + "([Oo]" + DOT_ONE_NONE + "fl|O" + DOT_ONE_NONE + "FL)" + DOT_ONE_NONE + EOS, "1xog fleirax3");
        abbreviationDict.put(BOS + "([Oo]" + DOT_ONE_NONE + "m" + DOT_ONE_NONE + "fl|O" + DOT_ONE_NONE + "M" + DOT_ONE_NONE + "FL)"
                + DOT_ONE_NONE + EOS, "1xog margt fleirax3");
        abbreviationDict.put(BOS + "([Oo]" + DOT_ONE_NONE + "s" + DOT_ONE_NONE + "frv?|O" + DOT_ONE_NONE + "S" + DOT_ONE_NONE + "FRV?)"
                + DOT_ONE_NONE + EOS, "1xog svo framvegisx3");
        abbreviationDict.put(BOS + "([Oo]" + DOT_ONE_NONE + "þ" + DOT_ONE_NONE + "h|O" + DOT_ONE_NONE + "Þ" + DOT_ONE_NONE + "H)"
                + DOT_ONE_NONE + EOS, "1xog þess háttarx3");
        abbreviationDict.put(BOS + "([Oo]" + DOT_ONE_NONE + "þ" + DOT_ONE_NONE + "u" + DOT_ONE_NONE + "l|O" + DOT_ONE_NONE + "Þ"
                + DOT_ONE_NONE + "U" + DOT_ONE_NONE + "L)" + DOT_ONE_NONE + EOS, "1xog því um líktx3");
        abbreviationDict.put(BOS + "([Pp]" + DOT_ONE_NONE + ")" + EOS, "1xpakkix3");

        abbreviationDict.put(BOS + "([Rr]n" + DOT_ONE_NONE + ")(:| [\\d\\-]+)" + EOS, "1xreikningsnúmerx3x4");
        abbreviationDict.put(BOS + "([Rr]itstj" + DOT_ONE_NONE + ")" + EOS, "1xritstjórix3");
        abbreviationDict.put(BOS + "([Rr]slm" + DOT_ONE_NONE + ")" + EOS, "1xrannsóknarlögreglumaðurx3");
        abbreviationDict.put(BOS + "([Rr]ví?k|RVÍ?K)" + EOS, "1xReykjavíkx3");

        abbreviationDict.put(BOS + "(([Ss]íma)?nr" + DOT_ONE_NONE + ")", "1x3xnúmer ");
        abbreviationDict.put(BOS + "([Ss]"+ DOT_ONE_NONE + ")(:| \\d{3}\\-?\\d{4}|\\d{7})" + EOS, "1xsímix3x4");
        abbreviationDict.put(BOS + "([Ss]br" + DOT_ONE_NONE + ")" + EOS, "1xsamanberx3");
        abbreviationDict.put(BOS + "([Ss]" + DOT_ONE_NONE + "l" + DOT_ONE_NONE + "|SL" + DOT + ")" + EOS, "síðastliðinn");
        abbreviationDict.put(BOS + "([Ss]" + DOT_ONE_NONE + "k" + DOT_ONE_NONE + ")" + EOS, "1xsvokallaðx3");
        abbreviationDict.put(BOS + "([Ss]kv|SKV)" + DOT_ONE_NONE + EOS, "1xsamkvæmtx3");
        abbreviationDict.put(BOS + "([Ss]" + DOT + " ?s" + DOT_ONE_NONE + ")" + EOS, "1xsvo semx3");
        abbreviationDict.put(BOS + "([Ss]amþ|SAMÞ)" + DOT_ONE_NONE + EOS, "1xsamþykkix3"); // ekki "samþykkt"?
        abbreviationDict.put(BOS + "([Ss]gt" + DOT_ONE_NONE + ")" + EOS, "1xsergeantx3");
        abbreviationDict.put(BOS + "([Ss]t" + DOT_ONE_NONE + ")" + EOS, "1xsaintx3");
        abbreviationDict.put(BOS + "([Ss]ltjn|SLTJN)" + DOT_ONE_NONE + EOS, "1xSeltjarnarnesx3");
        abbreviationDict.put(BOS + "([Ss]thlm|STHLM)" + EOS, "1xStokkhólmurx3");

        abbreviationDict.put(BOS + "([Tt]bl|TBL)" + DOT_ONE_NONE + EOS, "1xtölublaðx3"); //beyging?
        abbreviationDict.put(BOS + "([Tt]l" + DOT_ONE_NONE + ")" + EOS, "1xtengiliðurx3");
        abbreviationDict.put(BOS + "([Tt]" + DOT + "h" + DOT_ONE_NONE + ")" + EOS, "1xtil hægrix3");
        abbreviationDict.put(BOS + "([Tt]" + DOT + "v" + DOT_ONE_NONE + ")" + EOS, "1xtil vinstrix3");
        abbreviationDict.put(BOS + "([Tt]" + DOT + "a" + DOT + "m|T" + DOT + "A" + DOT + "M)" + DOT_ONE_NONE + EOS, "1xtil að myndax3");
        abbreviationDict.put(BOS + "([Tt]" + DOT_ONE_NONE + "d|T" + DOT_ONE_NONE + "D)" + DOT_ONE_NONE + EOS, "1xtil dæmisx3");

        abbreviationDict.put(BOS + "([Uu]" + DOT_ONE_NONE + "þ" + DOT_ONE_NONE + "b|U" + DOT_ONE_NONE + "Þ" + DOT_ONE_NONE + "B)"
                + DOT_ONE_NONE + EOS, "1xum það bilx3");
        abbreviationDict.put(BOS + "([Uu]ppl|UPPL)" + DOT_ONE_NONE + EOS, "1xupplýsingarx3");
        abbreviationDict.put(BOS + "([Uu]td|UTD)" + DOT_ONE_NONE + EOS, "1xunitedx3");
        abbreviationDict.put(BOS + "([Vv]s|VS)" + DOT_ONE_NONE + EOS, "1xversusx3");
        abbreviationDict.put(BOS + "([Vv]sk" + DOT_ONE_NONE + ")" + EOS, "1xvirðisaukaskattx3"); // beyginar? ekki "skattur"?

        abbreviationDict.put(BOS + "([Þþ]f" + DOT_ONE_NONE+ ")" + EOS, "1xþolfallx3");
        abbreviationDict.put(BOS + "([Þþ]gf" + DOT_ONE_NONE+ ")" + EOS, "1xþágufallx3");
        abbreviationDict.put(BOS + "([Þþ]lt" + DOT_ONE_NONE+ ")" + EOS, "1xþáliðin tíðx3");
        abbreviationDict.put(BOS + "([Þþ]" + DOT + "á" + DOT + ")" + EOS, "1xþessa ársx3");
        abbreviationDict.put(BOS + "([Þþ]" + DOT + "h" + DOT + ")" + EOS, "1xþess háttarx3");
        abbreviationDict.put(BOS + "([Þþ]" + DOT + "m" + DOT + ")" + EOS, "1xþessa mánaðarx3");
        abbreviationDict.put(BOS + "([Þþ]" + DOT + "a" + DOT_ONE_NONE + ")" + EOS, "1xþannig aðx3");
        abbreviationDict.put(BOS + "([Þþ]" + DOT_ONE_NONE + "e" + DOT_ONE_NONE + "a" + DOT_ONE_NONE + "s|" +
                "Þ" + DOT_ONE_NONE + "E" + DOT_ONE_NONE + "A" + DOT_ONE_NONE + "S)" + DOT_ONE_NONE + EOS, "1xþað er að segjax3");
        abbreviationDict.put(BOS + "([Þþ]" + DOT_ONE_NONE + "a" + DOT_ONE_NONE + DOT_ONE_NONE + "a|Þ" + DOT_ONE_NONE + "A"
                + DOT_ONE_NONE + "A)" + DOT_ONE_NONE + EOS, "1xþá og því aðeins aðx3");
        abbreviationDict.put(BOS + "([Þþ]" + DOT + "u" + DOT + "l" + DOT + "|Þ" + DOT + "U" + DOT + "L)" + DOT_ONE_NONE + EOS,
                "1xþví um líktx3");
        abbreviationDict.put(BOS + "([Þþ]" + DOT + "a" + DOT + "l" + DOT + "|Þ" + DOT + "A" + DOT + "L)" + DOT_ONE_NONE + EOS,
                "1xþar af leiðandix3");
        abbreviationDict.put(BOS + "([Þþ]" + DOT + "á(" + DOT + "| )m" + DOT + "|Þ" + DOT + "Á(" + DOT + "| )M)" + DOT_ONE_NONE + EOS,
                "1xþar á meðalx3");
        abbreviationDict.put(BOS + "([Þþ]" + DOT + "m" + DOT + "t" + DOT + "|Þ" + DOT + "M" + DOT + "T)" + DOT_ONE_NONE + EOS,
                "1xþar með taliðx3");

        abbreviationDict.put(" ((" + MEASURE_PREFIX_DIGITS + "|" + MEASURE_PREFIX_WORDS + ")( )?\\s)(þú" + DOT_ONE_NONE + ")" +
                "( " + LETTERS + "*)?", "1xþúsundx11"); // changed from group 13 to 11
        abbreviationDict.put(" ([Mm]örg )þús" + DOT_ONE_NONE + "( " + LETTERS + "*)?", "1xþúsundx2");

        abbreviationDict.put("(\\d+" + DOT + ") [Áá]rg" + DOT + EOS, "1x árgangurx2");
        abbreviationDict.put(BOS + "([Óó]ákv" + DOT + "gr" + DOT + ")" + EOS, "1xóáveðinn greinirx3");
        abbreviationDict.put("(\\d+" + DOT + ") útg" + DOT + EOS, "1x útgáfax2");
        abbreviationDict.put(BOS + "([Íí]sl|ÍSL)" + DOT_ONE_NONE + EOS, "1xíslenskax3");

        abbreviationDict.put("([02-9])( )?°C" + EOS, "1x gráður selsíusx2"); // 3 -> 2, all entries below
        abbreviationDict.put("(1)( )?°C", "1x gráða selsíusx2");
        abbreviationDict.put("([02-9])( )?°F" + EOS, "1x gráður farenheitx2");
        abbreviationDict.put("(1)( )?°F", "1x gráða farenheitx2");
        abbreviationDict.put("([02-9])( )?°W" + EOS, "1x gráður vesturx2");
        abbreviationDict.put("(1)( )?°W", "1x gráða vesturx2");
        abbreviationDict.put("([02-9])( )?°N" + EOS, "1x gráður norðurx2");
        abbreviationDict.put("(1)( )?°N", "1x gráða norðurx2");
        abbreviationDict.put("([02-9])( )?°E" + EOS, "1x gráður austurx2");
        abbreviationDict.put("(1)( )?°E", "1x gráða austurx2");
        abbreviationDict.put("([02-9])( )?°S" + EOS, "1x gráður suðurx2");
        abbreviationDict.put("(1)( )?°S", "1x gráða suðurx2");
    }


    public static final Map<String, String> directionDict = new HashMap<>();
    static {
        // we don't accept dashes (u2013 or u2014), only standard hyphenation.
        // see more patterns in directiondict.txt
        directionDict.put("(\\W|^)(SV-(:?(til|lands|átt|verðu|vert)))(\\W|$)", "1xsuðvestanx3x4");
        directionDict.put("(\\W|^)(NV-(:?(til|lands|átt|verðu|vert)))(\\W|$)", "1xnorðvestanx3x4");
        directionDict.put("(\\W|^)(NA-(:?(til|lands|átt|verðu|vert)))(\\W|$)", "1xnorðaustanx3x4");
        directionDict.put("(\\W|^)(SA-(:?(til|lands|átt|verðu|vert)))(\\W|$)", "1xsuðaustanx4x5");
        directionDict.put("(\\W|^)(A-(:?(til|lands|átt|verðu|vert)))(\\W|$)", "1xaustanx4");
        directionDict.put("(\\W|^)(S-(:?(til|lands|átt|verðu|vert)))(\\W|$)", "1xsunnanx4");
        directionDict.put("(\\W|^)(V-(:?(til|lands|átt|verðu|vert)))(\\W|$)", "1xvestanx4");
        directionDict.put("(\\W|^)(N-(:?(til|lands|átt|verðu|vert)))(\\W|$)", "1xnorðanx4");
    }

    public static final Map<String, String> denominatorDict = new HashMap<>();
    static {
        // not complete, see denominatordict.txt for further patterns
        denominatorDict.put("\\/kg" + DOT_ONE_NONE + EOS, " á kílóiðx1");
        denominatorDict.put("\\/t" + DOT_ONE_NONE + EOS, " á tonniðx1");
        denominatorDict.put("\\/ha" + DOT_ONE_NONE + EOS, " á hektarannx1");
        denominatorDict.put("\\/mg" + DOT_ONE_NONE + EOS, " á milligrammiðx1");
        denominatorDict.put("\\/gr" + DOT_ONE_NONE + EOS, " á grammiðx1");
        denominatorDict.put("\\/ml" + DOT_ONE_NONE + EOS, " á millilítrannx1");
        denominatorDict.put("\\/dl" + DOT_ONE_NONE + EOS, " á desilítrannx1");
        denominatorDict.put("\\/l" + DOT_ONE_NONE + EOS, " á lítrannx1");
        denominatorDict.put("\\/km" + DOT_ONE_NONE + EOS, " á kílómetrax1");
        denominatorDict.put("\\/klst" + DOT_ONE_NONE + EOS, " á klukkustundx1");
        denominatorDict.put("\\/kw" + DOT_ONE_NONE + "(st|h)" + DOT_ONE_NONE + EOS, " á kílóvattstundx2");
        denominatorDict.put("\\/Mw" + DOT_ONE_NONE + "(st|h)" + DOT_ONE_NONE + EOS, " á megavattstundx2");
        denominatorDict.put("\\/Gw" + DOT_ONE_NONE + "(st|h)" + DOT_ONE_NONE + EOS, " á gígavattstundx2");
        denominatorDict.put("\\/Tw" + DOT_ONE_NONE + "(st|h)" + DOT_ONE_NONE + EOS, " á teravattstundx2");
        denominatorDict.put("\\/s(ek)?" + DOT_ONE_NONE + EOS, " á sekúndux2");
        denominatorDict.put("\\/mín" + DOT_ONE_NONE + EOS, " á mínútux1");
        denominatorDict.put("\\/fm" + DOT_ONE_NONE + EOS, " á fermetrax1");
        denominatorDict.put("\\/ferm" + DOT_ONE_NONE + EOS, " á fermetrax1");
    }


    public static Map<String, String> weightDict = new HashMap<>();
    static {
        weightDict.put("(" + BOS + "(" + prepositions.get(DAT) + ") ((\\d{1,2}\\.)?(\\d{3}\\.?)*(\\d*1|\\d,\\d*1))) t" + DOT_ONE_NONE + EOS, "1x tonnix10");
        weightDict.put("(" + BOS + "(" + prepositions.get(GEN) + ") ((\\d{1,2}\\.)?(\\d{3}\\.?)*(\\d*1|\\d,\\d*1))) t" + DOT_ONE_NONE + EOS, "1x tonnsx10");
        weightDict.put("(" + BOS + "(" + prepositions.get(DAT) + ") ((\\d{1,2}\\.)?(\\d{3}\\.?)*(\\d*[02-9]|\\d,\\d*[02-9]))) t" + DOT_ONE_NONE + EOS, "1x tonnumx9"); // changed from group 10 to 9
        weightDict.put("(" + BOS + "(" + prepositions.get(DAT) + ") (((\\d{1,2}\\.)?(\\d{3}\\.?)*|\\d+)(,\\d+)?)?) " + patternSelection.get(AMOUNT) + " t" + DOT_ONE_NONE + EOS, "1 x11x tonnumx13");
        // usw. three more, and the same for grams
        weightDict.put("(" + BOS + "(" + prepositions.get(DAT) + ") ((\\d{1,2}\\.)?(\\d{3}\\.?)*(\\d*1|\\d,\\d*1))) g" + DOT_ONE_NONE + EOS, "1x grammix10");
        weightDict.put("(" + BOS + "(" + prepositions.get(GEN) + ") ((\\d{1,2}\\.)?(\\d{3}\\.?)*(\\d*1|\\d,\\d*1))) g" + DOT_ONE_NONE + EOS, "1x grammsx10");
        weightDict.put("(" + BOS + "(" + prepositions.get(DAT) + ") ((\\d{1,2}\\.)?(\\d{3}\\.?)*(\\d*[02-9]|\\d,\\d*[02-9]))) g\\.?(\\W|$)", "1x grömmumx10");
        weightDict.put("(" + BOS + "(" + prepositions.get(DAT) + ") (((\\d{1,2}\\.)?(\\d{3}\\.?)*|\\d+)(,\\d+)?)?) " + patternSelection.get(AMOUNT) + " g"+ DOT_ONE_NONE + EOS, "1 x11x grömmumx13");
        weightDict.put("(1 )gr?" + DOT_ONE_NONE + EOS, "1xgrammx2");
        weightDict.put("([02-9]|" + patternSelection.get(AMOUNT) + ") gr?\\.?(\\W|$)", "1x grömmx3");


        // another section for nanó/milli/míkró/píkó/attó/zeptó/yoktó-kíló/pund + grammi/gramms/grömmum, ...
        // see class weight_dict.py in regina
    }

    public static OrderedMap<String, String> distanceDict = new ListOrderedMap<>();
    public static Map<String, String> getDistanceDict() {
        if (!distanceDict.isEmpty())
            return distanceDict;

        Map<String, String> prefixMap = new HashMap<>();
        // first initialized with "fet", "tomma", and some cryptic patterns for "metri"

        distanceDict.put("(" + BOS + "(" + prepositions.get(ACC_DAT_GEN_COMB) + ") " + NUMBER_EOS_1 + " )m" + DOT_ONE_NONE +
                "( (?![kmgyabefstvö]" + DOT + ")" + LETTER_OR_DIGIT + "*" + EOS  + ")", "1x metrax10"); // from 14 to 10
        distanceDict.put("(" + BOS + "(" + prepositions.get(ACC_GEN) + ") (" + NUMBER_EOS_NOT_1 + " )m" + DOT_ONE_NONE +
                "( (?![kmgyabefstvö]" + DOT + ")" + LETTER_OR_DIGIT + "*" + EOS  + ")", "1x metrax9x10");
        distanceDict.put("(" + BOS + "(" + prepositions.get(ACC_GEN) + ") " + NUMBER_ANY + " " + patternSelection.get(AMOUNT) + " )m" + DOT_ONE_NONE +
                "( (?![kmgyabefstvö]" + DOT + ")" + LETTER_OR_DIGIT + "*" + EOS  + ")", "1x 13x metrax16");
        distanceDict.put("(" + BOS + "(" + prepositions.get(DAT) + ") (" + NUMBER_EOS_NOT_1 + " )m" + DOT_ONE_NONE +
                "( (?![kmgyabefstvö]" + DOT + ")" + LETTER_OR_DIGIT + "*" + EOS + ")", "1x metrumx10");
        distanceDict.put("(" + BOS + "(" + prepositions.get(DAT) + ") " + NUMBER_ANY + " " + patternSelection.get(AMOUNT) + " )m" + DOT_ONE_NONE +
                "( (?![kmgyabefstvö]" + DOT + ")(?![kmgyabefstvö]" + DOT + ")" + LETTER_OR_DIGIT + "*" + EOS  + ")", "1x 11x metrumx14");
        distanceDict.put("(1 )m" + DOT_ONE_NONE + "( (?![kmgyabefstvö]" + DOT + ")" + LETTER_OR_DIGIT + "*" + EOS + ")",
                "1xmetrix2");
        distanceDict.put("([02-9] )m" + DOT_ONE_NONE + "( (?![kmgyabefstvö]" + DOT + ")" + LETTER_OR_DIGIT + "*" + EOS  + ")",
                "1xmetrarx2");

        // also píkó, nanó, míkró, njúton, in original regina
        prefixMap.put("m", "milli");
        prefixMap.put("[cs]", "senti");
        prefixMap.put("d", "desi");
        prefixMap.put("k", "kíló");

        for (String letter : prefixMap.keySet()) {
            distanceDict.put("(" + BOS + "(" + prepositions.get(ACC_DAT_GEN_COMB) + ") ((\\d{1,2}" + DOT + ")?(\\d{3}"
                    + DOT + ")*(\\d*1|\\d,\\d*1))) " + letter + "m" + DOT_ONE_NONE + EOS, "1x " + prefixMap.get(letter) + "metrax10"); //14->10
            distanceDict.put("(" + BOS + "(" + prepositions.get(ACC_GEN) + ") " + NUMBER_EOS_NOT_1 + " " + letter + "m"
                    + DOT_ONE_NONE + EOS, "1x " + prefixMap.get(letter) + "metrax8"); // different group count from regina! (12)
            distanceDict.put("(" + BOS + "(" + prepositions.get(ACC_GEN) + ") " + NUMBER_ANY + ") " + patternSelection.get(AMOUNT) +
                    " " + letter + "m" + DOT_ONE_NONE + EOS, "1x 10x " + prefixMap.get(letter) + "metrax12");
            distanceDict.put("(" + BOS + "(" + prepositions.get(DAT) + ") " + NUMBER_EOS_NOT_1 + " " + letter + "m" +
                    DOT_ONE_NONE + EOS, "1x " + prefixMap.get(letter) + "metrumx8"); //10 -> 8
            distanceDict.put("(" + BOS + "(" + prepositions.get(DAT) + ") " + NUMBER_ANY + ") " + patternSelection.get(AMOUNT) +
                    " " + letter + "m" + DOT_ONE_NONE + EOS, "1x 11x" + prefixMap.get(letter) + "metrumx14");
            distanceDict.put("(1 )" + letter + "m" + DOT_ONE_NONE + EOS, "1x " + prefixMap.get(letter) + "metri x2");
            distanceDict.put("([0-9]|" + patternSelection.get(AMOUNT) + ") " + letter + "m" + DOT_ONE_NONE + EOS, "1x " + prefixMap.get(letter) + "metrarx3");
        }

        return distanceDict;
    }

    //TODO: take care not to normalize the superscripts away when doing unicode normalization!
    private static Map<String, String> dimensionBefore = new HashMap<String, String>() {{
        put("²", "fer");
        // put("2", "fer");
        // put("³", "rúm");
        // put("3", "rúm");
    }};
    private static Map<String, String> dimensionAfter = new HashMap<String, String>() {{
        put("f", "fer");
        put("fer", "fer");
        // put("rúm", "rúm");
    }};
    private static Map<String, String> prefixMeterDimension = new HashMap<String, String>() {{
        put("", "");
        //  put("m", "milli");
        // put("[cs]", "senti");
        // put("d", "desi");
        // put("k", "kíló");
    }};


    public static Map<String, String> areaDict = new HashMap<>();

    public static Map<String, String> getAreaDict() {
        if (!areaDict.isEmpty())
            return areaDict;

        areaDict.put("((\\W|^)(" + prepositions.get(ACC_DAT_GEN_COMB) + ") ((\\d{1,2}\\.)?(\\d{3}\\.?)*(\\d*1|\\d,\\d*1))) ha\\.?(\\W|$)", "1x hektarax14");
        areaDict.put("((\\W|^)(" + prepositions.get(ACC_GEN) + ") ((\\d{1,2}\\.)?(\\d{3}\\.?)*(\\d*[02-9]|\\d,\\d*[02-9]))) ha\\.?(\\W|$)", "1x hektarax12");
        areaDict.put("((\\W|^)(" + prepositions.get(ACC_GEN) + ") (((\\d{1,2}\\.)?(\\d{3}\\.?)*|\\d+)(,\\d+)?)?) " + patternSelection.get(AMOUNT) + " ha\\.?(\\W|$)", "1x 13x hektarax16x");
        areaDict.put("((\\W|^)(" + prepositions.get(DAT)+ ") ((\\d{1,2}\\.)?(\\d{3}\\.?)*(\\d*[02-9]|\\d,\\d*[02-9]))) ha\\.?(\\W|$)","1x hekturumx10");
        areaDict.put("((\\W|^)(" + prepositions.get(DAT) + ") (((\\d{1,2}\\.)?(\\d{3}\\.?)*|\\d+)(,\\d+)?)?) " + patternSelection.get(AMOUNT) + " ha\\.?(\\W|$)", "1x 11x hekturumx14");
        areaDict.put("(1) ha\\.?(\\W|$)", "1x hektarix2");
        // does this change later? at the moment we get: "reisa 15 ha ..." -> "reisa 15 hektarar ..."
        areaDict.put("([02-9]|" + patternSelection.get(AMOUNT) + ") ha\\.?(\\W|$)","1x hektararx3");

        for (String letter : prefixMeterDimension.keySet()) {
            for (String superscript : dimensionAfter.keySet()) {
                areaDict.put("((\\W|^)(" + prepositions.get(ACC_DAT_GEN_COMB) + ") ((\\d{1,2}\\.)?(\\d{3}\\.?)*(\\d*1|\\d,\\d*1)) " + letter + "m" + superscript + "(\\W|$))",
                        "1x " + dimensionAfter.get(superscript) + prefixMeterDimension.get(letter) + "metrax14");
                areaDict.put("((\\W|^)(" + prepositions.get(ACC_GEN) + ") ((\\d{1,2}\\.)?(\\d{3}\\.?)*(\\d*[02-9]|\\d,\\d*[02-9]))) " + letter + "m" + superscript + "(\\W|$)",
                        "1x " + dimensionAfter.get(superscript) + prefixMeterDimension.get(letter) + "metrax12");
                areaDict.put("((\\W|^)(" + prepositions.get(ACC_GEN) + ") (((\\d{1,2}\\.)?(\\d{3}\\.?)*|\\d+)(,\\d+)?)?) " + patternSelection.get(AMOUNT) + " " + letter + "m" + superscript + "(\\W|$)",
                        "1x 13x " + dimensionAfter.get(superscript) + prefixMeterDimension.get(letter) + "metrax16");
                areaDict.put("((\\W|^)(" + prepositions.get(DAT) + ") ((\\d{1,2}\\.)?(\\d{3}\\.?)*(\\d*[02-9]|\\d,\\d*[02-9]))) " + letter + "m" + superscript + "(\\W|$)",
                        "1x " + dimensionAfter.get(superscript) + prefixMeterDimension.get(letter) + "metrumx10");
                areaDict.put("((\\W|^)(" + prepositions.get(DAT) + ") (((\\d{1,2}\\.)?(\\d{3}\\.?)*|\\d+)(,\\d+)?)?) " + patternSelection.get(AMOUNT) + " " + letter + "m" + superscript + "(\\W|$)",
                        "1x 11x " + dimensionAfter.get(superscript) + prefixMeterDimension.get(letter) + "metrumx14");
                areaDict.put("(1 )" + letter + "m" + superscript + "(\\W|$)", "1x" + dimensionAfter.get(superscript) + prefixMeterDimension.get(letter) + "metrix2");
                areaDict.put("([02-9]|" + patternSelection.get(AMOUNT) + ") " + letter + "m" + superscript + "(\\W|$)", "1x " + dimensionAfter.get(superscript) + prefixMeterDimension.get(letter) + "metrar x3");

            }
        }

        for (String letter : prefixMeterDimension.keySet()) {
            for (String preprefix : dimensionBefore.keySet()) {
                areaDict.put("((\\W|^)(" + prepositions.get(ACC_DAT_GEN_COMB) + ") ((\\d{1,2}\\.)?(\\d{3}\\.?)*(\\d*1|\\d,\\d*1))) " + preprefix + letter + "m\\.?(\\W|$)",
                        "1x " + dimensionBefore.get(preprefix) + prefixMeterDimension.get(letter) + "metrax14");
                areaDict.put("((\\W|^)(" + prepositions.get(ACC_GEN) + ") ((\\d{1,2}\\.)?(\\d{3}\\.?)*(\\d*[02-9]|\\d,\\d*[02-9]))) " + preprefix + letter + "m\\.?(\\W|$)",
                        "1x " + dimensionBefore.get(preprefix) + prefixMeterDimension.get(letter) + "metrax12");
                areaDict.put("((\\W|^)(" + prepositions.get(ACC_GEN) + ") (((\\d{1,2}\\.)?(\\d{3}\\.?)*|\\d+)(,\\d+)?)?) " + patternSelection.get(AMOUNT) + " " + preprefix + letter + "m\\.?(\\W|$)",
                        "1x 13x " + dimensionBefore.get(preprefix) + prefixMeterDimension.get(letter) + "metrax16");
                areaDict.put("((\\W|^)(" + prepositions.get(DAT) + ") ((\\d{1,2}\\.)?(\\d{3}\\.?)*(\\d*[02-9]|\\d,\\d*[02-9]))) " + preprefix + letter + "m\\.?(\\W|$)",
                        "1x " + dimensionBefore.get(preprefix) + prefixMeterDimension.get(letter) + "metrumx8"); //10 -> 8
                areaDict.put("((\\W|^)(" + prepositions.get(DAT) + ") (((\\d{1,2}\\.)?(\\d{3}\\.?)*|\\d+)(,\\d+)?)?) " + patternSelection.get(AMOUNT) + " " + preprefix + letter + "m\\.?(\\W|$)",
                        "1x 11x" + dimensionBefore.get(preprefix) + prefixMeterDimension.get(letter) + "metrumx14");
                areaDict.put("(1 )" + preprefix + letter + "m\\.?(\\W|$)", "1x " + dimensionBefore.get(preprefix) + prefixMeterDimension.get(letter) + "metrix2");
                areaDict.put("([02-9]|" + patternSelection.get(AMOUNT) + ") " + preprefix + letter + "m\\.?(\\W|$)", "1x " + dimensionBefore.get(preprefix) + prefixMeterDimension.get(letter) + "metrarx3");
                //added ABN, previous patterns did not capture " ... ( 150.000 m² ) ..." - still need to figure that out
                areaDict.put("((\\W|^)(\\d{3}\\.\\d{3})) " + letter + "m" + preprefix + "(\\W|$)", "1x " + dimensionBefore.get(preprefix) + prefixMeterDimension.get(letter) + "metrarx4");
            }
        }
        return areaDict;
    }

    private static Map<String, String> volumeDict = new HashMap<>();

    public static Map<String, String> getVolumeDict() {
        if (!volumeDict.isEmpty())
            return volumeDict;

        Map<String, String> prefixLiter = new HashMap<String, String>() {{
            put("", "");
            put("d", "desi");
            put("c", "senti");
            put("m", "milli");
        }};

        for (String letter : prefixLiter.keySet()) {
            volumeDict.put("((\\W|^)(" + prepositions.get(ACC_DAT_GEN_COMB)+ ") ((\\d{1,2}\\.)?(\\d{3}\\.?)*(\\d*1|\\d,\\d*1))) " + letter + "[Ll]\\.?(\\W|$)", "1x " + prefixLiter.get(letter) + "lítrax14x");
            volumeDict.put("((\\W|^)(" + prepositions.get(ACC_GEN) + ") ((\\d{1,2}\\.)?(\\d{3}\\.?)*(\\d*[02-9]|\\d,\\d*[02-9]))) " + letter + "[Ll]\\.?(\\W|$)", "1 " + prefixLiter.get(letter) + "lítrax9"); // 12->9
            volumeDict.put("((\\W|^)(" + prepositions.get(ACC_GEN) + ") (((\\d{1,2}\\.)?(\\d{3}\\.?)*|\\d+)(,\\d+)?)?) " + patternSelection.get(AMOUNT) + " " + letter + "[Ll]\\.?(\\W|$)", "1x 13x " + prefixLiter.get(letter) + "lítrax16");
            volumeDict.put("((\\W|^)(" + prepositions.get(DAT) + ") ((\\d{1,2}\\.)?(\\d{3}\\.?)*(\\d*[02-9]|\\d,\\d*[02-9]))) " + letter + "[Ll]\\.?(\\W|$)", "1x " + prefixLiter.get(letter) + "lítrumx10");
            volumeDict.put("((\\W|^)(" + prepositions.get(DAT) + ") (((\\d{1,2}\\.)?(\\d{3}\\.?)*|\\d+)(,\\d+)?)?) " + patternSelection.get(AMOUNT) + " " + letter + "[Ll]\\.?(\\W|$)", "1x 11x " + prefixLiter.get(letter) + "lítrumx14");
            volumeDict.put("(1 )" + letter + "[Ll]\\.?(\\W|$)", "1x" + prefixLiter.get(letter) + "lítrix2");
            volumeDict.put("([02-9]|" + patternSelection.get(AMOUNT) + ") " + letter + "[Ll]\\.?(\\W|$)", "");
            //if (!letter.isEmpty())
            //    volumeDict.put("(\\W|^)" + letter + "l\\.?(\\W|$)", "1x" + prefixLiter.get(letter) + "lítrar x2");
        }

        return volumeDict;
    }

    private static Map<String, String> timeDict = new HashMap<>();

    public static Map<String, String> getTimeDict() {
        if  (!timeDict.isEmpty())
            return timeDict;

        timeDict.put("((\\W|^)(" + prepositions.get(GEN) + ") ((\\d{1,2}\\.)?(\\d{3}\\.?)*(\\d*1|\\d,\\d*1))) klst\\.?(\\W|$)",  "1x klukkustundarx10");
        timeDict.put("((\\W|^)(" + prepositions.get(DAT) + ") ((\\d{1,2}\\.)?(\\d{3}\\.?)*(\\d*[02-9]|\\d,\\d*[02-9]))) klst\\.?(\\W|$)", "1x klukkustundumx10");
        timeDict.put("((\\W|^)(" + prepositions.get(DAT) + ") (((\\d{1,2}\\.)?(\\d{3}\\.?)*|\\d+)(,\\d+)?)?) " + patternSelection.get(AMOUNT) + " klst\\.?(\\W|$)", "1x 11x klukkustundumx14");
        timeDict.put("((\\W|^)(" + prepositions.get(GEN) + ") ((\\d{1,2}\\.)?(\\d{3}\\.?)*(\\d*[02-9]|\\d,\\d*[02-9]))) klst\\.?(\\W|$)", "1x klukkustundax10");
        timeDict.put("((\\W|^)(" + prepositions.get(GEN) + ") (((\\d{1,2}\\.)?(\\d{3}\\.?)*|\\d+)(,\\d+)?)?) " + patternSelection.get(AMOUNT) + " klst\\.?(\\W|$)", "1x 11x klukkustundax14");
        timeDict.put("(1 )klst\\.?(\\W|$)", "1x klukkustundx2");
        timeDict.put("(\\W|^)klst\\.?(\\W|$)", "1xklukkustundirx2x");

        Map<String, String> prefixTime = new HashMap<String, String>() {{
            put("mín()?", "mínút");
            put("s(ek)?", "sekúnd");
            put("ms(ek)?", "millisekúnd");
        }};

        for (String letters : prefixTime.keySet()) {
            timeDict.put("(" + BOS + "(" + prepositions.get(ACC_DAT_GEN_COMB) + ") ((\\d{1,2}\\.)?(\\d{3}\\.?)*(\\d*1|\\d,\\d*1))) " + letters + DOT_ONE_NONE + EOS, "1x " + prefixTime.get(letters) + "ux11");
            timeDict.put("(" + BOS + "(" + prepositions.get(DAT) + ") ((\\d{1,2}\\.)?(\\d{3}\\.?)*(\\d*[02-9]|\\d,\\d*[02-9]))) " + letters + DOT_ONE_NONE + EOS, "1x " + prefixTime.get(letters) + "umx11");
            timeDict.put("(" + BOS + "(" + prepositions.get(DAT) + ") (((\\d{1,2}\\.)?(\\d{3}\\.?)*|\\d+)(,\\d+)?)?) " + patternSelection.get(AMOUNT) + " " + letters + DOT_ONE_NONE + EOS, "1x 11x " + prefixTime.get(letters) + "umx15");
            timeDict.put("(" + BOS + "(" + prepositions.get(GEN) + ") ((\\d{1,2}\\.)?(\\d{3}\\.?)*(\\d*[02-9]|\\d,\\d*[02-9]))) " + letters + DOT_ONE_NONE + EOS, "1x " + prefixTime.get(letters) + "nax9"); //changed group from 11 to 9
            timeDict.put("(" + BOS + "(" + prepositions.get(GEN) + ") (((\\d{1,2}\\.)?(\\d{3}\\.?)*|\\d+)(,\\d+)?)?) " + patternSelection.get(AMOUNT) + " " + letters + DOT_ONE_NONE + EOS, "1x 11x " + prefixTime.get(letters) + "nax15");
            // added ABN: we need 'undir' ('undir x sek/klst/...')
            timeDict.put("((\\W|^)(" + prepositions.get(ACC_DAT) + ") ((\\d{1,2}\\.)?(\\d{3}\\.?)*(\\d*[02-9]|\\d,\\d*[02-9]))) " + letters + DOT_ONE_NONE + EOS, "1x " + prefixTime.get(letters) + "umx9");

            timeDict.put("(1 )" + letters + DOT_ONE_NONE + EOS, "1x" + prefixTime.get(letters) + "ax2");
            //TODO: this one messes up, need to give the preposition patterns priority and not allow this one to intervene. But why do they both match after one has been substituted? I.e. " ... sekúndur ..." matches the pattern above with preposition
            //timeDict.put("([02-9]|" + patternSelection.get(AMOUNT) + ") " + letters + "\\.?(\\W|$)", "1x " + prefixTime.get(letters) + "ur x3");
        }

        return timeDict;
    }

    public static Map<String, String> currencyDict = new HashMap<>();

    public static Map<String, String> getCurrencyDict() {
        if (!currencyDict.isEmpty())
            return currencyDict;
        // krónur:
        currencyDict.put("((\\W|^)(" + prepositions.get(DAT) + ")) kr\\.?\\-? ?((((\\d{1,2}\\.)?(\\d{3}\\.?)*|\\d+)(,\\d+)?)? " + patternSelection.get(AMOUNT) + ")" + EOS, "1x 6x krónumx15");
        currencyDict.put("((\\W|^)(" + prepositions.get(GEN) + ")) kr\\.?\\-? ?((((\\d{1,2}\\.)?(\\d{3}\\.?)*|\\d+)(,\\d+)?)? " + patternSelection.get(AMOUNT) + ")" + EOS, "1x 6xkrónax15");
        currencyDict.put("(\\W|^)[Kk]r\\.? ?((((\\d{1,2}\\.)?(\\d{3}\\.?)*|\\d+)(,\\d+)?)? " + patternSelection.get(AMOUNT) + ")" + EOS, "1x 2x krónurx11");
        currencyDict.put("((\\W|^)(" + prepositions.get(ACC_DAT_GEN_COMB) + ") ((\\d{1,2}\\.)?(\\d{3}\\.?)*(\\d*1|\\d,\\d*1))) ?kr\\.?\\-?" + EOS, "1x krónux14");
        currencyDict.put("((\\W|^)(" + prepositions.get(ACC_DAT_GEN_COMB) + ")) kr\\.?\\-? ?((\\d{1,2}\\.)?(\\d{3}\\.?)*(\\d*1|\\d,\\d*1))" + EOS, "1x 10x krónux14");
        currencyDict.put("((\\W|^)(" + prepositions.get(DAT) + ") ((\\d{1,2}\\.)?(\\d{3}\\.?)*(\\d*[02-9]|\\d,\\d*[02-9]))) kr\\.?\\-?" + EOS, "1x krónumx10");
        currencyDict.put("((\\W|^)(" + prepositions.get(DAT) + ") (((\\d{1,2}\\.)?(\\d{3}\\.?)*|\\d+)(,\\d+)?)? " + patternSelection.get(AMOUNT) + ") kr\\.?\\-?" + EOS, "1x 8xkrónumx14");
        currencyDict.put("((\\W|^)(" + prepositions.get(DAT) + ")) kr\\.?\\-? ?((\\d{1,2}\\.)?(\\d{3}\\.?)*(\\d*[02-9]|\\d,\\d*[02-9]))" + EOS, "1x 9x krónumx10");
        currencyDict.put("((\\W|^)(" + prepositions.get(GEN) + ") ((\\d{1,2}\\.)?(\\d{3}\\.?)*(\\d*[02-9]|\\d,\\d*[02-9]))) kr\\.?\\-?" + EOS, "1x krónax10");
        currencyDict.put("((\\W|^)(" + prepositions.get(GEN) + ") (((\\d{1,2}\\.)?(\\d{3}\\.?)*|\\d+)(,\\d+)?)? " + patternSelection.get(AMOUNT) + ") kr\\.?\\-?" + EOS, "1x 8xkrónax14");
        currencyDict.put("((\\W|^)(" + prepositions.get(GEN) + ")) kr\\.?\\-? ?((\\d{1,2}\\.)?(\\d{3}\\.?)*(\\d*[02-9]|\\d,\\d*[02-9]))" + EOS, "1x 9x krónax10");
        currencyDict.put("(1 ?)kr\\.?\\-?(\\W|$)", "1xkrónax2");
        currencyDict.put("([02-9]|" + patternSelection.get(AMOUNT) + ") ?kr\\.?\\-?(\\W|$)", "1x krónurx3");
        // is this an error? (2 times group 2)
        //currencyDict.put("(\\W|^)[Kk]r\\.? ?(\\d)", "1x2x krónurx2");
        currencyDict.put("(\\W|^)[Kk]r\\.? ?(\\d)", "1xkrónur x2");

        // MUCH more here! other currencies, etc.

        return currencyDict;

    }

    public static Map<String, String> electronicDict = new HashMap<>();

    public static Map<String, String> getElectronicDict() {
        if (!electronicDict.isEmpty())
            return electronicDict;

        Map<String, String> wattPrefix = new HashMap<>();
        wattPrefix.put("", "");
        wattPrefix.put("k", "kíló");
        wattPrefix.put("M", "Mega");
        wattPrefix.put("G", "Gíga");
        wattPrefix.put("T", "Tera");


        Map<String, String> measurement = new HashMap<>();
        measurement.put("V", "volt");
        measurement.put("Hz", "herz");


        for (String letter : wattPrefix.keySet()) {
            electronicDict.put("(" + BOS + "(" + prepositions.get(GEN) + ") (" + NUMBER_EOS_1 + ")) " + letter + "[Ww]"
                    + DOT_ONE_NONE + "(st|h)" + DOT_ONE_NONE + EOS, "1x " + wattPrefix.get(letter) + "vattstundarx11");
            electronicDict.put("(" + BOS + "(" + prepositions.get(DAT) + ") (" + NUMBER_EOS_NOT_1 + ") " + letter + "[Ww]"
                    + DOT_ONE_NONE + "(st|h)" + DOT_ONE_NONE + EOS, "1x " + wattPrefix.get(letter) + "vattstundumx11");
            electronicDict.put("(" + BOS + "(" + prepositions.get(DAT) + ") (" + NUMBER_ANY + ") " + patternSelection.get(AMOUNT)
                    + ") " + letter + "[Ww]" + DOT_ONE_NONE + "(st|h)" + DOT_ONE_NONE + EOS, "1x 11x " + wattPrefix.get(letter) + "vattstundumx15");
            electronicDict.put("([02-9]|" + patternSelection.get(AMOUNT) + ") " + letter + "W" + EOS, "1x " + wattPrefix.get(letter) + "vöttx3");
            electronicDict.put("(1 )" + letter + "W" + EOS, "1x " + wattPrefix.get(letter) + "vattx2");
            electronicDict.put("([02-9]|" + patternSelection.get(AMOUNT) + ") " + letter + "[Ww]" + DOT_ONE_NONE + "(st|h)" +
                    DOT_ONE_NONE + EOS, "1x " + wattPrefix.get(letter) + "vattstundirx3");
            //electronic_dict.update({"([02-9]|" + amounts + ") " + letter + "[Ww]\.?(st|h)\.?(\W|$)": "\g<1> " + prefix + "vattstundir \g<3>"})
            // etc. see electronic_dict.py in regina original
        }
        return electronicDict;
    }

    public static Map<String, String> restDict = new HashMap<>();
    static {
        restDict.put("(" + BOS + "(" + prepositions.get(DAT) + ") (" + NUMBER_EOS_1 + ")) ?\\%" + EOS, "1x prósentix9"); // changed from group 10 to 9 ( in the next entries as well)
        restDict.put("(" + BOS + "(" + prepositions.get(GEN) + ") (" + NUMBER_EOS_1 + ")) ?\\%" + EOS, "1x prósentsx9");
        restDict.put("(" + BOS + "(" + prepositions.get(DAT) + ") (" + NUMBER_EOS_NOT_1 + ") ?\\%" + EOS, "1x prósentumx9");
        restDict.put("(" + BOS + "(" + prepositions.get(DAT) + ") (" + NUMBER_ANY + ") " + patternSelection.get(AMOUNT)
                + ")\\%" + EOS, "1x 11x prósentumx14");
        restDict.put("(" + BOS + "(" + prepositions.get(GEN) + ") (" + NUMBER_EOS_NOT_1 + ") ?\\%" + EOS, "1x prósentax8"); // changed from 10 to 8
        restDict.put("(" + BOS + "(" + prepositions.get(GEN) + ") (" + NUMBER_ANY + ") " + patternSelection.get(AMOUNT)
                + ")\\%" + EOS, "1x 11x prósentax14");
        restDict.put("\\%", " prósent");

    }

    public static Map<String, String> periodDict = new HashMap<String, String>() {{
        put("(\\W|^)mán(ud)?\\.?(\\W|$)", "1xmánudagx3");
        put("(\\W|^)þri(ðjud)?\\.?(\\W|$)", "1xþriðjudagx3");
        put("(\\W|^)mið(vikud)?\\.?(\\W|$)", "1xmiðvikudagx3");
        put("(\\W|^)fim(mtud)?\\.?(\\W|$)", "1xfimmtudagx3");
        put("(\\W|^)fös(tud)?\\.?(\\W|$)", "1xföstudagx3");
        put("(\\W|^)lau(gard)?\\.?(\\W|$)", "1xlaugardagx3");
        put("(\\W|^)sun(nud)?\\.?(\\W|$)", "1xsunnudagx3");

        put("(\\W|^)jan\\.?(\\W|$)", "1xjanúarx3");
        put("(\\W|^)feb\\.?(\\W|$)", "1xfebrúarx3");
        put("(\\W|^)mar\\.?(\\W|$)", "1xmarsx3");
        put("(\\W|^)apr\\.?(\\W|$)", "1xaprílx3");
        put("(\\W|^)jún\\.?(\\W|$)", "1xjúníx3");
        put("(\\W|^)júl\\.?(\\W|$)", "1xjúlíx3");
        put("(\\W|^)ágú?\\.?(\\W|$)", "1xágústx3");
        put("(\\W|^)sept?\\.?(\\W|$)", "1xseptemberx3");
        put("(\\W|^)okt\\.?(\\W|$)", "1xoktóberx3");
        put("(\\W|^)nóv\\.?(\\W|$)", "1xnóvemberx3");
        put("(\\W|^)des\\.?(\\W|$)", "1xdesemberx3");

        /*
                "(\W|^)II\.?(\W|$)": "\g<1>annar\g<2>",
                "(\W|^)III\.?(\W|$)": "\g<1>þriðji\g<2>",
                "(\W|^)IV\.?(\W|$)": "\g<1>fjórði\g<2>",
                "(\W|^)VI\.?(\W|$)": "\g<1>sjötti\g<2>",
                "(\W|^)VII\.?(\W|$)": "\g<1>sjöundi\g<2>",
                "(\W|^)VIII\.?(\W|$)": "\g<1>áttundi\g<2>",
                "(\W|^)IX\.?(\W|$)": "\g<1>níundi\g<2>",
                "(\W|^)XI\.?(\W|$)": "\g<1>ellefti\g<2>",
                "(\W|^)XII\.?(\W|$)": "\g<1>tólfti\g<2>",
                "(\W|^)XIII\.?(\W|$)": "\g<1>þrettándi\g<2>",
                "(\W|^)XIV\.?(\W|$)": "\g<1>fjórtándi\g<2>",
                "(\W|^)XV\.?(\W|$)": "\g<1>fimmtándi\g<2>",
                "(\W|^)XVI\.?(\W|$)": "\g<1>sextándi\g<2>",
                "(\W|^)XVII\.?(\W|$)": "\g<1>sautjándi\g<2>",
                "(\W|^)XVIII\.?(\W|$)": "\g<1>átjándi\g<2>",
                "(\W|^)XIX\.?(\W|$)": "\g<1>nítjándi\g<2>"} */

    }};


}
