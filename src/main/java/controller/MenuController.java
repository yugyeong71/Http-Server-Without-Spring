package controller;

import java.util.*;

import domain.Menu;
import http.HttpRequest;
import http.HttpResponse;
import util.ResponseMessage;

/**
 *  - 메뉴 조회/추가
 *  - Validation
 *  - 응답 생성
 */
public class MenuController {

	private static final List<Menu> MENU_LIST = Arrays.asList(
		new Menu("김치찌개", 5000, "한식"),
		new Menu("깐풍기", 9000, "중식"),
		new Menu("스시", 15000, "일식"),
		new Menu("토마토 파스타", 9000, "양식"),
		new Menu("떡볶이", 4000, "분식")
	);

	/**
	 * GET : 메뉴판 조회
	 */
	public HttpResponse getMenuList(HttpRequest request) {
		System.out.println("[Request] 메뉴판 조회");

		HttpResponse response = new HttpResponse();

		try {
			// Query 파라미터에서 category 추출 (있으면 필터링, 없으면 전체 조회)
			String category = request.getQueryParam("category");

			List<Menu> menuList;

			if (category != null && !category.isEmpty()) {
				System.out.println("- 카테고리 : " + category);

				menuList = filterByCategory(MENU_LIST, category);
			} else {
				menuList = MENU_LIST;
			}

			String menuListToJson = menuListToJson(menuList);

			response.setStatus(ResponseMessage.SUCCESS.getCode(), ResponseMessage.SUCCESS.getMessage());
			response.setBody(menuListToJson);
		} catch (Exception e) {
			response.setStatus(ResponseMessage.INTERNAL_SERVER_ERROR.getCode(), ResponseMessage.INTERNAL_SERVER_ERROR.getMessage());
			response.setBody("{\"error\":\"" + e.getMessage() + "\"}");
		}

		return response;
	}

	/**
	 * POST : 메뉴판에 메뉴 추가
	 */
	public HttpResponse postMenu(HttpRequest request) {
		System.out.println("[Request] 메뉴 추가");

		HttpResponse response = new HttpResponse();

		try {
			String body = request.getBody();

			// Validation : Null / Empty 체크
			if (body == null || body.isEmpty()) {
				response.setStatus(ResponseMessage.BAD_REQUEST.getCode(), ResponseMessage.BAD_REQUEST.getMessage());
				response.setBody("{\"error\":\"요청 값이 존재하지 않습니다.\"}");

				return response;
			}

			System.out.println("[요청 Body] " + body);

			// JSON 파싱
			Map<String, String> parseJson = parseJson(body);

			// Validation : 필수값 검증
			if (!parseJson.containsKey("name") || !parseJson.containsKey("price") || !parseJson.containsKey("category")) {
				response.setStatus(ResponseMessage.BAD_REQUEST.getCode(), ResponseMessage.BAD_REQUEST.getMessage());
				response.setBody("{\"error\":\"필수 필드가 누락되었습니다.\"}");

				return response;
			}

			// Validation : 가격 유효성 검증
			int price;

			try {
				price = Integer.parseInt(parseJson.get("price"));

				if (price <= 0) {
					throw new IllegalArgumentException("가격은 0보다 커야 합니다.");
				}
			} catch (NumberFormatException e) {
				response.setStatus(ResponseMessage.BAD_REQUEST.getCode(), ResponseMessage.BAD_REQUEST.getMessage());
				response.setBody("{\"error\":\"숫자만 입력 가능합니다.\"}");

				return response;
			}

			response.setStatus(ResponseMessage.SUCCESS.getCode(), ResponseMessage.SUCCESS.getMessage());
			response.setBody(String.format("{\"message\":\"메뉴가 추가되었습니다.\",\"name\":\"%s\",\"price\":%d,\"category\":\"%s\"}",
				parseJson.get("name"), price, parseJson.get("category")
			));

		} catch (Exception e) {
			response.setStatus(ResponseMessage.INTERNAL_SERVER_ERROR.getCode(), ResponseMessage.INTERNAL_SERVER_ERROR.getMessage());
			response.setBody("{\"error\":\"" + e.getMessage() + "\"}");
		}

		return response;
	}

	/**
	 * 카테고리 별 메뉴 필터링
	 */
	private List<Menu> filterByCategory(List<Menu> menuList, String category) {
		List<Menu> result = new ArrayList<>();

		for (Menu menu : menuList) {
			if (menu.getCategory().equals(category)) {
				result.add(menu);
			}
		}

		return result;
	}

	/**
	 * 메뉴판 Json 파싱
	 */
	private String menuListToJson(List<Menu> menus) {
		StringBuilder stringBuilder = new StringBuilder("[");

		for (int i = 0; i < menus.size(); i++) {
			stringBuilder.append(menus.get(i).toJson());
			if (i < menus.size() - 1) {
				stringBuilder.append(",");
			}
		}

		stringBuilder.append("]");

		return stringBuilder.toString();
	}

	/**
	 * JSON 파싱
	 */
	private Map<String, String> parseJson(String json) {
		Map<String, String> result = new HashMap<>();

		json = json.trim().replaceAll("[{}]", "");

		String[] pairs = json.split(",");

		for (String pair : pairs) {
			String[] keyValue = pair.split(":", 2);
			if (keyValue.length == 2) {
				String key = keyValue[0].trim().replaceAll("\"", "");
				String value = keyValue[1].trim().replaceAll("\"", "");
				result.put(key, value);
			}
		}

		return result;
	}
}
