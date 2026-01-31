package http;

import java.util.HashMap;
import java.util.Map;

/**
 *  [요청 데이터 객체]
 *  - 파싱된 HTTP 요청 데이터를 담는 DTO
 */
public class HttpRequest {

	private String method;

	private String path;

	private String queryString;

	private final Map<String, String> headers;

	private final Map<String, String> queryParams;

	private String body;

	public HttpRequest() {
		this.headers = new HashMap<>();
		this.queryParams = new HashMap<>();
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getQueryString() {
		return queryString;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	public void addHeader(String key, String value) {
		this.headers.put(key.toLowerCase(), value);
	}

	public String getHeader(String key) {
		return headers.get(key.toLowerCase());
	}

	public void addQueryParam(String key, String value) {
		this.queryParams.put(key, value);
	}

	public String getQueryParam(String key) {
		return queryParams.get(key);
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	@Override
	public String toString() {
		return "HttpRequest{" +
			"method='" + method + '\'' +
			", path='" + path + '\'' +
			", queryString='" + queryString + '\'' +
			", headers=" + headers +
			", queryParams=" + queryParams +
			", body='" + body + '\'' +
			'}';
	}

}
