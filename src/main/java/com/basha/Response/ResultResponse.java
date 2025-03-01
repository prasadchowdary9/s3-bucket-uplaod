package com.basha.Response;

public class ResultResponse {

	private MetaResponse metaResponse;
	private Object result;

	public MetaResponse getMetaResponse() {
		return metaResponse;
	}

	public void setMetaResponse(MetaResponse metaResponse) {
		this.metaResponse = metaResponse;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

}
