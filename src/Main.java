import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

/**
 * Main class to drive the program
 * @author Conor Smyth 12452382 <conor.smyth39@mail.dcu.ie>
 * @author Phil Brennan 12759011 <philip.brennan36@mail.dcu.ie>
 */
class Main {
	private static Queue<Map<Person, ReentrantLock>> personQueue;
	private final static int ELEVATOR_COUNT = 1;//Generator.generateRandomNumber();

	public static void main(String[] args) {
		startPersonGenerator();

		ExecutorService elevatorPool = Executors.newFixedThreadPool(ELEVATOR_COUNT);

		Elevator[] elevators = new Elevator[ELEVATOR_COUNT];

		for(int i = 0; i < ELEVATOR_COUNT; i++) {
			elevators[i] = new Elevator(personQueue);

			elevatorPool.execute(elevators[i]);
		}
	}

	private static void startPersonGenerator() {
		personQueue = new ConcurrentLinkedQueue<>();

		PersonGenerator personGenerator = new PersonGenerator(personQueue);

		Thread generatorThread = new Thread(personGenerator);

		generatorThread.start();
	}
}
