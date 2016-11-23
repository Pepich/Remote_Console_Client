package com.nemez.remoteconsole;

import javax.swing.JFrame;

import com.nemez.remoteconsole.gui.ChangePasswordDialog;
import com.nemez.remoteconsole.gui.CodeDialog;
import com.nemez.remoteconsole.gui.ConsoleWindow;
import com.nemez.remoteconsole.gui.IGADialog;
import com.nemez.remoteconsole.gui.OverridePasswordDialog;
import com.nemez.remoteconsole.gui.PasswordDialog;
import com.nemez.remoteconsole.gui.TokenDialog;
import com.nemez.remoteconsole.gui.UsernameDialog;
import com.redstoner.protected_classes.ConnectionListener;

public class Listener implements ConnectionListener {

	@Override
	public String getUsername() {
		UsernameDialog dialog = new UsernameDialog();
		dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		dialog.setLocationRelativeTo(ConsoleWindow.frame);
		dialog.setVisible(true);
		while (!dialog.exited) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
			}
		}
		return dialog.result;
	}

	@Override
	public String[] getPasswordOverride() {
		OverridePasswordDialog dialog = new OverridePasswordDialog();
		dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		dialog.setLocationRelativeTo(ConsoleWindow.frame);
		dialog.setVisible(true);
		while (!dialog.exited) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
			}
		}
		return dialog.result;
	}

	@Override
	public String[] getPasswordChange() {
		ChangePasswordDialog dialog = new ChangePasswordDialog();
		dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		dialog.setLocationRelativeTo(ConsoleWindow.frame);
		dialog.setVisible(true);
		while (!dialog.exited) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
			}
		}
		return dialog.result;
	}

	@Override
	public String getPassword() {
		PasswordDialog dialog = new PasswordDialog();
		dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		dialog.setLocationRelativeTo(ConsoleWindow.frame);
		dialog.setVisible(true);
		while (!dialog.exited) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
			}
		}
		return dialog.result;
	}

	@Override
	public String getToken() {
		TokenDialog dialog = new TokenDialog();
		dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		dialog.setLocationRelativeTo(ConsoleWindow.frame);
		dialog.setVisible(true);
		while (!dialog.exited) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
			}
		}
		return dialog.result;
	}

	@Override
	public String get2FACode() {
		CodeDialog dialog = new CodeDialog();
		dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		dialog.setLocationRelativeTo(ConsoleWindow.frame);
		dialog.setVisible(true);
		while (!dialog.exited) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
			}
		}
		return dialog.result;
	}

	@Override
	public boolean getIGA() {
		IGADialog dialog = new IGADialog();
		dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		dialog.setLocationRelativeTo(ConsoleWindow.frame);
		dialog.setVisible(true);
		while (!dialog.exited) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
			}
		}
		return dialog.result;
	}

	@Override
	public void addMessage(String message) {
		ConsoleWindow.writeLine(message);
	}

	@Override
	public void addCommand(String message) {
		ConsoleWindow.writeLine(message);
	}
}
