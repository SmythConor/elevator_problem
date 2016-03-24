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

    public synchronized Map<Person, ReentrantLock> getPersonWithLock(Person person){
        for(Map<Person, ReentrantLock> p : personQueue){
            if(p.keySet().contains(person)) {
                return p;
            }
        }
        return null;
    }

    public synchronized boolean isPersonOnCurrentFloor(int currentFloor){
        for (Map<Person, ReentrantLock> pair : personQueue) {
            for(Person p : pair.keySet()){
                if(p.getArrivalFloor() == currentFloor)
                    return true;
            }
        }
        return false;
    }

    public boolean continueDirection(int currentFloor, Direction direction){
        for (Map<Person, ReentrantLock> pair : personQueue) {
            for(Person p : pair.keySet()){
                if((p.getDestinationFloor()-currentFloor > 0) && (Direction.UP == direction)){
                    return true;
                }
                else if((p.getDestinationFloor()-currentFloor < 0) && (Direction.DOWN == direction)){
                    return true;
                }
            }
        }
        return false;
    }

    public synchronized Person getPersonOnCurrentFloor(int currentFloor){
        for (Map<Person, ReentrantLock> pair : personQueue) {
            for(Person p : pair.keySet()){
                if(p.getArrivalFloor() == currentFloor)
                    return p;
            }
        }
        return null;
    }
}