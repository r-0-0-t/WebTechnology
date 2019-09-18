package com.ishan.httpServer;

public class Main {
	public static int port = 9000;
	public static void main(String[] args) {
//		// start http server
//		SimpleHttpServer httpServer = new SimpleHttpServer();
//		httpServer.Start(port);
		
		// start https server
		SimpleHttpServer httpServer = new SimpleHttpServer();
		httpServer.Start(port);
		
//		System.out.println(System.getProperty("user.dir"));
//		System.out.println(Main.class.getClassLoader().getResource("").getPath());
		
	}
}
