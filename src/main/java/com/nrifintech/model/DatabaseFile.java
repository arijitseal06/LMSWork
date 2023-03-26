package com.nrifintech.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "file")
public class DatabaseFile {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int fileId;
	
	private String fileName;
	
	private String fileType;
	
	@Lob
	private byte[] data;
	

	public int getFileId() {
		return fileId;
	}

	public void setFileId(int fileId) {
		this.fileId = fileId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public DatabaseFile() {
		super();
		// TODO Auto-generated constructor stub
	}

	public DatabaseFile(int fileId, String fileName, String fileType, byte[] data, Book book) {
		super();
		this.fileId = fileId;
		this.fileName = fileName;
		this.fileType = fileType;
		this.data = data;
	}
	public DatabaseFile(String fileName, String fileType, byte[] data) {
		super();
		this.fileName = fileName;
		this.fileType = fileType;
		this.data = data;
	}

	
}
