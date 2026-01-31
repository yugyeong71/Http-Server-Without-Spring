package http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 *  [HTTP Request 파싱]
 *  - Byte → HttpRequest 객체 변환
 */
public class HttpRequestParser {

	/**
	 * HTTP Request 파싱
	 */
	public static HttpRequest parse(InputStream inputStream) throws IOException {
		BufferedReader reader = new BufferedReader(
			new InputStreamReader(inputStream, StandardCharsets.UTF_8)
		);

		HttpRequest request = new HttpRequest();

		// 1. Request Line 파싱 : "GET /api/menu?category=한식 HTTP/1.1"
		parseRequestLine(reader.readLine(), request);

		// 2. Headers 파싱 : "Content-Type: application/json\r\n"
		parseHeaders(reader, request);

		// 3. Body 파싱 (POST)
		if ("POST".equalsIgnoreCase(request.getMethod())) {
			parseBody(reader, request);
		}

		return request;
	}

	/**
	 * Request Line 파싱
	 */
	private static void parseRequestLine(String requestLine, HttpRequest request) {
		if (requestLine == null || requestLine.isEmpty()) {
			throw new IllegalArgumentException("Request Line은 필수입니다.");
		}

		String[] parts = requestLine.split(" ");

		if (parts.length != 3) {
			throw new IllegalArgumentException("잘못된 Request Line입니다.");
		}

		// HTTP 메소드
		request.setMethod(parts[0]);

		// Path와 Query String 분리
		String fullPath = parts[1];
		int queryIndex = fullPath.indexOf('?');

		if (queryIndex != -1) {
			// Query String이 있는 경우
			request.setPath(fullPath.substring(0, queryIndex));
			request.setQueryString(fullPath.substring(queryIndex + 1));

			parseQueryString(request.getQueryString(), request);
		} else {
			// Query String이 없는 경우
			request.setPath(fullPath);
		}

		System.out.println("[" + request.getMethod() + "] " + request.getPath());
	}

	/**
	 * Query String 파싱
	 */
	private static void parseQueryString(String queryString, HttpRequest request) {
		if (queryString == null || queryString.isEmpty()) {
			return;
		}

		String[] params = queryString.split("&");

		for (String param : params) {
			String[] keyValue = param.split("=", 2);

			if (keyValue.length == 2) {
				try {
					String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
					String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);

					request.addQueryParam(key, value);
				} catch (Exception e) {
					System.err.println("Query 파라미터 디코딩 실패입니다.");
				}
			}
		}
	}

	/**
	 * Headers 파싱
	 */
	private static void parseHeaders(BufferedReader reader, HttpRequest request) throws IOException {
		String line;

		while ((line = reader.readLine()) != null) {
			if (line.isEmpty()) {
				break;
			}

			// Header 형식 : Key: Value
			int colonIndex = line.indexOf(':');

			if (colonIndex != -1) {
				String key = line.substring(0, colonIndex).trim();
				String value = line.substring(colonIndex + 1).trim();
				request.addHeader(key, value);
			}
		}
	}

	/**
	 * Body 파싱
	 */
	private static void parseBody(BufferedReader reader, HttpRequest request) throws IOException {
		String contentLengthStr = request.getHeader("content-length");

		if (contentLengthStr == null) {
			return; // Content-Length 없으면 Body도 없음
		}

		try {
			int contentLength = Integer.parseInt(contentLengthStr);

			if (contentLength > 0) {
				char[] bodyChars = new char[contentLength];
				int read = reader.read(bodyChars, 0, contentLength);

				if (read > 0) {
					request.setBody(new String(bodyChars, 0, read));
					System.out.println("Body : " + request.getBody());
				}
			}
		} catch (NumberFormatException e) {
			System.err.println("Content-Length 파싱 실패 : " + contentLengthStr);
		}
	}
}
