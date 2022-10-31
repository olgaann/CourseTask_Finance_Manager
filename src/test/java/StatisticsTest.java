import org.json.simple.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.*;

public class StatisticsTest {
    static List<Statistics> statisticsList = new ArrayList<>();
    Date currentDate = new Date();

    @DisplayName("Тестирование метода getMapCategories()")
    @Test
    void getMapCategoriesTest() {
        Map<String, String> map = new HashMap<>();
        File file = new File("test.tsv");
        map.put("мыло", "быт");
        map.put("акции", "финансы");
        map.put("шапка", "одежда");
        map.put("сок", "напитки");

        Assertions.assertEquals(map, Statistics.getMapCategories(file));

    }

    @DisplayName("Тестирование метода maxCategory()")
    @Test
    void  maxCategoryTest() {
        Purchase  purchase1 = new Purchase("мыло", currentDate, 80);
        Purchase  purchase2 = new Purchase("булка", currentDate, 40);
        Purchase  purchase3 = new Purchase("сухарики", currentDate, 40);
        Purchase  purchase4 = new Purchase("сок", currentDate, 30);

        statisticsList.add(new Statistics(purchase1));
        statisticsList.add(new Statistics(purchase2));
        statisticsList.add(new Statistics(purchase3));
        statisticsList.add(new Statistics(purchase4));

        System.out.println(statisticsList);

        JSONObject obj = new JSONObject();
        JSONObject obj2 = new JSONObject();
        obj.put("sum", 80);
        obj.put("categories", "еда, быт");

        obj2.put("maxCategory", obj);

        System.out.println(obj2);

        Assertions.assertEquals(obj2, Statistics.maxCategory(statisticsList));

        statisticsList.remove(0);
        statisticsList.remove(0);
        System.out.println(statisticsList);

        JSONObject obj3 = new JSONObject();
        JSONObject obj4 = new JSONObject();
        obj3.put("sum", 40);
        obj3.put("category", "еда");
        obj4.put("maxCategory", obj3);

        System.out.println(obj4);
        Assertions.assertEquals(obj4, Statistics.maxCategory(statisticsList));

    }
}

