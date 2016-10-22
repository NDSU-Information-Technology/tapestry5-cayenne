package com.googlecode.tapestry5cayenne.internal;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.googlecode.tapestry5cayenne.services.EncodedValueEncrypter;

@Test(groups="all")
public class TestPlainTextEncodedValueEncrypter extends Assert {
    
    private EncodedValueEncrypter enc;
    
    @BeforeClass
    void setupEnc() {
        enc = new PlainTextEncodedValueEncrypter();
    }
    
    @DataProvider(name="inputs")
    Object[][] inputs() {
        return new Object[][] {
            {
                null
            },
            {
                ""
            },
            {
                "foo"
            }
        };
    }
    
    @Test(dataProvider="inputs")
    public void encrypt_returns_input(String input) {
        assertEquals(enc.encrypt(input),input);
    }
    
    @Test(dataProvider="inputs")
    public void decrypt_returns_input(String input) {
        assertEquals(enc.decrypt(input),input);
    }
}
