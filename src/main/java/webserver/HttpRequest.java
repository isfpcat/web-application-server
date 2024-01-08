package webserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Strings;

import util.HttpRequestUtils;
import util.Uri;

public class HttpRequest {
	private BufferedReader reader;
	
	private String method;
	private String path;
	private String queryString;
	private Map<String, String> headerMaps;
	private Map<String, String> userParams;
	
	public HttpRequest (BufferedReader in) {
		this.reader = in;
		
		String line = "";
    	try {
    		line = reader.readLine();
    		if (line == null) {
    			return;
    		}
    		Uri uri = HttpRequestUtils.parseUri(line);
			this.method = uri.getMethod();
			this.path = uri.getPath();
    		
			String header = "";
			while(!"".equals((line = reader.readLine()))){
				if (line == null) break;
				header += line+"\n";
			}
			
			String body = "";
			while(!"".equals((line = reader.readLine()))){
				if (line == null) break;
				body += line+"\n";
			}
			
			this.queryString = uri.getQueryParams();
			if (Strings.isNullOrEmpty(this.queryString)) {
				this.queryString = body;
			}
			
			this.headerMaps = HttpRequestUtils.parseHeaders(header);
			this.userParams = HttpRequestUtils.parseQueryString(this.queryString);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
	
	public String getMethod() {
		return this.method;
	}
	
	public String getPath() {
		return this.path;
	}
	
	public String getHeader(String field) {
		return this.headerMaps.get(field);
	}
	
	public String getParameter(String field) {
		return this.userParams.get(field);
	}
}
