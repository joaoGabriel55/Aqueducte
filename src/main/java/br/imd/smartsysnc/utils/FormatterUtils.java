package br.imd.smartsysnc.utils;

public class FormatterUtils {

	public static String getHourFormat(String data) {
		String hour = data.substring(0, 2);
		String minutes = data.substring(2, 4);
		return hour + ":" + minutes;
	}
	
	public static String dataFormattedForSIGEduc(String data) {
		String dataFormated = data.replace("/", "%2F");
		return dataFormated;
	}

}
