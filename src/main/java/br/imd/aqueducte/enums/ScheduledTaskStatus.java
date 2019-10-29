package br.imd.aqueducte.enums;

/**
 * <b>Status</b>: DONE, PENDING, STOPPED, NONE
 * */
public enum ScheduledTaskStatus {

	DONE, PENDING, STOPPED,
	/** Outside date scheduled */
	NONE;

	public static ScheduledTaskStatus getStatus(String status) {
		switch (status) {
		case "DONE":
			return DONE;
		case "PENDING":
			return PENDING;
		case "STOPPED":
			return STOPPED;
		case "NONE":
			return NONE;

		default:
			return NONE;
		}
	}

}
