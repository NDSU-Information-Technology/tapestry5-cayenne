/*  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.googlecode.tapestry5cayenne.services;

/**
 * Used by {@link CayenneEntityEncoder} to encrypt and decrypt pk-containing
 * strings that will be sent to the client. In general, the only thing required
 * of an encrypter implementation is: input.equals(decrypt(encrypt(input)));
 * 
 * @author robertz
 *
 */
public interface EncodedValueEncrypter {

  /**
   * Encrypts the value to be sent to the client.
   * 
   * @param plainTextValue plain text value
   * @return the encrypted value
   */
  String encrypt(String plainTextValue);

  /**
   * Decrypts the value from the client.
   * 
   * @param encryptedValue encrypted value
   * @return the decrypted string.
   */
  String decrypt(String encryptedValue);

}
