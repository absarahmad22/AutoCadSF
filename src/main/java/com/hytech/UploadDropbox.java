package com.hytech;

import java.io.FileInputStream;
import java.io.InputStream;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.GetMetadataErrorException;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.users.FullAccount;

public class UploadDropbox {
	private static final String ACCESS_TOKEN_CHOICE = "o9bf_y0JM7AAAAAAAAAAheaUuZseOjvWHYVY_N5-LG74Yb5ydPMqC7emLh4Elx5L";

	@SuppressWarnings("deprecation")
	public static void uploadFileDropbox(String fileName) throws DbxException {
		DbxClientV2 client = congfigureDropbox();
		 try {
		        if(checkFileInDropbox("/FileShareAutoCad/"+fileName+".txt",client)) {
		        	client.files().delete("/FileShareAutoCad/"+fileName+".txt");
		        	System.out.println("file deleted from server");
		        }
		        try (InputStream in = new FileInputStream("c://tmp//"+fileName+".txt")) {
		            @SuppressWarnings("unused")
					FileMetadata metadata = client.files().uploadBuilder("/FileShareAutoCad/"+fileName+".txt").uploadAndFinish(in);
		            System.out.println("File Uploaded Successfully !!");
		        }
			}catch(Exception exp) {
				System.out.println("Exception occur :"+exp.getMessage());
				System.out.println("Error in File Upload !!");
			}
	}
	
	private static boolean checkFileInDropbox(String dropboxPath,DbxClientV2 client ) throws DbxException {
        try {
            client.files().getMetadata(dropboxPath);
            System.out.println("File found.");
            return true;
        } catch (GetMetadataErrorException e){
            if (e.errorValue.isPath() && e.errorValue.getPathValue().isNotFound()) {
                System.out.println("File not found.");
                return false;
            }
        }
		return true;
	}
	
	private static DbxClientV2 congfigureDropbox() {
		DbxClientV2 client = null;
		try {
			DbxRequestConfig config = new DbxRequestConfig("dropbox/SFAutoCad");
	        client = new DbxClientV2(config, ACCESS_TOKEN_CHOICE);
			FullAccount account = client.users().getCurrentAccount();
	        System.out.println(account.getName().getDisplayName());
			ListFolderResult result = client.files().listFolder("");
	        while (true) {
	            for (Metadata metadata : result.getEntries()) {
	                System.out.println(metadata.getPathLower());
	            }
	            if (!result.getHasMore()) {
	                break;
	            }
	            result = client.files().listFolderContinue(result.getCursor());
	       }
		}catch(Exception exp) {
			exp.printStackTrace();
		}
		return client;
	}
}
