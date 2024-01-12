package webserver;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.Pair;


public class HttpResponse {
	private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);
	private DataOutputStream dos = null;
	private Collection<Pair> headerMap;
	
	public HttpResponse(OutputStream out) {
		this.dos = new DataOutputStream(out);
		this.headerMap = new ArrayList<Pair>();
	}
	
	public void addHeaderProperty(Pair property) {
		headerMap.add(property);
	}
	
	public void redirect(String uri) {
		try {
			dos.writeBytes("HTTP/1.1 302 Found \r\n");
			dos.writeBytes("Location: http://localhost:8080/" + uri + "\r\n");
			writeHeader();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeHeader() {
		try {
			for (Pair property : headerMap) {
				dos.writeBytes(property.getKey()+": " + property.getValue() + "\r\n");
			}
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}
	
	public void forward(String uri) {
		try {
			byte[] body = Files.readAllBytes(new File("./webapp/" + uri).toPath());
			
			dos.writeBytes("HTTP/1.1 200 OK \r\n");
			
			if (uri.endsWith("html")) {
				headerMap.add(new Pair("Content-Type", "text/html"));
			} else if (uri.endsWith("css")) {
				headerMap.add(new Pair("Content-Type", "text/css"));
			} else {
				headerMap.add(new Pair("Content-Type", "*/*"));
			}
			headerMap.add(new Pair("Content-Length", String.valueOf(body.length)));
			writeHeader();
			
			dos.writeBytes("\r\n");
			dos.write(body, 0, body.length);
			dos.flush();
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

    public void forwardWithBody(byte[] body) {
        try {
        	dos.writeBytes("HTTP/1.1 200 OK \r\n");
        	headerMap.add(new Pair("Content-Type", "text/html"));
        	headerMap.add(new Pair("Content-Length", String.valueOf(body.length)));
        	writeHeader();
        	dos.writeBytes("\r\n");
        	
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
