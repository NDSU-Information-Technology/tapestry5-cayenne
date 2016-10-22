package com.googlecode.tapestry5cayenne.services;

/**
 * Used by {@link CayenneEntityEncoder} to encrypt and decrypt pk-containing strings
 * that will be sent to the client.
 * In general, the only thing required of an encrypter implementation is:
 * input.equals(decrypt(encrypt(input)));
 * @author robertz
 *
 */
public interface EncodedValueEncrypter {
    
    /**
     * Encrypts the value to be sent to the client.
     * @param plainTextValue
     * @return the encrypted value
     */
    String encrypt(String plainTextValue);
    
    /**
     * Decrypts the value from the client.
     * @param encryptedValue
     * @return the decrypted string.
     */
    String decrypt(String encryptedValue);

}
