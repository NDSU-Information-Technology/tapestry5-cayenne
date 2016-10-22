package com.googlecode.tapestry5cayenne.internal;

import com.googlecode.tapestry5cayenne.services.EncodedValueEncrypter;

/**
 * Default implementation of EncodedValueEncrypter.
 * This implementation simply returns the plain-text strings passed to it.
 * @author robertz
 *
 */
public class PlainTextEncodedValueEncrypter implements EncodedValueEncrypter {

    public String decrypt(String encryptedValue) {
        return encryptedValue;
    }

    public String encrypt(String plainTextValue) {
        return plainTextValue;
    }

}
