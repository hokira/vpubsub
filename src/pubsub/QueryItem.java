package pubsub;

import java.io.Serializable;

public class QueryItem implements Serializable{
	public int streamID;
	public long startTime;
	public long endTime;
	public QueryItem(int streamID, long startTime, long endTime) {
		super();
		this.streamID = streamID;
		this.startTime = startTime;
		this.endTime = endTime;
	}
	@Override
	public String toString() {
		return "QueryItem [streamID=" + streamID + ", startTime=" + startTime
				+ ", endTime=" + endTime + "]";
	}

}