package com.nemez.remoteconsole.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JPasswordField;

public class PasswordDialog extends JDialog {
	private JPasswordField passwordField;
	public String result;
	public volatile boolean exited;

	/**
	 * Create the dialog.
	 */
	public PasswordDialog() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				result = null;
				exited = true;
			}
		});
		setTitle("Password");
		setBounds(100, 100, 450, 119);
		getContentPane().setLayout(new BorderLayout());
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						result = "";
						for (char c : passwordField.getPassword()) {
							result += c;
						}
						dispose();
						exited = true;
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						result = null;
						dispose();
						exited = true;
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		{
			JPanel panel = new JPanel();
			getContentPane().add(panel, BorderLayout.CENTER);
			GridBagLayout gbl_panel = new GridBagLayout();
			gbl_panel.columnWidths = new int[]{0, 61, 0, 0};
			gbl_panel.rowHeights = new int[]{48, 0};
			gbl_panel.columnWeights = new double[]{0.0, 0.0, 1.0, Double.MIN_VALUE};
			gbl_panel.rowWeights = new double[]{0.0, Double.MIN_VALUE};
			panel.setLayout(gbl_panel);
			{
				JLabel lblUsername = new JLabel("Password:");
				GridBagConstraints gbc_lblUsername = new GridBagConstraints();
				gbc_lblUsername.gridwidth = 2;
				gbc_lblUsername.insets = new Insets(0, 0, 0, 5);
				gbc_lblUsername.gridx = 0;
				gbc_lblUsername.gridy = 0;
				panel.add(lblUsername, gbc_lblUsername);
			}
			{
				passwordField = new JPasswordField();
				passwordField.addKeyListener(new KeyAdapter() {
					public void keyPressed(KeyEvent arg0) {
						if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
							result = "";
							for (char c : passwordField.getPassword()) {
								result += c;
							}
							dispose();
							exited = true;
						}
					}
				});
				GridBagConstraints gbc_passwordField = new GridBagConstraints();
				gbc_passwordField.fill = GridBagConstraints.HORIZONTAL;
				gbc_passwordField.gridx = 2;
				gbc_passwordField.gridy = 0;
				panel.add(passwordField, gbc_passwordField);
			}
		}
	}

}
