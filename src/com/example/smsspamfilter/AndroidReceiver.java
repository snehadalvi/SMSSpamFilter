package com.example.smsspamfilter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class AndroidReceiver extends BroadcastReceiver {
	private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
	final int spam_threshhold = 10;
	HashMap<String, Integer> SpamWord = new HashMap<String, Integer>();

	@Override
	public void onReceive(Context context, Intent intent) {
		//this stops notifications to others
		this.abortBroadcast();

		if (intent.getAction() == SMS_RECEIVED) {
			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				Object[] pdus = (Object[])bundle.get("pdus");
				final SmsMessage[] messages = new SmsMessage[pdus.length];
				String msg, sender, fmsg;

				for (int i = 0; i < pdus.length; i++) {
					messages[i] = SmsMessage.createFromPdu((byte[])pdus[i]);

				msg=messages[i].getMessageBody().toString()+"\n";
				sender=messages[i].getOriginatingAddress().toString();
				fmsg= "\nSMS from "+sender+" : "+msg;

				//Load Spam keywords in a Hashmap
				
				SpamWord=loadSpamWords(context);
				
				//Check if the sms is spam or not
				if(!isSpam(msg, sender)){
					//if it is not a spam notify the user and store the sms in inbox
					this.clearAbortBroadcast(); 
				}
				else{
					//else store the sms in a db and not in inbox and do not notify the user
					saveSpam(fmsg, context);
					}//end if else
				}
		}
		}
	}

	public boolean isSpam(String msg, String sender)
	{	
		int weight=0;
		boolean flag=false;
		String regex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
		
		//sender="abc";
		if(!sender.matches("\\d{11}"))
			return true;
		
		//work remaining: 
		//1. work on URL shortners and domains
		
		//if(containsURL(msg))
		//	return true;
		
		StringTokenizer st= new StringTokenizer(msg);
		String word="";

		while(st.hasMoreElements())
		{
			word = st.nextElement().toString();
				
			if(word.matches("(.*%)|(.*!+)") || word.contains("$"))
				weight=weight+4;
			
			if(word.matches(regex))
				weight=weight+8;
			
			word=word.toLowerCase();
			
			if(SpamWord.containsKey(word)){
				weight=weight+SpamWord.get(word);
			}
		}

		if(weight<=spam_threshhold)
			flag=false;
		else
			flag=true;

		return flag;
	}

	public boolean containsURL(String msg)
	{
		boolean flag=false;
		String regex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
		StringTokenizer st= new StringTokenizer(msg);
		String word="";

		while(st.hasMoreElements())
		{
			word = st.nextElement().toString();
			Pattern patt = Pattern.compile(regex);
			Matcher matcher = patt.matcher(word);
			flag=matcher.matches();
			if(flag)
				break;
		}
		return flag;

	}

	public void saveSpam(String fmsg, Context context)
	{

		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(context.openFileOutput("SPAM_SMS_DATA",context.MODE_APPEND)));
			writer.write("\r"+fmsg);
		} 
		catch (Exception e) {
			e.printStackTrace();
		} 
		finally {
			if (writer != null) {
				try {
					writer.close();
				} 
				catch (IOException e) {
					e.printStackTrace();
				}     						
			}
		}
	}

	public HashMap<String, Integer> loadSpamWords(Context context)
	{
		HashMap<String, Integer> SpamWord = new HashMap<String, Integer>();
		String line, word="";
		int weight=0;
		try
		{
			BufferedReader reader;
			final InputStream file = context.getAssets().open("spam_keywords.txt");
			reader = new BufferedReader(new InputStreamReader(file));
			while((line = reader.readLine()) != null){
				StringTokenizer st = new StringTokenizer(line);
				while(st.hasMoreElements())
				{
					word=st.nextElement().toString();
					weight= Integer.parseInt(st.nextElement().toString());
				}
				SpamWord.put(word, weight);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		System.out.println(SpamWord);
		return SpamWord;
	}

}
