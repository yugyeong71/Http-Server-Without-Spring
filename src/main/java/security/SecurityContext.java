package security;

/**
 * [인증된 사용자 정보 저장]
 * - ThreadLocal 기반으로 각 스레드(요청)가 독립적인 Context를 가진다.
 * - Spring Security SecurityContextHolder.getContext() 역할
 */
public class SecurityContext {

	private static final ThreadLocal<UserPrincipal> store = new ThreadLocal<>();

	public static void setUser(UserPrincipal principal) {
		store.set(principal);
	}

	public static UserPrincipal getUser() {
		return store.get();
	}

	/**
	 * 요청 처리 완료 후, 호출 필수
	 */
	public static void clear() {
		store.remove();
	}

}
