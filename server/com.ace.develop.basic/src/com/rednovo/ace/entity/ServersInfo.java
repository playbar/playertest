/*  ------------------------------------------------------------------------------ 
 *                  软件名称:
 *                  公司名称:
 *                  开发作者:ZuKang.Song
 *       			开发时间:2016年6月8日/2016
 *    				All Rights Reserved 2016-2016
 *  ------------------------------------------------------------------------------
 *    				注意:本内容均来自，仅限内部交流使用,未经过公司许可 禁止转发    
 *  ------------------------------------------------------------------------------
 *                  prj-name：com.ace.web.service
 *                  ServersInfo.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.ace.entity;

/**
 * @author ZuKang.Song/2016年6月8日
 */
public class ServersInfo {

	private String id;
	private String ip;
	private String port;
	//状态 0是off 1是on
	private String status;
	private String description;
	private String schemaId;
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the ip
	 */
	public String getIp() {
		return ip;
	}
	/**
	 * @param ip the ip to set
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}
	/**
	 * @return the port
	 */
	public String getPort() {
		return port;
	}
	/**
	 * @param port the port to set
	 */
	public void setPort(String port) {
		this.port = port;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the schemaId
	 */
	public String getSchemaId() {
		return schemaId;
	}
	/**
	 * @param schemaId the schemaId to set
	 */
	public void setSchemaId(String schemaId) {
		this.schemaId = schemaId;
	}
}
