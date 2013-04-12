package pubsub;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class QueryResult implements Serializable {
	
	ArrayList<StreamInfo> streamInfoList;
	HashMap<Integer, Stream> streamMap;

	public QueryResult() {
		super();
		
		streamInfoList = new ArrayList<StreamInfo>();
		streamMap = new HashMap<Integer, Stream>();
	}
	
	public void addStreamInfo(StreamInfo si)
	{
		streamInfoList.add(si);
	}
	
	public void addStream(int streamID, Stream s)
	{
		streamMap.put(streamID, s);
	}

	public ArrayList<StreamInfo> getStreamInfoList() {
		return streamInfoList;
	}

	public HashMap<Integer, Stream> getStreamMap() {
		return streamMap;
	}

	@Override
	public String toString() {
		String str = "QueryResult [";
		
		for(Stream stream : streamMap.values())
		{
			str += "(" + stream.getStreamInfo().streamID + " : " + stream.getStreamInfo().startTime + " - " + stream.getStreamInfo().endTime + "), ";
		}
		
		str += "]";
		
		return str;
	}
	
	
}
