package controller;

import http.HttpRequest;
import http.HttpResponse;
import util.ResponseMessage;

/**
 * - 서버 상태 확인
 * - Public 엔드포인트 (인증 X)
 */
public class HealthController {

	/**
	 * GET : 서버 상태 확인
	 */
	public HttpResponse getHealth(HttpRequest request) {
		System.out.println("[Request] 서버 상태 확인");

		HttpResponse response = new HttpResponse();

		try {
			response.setStatus(ResponseMessage.SUCCESS.getCode(), ResponseMessage.SUCCESS.getMessage());
			response.setBody("{\"status\":\"UP\"}");

		} catch (Exception e) {
			response.setStatus(ResponseMessage.INTERNAL_SERVER_ERROR.getCode(), ResponseMessage.INTERNAL_SERVER_ERROR.getMessage());
			response.setBody("{\"error\":\"" + e.getMessage() + "\"}");
		}

		return response;
	}
}

