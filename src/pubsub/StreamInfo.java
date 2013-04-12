package pubsub;

import java.io.Serializable;

public class StreamInfo implements Serializable{
	public int streamID;
	public int length;
	public long startTime;
	public long endTime;
	public double left;
	public double top;
	public double right;
	public double bottom;

	public StreamInfo(int streamID, int length, long startTime, long endTime,
			double left, double top, double right, double bottom) {
		super();
		this.length = length;
		this.streamID = streamID;
		this.startTime = startTime;
		this.endTime = endTime;
		this.left = left;
		this.top = top;
		this.right = right;
		this.bottom = bottom;
	}
	

}
