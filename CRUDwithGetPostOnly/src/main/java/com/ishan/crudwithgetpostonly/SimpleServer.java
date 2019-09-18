/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ishan.crudwithgetpostonly;


import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;

public class SimpleServer {
    static Map<String,String> books = new HashMap<String, String>();
    
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/book", new GetHandler());
        server.createContext("/newbook", new PostHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    static class GetHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
        // parse request
            Map<String, Object> parameters = new HashMap<String, Object>();
            URI requestedUri = t.getRequestURI();
            String query = requestedUri.getRawQuery();
            parseQuery(query, parameters);
            // send response
            String response = "";
           
            String id = parameters.get("id").toString();
            if(parameters.containsKey("name")){
            String name = parameters.get("name").toString();
            books.put(id,name);
            response = "Book created at id = " + id + " is named " + name + "\n";
            } else {
                if(books.containsKey(id)){
                    String name = books.get(id).toString();
                    response = "Book at id = " + id + " is named " + books.get(id).toString() + "\n";
                } else {
                    response = "Book not found!";
                }
            }
                        
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
    
    static class PostHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
                        System.out.println("Started Post Method");
			Map<String, Object> parameters = new HashMap<String, Object>();
			InputStreamReader isr = new InputStreamReader(t.getRequestBody(), "utf-8");
			BufferedReader br = new BufferedReader(isr);
			String query = br.readLine();
                        System.out.println(query.toString());
                        
                        JSONObject obj = new JSONObject(query.toString());
                        String id = obj.getString("id");
                        String method = obj.getString("method");
                        String response = "";
                        
                        if(method.equalsIgnoreCase("delete")) {
                            books.remove(id);
                            response = "<h1> Book Deleted! </h1>";
                            t.sendResponseHeaders(200, response.length());
                        } else if(method.equalsIgnoreCase("update")){
                            String newname = obj.getString("name");
                            books.remove(id);
                            books.put(id,newname);
                            response = "<h1> Book Updated! </h1>";
                            t.sendResponseHeaders(200, response.length());
                        }
			OutputStream os = t.getResponseBody();
			os.write(response.toString().getBytes());
			os.close();
        }
    }

    @SuppressWarnings("unchecked")
	public static void parseQuery(String query, Map<String, Object> parameters) throws UnsupportedEncodingException {

		if (query != null) {
			String pairs[] = query.split("[&]");

			for (String pair : pairs) {
				String param[] = pair.split("[=]");

				String key = null;
				String value = null;
				if (param.length > 0) {
					key = URLDecoder.decode(param[0], System.getProperty("file.encoding"));
				}

				if (param.length > 1) {
					value = URLDecoder.decode(param[1], System.getProperty("file.encoding"));
				}

				if (parameters.containsKey(key)) {
					Object obj = parameters.get(key);
					if (obj instanceof List<?>) {
						List<String> values = (List<String>) obj;
						values.add(value);
					} else if (obj instanceof String) {
						List<String> values = new ArrayList<String>();
						values.add((String) obj);
						values.add(value);
						parameters.put(key, values);
					}
				} else {
					parameters.put(key, value);
				}
			}
		}
	}
}