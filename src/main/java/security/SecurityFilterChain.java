package security;

import java.util.Arrays;
import java.util.List;

import http.HttpRequest;
import http.HttpResponse;
import router.Router;

/**
 * [Security Filter Chain 진입점]
 * - Filter 목록 조립 및 요청마다 새 FilterChain 생성/실행
 * - Spring Security DefaultSecurityFilterChain 역할
 */
public class SecurityFilterChain {

	private final List<Filter> filters;

	private final Router router;

	public SecurityFilterChain(Router router) {
		this.router = router;
		this.filters = Arrays.asList(new AuthenticationFilter(), new AuthorizationFilter());
	}

	public HttpResponse execute(HttpRequest request) {
		HttpResponse response = new HttpResponse();

		try {
			FilterChain chain = new FilterChain(filters, router); // 요청마다 새 FilterChain
			chain.doFilter(request, response);
		} finally {
			SecurityContext.clear(); // 요청 종료 시, SecurityContext clear
			System.out.println("[SecurityFilterChain] SecurityContext clear 완료");
		}

		return response;
	}

}
