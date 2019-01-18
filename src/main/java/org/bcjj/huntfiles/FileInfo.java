package org.bcjj.huntfiles;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.lang3.StringUtils;
import org.bcjj.huntfiles.util.SevenZInputStream;

import com.github.junrar.Archive;
import com.github.junrar.rarfile.FileHeader;

public class FileInfo {

	public enum FileType {
		File,Zip,Z7,Rar	
	};
	
	File file;
	String name;
	String pathInPackage;
	String fechMod; 
	Long size;
	FileType fileType;
	SearchOptions searchOptions;
	
	List<Hit> hits=null;
	public FileInfo(File file, String pathInPackage, Long sizeInPackage, Long dateInPackage, List<Hit> hits, FileType fileType,SearchOptions searchOptions) {
		super();
		this.file = file;
		this.fileType=fileType;
		this.searchOptions=searchOptions;
		
		if (StringUtils.isBlank(pathInPackage)) {
			name=file.getName();
		} else {
			String x=pathInPackage;
			x=StringUtils.replace(x,"\\","/");
			int ult=x.lastIndexOf("/");
			if (ult>-1) {
				name=x.substring(ult+1)+" : ("+file.getName()+")";
			} else {
				name=x+" : ("+file.getName()+")";
			}
		}
		
		Date d=new Date(file.lastModified());
		if (dateInPackage!=null) {
			d=new Date(dateInPackage);
		}
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		fechMod=sdf.format(d);	
		
		
		size=file.length();
		if (sizeInPackage!=null) {
			size=sizeInPackage;
		}
		
		if (pathInPackage==null) {
			pathInPackage="";
		}
		this.pathInPackage = pathInPackage;
		this.hits=hits;
	}
	
	public SearchOptions getSearchOptions() {
		return searchOptions;
	}

	public String getName() {
		return name;
	}
	
	public Long getSize() {
		return size;
	}

	public File getFile() {
		return file;
	}

	public String getFechMod() {
		return fechMod;
	}	
	
	public String getPathInPackage() {
		return pathInPackage;
	}

	public List<Hit> getHits() {
		return hits;
	}

	public InputStream getInputStream() throws Exception {
		if (fileType==FileType.File) {
			FileInputStream fileInputStream=new FileInputStream(file);
			return fileInputStream;
		} 
		if (fileType==FileType.Zip) {
			ZipFile zipFile=new ZipFile(file);
			ZipEntry zipEntry=zipFile.getEntry(pathInPackage);
			InputStream is=zipFile.getInputStream(zipEntry);
			return is;
		}
		if (fileType==FileType.Z7) {
			SevenZFile sevenZFile = new SevenZFile(file);
			/*this way is not working... it doesnt move the sevenZFile.currentEntryIndex
			Iterator<SevenZArchiveEntry> entries=sevenZFile.getEntries().iterator(); 
			while (entries.hasNext()) {
				SevenZArchiveEntry sevenZArchiveEntry=entries.next();
				if (sevenZArchiveEntry.getName().equals(pathInPackage)) {
					InputStream is=new SevenZInputStream(sevenZArchiveEntry,sevenZFile);
					return is;
				}
			    
			}
			*/
			SevenZArchiveEntry sevenZArchiveEntry=null;
			while ((sevenZArchiveEntry =sevenZFile.getNextEntry())!=null) {
				if (sevenZArchiveEntry.getName().equals(pathInPackage)) {
					InputStream is=new SevenZInputStream(sevenZArchiveEntry,sevenZFile);
					return is;
				}
			}
		}
		if (fileType==FileType.Rar) {
			Archive rarFile=new Archive(file);
			List<FileHeader> fileHeaders = rarFile.getFileHeaders();
			for (FileHeader fileHeader:fileHeaders) {
				String path=fileHeader.getFileNameString();
				if (path.equals(pathInPackage)) {
					return rarFile.getInputStream(fileHeader);
				}
			}
		}
		return null;
	}
	
	public String toString() {
		return toString(false);
	}
	
	 public String toString(boolean onlyNames) {
		 String x="";
		 if (onlyNames) {
			 x=x+file.getName();
		 } else {
			 x=x+file;
		 }
		 if (!pathInPackage.equals("")) {
			 x=x+"|"+pathInPackage;
		 }
		 return x;
	 }
	
	 public String toString(boolean onlyNames, boolean showHits,String hitPrefix) {
		 String NL="\r\n";
		 StringBuilder sb=new StringBuilder(this.toString(onlyNames));
		 sb.append(NL);
		 if (showHits && hits!=null) {
			 for (Hit hit:hits) {
				 sb.append(hitPrefix).append(hit).append(NL);
			 }
		 }
		 
		 return sb.toString();
	 }
	 
}
