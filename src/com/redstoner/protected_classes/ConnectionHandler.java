package com.redstoner.protected_classes;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;

public class ConnectionHandler extends Thread
{
	private Ciphers ciphers;
	private ArrayList<ConnectionListener> listeners = new ArrayList<ConnectionListener>();
	private static ConnectionHandler instance;
	private boolean isRunning;
	private Socket socket;
	private ObjectInputStream objIn;
	private ObjectOutputStream objOut;
	private SecureRandom random;
	
	private ConnectionHandler(String host, int port)
	{
		try
		{
			Ciphers.initRSA();
			ciphers = new Ciphers();
			System.out.println("Opening socket to " + host + ":" + port);
			socket = new Socket(host, port);
			objIn = new ObjectInputStream(socket.getInputStream());
			objOut = new ObjectOutputStream(socket.getOutputStream());
			random = new SecureRandom();
		}
		catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeySpecException
				| ClassNotFoundException | IllegalBlockSizeException | BadPaddingException
				| InvalidAlgorithmParameterException | IOException | URISyntaxException e)
		{
			e.printStackTrace();
		}
	}
	
	public void addListener(ConnectionListener listener)
	{
		listeners.add(listener);
	}
	
	public boolean remove(ConnectionListener listener)
	{
		return listeners.remove(listener);
	}
	
	public static ConnectionHandler getInstance(String host, int port)
	{
		if (instance == null)
			instance = new ConnectionHandler(host, port);
		return instance;
	}
	
	@Override
	public void run()
	{
		isRunning = true;
		int status = 0;
		/* Connection status | Expected message | Answer
		 * ----------------------------------------------------------------------------------------------------------
		 * 0 = Handshake | "USR-CON-BGN" | "SRV-REQ-RSA" (1)
		 * PLAINTEXT | |
		 * ----------------------------------------------------------------------------------------------------------
		 * 1 = RSA integrity check | "xxxxxUSR-RSA-BGNxxxxx" | "SRV-REQ-AES" (2)
		 * RSA ENCRYPTED | |
		 * -----------------------------------------------------------------------------------------------------------------
		 * 2 = AES key exchange | <AES KEY> | "SRV-REQ-USN" (3)
		 * RSA ENCRYPTED | |
		 * -----------------------------------------------------------------------------------------------------------------
		 * 3 = Awaiting username | <xxxxxusernamexxxxx> | If username exists: "SRV-REQ-AUT" (4) or "SRV-REQ-IGA" (14) or "USR-NO-AUT" (6)
		 * AES ENCRYPTED | | Else: "SRV-REQ-USN" (3)
		 * ----------------------------------------------------------------------------------------------------------
		 * 4 = Awaiting authentication | <authentication> | If authentication OK: "SRV-REQ-CMD" (6) or "SRV-REQ-2FA" (5)
		 * AES ENCRYPTED | | Else: "SRV-REQ-AUT" (4)
		 * | | After three wrong attempts: disconnect()
		 * ----------------------------------------------------------------------------------------------------------
		 * 5 = Awaiting 2FA | <authentication> | If authentication OK: "SRV-REQ-CMD" (6)
		 * AES ENCRYPTED | | Else: "SRV-REQ-2FA" (5)
		 * | | After three wrong attempts: disconnect()
		 * ----------------------------------------------------------------------------------------------------------
		 * 14 = Offering IGA | <yes/no> | If IGA was used: "SRV-REQ-CMD" (6)
		 * AES ENCRYPTED | | Else: "SRV-REQ-AUT" (4)
		 * 6 = Authentication successful - awaiting commands. Further communication is AES ENCRYPTED.
		 * "xxxxx" resembles a five character long random sequence that is supposed to be generated through a cryptographic secure algorithm */
		while (isRunning && status != 6)
		{
			switch (status)
			{
				case 0:
					try
					{
						objOut.writeObject("USR-CON-BGN");
						objOut.flush();
						String input = (String) objIn.readObject();
						if (input.equals("SRV-REQ-RSA"))
							status++;
						else
						{
							if (input.startsWith("MSG: "))
								notifyListeners(input);
							else
								notifyListeners("MSG: Unknown input: " + input);
							disconnect("An unexpected error occured in state 0. Disconnecting...");
						}
						break;
					}
					catch (IOException | ClassNotFoundException e)
					{
						notifyListeners("MSG: [0;31;22m[ERROR]: " + e.getMessage());
						disconnect("An unexpected error occured in state 0. Disconnecting...");
					}
					break;
				case 1:
					try
					{
						StringBuilder sb = new StringBuilder();
						for (int i = 0; i < 5; i++)
							sb.append((char) random.nextInt());
						sb.append("USR-RSA-BGN");
						for (int i = 0; i < 5; i++)
							sb.append((char) random.nextInt());
						objOut.writeObject(new SealedObject(sb.toString(), Ciphers.RSA_ENCODE));
						objOut.flush();
						String input = (String) objIn.readObject();
						if (input.equals("SRV-REQ-AES"))
							status++;
						else
						{
							if (input.startsWith("MSG: "))
								notifyListeners(input);
							else
								notifyListeners("MSG: Unknown input: " + input);
							disconnect("An unexpected error occured in state 1. Disconnecting...");
						}
						break;
					}
					catch (IOException | IllegalBlockSizeException | ClassNotFoundException e)
					{
						notifyListeners("MSG: [0;31;22m[ERROR]: " + e.getMessage());
						disconnect("An unexpected error occured in state 1. Disconnecting...");
					}
				case 2:
					try
					{
						ciphers.sendAESKey(objOut);
						String input = (String) ((SealedObject) objIn.readObject())
								.getObject(ciphers.getNextAESDecode());
						if (input.equals("SRV-REQ-USN"))
							status++;
						else
						{
							if (input.startsWith("MSG: "))
								notifyListeners(input);
							else
								notifyListeners("MSG: Unknown input: " + input);
							disconnect("An unexpected error occured in state 2. Disconnecting...");
						}
						break;
					}
					catch (IOException | ClassNotFoundException | IllegalBlockSizeException | InvalidKeyException
							| BadPaddingException | InvalidAlgorithmParameterException | NoSuchAlgorithmException
							| NoSuchPaddingException e)
					{
						notifyListeners("MSG: [0;31;22m[ERROR]: " + e.getMessage());
						disconnect("An unexpected error occured in state 2. Disconnecting...");
					}
				case 3:
					try
					{
						StringBuilder sb = new StringBuilder();
						for (int i = 0; i < 5; i++)
							sb.append((char) random.nextInt());
						sb.append(getUsername());
						for (int i = 0; i < 5; i++)
							sb.append((char) random.nextInt());
						objOut.writeObject(new SealedObject(sb.toString(), ciphers.getNextAESEncode()));
						objOut.flush();
						String input = (String) ((SealedObject) objIn.readObject())
								.getObject(ciphers.getNextAESDecode());
						if (input.equals("SRV-REQ-AUT"))
							status++;
						else if (input.equals("SRV-REQ-IGA"))
							status = 14;
						else if (input.equals("USR-NO-AUT"))
							status = 6;
						else if (input.equals("SRV-REQ-USN"))
							;
						else
						{
							if (input.startsWith("MSG: "))
								notifyListeners(input);
							else
								notifyListeners("MSG: Unknown input: " + input);
							disconnect("An unexpected error occured in state 3. Disconnecting...");
						}
						break;
					}
					catch (IOException | ClassNotFoundException | IllegalBlockSizeException | InvalidKeyException
							| NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException
							| BadPaddingException e)
					{
						e.printStackTrace();
						notifyListeners("MSG: [0;31;22m[ERROR]: " + e.getMessage());
						disconnect("An unexpected error occured in state 3. Disconnecting...");
					}
				case 4:
					try
					{
						String input = (String) ((SealedObject) objIn.readObject())
								.getObject(ciphers.getNextAESDecode());
						if (input.equals("SRV-REQ-PWD"))
							objOut.writeObject(new SealedObject(getPassword(), ciphers.getNextAESEncode()));
						else if (input.equals("SRV-REQ-TKN"))
							objOut.writeObject(new SealedObject(getToken(), ciphers.getNextAESEncode()));
						else if (input.equals("SRV-REQ-IGA"))
						{
							status = 14;
							break;
						}
						else
						{
							if (input.startsWith("MSG: "))
								notifyListeners(input);
							else
								notifyListeners("MSG: Unknown input: " + input);
							disconnect("An unexpected error occured in state 4. Disconnecting...");
						}
						objOut.flush();
						input = (String) ((SealedObject) objIn.readObject()).getObject(ciphers.getNextAESDecode());
						if (input.equals("SRV-REQ-CMD"))
							status = 6;
						else if (input.equals("SRV-REQ-PWO"))
						{
							status = 6;
							objOut.writeObject(new SealedObject(getPWO(), ciphers.getNextAESEncode()));
							objOut.flush();
						}
						else if (input.equals("SRV-REQ-2FA"))
							status++;
						else if (input.equals("SRV-REQ-AUT"))
							;
						else
						{
							if (input.startsWith("MSG: "))
								notifyListeners(input);
							else
								notifyListeners("MSG: Unknown input: " + input);
							disconnect("An unexpected error occured in state 4. Disconnecting...");
						}
						break;
					}
					catch (IOException | ClassNotFoundException | IllegalBlockSizeException | InvalidKeyException
							| NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException
							| BadPaddingException e)
					{
						e.printStackTrace();
						notifyListeners("MSG: [0;31;22m[ERROR]: " + e.getMessage());
						disconnect("An unexpected error occured in state 4. Disconnecting...");
					}
				case 5:
					try
					{
						objOut.writeObject(new SealedObject(new String[] {get2FACode()}, ciphers.getNextAESEncode()));
						objOut.flush();
						String input = (String) ((SealedObject) objIn.readObject())
								.getObject(ciphers.getNextAESDecode());
						if (input.equals("SRV-REQ-CMD"))
							status = 6;
						else if (input.equals("SRV-REQ-2FA"))
							;
						else
						{
							if (input.startsWith("MSG: "))
								notifyListeners(input);
							else
								notifyListeners("MSG: Unknown input: " + input);
							disconnect("An unexpected error occured in state 5. Disconnecting...");
						}
						break;
					}
					catch (IOException | ClassNotFoundException | IllegalBlockSizeException | InvalidKeyException
							| NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException
							| BadPaddingException e)
					{
						notifyListeners("MSG: [0;31;22m[ERROR]: " + e.getMessage());
						disconnect("An unexpected error occured in state 5. Disconnecting...");
					}
				case 14:
					try
					{
						objOut.writeObject(new SealedObject(getIGA(), ciphers.getNextAESEncode()));
						objOut.flush();
						String input = (String) ((SealedObject) objIn.readObject())
								.getObject(ciphers.getNextAESDecode());
						if (input.equals("SRV-REQ-CMD"))
							status = 6;
						else if (input.equals("SRV-REQ-AUT"))
							status = 4;
						else if (input.equals("SRV-REQ-PWO"))
						{
							status = 6;
							objOut.writeObject(new SealedObject(getPWO(), ciphers.getNextAESEncode()));
							objOut.flush();
						}
						else
						{
							if (input.startsWith("MSG: "))
								notifyListeners(input);
							else
								notifyListeners("MSG: Unknown input: " + input);
							disconnect("An unexpected error occured in state 14. Disconnecting...");
						}
						break;
					}
					catch (IOException | ClassNotFoundException | IllegalBlockSizeException | InvalidKeyException
							| NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException
							| BadPaddingException e)
					{
						notifyListeners("MSG: [0;31;22m[ERROR]: " + e.getMessage());
						disconnect("An unexpected error occured in state 14. Disconnecting...");
					}
			}
		}
		notifyListeners("MSG: Awaiting commands...");
		while (isRunning)
		{
			try
			{
				String input = (String) ((SealedObject) objIn.readObject()).getObject(ciphers.getNextAESDecode());
				if (input.equals("SRV-REQ-PWO"))
				{
					objOut.writeObject(new SealedObject(getPWO(), ciphers.getNextAESEncode()));
					objOut.flush();
					continue;
				}
				else if (input.equals("SRV-REQ-PWC"))
				{
					objOut.writeObject(new SealedObject("CMD: cgpass " + getPWC(), ciphers.getNextAESEncode()));
					objOut.flush();
					continue;
				}
				else if (input.equals("SRV-REQ-CMD"))
				{
					notifyListeners("MSG: Awaiting commands...");
				}
				else
					notifyListeners(input);
			}
			catch (InvalidKeyException | ClassNotFoundException | IllegalBlockSizeException | BadPaddingException
					| InvalidAlgorithmParameterException | NoSuchAlgorithmException | NoSuchPaddingException
					| IOException e)
			{
				notifyListeners("MSG: [0;31;22m[ERROR]: " + e.getMessage());
				disconnect("An unexpected error occured. Disconnecting...");
				continue;
			}
		}
	}
	
	private String getPWC()
	{
		StringBuilder compiledString = new StringBuilder();
		String[] raw = listeners.get(0).getPasswordChange();
		for (String s : raw)
		{
			compiledString.append(s);
			compiledString.append(' ');
		}
		return compiledString.toString().trim();
	}
	
	private String[] getPWO()
	{
		return listeners.get(0).getPasswordOverride();
	}
	
	private String getToken()
	{
		return listeners.get(0).getToken();
	}
	
	private String getIGA()
	{
		return listeners.get(0).getIGA() ? "yes" : "no";
	}
	
	private String get2FACode()
	{
		return listeners.get(0).get2FACode();
	}
	
	private String getPassword()
	{
		return listeners.get(0).getPassword();
	}
	
	private String getUsername()
	{
		return listeners.get(0).getUsername();
	}
	
	/** Disconnects the client from the server */
	private synchronized void disconnect(String message)
	{
		notifyListeners("MSG: [0;31;22m[INFO]: " + message + "§r");
		try
		{
			objIn.close();
		}
		catch (IOException e)
		{
			notifyListeners("MSG: [0;31;22m[ERROR]: " + e.getMessage() + "§r");
		}
		try
		{
			objOut.close();
		}
		catch (IOException e)
		{
			notifyListeners("MSG: [0;31;22m[ERROR]: " + e.getMessage() + "§r");
		}
		isRunning = false;
	}
	
	/** Sends a command to the server
	 * 
	 * @param command the command to send */
	public synchronized void sendCommand(String command)
	{
		try
		{
			objOut.writeObject(new SealedObject("MSG: " + command, ciphers.getNextAESEncode()));
		}
		catch (InvalidKeyException | IllegalBlockSizeException | NoSuchAlgorithmException | NoSuchPaddingException
				| InvalidAlgorithmParameterException | IOException e)
		{
			notifyListeners("MSG: [0;31;22m[ERROR]: " + e.getMessage());
		}
	}
	
	/** Sends a command to the server to be interpreted by the plugin itself, instead of executed.
	 * This is for things like exit, cgpass, ...
	 * 
	 * @param command */
	public synchronized void sendLocalCommand(String command)
	{
		try
		{
			objOut.writeObject(new SealedObject("CMD: " + command, ciphers.getNextAESEncode()));
		}
		catch (InvalidKeyException | IllegalBlockSizeException | NoSuchAlgorithmException | NoSuchPaddingException
				| InvalidAlgorithmParameterException | IOException e)
		{
			notifyListeners("MSG: [0;31;22m[ERROR]: " + e.getMessage());
		}
	}
	
	private void notifyListeners(String message)
	{
		if (message.startsWith("CMD: "))
			for (ConnectionListener listener : listeners)
				listener.addCommand(message.replaceFirst("CMD: ", ""));
		else if (message.startsWith("MSG: "))
			for (ConnectionListener listener : listeners)
				listener.addMessage(message.replaceFirst("MSG: ", ""));
		else
			for (ConnectionListener listener : listeners)
				listener.addMessage(message);
	}
}
