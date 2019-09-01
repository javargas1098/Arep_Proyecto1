package edu.escuelaing.webServer.server;

import java.net.*;
import java.nio.charset.StandardCharsets;

import java.util.Set;

import javax.imageio.ImageIO;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class HttpServer {

	public static void main(String[] args) throws IOException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, ClassNotFoundException, NoSuchMethodException, SecurityException {

		while (true) {
			ServerSocket serverSocket = null;
			try {
				serverSocket = new ServerSocket(getPort());
			} catch (IOException e) {
				System.err.println("Could not listen on port: 4567.");
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
						String path;
						BufferedReader br = null;

						try {

							if (tempArray[1].contains(".html")) {
								path = System.getProperty("user.dir") + "/resources" + tempArray[1];
								br = new BufferedReader(new FileReader(path));
								out.write("HTTP/1.1 200 OK\r\n" + "Content-Type: text/html\r\n" + "\r\n");
								out.println();
								String temp = br.readLine();
								while (temp != null) {
									out.write(temp);
									temp = br.readLine();
								}

								br.close();
							}

						} catch (Exception e) {
							out.println("HTTP/1.1 404 Not Found");
							out.println("Content-Type: text/html");
							System.out.println("Not found");
							e.printStackTrace();
						}
						if (tempArray[1].contains(".png")) {
							out.write("HTTP/1.1 200 OK \r\n");
							out.println("Content-Type: image/png\r\n");
							out.println();
							
							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							BufferedImage image = ImageIO
									.read(new File(System.getProperty("user.dir") + "/resources" + tempArray[1]));
							ImageIO.write(image, "PNG", baos);
							clientSocket.getOutputStream().write(baos.toByteArray());
							clientSocket.getOutputStream().flush();

						} else if (tempArray[1].substring(1, 4).equals("App")) {
							out.write("HTTP/1.1 200 OK\r\n" + "Content-Type: text/html\r\n" + "\r\n");
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

									if (metodo.isAnnotationPresent(Web.class)) {
										out.write(metodo.invoke(null, param).toString());
									}

									break;
								}

							}

						}

						out.close();
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

	static int getPort() {
		if (System.getenv("PORT") != null) {
			return Integer.parseInt(System.getenv("PORT"));
		}
		return 4567; // returns default port if heroku-port isn't set (i.e. on localhost)
	}

}