package client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.math.BigInteger;

import javax.servlet.ServletInputStream;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import pubsub.Query;
import pubsub.QueryItem;
import pubsub.QueryResult;
import pubsub.StreamElement;
import pubsub.VPubSub;



public class VideoSubscriber {
	
	String serverURL;
	int id;
	
	public VideoSubscriber(String serverURL, int id)
	{
		this.serverURL = serverURL;
		this.id = id;
	}
	
	public static void main(String args[]) throws IOException, InterruptedException
	{
		VPubSub pubsub = new VPubSub("localhost");
		
		for(int i=0;i<1000;i++)
		{

			Query query = new Query();
			
			long current = System.currentTimeMillis();
			
			query.addQueryItem(new QueryItem(0,current - 3000,current));
			
			QueryResult result = pubsub.get(query);

			System.out.println("query result: " + result.toString());

			Thread.sleep(100);
		}
	}
	
}
