package edu.eci.arep.webserver.taller1_arep.connection;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class URLConnection {
    private static final int TEST_PORT = 35001;
    private HttpURLConnection createPostConnection(String path, String jsonPayload) throws Exception {
        HttpURLConnection connection = createConnection(path, "POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Content-Length", String.valueOf(jsonPayload.length()));

        // Enviar el payload
        try (OutputStream out = connection.getOutputStream()) {
            out.write(jsonPayload.getBytes("UTF-8"));
            out.flush();
        }
        return connection;
    }

    private HttpURLConnection createGetConnection(String path) throws Exception {
        return createConnection(path, "GET");
    }

    private HttpURLConnection createConnection(String path, String method) throws Exception {
        URL url = new URL("http://localhost:" + TEST_PORT + path);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.setDoOutput(true);
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        return connection;
    }

    private String readResponse(HttpURLConnection connection) throws Exception {
        InputStream inputStream;
        try {
            inputStream = connection.getInputStream();
        } catch (IOException e) {
            inputStream = connection.getErrorStream();
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }
        return response.toString();
    }
}
