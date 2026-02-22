package security;

import http.HttpRequest;
import http.HttpResponse;

public interface Filter {

	void doFilter(HttpRequest request, HttpResponse response, FilterChain chain);

}
