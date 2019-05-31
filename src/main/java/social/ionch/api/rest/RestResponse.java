package social.ionch.api.rest;

import javax.annotation.Nullable;

public class RestResponse {
	protected String content = null;
	protected Throwable error = null;
	protected int responseCode = -1;
	protected String mimeType = "text/plain";
	
	public RestResponse(int responseCode, String mimeType, String content) {
		this.responseCode = responseCode;
		this.content = content;
	}
	
	public RestResponse(Throwable t) {
		this.error = t;
	}
	
	public RestResponse(int responseCode) {
		this.responseCode = responseCode;
	}
	
	public boolean didError() {
		return error!=null || responseCode < 200 || responseCode >= 300;
	}
	
	public boolean didRespond() {
		return responseCode != -1;
	}
	
	public int getResponseCode() {
		return responseCode;
	}
	
	@Nullable
	public Throwable getError() {
		return error;
	}
	
	@Nullable
	public String getContent() {
		return content;
	}
	
	public String getMimeType() {
		return mimeType;
	}
}
