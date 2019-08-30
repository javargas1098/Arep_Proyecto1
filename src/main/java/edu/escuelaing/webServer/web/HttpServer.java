package edu.escuelaing.webServer.web;

import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.*;
import java.lang.reflect.Method;

public class HttpServer {
	public static void main(String[] args) throws IOException {
		initialice();
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(35000);
		} catch (IOException e) {
			System.err.println("Could not listen on port: 35000.");
			System.exit(1);
		}
		Socket clientSocket = null;
		try {
			System.out.println("Listo para recibir ...");
			clientSocket = serverSocket.accept();
		} catch (IOException e) {
			System.err.println("Accept failed.");
			System.exit(1);
		}
		PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
		BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		String inputLine, outputLine;
		while ((inputLine = in.readLine()) != null) {
			System.out.println("Received: " + inputLine);
			if (!in.ready()) {
				break;
			}
		}
		out.println("HTTP/1.1 200 OK");
		out.println("Content-Type: text/html");
		out.println("\r\n");
		outputLine = "<!DOCTYPE html>" + "<html>" + "<head>" + "<meta charset=\"UTF-8\">"
				+ "<title>Title of the document</title>\n" + "</head>" + "<body>" + initialice() + "</body>"
				+ "</html>" + inputLine;
//		BufferedWriter bw = new BufferedWriter(new FileWriter(System.getProperty("user.dir")+"//resultado.html"));
//		bw.write(outputLine);
//		bw.close();
		out.println(outputLine);

		out.close();
		in.close();
		clientSocket.close();
		serverSocket.close();
	}

	public static String initialice() {
		try {
		Class<?> c = Class.forName("edu.escuelaing.webServer.App.Calculadora");
		Method main = c.getDeclaredMethod("Cuadrado", null);
		System.out.format("invoking %s.main()%n", c.getName());
		 System.out.println(main.invoke(main, null));
		 return (String) main.invoke(main, null);
		}catch (Exception e) {
			// TODO: handle exception
			Logger.getLogger(Appendable.class.getName()).log(Level.SEVERE,null,e);
		}
		return null;
		
		
	}
}