package javapns.communication;

import java.io.*;
import java.security.*;
import java.security.cert.*;
import java.security.cert.Certificate;
import java.util.*;

import javapns.communication.exceptions.*;

/**
 * Class responsible for dealing with keystores.
 * 
 * @author Sylvain Pedneault
 */
public class KeystoreManager {

	private static final String REVIEW_MESSAGE = " Please review the procedure for generating a keystore for JavaPNS.";


	/**
	 * Loads a keystore.
	 * 
	 * @param server The server the keystore is intended for
	 * @return A loaded keystore
	 * @throws KeystoreException
	 */
	static KeyStore loadKeystore(AppleServer server) throws KeystoreException {
		return loadKeystore(server, server.getKeystoreStream());
	}


	/**
	 * Loads a keystore.
	 * 
	 * @param server the server the keystore is intended for
	 * @param keystore a keystore containing your private key and the certificate signed by Apple (File, InputStream, byte[], KeyStore or String for a file path)
	 * @return a loaded keystore
	 * @throws KeystoreException
	 */
	static KeyStore loadKeystore(AppleServer server, Object keystore) throws KeystoreException {
		return loadKeystore(server, keystore, false);
	}


	/**
	 * Loads a keystore.
	 * 
	 * @param server the server the keystore is intended for
	 * @param keystore a keystore containing your private key and the certificate signed by Apple (File, InputStream, byte[], KeyStore or String for a file path)
	 * @param verifyKeystore whether or not to perform basic verifications on the keystore to detect common mistakes.
	 * @return a loaded keystore
	 * @throws KeystoreException
	 */
	public static KeyStore loadKeystore(AppleServer server, Object keystore, boolean verifyKeystore) throws KeystoreException {
		if (keystore instanceof KeyStore) return (KeyStore) keystore;
		synchronized (server) {
			InputStream keystoreStream = streamKeystore(keystore);
			if (keystoreStream instanceof WrappedKeystore) return ((WrappedKeystore) keystoreStream).getKeystore();
			KeyStore keyStore;
			try {
				keyStore = KeyStore.getInstance(server.getKeystoreType());
				char[] password = KeystoreManager.getKeystorePasswordForSSL(server);
				keyStore.load(keystoreStream, password);
			} catch (Exception e) {
				throw wrapKeystoreException(e);
			} finally {
				try {
					keystoreStream.close();
				} catch (Exception e) {
				}
			}
			return keyStore;
		}
	}


	/**
	 * Make sure that the provided keystore will be reusable.
	 * 
	 * @param server the server the keystore is intended for
	 * @param keystore a keystore containing your private key and the certificate signed by Apple (File, InputStream, byte[], KeyStore or String for a file path)
	 * @return a reusable keystore
	 * @throws KeystoreException
	 */
	static Object ensureReusableKeystore(AppleServer server, Object keystore) throws KeystoreException {
		if (keystore instanceof InputStream) keystore = loadKeystore(server, keystore, false);
		return keystore;
	}


	/**
	 * Perform basic tests on a keystore to detect common user mistakes.
	 * If a problem is found, a KeystoreException is thrown.
	 * If no problem is found, this method simply returns without exceptions.
	 * 
	 * @param server the server the keystore is intended for
	 * @param keystore a keystore containing your private key and the certificate signed by Apple (File, InputStream, byte[], KeyStore or String for a file path)
	 * @throws KeystoreException
	 */
	public static void verifyKeystoreContent(AppleServer server, Object keystore) throws KeystoreException {
		KeyStore keystoreToValidate = null;
		if (keystore instanceof KeyStore) keystoreToValidate = (KeyStore) keystore;
		else keystoreToValidate = loadKeystore(server, keystore);
		verifyKeystoreContent(keystoreToValidate);
	}


	/**
	 * Perform basic tests on a keystore to detect common user mistakes (experimental).
	 * If a problem is found, a KeystoreException is thrown.
	 * If no problem is found, this method simply returns without exceptions.
	 * 
	 * @param keystore a keystore to verify
	 * @throws KeystoreException thrown if a problem was detected
	 */
	public static void verifyKeystoreContent(KeyStore keystore) throws KeystoreException {
		try {
			int numberOfCertificates = 0;
			Enumeration<String> aliases = keystore.aliases();
			while (aliases.hasMoreElements()) {
				String alias = aliases.nextElement();
				Certificate certificate = keystore.getCertificate(alias);
				if (certificate instanceof X509Certificate) {
					X509Certificate xcert = (X509Certificate) certificate;
					numberOfCertificates++;

					/* Check validity dates */
					xcert.checkValidity();

					/* Check issuer */
					boolean issuerIsApple = xcert.getIssuerDN().toString().contains("Apple");
					if (!issuerIsApple) throw new KeystoreException("Certificate was not issued by Apple." + REVIEW_MESSAGE);

					/* Check certificate key usage */
					boolean[] keyUsage = xcert.getKeyUsage();
					if (!keyUsage[0]) throw new KeystoreException("Certificate usage is incorrect." + REVIEW_MESSAGE);

				}
			}
			if (numberOfCertificates == 0) throw new KeystoreException("Keystore does not contain any valid certificate." + REVIEW_MESSAGE);
			if (numberOfCertificates > 1) throw new KeystoreException("Keystore contains too many certificates." + REVIEW_MESSAGE);

		} catch (KeystoreException e) {
			throw e;
		} catch (CertificateExpiredException e) {
			throw new KeystoreException("Certificate is expired. A new one must be issued.", e);
		} catch (CertificateNotYetValidException e) {
			throw new KeystoreException("Certificate is not yet valid. Wait until the validity period is reached or issue a new certificate.", e);
		} catch (Exception e) {
			/* We ignore any other exception, as we do not want to interrupt the process because of an error we did not expect. */
		}
	}


	static char[] getKeystorePasswordForSSL(AppleServer server) {
		String password = server.getKeystorePassword();
		if (password == null) password = "";
		//		if (password != null && password.length() == 0) password = null;
		char[] passchars = password != null ? password.toCharArray() : null;
		return passchars;
	}


	static KeystoreException wrapKeystoreException(Exception e) {
		if (e != null) {
			String msg = e.toString();
			if (msg.contains("javax.crypto.BadPaddingException")) {
				return new InvalidKeystorePasswordException();
			}
			if (msg.contains("DerInputStream.getLength(): lengthTag=127, too big")) {
				return new InvalidKeystoreFormatException();
			}
			if (msg.contains("java.lang.ArithmeticException: / by zero") || msg.contains("java.security.UnrecoverableKeyException: Get Key failed: / by zero")) {
				return new InvalidKeystorePasswordException("Blank passwords not supported (#38).  You must create your keystore with a non-empty password.");
			}
		}
		return new KeystoreException("Keystore exception: " + e.getMessage(), e);
	}


	/**
	 * Given an object representing a keystore, returns an actual stream for that keystore.
	 * Allows you to provide an actual keystore as an InputStream or a byte[] array,
	 * or a reference to a keystore file as a File object or a String path.
	 * 
	 * @param keystore a keystore containing your private key and the certificate signed by Apple (File, InputStream, byte[], KeyStore or String for a file path)
	 * @return A stream to the keystore.
	 * @throws FileNotFoundException
	 */
	static InputStream streamKeystore(Object keystore) throws InvalidKeystoreReferenceException {
		validateKeystoreParameter(keystore);
		try {
			if (keystore instanceof InputStream) return (InputStream) keystore;
			else if (keystore instanceof KeyStore) return new WrappedKeystore((KeyStore) keystore);
			else if (keystore instanceof File) return new BufferedInputStream(new FileInputStream((File) keystore));
			else if (keystore instanceof String) return new BufferedInputStream(new FileInputStream((String) keystore));
			else if (keystore instanceof byte[]) return new ByteArrayInputStream((byte[]) keystore);
			else return null; // we should not get here since validateKeystore ensures that the reference is valid
		} catch (Exception e) {
			throw new InvalidKeystoreReferenceException("Invalid keystore reference: " + e.getMessage());
		}
	}


	/**
	 * Ensures that a keystore parameter is actually supported by the KeystoreManager.
	 * 
	 * @param keystore a keystore containing your private key and the certificate signed by Apple (File, InputStream, byte[], KeyStore or String for a file path)
	 * @throws InvalidKeystoreReferenceException thrown if the provided keystore parameter is not supported
	 */
	public static void validateKeystoreParameter(Object keystore) throws InvalidKeystoreReferenceException {
		if (keystore == null) throw new InvalidKeystoreReferenceException((Object) null);
		if (keystore instanceof KeyStore) return;
		if (keystore instanceof InputStream) return;
		if (keystore instanceof String) keystore = new File((String) keystore);
		if (keystore instanceof File) {
			File file = (File) keystore;
			if (!file.exists()) throw new InvalidKeystoreReferenceException("Invalid keystore reference.  File does not exist: " + file.getAbsolutePath());
			if (!file.isFile()) throw new InvalidKeystoreReferenceException("Invalid keystore reference.  Path does not refer to a valid file: " + file.getAbsolutePath());
			if (file.length() <= 0) throw new InvalidKeystoreReferenceException("Invalid keystore reference.  File is empty: " + file.getAbsolutePath());
			return;
		}
		if (keystore instanceof byte[]) {
			byte[] bytes = (byte[]) keystore;
			if (bytes.length == 0) throw new InvalidKeystoreReferenceException("Invalid keystore reference. Byte array is empty");
			return;
		}
		throw new InvalidKeystoreReferenceException(keystore);
	}

}
