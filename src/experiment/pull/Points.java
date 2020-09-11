package experiment.pull;

public class Points {
	private Payment payment;
	public Points(Payment payment) {
		this.payment = payment;
	}
	public int getPoints() {
		return (int)Math.floor((payment.getPayment()*0.05));
	}
}
