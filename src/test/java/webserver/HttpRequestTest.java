package webserver;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import org.junit.Test;

public class HttpRequestTest {
    private String testDirectory = "./src/test/resources/";

    @Test
    public void request_GET() throws Exception {
    	String httpGetRequestMsg = "GET /user/create?userId=id&password=pwd&name=jy HTTP/1.1\n" + 
        		"Host: localhost:8080\n" + 
        		"Connection: keep-alive\n" + 
        		"Accept: */*\n";
    	StringReader sr = new StringReader(httpGetRequestMsg);
    	HttpRequest request = new HttpRequest(new BufferedReader(sr));
    	
    	assertEquals("GET", request.getMethod());
    	assertEquals("/user/create", request.getPath());
    	assertEquals("keep-alive", request.getHeader("Connection"));
    	assertEquals("id", request.getParameter("userId"));
    }
    
    @Test
    public void request_POST() throws Exception {
    	String httpPostRequestMsg = "POST /user/create HTTP/1.1\n" + 
        		"Host: localhost:8080\n" + 
        		"Connection: keep-alive\n" + 
        		"Content-Length: 30\n" + 
        		"Content-Type: application/x-www-form-urlencoded\n" + 
        		"Accept: */*\n" + 
        		"\n" + 
        		"userId=id&password=pwd&name=jy";
        
    	StringReader sr = new StringReader(httpPostRequestMsg);
    	HttpRequest request = new HttpRequest(new BufferedReader(sr));
    	
    	assertEquals("POST", request.getMethod());
    	assertEquals("/user/create", request.getPath());
    	assertEquals("30", request.getHeader("Content-Length"));
    	assertEquals("application/x-www-form-urlencoded", request.getHeader("Content-Type"));
    	assertEquals("keep-alive", request.getHeader("Connection"));
    	assertEquals("id", request.getParameter("userId"));
    }
}
