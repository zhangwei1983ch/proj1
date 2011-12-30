package http.entity.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.http.Header;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.log4j.Logger;

public class StringEntity extends AbstractHttpEntity implements Cloneable {
    
	protected final byte[] content;
	private final static String COMPRESSED = "compressed";
	private boolean needCompression = false;
	final static Logger log = Logger.getLogger(StringEntity.class);
	
	public StringEntity(final String s, String mimeType, String charset, boolean needCompression)
    		throws IOException 
    {
    	super();
    	if (s == null) {
            throw new IllegalArgumentException("Source string may not be null");
        }
        if (mimeType == null) {
            mimeType = HTTP.PLAIN_TEXT_TYPE;
        }
        if (charset == null) {
            charset = HTTP.DEFAULT_CONTENT_CHARSET;
        }
        this.needCompression = needCompression;
//        log.debug("compress String : " + s + " compressed ? " + needCompression);
        if(needCompression)
        {
	        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
	        GZIPOutputStream gzOut = null;
	        try {
	            gzOut = new GZIPOutputStream(byteOut);
	            gzOut.write(s.getBytes(charset));
	            gzOut.close();
	            byteOut.close();
	            content = byteOut.toByteArray();
	        } finally {
	            gzOut.close();
	            byteOut.close();
	        }
//	        
//	        ByteArrayInputStream byteIn = new ByteArrayInputStream(content);
//	        GZIPInputStream gzIn = new GZIPInputStream(byteIn);
//	        byte[] b = new byte[1024];
//	        int len = gzIn.read(b);
//	        log.debug("compressed String : " + new String(b, 0, len));
        }
        else
        {
        	content = s.getBytes(charset);
        }
        setContentType(mimeType + HTTP.CHARSET_PARAM + charset);
    }
    
    public Header getContentType() {
        String ctString = this.contentType.getValue();
        if(needCompression)
        	ctString = ctString + "; " + COMPRESSED + "=true";
        return new BasicHeader(HTTP.CONTENT_TYPE, ctString);
    }
    
    public StringEntity(final String s, String charset, boolean needCompression)
            throws IOException {
        this(s, null, charset, needCompression);
    }

    public boolean isRepeatable() {
        return true;
    }

    public long getContentLength() {
        return this.content.length;
    }

    public InputStream getContent() throws IOException {
        return new ByteArrayInputStream(this.content);
    }

    public void writeTo(final OutputStream outstream) throws IOException {
        if (outstream == null) {
            throw new IllegalArgumentException("Output stream may not be null");
        }
        outstream.write(this.content);
        outstream.flush();
    }

    /**
     * Tells that this entity is not streaming.
     *
     * @return <code>false</code>
     */
    public boolean isStreaming() {
        return false;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
	 * @param needCompression the needCompression to set
	 */
	public void setNeedCompression(boolean needCompression) {
		this.needCompression = needCompression;
	}
}
