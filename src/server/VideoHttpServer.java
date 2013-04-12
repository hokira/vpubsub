package server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

import pubsub.Query;
import pubsub.QueryItem;
import pubsub.QueryResult;
import pubsub.Stream;
import pubsub.StreamElement;
import pubsub.StreamInfo;



public class VideoHttpServer extends HttpServlet {
	/**
	 * Required by the Serializable interface
	 */
	private static final long serialVersionUID = -1838508097138721463L;

	private ConcurrentHashMap<Integer, Stream> streamMap = new ConcurrentHashMap<Integer, Stream>();

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String args[]) throws Exception {
		Server server = new Server(8080);
		Context root = new Context(server, "/", Context.SESSIONS);
		root.addServlet(new ServletHolder(new VideoHttpServer()), "/*");
		server.start();
	}

	private BigInteger lastVideoID = null;

	/**
	 * @throws IOException
	 */
	public VideoHttpServer() throws IOException {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {


	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		System.out.println("dopost");
		
		String mode = req.getParameter("mode");
		ServletInputStream is = req.getInputStream();
		byte[] data = IOUtils.toByteArray(is);
		is.close();

		//post image from publisher
		if(mode.compareTo("postStream") == 0)
		{
			ByteArrayInputStream bis = new ByteArrayInputStream(data);
			ObjectInput in = null;
			try {
				in = new ObjectInputStream(bis);
				StreamElement[] elements = (StreamElement[]) in.readObject(); 

				for(StreamElement se : elements)
				{
					if(streamMap.containsKey(se.getStreamID()) == false)
					{
						streamMap.put(se.getStreamID(), new Stream(se.getStreamID()));
						System.out.println("a new stream has been created: " + se.getStreamID());
					}

					streamMap.get(se.getStreamID()).put(se);

					System.out.println("stream element posted : " + se.toString());				

					saveJpeg(se.getData(), "img_" + se.getStreamID() + "_" + se.getTimestamp());
				}

			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				bis.close();
				in.close();
			}
		}
		//stream request from consumer
		else if(mode.compareTo("getStream") == 0)
		{
			ByteArrayInputStream bis = new ByteArrayInputStream(data);
			ObjectInput in = null;
			try 
			{
				in = new ObjectInputStream(bis);
				Query query = (Query) in.readObject(); 

				System.out.println("query received from a consumer : " + query.toString());

				QueryResult result = new QueryResult();

				//provide metadata
				for(Stream stream : streamMap.values())
				{
					result.addStreamInfo(stream.getStreamInfo());
				}

				//provide requested elements
				for(QueryItem qi : query.getQueryList())
				{
					Stream stream = streamMap.get(qi.streamID);
					
					if(stream != null)
					{
						Stream subStream = stream.get(qi.startTime, qi.endTime);
						result.addStream(qi.streamID, subStream);
					}
				}

				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ObjectOutput out = new ObjectOutputStream(bos);   
				out.writeObject(result);
				data = bos.toByteArray();
				out.close();
				bos.close();

				ServletOutputStream os = resp.getOutputStream();
				resp.setContentType("binary/octet-stream");
				resp.setContentLength(data.length);
				os.write(data);
				os.close();

			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				bis.close();
				in.close();
			}
		}
	}

	void saveJpeg(byte[] image, String filename)
	{
		try {
			FileOutputStream outputStream =	new FileOutputStream(filename + ".jpeg");
			outputStream.write(image);
			outputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}