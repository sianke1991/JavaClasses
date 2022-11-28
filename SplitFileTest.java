package com.example.demo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SplitFileTest {
	
	/**
	 * 테스트 용 100만 라인 파일을 생성한다. (파일명: longFile)
	 */
	void createTestFile() {
		File longFile = new File("longFile");
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(longFile))) {
			for (int i=0; i<1000000; i++) {
				bw.write(getRandomString(100)+"\n");
			} //i loop
		} catch (IOException ioe) {
			System.out.println("failed to create longFile");
			ioe.printStackTrace();
		} //try-catch-auto close
	}
	
	/**
	 * 임의의 문자열을 반환한다.
	 * @param length 반환할 문자열의 길이
	 * @return length 길이를 갖는 임의 문자열. length가 0 이하일 경우 빈 문자열을 반환한다.
	 */
	String getRandomString(int length) {
		if (length<=0) return "";
		char[] availableChars = new char[] {
				'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
				'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '_',
				'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
		};
		char[] result = new char[length];
		for (int i=0; i<length; i++) {
			result[i] = availableChars[(int)(Math.random()*availableChars.length)];
		}
		return new String(result);
	}
	
	/**
	 * 파일에서 읽어오는 문자열
	 */
	String       readStr     = null;
	/**
	 * 파일에서 읽어오는 문자열을 순서대로 저장하는 리스트
	 */
	List<String> readStrs    = null;
	/**
	 * 읽어올 파일 (파일명: longFile)
	 */
	File         longFile    = null;
	/**
	 * <code>longFile</code>를 작은 단위로 나눈 파일.<br/>
	 * (파일명: splitFile000, splitFile001...)
	 */
	File		 splitFile   = null;
	/**
	 * <code>longFile</code>를 나누기 할 단위.<br/>
	 * <code>splitFile</code>들 중 마지막 파일을 제외한 나머지 파일들은 <code>splitLength</code>의 길이를 가지며,<br/>
	 * <code>splitFile</code>들 중 마지막 파일은 <code>splitLength</code> 이하의 길이를 갖는다.
	 */
	int          splitLength = 100000;
	/**
	 * 파일에 쓸 문자열
	 */
	StringBuffer writeStr    = null;
	
	/**
	 * longFile을 10만 라인 단위로 나눈다.
	 */
	@Test
	void splitFile() {
		longFile = new File("longFile");
		readStrs = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(longFile))) {
			while ( (readStr=br.readLine()) != null ) {
				readStrs.add(readStr);
			} //while loop
		} catch (IOException ioe) {
			System.out.println("faled to read longFile");
			ioe.printStackTrace();
			return;
		} //try-catch-auto close
		System.out.println("success to read longFile");
		
		int numSplitFiles = (readStrs.size()+splitLength-1)/splitLength;
		for (int i=0; i<numSplitFiles-1; i++) {
			splitFile = new File(String.format("splitFile%03d", i));
			try (FileWriter fw = new FileWriter(splitFile)) { //기존에 작성된 내용을 전부 지운다.
				fw.write("");
			} catch (IOException ioe) {
				System.out.println(String.format("failed to reset %s", splitFile.getName()));
				ioe.printStackTrace();
				return;
			} //try-catch-auto close
			writeStr = new StringBuffer();
			for (int j=splitLength*i; j<splitLength*(i+1); j++) {
				writeStr.append(readStrs.get(j)).append('\n');
				if (writeStr.length() > 1000000) {
					try (BufferedWriter bw = new BufferedWriter(new FileWriter(splitFile, true))) { //기존에 작성된 내용을 보존하면서 내용을 덧붙인다.
						bw.write(writeStr.toString());
						writeStr = new StringBuffer();
					} catch (IOException ioe) {
						System.out.println(String.format("failed to write %s", splitFile.getName()));
						ioe.printStackTrace();
						return;
					} //try-catch-auto close
				} //if writeStr gets too long
			} //j loop (string line loop)
			try (BufferedWriter bw = new BufferedWriter(new FileWriter(splitFile, true))) { //기존에 작성된 내용을 보존하면서 내용을 덧붙인다.
				bw.write(writeStr.toString());
				writeStr = new StringBuffer();
			} catch (IOException ioe) {
				System.out.println(String.format("failed to write %s", splitFile.getName()));
				ioe.printStackTrace();
				return;
			} //try-catch-auto close
			System.out.println(String.format("success to write %s", splitFile.getName()));
		} //i loop (file loop - except for the last file)
		
		
		//the last file
		splitFile = new File(String.format("splitFile%03d", numSplitFiles-1));
		try (FileWriter fw = new FileWriter(splitFile)) { //기존에 작성된 내용을 전부 지운다.
			fw.write("");
		} catch (IOException ioe) {
			System.out.println(String.format("failed to reset %s", splitFile.getName()));
			ioe.printStackTrace();
			return;
		} //try-catch-auto close
		for (int j=splitLength*(numSplitFiles-1); j<readStrs.size(); j++) {
			writeStr.append(readStrs.get(j)).append('\n');
			if (writeStr.length() > 1000000) {
				try (BufferedWriter bw = new BufferedWriter(new FileWriter(splitFile, true))) { //기존에 작성된 내용을 보존하면서 내용을 덧붙인다.
					bw.write(writeStr.toString());
					writeStr = new StringBuffer();
				} catch (IOException ioe) {
					System.out.println(String.format("failed to write %s", splitFile.getName()));
					ioe.printStackTrace();
					return;
				} //try-catch-auto close
			} //if writeStr gets too long
		} //j loop (string line loop)
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(splitFile, true))) { //기존에 작성된 내용을 보존하면서 내용을 덧붙인다.
			bw.write(writeStr.toString());
			writeStr = new StringBuffer();
		} catch (IOException ioe) {
			System.out.println(String.format("failed to write %s", splitFile.getName()));
			ioe.printStackTrace();
			return;
		} //try-catch-auto close
		System.out.println(String.format("success to write %s", splitFile.getName()));	
	}
}
