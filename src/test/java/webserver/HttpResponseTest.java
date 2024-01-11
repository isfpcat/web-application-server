package webserver;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;

import org.junit.Test;

import util.Pair;

public class HttpResponseTest {
	private String testDirectory = "./src/test/resources/";
	
	@Test
    public void redirect_TEST() throws Exception {
		String uri = "index.html";
    	String httpResponseMsg = "HTTP/1.1 302 Found \r\n" + 
    			"Location: http://localhost:8080/" + uri + "\r\n";
    	
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	HttpResponse response = new HttpResponse(baos);
    	response.redirect(uri);
    	assertEquals(httpResponseMsg, baos.toString());
    }
	
	@Test
    public void redirectWithHeader_TEST() throws Exception {
		String uri = "index.html";
    	String httpResponseMsg = "HTTP/1.1 302 Found \r\n" + 
    			"Location: http://localhost:8080/" + uri + "\r\n" + 
    			"Set-Cookie: logined=true\r\n";
    	
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	HttpResponse response = new HttpResponse(baos);
    	
    	Pair property = new Pair("Set-Cookie", "logined=true");
    	response.addHeaderProperty(property);
    	response.redirect(uri);
    	
    	assertEquals(httpResponseMsg, baos.toString());
    }
	
	@Test
    public void forwardHtml_TEST() throws Exception {
		String uri = "index.html";
		byte[] body = Files.readAllBytes(new File(testDirectory + uri).toPath());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		out.write(body, 0, body.length);
		out.flush();
		String bodyRes = out.toString();
		
    	String httpResponseMsg = "HTTP/1.1 200 OK \r\n" +
    			"Set-Cookie: logined=true\r\n" +
    			"Content-Type: text/html\r\n" + 
    			"Content-Length: " + body.length + "\r\n" +
    			"\r\n" +
    			bodyRes;
    	
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	HttpResponse response = new HttpResponse(baos);
    	
    	Pair property = new Pair("Set-Cookie", "logined=true");
    	response.addHeaderProperty(property);
    	response.forward(testDirectory + uri);
    	
    	assertEquals(httpResponseMsg, baos.toString());
    }
	
	@Test
    public void forwardHtmlWithAddedHeader_TEST() throws Exception {
		String uri = "index.html";
		byte[] body = Files.readAllBytes(new File(testDirectory + uri).toPath());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		out.write(body, 0, body.length);
		out.flush();
		String bodyRes = out.toString();
		
    	String httpResponseMsg = "HTTP/1.1 200 OK \r\n" + 
    			"Content-Type: text/html\r\n" + 
    			"Content-Length: " + body.length + "\r\n" +
    			"\r\n" +
    			bodyRes;
    	
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	HttpResponse response = new HttpResponse(baos);
    	
    	response.forward(testDirectory + uri);
    	
    	assertEquals(httpResponseMsg, baos.toString());
    }
	
	@Test
    public void forwardCSS_TEST() throws Exception {
		String uri = "style.css";
		byte[] body = Files.readAllBytes(new File(testDirectory + uri).toPath());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		out.write(body, 0, body.length);
		out.flush();
		String bodyRes = out.toString();
		
    	String httpResponseMsg = "HTTP/1.1 200 OK \r\n" + 
    			"Content-Type: text/css\r\n" + 
    			"Content-Length: " + body.length + "\r\n" +
    			"\r\n" +
    			bodyRes;
    	
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	HttpResponse response = new HttpResponse(baos);
    	
    	response.forward(testDirectory + uri);
    	
    	assertEquals(httpResponseMsg, baos.toString());
    }
	
	@Test
    public void forwardFavicon_TEST() throws Exception {
		String uri = "favicon.ico";
		byte[] body = Files.readAllBytes(new File(testDirectory + uri).toPath());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		out.write(body, 0, body.length);
		out.flush();
		String bodyRes = out.toString();
		
    	String httpResponseMsg = "HTTP/1.1 200 OK \r\n" + 
    			"Content-Type: */*\r\n" + 
    			"Content-Length: " + body.length + "\r\n" +
    			"\r\n" +
    			bodyRes;
    	
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	HttpResponse response = new HttpResponse(baos);
    	
    	response.forward(testDirectory + uri);
    	
    	assertEquals(httpResponseMsg, baos.toString());
    }
	
	@Test
    public void forwardBody_TEST() throws Exception {
		byte[] body = "<body></body>".getBytes();
		
    	String httpResponseMsg = "HTTP/1.1 200 OK \r\n" + 
    			"Content-Type: text/html\r\n" + 
    			"Content-Length: " + body.length + "\r\n" +
    			"\r\n" +
    			"<body></body>";
    	
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	HttpResponse response = new HttpResponse(baos);
    	
    	response.forwardWithBody(body);
    	
    	assertEquals(httpResponseMsg, baos.toString());
    }
}
