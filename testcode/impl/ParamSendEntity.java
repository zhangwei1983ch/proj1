package http.entity.impl;

import http.entity.SendEntity;

public class ParamSendEntity implements SendEntity {
     private String paraName;
     private String contenBody;
	public String getParaName() {
		return paraName;
	}
	public void setParaName(String paraName) {
		this.paraName = paraName;
	}
	public String getContenBody() {
		return contenBody;
	}
	public void setContenBody(String value) {
		contenBody=value;
	}
}
