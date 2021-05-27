package textnorm;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class NormalizationManagerTest {

    @Test
    public void processTest() {
        String input = "Á samanlögðu svæði Vesturlands og Vestfjarða voru 4.400 gistinætur sem jafngildir 7,5% fækkun milli ára.";
        NormalizationManager manager = new NormalizationManager();
        String processed = manager.process(input);
        assertEquals("Á samanlögðu svæði Vesturlands og Vestfjarða voru fjögur þúsund og fjögur hundruð gistinætur sem jafngildir " +
                "sjö komma fimm prósent fækkun milli ára .", processed);
    }

    @Test
    public void processListTest() {
        NormalizationManager manager = new NormalizationManager();
        for (String sent : getTestSentences().keySet()) {
            String processed = manager.process(sent);
            assertEquals(getTestSentences().get(sent), processed);
        }
    }

    private Map<String, String> getTestSentences() {
        Map<String, String> testSentences = new HashMap<>();
        testSentences.put("þetta stóð í 4. gr. laga.",  "þetta stóð í fjórðu grein laga .");
        testSentences.put("Að jafnaði koma daglega um 48 rútur í Bláa Lónið .", "Að jafnaði koma daglega um fjörutíu og átta rútur í Bláa Lónið .");
        // should be sport domain, for now we can only set domain=sport manually (where the hyphen is replaced with a space instead of "til")
        testSentences.put("Áfram hélt fjörið í síðari hálfleik og þegar 3. leikhluti var tæplega hálfnaður var staðan 64-52 .",
                "Áfram hélt fjörið í síðari hálfleik og þegar þriðji leikhluti var tæplega hálfnaður var staðan sextíu og fjögur til fimmtíu og tvö .");
        testSentences.put("Viðeignin fer fram í sal FS og hefst klukkan 20 .", "Viðeignin fer fram í sal F S og hefst klukkan tuttugu ."); // spelling error!
        testSentences.put("Annars var verði 3500 fyrir tímann !", "Annars var verði þrjú þúsund og fimm hundruð fyrir tímann !"); // spelling error
        testSentences.put("Af því tilefni verða kynningar um allt land á hinum ýmsu deildum innann SL þriðjudaginn 18. janúar nk. ",
                "Af því tilefni verða kynningar um allt land á hinum ýmsu deildum innann S L þriðjudaginn átjánda janúar næstkomandi .");
        testSentences.put("„ Ég kíki daglega á facebook , karfan.is , vf.is , mbl.is , kkí , og utpabroncs.com . “ ",
                "\" Ég kíki daglega á facebook , karfan punktur is , v f punktur is , m b l punktur is , k k í , og utpabroncs punktur com . \"");
        testSentences.put("Arnór Ingvi Traustason 57. mín. Jónas Guðni, sem er 33 ára, hóf fótboltaferil sinn árið 2001.",
                "Arnór Ingvi Traustason fimmtugasta og sjöunda mínúta . Jónas Guðni , sem er þrjátíu og þriggja ára , hóf fótboltaferil sinn árið tvö þúsund og eitt .");
        // correct version impossible, since the tagger tags "lóð" and "byggingarreit" as dative, causing "í einni lóð og einum byggingarreit" (regina original does that as well)
        //testSentences.put("Einnig er gert ráð fyrir að sameina lóðir og byggingarreiti við Sjávarbraut 1 - 7 í 1 lóð og 1 byggingarreit.   Miðaverð fyrir fullorðna kr. 5500 ",
        //        "Einnig er gert ráð fyrir að sameina lóðir og byggingarreiti við Sjávarbraut eitt til sjö í eina lóð og einn byggingarreit . Miðaverð fyrir fullorðna krónur fimm þúsund og fimm hundruð .");
        testSentences.put("Einnig er gert ráð fyrir að sameina lóðir og byggingarreiti við Sjávarbraut 1 - 7 í 1 lóð og 1 byggingarreit.   Miðaverð fyrir fullorðna kr. 5500 ",
                "Einnig er gert ráð fyrir að sameina lóðir og byggingarreiti við Sjávarbraut eitt til sjö í einni lóð og einum byggingarreit . Miðaverð fyrir fullorðna krónur fimm þúsund og fimm hundruð .");
        testSentences.put("Stolt Sea Farm (SSF), fiskeldisarmur norsku skipa- og iðnaðarsamstæðunnar Stolt-Nielsen, jók sölu sína á flatfiski um 53% á öðrum ársfjórðungi 2015.",
                "Stolt Sea Farm ( S S F ) , fiskeldisarmur norsku skipa- og iðnaðarsamstæðunnar Stolt-Nielsen , jók sölu sína á flatfiski um fimmtíu og þrjú prósent á öðrum ársfjórðungi tvö þúsund og fimmtán .");
        testSentences.put("Í janúarbyrjun 1983 var stofnað nýtt hlutafélag, Víkurféttir ehf. sem tók við.",
                "Í janúarbyrjun nítján hundruð áttatíu og þrjú var stofnað nýtt hlutafélag , Víkurféttir E H F sem tók við .");
        testSentences.put("Jarðskjálfti að stærð 3,9 varð fyrir sunnan Kleifarvatn kl. 19:50 í gærkvöldi.",
                "Jarðskjálfti að stærð þrír komma níu varð fyrir sunnan Kleifarvatn klukkan nítján fimmtíu í gærkvöldi .");
        // can't interpret "söfnuðu krónum", same error in regina original
        //testSentences.put("Stelpurnar Carmen Diljá Guðbjarnardóttir og Elenora Rós Georgsdóttir söfnuðu 7.046 kr.",
        //        "Stelpurnar Carmen Diljá Guðbjarnardóttir og Elenora Rós Georgsdóttir söfnuðu sjö þúsund fjörutíu og sex krónum .");
        testSentences.put("Stelpurnar Carmen Diljá Guðbjarnardóttir og Elenora Rós Georgsdóttir söfnuðu 7.046 kr.",
                "Stelpurnar Carmen Diljá Guðbjarnardóttir og Elenora Rós Georgsdóttir söfnuðu sjö þúsund fjörutíu og sex krónur .");
        testSentences.put("Hann skoraði 21 stig og tók 12 fráköst.", "Hann skoraði tuttugu og eitt stig og tók tólf fráköst .");
        testSentences.put("Opna Suðurnesjamótið í pílu fer fram þann 4. desember nk. kl. 13:00 í píluaðstöðu Pílufélags Reykjanesbæjar að Hrannargötu 6. ",
                "Opna Suðurnesjamótið í pílu fer fram þann fjórða desember næstkomandi klukkan þrettán núll núll " +
                        "í píluaðstöðu Pílufélags Reykjanesbæjar að Hrannargötu sex .");
        testSentences.put("Karlar eru rétt innan við 2% hjúkrunarfræðinga á Íslandi",
                "Karlar eru rétt innan við tvö prósent hjúkrunarfræðinga á Íslandi .");
        // should be "sjö fimm sjö vélarnar" (same error in original regina)
        testSentences.put("Lendingarnar eru hlutfallslega svo fáar að elstu 757 -vélarnar eru aðeins hálfnaðar hvað líftíma varðar",
                "Lendingarnar eru hlutfallslega svo fáar að elstu sjö hundruð fimmtíu og sjö -vélarnar eru aðeins hálfnaðar hvað líftíma varðar .");
        testSentences.put("Á samanlögðu svæði Vesturlands og Vestfjarða voru 4.400 gistinætur sem jafngildir 7,5% fækkun milli ára.",
                "Á samanlögðu svæði Vesturlands og Vestfjarða voru fjögur þúsund og fjögur hundruð gistinætur sem jafngildir sjö komma fimm prósent fækkun milli ára .");

        return testSentences;
    }
}
