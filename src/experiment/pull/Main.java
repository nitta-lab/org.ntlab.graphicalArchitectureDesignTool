package experiment.pull;

import java.util.ArrayList;

public class Main {
	private History history = new History();
	private Total total = new Total(history);
	private Payment payment = new Payment(history);
	private Points points = new Points(payment);
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
