package webserver;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Test;

public class HttpRequestTest {
    private String testDirectory = "./src/test/resources/";

//    @Test
//    public void request_GET() throws Exception {
//    	InputStream in = new FileInputStream(new File(testDirectory + "Http_GET.txt"));
//    	InputStreamReader ir = new InputStreamReader(in);
//    	HttpRequest request = new HttpRequest(new BufferedReader(ir));
//    	
//    	assertEquals("GET", request.getMethod());
//    	assertEquals("/user/create", request.getPath());
//    	assertEquals("keep-alive", request.getHeader("Connection"));
//    	assertEquals("id", request.getParameter("userId"));
//    }
    
    @Test
    public void request_POST() throws Exception {
    	InputStream in = new FileInputStream(new File(testDirectory + "Http_POST.txt"));
    	InputStreamReader ir = new InputStreamReader(in);
    	HttpRequest request = new HttpRequest(new BufferedReader(ir));
    	
    	assertEquals("POST", request.getMethod());
    	assertEquals("/user/create", request.getPath());
    	assertEquals("30", request.getHeader("Content-Length"));
    	assertEquals("application/x-www-form-urlencoded", request.getHeader("Content-Type"));
    	assertEquals("keep-alive", request.getHeader("Connection"));
    	assertEquals("id", request.getParameter("userId"));
    }
}
