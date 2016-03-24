import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.TimeUnit;

/**
 * Elevator class for handling the elevator movement etc
 * @author Conor Smyth 12452382 <conor.smyth39@mail.dcu.ie>
 * @author Phil Brennan 12759011 <philip.brennan36@mail.dcu.ie>
 */
class Elevator implements Runnable {
	private final Double MAX_WEIGHT = 1320.0;

	private List<Person> peopleInElevator;
	private PersonQueue personQueue;
	private Integer currentFloor;
	private Double currentWeight;
	private Person buttonPress;


	private Direction direction;

	/**
	 * Contructor to shared person queue
	 * @param personQueue the queue of people arriving
	 */
	public Elevator(PersonQueue personQueue) {
		this.personQueue = personQueue;
		peopleInElevator = new ArrayList<Person>();

		currentFloor = 0;
		currentWeight = 0.0;
		buttonPress = null;
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
	 * @param person the person to pick up
	 */
	public void personArrived(Person person) {
		/* Get the person from the queue */
		Map<Person, ReentrantLock> personWithLock = personQueue.getPersonWithLock(person);
		Lock personLock = personWithLock.get(person);

		/* Attempt to get a lock on the person */
		if(personLock.tryLock()) {

			/* Check if the person can fit */
			if(!canFit(person)) {
				personLock.unlock();
			}
			else {
				/* Remove the person from the queue */
				personQueue.remove(personWithLock);

				/* Mark the floor as being empty */
				personQueue.setEmptyFloor(person.getArrivalFloor());

				/* Add person to this elevators list */
				peopleInElevator.add(person);

				/* Add the persons weight to the total */
				currentWeight += (person.getWeight() + person.getLuggageWeight());
			}
		}
	}

	/**
	* If the person was already locked
	*/
	public void lockedPersonArrived(Person person) {
		Map<Person, ReentrantLock> personWithLock = personQueue.getPersonWithLock(person);

		personQueue.remove(personWithLock);
		personQueue.setEmptyFloor(person.getArrivalFloor());
		peopleInElevator.add(person);
		currentWeight += (person.getWeight() + person.getLuggageWeight());

		Logger.log(person);
	}

	/**
	 * {@inheritdoc}
	 */
	@Override
	public synchronized void run() {
		while(true) {
			/* Check is the queue empty */
			while(personQueue.isEmpty()) {
				/* notify that queue is empty */
				personQueue.notifyOthers();
				try {
					/* Wait for the queue to fill up again */
					personQueue.sleepNow();
				} catch(InterruptedException e) {
					e.printStackTrace();
				}
			}

			/* Check is there a person on the floor */
			if(isPersonOnCurrentFloor(currentFloor)){
				/* Get the person on the current floor */
				Person p = getPersonOnCurrentFloor(currentFloor);

				/* Check if we locked this person */
				if(p.equals(buttonPress)) {
					buttonPress = null;

					/* Add the person to the elevator */
					lockedPersonArrived(p);
				} else if(ourDirection(p)) {
					/* Lock the person and add to the elevator */
					personArrived(p);
				}
			}

			/* Check if people have to be removed from the elevator and remove them */
			if(peopleInElevator.size() > 0) {
				LinkedList<Person> peopleToRemoveAtThisFloor = new LinkedList<>();

				for(Person p : peopleInElevator){
					if(p.getDestinationFloor() == currentFloor) {
						peopleToRemoveAtThisFloor.add(p);
					}
				}

				for(Person p : peopleToRemoveAtThisFloor){
					/* Remove the person from the list */
					peopleInElevator.remove(p);

					/* Remove the persons weight */
					currentWeight -= (p.getWeight() + p.getLuggageWeight());
				}
			}

			/* Check if the elevator is on the way to someone */
			if(buttonPress != null) {
				move(getDirection(currentFloor, buttonPress.getArrivalFloor()));
			} else if (!peopleInElevator.isEmpty()) {
				/* Check where to continue moving */
				int dest = peopleInElevator.get(0).getDestinationFloor();
				Direction dir = getDirection(currentFloor, dest);

				/* Move in that direction */
				move(dir);
			} else if(!personQueue.isEmpty()) {
				/* get the person waiting longest */
				Map<Person, ReentrantLock> topPersonAndLock = personQueue.getOldestButtonPress();

				/* If there's nobody, do nothing */
				if(topPersonAndLock != null) {
					Person topPerson = (topPersonAndLock.keySet()).toArray(new Person[0])[0];
					Direction directOfTopPerson = getDirection(currentFloor, topPerson.getArrivalFloor());

					/* set the button presser as the new person */
					buttonPress = topPerson;

					/* Move to the person */
					move(directOfTopPerson);
				} else {
					try {
						personQueue.sleepNow();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			} else {
				try {
					personQueue.sleepNow();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * General ues function for moving position
	 * @param direction the direction to move
	 */
	private void move(Direction direction) {
		try {
			Thread.sleep(1000);
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
		currentFloor = direction.equals(Direction.UP) ? currentFloor + 1 : currentFloor - 1;
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

	/**
	 * Check is there a person on this floor
	 * @param currentFloor the current floor
	 * @return true if there is person on this floor
	 */
	private boolean isPersonOnCurrentFloor(int currentFloor){
		return personQueue.isPersonOnCurrentFloor(currentFloor);
	}

	/**
	 * Check for a person on current floor
	 * @param currentFloor the current floor
	 * @return the person on this floor
	 */
	private Person getPersonOnCurrentFloor(int currentFloor){
		return personQueue.getPersonOnCurrentFloor(currentFloor);
	}
}
