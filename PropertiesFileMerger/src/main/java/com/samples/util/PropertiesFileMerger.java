package com.samples.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;

public class PropertiesFileMerger {
	private static final String PATH_SEPARATOR = "\\";
	private static final String rootPath = "D:\\project_folder\\environment";

	public static void main(String[] args) throws IOException {		
		File file = new File(rootPath + "/localhost");
		String[] files = file.list();

		List<String> environmentFoldersToScan = listFolders(rootPath);		 

		for (int i = 0; i < files.length; i++) {
			boolean moveFile = false;
			String fileToCompare =null;
			List<String> diffFiles = new ArrayList<String> ( );
			String originalFile = file.getPath() + PATH_SEPARATOR + files[i];
		
			System.out.println("------------------------------- Comparing "	+ files[i] + "----------------------------------");			 
		
			
			for (String environmentFolder : environmentFoldersToScan) {				
				fileToCompare = rootPath+PATH_SEPARATOR+environmentFolder+PATH_SEPARATOR+files[i];
				if(compareFiles(originalFile, fileToCompare))
				{
					moveFile = true;
					deleteFile(fileToCompare);
				}
				else
				{
					diffFiles.add(fileToCompare);
				}
			}			
			
			if(diffFiles.size()>0)
			{
				 for(String diffFile : diffFiles)
				 {
					 File modifiedFile = new File(diffFile);
					 if(modifiedFile.exists())
					 {
						 List<String> originalFileContent = FileUtils.readLines(new File(originalFile));					
						 List<String> envFileContent = FileUtils.readLines(modifiedFile);
						 List<String> updatedLines = envFileContent.stream().filter(s -> !originalFileContent.contains(s)).collect(Collectors.toList());
						 FileUtils.writeLines(modifiedFile, updatedLines, false);
					 }
				 }
			}
			
			if(moveFile)
			{
				moveFileToCommonFolder(originalFile, rootPath + "\\common\\" + files[i]);
				deleteFile(originalFile);
			}
		}
	}

	
	private static void moveFileToCommonFolder(String originalFileLocation, String newFileLocation) throws IOException {		 
		   File fileToCopy = new File(originalFileLocation);
		   File newFile = new File(newFileLocation);		 
		   FileUtils.copyFile(fileToCopy, newFile);
	}


	private static void deleteFile(String duplicateFile) throws IOException {		 
		  FileUtils.forceDelete(FileUtils.getFile(duplicateFile));
	}


	private static boolean compareFiles(String file1, String file2)	throws IOException {		
		File file_1 = new File(file1);
		File file_2 = new File(file2);
		boolean matchFound= FileUtils.contentEquals(file_1, file_2);
		System.out.println(file1+ " , " + file2 + " : "+ matchFound);
		return matchFound;
	}
	

	public static List<String> listFolders(String directoryName) {
		List<String> directoryList = new ArrayList<String>();
		File directory = new File(directoryName);
		File[] fList = directory.listFiles();
		for (File file : fList) {
			if (file.isDirectory() && !file.getName().equals("localhost")) {
				directoryList.add(file.getName());
			}
		}
		return directoryList;
	}
}
