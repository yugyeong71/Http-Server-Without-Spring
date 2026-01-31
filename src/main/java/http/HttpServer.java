package http;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *  [서버 관리]
 * 	- Socket 서버 생성
 * 	- 클라이언트 연결 수락
 * 	- 스레드 풀 관리
 */
public class HttpServer {

	private final int port;

	private final ExecutorService threadPool;

	private ServerSocket serverSocket;

	private volatile boolean running = false; // 서버 상태

	public HttpServer(int port, int threadPoolSize) {
		this.port = port;
		this.threadPool = Executors.newFixedThreadPool(threadPoolSize);
	}

	/**
	 * 서버 시작
	 */
	public void serverStart() throws IOException {
		// 1. ServerSocket 포트 바인딩
		serverSocket = new ServerSocket(port);

		running = true;

		System.out.println("=== 서버 기동 (" + port + ") ===");

		// 2. ExecutorService로 10개 스레드 풀 생성
		System.out.println("=== ThreadPool 생성 (" + ((ThreadPoolExecutor) threadPool).getCorePoolSize() + ") ===");

		// 3. 클라이언트 연결 대기 (블로킹)
		while (running) {
			try {
				// 4. 클라이언트 연결되면 Socket 객체 획득
				Socket clientSocket = serverSocket.accept();
				System.out.println("\n=== 클라이언트 연결 [" + clientSocket.getRemoteSocketAddress() + "] ===");

				// 5. RequestHandler를 스레드 풀에 제출 (비동기 처리)
				threadPool.submit(new RequestHandler(clientSocket));

			} catch (IOException e) {
				if (running) {
					System.err.println("=== 클라이언트 연결 실패 : " + e.getMessage() + " ===");
				}
			}
		}
	}

	/**
	 * 서버 종료
	 */
	public void serverStop() {
		running = false;

		try {
			if (serverSocket != null && !serverSocket.isClosed()) {
				serverSocket.close();
			}

			threadPool.shutdown();

			if (!threadPool.awaitTermination(5, TimeUnit.SECONDS)) {
				threadPool.shutdownNow();
			}

			System.out.println("=== 서버 종료 === ");

		} catch (Exception e) {
			System.err.println("=== 서버 종료 중 오류 : " + e.getMessage() + " ===");
		}
	}

}
