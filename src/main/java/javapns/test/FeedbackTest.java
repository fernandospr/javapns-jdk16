package javapns.test;

import java.util.*;

import javapns.*;
import javapns.communication.exceptions.*;
import javapns.devices.*;

/**
 * A command-line test facility for the Feedback Service.
 * <p>Example:  <code>java -cp "[required libraries]" javapns.test.FeedbackTest keystore.p12 mypass</code></p>
 * 
 * <p>By default, this test uses the sandbox service.  To switch, add "production" as a third parameter:</p>
 * <p>Example:  <code>java -cp "[required libraries]" javapns.test.FeedbackTest keystore.p12 mypass production</code></p>
 * 
 * @author Sylvain Pedneault
 */
public class FeedbackTest extends TestFoundation {

	/**
	 * Execute this class from the command line to run tests.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		/* Verify that the test is being invoked  */
		if (!verifyCorrectUsage(FeedbackTest.class, args, "keystore-path", "keystore-password", "[production|sandbox]")) return;

		/* Initialize Log4j to print logs to console */
		configureBasicLogging();

		/* Get a list of inactive devices */
		feedbackTest(args);
	}


	private FeedbackTest() {
	}


	/**
	 * Retrieves a list of inactive devices from the Feedback service.
	 * @param args
	 */
	private static void feedbackTest(String[] args) {
		String keystore = args[0];
		String password = args[1];
		boolean production = args.length >= 3 ? args[2].equalsIgnoreCase("production") : false;
		try {
			List<Device> devices = Push.feedback(keystore, password, production);

			for (Device device : devices) {
				System.out.println("Inactive device: " + device.getToken());
			}
		} catch (CommunicationException e) {
			e.printStackTrace();
		} catch (KeystoreException e) {
			e.printStackTrace();
		}
	}

}
