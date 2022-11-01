import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDate;
import java.util.Date;
import java.util.Scanner;

public class Purchase {
    private static final int PORT = 8989;
    private static final String HOST = "localhost";
    static Scanner scan = new Scanner(System.in);
    static GsonBuilder builder = new GsonBuilder();


    private String title;
    private LocalDate date;
    private int sum;

    public Purchase(String title, LocalDate date, int sum) {
        this.title = title;
        this.date = date;
        this.sum = sum;
    }

    public int getSum() {
        return sum;
    }

    public String getTitle() {
        return title;
    }

    public LocalDate getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "Purchase{" +
                "title='" + title + '\'' +
                ", date=" + date +
                ", sum=" + sum +
                '}';
    }

    public JSONObject convertPurchaseToJsonObj() { //конвертирует объект Purchase в json-объект
        JSONObject object = new JSONObject();
        object.put("sum", this.getSum());
        object.put("title", this.getTitle());
        String date = String.valueOf(this.getDate());
        String correctFormatDate = date.replaceAll("-", ".");
        object.put("date", correctFormatDate);

        return object;
    }

    public static void main(String[] args) { //клиент

        while (true) {

            try (Socket clientSocket = new Socket(HOST, PORT);
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {


                System.out.println("Введите название покупки. Для завершения введите 'end'");
                String product = scan.nextLine();
                if (product.equals("end")) {
                    break;
                }
                int sum;
                while (true) {
                    System.out.println("Введите сумму покупки:");
                    String inputSum = scan.nextLine();
                    try {
                        sum = (Integer.parseInt(inputSum));
                        break;
                    } catch (NumberFormatException exception) {
                        System.out.println("Сумма покупки должна быть целым числом.");
                    }
                }

                LocalDate localeDate = LocalDate.now();
                Purchase purchase = new Purchase(product, localeDate, sum);
                out.println(purchase.convertPurchaseToJsonObj());
                System.out.println("ответ сервера: " + in.readLine());

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }
}
