import org.json.simple.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public class StatisticsTest {


    @BeforeAll
    static void setUp() throws IOException {
        Purchase purchase1 = new Purchase("мыло", LocalDate.of(2021, 01, 02), 80);
        Purchase purchase2 = new Purchase("булка", LocalDate.now(), 40);
        Purchase purchase3 = new Purchase("сухарики", LocalDate.of(2022, 11, 01), 40);
        Purchase purchase4 = new Purchase("сок", LocalDate.now(), 30);
        new Statistics(purchase1);
        new Statistics(purchase2);
        new Statistics(purchase3);
        new Statistics(purchase4);
    }


    @DisplayName("Тестирование метода maxCategory()")
    @Test
    void maxCategoryTest() throws IOException {

        JSONObject objIn = new JSONObject();

        objIn.put("sum", 80);
        objIn.put("category", "еда");


        Assertions.assertEquals(objIn, Statistics.maxCategory("year"));

    }

    @DisplayName("Тестирование метода buildReply")
    @Test
    void buildReplyTest() {
        JSONObject objOut = new JSONObject();
        JSONObject objIn1 = new JSONObject(); // all;
        objIn1.put("sum", 80);
        objIn1.put("categories", "еда, быт");

        JSONObject objIn2 = new JSONObject(); // year;
        objIn2.put("sum", 80);
        objIn2.put("category", "еда");


        JSONObject objIn3 = new JSONObject(); // month;
        objIn3.put("sum", 80);
        objIn3.put("category", "еда");

        JSONObject objIn4 = new JSONObject(); // day;
        objIn4.put("sum", 40);
        objIn4.put("category", "еда");

        objOut.put("maxCategory", objIn1);
        objOut.put("maxYearCategory", objIn2);
        objOut.put("maxMonthCategory", objIn3);
        objOut.put("maxDayCategory", objIn4);

        Assertions.assertEquals(objOut, Statistics.buildReply());
    }


    @DisplayName("Тестирование метода getMapCategories()")
    @Test
    void getMapCategoriesTest() throws IOException {
        Map<String, String> map = new HashMap<>();
        File file = new File("test.tsv");
        map.put("мыло", "быт");
        map.put("акции", "финансы");
        map.put("шапка", "одежда");
        map.put("сок", "напитки");

        Assertions.assertEquals(map, Statistics.getMapCategories(file));

    }
}
