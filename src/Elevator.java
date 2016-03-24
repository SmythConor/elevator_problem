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
	private Person buttonPress;


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
	 */
	public void personArrived(Person person) {
		System.out.println("Person #"+person.getPersonId()+" got into the in elevator");

		Map<Person, ReentrantLock> personWithLock = personQueue.getPersonWithLock(person);

		Lock personLock = personWithLock.get(person);

		/* Attempt to get a lock on the person */
		if(personLock.tryLock()) {
			System.out.println("Elevator"+ Thread.currentThread().getId() + ": Person Lock Successful");
			/* Check if the person is going this elevators direction and the person can fit */
			//if(!ourDirection(person) || !canFit(person)) {
			personLock.unlock();
			//} else {
			/* Get the arrival and destination floors */
			int arrivalFloor = person.getArrivalFloor();
			int destinationFloor = person.getDestinationFloor();

			if (currentFloor != destinationFloor) {
				Direction directionToPerson = getDirection(currentFloor, arrivalFloor);

				//move(directionToPerson);
			}

			this.direction = getDirection(arrivalFloor, destinationFloor);

			//move(this.direction);

			personQueue.remove(personWithLock);
			personQueue.setEmptyFloor(person.getArrivalFloor());
			peopleInElevator.add(person);

			Logger.log(person);
			System.out.println("Person #" + person.getPersonId() + " entered on floor: " + person.getArrivalFloor());
		}
		else
			System.out.println("Elevator "+ Thread.currentThread().getId() + ": Person Lock Fail");
		//}
	}

	public void LockedPersonArrived(Person person) {
		System.out.println("Person #"+person.getPersonId()+" got into the in elevator");

		Map<Person, ReentrantLock> personWithLock = personQueue.getPersonWithLock(person);

		Lock personLock = personWithLock.get(person);

		/* Attempt to get a lock on the person */

			System.out.println("Elevator"+ Thread.currentThread().getId() + ": Already had Lock");
			/* Check if the person is going this elevators direction and the person can fit */
			//if(!ourDirection(person) || !canFit(person)) {
//			personLock.unlock();
			//} else {
			/* Get the arrival and destination floors */





			//move(this.direction);

			personQueue.remove(personWithLock);
			personQueue.setEmptyFloor(person.getArrivalFloor());
			peopleInElevator.add(person);

			Logger.log(person);
			System.out.println("Person #" + person.getPersonId() + " entered on floor: " + person.getArrivalFloor());
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
					personQueue.notifyOthers();

					try {
						personQueue.sleepNow();
					} catch(InterruptedException e) {
						e.printStackTrace();
					}
				}

				if(isPersonOnCurrentFloor(currentFloor)){
					Person p = getPersonOnCurrentFloor(currentFloor);
					System.out.println("Person #" +p.getPersonId()+" is on this floor");
					if(p.equals(buttonPress)) {
						buttonPress = null;
						LockedPersonArrived(p);
					}
					else if(getDirection(currentFloor, p.getDestinationFloor()).equals(direction))
						personArrived(p);
					else{
						System.out.println("Elevator "+ Thread.currentThread().getId() + ": just ignored a person not going my way");
					}
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

				if(buttonPress != null){
					move(getDirection(currentFloor, buttonPress.getArrivalFloor()));
				}
				else if (!peopleInElevator.isEmpty()){
					int dest = peopleInElevator.get(0).getDestinationFloor();
					Direction dir = getDirection(currentFloor, dest);
					move(dir);
				}
				else if(!personQueue.isEmpty()) {
					Map<Person, ReentrantLock> topPersonAndLock = personQueue.getOldestButtonPress();
					if(topPersonAndLock != null){
						Person topPerson = (topPersonAndLock.keySet()).toArray(new Person[0])[0];
						Direction directOfTopPerson = getDirection(currentFloor, topPerson.getArrivalFloor());
						System.out.println("Elevator "+ Thread.currentThread().getId() + ": is going to Floor #"+topPerson.getArrivalFloor());
						buttonPress = topPerson;
						move(directOfTopPerson);
					}
					else{
						System.out.println("Elevator "+ Thread.currentThread().getId() + ": Everyone in PeopleQueue were Locked");
						try {
							personQueue.sleepNow();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				else{
					try {
						personQueue.sleepNow();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				System.out.println("Elevator "+ Thread.currentThread().getId() +"  is now on Floor #"+currentFloor);
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
