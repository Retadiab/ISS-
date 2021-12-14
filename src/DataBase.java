import javax.swing.text.html.parser.Parser;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DataBase {
    Statement statement;
    ResultSet resultSet;
    Query query;

    public void connectToDataBase() {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/passwordsdb", "root", "basel321654987basel");

            statement = connection.createStatement();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public String executeQuery(ClientInfo clientInfo, String queryName) {
        query = new Query(clientInfo);

        String result = "error";
        try {
            if (queryName.equals("addAccount") || queryName.equals("addUser")||queryName.equals("deleteAccount")) {
                int value = statement.executeUpdate(query.queryList.get(queryName));
                result = String.valueOf(value);
            } else {
                resultSet = statement.executeQuery(query.queryList.get(queryName));
                while (resultSet.next()) {
                    result = resultSet.getString(1);
                }
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result;
    }
}

