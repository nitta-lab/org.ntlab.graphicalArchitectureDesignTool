package experiment.push;

import java.util.ArrayList;

public class Main {
	private Total total = new Total();
	private Points points = new Points();
	private History history = new History(total);
	private Payment payment = new Payment(points,history);
	public void purchase(int payment) {
		this.payment.purchase(payment);
	}
	public int getTotal() {
		return total.getTotal();
	}
	public int getPayment() {
		return payment.getPayment();
	}
	public ArrayList<Integer> getHistory() {
		return history.getHistory();
	}
	public int getPoints() {
		return points.getPoints();
	}
}
