package http;

import java.net.Socket;

import router.Router;

/**
 * 	[요청 처리 작업]
 * 	- 하나의 HTTP 요청-응답 사이클 처리
 */
public class RequestHandler implements Runnable {

	private final Socket socket;

	private static final Router router = new Router();

	public RequestHandler(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		try (socket) {
			System.out.println("\n=== [" + Thread.currentThread().getName() + "] 요청 처리 시작 ===");

			// 1. Socket의 InputStream에서 HTTP Request 파싱
			HttpRequest request = HttpRequestParser.parse(socket.getInputStream());

			// 2. Router로 요청 전달 → Controller 실행 → Response 획득
			HttpResponse response = router.route(request);

			// 3. Socket의 OutputStream으로 HTTP Response 전송
			response.send(socket.getOutputStream());

		} catch (Exception e) {
			System.err.println("=== 요청 처리 실패 : " + e.getMessage() + " ===");

			try {
				HttpResponse errorResponse = new HttpResponse();
				errorResponse.setStatus(500, "Internal Server Error");
				errorResponse.setBody("{\"error\":\"서버 오류가 발생했습니다\"}");

				errorResponse.send(socket.getOutputStream());
			} catch (Exception ignored) {}
		}
	}
}
