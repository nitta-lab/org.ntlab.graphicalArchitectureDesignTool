package experiment.pull;

public class Payment {
	private History history;
	private int payment;
	public Payment(History history) {
		this.history = history;
	}
	public void purchase(int payment) {
		this.payment = payment;
		history.updatePayment(payment);
	}
	public int getPayment() {
		return payment;
	}
}
