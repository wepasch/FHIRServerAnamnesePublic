package org.utility;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.fhir.resources.PatientResource;
import org.fhir.resources.OrganizationResource;
import org.fhir.resources.QuestionnaireResource;
import org.fhir.resources.QuestionnaireResponseResource;
import org.fhir.resources.CoverageResource;

import org.fhir.server.AnamneseServer;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.URLDecoder;

import java.nio.charset.StandardCharsets;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

import java.util.List;

import static org.fhir.server.DatabaseHandler.getQuestionsFromDb;

public class Utilities {
    private static final Logger LOGGER = LogManager.getLogger(Utilities.class);

    public static String convertResultSetToPatientsJson(ResultSet resultSet) {
        List<String> resources = new LinkedList<>();
        List<String[]> resultRows = new LinkedList<>();
        try {
            while (resultSet.next()) {
                String[] rowArray = new String[resultSet.getMetaData().getColumnCount()];
                for (int i = 0; i < rowArray.length; i++) {
                    rowArray[i] = resultSet.getString(i + 1);
                }
                resultRows.add(rowArray);
            }
        } catch (SQLException e) {
            LOGGER.error("{} – while converting result set to json string: {}", e, e.getMessage());
        }
        for (String[] resultRow : resultRows) {
            PatientResource patient = new PatientResource(resultRow);
            resources.add(patient.getJsonObjectString());
        }
        StringBuilder jsonArray = new StringBuilder("[");
        for (int i = 0; i < resources.size(); i++) {
            if (i != 0) {
                jsonArray.append(",");
            }
            jsonArray.append(resources.get(i));
        }
        jsonArray.append("]");
        return jsonArray.toString();
    }

    public static String convertResultSetToOrganizationsJson(ResultSet resultSet) {
        List<String> resources = new LinkedList<>();
        List<String[]> resultRows = new LinkedList<>();
        try {
            while (resultSet.next()) {
                String[] rowArray = new String[resultSet.getMetaData().getColumnCount()];
                for (int i = 0; i < rowArray.length; i++) {
                    rowArray[i] = resultSet.getString(i + 1);
                }
                resultRows.add(rowArray);
            }
        } catch (SQLException e) {
            LOGGER.error("{} – while converting result set to json string: {}", e, e.getMessage());
        }

        for (String[] resultRow : resultRows) {
            OrganizationResource organization = new OrganizationResource(resultRow);
            resources.add(organization.getJsonObjectString());
        }
        StringBuilder jsonArray = new StringBuilder("[");
        for (int i = 0; i < resources.size(); i++) {
            if (i != 0) {
                jsonArray.append(",");
            }
            jsonArray.append(resources.get(i));
        }
        jsonArray.append("]");
        return jsonArray.toString();
    }

    public static String convertResultSetToQuestionnairesJson(ResultSet resultSet) {
        List<String> resources = new LinkedList<>();
        List<String[]> resultRows = new LinkedList<>();
        try {
            while (resultSet.next()) {
                String[] rowArray = new String[resultSet.getMetaData().getColumnCount()];
                for (int i = 0; i < rowArray.length; i++) {
                    rowArray[i] = resultSet.getString(i + 1);
                }
                resultRows.add(rowArray);
            }
        } catch (SQLException e) {
            LOGGER.error("{} – while converting result set to json string: {}", e, e.getMessage());
        }

        for (String[] resultRow : resultRows) {
            QuestionnaireResource questionnaire = new QuestionnaireResource(resultRow);
            resources.add(questionnaire.getJsonObjectString());
        }
        StringBuilder jsonArray = new StringBuilder("[");
        for (int i = 0; i < resources.size(); i++) {
            if (i != 0) {
                jsonArray.append(",");
            }
            jsonArray.append(resources.get(i));
        }
        jsonArray.append("]");
        return jsonArray.toString();
    }

    public static String convertResultSetToQuestionnaireResponsesJson(ResultSet resultSet) {
        List<String> resources = new LinkedList<>();
        List<String[]> resultRows = new LinkedList<>();
        try {
            while (resultSet.next()) {
                String[] rowArray = new String[resultSet.getMetaData().getColumnCount()];
                for (int i = 0; i < rowArray.length; i++) {
                    rowArray[i] = resultSet.getString(i + 1);
                }
                resultRows.add(rowArray);
            }
        } catch (SQLException e) {
            LOGGER.error("{} – while converting result set to json string: {}", e, e.getMessage());
        }
        for (String[] resultRow : resultRows) {
            QuestionnaireResponseResource questionnaireResponse = new QuestionnaireResponseResource(resultRow);
            resources.add(questionnaireResponse.getJsonObjectString());
        }
        StringBuilder jsonArray = new StringBuilder("[");
        for (int i = 0; i < resources.size(); i++) {
            if (i != 0) {
                jsonArray.append(",");
            }
            jsonArray.append(resources.get(i));
        }
        jsonArray.append("]");
        return jsonArray.toString();
    }

    public static String convertResultSetToCoveragesJson(ResultSet resultSet) {
        List<String> resources = new LinkedList<>();
        List<String[]> resultRows = new LinkedList<>();
        try {
            while (resultSet.next()) {
                String[] rowArray = new String[resultSet.getMetaData().getColumnCount()];
                for (int i = 0; i < rowArray.length; i++) {
                    rowArray[i] = resultSet.getString(i + 1);
                }
                resultRows.add(rowArray);
            }
        } catch (SQLException e) {
            LOGGER.error("{} – while converting result set to json string: {}", e, e.getMessage());
        }

        for (String[] resultRow : resultRows) {
            CoverageResource coverage = new CoverageResource(resultRow);
            resources.add(coverage.getJsonObjectString());
        }
        StringBuilder jsonArray = new StringBuilder("[");
        for (int i = 0; i < resources.size(); i++) {
            if (i != 0) {
                jsonArray.append(",");
            }
            jsonArray.append(resources.get(i));
        }
        jsonArray.append("]");
        return jsonArray.toString();
    }

    public static String convertResultSetToQuestionnaireJson(ResultSet resultSet) {
        String jsonString = "";
        int resultCounter = 0;
        try {
            String[] dbArray = new String[resultSet.getMetaData().getColumnCount()];
            while (resultSet.next()) {
                if (resultCounter++ > 0) {
                    LOGGER.error("ResultSet for query with id is bigger than '1'.");
                    break;
                }
                for (int i = 0; i < dbArray.length; i++) {
                    dbArray[i] = resultSet.getString(i + 1);
                }
            }
            if (resultCounter == 0) {
                return jsonString;
            }
            if (dbArray.length == 4) {
                jsonString = new QuestionnaireResource(dbArray).getJsonObjectString();
            } else {
                jsonString = "";
            }
        } catch (SQLException e) {
            LOGGER.error("{} – while converting result set to json string: {}", e, e.getMessage());
        }
        return jsonString;
    }

    public static String convertResultSetToCoverageJson(ResultSet resultSet) {
        String jsonString = "";
        int resultCounter = 0;
        try {
            String[] dbArray = new String[resultSet.getMetaData().getColumnCount()];
            while (resultSet.next()) {
                if (resultCounter++ > 0) {
                    LOGGER.error("ResultSet for coverage query with id is bigger than '1'.");
                    break;
                }
                for (int i = 0; i < dbArray.length; i++) {
                    dbArray[i] = resultSet.getString(i + 1);
                }
            }
            if (resultCounter == 0) {
                return jsonString;
            }
            if (dbArray.length == 5) {
                jsonString = new CoverageResource(dbArray).getJsonObjectString();
            } else {
                jsonString = "";
            }
        } catch (SQLException e) {
            LOGGER.error("{} – while converting result set to json string: {}", e, e.getMessage());
        }
        return jsonString;
    }

    public static String convertResultSetToQuestionnaireResponseJson(ResultSet resultSet) {
        String jsonString = "";
        int resultCounter = 0;
        try {
            String[] dbArray = new String[resultSet.getMetaData().getColumnCount()];
            while (resultSet.next()) {
                if (resultCounter++ > 0) {
                    LOGGER.error("ResultSet for query with id is bigger than '1'.");
                    break;
                }
                for (int i = 0; i < dbArray.length; i++) {
                    dbArray[i] = resultSet.getString(i + 1);
                }
            }
            if (resultCounter == 0) {
                return jsonString;
            }
            if (dbArray.length == 14) {
                jsonString = new QuestionnaireResponseResource(dbArray).getJsonObjectString();
            } else {
                jsonString = "";
            }
        } catch (SQLException e) {
            LOGGER.error("{} – while converting result set to json string: {}", e, e.getMessage());
        }
        return jsonString;
    }

    public static String convertResultSetToPatientJson(ResultSet resultSet) {
        String jsonString = "";
        int resultCounter = 0;
        try {
            String[] dbArray = new String[resultSet.getMetaData().getColumnCount()];
            while (resultSet.next()) {
                if (resultCounter++ > 0) {
                    LOGGER.error("ResultSet for query with id is bigger than '1'.");
                    break;
                }
                for (int i = 0; i < dbArray.length; i++) {
                    dbArray[i] = resultSet.getString(i + 1);
                }
            }
            if (resultCounter == 0) {
                return jsonString;
            }
            if (dbArray.length == 12) {
                jsonString = new PatientResource(dbArray).getJsonObjectString();
            } else {
                jsonString = "";
            }
        } catch (SQLException e) {
            LOGGER.error("{} – while converting result set to json string: {}", e, e.getMessage());
        }
        return jsonString;
    }

    public static String convertResultSetToOrganizationJson(ResultSet resultSet) {
        String jsonString = "";
        int resultCounter = 0;
        try {
            String[] dbArray = new String[resultSet.getMetaData().getColumnCount()];
            while (resultSet.next()) {
                if (resultCounter++ > 0) {
                    LOGGER.error("ResultSet for query with id is bigger than '1'.");
                    break;
                }
                for (int i = 0; i < dbArray.length; i++) {
                    dbArray[i] = resultSet.getString(i + 1);
                }
            }
            if (resultCounter == 0) {
                return jsonString;
            }
            if (dbArray.length == 3) {
                jsonString = new OrganizationResource(dbArray).getJsonObjectString();
            } else {
                jsonString = "";
            }
        } catch (SQLException e) {
            LOGGER.error("{} – while converting result set to json string: {}", e, e.getMessage());
        }
        return jsonString;
    }

    public static String makeJsonPretty(String jsonString) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(jsonString);
        return gson.toJson(je);
    }

    public static String getHtmlAsString(String relPath) {
        String htmlRelPath = "HTML/" + relPath + ".html";
        LOGGER.info("Trying to fetch html with path '{}'.", htmlRelPath);
        return getFileAsString(htmlRelPath);
    }

    public static String getJsonAsString(String relPath) {
        String htmlRelPath = "JSON/" + relPath + ".json";
        LOGGER.info("Trying to fetch html with path '{}'.", htmlRelPath);
        return getFileAsString(htmlRelPath);
    }

    public static String getFileAsString(String relPath) {
        relPath = "/" + relPath;
        StringBuilder fileString = new StringBuilder();
        String currentLine;
        LOGGER.info("Trying to fetch file with path '{}'.", relPath);
        try (InputStream in = Utilities.class.getResourceAsStream(relPath)) {
            assert in != null;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
                while ((currentLine = reader.readLine()) != null) {
                    fileString.append(currentLine);
                    fileString.append("\n");
                }
            }
        } catch (Exception e) {
            LOGGER.error("{} – while getting html with path '{}' as String: {}", e, relPath, e.getMessage());
            return "";
        }
        return fileString.toString();
    }

    public static String getStringFollowing(String string, String delimiter) {
        final int followIndex = string.indexOf(delimiter);
        if (followIndex == -1) {
            return string;
        }
        return string.substring(followIndex + delimiter.length());
    }

    public static String[] deconstructUrl(String url) {
        if (url == null || url.equals("/" + AnamneseServer.SERVER_PATH_ADDITION)) {
            return new String[0];
        }
        url = getStringFollowing(url, AnamneseServer.SERVER_PATH_ADDITION);
        if (url.length() > 0 && url.charAt(0) == '/') {
            url = url.substring(1);
        }
        return url.split("/");
    }

    public static String getHtmlMethodNotForIndex(String method) {
        return "<p>Hauptseite nicht über <b>" + method + "</b>-Request erreichbar.<br><br></p>" +
                "<a href=\"http://" + AnamneseServer.serverUrl + "\"><b>Hauptseite</b></a>";
    }

    public static String getHtmlMethodNotForPage(String method, String page) {
        return "<p>Seite <b>" + page + "<b> nicht über <b>" + method + "</b>-Request erreichbar.<br><br></p>" +
                "<a href=\"http://" + AnamneseServer.serverUrl + "\"><b>Hauptseite</b></a>";
    }

    public static String getPageNotFoundFor(String urlEnding) {
        return getPageNotFoundText(AnamneseServer.serverUrl + "/" + urlEnding);
    }

    public static String getPageNotFoundFor(String[] urlEnding) {
        return getPageNotFoundText(arrayToUrl(urlEnding));
    }

    public static String getPageNotFoundText(String url) {
        return "<p>Seite <b>" + url + "</b> nicht gefunden.<br><br>" +
                "<a href=\"http://" + AnamneseServer.serverUrl + "\"><b>Hauptseite</b></a></p>";
    }

    public static String arrayToUrl(String[] array) {
        StringBuilder url = new StringBuilder(AnamneseServer.serverUrl);
        for (String s : array) {
            url.append("/");
            url.append(s);
        }
        return url.toString();
    }

    public static String jsonArrayToString(String array) {
        JSONArray jsonArray = new JSONArray(array);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < jsonArray.length(); i++) {
            if (i != 0) {
                sb.append(" ");
            }
            sb.append(jsonArray.get(i));
        }
        return sb.toString();
    }

    public static String[] getTelecom(JSONArray jsonArray) {
        String[] array = new String[2];
        for (int i = 0; i < jsonArray.length(); i++) {
            if (jsonArray.getJSONObject(i).getString("system").equals("phone")) {
                array[0] = jsonArray.getJSONObject(i).getString("value");
            } else if (jsonArray.getJSONObject(i).getString("system").equals("email")) {
                array[1] = jsonArray.getJSONObject(i).getString("value");
            }
        }
        return array;
    }


    public static long[] longArrayFromString(String string) {
        String[] strings = string.split(" ");
        int length = strings.length;
        long[] array = new long[length];
        try {
            for (int i = 0; i < length; i++) {
                array[i] = Long.parseLong(strings[i]);
            }
        } catch (NumberFormatException e) {
            LOGGER.error("While parsing '{}' to long: {}", string, e.getMessage());
        }
        return array;
    }

    public static String[] getQuestions(long[] ids) {
        return getQuestionsFromDb(ids);
    }

    public static String decodeUrl(String url) {
        String decoded;
        decoded = URLDecoder.decode(url, StandardCharsets.UTF_8);
        return decoded;
    }

    public static String[] decodeUrls(String[] urls) {
        for (int i = 0; i < urls.length; i++) {
            urls[i] = decodeUrl(urls[i]);
        }
        return urls;
    }


    public static String generateQuestionnaireResponseSql(int numberOfQuestions) {
        StringBuilder sb = new StringBuilder("INSERT INTO questionnaire_response (questionnaire, status, subject");
        for (int i = 1; i <= numberOfQuestions; i++) {
            sb.append(", answer");
            sb.append(i);
        }
        sb.append(") VALUES(?,?,?");
        sb.append(",?".repeat(Math.max(0, numberOfQuestions)));
        sb.append(")");
        return sb.toString();
    }
}
