package com.rednovo.solr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;

import com.rednovo.tools.PPConfiguration;

/**
 * 搜索索引服务操作类
 * 
 * @author bq.w/2016年3月17日
 */
public class SearchEngines {

	private static HttpSolrClient solrClient = null;

	static {
		String SOLR_RUL = PPConfiguration.getProperties("cfg.properties").getString("solr.server.url");
		solrClient = new HttpSolrClient(SOLR_RUL);
		solrClient.setSoTimeout(50000);
	}

	/**
	 * 添加索引文档
	 * 
	 * @param solrMap
	 * @throws SolrServerException
	 * @throws IOException
	 */
	public static void addIndex(Map<String, String> indexMap) throws SolrServerException, IOException {
		SolrInputDocument solrDocument = new SolrInputDocument();
		for (Map.Entry<String, String> entry : indexMap.entrySet()) {
			solrDocument.addField(entry.getKey(), entry.getValue());
		}
		solrClient.add(solrDocument);
		solrClient.commit();
	}

	/**
	 * 删除索引文档
	 * 
	 * @param ids
	 * @throws SolrServerException
	 * @throws IOException
	 */
	public static void removeIndex(List<String> ids) throws SolrServerException, IOException {
		solrClient.deleteById(ids);
		solrClient.commit();
	}

	/**
	 * 删除索引文档
	 * 
	 * @param id
	 * @throws SolrServerException
	 * @throws IOException
	 */
	public static void removeIndex(String id) throws SolrServerException, IOException {
		solrClient.deleteById(id);
		solrClient.commit();
	}

	/**
	 * 搜索索引文档
	 * 
	 * @param page
	 * @param pageSize
	 * @param query 要查询的内容
	 * @param finlds 指定要返回的字段名
	 * @throws SolrServerException
	 * @throws IOException
	 */
	public static List<Map<String, Object>> getIndex(int page, int pageSize, String query, String... finlds) throws SolrServerException, IOException {
		int start = (page == 1 ? 0 : ((page - 1) * pageSize));
		SolrQuery sq = new SolrQuery();
		sq.setFields(finlds);
		sq.setQuery(query);
		sq.setRows(pageSize);
		sq.setStart(start);
		System.out.println(">>>key:"+query);
		System.out.println(">>>key:"+solrClient.query(sq));
		SolrDocumentList sdl = solrClient.query(sq).getResults();
		List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
		for (SolrDocument sd : sdl) {
			listMap.add(sd.getFieldValueMap());
		}
		return listMap;
	}

	public static void main(String[] args) throws SolrServerException, IOException {
		
//		 Map<String,String> solrMap = new HashMap<String,String>();
//		 solrMap.put("id","2000");
//		 solrMap.put("name","改变昵称");
//		 addIndex(solrMap);
//		 List<Map<String,Object>> listMap = getSolrDocument("改变","id");
//		 for(Map<String,Object> queryMap:listMap){
//		 System.out.println("id:"+queryMap.get("id")+",name:"+queryMap.get("name"));
//		 }
		removeIndex("2000");
		getIndex(1,10,"变","id","name");

	}

}
