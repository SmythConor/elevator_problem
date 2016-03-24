import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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

	private Direction direction;

	/**
	 * Contructor to shared person queue
	 * @param personQueue the queue of people arriving
	 */
	public Elevator(PersonQueue personQueue) {
		System.out.println("elevator created");
		this.personQueue = personQueue;
		peopleInElevator = new ArrayList<Person>();

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
	public void personArrived(Person person) {
		System.out.println("Person #"+person.getPersonId()+" got into the in elevator");

		/* Shouldn't need this */
		if(personQueue.size() == 0) {
			return;
		}

		Map<Person, ReentrantLock> personWithLock = personQueue.getPersonWithLock(person);

		/* Get the person and lock objects */
		//Person person = (personWithLock.keySet()).toArray(new Person[0])[0];
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

				//move(directionToPerson);
			}

			this.direction = getDirection(arrivalFloor, destinationFloor);

			//move(this.direction);

			personQueue.remove(personWithLock);
			peopleInElevator.add(person);
			Logger.log(person);
			System.out.println("Person #"+person.getPersonId() + " entered on floor: " + person.getArrivalFloor());
		//}
	}

	/**
	 * {@inheritdoc}
	 */
	@Override
		public synchronized void run() {
			System.out.println("elevator thread begins");
			while(true) {
				while(personQueue.isEmpty()) {
					System.out.println(personQueue.size()+" No one waiting for lifts...");
					notifyAll();

					try {
						wait();
					} catch(InterruptedException e) {
						e.printStackTrace();
					}
				}

				if(isPersonOnCurrentFloor(currentFloor)){
					Person p = getPersonOnCurrentFloor(currentFloor);
					System.out.println("Person #" +p.getPersonId()+" is on this floor");
					personArrived(p);
				}

				if(peopleInElevator.size() > 0){
					LinkedList<Person> peopleToRemoveAtThisFloor = new LinkedList<>();

					for(Person p : peopleInElevator){
						if(p.getDestinationFloor() == currentFloor) {
							peopleToRemoveAtThisFloor.add(p);
						}
					}

					for(Person p : peopleToRemoveAtThisFloor){
						peopleInElevator.remove(p);
						System.out.println("Person #"+p.getPersonId()+" arrived at their floor");
					}
				}

				Map<Person, ReentrantLock> topPersonAndLock = personQueue.peek();
				Person topPerson =(topPersonAndLock.keySet()).toArray(new Person[0])[0];
				Direction directOfTopPerson = getDirection(currentFloor, topPerson.getArrivalFloor());

				if(continueDirection(currentFloor, direction))
					move(direction);
				else if(peopleInElevator.isEmpty())
					move(directOfTopPerson);
				else
					move(getDirection(currentFloor, peopleInElevator.get(0).getArrivalFloor()));
			}
		}

	/**
	 * General ues function for moving position
	 * @param direction the direction to move
	 */
	private void move(Direction direction) {
		//System.out.println("Dir: " + direction + " noFloors: " + noFloors)
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

	private boolean continueDirection(int currentFloor, Direction direction){
		return personQueue.continueDirection(currentFloor, direction);
	}

	private boolean isPersonOnCurrentFloor(int currentFloor){
		return personQueue.isPersonOnCurrentFloor(currentFloor);
	}

	private Person getPersonOnCurrentFloor(int currentFloor){
		return personQueue.getPersonOnCurrentFloor(currentFloor);
	}
}
