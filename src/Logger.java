/**
 * Logger class for logging
 * @author Conor Smyth 12452382 <conor.smyth39@mail.dcu.ie>
 * @author Phil Brennan <@mail.dcu.ie>
 */
class Logger {
	public static void log(Person p) {
		StringBuilder builder = new StringBuilder();

		builder.append("Person: ");
		builder.append(p.getPersonId());
		builder.append(" makes request at: ");
		builder.append(p.getArrivalTime());
		builder.append(" starting at floor: ");
		builder.append(p.getArrivalFloor());
		builder.append(" with the destination floor: ");
		builder.append(p.getDestinationFloor());

		System.out.println(builder);
	}
}
