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
import javax.swing.JPasswordField;
import javax.swing.border.EmptyBorder;

public class OverridePasswordDialog extends JDialog {
	private JPasswordField passwordField;
	private JPasswordField passwordField_1;
	public String[] result;
	public volatile boolean exited;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			OverridePasswordDialog dialog = new OverridePasswordDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public OverridePasswordDialog() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				result = null;
				exited = true;
			}
		});
		setTitle("Override Password");
		setBounds(100, 100, 450, 144);
		getContentPane().setLayout(new BorderLayout());
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						result = new String[2];
						String temp = "";
						for (char c : passwordField.getPassword()) {
							temp += c;
						}
						result[0] = temp;
						temp = "";
						for (char c : passwordField_1.getPassword()) {
							temp += c;
						}
						result[1] = temp;
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
					public void actionPerformed(ActionEvent e) {
						result = new String[] {null, null};
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
			gbl_panel.columnWidths = new int[]{43, 104, 29, 244, 0};
			gbl_panel.rowHeights = new int[]{41, 35, 0};
			gbl_panel.columnWeights = new double[]{0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
			gbl_panel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
			panel.setLayout(gbl_panel);
			{
				JLabel lblUsername = new JLabel("New password:");
				GridBagConstraints gbc_lblUsername = new GridBagConstraints();
				gbc_lblUsername.gridwidth = 2;
				gbc_lblUsername.insets = new Insets(0, 0, 5, 5);
				gbc_lblUsername.gridx = 0;
				gbc_lblUsername.gridy = 0;
				panel.add(lblUsername, gbc_lblUsername);
			}
			{
				passwordField = new JPasswordField();
				GridBagConstraints gbc_passwordField = new GridBagConstraints();
				gbc_passwordField.insets = new Insets(0, 0, 5, 0);
				gbc_passwordField.fill = GridBagConstraints.HORIZONTAL;
				gbc_passwordField.gridx = 3;
				gbc_passwordField.gridy = 0;
				panel.add(passwordField, gbc_passwordField);
			}
			{
				JLabel lblConfirmPassword = new JLabel("Confirm password:");
				GridBagConstraints gbc_lblConfirmPassword = new GridBagConstraints();
				gbc_lblConfirmPassword.gridwidth = 2;
				gbc_lblConfirmPassword.insets = new Insets(0, 0, 0, 5);
				gbc_lblConfirmPassword.gridx = 0;
				gbc_lblConfirmPassword.gridy = 1;
				panel.add(lblConfirmPassword, gbc_lblConfirmPassword);
			}
			{
				passwordField_1 = new JPasswordField();
				passwordField_1.addKeyListener(new KeyAdapter() {
					@Override
					public void keyPressed(KeyEvent arg0) {
						if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
							result = new String[2];
							String temp = "";
							for (char c : passwordField.getPassword()) {
								temp += c;
							}
							result[0] = temp;
							temp = "";
							for (char c : passwordField_1.getPassword()) {
								temp += c;
							}
							result[1] = temp;
							dispose();
							exited = true;
						}
					}
				});
				GridBagConstraints gbc_passwordField_1 = new GridBagConstraints();
				gbc_passwordField_1.fill = GridBagConstraints.HORIZONTAL;
				gbc_passwordField_1.gridx = 3;
				gbc_passwordField_1.gridy = 1;
				panel.add(passwordField_1, gbc_passwordField_1);
			}
		}
	}

}
