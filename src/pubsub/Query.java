package pubsub;

import java.io.Serializable;
import java.util.ArrayList;

public class Query implements Serializable {
	
	ArrayList<QueryItem> queryList;

	public Query() {
		super();
		
		queryList = new ArrayList<QueryItem>();
	}
	
	public void addQueryItem(QueryItem qi)
	{
		queryList.add(qi);
	}

	public ArrayList<QueryItem> getQueryList() {
		return queryList;
	}

	@Override
	public String toString() {
		String str = "Query [";
		
		for(QueryItem qi : queryList)
			str += "(" + qi.streamID + ", " + qi.startTime + " - " + qi.endTime + "), ";
		
		str+= "]";
		return str;
	}
	
	
}
