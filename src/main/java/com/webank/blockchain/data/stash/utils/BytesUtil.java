/**
 * Copyright 2020 Webank.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.webank.blockchain.data.stash.utils;

/**
 * BytesUtil
 *
 * @Description: BytesUtil
 * @author graysonzhang
 * @data 2019-07-31 15:41:22
 *
 */
public class BytesUtil {

	public static int byte4ToInt(byte[] bytes) {
		int b0 = bytes[0] & 0xFF;
		int b1 = bytes[1] & 0xFF;
		int b2 = bytes[2] & 0xFF;
		int b3 = bytes[3] & 0xFF;
		return (b0 << 24) | (b1 << 16) | (b2 << 8) | b3;
	}

	public static long byte4UnsignToLong(byte[] bytes) {
		int firstByte = (0x000000FF & ((int) bytes[0]));
		int secondByte = (0x000000FF & ((int) bytes[1]));
		int thirdByte = (0x000000FF & ((int) bytes[2]));
		int fourthByte = (0x000000FF & ((int) bytes[3]));
		return ((long) (firstByte << 24 | secondByte << 16 | thirdByte << 8 | fourthByte)) & 0xFFFFFFFFL;
	}

	public static long byteArrayToLong(byte[] b) {
		return b[7] & 0xFF | (b[6] & 0xFF) << (8 * 1) | (b[5] & 0xFF) << (8 * 2) | (b[4] & 0xFF) << (8 * 3)
				| (b[3] & 0xFF) << (8 * 4) | (b[2] & 0xFF) << (8 * 5) | (b[1] & 0xFF) << (8 * 6)
				| (b[0] & 0xFF) << (8 * 7);
	}

	public static byte[] subBytes(byte[] src, int srcPos, int length) {
		byte[] dest = new byte[length];
		System.arraycopy(src, srcPos, dest, 0, length);
		return dest;
	}
	
	public static long byte4UnsignFromSrcToLong(byte[] src, int srcPos){
	    byte[] dest = subBytes(src, srcPos, 4);
	    return byte4UnsignToLong(dest);
	}
	
	public static int byte4FromSrcToInt(byte[] src, int srcPos){
	    byte[] dest = subBytes(src, srcPos, 4);
	    return byte4ToInt(dest);
	}
	
	public static long byte8FromSrcToLong(byte[] src, int srcPos){
	    byte[] dest = subBytes(src, srcPos, 8);
	    return byteArrayToLong(dest);
	}
}
