/**
 * com.swordfish.net.SessionPool.java@com.tax.splider
 */
package com.rednovo.tools.web;

import java.util.HashMap;

import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

/**
 * 
 * @author Yongchao.Yang@2013年10月29日
 * 
 */
public class SessionPool {
	private HashMap<String, HttpContext> pool = null;
	private static SessionPool sp;

	private SessionPool() {
		pool = new HashMap<String, HttpContext>();
	}

	public void destorySession(String host) {
		pool.remove(host);
	}

	public HttpContext getSession(String host) {
		HttpContext session = this.pool.get(host);
		if (null == session) {
			session = new BasicHttpContext();
			this.pool.put(host, session);
		}
		return session;
	}

	public static SessionPool getInstance() {
		if (sp == null) {
			sp = new SessionPool();
		}
		return sp;
	}
}
