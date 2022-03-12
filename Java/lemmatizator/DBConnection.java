import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class DBConnection {

    private static Connection connection;

    private static String dbName = "search_engine";
    private static String dbUser = "sanya";
    private static String dbPass = "passWd1984!";
    private static HashSet<String> setQuery;
    private static StringBuilder builderQuery;

    static {
        clearQuery();
    }

    public synchronized static Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/" + dbName +
                                "?user=" + dbUser + "&password=" + dbPass);
                connection.createStatement().execute("DROP TABLE IF EXISTS page");
                connection.createStatement().execute("DROP TABLE IF EXISTS field");
                connection.createStatement().execute("DROP TABLE IF EXISTS lemma");
                connection.createStatement().execute("DROP TABLE IF EXISTS index_lemma");
                connection.createStatement().execute("CREATE TABLE page(" +
                        "id INT NOT NULL AUTO_INCREMENT, " +
                        "path TEXT NOT NULL, " +
                        "code INT NOT NULL, " +
                        "content MEDIUMTEXT NOT NULL, " +
                        "PRIMARY KEY(id), " +
                        "INDEX (path(200)))");
                connection.createStatement().execute("CREATE TABLE field(" +
                        "id INT NOT NULL AUTO_INCREMENT, " +
                        "name VARCHAR(255) NOT NULL, " +
                        "selector VARCHAR(255) NOT NULL, " +
                        "weight FLOAT NOT NULL, " +
                        "PRIMARY KEY(id), " +
                        "INDEX (name(255)))");
                connection.createStatement().execute("CREATE TABLE lemma(" +
                        "id INT NOT NULL AUTO_INCREMENT, " +
                        "lemma VARCHAR(255) NOT NULL, " +
                        "frequency INT NOT NULL, " +
                        "PRIMARY KEY(id), " +
                        "UNIQUE KEY (lemma(255)))");
                connection.createStatement().execute("CREATE TABLE index_lemma(" +
                        "id INT NOT NULL AUTO_INCREMENT, " +
                        "page_id INT NOT NULL, " +
                        "lemma_id INT NOT NULL, " +
                        "rank_lemma FLOAT NOT NULL, " +
                        "PRIMARY KEY(id))");
                connection.createStatement().execute("INSERT INTO field(name, selector, weight) " +
                        "VALUES('title', 'title', 1.0), ('body', 'body', 0.8)");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }

    public synchronized static void append(String path, int statusCode, String content) throws SQLException {
        setQuery.add(path);
        if (builderQuery.length() > 1) {
            builderQuery.append(", ");
        }
        builderQuery.append("(\"" + path + "\", " + statusCode + ", \"" + content + "\")");
        if (setQuery.size() > 1000) {
            String query = builderQuery.toString();
            getConnection().createStatement().execute(query);
            clearQuery();
        }
    }

    public synchronized static boolean isExistPath(String path) throws SQLException {
        if (setQuery.contains(path)) {
            return true;
        }
        String query = "SELECT path FROM page WHERE path = \"" + path + "\"";
        try (ResultSet rs = getConnection().createStatement().executeQuery(query)) {
            return rs.next();
        }
    }

    public static List<String> getFields() {
        List<String> result = new ArrayList<>();
        String query = "SELECT selector FROM field";
        try (ResultSet rs = getConnection().createStatement().executeQuery(query)) {
            while (rs.next()) {
                result.add(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public synchronized static void addLemms(List<String> lemmas) {
        StringBuilder builderLemmasQuery = new StringBuilder("INSERT INTO lemma(lemma, frequency) VALUES");
        lemmas.forEach(lemma->{
            if (builderLemmasQuery.length() > 42) {
                builderLemmasQuery.append(", ");
            }
            builderLemmasQuery.append("(\"" + lemma + "\", 1)");
        });
        builderLemmasQuery.append(" ON DUPLICATE KEY UPDATE frequency=frequency + 1");
        String query = builderLemmasQuery.toString();
        try {
            getConnection().createStatement().execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void clearQuery() {
        builderQuery = new StringBuilder("INSERT INTO page(path, code, content) VALUES");
        setQuery = new HashSet<>();
    }
}
