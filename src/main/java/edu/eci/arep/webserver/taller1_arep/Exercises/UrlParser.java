/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.eci.arep.webserver.taller1_arep.Exercises;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class UrlParser {

    public static void main(String[] args) throws MalformedURLException {
        URL myUrl = new URL("https://github.com:443/index.html?name=hola#seccion1");
        System.out.println(myUrl.getProtocol());
        System.out.println(myUrl.getHost());
        System.out.println(myUrl.getPort());
        System.out.println(myUrl.getAuthority());
        System.out.println(myUrl.getPath());
        System.out.println(myUrl.getFile());
        System.out.println(myUrl.getQuery());
        System.out.println(myUrl.getRef());
    }

}
