package javapns.test;

import javapns.communication.*;
import javapns.notification.*;

import org.apache.log4j.*;

class TestFoundation {

	static boolean verifyCorrectUsage(Class testClass, String[] argsProvided, String... argsRequired) {
		if (argsProvided == null) return true;
		int numberOfArgsRequired = countArgumentsRequired(argsRequired);
		if (argsProvided.length < numberOfArgsRequired) {
			String message = getUsageMessage(testClass, argsRequired);
			System.out.println(message);
			return false;
		}
		return true;
	}


	private static String getUsageMessage(Class testClass, String... argsRequired) {
		StringBuilder message = new StringBuilder("Usage: ");
		message.append("java -cp \"<required libraries>\" ");
		message.append(testClass.getName());
		for (String argRequired : argsRequired) {
			boolean optional = argRequired.startsWith("[");
			if (optional) {
				message.append(" [");
				message.append(argRequired.substring(1, argRequired.length() - 1));
				message.append("]");
			} else {
				message.append(" <");
				message.append(argRequired);
				message.append(">");
			}
		}
		return message.toString();
	}


	private static int countArgumentsRequired(String... argsRequired) {
		int numberOfArgsRequired = 0;
		for (String argRequired : argsRequired) {
			if (argRequired.startsWith("[")) break;
			numberOfArgsRequired++;
		}
		return numberOfArgsRequired;
	}


	/**
	 * Enable Log4J with a basic default configuration (console only).
	 */
	public static void configureBasicLogging() {
		try {
			BasicConfigurator.configure();
		} catch (Exception e) {
		}
	}


	/**
	 * Validate a keystore reference and print the results to the console.
	 * 
	 * @param keystoreReference a reference to or an actual keystore
	 * @param password password for the keystore
	 * @param production service to use
	 */
	public static void verifyKeystore(Object keystoreReference, String password, boolean production) {
		try {
			System.out.print("Validating keystore reference: ");
			KeystoreManager.validateKeystoreParameter(keystoreReference);
			System.out.println("VALID  (keystore was found)");
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (password != null) {
			try {
				System.out.print("Verifying keystore content: ");
				AppleNotificationServer server = new AppleNotificationServerBasicImpl(keystoreReference, password, production);
				KeystoreManager.verifyKeystoreContent(server, keystoreReference);
				System.out.println("VERIFIED  (no common mistakes detected)");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
