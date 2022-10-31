import org.json.simple.JSONObject;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Statistics {
    static List<Statistics> statisticsList = new ArrayList<>();
    File file = new File("categories.tsv");

    private String category;
    private Purchase purchase;

    public Statistics(Purchase purchase) {
        this.purchase = purchase;
        if (getMapCategories(file).containsKey(purchase.getTitle())) {
            this.category = getMapCategories(file).get(purchase.getTitle());
        } else {
            this.category = "другое";
        }
        statisticsList.add(this);

    }

    public String getCategory() {
        return category;
    }

    public Purchase getPurchase() {
        return purchase;
    }

    static JSONObject maxCategory(List<Statistics> statisticsList) {
        Map<String, Integer> map = new HashMap<>();

        for (Statistics statistics : statisticsList) {
            String key = statistics.getCategory();
            if (map.containsKey(key)) {
                int value = map.get(key);
                value += statistics.getPurchase().getSum();
                map.put(key, value);
            } else {
                int value = statistics.getPurchase().getSum();
                map.put(key, value);
            }
        }

        int maxSum = Collections.max(map.values()); //ищем в мапе максимальное значение суммы покупок
        //формируем список категорий, соотвествующих максимальному значению покупок
        List<String> listOfMax = map.entrySet().stream()
                .filter(entry -> entry.getValue() == maxSum)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());


        JSONObject objIn = new JSONObject();
        JSONObject objOut = new JSONObject();
        objIn.put("sum", maxSum);
        if (listOfMax.size() == 1) {
            objIn.put("category", listOfMax.get(0));
        } else {
            String fewMaxCategories = listOfMax.get(0);
            for (int i = 1; i < listOfMax.size(); i++) {
                fewMaxCategories = fewMaxCategories + ", " + listOfMax.get(i);
            }
            objIn.put("categories", fewMaxCategories);
        }

        objOut.put("maxCategory", objIn);

        return objOut;
    }

    static Map<String, String> getMapCategories(File file) { //метод получения мапы (покупка=категория) из файла categories.tsv
        Map<String, String> map = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] arr = line.split("\t");
                map.put(arr[0], arr[1]);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return map;
    }

    @Override
    public String toString() {
        return "Statistics{" +
                "category='" + category + '\'' +
                ", purchase=" + purchase +
                '}';
    }
}
