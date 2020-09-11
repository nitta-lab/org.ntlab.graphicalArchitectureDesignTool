package experiment.push;

public class Points {
	private int points;
	public void updatePayment(int payment) {
		points = (int)Math.floor((payment*0.05));
	}
	public int getPoints() {
		return points;
	}
}
