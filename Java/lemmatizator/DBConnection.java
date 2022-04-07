package ru.nedovizin;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class DBConnection {

    private static Connection connection;

    private static final String dbName = "search_engine";
    private static final String dbUser = "sanya";
    private static final String dbPass = "passWd1984!";
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
                try (Statement statement = connection.createStatement()) {
                    statement.execute("DROP TABLE IF EXISTS page");
                    statement.execute("DROP TABLE IF EXISTS field");
                    statement.execute("DROP TABLE IF EXISTS lemma");
                    statement.execute("DROP TABLE IF EXISTS index_lemma");
                    statement.execute("DROP TABLE IF EXISTS site");
                    statement.execute("CREATE TABLE page(" +
                            "id INT NOT NULL AUTO_INCREMENT, " +
                            "path TEXT NOT NULL, " +
                            "code INT NOT NULL, " +
                            "content MEDIUMTEXT NOT NULL, " +
                            "site_id INT NOT NULL, " +
                            "PRIMARY KEY(id), " +
                            "INDEX (path(200)))");
                    statement.execute("CREATE TABLE field(" +
                            "id INT NOT NULL AUTO_INCREMENT, " +
                            "name VARCHAR(255) NOT NULL, " +
                            "selector VARCHAR(255) NOT NULL, " +
                            "weight FLOAT NOT NULL, " +
                            "PRIMARY KEY(id), " +
                            "INDEX (name(255)))");
                    statement.execute("CREATE TABLE lemma(" +
                            "id INT NOT NULL AUTO_INCREMENT, " +
                            "lemma VARCHAR(255) NOT NULL, " +
                            "frequency INT NOT NULL, " +
                            "site_id INT NOT NULL, " +
                            "PRIMARY KEY(id), " +
                            "UNIQUE KEY (lemma(255)))");
                    statement.execute("CREATE TABLE index_lemma(" +
                            "id INT NOT NULL AUTO_INCREMENT, " +
                            "page_id INT NOT NULL, " +
                            "lemma_id INT NOT NULL, " +
                            "rank_lemma FLOAT NOT NULL, " +
                            "PRIMARY KEY(id), " +
                            "UNIQUE KEY (page_id, lemma_id))");
                    statement.execute("CREATE TABLE site(" +
                            "id INT NOT NULL AUTO_INCREMENT, " +
                            "status ENUM('INDEXING', 'INDEXED', 'FAILED') NOT NULL, " +
                            "status_time DATETIME NOT NULL, " +
                            "last_error TEXT, " +
                            "url VARCHAR(255) NOT NULL, " +
                            "name VARCHAR(255) NOT NULL, " +
                            "PRIMARY KEY(id))");
                    statement.execute("INSERT INTO field(name, selector, weight) " +
                            "VALUES('title', 'title', 1.0), ('body', 'body', 0.8)");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }

    public synchronized static void append(String path, int statusCode, String content) throws SQLException {
        setQuery.add(path);
        if (setQuery.size() > 1) {
            builderQuery.append(", ");
        }
        builderQuery.append("(\"" + path + "\", " + statusCode + ", \"" + content + "\")");
        if (setQuery.size() > 100) {
            flushPages();
        }
    }

    public synchronized static boolean isExistPath(String path) throws SQLException {
        if (setQuery.contains(path)) {
            return true;
        }
        return isExistPathInDB(path);
    }

    private static boolean isExistPathInDB(String path) throws SQLException {
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

    public static void addLemmas(List<String> lemmas) {
        StringBuilder builderLemmasQuery = new StringBuilder("INSERT INTO lemma(lemma, frequency) VALUES");
        lemmas.forEach(lemma -> {
            if (builderLemmasQuery.length() > 42) {
                builderLemmasQuery.append(", ");
            }
            builderLemmasQuery.append("(\"" + lemma + "\", 1)");
        });
        builderLemmasQuery.append(" ON DUPLICATE KEY UPDATE frequency=frequency + 1");
        String query = builderLemmasQuery.toString();
        try {
            getConnection().createStatement().executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addIndexes(List<String> lemmas, String path, String field) {
        try {
            int idPage;
            float weightField;
            int idLemma;
            int frequency;

            if (!isExistPathInDB(path)) {
                flushPages();
            }

            String queryPage = "SELECT id FROM page WHERE path=\"" + path + "\" LIMIT 1";
            try (var rs = getConnection().createStatement().executeQuery(queryPage)) {
                if (rs.next()) {
                    idPage = rs.getInt(1);
                } else {
                    return;
                }
            }

            String queryField = "SELECT weight FROM field WHERE selector=\"" + field + "\" LIMIT 1";
            try (var rs = getConnection().createStatement().executeQuery(queryField)) {
                if (rs.next()) {
                    weightField = rs.getFloat(1);
                } else {
                    return;
                }
            }

            Iterator<String> iteratorLemmas = lemmas.iterator();
            HashMap<Integer, Float> lemmasFrequency = new HashMap<>();
            while (iteratorLemmas.hasNext()) {
                String queryLemma = "SELECT id, frequency FROM lemma WHERE lemma=\"" + iteratorLemmas.next() + "\" LIMIT 1";
                try (var rs = getConnection().createStatement().executeQuery(queryLemma)) {
                    if (rs.next()) {
                        idLemma = rs.getInt(1);
                        frequency = rs.getInt(2);
                    } else {
                        return;
                    }
                }

                lemmasFrequency.put(
                        idLemma,
                        lemmasFrequency.getOrDefault(idLemma, 0f) + frequency * weightField
                );
            }
            lemmasFrequency.forEach((idL, frequ) -> {
                StringBuilder builderIndex = new StringBuilder("INSERT INTO index_lemma(page_id, lemma_id, rank_lemma) VALUES");
                builderIndex.append("(" + idPage + ", " + idL + ", " + frequ * weightField + ")");
                builderIndex.append(" ON DUPLICATE KEY UPDATE rank_lemma=rank_lemma + " + frequ * weightField);
                try (Statement statement = getConnection().createStatement()) {
                    statement.execute(builderIndex.toString());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getSortedLemmasByFrequency(List<String> lemmas) throws SQLException {
        List<String> result = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT lemma, frequency FROM lemma WHERE lemma IN (");
        query.append(String.join(",", lemmas.stream().map(lemma -> "\"" + lemma + "\"").collect(Collectors.toList())));
        query.append(") ORDER BY frequency");
        try (ResultSet rs = getConnection().createStatement().executeQuery(query.toString())) {
            while (rs.next()) {
                result.add(rs.getString(1));
            }
            return result;
        }
    }

    public synchronized static List<Relative> getRelativeTable(List<String> lemmas) throws SQLException {
        List<Relative> result = new ArrayList<>();
        String queryIndex = getQueryIndexFromLemmas(lemmas);
        if (queryIndex.isEmpty()) {
            return result;
        }
        List<Integer> indexes = new ArrayList<>();
        try (ResultSet rs = getConnection().createStatement().executeQuery(queryIndex)) {
            while (rs.next()) {
                indexes.add(rs.getInt(2));
            }
        }
        if (indexes.size() == 0) {
            return result;
        }
        prepareTemporaryTables(indexes);
        String queryPagesRanks = getQueryPagesRanks();
        ResultSet rs = getConnection().createStatement().executeQuery(queryPagesRanks);
        try (rs) {
            while (rs.next()) {
                String uri = rs.getString(1);
                String content = rs.getString(2);
                float relevance = rs.getFloat(3);

                result.add(new Relative(uri, content, relevance));
            }
        }
        return result;
    }

    public synchronized static void addSite(String site, String name) {
        String queryUpdate = "INSERT INTO site(status, status_time, url, name) VALUES (" +
                "'INDEXING', " + LocalDateTime.now() + ", " + site + ", " + name + ")";
        String queryGetId = "SELECT id FROM site WHERE url = ? LIMIT 1";
        try (var statement = getConnection().createStatement()) {
            statement.executeUpdate(queryUpdate);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void flushPages() throws SQLException {
        String query = builderQuery.toString();
        try(Statement statement = getConnection().createStatement()){
            statement.execute(query);
            clearQuery();
        }
    }

    private static void clearQuery() {
        builderQuery = new StringBuilder("INSERT INTO page(path, code, content) VALUES");
        setQuery = new HashSet<>();
    }

    private static String getQueryIndexFromLemmas(List<String> lemmas) {
        boolean isFirst = true;
        String queryLast = "";
        int count = 0;
        for (String lemma : lemmas) {
            if (isFirst) {
                queryLast = "SELECT page.path, index_lemma.id FROM lemma " +
                        "JOIN index_lemma ON lemma.id=index_lemma.lemma_id " +
                        "JOIN page ON page.id=index_lemma.page_id " +
                        "WHERE lemma='" + lemma + "'";
                isFirst = false;
            } else {
                queryLast = "SELECT page.path, index_lemma.id FROM lemma " +
                        "JOIN index_lemma ON lemma.id=index_lemma.lemma_id " +
                        "JOIN page ON page.id=index_lemma.page_id " +
                        "WHERE lemma.lemma='" + lemma + "' AND page.path IN (SELECT path FROM (" +
                        queryLast +
                        ") t" + count + ")";
            }
            count++;
        }
        return queryLast;
    }
    
    private static void prepareTemporaryTables(List<Integer> indexes) throws SQLException {
        try (Statement statement = getConnection().createStatement()) {
            statement.execute("CREATE TEMPORARY TABLE t_abs_ranks " +
                    "SELECT page_id, SUM(rank_lemma) abs_rank " +
                    "FROM index_lemma WHERE id IN (" + indexes.stream().map(String::valueOf).collect(Collectors.joining(",")) + ") " +
                    "GROUP BY page_id; ");

            statement.execute("CREATE TEMPORARY TABLE t_max_ranks " +
                    "SELECT MAX(abs_rank) max_rank FROM t_abs_ranks; ");

            statement.execute("CREATE TEMPORARY TABLE t_relative " +
                    "SELECT t_abs_ranks.page_id, abs_rank, abs_rank/max_rank relevance " +
                    "FROM t_abs_ranks " +
                    "JOIN t_max_ranks " +
                    "ORDER BY relevance; ");
        }
    }

    private static String getQueryPagesRanks() {
        return "SELECT page.path, page.content, t_relative.relevance FROM page " +
                "RIGHT JOIN `t_relative` as t_relative ON t_relative.page_id=page.id ORDER BY t_relative.relevance DESC; ";
    }
}
