/**
 * com.swordfish.net.RequestConfig.java@com.tax.splider
 */
package com.rednovo.tools.web;

import java.util.Arrays;

import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;

/**
 * 
 * @author Yongchao.Yang@2013年10月29日
 * 
 */
public class PawConfig {
	private static RequestConfig config;
	static {
		RequestConfig defaultConfig = RequestConfig.custom()
				.setCookieSpec(CookieSpecs.BEST_MATCH)
				.setExpectContinueEnabled(true)
				.setStaleConnectionCheckEnabled(true)
				.setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST))
				.setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC)).build();
		config = RequestConfig.copy(defaultConfig)
				.setCookieSpec(CookieSpecs.BROWSER_COMPATIBILITY)
				.setSocketTimeout(10000)
				.setConnectTimeout(10000)
				.setConnectionRequestTimeout(10000)
				.build();
	}
	
	public static RequestConfig getConfig(){
		return config;
	}

}
