import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

class PersonQueue {
	private Queue<Map<Person, ReentrantLock>> personQueue;

	public PersonQueue() {
		personQueue = new ConcurrentLinkedQueue<>();
	}

	public synchronized boolean add(Person person) {
		Map<Person, ReentrantLock> personWithLock = new ConcurrentHashMap<>();

		personWithLock.put(person, new ReentrantLock());

		return personQueue.add(personWithLock);
	}

	public synchronized Map<Person, ReentrantLock> peek() {
		return personQueue.peek();
	}

	public synchronized Map<Person, ReentrantLock> remove() {
		return personQueue.remove();
	}

	public synchronized boolean remove(Map<Person, ReentrantLock> personWithLock) {
		return personQueue.remove(personWithLock);
	}

	public boolean isEmpty() {
		return size() == 0;
	}

	public int size() {
		return personQueue.size();
	}
}
