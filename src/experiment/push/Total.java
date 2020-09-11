package experiment.push;

import java.util.ArrayList;

public class Total {
	private int total;
	public void updateHistory(ArrayList<Integer> history) {
		total = history.stream().mapToInt(x->x).sum();
	}
	public int getTotal() {
		return total;
	}
}
