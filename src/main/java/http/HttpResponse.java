package http;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 *  [응답 생성 및 전송]
 *  - HTTP 응답 메시지 생성
 *  - Socket으로 응답 전송
 */
public class HttpResponse {

	private int statusCode;

	private String statusMessage;

	private final Map<String, String> headers;

	private String body;

	public HttpResponse() {
		this.headers = new HashMap<>();
		this.statusCode = 200;
		this.statusMessage = "OK";
	}

	public void setStatus(int code, String message) {
		this.statusCode = code;
		this.statusMessage = message;
	}

	public void setBody(String body) {
		this.body = body;
	}

	/**
	 * HTTP Response -> OutputStream으로 전송
	 */
	public void send(OutputStream outputStream) throws IOException {
		StringBuilder response = new StringBuilder();

		// 1. Status Line 생성 : "HTTP/1.1 200 OK\r\n"
		response.append("HTTP/1.1 ")
			.append(statusCode)
			.append(" ")
			.append(statusMessage)
			.append("\r\n");

		// 2. Headers 생성
		// Body가 있으면 Content-Length 추가
		if (body != null) {
			byte[] bodyBytes = body.getBytes(StandardCharsets.UTF_8);
			headers.put("Content-Length", String.valueOf(bodyBytes.length));
		}

		headers.put("Content-Type", headers.getOrDefault("Content-Type", "application/json; charset=UTF-8"));

		for (Map.Entry<String, String> header : headers.entrySet()) {
			response.append(header.getKey())
				.append(": ")
				.append(header.getValue())
				.append("\r\n");
		}

		// 헤더와 바디 구분
		response.append("\r\n");

		// Body
		if (body != null) {
			response.append(body);
		}

		// 전송
		outputStream.write(response.toString().getBytes(StandardCharsets.UTF_8));
		outputStream.flush();

		System.out.println("[Response] " + statusCode);
	}
}
