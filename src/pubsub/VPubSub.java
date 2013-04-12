package pubsub;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;


public class VPubSub extends Thread{

	String serverURL;
    private Queue<StreamElement> sendQueue = new LinkedList<StreamElement>();
	
	public VPubSub(String serverURL)
	{
		this.serverURL = serverURL;
		this.start();
	}
	
	public synchronized void put(StreamElement element)
	{
		sendQueue.add(element);
    	notifyAll();
	}
	
	public QueryResult get(Query query) throws ClientProtocolException, IOException {

		String url = "http://" + serverURL + ":8080?mode=getStream";

		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(url);
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = new ObjectOutputStream(bos);   
		out.writeObject(query);
		byte[] data = bos.toByteArray();
		out.close();
		bos.close();

		ByteArrayEntity byteEntity = new ByteArrayEntity(data);
		byteEntity.setContentType("binary/octet-stream");
		byteEntity.setChunked(true);
		httppost.setEntity(byteEntity);

		ResponseHandler<byte[]> handler = new ResponseHandler<byte[]>() {
		    public byte[] handleResponse(
		            HttpResponse response) throws ClientProtocolException, IOException {
		        HttpEntity entity = response.getEntity();
		        if (entity != null) {
		            return EntityUtils.toByteArray(entity);
		        } else {
		            return null;
		        }
		    }
		};
		
		data = httpclient.execute(httppost, handler);

		ByteArrayInputStream bis = new ByteArrayInputStream(data);
		ObjectInput in = null;
		QueryResult qr = null;
		try {
			in = new ObjectInputStream(bis);
			qr = (QueryResult) in.readObject();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			bis.close();
			in.close();
		}
		
//		httppost.releaseConnection();
//		httpclient.getConnectionManager().shutdown();
		
		return qr;
	}
	
	private synchronized ArrayList<StreamElement> popElements()
    {
    	if(sendQueue.isEmpty())
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	
    	ArrayList<StreamElement> ret = new ArrayList<StreamElement>();
    	
    	while(!sendQueue.isEmpty())
    	{
    		ret.add(sendQueue.remove());
    	}
    	
    	return ret;
    }
    
    @Override
	public void run() 
    {
		while(true)
		{
			ArrayList<StreamElement> elementArray = popElements();

			try {
				StreamElement[] elements = elementArray.toArray(new StreamElement[1]);
				postStream(elements);

			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void postStream(StreamElement[] elements) throws ClientProtocolException, IOException {

		
		String url = "http://" + serverURL + ":8080?mode=postStream";


		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(url);
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = new ObjectOutputStream(bos);   
		out.writeObject(elements);
		byte[] data = bos.toByteArray();
		out.close();
		bos.close();

		ByteArrayEntity byteEntity = new ByteArrayEntity(data);
		byteEntity.setContentType("binary/octet-stream");
		byteEntity.setChunked(true);
		httppost.setEntity(byteEntity);

		HttpResponse resp = httpclient.execute(httppost);
		
		int code = resp.getStatusLine().getStatusCode();
//		System.out.println("return code: " + code);
		
		System.out.println(elements.length + " elements are posted");
		
//		httppost.releaseConnection();
	}

}
