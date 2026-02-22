package security;

import java.util.List;

import http.HttpRequest;
import http.HttpResponse;
import router.Router;

public class FilterChain {

	private final List<Filter> filters;

	private final Router router;

	private int index = 0; // 현재 실행할 Filter 인덱스

	public FilterChain(List<Filter> filters, Router router) {
		this.filters = filters;
		this.router = router;
	}

	/**
	 * 다음 Filter 실행.
	 * 모든 Filter가 통과되면 마지막에 Controller(Router) 호출.
	 */
	public void doFilter(HttpRequest request, HttpResponse response) {
		if (index < filters.size()) { // 아직 실행할 Filter가 남아있으면 다음 Filter 호출
			Filter next = filters.get(index++);
			next.doFilter(request, response, this);
		} else { // 모든 Filter 통과 → Router로 실제 비즈니스 로직 실행
			HttpResponse routeResponse = router.route(request);
			response.setStatus(routeResponse.getStatusCode(), routeResponse.getStatusMessage());
			response.setBody(routeResponse.getBody());
		}
	}

}
