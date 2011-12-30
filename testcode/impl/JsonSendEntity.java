package http.entity.impl;

import http.ContentType;
import http.entity.SendEntity;

import java.nio.charset.Charset;

import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.protocol.HTTP;

public class JsonSendEntity implements SendEntity {
     private String paraName;
     private String json;
     private boolean needCompression;
     
	public String getParaName() {
		return paraName;
	}
	public void setParaName(String paraName) {
		this.paraName = paraName;
	}
	public ContentBody getContenBody() {
		ContentBody contentBody = null;
		try {
			if(needCompression)
			{
				contentBody = MimeStringBody.create(json, ContentType.APPLICATION_JSON, Charset.forName(HTTP.UTF_8));
			}
			else
			{
				contentBody = StringBody.create(json, ContentType.APPLICATION_JSON, Charset.forName(HTTP.UTF_8));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}	
		
		return contentBody;
	}
	public void setContenBody(String json) {
		this.json = json;
	}
    
    public String getJsonBody()
    {
    	return json;
    }
	/**
	 * @param needCompression the needCompression to set
	 */
	public void setNeedCompression(boolean needCompression) {
		this.needCompression = needCompression;
	}
	/**
	 * @return the needCompression
	 */
	public boolean isNeedCompression() {
		return needCompression;
	}

}
