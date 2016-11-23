package com.redstoner.protected_classes;

public interface ConnectionListener
{
	/**
	 * @return the username
	 */
	public String getUsername();
	
	/**
	 * @return an array of string: [newPassword, passwordConfirmation]
	 */
	public String[] getPasswordOverride();
	
	/**
	 * @return an array of string: [currentPassword, newPassword, passwordConfirmation]
	 */
	public String[] getPasswordChange();
	
	/**
	 * @return the user's password
	 */
	public String getPassword();
	
	/**
	 * @return the authentication token
	 */
	public String getToken();
	
	/**
	 * Will be invoked when a new message was sent
	 * 
	 * @param message the message with escape color codes
	 */
	public void addMessage(String message);
	
	/**
	 * Will be invoked when a command answer is coming back
	 * 
	 * @param message the message with escaped color codes
	 */
	public void addCommand(String message);
	
	/**
	 * Will be invoked to request the 2FA code
	 * 
	 * @return the 2FA code provided by the user
	 */
	public String get2FACode();
	
	/**
	 * Will be invoked if the user is offered ingame authentication
	 * 
	 * @return true if IGA is supposed to be used, false else
	 */
	public boolean getIGA();
}
