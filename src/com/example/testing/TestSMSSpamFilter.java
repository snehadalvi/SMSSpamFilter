package com.example.testing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.StringTokenizer;
import com.example.smsspamfilter.AndroidReceiver;

public class TestSMSSpamFilter {

	/**
	 * @param args
	 */
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		AndroidReceiver ad=new AndroidReceiver();
		HashMap<String, Integer> SpamWord = new HashMap<String, Integer>();
		String line, word="";
		int weight=0;
		BufferedReader reader;
		
		try{
			reader = new BufferedReader(new FileReader("\\assets\\spam_keywords.txt"));
			while((line = reader.readLine()) != null){
				StringTokenizer st = new StringTokenizer(line);
				while(st.hasMoreElements())
				{
					word=st.nextElement().toString();
					weight= Integer.parseInt(st.nextElement().toString());
				}
				SpamWord.put(word, weight);
				System.out.println(word+" "+weight);
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		//System.out.println(SpamWord.toString());
	}

}
