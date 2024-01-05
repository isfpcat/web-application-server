package webserver;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.Pair;


public class ResponseHandler extends Thread{
	private static final Logger log = LoggerFactory.getLogger(ResponseHandler.class);
	
	static void response(DataOutputStream dos, int statusCode, Collection<Pair> headerProperty, byte[] body) {
		try {
			if (statusCode == 200) {
				dos.writeBytes("HTTP/1.1 200 OK \r\n");
			} else if (statusCode == 302) {
				dos.writeBytes("HTTP/1.1 302 Found \r\n");
				dos.writeBytes("Location: http://localhost:8080/index.html \r\n");
			}
			
			for (Pair property : headerProperty) {
				dos.writeBytes(property.getKey()+": " + property.getValue() + "\r\n");
			}
			
			dos.writeBytes("\r\n");	
			
			if (body.length > 0) {
				responseBody(dos, body);
			}
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

    static private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
