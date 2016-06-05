package sinvoice_lib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class MyTextUtil {

	public static String encode2(String str, String fuhao) {
		String s16 = MyTextUtil.Char2GBK(str);
		String s2 = hexString2binaryString(s16);
		s2 = addProveStr2(s2);
		String s2deal = dealRepeat3(s2, fuhao);
		return s2deal;
	}

	public static String decode2(String str, String fuhao) {
		str = unDealRepeat3(str, fuhao);
		if (!checkout2(str)) {
			return null;
		}
		str = str.substring(0,str.length()-4);
		String s16 = binaryString2hexString(str);
		String chars = MyTextUtil.GBK2Char(s16);
		return chars;
	}

	public static String unDealRepeat3(String str, String fuhao) {
		StringBuffer stringBuffer = new StringBuffer();
		char fu = fuhao.charAt(0);
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (c != fu) {
				stringBuffer.append(c);
			}
		}
		return stringBuffer.toString();
	}

	/*
	 * 例如 010011 变成 0#1#0#0#1#1
	 */
	public static String dealRepeat3(String str, String fuhao) {
		StringBuffer stringBuffer = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			stringBuffer.append(str.charAt(i)).append(fuhao);
		}
		return stringBuffer.toString();
	}

	public static String hexString2binaryString(String hexString) {
		if (hexString == null || hexString.length() % 2 != 0)
			return null;
		String bString = "", tmp;
		for (int i = 0; i < hexString.length(); i++) {
			tmp = "0000"
					+ Integer.toBinaryString(Integer.parseInt(
							hexString.substring(i, i + 1), 16));
			bString += tmp.substring(tmp.length() - 4);
		}
		return bString;
	}

	public static String binaryString2hexString(String bString) {
		if (bString == null || bString.equals("") || bString.length() % 8 != 0)
			return null;
		StringBuffer tmp = new StringBuffer();
		int iTmp = 0;
		for (int i = 0; i < bString.length(); i += 4) {
			iTmp = 0;
			for (int j = 0; j < 4; j++) {
				iTmp += Integer.parseInt(bString.substring(i + j, i + j + 1)) << (4 - j - 1);
			}
			tmp.append(Integer.toHexString(iTmp));
		}
		return tmp.toString().toUpperCase();
	}

	public static String decodeText(String text, String fuhao1, String fuhao2) {
		String result = "";
		String temp;
		text = undealRepeat(text, fuhao1);
		if (!checkout(text)) {
			return null;
		}
		text = text.substring(0, text.length()-3);
		List<StrInfo> list = cutText(text, fuhao2);
		if (list.size() == 0) {
			return GBK2Char(text);
		}
		for (int i = 0; i < list.size(); i++) {
			StrInfo info = list.get(i);
			if (info.issigned) {
				result += info.getText();
			} else {
				temp = GBK2Char(info.getText());
				result += temp;
			}
		}
		return result;
	}

	public static String encodeText(String text, String fuhao1, String fuhao2) {
		String result = "";
		String temp;
		text = signNumber2(text, fuhao2);
		System.out.println(text + " 分离数字");
		List<StrInfo> list = cutText(text, fuhao2);
		if (list.size() != 0) {
			for (int i = 0; i < list.size(); i++) {
				StrInfo info = list.get(i);
				// System.out.println("info text " + info.getText());
				// 是否是一串号码
				if (info.issigned) {
					result += fuhao2 + info.getText() + fuhao2;
				} else {
					temp = Char2GBK(info.getText());
					result += temp;
				}
			}
		} else {
			result = Char2GBK(text);
		}
		result = addProveStr(result);
//		System.out.println(result + " ");
		result = dealRepeat(result, fuhao1);
//		System.out.println(result + " deal repeat");
		return result;
	}

	/*
	 * 将字符串中的数字用符号圈点出来
	 */
	public static String signNumber(String text, String fuhao) {
		char ch;
		int startIndex = -1;
		int endIndex = -1;
		boolean can = true;
		for (int i = 0; i < text.length(); i++) {
			ch = text.charAt(i);
			if (ch >= '0' && ch <= '9' && can) {
				if (startIndex == -1) {
					startIndex = i;
					endIndex = i;

				} else {
					endIndex++;
				}
			} else if (ch == fuhao.charAt(0)) {
				can = !can;
			} else {
				if (startIndex != -1)
					break;
			}
		}
		if (startIndex != -1 && endIndex != -1) {
			text = text.substring(0, startIndex) + fuhao
					+ text.substring(startIndex, endIndex + 1) + fuhao
					+ text.substring(endIndex + 1, text.length());
			text = signNumber(text, fuhao);
		}

		return text;
	}

	public static boolean checkout2(String text) {
		try {
			String str = text.substring(0, text.length() - 4);
			if (genProveStr2(str).equals(
					text.charAt(text.length() - 4) + ""
							+ text.charAt(text.length() - 3) + ""
							+ text.charAt(text.length() - 2) + ""
							+ text.charAt(text.length() - 1))) {
				return true;
			}
		} catch (Exception e) {

		}

		return false;
	}

	/*
	 * 根据末尾的校验位检验数字局是否出现错误 return boolean
	 */
	public static boolean checkout(String text) {
		try {
			String str = text.substring(0, text.length() - 3);
			if (genProveStr(str).equals(
					text.charAt(text.length() - 3) + ""
							+ text.charAt(text.length() - 2) + ""
							+ text.charAt(text.length() - 1))) {
				return true;
			}
		} catch (Exception e) {

		}

		return false;
	}

	/*
	 * 将原字符串末尾加上校验位 return 新的字符串
	 */

	public static String addProveStr(String text) {
		String str = genProveStr(text);
		return text + str;
	}
	
	public static String addProveStr2(String text) {
		String str = genProveStr2(text);
		return text + str;
	}

	public static String genProveStr2(String text) {
		int num = 0;
		for (int i = 0; i < text.length(); i++) {
			if (text.charAt(i) == '1') {
				num++;
			}
		}
		num = num % 16;
		String str = Integer.toBinaryString(num);
		return convertTo4(str);
	}

	private static String convertTo4(String str) {
		switch (str.length()) {
		case 1:
			str = "000" + str;
			break;
		case 2:
			str = "00" + str;
			break;
		case 3:
			str = "0" + str;
			break;

		default:
			break;
		}
		return str;
	}

	/*
	 * 根据一个字符串求出其检验位数值，3位，范围：0-F return 检验位
	 */
	private static String genProveStr(String text) {
		char ch;
		int num = 0;
		int num1;
		int num2;
		int num3;
		String str1 = "";
		String str2 = "";
		String str3 = "";
		for (int i = 0; i < text.length(); i++) {
			ch = text.charAt(i);
			if (ch >= '0' && ch <= '9')
				num += (int) (ch - '0') * (i + 1);
			else if (ch >= 'A' && ch <= 'F')
				num += (int) (ch - 'A' + 10) * (i + 1);
		}
		num3 = num * 123 % 16;
		num = num % 256;
		num1 = num / 16;
		num2 = num % 16;
		if (num1 >= 0 && num1 <= 9)
			str1 = (char) (num1 + '0') + "";
		else if (num1 >= 10 && num1 <= 15)
			str1 = (char) (num1 - 10 + 'A') + "";

		if (num2 >= 0 && num2 <= 9)
			str2 = (char) (num2 + '0') + "";
		else if (num2 >= 10 && num2 <= 15)
			str2 = (char) (num2 - 10 + 'A') + "";

		if (num3 >= 0 && num3 <= 9)
			str3 = (char) (num3 + '0') + "";
		else if (num3 >= 10 && num3 <= 15)
			str3 = (char) (num3 - 10 + 'A') + "";
		return (str1 + str2 + str3);
	}

	/*
	 * 若字符串中含有连续相同字符，利用分隔符处理之 return 处理后的字符串
	 */
	public static String dealRepeat(String text, String fenge) {
		int count = 1;
		for (int i = 1; i < text.length(); i++) {
			char ch1 = text.charAt(i - 1);
			char ch2 = text.charAt(i);
			if (ch1 == ch2) {
				count++;
				if (i == text.length() - 1) {
					int startIndex = i - count + 1;
					String newStr = ch1 + fenge + "" + count + fenge;
					String oldStr = text.substring(startIndex, startIndex
							+ count);
					text = text.replace(oldStr, newStr);
					return text;
				}
			} else if (count > 1) {
				int startIndex = i - count;
				String newStr = ch1 + fenge + "" + count + fenge;
				String oldStr = text.substring(startIndex, startIndex + count);
				text = text.replace(oldStr, newStr);
				text = dealRepeat(text, fenge);
				return text;
			}
		}
		return text;
	}

	/*
	 * 将经过重复字符处理的字符串还原 return 未进行重复字符处理的原字符串
	 */
	public static String undealRepeat(String text, String fenge) {
		try {
			int index = text.indexOf(fenge);
			if (index == -1)
				return text;
			char num = text.charAt(index + 1);
			char ch = text.charAt(index - 1);
			int number = num - '0';
			String newStr = "";
			for (int i = 0; i < number; i++) {
				newStr += "" + ch;
			}
			String oldStr = text.substring(index - 1, index + 3);
			text = text.replace(oldStr, newStr);
			text = undealRepeat(text, fenge);
			return text;
		} catch (Exception e) {

		}

		return text;
	}

	/*
	 * 把由0-F表示的字符串转换为汉字
	 */
	public static String GBK2Char(String hexStr) {
		String str = "0123456789ABCDEF";
		// String str = "0123456789abcdef";
		char[] hexs = hexStr.toCharArray();
		byte[] bytes = new byte[hexStr.length() / 2];
		int n;
		for (int i = 0; i < bytes.length; i++) {
			n = str.indexOf(hexs[2 * i]) * 16;
			n += str.indexOf(hexs[2 * i + 1]);
			bytes[i] = (byte) (n & 0xff);
		}
		return new String(bytes);
	}

	/*
	 * 把汉字转换为由0-F表示的字符串
	 */
	public static final String Char2GBK(String str) {
		String rel = "";
		String temp;
		for (int i = 0; i < str.length(); i++) {
			String ch = str.charAt(i) + "";
			byte[] bytes = ch.getBytes();
			temp = bytesToHexString(bytes);
			rel += temp;
		}
		return rel;
	}

	/*
	 * 将byte[]转换为String
	 */
	private static final String bytesToHexString(byte[] bArray) {
		StringBuffer sb = new StringBuffer(bArray.length);
		String sTemp;
		for (int i = 0; i < bArray.length; i++) {
			sTemp = Integer.toHexString(0xFF & bArray[i]);
			if (sTemp.length() < 2)
				sb.append(0);
			sb.append(sTemp.toUpperCase());
		}
		return sb.toString();
	}

	/*
	 * 获取当前编码
	 */
	public static String getEncoding(String str) {
		String encode = "GB2312";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) {
				String s = encode;
				return s;
			}
		} catch (Exception exception) {
		}
		encode = "ISO-8859-1";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) {
				String s1 = encode;
				return s1;
			}
		} catch (Exception exception1) {
		}
		encode = "UTF-8";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) {
				String s2 = encode;
				return s2;
			}
		} catch (Exception exception2) {
		}
		encode = "GBK";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) {
				String s3 = encode;
				return s3;
			}
		} catch (Exception exception3) {
		}
		return "";
	}

	public static class StrInfo {
		boolean issigned;
		String text;

		public StrInfo(boolean issigned, String text) {
			this.issigned = issigned;
			this.text = text;
		}

		public boolean isIssigned() {
			return issigned;
		}

		public void setIssigned(boolean issigned) {
			this.issigned = issigned;
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

		@Override
		public String toString() {
			return "StrInfo [issigned=" + issigned + ", text=" + text + "]";
		}

	}

	// 将字符串切割，比如wen:#13320941403#,切割成wen: 和 13320941403
	public static List<StrInfo> cutText(String text, String fuhao) {
		List<StrInfo> list = new ArrayList<StrInfo>();
		List<Integer> intList = new ArrayList<Integer>();
		boolean isEnd = true;
		int last = 0;
		int index;
		String temp;
		for (int i = 0; i < text.length(); i++) {
			char ch = text.charAt(i);
			if (ch == fuhao.charAt(0)) {
				intList.add(i);
			}
		}
		for (int i = 0; i < intList.size(); i++) {
			index = intList.get(i);
			isEnd = !isEnd;
			if (index == 0) {
				last = index;
			} else {
				if (i == 0)
					temp = text.substring(0, index);
				else
					temp = text.substring(last + 1, index);
				last = index;
				if (isEnd) {
					StrInfo info = new StrInfo(true, temp);
					list.add(info);
				} else {
					StrInfo info = new StrInfo(false, temp);
					list.add(info);
				}
			}
		}

		return list;
	}

	public static String unSignNumber(String text, String fuhao) {
		text = text.replace(fuhao, "");
		return text;
	}

	// 如果字符串中连续出现了超过四次的数字，那么用符号圈出来
	// 如 wen:13320941403 变成 wen:#13320941403#
	public static String signNumber2(String text, String fuhao) {
		Map<String, Integer> map;
		List<Map<String, Integer>> list = new ArrayList<Map<String, Integer>>();
		int start = -1;
		int end = -1;
		char ch;
		int count = 0;
		int last = 0;
		String resultText = "";
		for (int i = 0; i < text.length(); i++) {
			ch = text.charAt(i);
			if (ch >= '0' && ch <= '9') {
				if (count == 0) {
					start = i;
					end = i;
					count++;
				} else {
					count++;
					end++;
				}
				if (i == text.length() - 1 && count >= 4) {
					map = new HashMap<String, Integer>();
					map.put("start", start);
					map.put("end", end);
					list.add(map);
					count = 0;
					start = -1;
					end = -1;
				}
			} else {
				if (count >= 4) {
					map = new HashMap<String, Integer>();
					map.put("start", start);
					map.put("end", end);
					list.add(map);
					count = 0;
					start = -1;
					end = -1;
				} else {
					count = 0;
					start = -1;
					end = -1;
				}
			}
		}

		if (list.size() == 0)
			return text;

		for (int i = 0; i < list.size(); i++) {
			map = (Map<String, Integer>) list.get(i);
			start = map.get("start");
			end = map.get("end");
			resultText += text.substring(last, start);
			last = end + 1;
			resultText += fuhao + text.substring(start, end + 1) + fuhao;
		}
		return resultText;

	}

}
