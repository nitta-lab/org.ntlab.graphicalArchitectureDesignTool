package experiment;

import experiment.pull.Main;

public class PullExpriment {
	public static void main(String[] args) {
		Main main = new Main();
		Long times = (long) 0;
		int num = 10000;
		int kirisute = 100;

		for (int i = 0; i < num; i++) {
			long startTime = System.nanoTime();
			main.purchase(100);
			long endTime = System.nanoTime();
			main.getHistory().clear();
			if(i > kirisute) times += endTime - startTime;
		}
		System.out.println("purchase:" + (double) times / (num - kirisute));
		times = (long) 0;
		for (int i = 0; i < num; i++) {
			long startTime = System.nanoTime();
			main.getHistory();
			long endTime = System.nanoTime();
			if(i > kirisute) times += endTime - startTime;
		}
		System.out.println("getHistory:" + (double) times / (num - kirisute));
		times = (long) 0;
		for (int i = 0; i < num; i++) {
			long startTime = System.nanoTime();
			main.getPayment();
			long endTime = System.nanoTime();
			if(i > kirisute) times += endTime - startTime;
		}
		System.out.println("getPayment:" + (double) times / (num - kirisute));
		times = (long) 0;
		for (int i = 0; i < num; i++) {
			long startTime = System.nanoTime();
			main.getPoints();
			long endTime = System.nanoTime();
			if(i > kirisute) times += endTime - startTime;
		}
		System.out.println("getPoints:" + (double) times / (num - kirisute));
		times = (long) 0;
		for (int i = 0; i < num; i++) {
			long startTime = System.nanoTime();
			main.getTotal();
			long endTime = System.nanoTime();
			if(i > kirisute) times += endTime - startTime;
		}
		System.out.println("getTotal:" + (double) times / (num - kirisute));
	}
}
