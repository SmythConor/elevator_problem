import java.util.Random;

/**
 * Generator class for generating values
 * @author Conor Smyth 12452382 <conor.smyth39@mail.dcu.ie>
 * @author Phil Brennan 12759011 <philip.brennan36@mail.dcu.ie>
 */
class Generator {
	private static final Integer MAX_PERSON = 5000;
	private static final Integer MAX_FLOOR = 10;
	private static Long startTime = System.currentTimeMillis();

	/**
	 * Generate random number between 1 and 100
	 * @return random Integer between 1 and 100
	 */
	public static Integer generateRandomNumber() {
		return new Random().nextInt(100) + 1;
	}

	/**
	 * Generate Random number between 1 and bound
	 * @param bound max value for number generation
	 * @return random Integer between 1 and bound
	 */
	public static Integer generateRandomNumber(Integer bound) {
		return new Random().nextInt(bound) + 1;
	}

	/**
	 * Generate random double between 1 and 100
	 * @return random Double between 1 and 100
	 */
	public static Double generateRandomNumberD() {
		return new Random().nextDouble() * 100;
	}

	/**
	 * Generate Random number between 1 and bound
	 * @param bound max value for number generation
	 * @return random Double between 1 and bound
	 */
	public static Double generateRandomNumberD(Integer bound) {
		return new Random().nextDouble() * bound;
	}

	/**
	 * Generate a random id for a person
	 * @return random number between 1 and MAX_PERSON
	 */
	public static Integer generateId() {
		return generateRandomNumber(MAX_PERSON);
	}

	/**
	 * Generate a random weight
	 * @return random weight as double between 1 and 100
	 */
	public static Double generateWeight() {
		return generateRandomNumberD(100);
	}

	/**
	 * Generate a random time
	 * @return random time between 1 and 2000
	 */
	public static Integer generateTime() {
		return generateRandomNumber(2000);
	}

	public static Integer generateArrivalTime() {
		return new Long(System.currentTimeMillis() - startTime).intValue();
	}

	/**
	 * Generate a random floor
	 * @return random floor between 0 and MAX_FLOOR
	 */
	public static Integer generateFloor(boolean[] occupiedFloors) {
		int i;

		do{
			i = generateRandomNumber(MAX_FLOOR);
			i = i-1;
		} while(occupiedFloors[i]);

		occupiedFloors[i] = true;

		return i;
	}

	/**
	 * Generate a different random floor
	 * @param floor
	 * @return random floor not equal to floor
	 */
	public static Integer generateFloor(Integer floor) {
		Integer f = 0;

		do {
			f = generateRandomNumber(MAX_FLOOR);
		} while(f == floor);

		return f;
	}
}
