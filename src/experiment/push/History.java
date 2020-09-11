package experiment.push;

import java.util.ArrayList;

public class History {
	private Total total;
	private ArrayList<Integer> history = new ArrayList<Integer>();
	public void updatePayment(int payment) {
		history.add(payment);
		total.updateHistory(history);
	}
	public History(Total total) {
		this.total = total;
	}
	public ArrayList<Integer> getHistory() {
		return history;
	}
}
