import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.simple.JSONObject;

import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


public class Statistics {
    static List<Statistics> statisticsList = new ArrayList<>();
    static File file = new File("categories.tsv");

    private String category;
    private Purchase purchase;

    public Statistics(Purchase purchase) throws IOException {
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

    static JSONObject maxCategory(String period, LocalDate purchaseLocalDate) {//метод формирует JSONObject {"sum":...,"category":"..."} за период, переданный параметром
        List<Statistics> filteredStatisticsList = new ArrayList<>(); //отфильтрованный список
        LocalDate today = LocalDate.now(); //это должна быть дата покупки, а не сегодняшняя

        switch (period) {
            case "all":
                filteredStatisticsList = statisticsList.stream().collect(Collectors.toList());
                break;
            case "year":
                filteredStatisticsList = statisticsList.stream()
                        .filter(statistics -> (statistics.getPurchase().getDate().getYear() == purchaseLocalDate.getYear()))
                        .collect(Collectors.toList());
                break;
            case "month":
                filteredStatisticsList = statisticsList.stream()
                        .filter(statistics -> (statistics.getPurchase().getDate().getYear() == purchaseLocalDate.getYear())
                                && (statistics.getPurchase().getDate().getMonth() == purchaseLocalDate.getMonth()))
                        .collect(Collectors.toList());
                break;
            case "day":
                filteredStatisticsList = statisticsList.stream()
                        .filter(statistics -> (statistics.getPurchase().getDate().getYear() == purchaseLocalDate.getYear())
                                && (statistics.getPurchase().getDate().getMonth() == purchaseLocalDate.getMonth())
                                && (statistics.getPurchase().getDate().getDayOfMonth() == purchaseLocalDate.getDayOfMonth()))
                        .collect(Collectors.toList());
                break;
        }


        Map<String, Integer> map = new HashMap<>();//мапа "category"="sum"
        JSONObject objIn = new JSONObject();

        if (filteredStatisticsList.size() > 0) {
            //заполняем мапу с помощью отфильтрованного списка:
            for (Statistics statistics : filteredStatisticsList) {
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


            objIn = new JSONObject();
            //формируем JSONObject
            objIn.put("sum", maxSum);
            if (listOfMax.size() == 1) {
                objIn.put("category", listOfMax.get(0));
            } else { //если несколько категорий имеют максимальную сумму:
                String fewMaxCategories = listOfMax.get(0);
                for (int i = 1; i < listOfMax.size(); i++) {
                    fewMaxCategories = fewMaxCategories + ", " + listOfMax.get(i);
                }
                objIn.put("categories", fewMaxCategories);
            }
        } else {
            objIn.put("sum", 0);
            objIn.put("category", "null");
        }

        return objIn;
    }

    static JSONObject buildReply(LocalDate purchaseLocalDate) {//метод формирует "общий" JSONObject, за все периоды

        JSONObject objOut = new JSONObject();

        JSONObject objIn1 = maxCategory("all", purchaseLocalDate);
        JSONObject objIn2 = maxCategory("year", purchaseLocalDate);
        JSONObject objIn3 = maxCategory("month", purchaseLocalDate);
        JSONObject objIn4 = maxCategory("day", purchaseLocalDate);

        objOut.put("maxCategory", objIn1);
        objOut.put("maxYearCategory", objIn2);
        objOut.put("maxMonthCategory", objIn3);
        objOut.put("maxDayCategory", objIn4);

        return objOut;
    }

    static Map<String, String> getMapCategories(File file) throws RuntimeException, IOException { //метод получения мапы (покупка=категория) из файла categories.tsv
        Map<String, String> map = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] arr = line.split("\t");
                map.put(arr[0], arr[1]);
            }
        }

        return map;
    }


    public void autoSaveToJsonFile(File bin) throws IOException { // метод автосохранения объекта Statistics в файл
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String json = gson.toJson(this); //конвертируем объект Statistics в json-строку

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(bin, true))) {
            writer.write(json + "\n");
        }
    }

    static void loadBinFromFile(File bin) throws IOException { // метод загрузки истории из файла
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        try (BufferedReader reader = new BufferedReader(new FileReader(bin))) {
            String json;
            while ((json = reader.readLine()) != null) {
                Statistics statistics = gson.fromJson(json, Statistics.class);
                statisticsList.add(statistics);
            }
        }
    }


    @Override
    public String toString() {
        return "Statistics{" +
                "category='" + category + '\'' +
                ", purchase=" + purchase +
                '}';
    }
}
