package util;

public enum ResponseMessage {

	SUCCESS(200, "OK"),

	BAD_REQUEST(400, "올바르지 않은 요청입니다."),

	NOT_FOUND(404, "존재하지 않는 데이터입니다."),

	INTERNAL_SERVER_ERROR(500, "서버 에러입니다.");

	private final int code;

	private final String message;

	ResponseMessage(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

}
