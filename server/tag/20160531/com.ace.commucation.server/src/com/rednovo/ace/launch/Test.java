package com.rednovo.ace.launch;

import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.alibaba.fastjson.JSON;
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

	public static void tttt() {
		System.out.println("$你好".replaceAll("\\$", "仰永潮"));
	}

	public static void main(String[] args) {
		try {
			// Test.getBytee();
			Test.tttt();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
