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
package edu.ndsu.eci.tapestry5cayenne.internal;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import edu.ndsu.eci.tapestry5cayenne.internal.PlainTextEncodedValueEncrypter;
import edu.ndsu.eci.tapestry5cayenne.services.EncodedValueEncrypter;

@Test(groups = "all")
public class TestPlainTextEncodedValueEncrypter extends Assert {

  private EncodedValueEncrypter enc;

  @BeforeClass
  void setupEnc() {
    enc = new PlainTextEncodedValueEncrypter();
  }

  @DataProvider(name = "inputs")
  Object[][] inputs() {
    return new Object[][] { { null }, { "" }, { "foo" } };
  }

  @Test(dataProvider = "inputs")
  public void encrypt_returns_input(String input) {
    assertEquals(enc.encrypt(input), input);
  }

  @Test(dataProvider = "inputs")
  public void decrypt_returns_input(String input) {
    assertEquals(enc.decrypt(input), input);
  }
}
