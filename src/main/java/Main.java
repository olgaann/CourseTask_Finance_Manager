import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.simple.JSONObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    private static final int PORT = 8989;
    //static File bin = new File("data.bin");

    public static void main(String[] args) {
        File bin = new File("data.bin");

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
                    //преобразуем ее в объект Purchase
                    GsonBuilder builder = new GsonBuilder();
                    Gson gson = builder.create();
                    Purchase purchase = gson.fromJson(jsonString, Purchase.class);
                    Statistics statistics = new Statistics(purchase);
                    statistics.autoSaveToJsonFile(bin);
                    System.out.println(statistics);

                    //формируем ответ в виде json-объекта
                    JSONObject reply = Statistics.maxCategory(Statistics.statisticsList);
                    //отправляем ответ клиенту
                    out.println(reply);
                }
            }
        } catch (IOException e) {
            System.out.println("Не могу стартовать сервер");
            e.printStackTrace();
        }
    }
}
