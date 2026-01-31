package router;

import controller.MenuController;
import http.HttpRequest;
import http.HttpResponse;

/**
 * 	[경로 라우팅]
 * 	- 요청 경로와 메소드에 따라 적절한 Controller 메소드 호출
 */
public class Router {

	private final MenuController menuController;

	public Router() {
		this.menuController = new MenuController();
	}

	/**
	 * 요청을 적절한 Controller로 라우팅
	 */
	public HttpResponse route(HttpRequest request) {
		String method = request.getMethod();
		String path = request.getPath();

		System.out.println("[라우팅] " + method + " " + path);

		// @GetMapping
		if ("GET".equals(method) && "/api/menu".equals(path)) {
			return menuController.getMenuList(request);
		}

		// PostMapping
		if ("POST".equals(method) && "/api/menu".equals(path)) {
			return menuController.postMenu(request);
		}

		return notFoundPath(path);
	}

	private HttpResponse notFoundPath(String path) {
		HttpResponse response = new HttpResponse();
		response.setStatus(404, "Not Found");
		response.setBody("{\"error\":\"경로를 찾을 수 없습니다: " + path + "\"}");

		return response;
	}
}
