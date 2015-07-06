package br.ufrj.cos.redes.main;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import br.ufrj.cos.redes.receiver.ReceiverBufferPlayerExample;
import br.ufrj.cos.redes.sender.SenderExample;

public class Main {
	
	public static void main(String[] args) {
		
		//these values are always read in config.properties
		boolean isSender = false;
		String RTT = null;		
		String TIMESTAMP_LOG_FILE_NAME = null;
		String LATENCY_LOG_FILE_NAME = null;

		//if chosen sender in 'config.properties', these are the default values
		String REQUESTED_FILE_NAME = "C:\\Users\\Joao&Duda\\Desktop\\JP\\2015-1\\redes\\testinput.txt"; 	
		String RECEIVED_FILE_NAME = "C:\\Users\\Joao&Duda\\Desktop\\JP\\2015-1\\redes\\testrecv.txt";
//		String REQUESTED_FILE_NAME = "C:\\Users\\Pedro Freitas\\Desktop\\testinput.txt"; 	
//		String RECEIVED_FILE_NAME = "C:\\Users\\Pedro Freitas\\Desktop\\testrecv.txt";		
		String B = "50";	
		String F = "0.0";

		//if chosen receiver in 'config.properties', these are the default values
		String sequentialOrRandom = "Random";
		String r = "-1";		
		
		
		
		
		Properties prop = new Properties();
		InputStream input = null;
		String propFileName = "config.properties";
		
		try {			
			input = Main.class.getClassLoader().getResourceAsStream(propFileName);
			
			if(input == null) {
				System.out.println("Failure to load the file '" + propFileName + "'");
				return;
			}
			
			prop.load(input);

			isSender = prop.getProperty("SENDER_OR_RECEIVER").equalsIgnoreCase("Sender");
			if(isSender) {
				sequentialOrRandom = prop.getProperty("SEQUENTIAL_OR_RANDOM");
				if(sequentialOrRandom.equalsIgnoreCase("Sequential")) {
					r = prop.getProperty("r");					
				}
			} else {
				REQUESTED_FILE_NAME = prop.getProperty("REQUESTED_FILE_NAME");
				RECEIVED_FILE_NAME = prop.getProperty("RECEIVED_FILE_NAME");
				B = prop.getProperty("B");
				F = prop.getProperty("F");
			}
			RTT = prop.getProperty("RTT");
			TIMESTAMP_LOG_FILE_NAME = prop.getProperty("TIMESTAMP_LOG_FILE_NAME");
			LATENCY_LOG_FILE_NAME = prop.getProperty("LATENCY_LOG_FILE_NAME");
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		String[] args1 = {sequentialOrRandom, r};		
		String[] args2 = {REQUESTED_FILE_NAME, RECEIVED_FILE_NAME, B, F, RTT, TIMESTAMP_LOG_FILE_NAME, LATENCY_LOG_FILE_NAME};
		
		
		Thread senderThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				SenderExample.main(args1);	
			}
		});

		Thread receiverThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				ReceiverBufferPlayerExample.main(args2);	
			}
		});
		
		senderThread.start();
		receiverThread.start();
		
	}
}
