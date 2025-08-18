package edu.eci.arep.webserver.taller1_arep;

import edu.eci.arep.webserver.taller1_arep.httpserver.HttpServer;

public class server {
    public static void main(String[] args) {
        HttpServer httpServer = new HttpServer(35000);
        try{
            httpServer.start();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
