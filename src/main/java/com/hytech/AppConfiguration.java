package com.hytech;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.Properties;

import org.apache.commons.net.ftp.FTPClient;

public class AppConfiguration {
	 public static void main(String[] args) {
			
			Properties prop = new Properties();
			OutputStream output = null;
			FTPClient ftpClient = new FTPClient();
			
            try {
				ftpClient.connect("192.168.0.34",21);
			} catch (SocketException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
            
            boolean islogin;
			try {
				islogin = ftpClient.login("salesforce", "salesforce@123");
				System.out.println(islogin);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
            
			
			//first make connection with FTP Credential
			
			//if success then save it to properties file
			
			//otherwise prompt for valid credential
			

			try {

				output = new FileOutputStream("config.properties");
				// set the properties value
				prop.setProperty("SERVER", "192.168.0.34");
				prop.setProperty("PORT", "21");
				prop.setProperty("USERNAME", "salesforce");
				prop.setProperty("PASSWORD", "salesforce@123");
				// save properties to project root folder
				prop.store(output, null);

			} catch (IOException io) {
				io.printStackTrace();
			} finally {
				if (output != null) {
					try {
						output.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		  }
}
