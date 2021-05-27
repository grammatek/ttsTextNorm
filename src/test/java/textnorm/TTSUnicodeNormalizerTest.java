package textnorm;

import org.junit.Test;
import static org.junit.Assert.*;

public class TTSUnicodeNormalizerTest {

    @Test
    public void unicodeNormalizingTest() {
        TTSUnicodeNormalizer normalizer = new TTSUnicodeNormalizer();
        String input = "„ Við vorum samheldnir og þéttir og það er gott að innbyrða sigur á útivelli gegn öflugu liði eins og Breiðabliki , “ sagði Willum Þór Þórsson";
        String normalized = normalizer.normalize_encoding(input);
        assertEquals("\" Við vorum samheldnir og þéttir og það er gott að innbyrða sigur á útivelli gegn öflugu liði eins og Breiðabliki , \" sagði Willum Þór Þórsson", normalized);

        input = "sem hefur gert henni kleyft að nýta sýningarrými – og rými almennt – með nýjum hætti";
        normalized = normalizer.normalize_encoding(input);
        assertEquals("sem hefur gert henni kleyft að nýta sýningarrými - og rými almennt - með nýjum hætti", normalized);
    }
}
