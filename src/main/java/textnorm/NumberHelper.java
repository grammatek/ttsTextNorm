package textnorm;

import java.util.HashMap;
import java.util.Map;

public class NumberHelper {

    public static final String HUNDRED_THOUSANDS = "hundred thousands";
    public static final String TEN_THOUSANDS = "ten thousands";
    public static final String THOUSANDS = "thousands";
    public static final String HUNDREDS = "hundreds";
    public static final String DOZENS = "dozens";
    public static final String ONES = "ones";

    //sport
    public static final String FIRST_TEN = "first_ten";
    public static final String FIRST_ONE = "first_one";
    public static final String BETWEEN_TEAMS = "between_teams";
    public static final String SECOND_TEN = "second_ten";
    public static final String SECOND_ONE = "second_one";

    // decimal
    public static final String POINTS = "points";
    public static final String P2 = "point2";
    public static final String P3 = "point3";
    public static final String P4 = "point4";
    public static final String P5 = "point5";
    public static final String P6 = "point6";
    public static final String P7 = "point7";
    public static final String P8 = "point8";
    public static final String P9 = "point9";
    public static final String P10 = "point10";

    private NumberHelper() {}

    public static final String[] INT_COLS_THOUSAND = new String[]{THOUSANDS, HUNDREDS, DOZENS, "ones"};
    public static final String[] INT_COLS_MILLION = new String[] {HUNDRED_THOUSANDS, TEN_THOUSANDS, THOUSANDS, HUNDREDS, DOZENS, "ones"};

    public static final String [] INT_COLS_BIG = new String[] {"hundred billions", "ten billions", "billions", "hundred millions", "ten millions", "millions",
            HUNDRED_THOUSANDS, TEN_THOUSANDS, THOUSANDS, HUNDREDS, DOZENS, ONES};

    public static final String [] DECIMAL_COLS_THOUSAND = new String[] {THOUSANDS, HUNDREDS, DOZENS, ONES, POINTS ,P2 ,P3, P4,
            P5, P6, P7, P8, P9, P10};

    public static final String [] DECIMAL_COLS_BIG = new String[] {"hundred billions", "ten billions","billions", "hundred millions","ten millions","millions",
            HUNDRED_THOUSANDS, TEN_THOUSANDS, THOUSANDS, HUNDREDS, DOZENS, ONES,POINTS ,P2 ,P3, P4,
            P5, P6, P7, P8, P9, P10};


    public static final String[] TIME_SPORT_COLS = new String[] {FIRST_TEN, FIRST_ONE, BETWEEN_TEAMS, SECOND_TEN, SECOND_ONE};

    public static final String ORDINAL_THOUSAND_PTRN = "^([1-9]\\.?\\d{3}|[1-9]\\d{0,2})\\.$"; //1.234. or 1. or 12. or 123.
    public static final String ORDINAL_MILLION_PTRN = "^[1-9]\\d{0,2}\\.\\d{3}\\.$";
    public static final String ORDINAL_BIG_PTRN = "^[1-9]\\d{0,2}(\\.\\d{3}){2,3}\\.$";

    public static final String CARDINAL_THOUSAND_PTRN = "^([1-9]\\.?\\d{3}|[1-9]\\d{0,2})$"; //1.234 or 1 or 12 or 123
    public static final String CARDINAL_MILLION_PTRN = "^[1-9]\\d{0,2}\\.\\d{3}$"; //1.234 or 12.345 or 123.456
    public static final String CARDINAL_BIG_PTRN = "^[1-9]\\d{0,2}(\\.\\d{3}){2,3}$";

    public static final String DECIMAL_THOUSAND_PTRN = "^([1-9]\\.?\\d{3}|[1-9]\\d{0,2}),\\d+$"; //1.123,4 or 1232,4 or 123,4 or 12,42345 or 1,489
    public static final String DECIMAL_BIG_PTRN = "^[1-9]\\d{0,2}\\.?(\\d{3}){1,3},\\d+$";

    public static final String FRACTION_PTRN = "^([1-9]\\d{0,2} ?)?([1-9]\\d*\\/([2-9]|[1-9]\\d+)|(??|???|???|??|??))$"; // 4/8 or ??? , etc.
    public static final String TIME_PTRN = "^(([01]?\\d|2[0-4])[:\\.][0-5]|0)\\d$"; // 01:55 or 01.55
    public static final String SPORT_PTRN = "^(?!1\\/2)([1-9]\\d?\\/[1-9]\\d?)$";

    public static final String LETTERS_PTRN = "^(?!^(R??V|SPRON|\\-|\\.)$)[\\-\\.A-Z????????????????????]{1,5}$";
    public static final String ROMAN_LETTERS_PTRN = "[IVXLCDM]{5,20}";

    public static final String SYMBOL_PTRN = "^[^A-Z????????????????????a-z????????????????????\\d]$";

    public static final Map<String, String> DIGIT_NUMBERS = new HashMap<>();
    static {
        DIGIT_NUMBERS.put("0", " n??ll");
        DIGIT_NUMBERS.put("1", " einn");
        DIGIT_NUMBERS.put("2", " tveir");
        DIGIT_NUMBERS.put("3", " ??r??r");
        DIGIT_NUMBERS.put("4", " fj??rir");
        DIGIT_NUMBERS.put("5", " fimm");
        DIGIT_NUMBERS.put("6", " sex");
        DIGIT_NUMBERS.put("7", " sj??");
        DIGIT_NUMBERS.put("8", " ??tta");
        DIGIT_NUMBERS.put("9", " n??u");
        // DIGIT_NUMBERS.put("\\-", " <sil>"); not sure about this, without context difficult to say if it should be "sil" or not
        DIGIT_NUMBERS.put("\\-", "");
        DIGIT_NUMBERS.put("\\+", " pl??s");
       // DIGIT_NUMBERS.put("\\.", " punktur"); if we have more sentences being normalized, this replaces end-of-sentence dot as well. We don't want that
        DIGIT_NUMBERS.put(":", " tv??punktur");
       // DIGIT_NUMBERS.put(",", " komma"); converts normal sentence commas, ask what this is supposed to do
        DIGIT_NUMBERS.put("\\/", " sk??strik");
    }

    public static final Map<String, String> DIGITS_ORD = new HashMap<>();
    static {
        DIGITS_ORD.put("1", "fyrsta");
        DIGITS_ORD.put("2", "annan");
        DIGITS_ORD.put("3", "??ri??ja");
        DIGITS_ORD.put("4", "fj??r??a");
        DIGITS_ORD.put("5", "fimmta");
        DIGITS_ORD.put("6", "sj??tta");
        DIGITS_ORD.put("7", "sj??unda");
        DIGITS_ORD.put("8", "??ttunda");
        DIGITS_ORD.put("9", "n??unda");
        }

    public static final Map<String ,String> WLINK_NUMBERS = new HashMap<>();
    static {
        WLINK_NUMBERS.put("0", "n??ll");
        WLINK_NUMBERS.put("1", "einn");
        WLINK_NUMBERS.put("2", "tveir");
        WLINK_NUMBERS.put("3", "??r??r");
        WLINK_NUMBERS.put("4", "fj??rir");
        WLINK_NUMBERS.put("5", "fimm");
        WLINK_NUMBERS.put("6", "sex");
        WLINK_NUMBERS.put("7", "sj??");
        WLINK_NUMBERS.put("8", "??tta");
        WLINK_NUMBERS.put("9", "n??u");
        WLINK_NUMBERS.put("\\.", "punktur");
        WLINK_NUMBERS.put("\\-", "bandstrik");
        WLINK_NUMBERS.put("\\/", "sk??strik");
        WLINK_NUMBERS.put("_", "undirstrik");
        WLINK_NUMBERS.put("@", "hj??");
        WLINK_NUMBERS.put(":", "tv??punktur");
        WLINK_NUMBERS.put("=", "jafnt og");
        WLINK_NUMBERS.put("\\?", "spurningarmerki");
        WLINK_NUMBERS.put("!", "upphr??punarmerki");
        WLINK_NUMBERS.put("&", "og");
        WLINK_NUMBERS.put("%", "pr??sent");
        WLINK_NUMBERS.put("#", "myllumerki");
    }
}


