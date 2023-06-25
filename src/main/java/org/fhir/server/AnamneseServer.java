package org.fhir.server;

import com.sun.net.httpserver.HttpServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class AnamneseServer {
    private static final Logger LOGGER = LogManager.getLogger(AnamneseServer.class);
    public static final String SERVER_PATH_ADDITION = "FHIRAnamnese";
    private static final int N_THREADS = 10;
    public static String serverUrl = "<NO URL>";

    public static void main(String[] args) {
        checkArgs(args, 3);
        HttpServer server = setUpServer(args);
        if (server == null) {
            LOGGER.error("Server has not been set up.");
            return;
        }
        startServer(server, N_THREADS, Integer.parseInt(args[1]), args[0]);
        //waitForServerStop(server);
    }

    public static void startServer(HttpServer server, int nThreads, int port, String usedUrl) {
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(nThreads);
        String serverPathAddition = "/" + SERVER_PATH_ADDITION;
        server.createContext(serverPathAddition, new HttpHandlerImpl());
        server.setExecutor(threadPoolExecutor);
        server.start();
        if (usedUrl.equals("172.31.0.242")) {
            usedUrl = "13.53.138.202";
        }
        serverUrl = usedUrl + ":" + port + "/" + SERVER_PATH_ADDITION;
        LOGGER.info("Server started with port {}.", port);
        LOGGER.info("Server url: {}", serverUrl);
    }

    private static HttpServer setUpServer(String[] serverArgs) {
        int[] serverArgsInts = parseToInt(serverArgs, 1, 2);
        if (serverArgsInts == null) {
            LOGGER.error("Array with integer arguments for server was 'null'.");
            return null;
        }

        HttpServer server;
        try {
            server = HttpServer.create(
                    new InetSocketAddress(serverArgs[0], serverArgsInts[0]), serverArgsInts[1]);
        } catch (IOException e) {
            LOGGER.error("{} while creating Http-server: {}", e, e.getMessage());
            return null;
        }
        LOGGER.info("Server has been set up: {}{}{}", server.getAddress(), "/", SERVER_PATH_ADDITION);
        return server;
    }

    public static void checkArgs(String[] args, int numberOfArgs) {
        while (args.length != numberOfArgs) {
            LOGGER.error("{} arguments are required (given {}: {})." +
                    "Enter arguments via console or exit program.", numberOfArgs, args.length, Arrays.toString(args));
            args = getArguments(numberOfArgs);
        }
        LOGGER.info("Entered arguments: {}", Arrays.toString(args));
    }

    public static String[] getArguments(int numberOfArgs) {
        String[] args = new String[numberOfArgs];
        try (Scanner sc = new Scanner(System.in)) {
            for (int i = 0; i < args.length; i++) {
                System.out.println("Enter argument number " + (i + 1) + ":");
                args[i] = sc.next();
            }
        }
        return args;
    }

    public static int[] parseToInt(String[] input, int startIndex, int parseNumber) {
        if (input.length < (startIndex + parseNumber)) {
            LOGGER.error("Inputs' array too small. It has {} elements, but {} are needed.",
                    input.length, (startIndex + parseNumber));
            return null;
        }
        int[] output = new int[parseNumber];
        for (int i = 0; i < parseNumber; i++) {
            try {
                output[i] = Integer.parseInt(input[startIndex + i]);
            } catch (NumberFormatException e) {
                LOGGER.error("{} while parsing element number {} to int: {}", e, i, e.getMessage());
                return null;
            }
        }
        return output;
    }
}
