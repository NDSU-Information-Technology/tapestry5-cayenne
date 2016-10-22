package com.googlecode.tapestry5cayenne.internal;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.googlecode.tapestry5cayenne.SearchType;

@Test(groups="all")
public class SearchTypeTest extends Assert {

    @DataProvider(name="prefixes")
    public Object[][] prefixes() {
        return new Object[][] {
                {
                    null,null
                },
                {
                    "",""
                },
                {
                    "Foo","Foo%"
                }
        };
    }
    
    @Test(dataProvider="prefixes")
    void test_prefix(String input, String output) {
        assertEquals(SearchType.PREFIX.maskInput(input),output);
    }
    
    @DataProvider(name="suffixes")
    public Object[][] suffixes() {
        return new Object[][] {
                {
                    null,null
                },
                {
                    "",""
                },
                {
                    "Foo","%Foo"
                }
        };
    }
    
    @Test(dataProvider="suffixes")
    void test_suffix(String input, String output) {
        assertEquals(SearchType.SUFFIX.maskInput(input),output);
    }
    
    
    @DataProvider(name="anywheres")
    public Object[][] anywheres() {
        return new Object[][] {
                {
                    null,null
                },
                {
                    "",""
                },
                {
                    "Foo","%Foo%"
                }
        };
    }
    
    @Test(dataProvider="anywheres")
    void test_anywhere(String input, String output) {
        assertEquals(SearchType.ANYWHERE.maskInput(input),output);
    }
    
    @DataProvider(name="soundexes")
    public Object[][] soundexes() {
        return new Object[][] {
                {
                    null,null
                },
                {
                    "",""
                },
                {
                    "A","A000"
                },
                {
                    "AB","A100"
                },
                {
                    "Hello","H400"
                },
                {
                    "goodbye","G310"
                }
        };
    }
    
    @Test(dataProvider="soundexes")
    void test_soundex(String input, String output) {
        assertEquals(SearchType.SOUNDEX.maskInput(input),output);
    }
    
}
