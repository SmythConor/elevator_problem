import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import java.util.List;
import java.util.ArrayList;

/**
 * Elevator class for handling the elevator movement etc
 * @author Conor Smyth 12452382 <conor.smyth39@mail.dcu.ie>
 * @author Phil Brennan 12759011 <philip.brennan36@mail.dcu.ie>
 */
class Elevator implements Runnable {
	private final Double MAX_WEIGHT = 1320.0;

	private List<Person> persons;
	private Queue<Map<Person, ReentrantLock>> personQueue;
	private Integer currentFloor;
	private Double currentWeight;

	private Direction direction;

	/**
	 * Contructor to shared person queue
	 * @param personQueue the queue of people arriving
	 */
	public Elevator(Queue<Map<Person, ReentrantLock>> personQueue) {
		this.personQueue = personQueue;
		persons = new ArrayList<Person>();

		currentFloor = 0;
		currentWeight = 0.0;

		direction = Direction.UP;
	}

	/**
	 * Check to see if the person can fit in the elevator
	 * @param p the person to check
	 * @return true if the person can fit
	 */
	private boolean canFit(Person p) {
		Double newWeight = currentWeight + (p.getWeight() + p.getLuggageWeight());

		return newWeight < MAX_WEIGHT;
	}

	/**
	 * Check the direction this elevator is going against the direction
	 * the person wants to go
	 * @param person the person who needs an elevator
	 * @return true if the person is going the correct direction
	 */
	private boolean ourDirection(Person person) {
		int arrivalFloor = person.getArrivalFloor();
		int destinationFloor = person.getDestinationFloor();

		Direction direction = getDirection(destinationFloor, arrivalFloor);

		return this.direction == direction;
	}

	/**
	 * General purpose arrival function to pick up a person
	 */
	public void personArrived() {
		/* Shouldn't need this */
		if(personQueue.size() == 0) {
			return;
		}

		Map<Person, ReentrantLock> personWithLock = personQueue.peek();

		/* Get the person and lock objects */
		Person person = (personWithLock.keySet()).toArray(new Person[0])[0];
		Lock personLock = personWithLock.get(person);

		/* Attempt to get a lock on the person */
		personLock.tryLock();

		/* Check if the person is going this elevators direction and the person can fit */
		//if(!ourDirection(person) || !canFit(person)) {
			personLock.unlock();
		//} else {
			/* Get the arrival and destination floors */
			int arrivalFloor = person.getArrivalFloor();
			int destinationFloor = person.getDestinationFloor();

			if(currentFloor != destinationFloor) {
				Direction directionToPerson = getDirection(currentFloor, arrivalFloor);
				int dir = Math.abs(currentFloor - arrivalFloor);

				move(directionToPerson, dir);
			}

			this.direction = getDirection(arrivalFloor, destinationFloor);

			/* Wait for the number of floors */
			int noFloors = Math.abs(arrivalFloor - destinationFloor);

			move(this.direction, noFloors);

			personQueue.remove(personWithLock);
			Logger.log(person);
			System.out.println("Current floor: " + currentFloor);
		//}
	}

	/**
	 * {@inheritdoc}
	 */
	@Override
		public synchronized void run() {
			while(true) {
				while(personQueue.isEmpty()) {
					System.out.println(personQueue.size());
					try {
						wait();
					} catch(InterruptedException e) {
						e.printStackTrace();
					}
				}

				personArrived();
			}
		}

	/**
	 * General ues function for moving position
	 * @param direction the direction to move
	 * @param noFloors the number of floors to move
	 */
	private void move(Direction direction, int noFloors) {
		//System.out.println("Dir: " + direction + " noFloors: " + noFloors);
		for(int i = 0; i <= noFloors; i++) {
			try {
				Thread.sleep(100);
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
			//System.out.println(currentFloor);

			currentFloor = direction.equals(Direction.UP) ? currentFloor + 1 : currentFloor - 1;
		}
	}

	/**
	 * Get the direction from x to y
	 * @param x the starting point
	 * @param y the end point
	 * @return Direction 
	 */
	private Direction getDirection(int x, int y) {
		int directionRep = x - y;

		return (directionRep > 0) ? Direction.DOWN : Direction.UP;
	}
}
