package org.bcjj.huntfiles.gui;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import org.bcjj.huntfiles.FileInfo;

public class FilesTableModel extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final String NAME="name";
	private static final String PATH="path";
	private static final String INTERNALPATH="internaPath";
	private static final String SIZE="size";
	private static final String DATE="date";
	private static final String HITS="hits";
	
	private String[] columnNames={NAME,PATH,INTERNALPATH,SIZE,DATE,HITS};
	
	Vector<FileInfo> fileInfos=new Vector<FileInfo>();
	JTable tabla;
	
	public FilesTableModel(JTable tabla) {
		this.fileInfos=new Vector<FileInfo>();
		this.tabla=tabla;
	}
	
	public void setFiles(List<FileInfo> files) {
		this.fileInfos=new Vector<FileInfo>(files);
	}
	
	public void addFileInfo(FileInfo f) {
		this.fileInfos.add(f);
	}
	
	public void clearFiles() {
		fileInfos.clear();
	}
	
	@Override
	public int getRowCount() {
		return fileInfos.size();
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		FileInfo fi=fileInfos.get(rowIndex);
		File f=fi.getFile();
		String columna=getColumnName(columnIndex);
		if (columna==NAME) {
			return fi.getName();
		}
		if (columna==PATH) {
			return f.getPath();
		}
		if (columna==INTERNALPATH) {
			return fi.getRutaInPackage();
		}
		if (columna==SIZE) {
			return fi.getSize();
		}
		if (columna==DATE) {
			return fi.getFechMod();
		}
		if (columna==HITS) {
			if (fi.getHits()==null) {
				return 0;
			}
			return fi.getHits().size();
		}
		return "";
	}

	@Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

	@Override
	public Class<?> getColumnClass(int columnIndex) {
	    if (fileInfos.isEmpty()) {
	        return Object.class;
	    }
	    return getValueAt(0, columnIndex).getClass();
	}
	
	public FileInfo getFileInfo(int row) {
		return fileInfos.elementAt(row);
	}
	
}
