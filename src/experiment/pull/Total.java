package experiment.pull;

public class Total {
	private History history;
	public Total(History history) {
		this.history = history;
	}
	public int getTotal() {
		return history.getHistory().stream().mapToInt(x->x).sum();
	}
}
