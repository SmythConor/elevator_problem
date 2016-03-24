import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

/**
 * Main class to drive the program
 * @author Conor Smyth 12452382 <conor.smyth39@mail.dcu.ie>
 * @author Phil Brennan 12759011 <philip.brennan36@mail.dcu.ie>
 */
class Main {
	private static PersonQueue personQueue;
	private final static int ELEVATOR_COUNT = 2;

	public static void main(String[] args) {
		personQueue = new PersonQueue();
		startPersonGenerator();

		ExecutorService elevatorPool = Executors.newFixedThreadPool(ELEVATOR_COUNT);

		Elevator[] elevators = new Elevator[ELEVATOR_COUNT];

		for(int i = 0; i < ELEVATOR_COUNT; i++) {
			elevators[i] = new Elevator(personQueue);

			elevatorPool.execute(elevators[i]);
		}

		if(elevatorPool.isTerminated()) {
			personQueue.closeLogger();
		}
	}

	/**
	 * Static function to start the person generator
	 */
	private static void startPersonGenerator() {
		personQueue = new PersonQueue();

		PersonGenerator personGenerator = new PersonGenerator(personQueue);

		Thread generatorThread = new Thread(personGenerator);

		generatorThread.start();
	}
}
