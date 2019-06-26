package br.imd.smartsysnc.utils;

import java.util.Map;

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

	public static Object getValuePropertyNGSILD(Map<Object, Object> propertiesObjNGSILD) {
		if (propertiesObjNGSILD != null) {
			Map<Object, Object> map = propertiesObjNGSILD;
			return map.get("value").toString();
		}
		return null;
	}

}
