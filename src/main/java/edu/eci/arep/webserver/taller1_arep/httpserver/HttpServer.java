package edu.eci.arep.webserver.taller1_arep.httpserver;

import edu.eci.arep.webserver.taller1_arep.classes.Task;
import edu.eci.arep.webserver.taller1_arep.classes.TaskManager;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class HttpServer {
    static TaskManager taskManager = new TaskManager();
    int portServer;
    public HttpServer(int portServer) {
        this.portServer = portServer;
    }
    public void start() throws Exception {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(portServer);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
        }
        Socket clientSocket = null;
        Boolean running = true;
        while (running) {
            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            clientSocket.getInputStream()));
            OutputStream outputStream = clientSocket.getOutputStream();
            String inputLine, firstLine="";
            boolean isFirstLine = true;
            int contentLength = 0;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Received: " + inputLine);
                if (isFirstLine) {
                    isFirstLine = false;
                    firstLine = inputLine;
                }
                if (inputLine.toLowerCase().startsWith("content-length:")) {
                    contentLength = Integer.parseInt(inputLine.split(":")[1].trim());
                }
                if (inputLine.isEmpty() || inputLine.trim().isEmpty()) {
                    break;
                }
            }
            String body = "";
            if (contentLength > 0) {
                char[] bodyChars = new char[contentLength];
                in.read(bodyChars, 0, contentLength);
                body = new String(bodyChars);
            }
            manageRequest(firstLine,body,outputStream);
            outputStream.close();
            in.close();
            clientSocket.close();
        }
        serverSocket.close();
    }
    /**
     * Manages an HTTP request by processing the method, resource, and body,
     * and writing the corresponding response.
     *
     * @param inputLine the first line of the HTTP request (contains method and resource)
     * @param body      the body of the request, if present
     * @param out       the output stream used to send the response back to the client
     * @throws IOException if an error occurs while writing to the output stream
     */
    private static void manageRequest(String inputLine, String body,OutputStream out) throws IOException {
        try {
            String[] dividedUri = inputLine.split(" ");
            URI requestUri = new URI(dividedUri[1]);
            String path = requestUri.getPath();
            String method = dividedUri[0];
            if(method.equals("GET") && path.startsWith("/app/tasks")) {
                String param = requestUri.getQuery().split("=")[1];
                getTasks(param, out);
            }else if(method.equals("POST") && path.startsWith("/app/save")){
                    String taskName = "";
                    String taskDescription = "";
                    String[] values = body.split(",");
                    for(String value : values){
                        String[] pair = value.split(":");
                        String key = pair[0].trim().replace("\"","").replace("{","").replace("}","").replace(" ","");
                        String val = pair[1].trim().replace("\"","").replace("{","").replace("}","").replace(" ","");;
                        if(key.equals("name")){
                            taskName = val;
                        }
                        if(key.equals("description")){
                            taskDescription = val;
                        }
                    }
                    saveTask(taskName, taskDescription,out);
            }else if(method.equals("GET") && (path.equals("/") || path.endsWith("html") || path.endsWith("js") || path.endsWith("css")
                    || path.endsWith("png") || path.endsWith("jpg") || path.endsWith("jpeg"))){
                getResources(path, out);
            }else{
                sendNotAllowedResponse(out,"Method "+method+" "+path+" not supported");
            }
        }catch (FileNotFoundException e){
            sendNotFoundResponse(out,e.getMessage());
        }catch (IOException e) {
            sendServerErrorResponse(out,e.getMessage());
        }catch (Exception e) {
            sendNotFoundResponse(out,e.getMessage());
        }

    }
    /**
     * Manage disk files.
     *
     * @param path resource of the request
     * @param out  the output stream used to send the response back to the client
     * @throws IOException if an error occurs while writing to the output stream
     */
    private static void getResources(String path, OutputStream out) throws IOException {
        String fullPath = "src/main/resources";
        if(path.equals("/")){
            fullPath += "/" + "pages/index.html";
        }
        else if(path.endsWith("html")){
            fullPath += "/" + "pages" + path;
        }else {
            fullPath += path;
        }
        if(fullPath.endsWith("html") || fullPath.endsWith("css") || fullPath.endsWith("js")){
            sendTextFile(fullPath, out);
        }else{
            sendImageFile(fullPath, out);
        }
    }
    /**
     * Read text files (html, css and javascript).
     *
     * @param filePath full path of the file
     * @throws IOException if an error occurs while reading the file
     */
    public static String readFile(String filePath) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }
    /**
     * Gets the header based on the file extension.
     * @param path full path of the file
     */
    private static String getHeader(String path){
        if (path.endsWith(".html") || path.endsWith(".htm")) return "text/html";
        if (path.endsWith(".css"))  return "text/css";
        if (path.endsWith(".js"))   return "application/javascript";
        if (path.endsWith(".png"))  return "image/png";
        if (path.endsWith(".jpg") || path.endsWith(".jpeg")) return "image/jpeg";
        return "application/octet-stream";
    }
    /**
     * Read and send the file text (html, css or javascript)
     * @param fullPath full path of the file
     * @param out the output stream used to send the response back to the client
     * @throws IOException if an error occurs while writing to the output stream
     */
    private static void sendTextFile(String fullPath, OutputStream out)throws IOException{
        String header = "content-type: "+getHeader(fullPath);
        String output = "HTTP/1.1 200 OK\n\r"
                +header+"\n\r"
                + "\n\r";
        output += readFile(fullPath);
        out.write(output.getBytes());
        out.flush();
    }
    /**
     * Read and send images (png, jpg or jpeg)
     * @param fullPath full path of the file
     * @param out the output stream used to send the response back to the client
     * @throws IOException if an error occurs while reading the file image
     */
    private static void sendImageFile(String fullPath, OutputStream out)throws IOException{
        System.out.println("IMAGE: "+fullPath);
        Path filePath = Paths.get(fullPath);
        if (!Files.exists(filePath)) {
            sendNotFoundResponse(out,"Image not found");
            return;
        }
        byte[] fileContent = Files.readAllBytes(filePath);
        String headers = "HTTP/1.1 200 OK\r\n"
                + "Content-Type: " + getHeader(fullPath) + "\r\n"
                + "Content-Length: " + fileContent.length + "\r\n"
                + "\r\n";
        out.write(headers.getBytes());
        out.flush();
        out.write(fileContent);
        out.flush();
    }
    /**
     * Send when a resource was not found
     * @param out the output stream used to send the response back to the client
     * @param message details of the error
     * @throws IOException if an error occurs while writing to the output stream
     */
    private static void sendNotFoundResponse(OutputStream out, String message) throws IOException {
        String response = "HTTP/1.1 404 Not Found\r\n"
                + "Content-Type: text/plain\r\n"
                + "\r\n"
                + "404 Not Found: " + message;
        out.write(response.getBytes());
        out.flush();
    }
    /**
     * Send when a not controlled exception occurs
     * @param out the output stream used to send the response back to the client
     * @param message details of the error
     * @throws IOException if an error occurs while writing to the output stream
     */
    private static void sendServerErrorResponse(OutputStream out, String message) throws IOException {
        String response = "HTTP/1.1 500 Internal Server Error\r\n"
                + "Content-Type: text/plain\r\n"
                + "\r\n"
                + "500 Internal Server Error: "+message;
        out.write(response.getBytes());
        out.flush();
    }
    /**
     * Send when a method does not exist
     * @param out the output stream used to send the response back to the client
     * @param message details of the error
     * @throws IOException if an error occurs while writing to the output stream
     */
    private static void sendNotAllowedResponse(OutputStream out, String message) throws IOException {
        String response = "HTTP/1.1 405 Method Not Allowed\r\n"
                + "Content-Type: text/plain\r\n"
                + "\r\n"
                + "405 Method Not Allowed: " + message;
        out.write(response.getBytes());
        out.flush();
    }
    /**
     * Send the tasks based on query param of the request
     * @param out the output stream used to send the response back to the client
     * @param param name of the tasks
     * @throws IOException if an error occurs while writing to the output stream
     */
    private static void getTasks(String param, OutputStream out) throws IOException{
        List<Task> tasks;
        if(!param.equals("All")){
            tasks = taskManager.getTasksByName(param);
        }else{
            tasks = taskManager.getTasks();
        }
        String jsonResponse = "[";
        for(int i = 0; i<tasks.size(); i++){
            Task task = tasks.get(i);
            jsonResponse += convertToJsonString(task.getId(), task.getName(), task.getDescription());
            if(i<tasks.size()-1){
                jsonResponse += ",";
            }
        }
        jsonResponse += "]";
        String response = "HTTP/1.1 200 OK\r\n"
                + "Content-Type: application/json\r\n"
                + "Content-Length: " + jsonResponse.getBytes().length + "\r\n"
                + "\r\n"
                + jsonResponse;
        out.write(response.getBytes());
        out.flush();
    }
    /**
     * Save a task in memory
     * @param out the output stream used to send the response back to the client
     * @param name name of the task
     * @param description description of the task
     * @throws IOException if an error occurs while writing to the output stream
     */
    private static void saveTask(String name, String description,OutputStream out) throws IOException{
        Task newTask = taskManager.addTask(name,description);
        String jsonBody = convertToJsonString(newTask.getId(), newTask.getName(), newTask.getDescription());
        String response = "HTTP/1.1 200 OK\r\n"
                + "Content-Type: application/json\r\n"
                + "Content-Length: " + jsonBody.getBytes().length + "\r\n"
                + "\r\n"
                + jsonBody;

        out.write(response.getBytes());
        out.flush();
    }
    /**
     * Convert task object to json
     * @param id ID of the task
     * @param name name of the task
     * @param description description of the task
     */
    private static String convertToJsonString(int id, String name, String description){
        return "{"
                + "\"id\": " + id + ","
                + "\"name\": \"" + name + "\","
                + "\"description\": \"" + description + "\""
                + "}";
    }
}
