package masking;

public class MaskingUtils {
	/**
	 * 입력받은 문자열이 <code>blank</code>인지 여부를 반환한다.<br/>
	 * 입력받은 문자열이 <code>null</code>일 경우 <code>true</code>를 반환한다.
	 * @param str
	 * @return
	 */
	private static boolean isBlank(CharSequence str) {
		return str==null || str.toString().trim().length()==0;
	}
	
	/**
	 * 한글로 이루어진 문자열을 마스킹 처리한 결과를 반환한다.
	 * @param strArr
	 * @return
	 */
	private static String getHanMasked(char[] input) {
		char[] strArr = new char[input.length];
		System.arraycopy(input, 0, strArr, 0, input.length);
		switch (strArr.length) {
			case 0:
				return "";
			case 1:
				return Character.toString(strArr[0]);
			case 2: case 3:
				if (!Character.isWhitespace(strArr[1])) strArr[1] = '*';
				return new String(strArr);
			default:
				int lenMinusOne = strArr.length-1;
				for (int i=1; i<lenMinusOne; i++) {
					if (Character.isWhitespace(strArr[i])) continue;
					strArr[i] = '*';
				}
				return new String(strArr);
		}
	}
	
	/**
	 * 한글이 아닌 다른 문자가 포함된 문자열을 마스킹 처리한 결과를 반환한다.
	 * @param strArr
	 * @return
	 */
	private static String getOthersMasked(char[] input) {
		char[] strArr = new char[input.length];
		System.arraycopy(input, 0, strArr, 0, input.length);
		switch (strArr.length) {
			case 0: case 1: case 2: case 3: case 4:
				return new String(strArr);
			default:
				for (int i=4; i<strArr.length; i++) {
					if (Character.isWhitespace(strArr[i])) continue;
					strArr[i] = '*';
				}
				return new String(strArr);
		}
		
	}
	
	/**
	 * 입력받은 문자열을 마스킹 한 결과를 반환한다.
	 * @param str
	 * @return
	 */
	public static String getMasked(String str) {
		if (isBlank(str)) return "";
		char[] strArr = str.toCharArray();
		char minChar = strArr[0];
		char maxChar = strArr[0];
		for (char ch:strArr) {
			if (ch>maxChar) maxChar = ch;
			else if (ch<minChar) minChar = ch;
		}
		if (minChar>='가' && maxChar<='힣') {
			return getHanMasked(strArr);
		} else {
			return getOthersMasked(strArr);
		}
	}
	
	public static void main(String[] args) {
		System.out.println(MaskingUtils.getMasked("이산화 탄소")); //이산화 **
		System.out.println(MaskingUtils.getMasked("이산화탄소")); //이***소
		System.out.println(MaskingUtils.getMasked("Carbon Dioxide")); //Carb** *******
	}	

}
