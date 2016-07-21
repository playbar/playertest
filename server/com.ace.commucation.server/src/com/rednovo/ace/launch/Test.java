package com.rednovo.ace.launch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rednovo.ace.entity.Server;
import com.rednovo.tools.DateUtil;
import com.rednovo.tools.PPConfiguration;

public class Test {

	public Test() {
		// TODO Auto-generated constructor stub
	}

	public static byte[] getData(ByteBuffer buffer, SocketChannel sc, int length) throws Exception {
		byte[] bytes = new byte[length];
		int dataIndex = 0;
		while (true) {
			if (dataIndex < length) {// 如果没有获取足够数据，则获取知道完整，然后退出
				int remain = buffer.remaining();
				int byteRemain = length - dataIndex;
				if (remain > 0) {
					int readCnt = byteRemain > remain ? remain : byteRemain;
					buffer.get(bytes, dataIndex, readCnt);
					dataIndex = dataIndex + readCnt;
				} else {
					buffer.clear();
					sc.read(buffer);
					buffer.flip();
				}
			} else {
				break;
			}
		}
		return bytes;
	}

	public static void test() {
		ByteBuffer bf = ByteBuffer.allocate(1024);
		bf.putInt(2);
		bf.putInt(3);
		bf.put("hello".getBytes());
		bf.flip();
		byte[] bytes = new byte[8];
		try {
			bytes = getData(bf, null, 8);
			ByteBuffer bf2 = ByteBuffer.allocate(8);
			bf2.put(bytes);
			bf2.flip();

			System.out.println("int 1:" + bf2.getInt());
			System.out.println("int 2:" + bf2.getInt());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void test2() throws Exception {
		FileChannel fc = new FileInputStream("g:/1.jpg").getChannel();
		ByteBuffer buffer = ByteBuffer.allocate(1024);

		while (fc.read(buffer) > 0) {
			System.out.println("limit:" + buffer.limit() + ",position:" + buffer.position() + ",capactity:" + buffer.capacity());
			// buffer.flip();
			System.out.println("limit:" + buffer.limit() + ",position:" + buffer.position() + ",capactity:" + buffer.capacity());

		}
	}

	public static void test3() {
		ByteBuffer bb = ByteBuffer.allocate(1024);
		System.out.println("limit1:" + bb.limit() + ",position:" + bb.position() + ",capactiy:" + bb.capacity());
		bb.put((byte) 1);
		System.out.println("limit2:" + bb.limit() + ",position2:" + bb.position() + ",capactiy2:" + bb.capacity());
		bb.put((byte) 1);
		// bb.flip();
		System.out.println("limit3:" + bb.limit() + ",position3:" + bb.position() + ",capactiy3:" + bb.capacity());
	}

	public static void test4() {
		try {
			FileInputStream fis = new FileInputStream("g:/1.jpg");
			System.out.println(fis.available());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void test5() {
		ArrayList<Integer> list = new ArrayList<Integer>();
		Integer cnt = new Integer(1);
		list.add(cnt);
		cnt = new Integer(10);
		System.out.println(list.get(0).intValue());
	}

	public static void time() {
		System.out.println(DateUtil.getTimeInMillis());
	}

	public static void collection() {
		HashMap<String, LinkedList<String>> map = new HashMap<String, LinkedList<String>>();
		LinkedList<String> list1 = new LinkedList<String>(), list2 = new LinkedList<String>();
		list1.add("a");
		list1.add("b");
		list1.add("c");
		map.put("aa", list1);

		list2.add("1");
		list2.add("2");
		list2.add("3");
		map.put("1", list2);

		Iterator<String> it = map.keySet().iterator();
		while (it.hasNext()) {
			String str = it.next();
			System.out.println(map.get(str));
			it.remove();
		}

		System.out.println("---------------------------");
		System.out.println(map.size());
	}

	public static void numberTest() {
		System.out.println("/home/121.text".substring("/home/121.text".lastIndexOf(".")));
	}

	public static void testRemove() {
		List<String> list = new ArrayList<String>();
		list.add("a");
		list.add("b");
		list.add("c");
		list.add("d");

		Set<String> setn = new HashSet<String>();
		setn.add("n");

		Set<String> set = new HashSet<String>();
		set.add("a");
		set.add("b");
		set.add("c");
		set.add("n");
		set.add("m");

		set.removeAll(list);
		set.removeAll(setn);
		System.out.println(set);
	}

	public static void compare() {
		BigDecimal b = new BigDecimal(3.1);
		BigDecimal b2 = new BigDecimal(3.0);
		System.out.println(b.compareTo(b2));
	}

	public static void getBytee() {
		System.out.println("t".getBytes().length);
		try {
			System.out.println("好".getBytes("utf-8").length);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void tt() {
		ArrayList<Server> list = new ArrayList<Server>();
		Server s = new Server();
		s.setId("001");
		s.setIp("172.16.150.2");
		s.setPort(9999);
		list.add(s);

		s = new Server();

		s.setId("001");
		s.setIp("172.16.150.21");
		s.setPort(9999);
		list.add(s);
		System.out.println(JSON.toJSON(list));
	}

	public static void ttt(String userId) {
		StringBuffer userDir = new StringBuffer(userId.substring(0, userId.length() - 3));
		int len = userDir.length();
		for (int i = 1; i < len; i++) {
			userDir.insert((i * 2) - 1, File.separator);
		}
		StringBuffer baseDir = new StringBuffer(PPConfiguration.getProperties("cfg.properties").getString("user.photo.path"));
		baseDir.append(File.separator).append(userDir);
		File dir = new File(baseDir.toString());
		System.out.println(dir.mkdirs());;

	}

	public static JSONObject verifyApplePay(String url, String ticket) throws Exception {
		// this.getLog().info("[url]:" + url + ",[ticket]:" + ticket);
		URL appUrl = new URL(url);

		HttpsURLConnection connection = (HttpsURLConnection) appUrl.openConnection();
		connection.setRequestMethod("POST");
		connection.setDoOutput(true);
		connection.setAllowUserInteraction(false);

		Map<String, String> map = new HashMap<String, String>();
		map.put("receipt-data", ticket);
		String jsonStr = JSON.toJSONString(map);

		PrintStream ps = new PrintStream(connection.getOutputStream());
		ps.print(jsonStr);
		ps.close();

		BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String str;
		StringBuffer sb = new StringBuffer();
		while ((str = br.readLine()) != null) {
			sb.append(str);
		}
		br.close();
		System.out.println(sb);

		return JSON.parseObject(sb.toString());

	}

	public static void main(String[] args) {
		try {
			// Test.getBytee();
			Test.verifyApplePay("https://buy.itunes.apple.com/verifyReceipt", "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");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
