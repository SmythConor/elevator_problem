import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Person Queue for holding information about the queue
 * Also handles synchronization of the personQueue
 * @author Conor Smyth 12452382 <conor.smyth39@mail.dcu.ie>
 * @author Phil Brennan 12759011 <philip.brennan36@mail.dcu.ie>
 */
class PersonQueue {
	private Queue<Map<Person, ReentrantLock>> personQueue;
	private boolean[] occupiedFloors = new boolean[10];
	private long generatorID;
	private Logger logger;

	public PersonQueue() {
		for(int i=1; i<occupiedFloors.length;i++)
			occupiedFloors[i] = false;

		personQueue = new ConcurrentLinkedQueue<>();

		logger = new Logger();
	}

	/**
	 * Add a person to the queue
	 * @param person the person to add
	 * @return true if the person gets added
	 */
	public synchronized boolean add(Person person) {
		Map<Person, ReentrantLock> personWithLock = new ConcurrentHashMap<>();

		personWithLock.put(person, new ReentrantLock());

		return personQueue.add(personWithLock);
	}

	/**
	 * Peek the person from top of the queue
	 * @return peek at the person from the top of the queue
	 */
	public synchronized Map<Person, ReentrantLock> peek() {
		return personQueue.peek();
	}

	/**
	 * Remove a person from the top of the queue
	 * @return person from the top of the queue
	 */
	public synchronized Map<Person, ReentrantLock> remove() {
		return personQueue.remove();
	}

	/**
	 * Remove a person from the queue
	 * @param personWithLock the person to remove
	 * @return true if the person was removed
	 */
	public synchronized boolean remove(Map<Person, ReentrantLock> personWithLock) {
		return personQueue.remove(personWithLock);
	}

	/**
	 * Return true if the queue is empty
	 * @return true if the queue is empty
	 */
	public boolean isEmpty() {
		return size() == 0;
	}

	/**
	 * Return the size of the queue
	 * @return the size of the queue as an int
	 */
	public int size() {
		return personQueue.size();
	}

	/**
	 * Get a specific person from the queue
	 * @param person the person to get
	 * @return the person matching param
	 */
	public synchronized Map<Person, ReentrantLock> getPersonWithLock(Person person){
		for(Map<Person, ReentrantLock> p : personQueue){
			if(p.keySet().contains(person)) {
				return p;
			}
		}

		return null;
	}

	/**
	 * Cycle through the queue to get the oldest person not locked
	 * @return person in the queue the longest and their lock
	 */
	public synchronized Map<Person, ReentrantLock> getOldestButtonPress() {
		for(Map<Person, ReentrantLock> pair : personQueue){
			for(Person p : pair.keySet()){
				if(pair.get(p).tryLock())
					return pair;
			}
		}

		return null;
	}

	/**
	 * Check if there was a person on the current floor
	 * @param currentFloor the floor to check
	 * @return true if there is a person on the floor that isn't locked
	 */
	public synchronized boolean isPersonOnCurrentFloor(int currentFloor) {
		for (Map<Person, ReentrantLock> pair : personQueue) {
			for(Person p : pair.keySet()){
				Lock personLock = pair.get(p);

				if(p.getArrivalFloor() == currentFloor)
					if(personLock.tryLock())
						return true;
			}
		}
		return false;
	}

	/**
	 * Get the person on the current floor
	 * @param currentFloor the floor to check
	 * @return the person on that floor otherwise null
	 */
	public synchronized Person getPersonOnCurrentFloor(int currentFloor) {
		for (Map<Person, ReentrantLock> pair : personQueue) {
			for(Person p : pair.keySet()){
				if(p.getArrivalFloor() == currentFloor)
					return p;
			}
		}

		return null;
	}

	/**
	 * Call notifyAll on this object
	 */
	public synchronized void notifyOthers() {
		notifyAll();
	}

	/**
	 * Call wait on this object
	 */
	public synchronized void sleepNow() throws InterruptedException {
		wait();
	}

	/**
	 * Get the occupiedFloors array
	 */
	public boolean[] getOccupiedFloors() {
		return occupiedFloors;
	}

	/**
	 * Set a floor as being occupied
	 */
	public void setOccupiedFloor(int i) {
		occupiedFloors[i] = true;
	}

	/**
	 * Set a floor as empty
	 */
	public void setEmptyFloor(int i){
		occupiedFloors[i] = false;
	}

	/**
	 * Log a person
	 */
	public void log(Person person) {
		logger.log(person);
	}

	/**
	 * Close the logger
	 */
	public void closeLogger() {
		logger.close();
	}
}
