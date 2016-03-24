import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Person Generator for generating people randomly
 * @author Conor Smyth 12452382 <conor.smyth39@mail.dcu.ie>
 * @author Phil Brennan 12759011 <philip.brennan36@mail.dcu.ie>
 */
class PersonGenerator implements Runnable {
	private PersonQueue personQueue;


	public PersonGenerator(PersonQueue personQueue) {
		this.personQueue = personQueue;
	}

	@Override
		public synchronized void run() {
			while(true) {
				while (personQueue.size() < 10) {
					if (personQueue.size() > 0) {
						personQueue.notifyOthers();
					}

					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Person person = new Person(personQueue.getOccupiedFloors());

					personQueue.setOccupiedFloor(person.getArrivalFloor());
					personQueue.add(person);
					personQueue.notifyOthers();
				}

				try {
					personQueue.sleepNow();
				} catch (InterruptedException e) {
					System.out.println("Person generator");
					e.printStackTrace();
				}
			}
		}
}
