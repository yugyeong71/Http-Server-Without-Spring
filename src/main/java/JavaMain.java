import java.io.IOException;

import http.HttpServer;

public class JavaMain {

	private static final int PORT = 8080;

	private static final int THREAD_POOL_SIZE = 10;

	/**
	 * 서버 생성 → Shutdown Hook 등록 → 서버 시작
	 */
	public static void main(String[] args) {
		// 8080 포트, 스레드 풀 크기 10으로 서버 생성
		HttpServer server = new HttpServer(PORT, THREAD_POOL_SIZE);

		// JVM 종료 시 서버를 정상적으로 종료하기 위한 Shutdown Hook 등록
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			System.out.println("\n서버 종료 중");
			server.serverStop();
		}));

		// 서버 실행 중 에러 발생 시 에러 메시지 출력
		try {
			server.serverStart();
		} catch (IOException e) {
			System.err.println("=== 서버 기동 실패: " + e.getMessage() + " ===");
		}
	}

}
