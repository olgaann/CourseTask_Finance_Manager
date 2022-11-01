import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDate;

public class Main {
    private static final int PORT = 8989;


    public static void main(String[] args) {
        File bin = new File("data.bin");

        //внимание! красные предупреждения возникают из-за этого блока:
        try {
            if (!bin.createNewFile() || bin.length() != 0L) {
                Statistics.loadBinFromFile(bin);
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        try (ServerSocket serverSocket = new ServerSocket(PORT);) { // стартуем сервер один(!) раз
            System.out.println("Сервер стартует");

            while (true) { // в цикле(!) принимаем подключения

                try (
                        Socket socket = serverSocket.accept();
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        PrintWriter out = new PrintWriter(socket.getOutputStream());
                ) {

                    String jsonString = in.readLine(); //получаем json-строку от клиента
                    System.out.println(jsonString);
                    //пытаемся ее распарсить
                    JSONParser parser = new JSONParser();
                    Object object = parser.parse(jsonString);
                    JSONObject jsonObject = (JSONObject) object;


                    String title = (String) jsonObject.get("title");
                    long summ = (long) jsonObject.get("sum");
                    int sum = (int) summ;
                    String date = (String) jsonObject.get("date");
                    String[] arr = date.split("\\.");
                    int year = Integer.parseInt(arr[0]);
                    int month = Integer.parseInt(arr[1]);
                    int day = Integer.parseInt(arr[2]);

                    Purchase purchase = new Purchase(title, LocalDate.of(year, month, day), sum);
                    Statistics statistics = new Statistics(purchase);
                    statistics.autoSaveToJsonFile(bin);
                    //System.out.println((statistics.getPurchase().getDate()).get);
                    System.out.println(statistics);

                    //формируем ответ в виде json-объекта
                    JSONObject reply = Statistics.buildReply();
                    //отправляем ответ клиенту
                    out.println(reply);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException e) {
            System.out.println("Не могу стартовать сервер");
            e.printStackTrace();
        }
    }
}
