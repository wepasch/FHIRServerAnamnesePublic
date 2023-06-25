package org.fhir.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.util.IOUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import java.util.Arrays;
import java.util.List;

import static org.fhir.server.AnamneseServer.serverUrl;

import static org.fhir.server.DatabaseHandler.addResourceToDatabase;
import static org.fhir.server.DatabaseHandler.makeGroupIdRequest;
import static org.fhir.server.DatabaseHandler.makeSearchRequest;

import static org.utility.Utilities.decodeUrls;
import static org.utility.Utilities.deconstructUrl;
import static org.utility.Utilities.getHtmlAsString;
import static org.utility.Utilities.getHtmlMethodNotForPage;
import static org.utility.Utilities.getHtmlMethodNotForIndex;
import static org.utility.Utilities.arrayToUrl;
import static org.utility.Utilities.getPageNotFoundFor;
import static org.utility.Utilities.getJsonAsString;


public class HttpHandlerImpl implements HttpHandler {
    private static final Logger LOGGER = LogManager.getLogger(HttpHandlerImpl.class);
    private static final String API_MARKER = "api";
    private static final String QUESTIONMARK = "\\?";
    private static final String EQUALS_SIGN = "=";
    public static final int CODE_OK = 200;
    public static final int CODE_CREATED = 201;
    public static final int CODE_CLIENT_ERROR = 400;
    public static final int CODE_NOT_FOUND = 404;
    public static final int CODE_NOT_IMPL = 501;
    private final List<String> availablePages = Arrays.asList("index", "getPatient", "makeAnamnese", "getAnamnese");
    private static final List<String> blankJson = Arrays.asList("blankPatient", "blankCoverage", "blankOrganization", "blankQuestionnaire", "blankQuestionnaireResponse");

    @Override
    public void handle(HttpExchange httpExchange) {
        httpExchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        httpExchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST");
        httpExchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");

        String[] urlComponents = deconstructUrl(httpExchange.getRequestURI().toString());
        String requestMethod = httpExchange.getRequestMethod();
        LOGGER.info("Processing {} to {}.", requestMethod, httpExchange.getRequestURI().toString());

        if ("OPTIONS".equals(requestMethod)) {
            handleOptionsRequest(httpExchange);
            return;
        }

        if (urlComponents.length == 0) {
            if (requestMethod.equals("GET")) {
                handleResponse(httpExchange, getHtmlAsString("index"), CODE_OK);
            } else {
                handleResponse(httpExchange, getHtmlMethodNotForIndex(requestMethod), CODE_NOT_FOUND);
            }
            return;
        }

        if (!urlComponents[0].equals(API_MARKER)) {
            httpExchange.getResponseHeaders().add("Content-Type", "text/html; charset=UTF-8");
            if ("GET".equals(requestMethod)) {
                handleClientGet(httpExchange, urlComponents);
            } else {
                handleResponse(httpExchange, getHtmlMethodNotForPage(requestMethod, arrayToUrl(urlComponents)), CODE_NOT_FOUND);
            }
            return;
        }

        httpExchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
        urlComponents = Arrays.copyOfRange(urlComponents, 1, urlComponents.length);
        if ("GET".equals(requestMethod)) {
            handleApiGet(httpExchange, urlComponents);
        } else if ("POST".equals(requestMethod)) {
            handlePostRequest(httpExchange, urlComponents[0]);
        } else if ("PUT".equals(requestMethod)) {
            handlePutRequest(httpExchange);
        } else if ("DELETE".equals(requestMethod)) {
            handleDeleteRequest(httpExchange);
        } else {
            handleQMRequest(httpExchange);
        }
    }

    private static String makeResourceTypeRequest(String resource) {
        return DatabaseHandler.makeGroupRequest(resource);
    }


    private void handlePostRequest(HttpExchange httpExchange, String resourceType) {
        //handleResponse(httpExchange, "handledPostRequest of:\n" + resourceType, CODE_NOT_IMPL);
        String body = httpExchangeBodytoString(httpExchange.getRequestBody());
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(body);

        } catch (Exception e) {
            handleResponse(httpExchange, "", CODE_NOT_IMPL);
            return;
        }
        long generatedId = addResourceToDatabase(jsonObject, resourceType);
        resourceType = resourceType.substring(0, 1).toUpperCase() + resourceType.substring(1);
        if (generatedId > 0) {
            JSONObject jsonResponseObject = new JSONObject();
            jsonResponseObject.put("resourceType", resourceType);
            jsonResponseObject.put("generatedId", generatedId);
            httpExchange.getResponseHeaders().add("Location", serverUrl + "/api/" + resourceType + "/" + generatedId);
            handleResponse(httpExchange, jsonResponseObject.toString(), CODE_CREATED);
        } else {
            handleResponse(httpExchange, "", CODE_NOT_FOUND);
        }

    }

    private void handlePutRequest(HttpExchange httpExchange) {
        handleResponse(httpExchange, "handledPutRequest", CODE_NOT_IMPL);
    }

    private void handleDeleteRequest(HttpExchange httpExchange) {
        handleResponse(httpExchange, "handledDeleteRequest", CODE_NOT_IMPL);
    }

    private void handleQMRequest(HttpExchange httpExchange) {
        handleResponse(httpExchange, "handledQMRequest", CODE_NOT_IMPL);
    }

    private void handleResponse(HttpExchange httpExchange, String htmlBody, int statusCode) {
        flushResponse(httpExchange, htmlBody, statusCode);
    }

    private void flushResponse(HttpExchange httpExchange, String htmlResponse, int statusCode) {
        final int bodyByteLength = htmlResponse.getBytes().length;
        try (OutputStream outputStream = httpExchange.getResponseBody()) {
            httpExchange.sendResponseHeaders(statusCode, bodyByteLength);
            outputStream.write(htmlResponse.getBytes());
            outputStream.flush();
        } catch (IOException e) {
            LOGGER.error("{} – while sending html response: {}", e, e.getMessage());
        }

    }

    private void handleClientGet(HttpExchange httpExchange, String[] urlComponents) {
        if (urlComponents.length < 1) {
            handleResponse(httpExchange, getPageNotFoundFor(urlComponents), CODE_NOT_FOUND);
        }
        String requestedPage;
        if (urlComponents[0].equals("") || urlComponents[0].equals("main")) {
            requestedPage = "index";
        } else {
            requestedPage = urlComponents[0];
        }
        String htmlResponse;
        int statusCode;
        if (availablePages.contains(requestedPage)) {
            htmlResponse = getHtmlAsString(requestedPage);
            statusCode = CODE_OK;
        } else {
            htmlResponse = getPageNotFoundFor(new String[]{requestedPage});
            statusCode = CODE_NOT_FOUND;
        }
        handleResponse(httpExchange, htmlResponse, statusCode);
    }

    private void handleApiGet(HttpExchange httpExchange, String[] urlComponents) {
        if (urlComponents.length < 1 || urlComponents.length > 2) {
            handleResponse(httpExchange, "", CODE_CLIENT_ERROR);
        }
        String[] resourcePart = urlComponents[0].split(QUESTIONMARK);
        boolean isParameterRequest = (resourcePart.length == 2 && resourcePart[1].contains(EQUALS_SIGN));
        String resource = resourcePart[0];
        if (blankJson.contains(resource)) {
            handleResponse(httpExchange, getJsonAsString(resource), CODE_OK);
            return;
        }
        resource = resource.toLowerCase();

        if (!DatabaseHandler.isResource(resource)) {
            handleResponse(httpExchange, "<p>Ressourcentyp <b>" + resourcePart[0] + "</b> exisistiert nicht.</p>", CODE_NOT_FOUND);
            return;
        }
        if (urlComponents.length == 1 && resourcePart.length == 1) {
            LOGGER.info("Group request for '{}'.", resource);
            handleResponse(httpExchange, makeResourceTypeRequest(resource), CODE_OK);
        } else if (urlComponents.length == 2 && resourcePart.length == 1) {
            LOGGER.info("Id request for '{}/{}'.", resource, urlComponents[1]);
            handleResponse(httpExchange, makeGroupIdRequest(resource, urlComponents[1]), CODE_OK);
        } else if (isParameterRequest) {
            String[] parameters = resourcePart[1].split("=");
            if (parameters.length == 0) {
                parameters = new String[]{"", ""};
            } else if (parameters.length == 1) {
                parameters = new String[]{parameters[0], ""};
            }
            parameters[0] = parameters[0].toLowerCase();
            if (urlComponents.length == 2) {
                parameters[1] += "/" + urlComponents[1];
            }
            decodeUrls(parameters);
            LOGGER.info("Search request for '{}' with {}={}.", resource, parameters[0], parameters[1]);
            String body = makeSearchRequest(resource, parameters[0], parameters[1]);
            handleResponse(httpExchange, body, CODE_OK);
        } else {
            LOGGER.error("Request not available with {}.", Arrays.toString(urlComponents));
            handleResponse(httpExchange, "", CODE_NOT_IMPL);
        }
    }

    private void handleOptionsRequest(HttpExchange httpExchange) {
        handleResponse(httpExchange, "", CODE_OK);
    }


    private static String httpExchangeBodytoString(InputStream ip) {
        String body = "";
        try (Reader rd = new InputStreamReader(ip)) {
            body = IOUtils.toString(rd);
        } catch (IOException e) {
            LOGGER.error("{} – while reading body of post request: {}", e, e.getMessage());
        }
        return body;
    }


}