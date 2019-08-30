package edu.escuelaing.webServer.web;

import java.net.*;
import java.nio.charset.StandardCharsets;

import java.util.Set;

import javax.imageio.ImageIO;


import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.awt.image.BufferedImage;
import java.io.*;

import java.lang.reflect.Method;

public class HttpServerNoConcurrente {
	public static void main(String[] args) throws IOException {
		while (true) {
			ServerSocket serverSocket = null;
			try {
				serverSocket = new ServerSocket(35000);
			} catch (IOException e) {
				System.err.println("Could not listen on port: 35000.");
				System.exit(1);
			}
			PrintWriter out;
			BufferedReader in;
			Socket clientSocket;

			clientSocket = null;
			try {
				System.out.println("Listo para recibir ...");
				clientSocket = serverSocket.accept();
			} catch (IOException e) {
				System.err.println("Accept failed.");
				System.exit(1);
			}
			while (!clientSocket.isClosed()) {
				out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8),
						true);
				in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				String inputLine, totalInput = "";
				while ((inputLine = in.readLine()) != null) {
					totalInput = totalInput + "\n" + inputLine;
					System.out.println("Received: " + inputLine);
					if (inputLine.contains("GET")) {
						String[] tempArray = inputLine.split(" ");
						String path = System.getProperty("user.dir") + "/resources" + tempArray[1];
						BufferedReader br = null;
						
						try {
							br = new BufferedReader(new FileReader(path));
							if (path.contains(".html")) {
								out.write("HTTP/1.1 200 OK");
								out.println("Content-Type: text/html");
								out.println();
								String temp = br.readLine();
								while (temp != null) {
									out.write(temp);
									temp = br.readLine();
								}

								br.close();
							} else if (path.contains(".png")) {
								out.write("HTTP/1.1 200 OK");
								out.println("Content-Type: image/png");
								out.println();
								BufferedImage image = ImageIO
										.read(new File(System.getProperty("user.dir") + "/resources" + tempArray[1]));
								ImageIO.write(image, "PNG", clientSocket.getOutputStream());

							} else if (path.substring(1, 4).equals("App")) {
								out.write("HTTP/1.1 200 OK");
								out.println("Content-Type: text/html");
								out.println();

								Reflections reflections = new Reflections("edu.escuelaing.webServer.App",
										new SubTypesScanner(false));

								Set<Class<? extends Object>> allClasses = reflections.getSubTypesOf(Object.class);

								String[] appPath = tempArray[1].split("/");

								for (Object clas : allClasses) {
									String[] pathString = clas.toString().split(" ");
									String[] classString = pathString[1].split("\\.");
									if (appPath[2].equals(classString[4])) {

										Class c = Class.forName(pathString[1]);
										String m = appPath[3].split(":")[0];
										String param = appPath[3].split(":")[1];
										Method metodo = c.getDeclaredMethod(m, String.class);

										if (metodo.isAnnotationPresent(web.class)) {
											out.write(metodo.invoke(null, param).toString());
										}

										break;
									}

								}

								out.close();
							}

						} catch (Exception e) {
							out.println("HTTP/1.1 404 Not Found");
							out.println("Content-Type: text/html");
							System.out.println("Not found");
							e.printStackTrace();
						}

						if (!in.ready()) {
							break;
						}
					}
					in.close();
				}
				clientSocket.close();
				serverSocket.close();
			}
		}

	}
}
