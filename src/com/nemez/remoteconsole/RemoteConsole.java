package com.nemez.remoteconsole;

import com.nemez.remoteconsole.gui.ConsoleWindow;
import com.redstoner.protected_classes.ConnectionHandler;

public class RemoteConsole
{
	
	public static ConnectionHandler handler;
	
	public static void main(String[] args)
	{
		ConsoleWindow.initialize();
		handler = ConnectionHandler.getInstance("redstoner.com", 9000);
		handler.addListener(new Listener());
		handler.start();
		System.out.println("RemoteConsoleGUI initialized.");
	}
}
