package com.nemez.remoteconsole;

import com.nemez.remoteconsole.gui.ConsoleWindow;
import com.redstoner.protected_classes.ConnectionHandler;

public class RemoteConsole
{
	public static ConnectionHandler handler;
	
	public static void main(String[] args)
	{
		ConsoleWindow.initialize();
		String ip = "redstoner.com";
		int port = 9000;
		String[] params;
		if (args.length == 1)
			params = args[0].split(":");
		else
			params = args;
		if (params.length == 2)
		{
			ip = params[0];
			try
			{
				port = Integer.parseInt(params[1]);
			}
			catch (NumberFormatException e)
			{
				ip = "redstoner.com";
				port = 9000;
			}
		}
		handler = ConnectionHandler.getInstance(ip, port);
		handler.addListener(new Listener());
		handler.start();
		System.out.println("RemoteConsoleGUI initialized.");
	}
}
