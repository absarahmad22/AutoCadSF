package com.hytech;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
public class DownloadFileFTP {
	  public static boolean download(String server,int port,String user,String pass,String fileName) {
		  	boolean success = false;
	        FTPClient ftpClient = new FTPClient();
	        try {
	 
	            ftpClient.connect(server, port);
	            ftpClient.login(user, pass);
	            ftpClient.enterLocalPassiveMode();
	            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

	            String remoteFile = "/"+fileName;
	            System.out.println("remote File :"+remoteFile);
	            File downloadFile = new File("C:/tmp/"+fileName);
	            OutputStream outputStream1 = new BufferedOutputStream(new FileOutputStream(downloadFile));
	            success = ftpClient.retrieveFile(remoteFile, outputStream1);
	            outputStream1.close();
	            System.out.println("Success :"+success);
	            if (success) {
	                System.out.println("File named :"+fileName+" has been downloaded successfully.");
	            }
	 
	        } catch (IOException ex) {
	            System.out.println("Error: " + ex.getMessage());
	            ex.printStackTrace();
	        } finally {
	            try {
	                if (ftpClient.isConnected()) {
	                    ftpClient.logout();
	                    ftpClient.disconnect();
	                }
	            } catch (IOException ex) {
	                ex.printStackTrace();
	            }
	        }
	        return success;
	    }
}
