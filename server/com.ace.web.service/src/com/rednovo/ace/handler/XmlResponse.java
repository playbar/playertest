package com.rednovo.ace.handler;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class XmlResponse extends HttpServletResponseWrapper {

	public XmlResponse(HttpServletResponse response) {
		super(response);
	}

}
