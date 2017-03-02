import java.net.*;
import java.util.*;
import java.io.*;
import java.text.*;

public class SlaveBot
{
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
			// input format example = -h localhost -p 9999
			if(dataArray.length == 4)
			{
				ip = dataArray[1];
				port = Integer.parseInt(dataArray[3]);
			}
			//input format example = -h localhost 9999
			if(dataArray.length == 3)
			{
				ip = dataArray[1];
				port = Integer.parseInt(dataArray[2]);
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
}
