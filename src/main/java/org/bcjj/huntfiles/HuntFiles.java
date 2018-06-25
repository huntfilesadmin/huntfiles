package org.bcjj.huntfiles;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.bcjj.huntfiles.FileInfo.FileType;
import org.bcjj.huntfiles.gui.HuntFilesMainWindow;

import com.github.junrar.Archive;
import com.github.junrar.exception.RarException;
import com.github.junrar.rarfile.BaseBlock;


public class HuntFiles  {

	public static void main(String [] s) throws Exception {
		SearchOptions searchOptions=null;
		try {
			 searchOptions=new SearchOptions(s);
		} catch (Exception r) {
			return;
		}
		
		HuntFilesListener huntFilesListener = new HuntFilesListener() {
			@Override
			public void addFile(FileInfo fileInfo) {
				System.out.println(fileInfo.toString(true,"  > "));
			}

			@Override
			public void addError(String err) {
				System.out.println("ERROR "+err);
			}

			@Override
			public void updateProgress(int foundFiles, int searchedFiles, int totalFiles) {
				//System.out.println("foundFiles:"+foundFiles+" in  searchedFiles:"+searchedFiles+" /  totalFiles:"+totalFiles);
			}
			
			@Override
			public void workingInArchive(String archive) {
				System.out.println("working in "+archive);
			}
			
		};
		
		if (!searchOptions.isConsole()) {
			HuntFilesMainWindow.main(searchOptions);
		} else if (searchOptions.isValid()){
			HuntFiles huntFiles=new HuntFiles(searchOptions,huntFilesListener);
			String stats=huntFiles.search();
			System.out.println("search stats:"+stats);
		}
	}
	
	HuntFilesListener listener=null;
	SearchOptions searchOptions=null;
	boolean stop=false;
	
	public HuntFiles(SearchOptions searchOptions,HuntFilesListener listener)  {
		this.listener=listener;
		this.searchOptions=searchOptions;
	}
	


	public String search() throws Exception {
		stop=false;
		if (searchOptions==null || searchOptions.getDir()==null) {
			throw new Exception("Initial Directory is needed");
		}
		searchInDir(new File(searchOptions.getDir()));
		return "ok";
	}

	
	
	
	
	
	
	
	private void searchInDir(File directory) {
		File [] files=directory.listFiles();
		if (files==null) {
			return;
		}
		listener.workingInArchive(directory.getPath());
		for (File file:files) {
			if (stop) {
				return;
			}
			if (file.isFile()) {
				String ext=FilenameUtils.getExtension(file.getName());
				if (ext!=null) {
					if (ext.equalsIgnoreCase("zip") || ext.equalsIgnoreCase("jar") || ext.equalsIgnoreCase("war")) {
						if (searchOptions.isZipjar()) {
							try {
								listener.workingInArchive(file.getPath());
								searchInZip(file);
							} catch (Exception r) {
								listener.addError("ERROR in zipFile "+file+" :: "+r);
							}
							listener.workingInArchive(directory.getPath());
						}
					} else if (ext.equalsIgnoreCase("7z")) {
						if (searchOptions.isZ7()) {
							try {
								listener.workingInArchive(file.getPath());
								searchIn7Z(file);
							} catch (Exception r) {
								listener.addError("ERROR in zipFile "+file+" :: "+r);
							}
							listener.workingInArchive(directory.getPath());
						}
					} else if (ext.equalsIgnoreCase("rar")) {
						if (searchOptions.isRar()) {
							try {
								listener.workingInArchive(file.getPath());
								searchInRar(file);
							} catch (Exception r) {
								listener.addError("ERROR in zipFile "+file+" :: "+r);
							}
							listener.workingInArchive(directory.getPath());
						}
					} else {
						searchInFile(file,ext.toLowerCase());
					}
				} else {
					searchInFile(file,"txt");
				}
				//String ext1 = FilenameUtils.getExtension("/path/to/file/foo.txt"); // returns "txt"
			}
		}
		if (searchOptions.isRecursive()) {
			for (File dir:files) {
				if (stop) {
					return;
				}
				searchInDir(dir);
			}
		}
	}

	
	
	
	private void searchInZip(File file) throws Exception {
		ZipFile zipFile=new ZipFile(file);
		Enumeration<? extends ZipEntry> entries = zipFile.entries();
		Hashtable<String, Long> h=new Hashtable<String, Long>();
		while (entries.hasMoreElements()) {
			if (stop) {
				return;
			}
		    ZipEntry zipEntry = entries.nextElement();
		    if (!zipEntry.isDirectory()) {
		    	searchInZipEntry(zipEntry,zipFile,file);
		    }
		}
	}
	
	
	private void searchInRar(File file) throws Exception {
		throw new Exception("Rar Search No Implemented yet");
	}	
	
	private String getNameFromPath(String path) {
		String name=path;
		path=StringUtils.replace(path,"\\","/");
		int ult=path.lastIndexOf("/");
		if (ult>-1) {
			name=path.substring(ult+1);
		} else {
			name=path;
		}
		return name;
	}
	
	private void searchInZipEntry(ZipEntry zipEntry,ZipFile zipFile,File file) {
		String path=zipEntry.getName();
		
		if (searchOptions.getFilename()!=null) {
			String zipPath=zipEntry.getName();
			String name=getNameFromPath(zipPath);
			if (!FilenameUtils.wildcardMatch(name, searchOptions.getFilename(), IOCase.INSENSITIVE)) {
				return;
			}
		}
		
		if (searchOptions.getAfter()!=null) {
			if (zipEntry.getTime()<searchOptions.getAfter()) {
				return;
			}
		}
		
		if (searchOptions.getBefore()!=null) {
			if (zipEntry.getTime()>searchOptions.getBefore()) {
				return;
			}
		}
		
		if (searchOptions.getGreaterThan()!=null) {
			if (zipEntry.getSize()<searchOptions.getGreaterThan()) {
				return;
			}
		}
		
		if (searchOptions.getLessThan()!=null) {
			if (zipEntry.getSize()>searchOptions.getLessThan()) {
				return;
			}
		}
		
		if (searchOptions.getIgnorePaths()!=null && searchOptions.getIgnorePaths().size()>0) {
			String filePath=path;
			filePath=StringUtils.replace(filePath, "\\", "/");
			filePath=StringUtils.replace(filePath, "//", "/");
			for (String ignorePath:searchOptions.getIgnorePaths()) {
				if (FilenameUtils.wildcardMatch(filePath, ignorePath, IOCase.INSENSITIVE)) {
					return;
				}
			}
		}
		
		List<Hit> hits=null;
		if (searchOptions.getText()!=null) {
			try (InputStream is=zipFile.getInputStream(zipEntry)) {
				hits=searchText(is,searchOptions.getText());
				if (hits.size()==0) {
					return;
				}
			} catch (Exception r) {
				listener.addError("Error reading "+file+"|"+path+" :: "+r);
			}
		}
		
		FileInfo fileInfo=new FileInfo(file, path, zipEntry.getSize(), zipEntry.getTime() ,hits, FileType.Zip,searchOptions);
		listener.addFile(fileInfo);
		return;
	}
	
	
	
	private void searchInFile(File file, String ext) {
		
		if (searchOptions.getFilename()!=null) {
			if (!FilenameUtils.wildcardMatch(file.getName(), searchOptions.getFilename(), IOCase.INSENSITIVE)) {
				return;
			}
		}
		
		if (searchOptions.getAfter()!=null) {
			if (file.lastModified()<searchOptions.getAfter()) {
				return;
			}
		}
		
		if (searchOptions.getBefore()!=null) {
			if (file.lastModified()>searchOptions.getBefore()) {
				return;
			}
		}
		
		if (searchOptions.getGreaterThan()!=null) {
			if (file.length()<searchOptions.getGreaterThan()) {
				return;
			}
		}
		
		if (searchOptions.getLessThan()!=null) {
			if (file.length()>searchOptions.getLessThan()) {
				return;
			}
		}
		
		if (searchOptions.getIgnorePaths()!=null && searchOptions.getIgnorePaths().size()>0) {
			String filePath=file.getPath();
			filePath=StringUtils.replace(filePath, "\\", "/");
			filePath=StringUtils.replace(filePath, "//", "/");
			for (String ignorePath:searchOptions.getIgnorePaths()) {
				if (FilenameUtils.wildcardMatch(filePath, ignorePath, IOCase.INSENSITIVE)) {
					return;
				}
			}
		}
		
		List<Hit> hits=null;
		if (searchOptions.getText()!=null) {
			try (InputStream is=new FileInputStream(file)) {
				hits=searchText(is,searchOptions.getText());
				if (hits.size()==0) {
					return;
				}
			} catch (Exception r) {
				listener.addError("Error reading "+file+" :: "+r);
			}
		}
		
		FileInfo fileInfo=new FileInfo(file, null,null,null, hits, FileType.File,searchOptions);
		listener.addFile(fileInfo);
		
	}

	

	
	private List<Hit> searchText(InputStream is, String text) throws Exception {
		text=text.toLowerCase();
		BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(is));
		String line="";
		int lineNum=0;
		List<Hit> hits=new ArrayList<Hit>();
		while ((line=bufferedReader.readLine())!=null) {
			lineNum++;
			if (line.toLowerCase().indexOf(text)>-1) {
				Hit hit=new Hit(lineNum, line);
				hits.add(hit);
			}
		}
		return hits;
	}







	private void searchIn7Z(File file) throws Exception {
		throw new Exception("7Z search No Implemented yet");
	}







	public void endSearch() {
		stop=true;
	}
	
	

	
	
	
	
	
	
	
}
