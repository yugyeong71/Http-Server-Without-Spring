package security;

import http.HttpRequest;
import http.HttpResponse;

/**
 * [인가 Filter]
 * - URL 기반 접근 권한 제어
 * - Spring Security authorizeHttpRequests().requestMatchers() 역할
 */
public class AuthorizationFilter implements Filter {

	@Override
	public void doFilter(HttpRequest request, HttpResponse response, FilterChain chain) {
		String path = request.getPath();

		if ("/health".equals(path)) {
			chain.doFilter(request, response);
			return;
		}

		UserPrincipal principal = SecurityContext.getUser();

		if (principal == null) {
			response.setStatus(401, "Unauthorized");
			response.setBody("{\"error\":\"인증 정보를 찾을 수 없습니다.\"}");

			return;
		}

		String role = principal.getRole();

		if (path.startsWith("/admin/") || "/admin".equals(path)) {
			if (!"ADMIN".equals(role)) {
				System.out.println("[AuthorizationFilter] 인가 실패 (403)");
				response.setStatus(403, "Forbidden");
				response.setBody("{\"error\":\"관리자 권한이 필요합니다.\"}");

				return;
			}
		} else if ("/users".equals(path)) {
			if (!"USER".equals(role) && !"ADMIN".equals(role)) {
				response.setStatus(403, "Forbidden");
				response.setBody("{\"error\":\"접근 권한이 없습니다.\"}");

				return;
			}
		}

		System.out.println("[AuthorizationFilter] 인가 성공 : " + principal + " → " + path);

		chain.doFilter(request, response);
	}

}
