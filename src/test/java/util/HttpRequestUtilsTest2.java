package util;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;


public class HttpRequestUtilsTest2 {
    
	@Test
    public void parseQueryStringTest() {
		String userId = "test";
		String password = "pwd";
		String name = "jylee";
		String email = "a%40naver.com";
		
    	String query = "userId=" + userId + "&password=" + password + "&name=" + name+ "&email=" + email;
    	Map<String, String> result = HttpRequestUtils.parseQueryString(query);
    	
    	assertTrue(userId.equals(result.get("userId")));
    	assertTrue(password.equals(result.get("password")));
    	assertTrue(name.equals(result.get("name")));
    	assertTrue(email.equals(result.get("email")));
    }
	
	@Test
    public void parseCookiesTest() {
		String logined = "true";
		String path = "//";
		
    	String cookie = "logined="+ logined + "; " + "path="+path;
    	Map<String, String> result = HttpRequestUtils.parseCookies(cookie);
    	
    	assertTrue(logined.equals(result.get("logined")));
    	assertTrue(path.equals(result.get("path")));
    }
	
	@Test
    public void parseUriTest() {
		String method = "GET";
		String uri = "/index.html";
		String protocol = "HTTP/1.1";
		
    	String request = method + " " + uri + " " + protocol + " ";
    	Uri result = HttpRequestUtils.parseUri(request);
    	
    	assertTrue(method.equals(result.getMethod()));
    	assertTrue(uri.equals(result.getPath()));
    	assertTrue(protocol.equals(result.getProtocol()));
    }
	
	@Test
    public void parseHeadersTest() {
		String accept = "test/html,*/*";
		String cookie = "logined=true";
		
    	String headers = "Accept: " + accept + "\n" + "Cookie: " + cookie + "\n";
    	Map<String, String> result = HttpRequestUtils.parseHeaders(headers);
    	
    	assertTrue(accept.equals(result.get("Accept")));
    	assertTrue(cookie.equals(result.get("Cookie")));
    }

}
