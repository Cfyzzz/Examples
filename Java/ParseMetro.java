import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        try {
//            Document doc = getDocumentByUrl("https://skillbox-java.github.io/");
            Document doc = getDocumentByPath("data/code.html");

            JSONObject metroJSON = getMetroObject(doc);

            try (FileWriter file = new FileWriter("data/map.json")) {
                metroJSON.writeJSONString(file);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        createReport("data/map.json");
    }

    private static JSONObject getMetroObject(Document doc) {
        Elements linesMetro = doc.select(".js-metro-line");
        Elements stationsMetro = doc.select(".t-metrostation-list-table");

        List<String[]> lines = linesMetro.stream()
                .map(line -> new String[]{line.attr("data-line"), line.text()})
                .toList();
        List<String[]> stations = stationsMetro.stream()
                .flatMap(
                        line -> line.children()
                                .select(".name")
                                .stream()
                                .map(e -> new String[]{e.text(), line.attr("data-line")}))
                .toList();

        JSONArray cennectionsJSON = getCrossStations(stationsMetro);

        JSONArray linesJSON = new JSONArray();
        for (var line : lines) {
            JSONObject lineJSON = new JSONObject();
            lineJSON.put("number", line[0]);
            lineJSON.put("name", line[1]);
            linesJSON.add(lineJSON);
        }

        Map<String, List<String>> stationsMap = new HashMap<>();
        for (var station : stations) {
            if (stationsMap.containsKey(station[1])) {
                stationsMap.get(station[1]).add(station[0]);
            } else {
                var elem = new ArrayList<String>();
                elem.add(station[0]);
                stationsMap.put(station[1], elem);
            }
        }

        JSONObject stationsJSON = new JSONObject();
        for (var station : stationsMap.entrySet()) {
            JSONArray stationsOfLine = new JSONArray();
            stationsOfLine.addAll(station.getValue());
            stationsJSON.put(station.getKey(), stationsOfLine);
        }

        JSONObject metroJSON = new JSONObject();
        metroJSON.put("lines", linesJSON);
        metroJSON.put("stations", stationsJSON);
        metroJSON.put("connections", cennectionsJSON);
        return metroJSON;
    }

    private static JSONArray getCrossStations(Elements stationsMetro) {
        JSONArray connectionsJSON = new JSONArray();
        for (var stationMetro : stationsMetro) {
            Elements stationsCrossMetro = stationMetro.select(".single-station");
            for (var station : stationsCrossMetro) {
                String name = station.select(".name").text();

                JSONArray connectJSON = new JSONArray();

                JSONObject stationOnConnectionJSON = new JSONObject();
                stationOnConnectionJSON.put("line", stationMetro.attr("data-line"));
                stationOnConnectionJSON.put("station", name);
                connectJSON.add(stationOnConnectionJSON);

                for (var st : station.select(".t-icon-metroln")) {
                    stationOnConnectionJSON = new JSONObject();
                    stationOnConnectionJSON.put("line", st.attributes().get("class").substring(18));
                    stationOnConnectionJSON.put("station", st.attr("title").substring(19));
                    connectJSON.add(stationOnConnectionJSON);
                }

                if (connectJSON.size() > 1) {
                    connectionsJSON.add(connectJSON);
                }
            }
        }
        return connectionsJSON;
    }

    private static Document getDocumentByUrl(String url) throws IOException {
        return Jsoup.connect(url).maxBodySize(0).get();
    }

    private static Document getDocumentByPath(String path) throws IOException {
        StringBuilder builder = new StringBuilder();
        List<String> linesCode = Files.readAllLines(Path.of(path));
        linesCode.forEach(builder::append);
        return Jsoup.parse(builder.toString());
    }

    private static void createReport(String path) {
        try {
            JSONParser parser = new JSONParser();
            JSONObject jsonData = (JSONObject) parser.parse(getJsonFile(path));

            JSONObject stationsObject = (JSONObject) jsonData.get("stations");
            parseStations(stationsObject);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static String getJsonFile(String path) {
        StringBuilder builder = new StringBuilder();
        try {
            List<String> lines = Files.readAllLines(Paths.get(path));
            lines.forEach(line -> builder.append(line));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return builder.toString();
    }

    private static void parseStations(JSONObject stationsObject) {
        stationsObject.forEach((lineNumberObject, stationsArray) ->
        {
            String lineName = (String) lineNumberObject;
            int countStations = ((JSONArray) stationsArray).size();
            System.out.printf("%s: %d%n", lineName, countStations);
        });
    }
}
