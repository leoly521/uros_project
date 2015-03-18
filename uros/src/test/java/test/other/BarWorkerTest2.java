package test.other;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class BarWorkerTest2 {

	private static int taskCount = 2;

	public static void main(String[] args) throws Exception {

		ExecutorService pool = Executors.newCachedThreadPool();

		List<BarWorker> tasks = new ArrayList<BarWorker>();
		for (int i = 1; i <= taskCount; i++) {
			tasks.add(new BarWorker("Bar" + i));
		}

		for (BarWorker task : tasks) {
			pool.execute(task);
		}

		pool.shutdown();
	}

	static class BarWorker implements Runnable {

		private static Boolean exists = false;
		private String name;

		public BarWorker(String name) {
			this.name = name;
		}

		@Override
		public void run() {
			System.out.println(name + "---" + exists);
			if (!exists) {
				try {
					TimeUnit.SECONDS.sleep(1);
				} catch (InterruptedException e1) {
					// do nothing
				}
				exists = true;
				System.out.println(name + " enter");
				try {
					System.out.println(name + " working");
					TimeUnit.SECONDS.sleep(2);
				} catch (InterruptedException e) {
					// do nothing
				}
				System.out.println(name + " leave");
				//exists = false;
			} else {
				System.out.println(name + " give up");
			}
		}
	}
}