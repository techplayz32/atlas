package com.atlas;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.*;

public class atlasUtils {
    public static void printErrorException(Exception e) {
        throw new UnsupportedOperationException("\u001B[1;36mERROR:\u001B[0m " + e);
    }

    public static void printError(String e) {
        System.out.println("\u001B[1;36mERROR:\u001B[0m " + e);
    }

    public static void printLoadingAnimation(String text, int count, long delayMillis) {
        for (int i = 1; i <= count; i++) {
            StringBuilder loadingText = new StringBuilder(text);
            for (int j = 0; j < i; j++) {
                loadingText.append('.');
            }
            System.out.print("\r" + loadingText.toString()); // what
            try {
                Thread.sleep(delayMillis); // delay!!!
            } catch (InterruptedException e) {
                printErrorException(e);
            }
        }
        System.out.println(); // move to next line
    }

    public static String sendGetRequest(String urlString, Map<String, String> headers) throws Exception {
        // here, i will be yapping so i don't will explain why i made function in this way
        // read as much as you want. made for nerds :D
        URI uri = new URI(urlString); // basically some shit that i don't understand, but need exclude it in code
        URL url = uri.toURL(); // ok, so URI is transforming url or sorta this shit
        HttpURLConnection conn = (HttpURLConnection) url.openConnection(); // opens http connection to url
        conn.setRequestMethod("GET"); // and sending the request method
    
        if (headers != null) { // checking if headers are NOT empty
            for (Map.Entry<String, String> header : headers.entrySet()) { // goes through every headers in Map<String, String>
                // Map basically.. just checking if key and value or two strings in one line: for example, ("Authorization": "Bot token of bot")
                conn.setRequestProperty(header.getKey(), header.getValue()); // here, our connection setting up the headers as "properties"
            }
        }
    
        int responseCode = conn.getResponseCode(); // uh status code?
        if (responseCode == HttpURLConnection.HTTP_OK) { // it is means what it said here
            // BufferedReader, Simply put, it enables us to minimize the number of I/O operations by reading chunks of characters and storing them in an internal buffer
            // https://www.baeldung.com/java-buffered-reader
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream())); // basically, inputStream is like a pipe of data which obtained from process 
            // with which it is connected and processing right now
            String inputLine; // basically, inputLine checks if "in" is not null.
            // NOTE: inputLine is reading the lines in "in", bufferedReader and inputStream - and in this way, it is putting response from website
            // to the StringBuilder "response"
            StringBuilder response = new StringBuilder(); // well, the response of the website!!1
    
            while ((inputLine = in.readLine()) != null) { // read what i said previously
                response.append(inputLine);
            }
            in.close(); // then our inputStream with BufferedReader is closing like Scanner (or "input" from python)
            return response.toString(); // and it returns response from website.
        } else if (responseCode == 429) { 
            throw new Exception("\u001B[1;36mERROR:\u001B[0m rate limit exceeded");
        } else {
            throw new Exception("\u001B[1;36mERROR:\u001B[0m GET request failed with response code: " + responseCode);
        }
    }
    
    public static String sendPostRequest(String urlString, String jsonInputString, Map<String, String> headers) throws Exception {
        URI uri = new URI(urlString);
        URL url = uri.toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; utf-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);
    
        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                conn.setRequestProperty(header.getKey(), header.getValue());
            }
        }

        try (OutputStream os = conn.getOutputStream()) {
           byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }
    
        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED || responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
    
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        } else if (responseCode == 429) {
            if (!urlString.startsWith("https://discord.com/api/webhooks/")) {
                throw new Exception("\u001B[1;36mERROR:\u001B[0m rate limit exceeded");
            } else {
                atlasUtils.printError("rate limit exceeded!");
            }
        } else {
            throw new Exception("\u001B[1;36mERROR:\u001B[0m POST request failed with response code: " + responseCode);
        }
    }
    
    public static String sendDeleteRequest(String urlString, Map<String, String> headers) throws Exception {
        URI uri = new URI(urlString);
        URL url = uri.toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("DELETE");
    
        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                conn.setRequestProperty(header.getKey(), header.getValue());
            }
        }
    
        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
    
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        } else if (responseCode == 429) {
            throw new Exception("\u001B[1;36mERROR:\u001B[0m rate limit exceeded");
        } else {
            throw new Exception("\u001B[1;36mERROR:\u001B[0m DELETE request failed with response code: " + responseCode);
        }
    }    

    public static void clearConsole() {
        try {
            String operatingSystem = System.getProperty("os.name");

            if (operatingSystem.contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                new ProcessBuilder("clear").inheritIO().start().waitFor();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void printColorfulText(String text, int colorCode) {
        String[] colors = {
            "\u001B[0m",  // RESET - 0
            "\u001B[1;31m", // RED - 1
            "\u001B[1;32m", // GREEN - 2
            "\u001B[1;33m", // YELLOW - 3
            "\u001B[1;34m", // BLUE - 4
            "\u001B[1;35m", // PURPLE - 5
            "\u001B[1;36m", // CYAN - 6
            "\u001B[1;37m"  // WHITE - 7
        };

        if (colorCode >= 0 && colorCode < colors.length) {
            System.out.println(colors[colorCode] + text + colors[0]);
        } else {
            System.err.println("Invalid argument 'colorCode'!");
        }
    }
}
