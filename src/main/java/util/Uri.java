package util;

public class Uri {
	  String method;
      String uri;
      String protocol;

      Uri(String method, String uri, String protocol) {
          this.method = method.trim();
          this.uri = uri.trim();
          this.protocol = protocol.trim();
      }

      public String getMethod() {
          return method;
      }

      public String getUri() {
          return uri;
      }
      
      public String getProtocol() {
          return protocol;
      }

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((method == null) ? 0 : method.hashCode());
			result = prime * result + ((protocol == null) ? 0 : protocol.hashCode());
			result = prime * result + ((uri == null) ? 0 : uri.hashCode());
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
			if (protocol == null) {
				if (other.protocol != null)
					return false;
			} else if (!protocol.equals(other.protocol))
				return false;
			if (uri == null) {
				if (other.uri != null)
					return false;
			} else if (!uri.equals(other.uri))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "Uri [method=" + method + ", uri=" + uri + ", protocol=" + protocol + "]";
		}
}
