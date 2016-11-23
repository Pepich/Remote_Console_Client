package com.nemez.remoteconsole.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import com.nemez.remoteconsole.RemoteConsole;

public class ConsoleWindow
{
	
	public static JFrame frame;
	private static JScrollPane scrollPane;
	private static JTextPane textPane;
	private static JTextField textField;
	private static JMenu mnSendCommand;
	private static JMenuItem mntmExit;
	private static JMenuItem mntmChangePassword;
	private static JMenuItem mntmTestdafuqIs;
	private static JMenuItem mntmHelp;
	private static JMenu mnFactorAuthentication;
	private static JMenuItem mntmEnable;
	private static JMenuItem mntmDisabl;
	private static StyleContext sc = StyleContext.getDefaultStyleContext();
	private static StyledDocument doc;
	private static JMenuItem mntmResendSecretCode;
	
	public static void initialize()
	{
		frame = new JFrame();
		frame.setTitle("Remote Console");
		frame.getContentPane().setBackground(Color.DARK_GRAY);
		frame.setBackground(Color.DARK_GRAY);
		frame.setBounds(100, 100, 638, 432);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.setBackground(Color.GRAY);
		frame.setJMenuBar(menuBar);
		
		mnSendCommand = new JMenu("Send Command");
		menuBar.add(mnSendCommand);
		
		mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				RemoteConsole.handler.sendLocalCommand("exit");
			}
		});
		mnSendCommand.add(mntmExit);
		
		mntmChangePassword = new JMenuItem("Change password");
		mntmChangePassword.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				RemoteConsole.handler.sendLocalCommand("cgpass");
			}
		});
		mnSendCommand.add(mntmChangePassword);
		
		mntmTestdafuqIs = new JMenuItem("Test (Pepe's little friend)");
		mntmTestdafuqIs.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				RemoteConsole.handler.sendLocalCommand("test");
			}
		});
		mnSendCommand.add(mntmTestdafuqIs);
		
		mnFactorAuthentication = new JMenu("2 Factor Authentication");
		mnSendCommand.add(mnFactorAuthentication);
		
		mntmEnable = new JMenuItem("Enable");
		mntmEnable.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				RemoteConsole.handler.sendLocalCommand("enable2FA");
			}
		});
		mnFactorAuthentication.add(mntmEnable);
		
		mntmDisabl = new JMenuItem("Disable");
		mntmDisabl.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				RemoteConsole.handler.sendLocalCommand("disable2FA");
			}
		});
		mnFactorAuthentication.add(mntmDisabl);
		
		mntmResendSecretCode = new JMenuItem("Re-send secret code");
		mntmResendSecretCode.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				RemoteConsole.handler.sendLocalCommand("get2FAsecret");
			}
		});
		mnFactorAuthentication.add(mntmResendSecretCode);
		
		mntmHelp = new JMenuItem("Help");
		mntmHelp.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				RemoteConsole.handler.sendLocalCommand("help");
			}
		});
		mnSendCommand.add(mntmHelp);
		frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
		
		scrollPane = new JScrollPane();
		scrollPane.setAutoscrolls(true);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		frame.getContentPane().add(scrollPane);
		
		textPane = new JTextPane();
		textPane.setForeground(Color.WHITE);
		textPane.setBackground(Color.DARK_GRAY);
		textPane.setEditable(false);
		scrollPane.setViewportView(textPane);
		
		textField = new JTextField();
		textField.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent arg0)
			{
				if (arg0.getKeyCode() == KeyEvent.VK_ENTER && textField.getText().length() > 0)
				{
					RemoteConsole.handler.sendCommand(textField.getText());
					textField.setText("");
				}
			}
		});
		textField.setForeground(Color.WHITE);
		textField.setBackground(Color.DARK_GRAY);
		textField.setMaximumSize(new Dimension(2147483647, 24));
		frame.getContentPane().add(textField);
		textField.setColumns(10);
		doc = textPane.getStyledDocument();
		
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	public static void writeLine(String s)
	{
		if (s == null) return;
		if (s.startsWith("Enabled auto read") || s.startsWith("Disabled auto read"))
		{
			return;
		}
		else if (s.startsWith("Set listener of net.minecraft.server.v1_8_R3.NetworkManager"))
		{
			return;
		}
		System.out.println(s);
		try
		{
			s = (s.endsWith("\n") ? (s.startsWith(">\r[") ? s.substring(1) : s) : s + "\n");
			Color color = new Color(0xEEEEEC);
			boolean italics = false, underline = false, bold = false, strikethrough = false, magic = false;
			String buffer = "";
			
			for (char c : s.toCharArray())
			{
				buffer += c;
				if (buffer.endsWith("[0;30;22m") || buffer.endsWith("§0"))
				{
					if (buffer.charAt(buffer.length() - 2) == '§')
						addWord(buffer.substring(0, buffer.length() - 2), color, italics, strikethrough, underline,
								bold, magic);
					else
						addWord(buffer.substring(0, buffer.length() - 10), color, italics, strikethrough, underline,
								bold, magic);
					color = new Color(0x2E3335); // 0
					italics = false;
					strikethrough = false;
					underline = false;
					bold = false;
					magic = false;
					buffer = "";
				}
				else if (buffer.endsWith("[0;34;22m") || buffer.endsWith("§1"))
				{
					if (buffer.charAt(buffer.length() - 2) == '§')
						addWord(buffer.substring(0, buffer.length() - 2), color, italics, strikethrough, underline,
								bold, magic);
					else
						addWord(buffer.substring(0, buffer.length() - 10), color, italics, strikethrough, underline,
								bold, magic);
					color = new Color(0x3465A4); // 1
					italics = false;
					strikethrough = false;
					underline = false;
					bold = false;
					magic = false;
					buffer = "";
				}
				else if (buffer.endsWith("[0;32;22m") || buffer.endsWith("§2"))
				{
					if (buffer.charAt(buffer.length() - 2) == '§')
						addWord(buffer.substring(0, buffer.length() - 2), color, italics, strikethrough, underline,
								bold, magic);
					else
						addWord(buffer.substring(0, buffer.length() - 10), color, italics, strikethrough, underline,
								bold, magic);
					color = new Color(0x4E9A06); // 2
					italics = false;
					strikethrough = false;
					underline = false;
					bold = false;
					magic = false;
					buffer = "";
				}
				else if (buffer.endsWith("[0;36;22m") || buffer.endsWith("§3"))
				{
					if (buffer.charAt(buffer.length() - 2) == '§')
						addWord(buffer.substring(0, buffer.length() - 2), color, italics, strikethrough, underline,
								bold, magic);
					else
						addWord(buffer.substring(0, buffer.length() - 10), color, italics, strikethrough, underline,
								bold, magic);
					color = new Color(0x06989A); // 3
					italics = false;
					strikethrough = false;
					underline = false;
					bold = false;
					magic = false;
					buffer = "";
				}
				else if (buffer.endsWith("[0;31;22m") || buffer.endsWith("§4"))
				{
					if (buffer.charAt(buffer.length() - 2) == '§')
						addWord(buffer.substring(0, buffer.length() - 2), color, italics, strikethrough, underline,
								bold, magic);
					else
						addWord(buffer.substring(0, buffer.length() - 10), color, italics, strikethrough, underline,
								bold, magic);
					color = new Color(0xCC0000); // 4
					italics = false;
					strikethrough = false;
					underline = false;
					bold = false;
					magic = false;
					buffer = "";
				}
				else if (buffer.endsWith("[0;35;22m") || buffer.endsWith("§5"))
				{
					if (buffer.charAt(buffer.length() - 2) == '§')
						addWord(buffer.substring(0, buffer.length() - 2), color, italics, strikethrough, underline,
								bold, magic);
					else
						addWord(buffer.substring(0, buffer.length() - 10), color, italics, strikethrough, underline,
								bold, magic);
					color = new Color(0x75507B); // 5
					italics = false;
					strikethrough = false;
					underline = false;
					bold = false;
					magic = false;
					buffer = "";
				}
				else if (buffer.endsWith("[0;33;22m") || buffer.endsWith("§6"))
				{
					if (buffer.charAt(buffer.length() - 2) == '§')
						addWord(buffer.substring(0, buffer.length() - 2), color, italics, strikethrough, underline,
								bold, magic);
					else
						addWord(buffer.substring(0, buffer.length() - 10), color, italics, strikethrough, underline,
								bold, magic);
					color = new Color(0xBF9C01); // 6
					italics = false;
					strikethrough = false;
					underline = false;
					bold = false;
					magic = false;
					buffer = "";
				}
				else if (buffer.endsWith("[0;37;22m") || buffer.endsWith("§7"))
				{
					if (buffer.charAt(buffer.length() - 2) == '§')
						addWord(buffer.substring(0, buffer.length() - 2), color, italics, strikethrough, underline,
								bold, magic);
					else
						addWord(buffer.substring(0, buffer.length() - 10), color, italics, strikethrough, underline,
								bold, magic);
					color = new Color(0xD3D7CF); // 7
					italics = false;
					strikethrough = false;
					underline = false;
					bold = false;
					magic = false;
					buffer = "";
				}
				else if (buffer.endsWith("[0;30;1m") || buffer.endsWith("§8"))
				{
					if (buffer.charAt(buffer.length() - 2) == '§')
						addWord(buffer.substring(0, buffer.length() - 2), color, italics, strikethrough, underline,
								bold, magic);
					else
						addWord(buffer.substring(0, buffer.length() - 9), color, italics, strikethrough, underline,
								bold, magic);
					color = new Color(0x555753); // 8
					italics = false;
					strikethrough = false;
					underline = false;
					bold = false;
					magic = false;
					buffer = "";
				}
				else if (buffer.endsWith("[0;34;1m") || buffer.endsWith("§9"))
				{
					if (buffer.charAt(buffer.length() - 2) == '§')
						addWord(buffer.substring(0, buffer.length() - 2), color, italics, strikethrough, underline,
								bold, magic);
					else
						addWord(buffer.substring(0, buffer.length() - 9), color, italics, strikethrough, underline,
								bold, magic);
					color = new Color(0x729fCF); // 9
					italics = false;
					strikethrough = false;
					underline = false;
					bold = false;
					magic = false;
					buffer = "";
				}
				else if (buffer.endsWith("[0;32;1m") || buffer.endsWith("§a"))
				{
					if (buffer.charAt(buffer.length() - 2) == '§')
						addWord(buffer.substring(0, buffer.length() - 2), color, italics, strikethrough, underline,
								bold, magic);
					else
						addWord(buffer.substring(0, buffer.length() - 9), color, italics, strikethrough, underline,
								bold, magic);
					color = new Color(0x8AE234); // a
					italics = false;
					strikethrough = false;
					underline = false;
					bold = false;
					magic = false;
					buffer = "";
				}
				else if (buffer.endsWith("[0;36;1m") || buffer.endsWith("§b"))
				{
					if (buffer.charAt(buffer.length() - 2) == '§')
						addWord(buffer.substring(0, buffer.length() - 2), color, italics, strikethrough, underline,
								bold, magic);
					else
						addWord(buffer.substring(0, buffer.length() - 9), color, italics, strikethrough, underline,
								bold, magic);
					color = new Color(0x34E2E2); // b
					italics = false;
					strikethrough = false;
					underline = false;
					bold = false;
					magic = false;
					buffer = "";
				}
				else if (buffer.endsWith("[0;31;1m") || buffer.endsWith("§c"))
				{
					if (buffer.charAt(buffer.length() - 2) == '§')
						addWord(buffer.substring(0, buffer.length() - 2), color, italics, strikethrough, underline,
								bold, magic);
					else
						addWord(buffer.substring(0, buffer.length() - 9), color, italics, strikethrough, underline,
								bold, magic);
					color = new Color(0xE72929); // c
					italics = false;
					strikethrough = false;
					underline = false;
					bold = false;
					magic = false;
					buffer = "";
				}
				else if (buffer.endsWith("[0;35;1m") || buffer.endsWith("§d"))
				{
					if (buffer.charAt(buffer.length() - 2) == '§')
						addWord(buffer.substring(0, buffer.length() - 2), color, italics, strikethrough, underline,
								bold, magic);
					else
						addWord(buffer.substring(0, buffer.length() - 9), color, italics, strikethrough, underline,
								bold, magic);
					color = new Color(0xAD7fA8); // d
					italics = false;
					strikethrough = false;
					underline = false;
					bold = false;
					magic = false;
					buffer = "";
				}
				else if (buffer.endsWith("[0;33;1m") || buffer.endsWith("§e"))
				{
					if (buffer.charAt(buffer.length() - 2) == '§')
						addWord(buffer.substring(0, buffer.length() - 2), color, italics, strikethrough, underline,
								bold, magic);
					else
						addWord(buffer.substring(0, buffer.length() - 9), color, italics, strikethrough, underline,
								bold, magic);
					color = new Color(0xFCE94F); // e
					italics = false;
					strikethrough = false;
					underline = false;
					bold = false;
					magic = false;
					buffer = "";
				}
				else if (buffer.endsWith("[0;37;1m") || buffer.endsWith("§f"))
				{
					if (buffer.charAt(buffer.length() - 2) == '§')
						addWord(buffer.substring(0, buffer.length() - 2), color, italics, strikethrough, underline,
								bold, magic);
					else
						addWord(buffer.substring(0, buffer.length() - 9), color, italics, strikethrough, underline,
								bold, magic);
					color = new Color(0xEEEEEC); // f
					italics = false;
					strikethrough = false;
					underline = false;
					bold = false;
					magic = false;
					buffer = "";
				}
				else if (buffer.endsWith("[21m") || buffer.endsWith("§l"))
				{
					if (buffer.charAt(buffer.length() - 2) == '§')
						addWord(buffer.substring(0, buffer.length() - 2), color, italics, strikethrough, underline,
								bold, magic);
					else
						addWord(buffer.substring(0, buffer.length() - 5), color, italics, strikethrough, underline,
								bold, magic);
					bold = true; // l
					buffer = "";
				}
				else if (buffer.endsWith("[3m") || buffer.endsWith("§o"))
				{
					if (buffer.charAt(buffer.length() - 2) == '§')
						addWord(buffer.substring(0, buffer.length() - 2), color, italics, strikethrough, underline,
								bold, magic);
					else
						addWord(buffer.substring(0, buffer.length() - 4), color, italics, strikethrough, underline,
								bold, magic);
					italics = true; // o
					buffer = "";
				}
				else if (buffer.endsWith("[9m") || buffer.endsWith("§m"))
				{
					if (buffer.charAt(buffer.length() - 2) == '§')
						addWord(buffer.substring(0, buffer.length() - 2), color, italics, strikethrough, underline,
								bold, magic);
					else
						addWord(buffer.substring(0, buffer.length() - 4), color, italics, strikethrough, underline,
								bold, magic);
					strikethrough = true; // m
					buffer = "";
				}
				else if (buffer.endsWith("[4m") || buffer.endsWith("§n"))
				{
					if (buffer.charAt(buffer.length() - 2) == '§')
						addWord(buffer.substring(0, buffer.length() - 2), color, italics, strikethrough, underline,
								bold, magic);
					else
						addWord(buffer.substring(0, buffer.length() - 4), color, italics, strikethrough, underline,
								bold, magic);
					underline = true; // n
					buffer = "";
				}
				else if (buffer.endsWith("[5m") || buffer.endsWith("§k"))
				{
					if (buffer.charAt(buffer.length() - 2) == '§')
						addWord(buffer.substring(0, buffer.length() - 2), color, italics, strikethrough, underline,
								bold, magic);
					else
						addWord(buffer.substring(0, buffer.length() - 4), color, italics, strikethrough, underline,
								bold, magic);
					magic = true; // k
					buffer = "";
				}
				else if (buffer.endsWith("[m") || buffer.endsWith("§r"))
				{
					if (buffer.charAt(buffer.length() - 2) == '§')
						addWord(buffer.substring(0, buffer.length() - 2), color, italics, strikethrough, underline,
								bold, magic);
					else
						addWord(buffer.substring(0, buffer.length() - 3), color, italics, strikethrough, underline,
								bold, magic);
					color = new Color(0xEEEEEC); // r
					italics = false;
					strikethrough = false;
					underline = false;
					bold = false;
					buffer = "";
				}
			}
			addWord(buffer, color, italics, strikethrough, underline, bold, magic);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		try
		{
			scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
		}
		catch (NullPointerException e)
		{}
	}
	
	private static void addWord(String word, Color color, boolean italics, boolean strikethrough, boolean underline,
			boolean bold, boolean magic) throws Exception
	{
		MutableAttributeSet attr = new SimpleAttributeSet();
		AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, color);
		aset = sc.addAttribute(aset, StyleConstants.StrikeThrough, strikethrough);
		aset = sc.addAttribute(aset, StyleConstants.Bold, bold);
		aset = sc.addAttribute(aset, StyleConstants.Italic, italics);
		aset = sc.addAttribute(aset, StyleConstants.Underline, underline);
		if (magic)
		{
			aset = sc.addAttribute(aset, StyleConstants.Background,
					new Color(255 - color.getRed(), 255 - color.getGreen(), 255 - color.getBlue()));
		}
		attr.addAttributes(aset);
		int index = doc.getLength();
		doc.insertString(index, word, null);
		doc.setCharacterAttributes(index, word.length(), attr, false);
	}
}
