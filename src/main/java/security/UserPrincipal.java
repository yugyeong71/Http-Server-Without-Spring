package security;

/**
 * [인증된 사용자 정보를 담는 객체]
 * - Spring Security Authentication 객체 역할
 */
public class UserPrincipal {

	private final String username;

	private final String role; // "USER" / "ADMIN"

	public UserPrincipal(String username, String role) {
		this.username = username;
		this.role = role;
	}

	public String getUsername() { return username; }

	public String getRole() { return role; }

	@Override
	public String toString() {
		return "UserPrincipal{username='" + username + "', role='" + role + "'}";
	}

}
