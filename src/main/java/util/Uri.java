package util;

public class Uri {
	  String method;
      String path;
      String queryParams;
      String protocol;

      Uri(String method, String uri, String protocol) {
          this.method = method.trim();
          if (uri.contains("?")) {
        	  String[] tokens = uri.split("\\?");
        	  if (tokens.length == 2) {
        		  this.path = tokens[0];
            	  this.queryParams = tokens[1];
        	  }
          } else {
        	  this.path = uri.trim();
          }
          
          this.protocol = protocol.trim();
      }

	public String getMethod() {
		return method;
	}

	public String getPath() {
		return path;
	}

	public String getQueryParams() {
		return queryParams;
	}

	public String getProtocol() {
		return protocol;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((method == null) ? 0 : method.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		result = prime * result + ((protocol == null) ? 0 : protocol.hashCode());
		result = prime * result + ((queryParams == null) ? 0 : queryParams.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Uri other = (Uri) obj;
		if (method == null) {
			if (other.method != null)
				return false;
		} else if (!method.equals(other.method))
			return false;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		if (protocol == null) {
			if (other.protocol != null)
				return false;
		} else if (!protocol.equals(other.protocol))
			return false;
		if (queryParams == null) {
			if (other.queryParams != null)
				return false;
		} else if (!queryParams.equals(other.queryParams))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Uri [method=" + method + ", path=" + path + ", queryParams=" + queryParams + ", protocol=" + protocol
				+ "]";
	}
}
