import java.io.File;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import static java.nio.charset.StandardCharsets.UTF_8;
/**
 * Logger class for logging
 * @author Conor Smyth 12452382 <conor.smyth39@mail.dcu.ie>
 * @author Phil Brennan 12759011 <philip.brennan36@mail.dcu.ie>
 */
class Logger {
	private static OutputStream out;

	public Logger() {
		File output = new File("output.dat");

		if(output.exists()) {
			output.delete();
		}

		try {
			output.createNewFile();
		} catch(IOException e) {}

		try {
			out = new FileOutputStream(output);
		} catch(FileNotFoundException e) {
			System.out.println("File not found");
			e.printStackTrace();
		}
	}

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
		builder.append("\n");

		try {
		out.write(builder.toString().getBytes(UTF_8));
		out.flush();
		} catch(IOException e) {
			System.out.println("Issue writing to file");
			e.printStackTrace();
		}
	}

	public void close() {
		try {
		out.close();
		} catch(IOException e) {
			System.out.println("Issue closing file");
		}
	}
}
