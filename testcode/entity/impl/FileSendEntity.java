package http.entity.impl;

import http.ContentType;
import http.entity.SendEntity;

import java.io.File;

import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;

public class FileSendEntity implements SendEntity {
	private String paraName;
	private ContentBody contenBody;

	public String getParaName() {
		return paraName;
	}

	public void setParaName(String paraName) {
		this.paraName = paraName;
	}
	
	public ContentBody getContenBody() {
		return contenBody;
	}

	public void setContenBody(String path) {
		setContenBody(new File(path));
	}

	public void setContenBody(File file) {
		contenBody = new FileBody(file, ContentType.APPLICATION_FORM_URLENCODED);
	}

}
