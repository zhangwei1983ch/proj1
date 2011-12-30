package simple;

import static org.junit.Assert.assertEquals;
import http.HttpMethod;
import http.entity.SendEntity;
import http.entity.impl.FileSendEntity;
import http.entity.impl.JsonSendEntity;

import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.Vector;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;

import resource.ConnectionResource;

import com.loocha.bean.ServerResponse;
import com.loocha.bean.User;

import config.EnvConfig;

public class ServerClient {

	private String PROTOCOL;
	private String HOST;
	private int PORT;
	private String requestedURL;
	private List<SendEntity> sendEntities;
	private List<NameValuePair> qparams;
	private User user = null;
	private boolean isFormFormat;
	private static ObjectMapper om = new ObjectMapper();
	
	public ServerClient() {
		EnvConfig config = EnvConfig.getInstance();
		PROTOCOL = config.PROTOCOL;
		HOST = config.HOST;
		PORT = config.PORT;
		
//		HOST = "61.147.75.250";
//		PROTOCOL = "https";
//		PORT = 8443;
		
		sendEntities = new Vector<SendEntity>();
		qparams = new Vector<NameValuePair>();
		isFormFormat = false;
	}

	public void newResource(String s) {
		newResource(s, null);
	}

	public void setFormFormat() {
		this.isFormFormat = true;
	}

	public void newResource(String s, User user) {
		isFormFormat = false;
		requestedURL = s;
		this.user = user;
		sendEntities.clear();
		qparams.clear();
	}

	public void addParameters(String parameterName, Object object, int type) {
		SendEntity sendEntity = null;
		if (type == Parameter.FILE) {
			sendEntity = new FileSendEntity();
			sendEntity.setParaName(parameterName);
			((FileSendEntity) sendEntity).setContenBody((File) object);
		} else if (type == Parameter.JSON) {
			sendEntity = new JsonSendEntity();
			sendEntity.setParaName(parameterName);

			try {
				sendEntity.setContenBody(om.writeValueAsString(object));				
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(requestedURL);
			}
		} else if (type == Parameter.NULL) {
			qparams.add(new BasicNameValuePair(parameterName, String
					.valueOf(object)));
		}

		if (sendEntity != null)
			sendEntities.add(sendEntity);
	}

	public Integer getStatus() {
		return this.getHttpStatus(HttpMethod.GET);
	}

	public Integer postStatus() {
		return this.getHttpStatus(HttpMethod.POST);
	}

	public Integer putStatus() {
		return getHttpStatus(HttpMethod.PUT);
	}

	public Integer deleteStatus() {
		return getHttpStatus(HttpMethod.DELETE);
	}

	private int getHttpStatus(HttpMethod method) {
		ServerResponse sr;
		try {
			sr = getServerResponse(method);
		} catch (Exception e) {
			ConnectionResource.addFaildCount();
			return -1;
		}
		return Integer.parseInt(sr.getStatus());
	}

	private ServerResponse getServerResponse(HttpMethod method) throws Exception
	{
		String str = getResponse(method);
		ServerResponse sr = om.readValue(str, ServerResponse.class);
		return sr;
	}
	
	private String getResponse(HttpMethod method) throws Exception
	{
		HttpResponse response = null;
		URI uri = URIUtils.createURI(
					PROTOCOL, HOST, PORT, "/" + (EnvConfig.getInstance().needLoocha? "loocha/":"") + requestedURL,
					qparams.size() > 0 ? URLEncodedUtils.format(qparams,
							"UTF-8") : null, null);
		HttpUriRequest httpUriRequest;
		if (isFormFormat) {
			httpUriRequest = method.getHttpFormRequestBase(uri, sendEntities);
		} else {
			httpUriRequest = method.getHttpRequestBase(uri, sendEntities);
		}

		HttpClient httpClient = ConnectionResource.getInstance().getConnection();

		if (user != null) {
			setCredProvider((DefaultHttpClient) httpClient);
		}
		else
		{
			((DefaultHttpClient)httpClient).setCredentialsProvider(null);
		}
		
		response = httpClient.execute(httpUriRequest);
		
		int status = response.getStatusLine().getStatusCode();
		ConnectionResource.getInstance().returnConnection(httpClient);
		
		assertEquals(HttpStatus.SC_OK, status);
		return EntityUtils.toString(response.getEntity(), "UTF-8");
	}

	private void setCredProvider(DefaultHttpClient httpClient2) {
		CredentialsProvider credProvider = new BasicCredentialsProvider();
		credProvider.setCredentials(
				new AuthScope(HOST, PORT),
				new UsernamePasswordCredentials(user.getMobile(), user
						.getPassword()));
		httpClient2.setCredentialsProvider(credProvider);
	}
}
