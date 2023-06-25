package org.fhir.server;

import com.google.gson.JsonIOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fhir.resources.CoverageResource;
import org.fhir.resources.OrganizationResource;
import org.fhir.resources.PatientResource;
import org.fhir.resources.QuestionnaireResponseResource;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


import static org.utility.Utilities.convertResultSetToOrganizationsJson;
import static org.utility.Utilities.convertResultSetToPatientsJson;
import static org.utility.Utilities.convertResultSetToQuestionnairesJson;
import static org.utility.Utilities.convertResultSetToCoverageJson;
import static org.utility.Utilities.convertResultSetToCoveragesJson;
import static org.utility.Utilities.getTelecom;
import static org.utility.Utilities.convertResultSetToQuestionnaireResponseJson;
import static org.utility.Utilities.convertResultSetToQuestionnaireJson;
import static org.utility.Utilities.convertResultSetToQuestionnaireResponsesJson;
import static org.utility.Utilities.convertResultSetToPatientJson;
import static org.utility.Utilities.convertResultSetToOrganizationJson;
import static org.utility.Utilities.jsonArrayToString;
import static org.utility.Utilities.generateQuestionnaireResponseSql;


public class DatabaseHandler {
    private static final Logger LOGGER = LogManager.getLogger(DatabaseHandler.class);
    private final static String DB_URL_AWS = "***";
    private final static String DB_USER = "***";
    private final static String DB_PASSWORD = "***";
    private final static String SQL_ALL = "*";
    private final static String TABLE_PATIENT = "patient";
    private final static String TABLE_ORGANIZATION = "organization";
    private final static String TABLE_COVERAGE = "coverage";
    private final static String TABLE_QUESTIONNAIRE = "questionnaire";
    private final static String TABLE_QUESTIONNAIRE_RESPONSE = "questionnaire_response";
    private final static String TABLE_QUESTION = "question";
    private static final List<String> existingTables = Arrays.asList("patient", "coverage", "organization", "coverage", "questionnaire", "questionnaireresponse");
    private static final List<String> addibleResources = Arrays.asList("patient", "coverage", "organization", "coverage", "questionnaireresponse");
    private static final String EMPTY_JSON = "[]";
    private static final String KEY_RESOURCE_TYPE = "resourceType";
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    public static String makePatientGroupRequest(boolean withInactiveItems) {
        String groupJsonString = EMPTY_JSON;
        try (Connection connection = DriverManager.getConnection(DB_URL_AWS, DB_USER, DB_PASSWORD)) {
            LOGGER.info("Connection to '{}' established.", DB_URL_AWS);
            PreparedStatement preparedStatement;
            if (!withInactiveItems) {
                preparedStatement = connection.prepareStatement(
                        "SELECT " + SQL_ALL + " FROM " + TABLE_PATIENT + " where active = 't'");
            } else {
                preparedStatement = connection.prepareStatement(
                        "SELECT " + SQL_ALL + " FROM " + TABLE_PATIENT);
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            groupJsonString = convertResultSetToPatientsJson(resultSet);
        } catch (SQLException e) {
            LOGGER.error("{} – while making patient group request: {}", e, e.getMessage());
        }
        return groupJsonString;
    }

    public static String makeOrganizationGroupRequest(boolean withInactiveItems) {
        String groupJsonString = EMPTY_JSON;
        try (Connection connection = DriverManager.getConnection(DB_URL_AWS, DB_USER, DB_PASSWORD)) {
            LOGGER.info("Connection to '{}' established.", DB_URL_AWS);
            PreparedStatement preparedStatement;
            if (!withInactiveItems) {
                preparedStatement = connection.prepareStatement(
                        "SELECT " + SQL_ALL + " FROM " + TABLE_ORGANIZATION + " where active = 't'");
            } else {
                preparedStatement = connection.prepareStatement(
                        "SELECT " + SQL_ALL + " FROM " + TABLE_ORGANIZATION);
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            groupJsonString = convertResultSetToOrganizationsJson(resultSet);
        } catch (SQLException e) {
            LOGGER.error("{} – while making organization group request: {}", e, e.getMessage());
        }
        return groupJsonString;
    }

    public static String makeQuestionnaireGroupRequest(boolean withInactiveItems) {
        String groupJsonString = EMPTY_JSON;
        try (Connection connection = DriverManager.getConnection(DB_URL_AWS, DB_USER, DB_PASSWORD)) {
            LOGGER.info("Connection to '{}' established.", DB_URL_AWS);
            PreparedStatement preparedStatement;
            if (!withInactiveItems) {
                preparedStatement = connection.prepareStatement(
                        "SELECT " + SQL_ALL + " FROM " + TABLE_QUESTIONNAIRE + " where status = 'active'");
            } else {
                preparedStatement = connection.prepareStatement(
                        "SELECT " + SQL_ALL + " FROM " + TABLE_ORGANIZATION);
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            groupJsonString = convertResultSetToQuestionnairesJson(resultSet);
        } catch (SQLException e) {
            LOGGER.error("{} – while making organization group request: {}", e, e.getMessage());
        }
        return groupJsonString;
    }

    public static String makeCoverageGroupRequest(boolean withInactiveItems) {
        String groupJsonString = EMPTY_JSON;
        try (Connection connection = DriverManager.getConnection(DB_URL_AWS, DB_USER, DB_PASSWORD)) {
            LOGGER.info("Connection to '{}' established.", DB_URL_AWS);
            PreparedStatement preparedStatement;
            if (!withInactiveItems) {
                preparedStatement = connection.prepareStatement(
                        "SELECT " + SQL_ALL + " FROM " + TABLE_COVERAGE + " where status = 'active'");
            } else {
                preparedStatement = connection.prepareStatement(
                        "SELECT " + SQL_ALL + " FROM " + TABLE_COVERAGE);
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            groupJsonString = convertResultSetToCoveragesJson(resultSet);
        } catch (SQLException e) {
            LOGGER.error("{} – while making organization group request: {}", e, e.getMessage());
        }
        return groupJsonString;
    }

    public static String makeQuestionnaireResponseGroupRequest(boolean withInactiveItems) {
        String groupJsonString = EMPTY_JSON;
        try (Connection connection = DriverManager.getConnection(DB_URL_AWS, DB_USER, DB_PASSWORD)) {
            LOGGER.info("Connection to '{}' established.", DB_URL_AWS);
            PreparedStatement preparedStatement;
            if (!withInactiveItems) {
                preparedStatement = connection.prepareStatement(
                        "SELECT " + SQL_ALL + " FROM " + TABLE_QUESTIONNAIRE_RESPONSE + " where status = 'completed'");
            } else {
                preparedStatement = connection.prepareStatement(
                        "SELECT " + SQL_ALL + " FROM " + TABLE_QUESTIONNAIRE_RESPONSE);
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            groupJsonString = convertResultSetToQuestionnaireResponsesJson(resultSet);
        } catch (SQLException e) {
            LOGGER.error("{} – while making organization group request: {}", e, e.getMessage());
        }
        return groupJsonString;
    }


    public static String makeGroupRequest(String resource) {
        boolean withInactiveItems = false;
        if (resourceType(resource) == 2) {
            resource = resource.substring(0, resource.length() - 3);
            withInactiveItems = true;
        }
        return switch (resource) {
            case "patient" -> makePatientGroupRequest(withInactiveItems);
            case "organization" -> makeOrganizationGroupRequest(withInactiveItems);
            case "questionnaire" -> makeQuestionnaireGroupRequest(withInactiveItems);
            case "coverage" -> makeCoverageGroupRequest(withInactiveItems);
            case "questionnaireresponse" -> makeQuestionnaireResponseGroupRequest(withInactiveItems);
            default -> EMPTY_JSON;
        };
    }

    public static int resourceType(String resource) {
        List<String> allExistingResources = new LinkedList<>();
        for (String existingResource : existingTables) {
            allExistingResources.add(existingResource + "All");
        }
        if (existingTables.contains(resource)) {
            return 1;
        }
        if (allExistingResources.contains(resource)) {
            return 2;
        }
        return 0;
    }

    public static boolean isResource(String resource) {
        return resourceType(resource) != 0;
    }


    public static String makeGroupIdRequest(String resource, String itemId) {
        if (resourceType(resource) == 2) {
            resource = resource.substring(0, resource.length() - 3);
        }
        if (resource.equals("patient")) {
            return makePatientGroupIdRequest(itemId);
        }
        if (resource.equals("organization")) {
            return makeOrganizationGroupIdRequest(itemId);
        }
        if (resource.equals("coverage")) {
            return makeCoverageGroupIdRequest(itemId);
        }
        if (resource.equals("questionnaire")) {
            return makeQuestionnaireGroupIdRequest(itemId);
        }
        if (resource.equals("questionnaireresponse")) {
            return makeQuestionnaireResponseGroupIdRequest(itemId);
        }
        return "";
    }

    public static String makePatientGroupIdRequest(String itemId) {
        String groupJsonString = "";
        try (Connection connection = DriverManager.getConnection(DB_URL_AWS, DB_USER, DB_PASSWORD)) {
            LOGGER.info("Connection to '{}' established.", DB_URL_AWS);
            PreparedStatement preparedStatement;
            preparedStatement = connection.prepareStatement(
                    "SELECT " + SQL_ALL + " FROM " + TABLE_PATIENT + " WHERE id=" + itemId);
            ResultSet resultSet = preparedStatement.executeQuery();
            groupJsonString = convertResultSetToPatientJson(resultSet);
        } catch (SQLException e) {
            LOGGER.error("{} – while making patient id request: {}", e, e.getMessage());
        }
        return groupJsonString;
    }

    public static String makeOrganizationGroupIdRequest(String itemId) {
        String groupJsonString = "";
        try (Connection connection = DriverManager.getConnection(DB_URL_AWS, DB_USER, DB_PASSWORD)) {
            LOGGER.info("Connection to '{}' established.", DB_URL_AWS);
            PreparedStatement preparedStatement;
            preparedStatement = connection.prepareStatement(
                    "SELECT " + SQL_ALL + " FROM " + TABLE_ORGANIZATION + " WHERE id=" + itemId);
            ResultSet resultSet = preparedStatement.executeQuery();
            groupJsonString = convertResultSetToOrganizationJson(resultSet);
        } catch (SQLException e) {
            LOGGER.error("{} – while making patient id request: {}", e, e.getMessage());
        }
        return groupJsonString;
    }

    public static String makeCoverageGroupIdRequest(String itemId) {
        String groupJsonString = "";
        try (Connection connection = DriverManager.getConnection(DB_URL_AWS, DB_USER, DB_PASSWORD)) {
            LOGGER.info("Connection to '{}' established.", DB_URL_AWS);
            PreparedStatement preparedStatement;
            preparedStatement = connection.prepareStatement(
                    "SELECT " + SQL_ALL + " FROM " + TABLE_COVERAGE + " WHERE id=" + itemId);
            ResultSet resultSet = preparedStatement.executeQuery();
            groupJsonString = convertResultSetToCoverageJson(resultSet);
        } catch (SQLException e) {
            LOGGER.error("{} – while making patient id request: {}", e, e.getMessage());
        }
        return groupJsonString;
    }

    public static String makeQuestionnaireGroupIdRequest(String itemId) {
        String groupJsonString = "";
        try (Connection connection = DriverManager.getConnection(DB_URL_AWS, DB_USER, DB_PASSWORD)) {
            LOGGER.info("Connection to '{}' established.", DB_URL_AWS);
            PreparedStatement preparedStatement;
            preparedStatement = connection.prepareStatement(
                    "SELECT " + SQL_ALL + " FROM " + TABLE_QUESTIONNAIRE + " WHERE id=" + itemId);
            ResultSet resultSet = preparedStatement.executeQuery();
            groupJsonString = convertResultSetToQuestionnaireJson(resultSet);
        } catch (SQLException e) {
            LOGGER.error("{} – while making patient id request: {}", e, e.getMessage());
        }
        return groupJsonString;
    }

    public static String makeQuestionnaireResponseGroupIdRequest(String itemId) {
        String groupJsonString = "";
        try (Connection connection = DriverManager.getConnection(DB_URL_AWS, DB_USER, DB_PASSWORD)) {
            LOGGER.info("Connection to '{}' established.", DB_URL_AWS);
            PreparedStatement preparedStatement;
            preparedStatement = connection.prepareStatement(
                    "SELECT " + SQL_ALL + " FROM " + TABLE_QUESTIONNAIRE_RESPONSE + " WHERE id=" + itemId);
            ResultSet resultSet = preparedStatement.executeQuery();
            groupJsonString = convertResultSetToQuestionnaireResponseJson(resultSet);
        } catch (SQLException e) {
            LOGGER.error("{} – while making patient id request: {}", e, e.getMessage());
        }
        return groupJsonString;
    }

    public static String makePatientSearchRequest(String searchKey, String searchValue) {
        String groupJsonString = "";
        int keyColumnNumber = PatientResource.resourceFields.indexOf(searchKey);
        if (keyColumnNumber == -1) {
            LOGGER.info("Patient with {} = {} not found.", searchKey, searchValue);
            return groupJsonString;
        }
        try (Connection connection = DriverManager.getConnection(DB_URL_AWS, DB_USER, DB_PASSWORD)) {
            LOGGER.info("Connection to '{}' established.", DB_URL_AWS);
            PreparedStatement preparedStatement;
            preparedStatement = connection.prepareStatement(
                    "SELECT " + SQL_ALL + " FROM " + TABLE_PATIENT + " WHERE " + PatientResource.DB_PATIENT_COL_NAME.get(keyColumnNumber) + "='" + searchValue + "'");
            ResultSet resultSet = preparedStatement.executeQuery();
            groupJsonString = convertResultSetToPatientsJson(resultSet);
        } catch (SQLException e) {
            LOGGER.error("{} – while making group id request: {}", e, e.getMessage());
        }
        return groupJsonString;
    }

    public static String makeOrganizationSearchRequest(String searchKey, String searchValue) {
        String groupJsonString = "";
        int keyColumnNumber = OrganizationResource.resourceFields.indexOf(searchKey);
        if (keyColumnNumber == -1) {
            LOGGER.info("Organization with {} = {} not found.", searchKey, searchValue);
            return groupJsonString;
        }
        try (Connection connection = DriverManager.getConnection(DB_URL_AWS, DB_USER, DB_PASSWORD)) {
            LOGGER.info("Connection to '{}' established.", DB_URL_AWS);
            PreparedStatement preparedStatement;
            preparedStatement = connection.prepareStatement(
                    "SELECT " + SQL_ALL + " FROM " + TABLE_ORGANIZATION + " WHERE " + OrganizationResource.DB_ORGANIZATION_COL_NAME.get(keyColumnNumber) + "='" + searchValue + "'");
            ResultSet resultSet = preparedStatement.executeQuery();
            groupJsonString = convertResultSetToOrganizationsJson(resultSet);
        } catch (SQLException e) {
            LOGGER.error("{} – while making group id request: {}", e, e.getMessage());
        }
        return groupJsonString;
    }

    public static String makeQuestionnaireResponseSearchRequest(String searchKey, String searchValue) {
        String groupJsonString = "";
        int keyColumnNumber = QuestionnaireResponseResource.resourceFields.indexOf(searchKey);
        if (keyColumnNumber == -1) {
            LOGGER.info("Organization with {} = {} not found.", searchKey, searchValue);
            return groupJsonString;
        }
        try (Connection connection = DriverManager.getConnection(DB_URL_AWS, DB_USER, DB_PASSWORD)) {
            LOGGER.info("Connection to '{}' established.", DB_URL_AWS);
            PreparedStatement preparedStatement;
            preparedStatement = connection.prepareStatement(
                    "SELECT " + SQL_ALL + " FROM " + TABLE_QUESTIONNAIRE_RESPONSE + " WHERE " + searchKey + "='" + searchValue + "'");
            ResultSet resultSet = preparedStatement.executeQuery();
            groupJsonString = convertResultSetToQuestionnaireResponsesJson(resultSet);
        } catch (SQLException e) {
            LOGGER.error("{} – while making group id request: {}", e, e.getMessage());
        }
        return groupJsonString;
    }

    public static String makeCoverageSearchRequest(String searchKey, String searchValue) {
        String groupJsonString = "";
        int keyColumnNumber = CoverageResource.resourceFields.indexOf(searchKey);
        if (keyColumnNumber == -1) {
            LOGGER.info("Coverage with {} = {} not found.", searchKey, searchValue);
            return groupJsonString;
        }
        try (Connection connection = DriverManager.getConnection(DB_URL_AWS, DB_USER, DB_PASSWORD)) {
            LOGGER.info("Connection to '{}' established.", DB_URL_AWS);
            PreparedStatement preparedStatement;
            System.out.println("SELECT " + SQL_ALL + " FROM " + TABLE_COVERAGE + " WHERE " + searchKey + "='" + searchValue + "'");
            preparedStatement = connection.prepareStatement(
                    "SELECT " + SQL_ALL + " FROM " + TABLE_COVERAGE + " WHERE " + searchKey + "='" + searchValue + "'");
            ResultSet resultSet = preparedStatement.executeQuery();
            groupJsonString = convertResultSetToCoveragesJson(resultSet);
        } catch (SQLException e) {
            LOGGER.error("{} – while making group id request: {}", e, e.getMessage());
        }
        return groupJsonString;
    }

    public static long addResourceToDatabase(JSONObject jsonObject, String claimedResourceType) {

        String actualResourceType = "";
        try {
            actualResourceType = jsonObject.getString(KEY_RESOURCE_TYPE);
        } catch (JSONException e) {
            LOGGER.error("Resource did not have '{}'.", KEY_RESOURCE_TYPE);
        }
        claimedResourceType = claimedResourceType.toLowerCase();
        actualResourceType = actualResourceType.toLowerCase();
        if (!addibleResources.contains(claimedResourceType) || !claimedResourceType.equals(actualResourceType)) {
            return -1;
        }
        return switch (claimedResourceType) {
            case "patient" -> addPatientResource(jsonObject);
            case "organization" -> addOrganizationResource(jsonObject);
            case "coverage" -> addCoverageResource(jsonObject);
            case "questionnaireresponse" -> addQuestionnaireresponseResource(jsonObject);
            default -> -1;
        };
    }

    private static long addPatientResource(JSONObject jsonObject) {
        long generatedId = -1;
        String lastUpdated = dtf.format(LocalDateTime.now(ZoneId.of("Europe/Berlin"))) + ZoneId.systemDefault().getRules().getOffset(Instant.now());
        String givenNames = jsonArrayToString(jsonObject.getJSONArray("name").getJSONObject(1).getJSONArray("given").toString());
        String[] telecom = getTelecom(jsonObject.getJSONArray("telecom"));

        try (Connection connection = DriverManager.getConnection(DB_URL_AWS, DB_USER, DB_PASSWORD)) {
            LOGGER.info("Connection to '{}' established.", DB_URL_AWS);
            PreparedStatement preparedStatement;
            preparedStatement = connection.prepareStatement("INSERT INTO " + TABLE_PATIENT +
                    "(name, surname, birthdate, gender, address_line, address_city, address_postal_code, email, phone_number, last_updated, active) VALUES" +
                    "(?,?,?,?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, givenNames);
            preparedStatement.setString(2, jsonObject.getJSONArray("name").getJSONObject(0).getString("family"));
            preparedStatement.setString(3, jsonObject.getString("birthDate"));
            preparedStatement.setString(4, jsonObject.getString("gender"));
            preparedStatement.setString(5, jsonObject.getJSONArray("address").getJSONObject(0)
                    .getJSONArray("line").getString(0));
            preparedStatement.setString(6, jsonObject.getJSONArray("address").getJSONObject(0)
                    .getString("city"));
            preparedStatement.setString(7, jsonObject.getJSONArray("address").getJSONObject(0)
                    .getString("postalCode"));
            preparedStatement.setString(8, telecom[1]);
            preparedStatement.setString(9, telecom[0]);
            preparedStatement.setString(10, lastUpdated);
            preparedStatement.setBoolean(11, true);
            preparedStatement.execute();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                generatedId = generatedKeys.getLong(1);
            }
        } catch (SQLException e) {
            LOGGER.error("{} – while adding patient: {}", e, e.getMessage());
        }
        return generatedId;
    }

    private static long addOrganizationResource(JSONObject jsonObject) {
        long generatedId = -1;
        try (Connection connection = DriverManager.getConnection(DB_URL_AWS, DB_USER, DB_PASSWORD)) {
            LOGGER.info("Connection to '{}' established.", DB_URL_AWS);
            PreparedStatement preparedStatement;
            preparedStatement = connection.prepareStatement("INSERT INTO " + TABLE_ORGANIZATION +
                    "(name, active) VALUES(?, ?)", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, jsonObject.getString("name"));
            preparedStatement.setBoolean(2, true);
            preparedStatement.execute();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                generatedId = generatedKeys.getLong(1);
            }
        } catch (SQLException e) {
            LOGGER.error("{} – while adding organization: {}", e, e.getMessage());
        }
        return generatedId;
    }

    private static long addCoverageResource(JSONObject jsonObject) {
        long generatedId = -1;
        try (Connection connection = DriverManager.getConnection(DB_URL_AWS, DB_USER, DB_PASSWORD)) {
            LOGGER.info("Connection to '{}' established.", DB_URL_AWS);
            PreparedStatement preparedStatement;
            preparedStatement = connection.prepareStatement("INSERT INTO " + TABLE_COVERAGE +
                    "(status, subscriberid, beneficiary, payor) VALUES(?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, jsonObject.getString("status"));
            preparedStatement.setString(2, jsonObject.getString("subscriberId"));
            preparedStatement.setString(3, jsonObject.getJSONObject("beneficiary").getString("reference"));
            preparedStatement.setString(4, jsonObject.getJSONArray("payor").getJSONObject(0).getString("reference"));
            preparedStatement.execute();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                generatedId = generatedKeys.getLong(1);
            }
        } catch (SQLException e) {
            LOGGER.error("{} – while adding organization: {}", e, e.getMessage());
        }
        return generatedId;
    }

    private static long addQuestionnaireresponseResource(JSONObject jsonObject) {
        int numberOfAnswers = 0;
        try {
            numberOfAnswers = jsonObject.getJSONArray("item").length();
        } catch (JsonIOException e) {
            LOGGER.error("While getting json array length: {}", e.getMessage());
        }
        String sqlCommand = generateQuestionnaireResponseSql(numberOfAnswers);
        long generatedId = -1;
        try (Connection connection = DriverManager.getConnection(DB_URL_AWS, DB_USER, DB_PASSWORD)) {
            LOGGER.info("Connection to '{}' established.", DB_URL_AWS);
            PreparedStatement preparedStatement;
            preparedStatement = connection.prepareStatement(sqlCommand, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, jsonObject.getString("questionnaire"));
            preparedStatement.setString(2, jsonObject.getString("status"));
            preparedStatement.setString(3, jsonObject.getJSONObject("subject").getString("reference"));
            for (int i = 0; i < numberOfAnswers; i++) {
                preparedStatement.setString(i + 4, jsonObject.getJSONArray("item").getJSONObject(i).
                        getJSONArray("answer").getJSONObject(0).getString("valueString"));
            }
            preparedStatement.execute();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                generatedId = generatedKeys.getLong(1);
            }
        } catch (SQLException e) {
            LOGGER.error("{} – while adding organization: {}", e, e.getMessage());
        }
        return generatedId;
    }

    public static String makeSearchRequest(String resource, String parameter, String value) {
        if (value.equals("")) {
            return EMPTY_JSON;
        }
        String answer = "";
        switch (resource) {
            case "patient" -> answer = makePatientSearchRequest(parameter, value);
            case "organization" -> answer = makeOrganizationSearchRequest(parameter, value);
            case "questionnaireresponse" -> answer = makeQuestionnaireResponseSearchRequest(parameter, value);
            case "coverage" -> answer = makeCoverageSearchRequest(parameter, value);
            default -> LOGGER.info("No search request for '{}' possible.", resource);
        }
        if (answer.equals("")) {
            answer = "[]";
        }
        return answer;
    }

    /**
     * public static long addQuestion(String text) {
     * long generatedId = -1;
     * try (Connection connection = DriverManager.getConnection(DB_URL_AWS, DB_USER, DB_PASSWORD)) {
     * LOGGER.info("Connection to '{}' established.", DB_URL_AWS);
     * PreparedStatement preparedStatement;
     * preparedStatement = connection.prepareStatement("INSERT INTO " + TABLE_QUESTION +
     * "(text) VALUES(?)", Statement.RETURN_GENERATED_KEYS);
     * preparedStatement.setString(1, text);
     * preparedStatement.execute();
     * ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
     * if (generatedKeys.next()) {
     * generatedId = generatedKeys.getLong(1);
     * }
     * } catch (SQLException e) {
     * LOGGER.error("{} – while adding organization: {}", e, e.getMessage());
     * }
     * return generatedId;
     * }
     * <p>
     * public static String getQuestionFromDb(long id) {
     * String text = "";
     * try (Connection connection = DriverManager.getConnection(DB_URL_AWS, DB_USER, DB_PASSWORD)) {
     * LOGGER.info("Connection to '{}' established.", DB_URL_AWS);
     * PreparedStatement preparedStatement;
     * preparedStatement = connection.prepareStatement(
     * "SELECT " + SQL_ALL + " FROM " + TABLE_QUESTION + " WHERE id='" + id + "'");
     * ResultSet resultSet = preparedStatement.executeQuery();
     * while (resultSet.next()) {
     * text = resultSet.getString(2);
     * }
     * } catch (SQLException e) {
     * LOGGER.error("{} – while making group id request: {}", e, e.getMessage());
     * }
     * return text;
     * }
     */

    public static String[] getQuestionsFromDb(long[] ids) {
        String[] questions = new String[ids.length];
        String question = "";
        try (Connection connection = DriverManager.getConnection(DB_URL_AWS, DB_USER, DB_PASSWORD)) {
            for (int i = 0; i < ids.length; i++) {
                long id = ids[i];
                LOGGER.info("Connection to '{}' established.", DB_URL_AWS);
                PreparedStatement preparedStatement;
                preparedStatement = connection.prepareStatement(
                        "SELECT " + SQL_ALL + " FROM " + TABLE_QUESTION + " WHERE id='" + id + "'");
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    question = resultSet.getString(2);
                }
                questions[i] = question;
            }
        } catch (SQLException e) {
            LOGGER.error("{} – while making group id request: {}", e, e.getMessage());
        }
        return questions;
    }
}
