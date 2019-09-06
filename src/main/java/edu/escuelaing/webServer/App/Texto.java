package edu.escuelaing.webServer.App;

import edu.escuelaing.webServer.server.Web;


/**
 * @author Javier Vargas
 * POJO Texto
 */
public class Texto {
	/**
	 * Este metodo devuelve un nombre
	 * 
	 * @param data String
	 *   
	 * Es el dato recibido por parametro 
	 * 
	 * @return String name
	 * 
	 */
	@Web()
	public static String nombre(String name) {
		
		return "Tu nombre es: "+name;
	}
}
