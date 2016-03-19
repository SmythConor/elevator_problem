import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Person Generator for generating people randomly
 * @author Conor Smyth 12452382 <conor.smyth39@mail.dcu.ie>
 * @author Phil Brennan 12759011 <philip.brennan36@mail.dcu.ie>
 */
class PersonGenerator implements Runnable {
	private Queue<Map<Person, ReentrantLock>> personQueue;

	public PersonGenerator(Queue<Map<Person, ReentrantLock>> personQueue) {
		this.personQueue = personQueue;
	}

	@Override
	public synchronized void run() {
	int i = 0;
		while(i < 10) {
			if(personQueue.size() > 0) {
				System.out.println("Notified ye prick");
				notify();
			}

			Person person = new Person();
			System.out.println(person);

			Map<Person, ReentrantLock> personMap = new ConcurrentHashMap<>();
			personMap.put(person, new ReentrantLock());

			personQueue.add(personMap);

			int wait = Generator.generateTime();

			try {
				Thread.sleep(wait);
			} catch(InterruptedException e) {
				System.out.println("Person generater");
				e.printStackTrace();
			}

			i++;
		}
	}
}
