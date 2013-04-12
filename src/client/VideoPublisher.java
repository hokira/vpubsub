package client;

import java.io.IOException;
import java.util.Random;


import pubsub.StreamElement;
import pubsub.VPubSub;



public class VideoPublisher {

	String serverURL;
	int streamID;
	
	public VideoPublisher(String serverURL, int id)
	{
		this.serverURL = serverURL;
		this.streamID = id;
	}
	
	public static void main(String args[]) throws IOException, InterruptedException {

		VPubSub vp = new VPubSub("localhost");
		
		Random generator = new Random();

		for(int i=0;i<1000;i++)
		{
			int size = generator.nextInt(1024) + 1024;
			byte[] image = new byte[size];
			
			double x = 2;
			double y = 2;
			long ts = System.currentTimeMillis();

			StreamElement se = new StreamElement(0, ts, x, y, image);
			vp.put(se);
			
			System.out.println("image posted w/ size: " + size);
			Thread.sleep(1000);
		}
	}
	
}