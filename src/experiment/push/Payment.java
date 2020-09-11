package experiment.push;

public class Payment {
	private Points points;
	private History history;
	private int payment;
	public Payment(Points points, History history) {
		this.points = points;
		this.history = history;
	}
	public void purchase(int payment) {
		this.payment = payment;
		points.updatePayment(payment);
		history.updatePayment(payment);
	}
	public int getPayment() {
		return payment;
	}
}
