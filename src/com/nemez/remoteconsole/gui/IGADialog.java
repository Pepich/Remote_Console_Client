package com.nemez.remoteconsole.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class IGADialog extends JDialog {
	public boolean result;
	public volatile boolean exited;

	/**
	 * Create the dialog.
	 */
	public IGADialog() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				result = false;
				exited = true;
			}
		});
		setTitle("Username");
		setBounds(100, 100, 448, 108);
		getContentPane().setLayout(new BorderLayout());
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("Yes");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						result = true;
						dispose();
						exited = true;
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("No");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						result = false;
						dispose();
						exited = true;
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		{
			JLabel lblUseIngameAuthentication = new JLabel("Use In-Game authentication?");
			lblUseIngameAuthentication.setHorizontalAlignment(SwingConstants.CENTER);
			lblUseIngameAuthentication.setHorizontalTextPosition(SwingConstants.CENTER);
			getContentPane().add(lblUseIngameAuthentication, BorderLayout.CENTER);
		}
	}
}
