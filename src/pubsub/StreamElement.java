package pubsub;

import java.io.Serializable;
import java.math.BigInteger;

public class StreamElement implements Serializable {
	int streamID;
	long timestamp;
	double x;
	double y;
	byte[] data;

	public StreamElement(int streamID, long timestamp, double x, double y, byte[] data) {
		super();
		this.streamID = streamID;
		this.timestamp = timestamp;
		this.x = x;
		this.y = y;
		this.data = data;
	}

	@Override
	public String toString() {
		return "StreamElement [streamID=" + streamID + ", timestamp=" + timestamp + ", x=" + x + ", y=" + y + ", size=" + data.length + "]";
	}


	public int getStreamID() {
		return streamID;
	}


	public void setStreamID(int streamID) {
		this.streamID = streamID;
	}

	public long getTimestamp() {
		return timestamp;
	}


	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}


	public double getX() {
		return x;
	}


	public void setX(double x) {
		this.x = x;
	}


	public double getY() {
		return y;
	}


	public void setY(double y) {
		this.y = y;
	}


	public byte[] getData() {
		return data;
	}


	public void setData(byte[] data) {
		this.data = data;
	}

}
