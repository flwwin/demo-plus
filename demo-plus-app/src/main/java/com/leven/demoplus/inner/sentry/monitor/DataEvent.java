package com.leven.demoplus.inner.sentry.monitor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataEvent<T> implements Serializable {

	private static final long serialVersionUID = 3005351661758387770L;

	private Map<String, String> headers = new HashMap<String, String>();

	private List<T> body;

	public void addHeader(String key, String value) {
		headers.put(key, value);
	}

	public void addBody(T monitorVO) {
		if (body == null) {
			this.body = new ArrayList<T>();
		}
		this.body.add(monitorVO);
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public List<T> getBody() {
		return body;
	}

	public void setBody(List<T> body) {
		this.body = body;
	}

	@Override
	public String toString() {
		return "DataEvent [headers=" + headers + ", body=" + body + "]";
	}

}
