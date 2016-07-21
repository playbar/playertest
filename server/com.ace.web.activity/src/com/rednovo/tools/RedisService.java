/*  ------------------------------------------------------------------------------ 
 *                  软件名称:美播手机版
 *                  公司名称:多宝科技
 *                  开发作者:Jinglong.Zhao
 *       			开发时间:2014-4-18/2014
 *    				All Rights Reserved 2012-2015
 *  ------------------------------------------------------------------------------
 *    				注意:本内容均来自多宝科技研发部，仅限内部交流使用,未经过公司许可 禁止转发    
 *  ------------------------------------------------------------------------------
 *                  prj-name：com.ggcj.service
 *                  fileName：RedisService.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.Protocol;

import com.rednovo.ace.constant.Constant;
import com.rednovo.tools.RedisConfig.RedisNode;

/**
 * @author Administrator
 * 
 */
public class RedisService {
	private JedisPool pool;

	private static HashMap<String, RedisService> servers = new HashMap<String, RedisService>();
	private static Logger logger = Logger.getLogger(RedisService.class);

	/**
	 * 获取服务节点名称
	 * 
	 * @param nodeName String
	 * @return
	 * @author Yongchao.Yang
	 * @since 2014年8月9日下午1:16:22
	 */
	public static RedisService getServer(RedisNode node) {
		RedisService rs = servers.get(node.getName());
		if (rs == null) {
			rs = new RedisService(node.getName());
			servers.put(node.getName(), rs);
		}
		return rs;
	}

	private RedisService(String nodeName) {
		JedisPoolConfig jpc = new JedisPoolConfig();
		jpc.setTestOnBorrow(true);
		String host = PPConfiguration.getProperties("cfg.properties").getString("redis." + nodeName + ".host");
		int port = Integer.valueOf(PPConfiguration.getProperties("cfg.properties").getString("redis." + nodeName + ".host.port"));
		String passwd = PPConfiguration.getProperties("cfg.properties").getString("redis." + nodeName + ".host.pass");

		jpc.setMaxActive(PPConfiguration.getProperties("cfg.properties").getInt("redis." + nodeName + ".host.maxActive"));
		jpc.setMaxWait(PPConfiguration.getProperties("cfg.properties").getInt("redis." + nodeName + ".host.maxWait"));
		pool = new JedisPool(jpc, host, port, Protocol.DEFAULT_TIMEOUT, passwd);
	}

	/**
	 * 向指定标示中添加Map数据
	 * 
	 * @param mapName String
	 * @param map {@link HashMap}
	 */
	public void addMap(String mapName, HashMap<String, String> map) {
		Jedis jedis = null;
		// if (Validator.isEmpty(map)) {
		// logger.error("\t\t[RedisService][参数为空]");
		// return;
		// }
		try {
			jedis = pool.getResource();
			jedis.hmset(mapName, map);
		} catch (Exception ex) {
			logger.error("redis操作异常", ex);
		} finally {
			pool.returnResource(jedis);
		}
	}

	/**
	 * 向map中增加键值对
	 * 
	 * @param mapName
	 * @param key
	 * @param value
	 */
	public void addMap(String mapName, String key, String value) {
		Jedis jedis = null;
		// if (Validator.isEmpty(value)) {
		// logger.error("\t\t[RedisService][参数为空]");
		// return;
		// }
		try {
			jedis = pool.getResource();
			jedis.hset(mapName, key, value);
		} catch (Exception ex) {
			logger.error("redis操作异常", ex);
		} finally {
			pool.returnResource(jedis);
		}
	}

	/**
	 * 删除map中指定个的key
	 * 
	 * @param mapName
	 * @param key
	 */
	public void delMapKey(String mapName, String... key) {
		Jedis jedis = null;
		// if (Validator.isEmpty(key)) {
		// logger.error("\t\t[RedisService][参数为空]");
		// return;
		// }
		try {
			jedis = pool.getResource();
			jedis.hdel(mapName, key);
		} catch (Exception ex) {
			logger.error("redis操作异常", ex);
		} finally {
			pool.returnResource(jedis);
		}
	}

	/**
	 * 获取指定标示Map的key集合
	 * 
	 * @param mapName String
	 * @return {@link Set}
	 */
	public Set<String> getMapKeys(String mapName) {
		Jedis jedis = null;
		Set<String> keys = null;
		try {
			jedis = pool.getResource();
			keys = jedis.hkeys(mapName);
		} catch (Exception ex) {
			logger.error("redis操作异常", ex);
		} finally {
			pool.returnResource(jedis);
		}
		return keys;
	}

	/**
	 * 获取Map的Values集合
	 * 
	 * @param mapName String map名称
	 * @return {@link List}
	 */
	public List<String> getMapValues(String mapName) {
		Jedis jedis = null;
		List<String> list = null;
		try {
			jedis = pool.getResource();
			list = jedis.hvals(mapName);
		} catch (Exception ex) {
			logger.error("redis操作异常", ex);
		} finally {
			pool.returnResource(jedis);
		}
		return list;

	}

	/**
	 * 获取Map中的Value值
	 * 
	 * @param mapName String map名称
	 * @param key
	 * @return
	 */
	public List<String> getMapValues(String mapName, String... key) {
		Jedis jedis = null;
		List<String> list = new ArrayList<String>();
		// if (Validator.isEmpty(key)) {
		// logger.error("\t\t[RedisService][参数为空]");
		// return list;
		// }
		try {
			jedis = pool.getResource();
			list = jedis.hmget(mapName, key);
		} catch (Exception ex) {
			logger.error("redis操作异常", ex);
		} finally {
			pool.returnResource(jedis);
		}
		return list;
	}

	/**
	 * 或者map的大小
	 * 
	 * @param mapName
	 * @return
	 */
	public long getMapSize(String mapName) {
		Jedis jedis = null;
		Long size = null;
		try {
			jedis = pool.getResource();
			size = jedis.hlen(mapName);
		} catch (Exception ex) {
			logger.error("redis操作异常", ex);
		} finally {
			pool.returnResource(jedis);
		}
		return size;
	}

	/**
	 * 获取map中指定的key的value
	 * 
	 * @param mapName
	 * @param key
	 * @return
	 */
	public String getMapValue(String mapName, String key) {
		// if (Validator.isEmpty(key)) {
		// logger.error("\t\t[RedisService][参数为空]");
		// return "";
		// }
		List<String> values = this.getMapValues(mapName, key);
		if (!values.isEmpty()) {
			return values.get(0);
		}
		return "";
	}

	/**
	 * 或者指定map的所有键值对
	 * 
	 * @param mapName
	 * @return
	 */
	public Map<String, String> getMap(String mapName) {
		Map<String, String> map = new HashMap<String, String>();
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			map = jedis.hgetAll(mapName);
		} catch (Exception ex) {
			logger.error("redis操作异常", ex);
		} finally {
			pool.returnResource(jedis);
		}
		return map;
	}

	/**
	 * 给List末尾添加数据,
	 * 
	 * @param listName
	 * @param value
	 * @author Yongchao.Yang
	 * @since 2014年8月8日上午11:18:05
	 */
	public void addList(String listName, String... value) {
		this.addList(listName, false, value);
	}

	/**
	 * 向指定队列中增加元素(队列尾)
	 * 
	 * @param listName {@link String} 列表名称
	 * @param value
	 */
	public String addList(String listName, boolean isHeader, String... value) {
		Jedis jedis = null;
		// if (Validator.isEmpty(value)) {
		// logger.error("\t\t[RedisService][参数为空]");
		// return OperateStatus.OPERATE_STATUS_FAILED;
		// }
		try {
			jedis = pool.getResource();
			if (isHeader) {
				jedis.lpush(listName, value);
			} else {
				jedis.rpush(listName, value);
			}
			return Constant.OperaterStatus.SUCESSED.getValue();
		} catch (Exception ex) {
			logger.error("redis操作异常", ex);
		} finally {
			pool.returnResource(jedis);
		}

		return Constant.OperaterStatus.FAILED.getValue();

	}

	/**
	 * 向队列指定位置添加元素
	 * 
	 * @param listName {@link String} 列表名称
	 * @param index
	 * @param value
	 */
	public void addList(String listName, int index, String value) {
		Jedis jedis = null;
		// if (Validator.isEmpty(value)) {
		// logger.error("\t\t[RedisService][参数为空]");
		// return;
		// }
		try {
			jedis = pool.getResource();
			jedis.lset(listName, index, value);
		} catch (Exception ex) {
			logger.error("redis操作异常", ex);
		} finally {
			pool.returnResource(jedis);
		}
	}

	/**
	 * 获取队列所有元素
	 * 
	 * @param listName
	 * @return
	 */
	public List<String> getList(String listName) {
		Jedis jedis = null;
		List<String> list = null;
		try {
			jedis = pool.getResource();
			list = jedis.lrange(listName, 0, -1);
		} catch (Exception ex) {
			logger.error("redis操作异常", ex);
		} finally {
			pool.returnResource(jedis);
		}
		return list;
	}

	/**
	 * 获取队列指定区间元素
	 * 
	 * @param listName
	 * @param begIndex
	 * @param endIndex
	 * @return
	 */
	public List<String> getList(String listName, int begIndex, int endIndex) {
		Jedis jedis = null;
		List<String> list = null;
		try {
			jedis = pool.getResource();
			list = jedis.lrange(listName, begIndex, endIndex);
		} catch (Exception ex) {
			logger.error("redis操作异常", ex);
		} finally {
			pool.returnResource(jedis);
		}
		return list;
	}

	/**
	 * 获取队列指定位置元素
	 * 
	 * @param listName
	 * @param index
	 * @return
	 */
	public String getList(String listName, int index) {
		Jedis jedis = null;
		String value = "";
		try {
			jedis = pool.getResource();
			value = jedis.lindex(listName, Long.valueOf(index));
		} catch (Exception ex) {
			logger.error("redis操作异常", ex);
		} finally {
			pool.returnResource(jedis);
		}
		return value;
	}

	/**
	 * 从头部弹出列表所有元素
	 * 
	 * @param listName
	 * @return
	 * @author Yongchao.Yang
	 * @since 2014年11月8日下午9:00:03
	 */
	public List<String> popList(String listName) {
		ArrayList<String> list = new ArrayList<String>();
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			String value = "";
			while ((value = jedis.lpop(listName)) != null && !"".equals(value)) {
				list.add(value);
			}
		} catch (Exception ex) {
			logger.error("redis操作异常", ex);
		} finally {
			pool.returnResource(jedis);
		}
		return list;
	}

	/**
	 * 基于阻塞模式从队列中获取可用元素
	 * 
	 * @param listName
	 * @param timeOut
	 * @return
	 * @author Yongchao.Yang
	 * @since 2015年4月15日上午11:01:10
	 */
	public List<String> popListWithBlock(int timeOut, String... listName) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			return jedis.blpop(timeOut, listName);
		} catch (Exception ex) {
			logger.error("redis操作异常", ex);
		} finally {
			pool.returnResource(jedis);
		}
		return new ArrayList<String>();
	}

	/**
	 * 获取队列下一个元素
	 * 
	 * @param listName
	 * @return
	 * @author Yongchao.Yang
	 * @since 2015年4月13日下午12:31:43
	 */
	public String popListNextValue(String listName) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			return jedis.lpop(listName);
		} catch (Exception ex) {
			logger.error("redis操作异常", ex);
		} finally {
			pool.returnResource(jedis);
		}
		return "";
	}

	/**
	 * 删除队列中指定内容.all为false，则从队列尾部开始寻找，并删除一次
	 * 
	 * @param listName
	 * @param value
	 * @param all boolean 是否删除所有
	 */
	public void removeList(String listName, String value, boolean all) {
		Jedis jedis = null;
		// if (Validator.isEmpty(value)) {
		// logger.error("\t\t[RedisService][参数为空]");
		// return;
		// }
		long count = 0;// 默认删除所有
		if (!all) {
			count = -1;
		}

		try {
			jedis = pool.getResource();
			jedis.lrem(listName, count, value);
		} catch (Exception ex) {
			logger.error("redis操作异常", ex);
		} finally {
			pool.returnResource(jedis);
		}

	}

	/**
	 * 获取list大小
	 * 
	 * @param listName
	 * @return
	 * @author Yongchao.Yang
	 * @since 2015年4月14日下午5:21:05
	 */
	public long getListSize(String listName) {
		Jedis jedis = null;
		long size = 0;
		try {
			jedis = pool.getResource();
			size = jedis.llen(listName);
		} catch (Exception ex) {
			logger.error("redis操作异常", ex);
		} finally {
			pool.returnResource(jedis);
		}
		return size;
	}

	/**
	 * 向set中添加元素
	 * 
	 * @param setName
	 * @param value
	 * @param sortId int 排序值
	 */
	public void addSet(String setName, String... values) {
		Jedis jedis = null;
		// if (Validator.isEmpty(values)) {
		// logger.error("\t\t[RedisService][参数为空]");
		// return;
		// }
		try {
			jedis = pool.getResource();
			jedis.sadd(setName, values);
		} catch (Exception ex) {
			logger.error("redis操作异常", ex);
		} finally {
			pool.returnResource(jedis);
		}

	}

	/**
	 * 判断set集合中是否含有该元素
	 * 
	 * @param setName
	 * @param value
	 * @return
	 * @author Yongchao.Yang
	 * @since 2014年11月6日下午12:37:03
	 */
	public boolean containsSetValue(String setName, String value) {
		boolean hasValue = false;
		Jedis jedis = null;
		// if (Validator.isEmpty(value)) {
		// logger.error("\t\t[RedisService][参数为空]");
		// return hasValue;
		// }
		try {
			jedis = pool.getResource();
			hasValue = jedis.sismember(setName, value);
		} catch (Exception ex) {
			logger.error("redis操作异常", ex);
		} finally {
			pool.returnResource(jedis);
		}
		return hasValue;
	}

	/**
	 * 返回set集合元素
	 * 
	 * @param setName
	 * @return
	 * @author Yongchao.Yang
	 * @since 2014年11月6日下午12:39:36
	 */
	public Set<String> getSet(String setName) {
		Jedis jedis = null;
		Set<String> set = null;
		try {
			jedis = pool.getResource();
			set = jedis.smembers(setName);
		} catch (Exception ex) {
			logger.error("redis操作异常", ex);
		} finally {
			pool.returnResource(jedis);
		}
		return set;
	}

	/**
	 * 移除Set集合中指定元素
	 * 
	 * @param setName
	 * @param values
	 * @author Yongchao.Yang
	 * @since 2014年11月6日下午12:43:53
	 */
	public void removeSet(String setName, String... values) {
		Jedis jedis = null;
		// if (Validator.isEmpty(values)) {
		// logger.error("\t\t[RedisService][参数为空]");
		// return;
		// }
		try {
			jedis = pool.getResource();
			jedis.srem(setName, values);
		} catch (Exception ex) {
			logger.error("redis操作异常", ex);
		} finally {
			pool.returnResource(jedis);
		}
	}

	/**
	 * 删除一个或者多个Key绑定的数据
	 * 
	 * @param key
	 * @return
	 */
	public int removeKey(String... key) {
		Jedis jedis = null;
		int cnt = 0;
		// if (Validator.isEmpty(key)) {
		// logger.error("\t\t[RedisService][参数为空]");
		// return cnt;
		// }
		try {
			jedis = pool.getResource();
			cnt = jedis.del(key).intValue();
		} catch (Exception ex) {
			logger.error("redis操作异常", ex);
		} finally {
			pool.returnResource(jedis);
		}
		return cnt;
	}

	/**
	 * 设置指定Map Key 值的增减
	 * 
	 * @param key
	 * @param value
	 * @author BingQiang.Wang
	 * @since 2015年9月16日下午4:09:43
	 */
	public long sumMapValue(String mapName, String key, long value) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			return jedis.hincrBy(mapName, key, value);
		} catch (Exception ex) {
			logger.error("redis操作异常", ex);
		} finally {
			pool.returnResource(jedis);
		}
		return 0;
	}

	/**
	 * 销毁资源
	 */
	public void destoryPool() {
		this.pool.destroy();
	}

	/**
	 * 添加字符串缓存，并设置过期时间
	 * 
	 * @param key
	 * @param seconds
	 * @param value
	 * @author sg.z
	 * @since 2014年10月14日下午6:09:56
	 */
	public void addString(String key, int seconds, String value) {
		Jedis jedis = null;
		// if (Validator.isEmpty(key)) {
		// logger.error("\t\t[RedisService][参数为空]");
		// return;
		// }
		try {
			jedis = pool.getResource();
			jedis.setex(key, seconds, value);
		} catch (Exception ex) {
			logger.error("redis操作异常", ex);
		} finally {
			pool.returnResource(jedis);
		}

	}
	
	
	/**
	 * 添加字符串缓存
	 * @param key
	 * @param value
	 * @author sg.z
	 * @since 2014年10月14日下午6:09:56
	 */
	public void addString(String key, String value) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.set(key, value);
		} catch (Exception ex) {
			logger.error("redis操作异常", ex);
		} finally {
			pool.returnResource(jedis);
		}

	}

	/**
	 * 获取字符串缓存值
	 * 
	 * @param key
	 * @return
	 * @author sg.z
	 * @since 2014年10月14日下午6:13:30
	 */
	public String getString(String key) {
		Jedis jedis = null;
		String value = "";
		try {
			jedis = pool.getResource();
			value = jedis.get(key);;
		} catch (Exception ex) {
			logger.error("redis操作异常", ex);
		} finally {
			pool.returnResource(jedis);
		}
		return value;
	}

	/**
	 * 订阅信息
	 * 
	 * @param pubSub
	 * @author sg.z
	 * @since 2015年9月14日下午3:06:12
	 */
	public void subscribe(JedisPubSub pubSub, String... channels) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.subscribe(pubSub, channels);
		} catch (Exception ex) {
			logger.error("redis操作异常", ex);
		} finally {
			pool.returnResource(jedis);
		}
	}

	/**
	 * 发布信息
	 * 
	 * @param channel
	 * @param message
	 * @author sg.z
	 * @since 2015年9月14日下午3:14:43
	 */
	public void publish(String channel, String message) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.publish(channel, message);
		} catch (Exception ex) {
			logger.error("redis操作异常", ex);
		} finally {
			pool.returnResource(jedis);
		}
	}

	/**
	 * 清空REDIS备份数据
	 * 
	 * @author Yongchao.Yang
	 * @since 2014年8月5日下午7:52:16
	 */
	public void flushData() {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.flushAll();
		} catch (Exception ex) {
			logger.error("redis操作异常", ex);
		} finally {
			pool.returnResource(jedis);
		}

	}

	/**
	 * 获取一个客户端引用
	 * 
	 * @return
	 * @author Yongchao.Yang
	 * @since 2015年5月21日下午7:56:31
	 */
	public Jedis getJedis() {
		return pool.getResource();
	}

	/**
	 * 归还资源
	 * 
	 * @param jedis
	 * @author Yongchao.Yang
	 * @since 2015年5月21日下午7:54:43
	 */
	public void returnJedis(Jedis jedis) {
		pool.returnResource(jedis);
	}

	public static void main(String[] args) throws Exception {
		RedisService rs = RedisService.getServer(RedisNode.USER_NODE);
		for (int i = 0; i < 1000; i++) {
			rs.addList("tt", String.valueOf(i));
		}
		System.out.println(rs.getList("tt"));
		rs.removeKey("tt");
		System.out.println(rs.getList("tt"));
	}

}
