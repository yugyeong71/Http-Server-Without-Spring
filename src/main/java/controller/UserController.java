package controller;

import http.HttpRequest;
import http.HttpResponse;
import security.SecurityContext;
import security.UserPrincipal;
import util.ResponseMessage;

/**
 * - 사용자 목록 조회
 * - USER, ADMIN 권한 필요
 */
public class UserController {

	/**
	 * GET : 사용자 목록 조회
	 */
	public HttpResponse getUsers(HttpRequest request) {
		System.out.println("[Request] 사용자 목록 조회");

		HttpResponse response = new HttpResponse();

		try {
			UserPrincipal principal = SecurityContext.getUser();

			response.setStatus(ResponseMessage.SUCCESS.getCode(), ResponseMessage.SUCCESS.getMessage());
			response.setBody(String.format(
				"{\"message\":\"사용자 목록 조회\",\"requestedBy\":\"%s\",\"role\":\"%s\"}",
				principal.getUsername(), principal.getRole()
			));

		} catch (Exception e) {
			response.setStatus(ResponseMessage.INTERNAL_SERVER_ERROR.getCode(), ResponseMessage.INTERNAL_SERVER_ERROR.getMessage());
			response.setBody("{\"error\":\"" + e.getMessage() + "\"}");
		}

		return response;
	}
}

