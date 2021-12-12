import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerForMultiUsers {
    static int IDCounter = 0;

    public static void main(String[] args) throws IOException {
        DataBase dataBase = new DataBase();
        dataBase.connectToDataBase();
        try (ServerSocket listener = new ServerSocket(11111)) {
            System.out.println("Server is running...");
            ExecutorService pool = Executors.newFixedThreadPool(10);
            while (true) {
                pool.execute(new Server(dataBase,listener.accept()));
            }
        }
    }
}

