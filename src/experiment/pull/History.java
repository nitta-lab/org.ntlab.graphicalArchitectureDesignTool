package experiment.pull;

import java.util.ArrayList;

public class History {
	private ArrayList<Integer> history = new ArrayList<Integer>();
	public void updatePayment(int payment) {
		history.add(payment);
	}
	public ArrayList<Integer> getHistory() {
		return history;
	}
}
