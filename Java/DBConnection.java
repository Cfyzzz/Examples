import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class DBConnection {

    private static Connection connection;

    private static String dbName = "project";
    private static String dbUser = "user";
    private static String dbPass = "password";

    public static Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/" + dbName +
                        "?user=" + dbUser + "&password=" + dbPass);
                connection.createStatement().execute("DROP TABLE IF EXISTS voter_count");
                connection.createStatement().execute("CREATE TABLE voter_count(" +
                    "id INT NOT NULL AUTO_INCREMENT, " +
                    "name TINYTEXT NOT NULL, " +
                    "birthDate TINYTEXT NOT NULL, " +
                    "PRIMARY KEY(id))");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }

    public static void countVoters(List<Voter> voters) throws SQLException {
        if (voters.size() == 0) {
            return;
        }

        StringBuilder builderVoters = new StringBuilder("INSERT INTO voter_count(name, birthDate) VALUES");
        int count = 1;
        for (Voter voter : voters) {
            builderVoters.append("('" + voter.getName() + "', '" + voter.getBirthDay() + "')");
            builderVoters.append((count++ < voters.size()) ? ", ": "");
        }

        DBConnection.getConnection().createStatement().execute(builderVoters.toString());
    }

    public static void printVoterCounts() throws SQLException {
        String sql = "SELECT name, birthDate, COUNT(*) count FROM voter_count GROUP BY name, birthDate HAVING COUNT(*) > 1";
        ResultSet rs = DBConnection.getConnection().createStatement().executeQuery(sql);
        while (rs.next()) {
            System.out.println("\t" + rs.getString("name") + " (" +
                rs.getString("birthDate") + ") - " + rs.getInt("count"));
        }
    }
}
