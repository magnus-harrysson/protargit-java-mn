package com.harrys_it.ots.core.utils;

/**
 * start_flag,cmd,separator,data,end_flag.
 * Max size 16 bytes.
 * cmd and data is converted from int to hex and hex to int e.g. cmd 17 (int) will be byte[]{0x31,0x37} in hex ascii.
 */
public class McuSerialDataConverter {

	private McuSerialDataConverter(){
		// Hide
	}

	public static byte[] convertToASCII(int cmd, int data, int packetSize) {
    	var res = new byte[packetSize];
		var resIndex = 0;
		
		// START - Append start byte
		res[resIndex++] = (byte) 0x02;
		
		// CMD - add command to array
		resIndex = addToArrayAndIncreaseIndex(cmd, res, resIndex);

		// SEPARATOR (:) - Append separator byte
		res[resIndex++] = 0x3a;
		
		// DATA - add data to array
		resIndex = addToArrayAndIncreaseIndex(data, res, resIndex);

		// STOP - Append stop byte
		res[resIndex++] = (byte) 0x03;

		//Trim length of array.
		var trimmedArray = new byte[resIndex];
		System.arraycopy(res,0,trimmedArray,0,trimmedArray.length);
		
		return trimmedArray;
	}

	private static int addToArrayAndIncreaseIndex(int cmd, byte[] res, int resIndex) {
		var dataToString = Integer.toString(cmd);
		for(var i = 0 ; i < dataToString.length() ; i ++) {
			if(dataToString.length()>=i+1) {
				var tmp = Integer.parseInt(dataToString.substring(i, 1+i));
				res[resIndex++] = (byte) (tmp+48);
			}
		}
		return resIndex;
	}

	/* Convert byte[] char to a command int */
	public static int commandToInt(byte[] bytes) {
		 var startIndex = 1;
		 var endIndex = 0;
		 for (byte c : bytes) {
			 if(c==0x3a)
				 break;
			 endIndex++;
		 }
		 
		 var b = new int[endIndex-1];
		 
		 for(int i=startIndex;i<endIndex;i++) {
			 b[i-1] = (bytes[i] & 0xFF) - 48;
		 }
		 var sb = new StringBuilder();
		 for (int z : b) {
			sb.append(z);
		}
		 return Integer.parseInt(sb.toString());
	}

	/* Convert byte[] char to a data int */
	public static int dataToInt(byte[] bytes) {
		var startIndex = 0;
		var endIndex = 0;
		for (byte c : bytes) {
			if(c==0x3a)
				break;
			startIndex++;
		}
		 
		for (byte d : bytes) {
			if(d==0x03) break;
			endIndex++;
		}
		 
		var b = new int[endIndex-startIndex-1];
		var tmp = 0;
		for(int i=startIndex+1;i<endIndex;i++) {
			b[tmp++] = (bytes[i] & 0xFF) - 48;
		}
		var sb = new StringBuilder();
		for (int z : b) {
			sb.append(z);
		}
		return Integer.parseInt(sb.toString());
	}
}
