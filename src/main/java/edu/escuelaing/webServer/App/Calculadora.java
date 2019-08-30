package edu.escuelaing.webServer.App;

import edu.escuelaing.webServer.web.web;

public class Calculadora {
	@web()
	public static Integer Cuadrado(String number){
		Integer num=Integer.parseInt(number);
		return num;
	}
}
