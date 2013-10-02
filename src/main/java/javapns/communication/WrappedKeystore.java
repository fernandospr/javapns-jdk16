package javapns.communication;

import java.io.*;
import java.security.*;

/**
 * Special wrapper for a KeyStore.
 * 
 * @author Sylvain Pedneault
 */
class WrappedKeystore extends InputStream {

	private final KeyStore keystore;


	public WrappedKeystore(KeyStore keystore) {
		this.keystore = keystore;
	}


	@Override
	public int read() throws IOException {
		return 0;
	}


	public KeyStore getKeystore() {
		return keystore;
	}

}
