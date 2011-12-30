package http.entity.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.zip.GZIPOutputStream;

import org.apache.http.entity.mime.MIME;
import org.apache.http.entity.mime.content.AbstractContentBody;

public class MimeStringBody extends AbstractContentBody {
	private final byte[] content;
    private final Charset charset;

    /**
     * @throws IOException 
     * @since 4.1
     */
    public static MimeStringBody create(
            final String text,
            final String mimeType,
            final Charset charset) throws IllegalArgumentException, IOException {
        try {
            return new MimeStringBody(text, mimeType, charset);
        } catch (UnsupportedEncodingException ex) {
            throw new IllegalArgumentException("Charset " + charset + " is not supported", ex);
        }
    }

    /**
     * @throws IOException 
     * @since 4.1
     */
    public static MimeStringBody create(
            final String text, final Charset charset) throws IllegalArgumentException, IOException {
        return create(text, null, charset);
    }

    /**
     * @throws IOException 
     * @since 4.1
     */
    public static MimeStringBody create(final String text) throws IllegalArgumentException, IOException {
        return create(text, null, null);
    }

    /**
     * Create a StringBody from the specified text, mime type and character set.
     * 
     * @param text to be used for the body, not {@code null}
     * @param mimeType the mime type, not {@code null}
     * @param charset the character set, may be {@code null}, in which case the US-ASCII charset is used
     * @throws IOException 
     * @throws IllegalArgumentException if the {@code text} parameter is null
     */
    public MimeStringBody(
            final String text,
            final String mimeType,
            Charset charset) throws IOException {
        super(mimeType);
        if (text == null) {
            throw new IllegalArgumentException("Text may not be null");
        }
        if (charset == null) {
            charset = Charset.forName("US-ASCII");
        }
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        GZIPOutputStream gzOut = null;
        try {
            gzOut = new GZIPOutputStream(byteOut);
            gzOut.write(text.getBytes(charset));
            gzOut.close();
            byteOut.close();
            content = byteOut.toByteArray();
        } finally {
            gzOut.close();
            byteOut.close();
        }
        this.charset = charset;
    }

    /**
     * Create a StringBody from the specified text and character set.
     * The mime type is set to "text/plain".
     * 
     * @param text to be used for the body, not {@code null}
     * @param charset the character set, may be {@code null}, in which case the US-ASCII charset is used
     * @throws IOException 
     * @throws IllegalArgumentException if the {@code text} parameter is null
     */
    public MimeStringBody(final String text, final Charset charset) throws IOException {
        this(text, "text/plain", charset);
    }

    /**
     * Create a StringBody from the specified text.
     * The mime type is set to "text/plain".
     * The hosts default charset is used.
     * 
     * @param text to be used for the body, not {@code null}
     * @throws IOException 
     * @throws IllegalArgumentException if the {@code text} parameter is null
     */
    public MimeStringBody(final String text) throws IOException {
        this(text, "text/plain", null);
    }

    public Reader getReader() {
        return new InputStreamReader(
                new ByteArrayInputStream(this.content),
                this.charset);
    }

    /**
     * @deprecated use {@link #writeTo(OutputStream)}
     */
    @Deprecated
    public void writeTo(final OutputStream out, int mode) throws IOException {
        writeTo(out);
    }

    public void writeTo(final OutputStream out) throws IOException {
        if (out == null) {
            throw new IllegalArgumentException("Output stream may not be null");
        }
        InputStream in = new ByteArrayInputStream(this.content);
        byte[] tmp = new byte[4096];
        int l;
        while ((l = in.read(tmp)) != -1) {
            out.write(tmp, 0, l);
        }
        out.flush();
    }

    public String getTransferEncoding() {
        return MIME.ENC_8BIT;
    }

    public String getCharset() {
        return this.charset.name() + "; compressed=true";
    }

    public long getContentLength() {
        return this.content.length;
    }

    public String getFilename() {
        return null;
    }
}
