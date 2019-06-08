package br.imd.smartsysnc.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class MessageUtils {

	public static ResponseEntity<Object> sendMessage(Object message, HttpStatus httpStatus) {
		return new ResponseEntity<>(message, httpStatus);
	}

}
