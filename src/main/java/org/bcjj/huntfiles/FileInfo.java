package org.bcjj.huntfiles;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.lang3.StringUtils;

public class FileInfo {

	public enum FileType {
		File,Zip	
	};
	
	File file;
	String name;
	String rutaInPackage;
	String fechMod; 
	Long size;
	FileType fileType;
	
	List<Hit> hits=null;
	public FileInfo(File file, String rutaInPackage, List<Hit> hits, FileType fileType) {
		super();
		this.file = file;
		this.fileType=fileType;
		
		if (StringUtils.isBlank(rutaInPackage)) {
			name=file.getName();
		} else {
			String x=rutaInPackage;
			x=StringUtils.replace(x,"\\","/");
			int ult=x.lastIndexOf("/");
			if (ult>-1) {
				name=x.substring(ult+1)+" : ("+file.getName()+")";
			} else {
				name=x+" : ("+file.getName()+")";
			}
		}
		
		Date d=new Date(file.lastModified());
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		fechMod=sdf.format(d);	
		
		size=file.length();
		
		if (rutaInPackage==null) {
			rutaInPackage="";
		}
		this.rutaInPackage = rutaInPackage;
		this.hits=hits;
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
	
	public String getRutaInPackage() {
		return rutaInPackage;
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
			ZipEntry zipEntry=zipFile.getEntry(rutaInPackage);
			InputStream is=zipFile.getInputStream(zipEntry);
			return is;
		}
		
		return null;
	}
	
	 public String toString() {
		 String x=""+file;
		 if (!rutaInPackage.equals("")) {
			 x=x+"|"+rutaInPackage;
		 }
		 return x;
	 }
	
	 public String toString(boolean showHits,String hitPrefix) {
		 String NL="\r\n";
		 StringBuilder sb=new StringBuilder(this.toString());
		 sb.append(NL);
		 if (showHits && hits!=null) {
			 for (Hit hit:hits) {
				 sb.append(hitPrefix).append(hit).append(NL);
			 }
		 }
		 
		 return sb.toString();
	 }
	 
}
