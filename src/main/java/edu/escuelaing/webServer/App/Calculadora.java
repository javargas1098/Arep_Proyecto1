package edu.escuelaing.webServer.App;

import edu.escuelaing.webServer.server.Web;

public class Calculadora {
	@Web()
	public static Integer elevado(String number) {
		Integer num = Integer.parseInt(number);
		return num*num;
	}
}
