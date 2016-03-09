import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Person Generator for generating people randomly
 * @author Conor Smyth 12452382 <conor.smyth39@mail.dcu.ie>
 * @author Phil Brennan <@mail.dcu.ie>
 */
class PersonGenerator implements Runnable {
	private Queue<Map<Person, ReentrantLock>> personQueue;

	public PersonGenerator(Queue<Map<Person, ReentrantLock>> personQueue) {
		this.personQueue = personQueue;
	}

	@Override
	public void run() {
		while(true) {
			if(personQueue.size() > 0) {
				//notify thread pool somehow
			}

			Person person = new Person();

			Map<Person, ReentrantLock> personMap = new ConcurrentHashMap<>();
			personMap.put(p, new ReentrantLock());

			personQueue.add(personMap);

			int wait = Generator.generateTime();

			try {
				Thread.sleep(wait);
			} catch(InterruptedException e) {
				System.out.println("Person generater");
				e.printStackTrace();
			}
		}
	}
}
