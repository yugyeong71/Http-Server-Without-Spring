package router;

import controller.HealthController;
import controller.MenuController;
import controller.UserController;
import http.HttpRequest;
import http.HttpResponse;

/**
 * 	[경로 라우팅]
 * 	- 요청 경로와 메소드에 따라 적절한 Controller 메소드 호출
 */
public class Router {

	private final MenuController menuController;

	private final HealthController healthController;

	private final UserController userController;

	public Router() {
		this.healthController = new HealthController();
		this.userController = new UserController();
		this.menuController = new MenuController();
	}

	/**
	 * 요청을 적절한 Controller로 라우팅
	 */
	public HttpResponse route(HttpRequest request) {
		String method = request.getMethod();
		String path = request.getPath();

		System.out.println("[라우팅] " + method + " " + path);

		// @GetMapping("/api/menu")
		if ("GET".equals(method) && "/api/menu".equals(path)) {
			return menuController.getMenuList(request);
		}

		// @PostMapping("/api/menu")
		if ("POST".equals(method) && "/api/menu".equals(path)) {
			return menuController.postMenu(request);
		}

		// @GetMapping("/health")
		if ("GET".equals(method) && "/health".equals(path)) {
			return healthController.getHealth(request);
		}

		// @GetMapping("/users")
		if ("GET".equals(method) && "/users".equals(path)) {
			return userController.getUsers(request);
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
