package webserver;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.Pair;


public class HttpResponse {
	private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);
	
	public void writeHeader(DataOutputStream dos, Collection<Pair> headerProperty) {
		try {
			for (Pair property : headerProperty) {
				dos.writeBytes(property.getKey()+": " + property.getValue() + "\r\n");
			}
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}
	
	public void response(DataOutputStream dos, int statusCode, Collection<Pair> headerProperty, byte[] body) {
		try {
			if (statusCode == 200) {
				dos.writeBytes("HTTP/1.1 200 OK \r\n");
			} else if (statusCode == 302) {
				dos.writeBytes("HTTP/1.1 302 Found \r\n");
				dos.writeBytes("Location: http://localhost:8080/index.html \r\n");
			}
			
			writeHeader(dos, headerProperty);
			dos.writeBytes("\r\n");	
			
			responseBody(dos, body);
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

    public void responseBody(DataOutputStream dos, byte[] body) {
        try {
        	if (body.length > 0) {
	            dos.write(body, 0, body.length);
	            dos.flush();
        	}
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
