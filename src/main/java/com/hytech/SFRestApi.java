package com.hytech;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

@Path("/SFAPI")
public class SFRestApi {
	
	public  String SERVER   = "192.168.0.34";
	public  int    PORT     = 21;
	public  String USERNAME = "salesforce";
	public  String PASSWORD = "salesforce@123";
		
	//sample URL to run this method
	//http://9441f9c7.ngrok.io/AutoCadSF/rest/sfrestapi/absar
	@GET
	@Path("/{param}")
	public Response getMessage(@PathParam("param") String msg) {
		String output = "My Rest Api :"+msg;
		System.out.println(output);	
		return Response.status(200).entity(output).build();
	}
	
	
	/*
	 * This Method will the the file whether 
	 * File is Present in FTP folder or Not
	 * if all 3 file present and the status code is 200
	 * 		->Then we only request the file to download via API
	 * else returning status code is 404
	 * 		->Not to hit the API to download file
	 * 
	 * @checkFilesNeedToAttachSF(data)
	 * accept data in from comma separated format like
	 * ["Q-0249-20181114","Q-02509-20181114","Q-04324-20181114"]
	 * Converting List<String> using JSON.serialize() return above format of data
	 */
	
	
	@POST
	@Consumes("text/plain")
	@Path("/check")
	public Response checkFilesNeedToAttachSF(String allPendingFiles)throws Exception {
		System.out.println("Inside file check method");
    	FTPClient ftpClient = new FTPClient();
        ftpClient.connect(SERVER, PORT);
        boolean loginsuccess = ftpClient.login(USERNAME, PASSWORD);
        
		System.out.println("Login :"+loginsuccess);
		
		ArrayList<String> returnData = new ArrayList<String>();
		
		if(allPendingFiles!=null && allPendingFiles!="" && allPendingFiles.contains("[")){
			allPendingFiles = allPendingFiles.replace("[","");
		}
		if(allPendingFiles!=null && allPendingFiles!="" && allPendingFiles.contains("]")){
			allPendingFiles = allPendingFiles.replace("]","");
		}
		if(allPendingFiles!=null && allPendingFiles!="" && allPendingFiles.contains("\"")){
			allPendingFiles = allPendingFiles.replace("\"","");
		}
		
		System.out.println(allPendingFiles.length());

		if(allPendingFiles.length()>0 && allPendingFiles!=null && allPendingFiles!="")
		{
			List<String> arrList = new ArrayList<String>();
			arrList = Arrays.asList(allPendingFiles.split(","));
			System.out.println("arrList:"+arrList);
			for(String fileName : arrList) {
				System.out.println("fileName->"+fileName);
				if(checkFileFTP(fileName,ftpClient)) {
					returnData.add(fileName);
				}
			}
			if(!returnData.isEmpty()) {
				String data = "";
				for(String str : returnData) {
					if(data == "")
						data = str;
					else
						data = data+","+str;
				}
				//return comma separated data as response
				return Response.status(200).entity(data).build();
			}
			else
				System.out.println("No File is found to download");
				return Response.status(404).entity(null).build();
		}
		else 
		{
			System.out.println("Returning 404 file not found");
			return Response.status(404).entity(null).build();
		}		
	}
	
	private boolean checkFileFTP(String fileName,FTPClient ftpClient) {		
		if(fileName.contains("\"")) {
			fileName = fileName.replace("\"","");
		}
		String file1 = "/"+fileName+"-1.pdf";
		String file2 = "/"+fileName+"-2.pdf";
		String file3 = "/"+fileName+"-3.pdf";
        try {
            FTPFile[] remoteFile1 = ftpClient.listFiles(file1);
            FTPFile[] remoteFile2 = ftpClient.listFiles(file2);
            FTPFile[] remoteFile3 = ftpClient.listFiles(file3);
            return ((remoteFile1!=null && remoteFile1.length > 0) && (remoteFile2!=null && remoteFile2.length > 0) && ( remoteFile3!=null && remoteFile3.length > 0));
        }catch(Exception exp) {
        	exp.printStackTrace();
        }
		return false;
	}
	
	
	
	//return the binary data of PDF file
	//http://9441f9c7.ngrok.io/AutoCadSF/rest/sfrestapi/download/Q-02439-1.pdf
	
	@GET
	@Path("/download/{param}")
	public Response getFileData(@PathParam("param") String fileName) {
		System.out.println("before downloading :"+fileName);
		boolean downloaded = DownloadFileFTP.download(SERVER, PORT, USERNAME, PASSWORD, fileName);
		byte[] fileContent = null; // response the PDF binary data to end user
		if(downloaded) {
			try {
				fileContent = FileUtils.readFileToByteArray(new File("C:/tmp/"+fileName));
				//now delete file form server 
				File fileDelete = new File("C:/tmp/"+fileName);
				if(fileDelete.delete()) { 
					System.out.println("File deleted form server successfully !!");
				}
			} catch (IOException e) {
				try {
					throw new IOException("Unable to convert file to byte array. " + e.getMessage());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} 
		}	
		return Response.status(200).entity(fileContent).build();
	}
	
	
	
	//get the data from post request in row format with (quote name)
	//http://9441f9c7.ngrok.io/AutoCadSF/rest/sfrestapi/Q-02439
	
	@POST
	@Consumes("text/plain")
	@QueryParam("/{quotename}")
	public Response getMyPostData(String data, @QueryParam("quotename") String quoteName)throws Exception {
		System.out.println("Post method called QuoteName :"+quoteName);
		System.out.println(data);
		//creating file using data pass to it
		//find the file using quoteName
		//if present override it 
		//else create a new tab delimited file
		PrintWriter pw;
		File f = new File("C://tmp//"+quoteName+".txt");
		if(f.exists() && !f.isDirectory()) { 
		    pw = new PrintWriter(f);
		    for(String str : data.split("\n")) {
		    	pw.print(str);
		    	pw.println();
		    }
		    pw.flush();
		    pw.close();
		}else {
			File fn = new File("C://tmp//"+quoteName+".txt");
			if(fn.createNewFile()) {
				pw = new PrintWriter(fn);
			    for(String str : data.split("\n")) {
			    	pw.print(str);
			    	pw.println();
			    }
			    pw.flush();
			    pw.close();
			}
		}
			
		//Now upload file to a folder using FTP connection
		UploadFileFTP.uploadFTP(SERVER, PORT, USERNAME, PASSWORD, quoteName);
		return Response.status(200).entity(quoteName).build();
	}
}
