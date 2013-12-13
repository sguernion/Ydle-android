package org.ydle.utils;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ObjectSerializer {

	
	public ObjectSerializer() {
	
	}
	
	public static Set<String> serialize(List<? extends Serializable> objs)  throws IOException{
		if (objs == null)
			return null;
		Set<String> result = new HashSet<String>();
		for(Serializable o : objs){
			result.add(serialize(o));
		}
		return result;
	}
	
	public static List deserialize(Set<String> objs)  throws IOException{
		if (objs == null)
			return null;
		List<Object> result = new ArrayList<Object>();
		for(String o : objs){
			result.add(deserialize(o));
		}
		return result;
	}
	
	public static String serialize(Serializable obj) throws IOException {
		if (obj == null)
			return "";
		try {
			ByteArrayOutputStream serialObj = new ByteArrayOutputStream();
			ObjectOutputStream objStream = new ObjectOutputStream(serialObj);
			objStream.writeObject(obj);
			objStream.close();
			return encodeBytes(serialObj.toByteArray());
		} catch (Exception e) {
			throw new IOException("Serialization error: " + e.getMessage(), e);
		}
	}

	public static Object deserialize(String str) throws IOException {
		if (str == null || str.length() == 0)
			return null;
		try {
			ByteArrayInputStream serialObj = new ByteArrayInputStream(
					decodeBytes(str));
			ObjectInputStream objStream = new ObjectInputStream(serialObj);
			return objStream.readObject();
		} catch (Exception e) {
			throw new IOException("Deserialization error: " + e.getMessage(), e);
		}
	}

	public static String encodeBytes(byte[] bytes) {
		StringBuffer strBuf = new StringBuffer();

		for (int i = 0; i < bytes.length; i++) {
			strBuf.append((char) (((bytes[i] >> 4) & 0xF) + ((int) 'a')));
			strBuf.append((char) (((bytes[i]) & 0xF) + ((int) 'a')));
		}

		return strBuf.toString();
	}

	public static byte[] decodeBytes(String str) {
		byte[] bytes = new byte[str.length() / 2];
		for (int i = 0; i < str.length(); i += 2) {
			char c = str.charAt(i);
			bytes[i / 2] = (byte) ((c - 'a') << 4);
			c = str.charAt(i + 1);
			bytes[i / 2] += (c - 'a');
		}
		return bytes;
	}

	

}