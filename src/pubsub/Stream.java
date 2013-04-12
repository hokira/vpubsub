package pubsub;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;

public class Stream implements Serializable{
	private StreamInfo info;
	private LinkedList<StreamElement> buffer = new LinkedList<StreamElement>();
	
	public Stream(int streamID)
	{
		info = new StreamInfo(streamID, 0, -1, -1, 0, 0, 0, 0);
	}
	
	public StreamInfo getStreamInfo()
	{
		return info;
	}
	
	public synchronized void put(StreamElement se)
	{
		if(info.startTime == -1 && info.endTime == -1)
		{
			info.startTime = se.timestamp;
			info.left = info.right = se.x;
			info.top = info.bottom = se.y;
		}
		
		if(info.startTime > se.timestamp)
			info.startTime = se.timestamp;
		
		if(info.endTime < se.timestamp)
			info.endTime = se.timestamp;
		
		if(info.left > se.x)
			info.left = se.x;
		
		if(info.right < se.x)
			info.right = se.x;
		
		if(info.top > se.y)
			info.top = se.y;
		
		if(info.bottom < se.y)
			info.bottom = se.y;
		
		//physical time based ordering
		if(info.startTime < se.timestamp && info.endTime > se.timestamp)
		{
			for(int i=buffer.size()-1; i>0; i--)
			{
				if(buffer.get(i).getTimestamp() < se.getTimestamp())
				{
					buffer.add(i,se);
					break;
				}
			}
		}
		else
			buffer.addLast(se);
		
		info.length = buffer.size();
	}
	
	public synchronized Stream get(long startTime, long endTime)
	{
		Stream subStream = new Stream(info.streamID);
		
		for(int i=0;i<buffer.size();i++)
		{
			if(buffer.get(i).getTimestamp() >= startTime && buffer.get(i).getTimestamp() < endTime)
				subStream.put(buffer.get(i));
		}
		
		return subStream;
	}
	
	public synchronized int size()
	{
		return buffer.size();
	}

	public LinkedList<StreamElement> getBuffer() {
		return buffer;
	}
}
