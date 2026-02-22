package security;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import http.HttpRequest;
import http.HttpResponse;

/**
 * [인증 Filter]
 * - Authorization 헤더 파싱 후 사용자 인증
 * - Spring Security BasicAuthenticationFilter 역할
 */
public class AuthenticationFilter implements Filter {

	private static final String PERMIT_ALL = "/health";

	private static final Map<String, String> USER_DB = new HashMap<>();

	static {
		USER_DB.put("user:1234", "USER");
		USER_DB.put("admin:admin", "ADMIN");
	}

	@Override
	public void doFilter(HttpRequest request, HttpResponse response, FilterChain chain) {
		String path = request.getPath();

		if (PERMIT_ALL.equals(path)) {
			System.out.println("[AuthenticationFilter] Public Path (인증 X) : " + path);

			chain.doFilter(request, response);
			return;
		}

		String authHeader = request.getHeader("authorization");

		if (authHeader == null || !authHeader.startsWith("Basic ")) {
			System.out.println("[AuthenticationFilter] Authorization 헤더 누락 (401)");
			response.setStatus(401, "Unauthorized");
			response.setBody("{\"error\":\"인증이 필요합니다.\"}");

			return;
		}

		String credentials = decodeBasicAuth(authHeader);
		if (credentials == null) {
			response.setStatus(401, "Unauthorized");
			response.setBody("{\"error\":\"잘못된 Authorization 형식입니다.\"}");

			return;
		}

		String role = USER_DB.get(credentials);

		if (role == null) {
			System.out.println("[AuthenticationFilter] 인증 실패 (401)");
			response.setStatus(401, "Unauthorized");
			response.setBody("{\"error\":\"아이디 또는 비밀번호가 올바르지 않습니다.\"}");

			return;
		}

		// 인증 성공 → SecurityContext에 저장
		String username = credentials.split(":")[0];
		UserPrincipal principal = new UserPrincipal(username, role);
		SecurityContext.setUser(principal);

		System.out.println("[AuthenticationFilter] 인증 성공 : " + principal);
		chain.doFilter(request, response); // 다음 Filter로
	}

	private String decodeBasicAuth(String authHeader) {
		try {
			String base64Credentials = authHeader.substring("Basic ".length());
			byte[] decoded = Base64.getDecoder().decode(base64Credentials);

			return new String(decoded);
		} catch (Exception e) {
			return null;
		}
	}

}
