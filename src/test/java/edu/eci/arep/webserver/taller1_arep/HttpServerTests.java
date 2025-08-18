package edu.eci.arep.webserver.taller1_arep;

import edu.eci.arep.webserver.taller1_arep.connection.URLConnection;
import edu.eci.arep.webserver.taller1_arep.httpserver.HttpServer;

import org.junit.*;
import java.net.*;

import static org.junit.Assert.*;


public class HttpServerTests {
    private static HttpServer server;
    private static Thread serverThread;
    private static final int port = 35001;
    static URLConnection urlConnection;
    @BeforeClass
    public static void setUp() throws Exception {
        urlConnection = new URLConnection(port);
        server = new HttpServer(port);
        serverThread = new Thread(() -> {
            try { server.start(); } catch (Exception ignored) {}
        }, "http-server-test");
        serverThread.start();
        Thread.sleep(150);
    }
    @AfterClass
    public static void tearDown() throws Exception {
        if (serverThread != null) {
            serverThread.interrupt();
        }
    }
    /*
     *Returns index.html file
     */
    @Test
    public void testGetHtmlFile() throws Exception {
        HttpURLConnection getConnection = urlConnection.createGetConnection("/");
        int responseCode = getConnection.getResponseCode();
        assertEquals("Should return 200 OK",200, responseCode);
    }
    /*
     *Returns style.css file
     */
    @Test
    public void testGetCssFile() throws Exception {
        HttpURLConnection getConnection = urlConnection.createGetConnection("/styles/style.css");
        int responseCode = getConnection.getResponseCode();
        assertEquals("Should return 200 OK",200, responseCode);
    }
    /*
     *Returns script.js file
     */
    @Test
    public void testGetJsFile() throws Exception {
        HttpURLConnection getConnection = urlConnection.createGetConnection("/scripts/script.js");
        int responseCode = getConnection.getResponseCode();
        assertEquals("Should return 200 OK",200, responseCode);
    }
    /*
     *Returns image file
     */
    @Test
    public void testGetImageFile() throws Exception {
        HttpURLConnection getConnection = urlConnection.createGetConnection("/images/logo.png");
        int responseCode = getConnection.getResponseCode();
        assertEquals("Should return 200 OK",200, responseCode);
    }
    /*
     *Trying to get an unkown file
     */
    @Test
    public void testGetUnkownFile() throws Exception {
        HttpURLConnection getConnection = urlConnection.createGetConnection("/prueba.html");
        int responseCode = getConnection.getResponseCode();
        assertEquals("Should return 404 Not Found",404, responseCode);
    }

    /*
    *Create a new Task successfully
     */
    @Test
    public void testCreateTaskSuccess() throws Exception {
        String jsonPayload = "{\"name\":\"Test Task\",\"description\":\"Test Description\"}";

        HttpURLConnection connection = urlConnection.createPostConnection("/app/save", jsonPayload);

        int responseCode = connection.getResponseCode();
        assertEquals("Should return 200 OK",200, responseCode);
        String responseBody = urlConnection.readResponse(connection);
        assertTrue("Response should contain id", responseBody.contains("\"id\":"));
        assertTrue("Response should contain name", responseBody.contains("\"Test Task\""));
        assertTrue("Response should contain description", responseBody.contains("\"Test Description\""));
        connection.disconnect();
    }

    /*
     *Trying to add a task without name
     */
    @Test
    public void testCreateTaskMissingName() throws Exception {
        String jsonPayload = "{\"description\":\"Test Description\"}"; // Falta name

        HttpURLConnection connection = urlConnection.createPostConnection("/app/save", jsonPayload);

        int responseCode = connection.getResponseCode();
        assertEquals("Should return 400 Bad Request", 400, responseCode);
        String responseBody = urlConnection.readResponse(connection);
        assertFalse("Response should contain error about missing name",
                responseBody.toLowerCase().contains("name"));

        connection.disconnect();
    }

    /*
     *Trying to add a task using an invalid JSON
     */
    @Test
    public void testCreateTaskInvalidJson() throws Exception {
        String invalidJson = "{\"name\":\"Test\",\"description\":}";

        HttpURLConnection connection = urlConnection.createPostConnection("/app/save", invalidJson);

        int responseCode = connection.getResponseCode();
        System.out.println(responseCode);
        assertTrue("Should return Bad request error",responseCode == 400);

        connection.disconnect();
    }

    /*
     *Get all tasks successfully
     */
    @Test
    public void testGetTasks() throws Exception {
        String jsonPayload = "{\"name\":\"Get Test Task\",\"description\":\"For GET test\"}";
        HttpURLConnection postConnection = urlConnection.createPostConnection("/app/save", jsonPayload);
        postConnection.getResponseCode(); // Ejecutar POST
        postConnection.disconnect();

        HttpURLConnection getConnection = urlConnection.createGetConnection("/app/tasks?filter=All");

        int responseCode = getConnection.getResponseCode();
        assertEquals("Should return 200 OK",200, responseCode);

        String responseBody = urlConnection.readResponse(getConnection);
        assertTrue("Response should contain tasks",responseBody.length() > 0);

        getConnection.disconnect();
    }
    /*
     *Get tasks ny filter name successfully
     */
    @Test
    public void testGetTasksByName() throws Exception {
        String jsonPayload = "{\"name\":\"Task GET\",\"description\":\"For GET test\"}";
        HttpURLConnection postConnection = urlConnection.createPostConnection("/app/save", jsonPayload);
        postConnection.getResponseCode();
        postConnection.disconnect();

        HttpURLConnection getConnection = urlConnection.createGetConnection("/app/tasks?filter=Task%20GET");

        int responseCode = getConnection.getResponseCode();
        assertEquals("Should return 200 OK",200, responseCode);

        String responseBody = urlConnection.readResponse(getConnection);
        String[] inner = responseBody.trim().substring(1, responseBody.length()-1).trim().split("},");
        System.out.println( inner );
        assertTrue("Response should contain one task",inner.length == 1);;
        getConnection.disconnect();
    }

    /*
     *Send and invalid method
     */
    @Test
    public void testMethodNotAllowed() throws Exception {
        HttpURLConnection connection = urlConnection.createConnection("/app/save", "PUT");

        int responseCode = connection.getResponseCode();
        assertEquals("Should return 405 Method Not Allowed",405, responseCode);

        connection.disconnect();
    }
}

