/**
 * com.swordfish.net.SpiderFactory.java@com.tax.splider
 */
package com.rednovo.tools.web;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

/**
 * 
 * @author Yongchao.Yang@2013年10月29日
 * 
 */
public class SpiderFactory {
	private static PoolingHttpClientConnectionManager pool;
	private static CloseableHttpClient splider;

	static {
		pool = new PoolingHttpClientConnectionManager();
		pool.setDefaultMaxPerRoute(20);
		pool.setMaxTotal(2000);
		splider = HttpClients.custom().setConnectionManager(pool).build();
	}

	public static CloseableHttpClient getSplider() {
		return splider;
	}

}
