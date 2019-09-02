package edu.escuelaing.webServer.App;

import edu.escuelaing.webServer.server.Web;

public class Texto {
	@Web()
	public static String nombre(String name) {
		
		return "Tu nombre es: "+name;
	}
}
