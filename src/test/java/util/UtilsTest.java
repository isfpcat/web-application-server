package util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UtilsTest {
    private static final Logger logger = LoggerFactory.getLogger(UtilsTest.class);

    @Test
    public void isNullOrEmptySuccessTest() throws Exception {
        String param1 = "userId";
        String param2 = "password";
        Collection<String> collection = new ArrayList<String>();
        collection.add(param1);
        collection.add(param2);

        assertThat(Util.isNullOrEmpty(collection), is(false));
    }
    
    @Test
    public void isNullOrEmptyWithEmpty() throws Exception {
        Collection<String> collection = Collections.emptyList();

        assertThat(Util.isNullOrEmpty(collection), is(true));
    }
    
    @Test
    public void isNullOrEmptyWithNull() throws Exception {
        Collection<String> collection = null;

        assertThat(Util.isNullOrEmpty(collection), is(true));
    }
    
}
