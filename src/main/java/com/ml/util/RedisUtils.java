package com.ml.util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@SuppressWarnings("unused")
public class RedisUtils {
	private static final String IP = UtilProperties.getConfig("REDIS_IP"); // ip
	private static final int PORT = Integer.valueOf(UtilProperties.getConfig("REDIS_PORT")); // 端口
	private static final String AUTH = UtilProperties.getConfig("REDIS_AUTH"); // 密码(原始默认是没有密码)
	private static int MAX_ACTIVE = 1000; // 最大连接数
	private static int MAX_IDLE = 300000; // 设置最大空闲数
	private static int MAX_WAIT = 500; // 最大连接时间
	private static int TIMEOUT = 10000; // 超时时间
	private static boolean BORROW = true; // 在borrow一个事例时是否提前进行validate操作
	private static JedisPool pool = null;
	public static int EXPIRE_TIME = Integer.valueOf( UtilProperties.getConfig("REDIS_EXPIRE_TIME"));
	
	/**
	 * 初始化线程池
	 */
	static {
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(MAX_ACTIVE);
		config.setMaxIdle(MAX_IDLE);
		config.setMaxWaitMillis(MAX_WAIT);
		config.setTestOnBorrow(BORROW);
		//pool = new JedisPool(config, IP, PORT, TIMEOUT);
		pool = new JedisPool(config, IP, PORT, TIMEOUT, AUTH);
	}

	/**
	 * 获取连接
	 */
	public static synchronized Jedis getJedis() {
		try {
			if (pool != null) {
				return pool.getResource();
			} else {
				return null;
			}
		} catch (Exception e) {
			System.err.println("连接池连接异常");
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * @Description:设置失效时间
	 * @param @param
	 *            key
	 * @param @param
	 *            seconds
	 * @param @return
	 * @return boolean 返回类型
	 */
	public static void disableTime(String key, int seconds) {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			jedis.expire(key, seconds);

		} catch (Exception e) {
			System.err.println("设置失效失败.");
		} finally {
			getColse(jedis);
		}
	}


	/**
	 * @Description:存储key~value
	 * @param @param
	 *            key
	 * @param @param
	 *            value
	 * @return void 返回类型
	 */

	public static boolean addValue(String key, String value) {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			String code = jedis.set(key, value);
			if (code.equals("ok")) {
				return true;
			}
		} catch (Exception e) {
			System.err.println("插入数据有异常."+ e.getMessage());
			return false;
		} finally {
			getColse(jedis);
		}
		return false;
	}

	/**
	 * @Description:删除key
	 * @param @param
	 *            key
	 * @param @return
	 * @return boolean 返回类型
	 */
	public static boolean delKey(String key) {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			Long code = jedis.del(key);
			if (code > 1) {
				return true;
			}
		} catch (Exception e) {
			System.err.println("删除key异常.");
			return false;
		} finally {
			getColse(jedis);
		}
		return false;
	}
	
	/**
	 * @Description:删除key
	 * @param @param
	 *            key
	 * @param @return
	 * @return boolean 返回类型
	 */
	public static String getKey(String key) {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			String value = jedis.get(key);
			if(null == value || "".equals(value)) return null;
			else return value;
		} catch (Exception e) {
			System.err.println("获取key异常.");
			return null;
		} finally {
			getColse(jedis);
		}
	}

	/**
	 * @Description: 关闭连接
	 * @param @param
	 *            jedis
	 * @return void 返回类型
	 */

	public static void getColse(Jedis jedis) {
		if (jedis != null) {
			jedis.close();
		}
	}

}