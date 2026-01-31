package domain;

public class Menu {

	private final String name;

	private final int price;

	private final String category;

	public Menu(String name, int price, String category) {
		this.name = name;
		this.price = price;
		this.category = category;
	}

	public String getCategory() {
		return category;
	}

	public String toJson() {
		return String.format(
			"{\"name\":\"%s\",\"price\":%d,\"category\":\"%s\"}",
			name, price, category
		);
	}
}
