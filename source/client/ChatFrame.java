package Client;


import java.awt.*;
import java.util.List;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;
import javax.swing.text.*;

//import Server.ChatFrame.IconListener;

import javax.swing.border.BevelBorder;

public class ChatFrame extends JFrame {

	private JButton btnFile;
	private JButton btnSend;
	private JScrollPane chatPanel;
	private JLabel lbReceiver = new JLabel(" ");
	private JPanel contentPane;
	private JTextField txtMessage;
	private JTextPane chatWindow;
	DefaultListModel<String> groupList = new DefaultListModel<String>();
	JList<String> listGroup = new JList<String>(groupList);
	DefaultListModel<String> onlineUsers1 = new DefaultListModel<String>();
	JList<String> onlineUsersZ = new JList<String>(onlineUsers1);
	JList<String> listUserOnline = new JList<String>(onlineUsers1);
	JScrollPane OnlineListScrollPane;
	JScrollPane scrollGroup;
	private String username;
	private DataInputStream dis;
	private DataOutputStream dos;

	private HashMap<String, JTextPane> chatWindows = new HashMap<String, JTextPane>();

	Thread receiver;

	private void autoScroll() {
		chatPanel.getVerticalScrollBar().setValue(chatPanel.getVerticalScrollBar().getMaximum());
	}

	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Insert a emoji into chat pane.
	 */
	private void newEmoji(String username, String emoji, Boolean yourMessage, String PaneReceive) {

		StyledDocument doc;
		if (username.equals(this.username)) {
			doc = chatWindows.get(lbReceiver.getText()).getStyledDocument();
		} else {
			doc = chatWindows.get(PaneReceive).getStyledDocument();
		}

		Style userStyle = doc.getStyle("User style");
		if (userStyle == null) {
			userStyle = doc.addStyle("User style", null);
			StyleConstants.setBold(userStyle, true);
		}

		if (yourMessage == true) {
			StyleConstants.setForeground(userStyle, Color.red);
		} else {
			StyleConstants.setForeground(userStyle, Color.BLUE);
		}

		// In ra man hinh ten nguoi gui
		try {
			doc.insertString(doc.getLength(), username + ": ", userStyle);
		} catch (BadLocationException e) {
		}

		Style iconStyle = doc.getStyle("Icon style");
		if (iconStyle == null) {
			iconStyle = doc.addStyle("Icon style", null);
		}

		StyleConstants.setIcon(iconStyle, new ImageIcon(emoji));

		// In ra man hinh emoji
		try {
			doc.insertString(doc.getLength(), "invisible text", iconStyle);
		} catch (BadLocationException e) {
		}

		try {
			doc.insertString(doc.getLength(), "\n", userStyle);
		} catch (BadLocationException e) {
		}

		autoScroll();
	}

	/**
	 * Insert a file into chat pane.
	 */
	private void newFile(String username, String filename, byte[] file, Boolean yourMessage, String PaneReceive) {

		StyledDocument doc;
		String window = null;
		if (username.equals(this.username)) {
			window = lbReceiver.getText();
		} else {
			window = PaneReceive;
		}
		doc = chatWindows.get(window).getStyledDocument();
		Style userStyle = doc.getStyle("User style");
		if (userStyle == null) {
			userStyle = doc.addStyle("User style", null);
			StyleConstants.setBold(userStyle, true);
		}

		if (yourMessage == true) {
			StyleConstants.setForeground(userStyle, Color.red);
		} else {
			StyleConstants.setForeground(userStyle, Color.BLUE);
		}

		try {
			doc.insertString(doc.getLength(), username + ": ", userStyle);
		} catch (BadLocationException e) {
		}

		Style linkStyle = doc.getStyle("Link style");
		if (linkStyle == null) {
			linkStyle = doc.addStyle("Link style", null);
			StyleConstants.setForeground(linkStyle, Color.BLUE);
			StyleConstants.setUnderline(linkStyle, true);
			StyleConstants.setBold(linkStyle, true);
			linkStyle.addAttribute("link", new HyberlinkListener(filename, file));
		}

		if (chatWindows.get(window).getMouseListeners() != null) {
			// Táº¡o MouseListener cho cÃ¡c Ä‘Æ°á»�ng dáº«n táº£i vá»� file
			chatWindows.get(window).addMouseListener(new MouseListener() {

				@Override
				public void mouseClicked(MouseEvent e) {
					Element ele = doc.getCharacterElement(chatWindow.viewToModel(e.getPoint()));
					AttributeSet as = ele.getAttributes();
					HyberlinkListener listener = (HyberlinkListener) as.getAttribute("link");
					if (listener != null) {
						listener.execute();
					}
				}

				@Override
				public void mousePressed(MouseEvent e) {
					// TODO Auto-generated method stub

				}

				@Override
				public void mouseReleased(MouseEvent e) {
					// TODO Auto-generated method stub

				}

				@Override
				public void mouseEntered(MouseEvent e) {
					// TODO Auto-generated method stub

				}

				@Override
				public void mouseExited(MouseEvent e) {
					// TODO Auto-generated method stub

				}

			});
		}

		// In ra duong dan toi file
		try {
			doc.insertString(doc.getLength(), "<" + filename + ">", linkStyle);
		} catch (BadLocationException e1) {
			e1.printStackTrace();
		}

		// Xuá»‘ng dÃ²ng
		try {
			doc.insertString(doc.getLength(), "\n", userStyle);
		} catch (BadLocationException e1) {
			e1.printStackTrace();
		}

		autoScroll();
	}

	/**
	 * Insert a new message into chat pane.
	 */
	private void newMessage(String username, String message, Boolean yourMessage, String PaneReceive) {

		StyledDocument doc;
//		if (isGroupC.charAt(0) == '#') {
//			doc = chatWindows.get(isGroupC).getStyledDocument();
//		} else
		if (username.equals(this.username)) {
			doc = chatWindows.get(lbReceiver.getText()).getStyledDocument();
		} else {
			doc = chatWindows.get(PaneReceive).getStyledDocument();
		}

		Style userStyle = doc.getStyle("User style");
		if (userStyle == null) {
			userStyle = doc.addStyle("User style", null);
			StyleConstants.setBold(userStyle, true);
		}

		if (yourMessage == true) {
			StyleConstants.setForeground(userStyle, Color.red);
		} else {
			StyleConstants.setForeground(userStyle, Color.BLUE);
		}

		// In ra tÃªn ngÆ°á»�i gá»­i
		try {
			doc.insertString(doc.getLength(), username + ": ", userStyle);
		} catch (BadLocationException e) {
		}

		Style messageStyle = doc.getStyle("Message style");
		if (messageStyle == null) {
			messageStyle = doc.addStyle("Message style", null);
			StyleConstants.setForeground(messageStyle, Color.BLACK);
			StyleConstants.setBold(messageStyle, false);
		}

		// In ra ná»™i dung tin nháº¯n
		try {
			doc.insertString(doc.getLength(), message + "\n", messageStyle);
		} catch (BadLocationException e) {
		}

		autoScroll();
	}

	/**
	 * Create the frame.
	 */
	public ChatFrame(String username, DataInputStream dis, DataOutputStream dos) {
		setTitle("PROJECT TEAM 1");
		this.username = username;
		this.dis = dis;
		this.dos = dos;
		receiver = new Thread(new Receiver(dis));

		receiver.start();

		setDefaultLookAndFeelDecorated(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 686, 462);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setBackground(Color.WHITE);
		setContentPane(contentPane);

		chatWindows.put(" ", new JTextPane());
		chatWindow = chatWindows.get(" ");
		chatWindow.setFont(new Font("Arial", Font.PLAIN, 14));
		chatWindow.setEditable(false);
		contentPane.setLayout(null);

		JLayeredPane panel_3 = new JLayeredPane();
		panel_3.setBounds(106, 11, 541, 395);
		contentPane.add(panel_3);
		panel_3.setLayout(null);

		JPanel panelHome = new JPanel();
		panelHome.setVisible(false);

		JPanel panelChat = new JPanel();
		panelChat.setBounds(0, 0, 541, 395);
		panel_3.add(panelChat);
		panelChat.setOpaque(false);
		panelChat.setLayout(null);

		JPanel emojis = new JPanel();
		emojis.setOpaque(false);
		emojis.setBounds(130, 338, 411, 23);
		panelChat.add(emojis);
		emojis.setBackground(new Color(230, 240, 247));

		JLabel thumbupIcon = new JLabel(
				new ImageIcon("data\\icon\\emoji\\thumb-up1.png"));
		thumbupIcon.addMouseListener(new IconListener(thumbupIcon.getIcon().toString()));
		emojis.add(thumbupIcon);

		JLabel happyIcon = new JLabel(
				new ImageIcon("data\\icon\\emoji\\happy2.png"));
		happyIcon.addMouseListener(new IconListener(happyIcon.getIcon().toString()));
		emojis.add(happyIcon);

		JLabel smileIcon = new JLabel(
				new ImageIcon("data\\icon\\emoji\\smile3.png"));
		smileIcon.addMouseListener(new IconListener(smileIcon.getIcon().toString()));
		emojis.add(smileIcon);

		JLabel bigsmilecon = new JLabel(
				new ImageIcon("data\\icon\\emoji\\big-smile4.png"));
		bigsmilecon.addMouseListener(new IconListener(bigsmilecon.getIcon().toString()));
		emojis.add(bigsmilecon);

		JLabel kissIcon = new JLabel(
				new ImageIcon("data\\icon\\emoji\\kiss6.png"));
		kissIcon.addMouseListener(new IconListener(kissIcon.getIcon().toString()));
		emojis.add(kissIcon);

		JLabel heartIcon = new JLabel(
				new ImageIcon("data\\icon\\emoji\\heart7.png"));
		heartIcon.addMouseListener(new IconListener(heartIcon.getIcon().toString()));
		emojis.add(heartIcon);

		JLabel madIcon = new JLabel(
				new ImageIcon("data\\icon\\emoji\\mad8.png"));
		madIcon.addMouseListener(new IconListener(madIcon.getIcon().toString()));
		emojis.add(madIcon);

		JLabel angryIcon = new JLabel(
				new ImageIcon("data\\icon\\emoji\\angry9.png"));
		angryIcon.addMouseListener(new IconListener(angryIcon.getIcon().toString()));
		emojis.add(angryIcon);

		JLabel sadIcon = new JLabel(
				new ImageIcon("data\\icon\\emoji\\sad10.png"));
		sadIcon.addMouseListener(new IconListener(sadIcon.getIcon().toString()));
		emojis.add(sadIcon);

		JLabel confusedIcon = new JLabel(
				new ImageIcon("data\\icon\\emoji\\confused11.png"));
		confusedIcon.addMouseListener(new IconListener(confusedIcon.getIcon().toString()));
		emojis.add(confusedIcon);

		JLabel uphappyIcon = new JLabel(
				new ImageIcon("data\\icon\\emoji\\unhappy12.png"));
		uphappyIcon.addMouseListener(new IconListener(uphappyIcon.getIcon().toString()));
		emojis.add(uphappyIcon);

		JLabel crazyIcon = new JLabel(
				new ImageIcon("data\\icon\\emoji\\crazy13.png"));
		crazyIcon.addMouseListener(new IconListener(crazyIcon.getIcon().toString()));
		emojis.add(crazyIcon);

		JLabel suspiciousIcon = new JLabel(
				new ImageIcon("data\\icon\\emoji\\suspicious14.png"));
		suspiciousIcon.addMouseListener(new IconListener(suspiciousIcon.getIcon().toString()));
		emojis.add(suspiciousIcon);

		JLabel grumpyIcon = new JLabel(
				new ImageIcon("data\\icon\\emoji\\grumpy15.png"));
		grumpyIcon.addMouseListener(new IconListener(grumpyIcon.getIcon().toString()));
		emojis.add(grumpyIcon);

		JLabel drinkIcon = new JLabel(
				new ImageIcon("data\\icon\\emoji\\drink16.png"));
		drinkIcon.addMouseListener(new IconListener(drinkIcon.getIcon().toString()));
		emojis.add(drinkIcon);

		JLabel burgerIcon = new JLabel(
				new ImageIcon("data\\icon\\emoji\\burger17.png"));
		burgerIcon.addMouseListener(new IconListener(burgerIcon.getIcon().toString()));
		emojis.add(burgerIcon);

		JLabel joystickIcon = new JLabel(
				new ImageIcon("data\\icon\\emoji\\joystick18.png"));
		joystickIcon.addMouseListener(new IconListener(joystickIcon.getIcon().toString()));
		emojis.add(joystickIcon);

		JLabel poopIcon = new JLabel(
				new ImageIcon("data\\icon\\emoji\\poop19.png"));
		poopIcon.addMouseListener(new IconListener(poopIcon.getIcon().toString()));
		emojis.add(poopIcon);

		txtMessage = new JTextField();
		txtMessage.setBounds(127, 372, 342, 23);
		panelChat.add(txtMessage);
		txtMessage.setBorder(new BevelBorder(BevelBorder.LOWERED, new Color(0, 255, 255), new Color(255, 0, 255),
				new Color(0, 0, 0), new Color(127, 255, 212)));
		txtMessage.setEnabled(false);
		txtMessage.setColumns(10);

		btnFile = new JButton("");
		btnFile.setBounds(479, 372, 26, 23);
		panelChat.add(btnFile);
		btnFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				// Hien thi hop thoai cho nguoi dung chon file can gui
				JFileChooser fileChooser = new JFileChooser();
				int rVal = fileChooser.showOpenDialog(contentPane.getParent());
				if (rVal == JFileChooser.APPROVE_OPTION) {
					byte[] selectedFile = new byte[(int) fileChooser.getSelectedFile().length()];
					BufferedInputStream bis;
					try {
						bis = new BufferedInputStream(new FileInputStream(fileChooser.getSelectedFile()));
						// gan file vao bien selectedFile
						bis.read(selectedFile, 0, selectedFile.length);

						dos.writeUTF("File");
						dos.writeUTF(lbReceiver.getText());
						dos.writeUTF(fileChooser.getSelectedFile().getName());
						dos.writeUTF(String.valueOf(selectedFile.length));

						int size = selectedFile.length;
						int bufferSize = 2048;
						int offset = 0;

						// lan luoi gui cho server tung buffer cho toi khi het file
						while (size > 0) {
							dos.write(selectedFile, offset, Math.min(size, bufferSize));
							offset += Math.min(size, bufferSize);
							size -= bufferSize;
						}

						dos.flush();

						bis.close();

						// In ra man hinh file
						newFile(username, fileChooser.getSelectedFile().getName(), selectedFile, true,
								lbReceiver.getText());
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		btnFile.setEnabled(false);
		btnFile.setIcon(new ImageIcon("data\\icon\\component\\attach.png"));

		btnSend = new JButton("");
		btnSend.setBounds(515, 372, 26, 23);
		panelChat.add(btnSend);
		btnSend.setEnabled(false);
		btnSend.setIcon(new ImageIcon("data\\icon\\component\\send.png"));

		// Set action perform to send button.
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				try {
					dos.writeUTF("Text");
					dos.writeUTF(lbReceiver.getText());
					dos.writeUTF(txtMessage.getText());
					dos.flush();
				} catch (IOException e1) {
					e1.printStackTrace();
					newMessage("ERROR", "Network error!", true, "");
				}

				// In ra tin nhan len man hinh chat voi nguoi nháº­n
				newMessage(username, txtMessage.getText(), true, lbReceiver.getText());
				txtMessage.setText("");
			}
		});

		this.getRootPane().setDefaultButton(btnSend);

		chatPanel = new JScrollPane();
		chatPanel.setBounds(130, 110, 411, 223);
		panelChat.add(chatPanel);
		chatPanel.setBackground(new Color(30, 144, 255));
		chatPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		JPanel usernamePanel = new JPanel();
		usernamePanel.setBackground(new Color(72, 209, 204));
		chatPanel.setColumnHeaderView(usernamePanel);

		lbReceiver.setFont(new Font("Arial", Font.BOLD, 16));
		JLabel lbCall = new JLabel("callphone");
		lbCall.setIcon(new ImageIcon("data\\icon\\component\\call.png"));
		lbCall.setVisible(false);

		JLabel lbVideo = new JLabel("callvideo");
		lbVideo.setVisible(false);
		lbVideo.setIcon(new ImageIcon("data\\icon\\component\\video-camera.png"));

		JLabel lbSetting = new JLabel("settingchat");
		lbSetting.setVisible(false);
		lbSetting.setIcon(
				new ImageIcon("data\\icon\\component\\ellipsis.png"));

		JLabel lbAva = new JLabel("avtChat");
		lbAva.setVisible(false);
		lbAva.setIcon(
				new ImageIcon("data\\icon\\component\\userchat.png"));
		GroupLayout gl_usernamePanel = new GroupLayout(usernamePanel);
		gl_usernamePanel.setHorizontalGroup(gl_usernamePanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_usernamePanel.createSequentialGroup().addContainerGap()
						.addComponent(lbAva, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE).addGap(18)
						.addComponent(lbReceiver).addPreferredGap(ComponentPlacement.RELATED, 175, Short.MAX_VALUE)
						.addComponent(lbCall, GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE).addGap(18)
						.addComponent(lbVideo, GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addComponent(lbSetting, GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE)
						.addGap(94)));
		gl_usernamePanel.setVerticalGroup(gl_usernamePanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_usernamePanel.createSequentialGroup().addGap(5)
						.addGroup(gl_usernamePanel.createParallelGroup(Alignment.BASELINE)
								.addComponent(lbAva, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
								.addComponent(lbReceiver)
								.addComponent(lbSetting, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
								.addComponent(lbVideo, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
								.addComponent(lbCall, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE))));
		usernamePanel.setLayout(gl_usernamePanel);

		chatPanel.setViewportView(chatWindow);

		JPanel panel_1 = new JPanel();
		panel_1.setBounds(0, 0, 114, 99);
		panelChat.add(panel_1);
		panel_1.setBorder(new BevelBorder(BevelBorder.LOWERED, new Color(0, 255, 255), new Color(255, 0, 255),
				new Color(0, 0, 0), new Color(72, 209, 204)));
		panel_1.setBackground(new Color(245, 255, 250));
		panel_1.setLayout(null);

		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setBounds(10, 5, 94, 23);
		panel_1.add(panel);
		panel.setBackground(new Color(127, 255, 212));

		JLabel lbUsername = new JLabel(this.username);
		lbUsername.setFont(new Font("Arial", Font.BOLD, 15));
		panel.add(lbUsername);

		JLabel lblNewLabel_3 = new JLabel("");
		lblNewLabel_3.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_3.setIcon(
				new ImageIcon("data\\icon\\component\\profile.png"));
		lblNewLabel_3.setBounds(10, 29, 94, 70);
		panel_1.add(lblNewLabel_3);

		JPanel panel_2 = new JPanel();
		panel_2.setBounds(264, 0, 185, 99);
		panelChat.add(panel_2);

		JLabel lblNewLabel_2 = new JLabel("");
		lblNewLabel_2.setIcon(new ImageIcon("data\\icon\\component\\LogoLG.png"));
		panel_2.add(lblNewLabel_2);

		JPanel panel_4 = new JPanel();
		panel_4.setBounds(0, 144, 114, 99);
		panelChat.add(panel_4);
		panel_4.setLayout(null);

		OnlineListScrollPane = new JScrollPane(listUserOnline);
		OnlineListScrollPane.setBorder(new BevelBorder(BevelBorder.LOWERED, new Color(255, 0, 255),
				new Color(0, 255, 255), new Color(0, 0, 0), new Color(0, 255, 255)));
		OnlineListScrollPane.setBounds(0, 0, 113, 98);
		panel_4.add(OnlineListScrollPane);

		JLabel labeOnl = new JLabel("ONLINE USERS");
		labeOnl.setBounds(10, 119, 84, 14);
		panelChat.add(labeOnl);
		labeOnl.setHorizontalAlignment(SwingConstants.CENTER);
		labeOnl.setFont(new Font("Tahoma", Font.BOLD, 11));

		JLabel lbGroup = new JLabel("GROUP CHAT");
		lbGroup.setBounds(10, 249, 84, 14);
		panelChat.add(lbGroup);
		lbGroup.setHorizontalAlignment(SwingConstants.CENTER);
		lbGroup.setFont(new Font("Tahoma", Font.BOLD, 11));

		listGroup.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() > 1) {
					lbReceiver.setText(listGroup.getSelectedValue());
					if (chatWindow != chatWindows.get(lbReceiver.getText())) {
						txtMessage.setText("");
						chatWindow = chatWindows.get(lbReceiver.getText());
						chatPanel.setViewportView(chatWindow);
						chatPanel.validate();
					}

					if (lbReceiver.getText().isBlank()) {
						btnSend.setEnabled(false);
						btnFile.setEnabled(false);
						txtMessage.setEnabled(false);
						lbAva.setVisible(false);
						lbCall.setVisible(false);
						lbVideo.setVisible(false);
						lbSetting.setVisible(false);
					} else {
						btnSend.setEnabled(true);
						btnFile.setEnabled(true);
						txtMessage.setEnabled(true);
						lbAva.setVisible(true);
						lbCall.setVisible(true);
						lbVideo.setVisible(true);
						lbSetting.setVisible(true);
					}
				}
			}
		});

		// listUserOnline.setBounds(0, 12, 113, 86);
		// panel_4.add(listUserOnline);

		JPanel panel_5 = new JPanel();
		panel_5.setBounds(0, 274, 114, 87);
		panelChat.add(panel_5);
		panel_5.setLayout(null);

		scrollGroup = new JScrollPane(listGroup);
		scrollGroup.setBorder(new BevelBorder(BevelBorder.LOWERED, new Color(0, 255, 255), new Color(255, 0, 255),
				new Color(0, 0, 0), new Color(0, 255, 255)));
		scrollGroup.setBounds(0, 0, 114, 87);
		panel_5.add(scrollGroup);
		listUserOnline.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() > 1) {
					lbReceiver.setText(listUserOnline.getSelectedValue());
					if (chatWindow != chatWindows.get(lbReceiver.getText())) {
						txtMessage.setText("");
						chatWindow = chatWindows.get(lbReceiver.getText());
						chatPanel.setViewportView(chatWindow);
						chatPanel.validate();
					}

					if (lbReceiver.getText().isBlank()) {
						btnSend.setEnabled(false);
						btnFile.setEnabled(false);
						txtMessage.setEnabled(false);
						lbAva.setVisible(false);
						lbCall.setVisible(false);
						lbVideo.setVisible(false);
						lbSetting.setVisible(false);
					} else {
						btnSend.setEnabled(true);
						btnFile.setEnabled(true);
						txtMessage.setEnabled(true);
						lbAva.setVisible(true);
						lbCall.setVisible(true);
						lbVideo.setVisible(true);
						lbSetting.setVisible(true);
					}
				}
			}
		});

		txtMessage.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (txtMessage.getText().isBlank() || lbReceiver.getText().isBlank()) {
					btnSend.setEnabled(false);
				} else {
					btnSend.setEnabled(true);
				}
			}
		});
		panelHome.setOpaque(false);
		panelHome.setBounds(0, 0, 541, 395);
		panel_3.add(panelHome);
		panelHome.setLayout(null);

		JPanel panel_1_1 = new JPanel();
		panel_1_1.setBounds(371, 301, -209, -143);
		panel_1_1.setLayout(null);
		panel_1_1.setBorder(new BevelBorder(BevelBorder.LOWERED, new Color(0, 255, 255), new Color(255, 0, 255),

				new Color(0, 0, 0), new Color(72, 209, 204)));
		panel_1_1.setBackground(new Color(255, 0, 0));
		panelHome.add(panel_1_1);

		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setBounds(168, 5, 198, 102);
		lblNewLabel.setIcon(
				new ImageIcon("data\\icon\\component\\LogoChat.png"));
		panelHome.add(lblNewLabel);

		JPanel panel_6 = new JPanel();
		panel_6.setBounds(210, 192, 94, 23);
		panelHome.add(panel_6);
		panel_6.setBackground(new Color(127, 255, 212));

		JLabel lbUsername_1 = new JLabel(this.username);
		lbUsername_1.setFont(new Font("Arial", Font.BOLD, 15));
		panel_6.add(lbUsername_1);

		JLabel lblNewLabel_3_1 = new JLabel("");
		lblNewLabel_3_1.setIcon(
				new ImageIcon("data\\icon\\component\\profile.png"));
		lblNewLabel_3_1.setBounds(210, 226, 94, 70);
		panelHome.add(lblNewLabel_3_1);
		lblNewLabel_3_1.setHorizontalAlignment(SwingConstants.CENTER);

		JLabel lblNewLabel_1 = new JLabel("Welcome to Phake Chat");
		lblNewLabel_1.setOpaque(true);
		lblNewLabel_1.setBackground(new Color(0, 0, 0));
		lblNewLabel_1.setForeground(new Color(248, 248, 255));
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setFont(new Font("Lucida Handwriting", Font.BOLD, 19));
		lblNewLabel_1.setBounds(80, 126, 391, 35);
		panelHome.add(lblNewLabel_1);
		lblNewLabel_1.setIcon(
				new ImageIcon("data\\icon\\component\\call.png"));

		chatWindows.put(" ", new JTextPane());
		chatWindow = chatWindows.get(" ");
		chatWindow.setFont(new Font("Arial", Font.PLAIN, 14));
		chatWindow.setEditable(false);

		JPanel panelMenu = new JPanel();
		panelMenu.setBackground(new Color(127, 255, 212));
		panelMenu.setBounds(22, 11, 55, 395);
		contentPane.add(panelMenu);
		panelMenu.setLayout(null);

		JButton btnHome = new JButton("");

		btnHome.setBounds(3, 30, 49, 33);
		btnHome.setMinimumSize(new Dimension(33, 17));
		btnHome.setMaximumSize(new Dimension(33, 17));
		btnHome.setToolTipText("Home");
		btnHome.setBackground(new Color(240, 255, 255));
		btnHome.setIcon(
				new ImageIcon("data\\icon\\component\\home.png"));
		panelMenu.add(btnHome);

		JButton btnChat = new JButton("");

		btnChat.setBounds(3, 114, 49, 33);
		btnChat.setToolTipText("Chat");
		btnChat.setIcon(new ImageIcon(
				"data\\icon\\component\\messenger.png"));
		btnChat.setBackground(new Color(240, 255, 255));
		panelMenu.add(btnChat);

		JButton btnSetting = new JButton("");
		btnSetting.setBounds(3, 190, 49, 33);
		btnSetting.setToolTipText("Setting");
		btnSetting.setIcon(
				new ImageIcon("data\\icon\\component\\settings.png"));
		btnSetting.setBackground(new Color(240, 255, 255));
		panelMenu.add(btnSetting);

		JButton btnNewGroup = new JButton("");
		btnNewGroup.setBounds(3, 262, 49, 33);
		btnNewGroup.setToolTipText("Create Group");
		btnNewGroup.setIcon(
				new ImageIcon("data\\icon\\component\\team.png"));
		panelMenu.add(btnNewGroup);

		JButton btnLogout = new JButton("");
		btnLogout.setBounds(3, 337, 49, 33);
		btnLogout.setToolTipText("Logout");
		btnLogout.setIcon(
				new ImageIcon("data\\icon\\component\\logout2.png"));
		btnLogout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					dos.writeUTF("Log out");
					dos.flush();

					try {
						receiver.join();
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}

					if (dos != null) {
						dos.close();
					}
					if (dis != null) {
						dis.close();
					}
					try {
						Login frame = new Login();
						frame.setVisible(true);

					} catch (Exception e2) {
						e2.printStackTrace();
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				dispose();
			}
		});
		btnLogout.setBackground(new Color(240, 248, 255));
		panelMenu.add(btnLogout);

		JLabel lbBG = new JLabel("");
		lbBG.setIcon(new ImageIcon("data\\icon\\component\\BGChat.jpg"));
		lbBG.setBounds(0, 0, 670, 423);
		contentPane.add(lbBG);

		btnNewGroup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JTextField confirm = new JTextField();
				// String Topicname = confirm.getText();
				int action = JOptionPane.showConfirmDialog(null, confirm, "Input your name off group",
						JOptionPane.OK_CANCEL_OPTION);
				if (action == JOptionPane.OK_OPTION) {
					onlineUsersZ.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
					int action2 = JOptionPane.showConfirmDialog(null, onlineUsersZ, "Choose Member",
							JOptionPane.OK_CANCEL_OPTION);
					if (action2 == JOptionPane.OK_OPTION) {
						List<String> SeleListData = onlineUsersZ.getSelectedValuesList();
						String SeleList = username + " ";
						// String SeleList = "";
						if (onlineUsersZ.getSelectedIndex() != -1) {
							for (String s : SeleListData) {
								SeleList += s + " ";
							}
						}
						try {
							dos.writeUTF("CreatGroup");
							dos.writeUTF(SeleList);
							dos.writeUTF("#" + confirm.getText());
							dos.flush();
						} catch (IOException e1) {
							e1.printStackTrace();
							newMessage("ERROR", "Network error!", true, "");
						}
					}

				}
			}
		});
		btnHome.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panelChat.setVisible(false);
				panelHome.setVisible(true);
			}
		});
		btnChat.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panelChat.setVisible(true);
				panelHome.setVisible(false);
			}
		});
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {

				try {
					dos.writeUTF("Log out");
					dos.flush();

					try {
						receiver.join();
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}

					if (dos != null) {
						dos.close();
					}
					if (dis != null) {
						dis.close();
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});

	}

	/**
	 * Luong nhan tin nhan tu server cá»§a má»—i client
	 */
	class Receiver implements Runnable {

		private DataInputStream dis;

		public Receiver(DataInputStream dis) {
			this.dis = dis;
		}

		@Override
		public void run() {
			try {
				// JGroupList.addItem(" ");
				while (true) {
					// Nhan tin nhan tu sever
					String method = dis.readUTF();

					if (method.equals("Text")) {
						// nhan tin nhan van ban
						String sender = dis.readUTF();
						String message = dis.readUTF();
						String PaneReceiver = dis.readUTF();

						// In tin nhan len man hinh chat voi nguoi gui
						newMessage(sender, message, false, PaneReceiver);
					} else if (method.equals("CreatGroup")) {
						// Nhan tin nhan emojo
						String topicName = dis.readUTF();
						// Jgroup List add element
						// JGroupList.addItem(topicName);
						groupList.addElement(topicName);
						listGroup.validate();
						scrollGroup.validate();

						if (chatWindows.get(topicName) == null) {
							JTextPane temp = new JTextPane();
							temp.setFont(new Font("Arial", Font.PLAIN, 14));
							temp.setEditable(false);
							chatWindows.put(topicName, temp);
						}

					}

					else if (method.equals("Emoji")) {
						// Nhan tin nhan emoji
						String sender = dis.readUTF();
						String emoji = dis.readUTF();
						String PaneReceiver = dis.readUTF();

						// In tin nhan len man hinh chat voi nguoi gui
						newEmoji(sender, emoji, false, PaneReceiver);
					}

					else if (method.equals("File")) {
						// Nhan nhan file
						String sender = dis.readUTF();
						String filename = dis.readUTF();
						String PaneReceiver = dis.readUTF();
						int size = Integer.parseInt(dis.readUTF());
						int bufferSize = 2048;
						byte[] buffer = new byte[bufferSize];
						ByteArrayOutputStream file = new ByteArrayOutputStream();

						while (size > 0) {
							dis.read(buffer, 0, Math.min(bufferSize, size));
							file.write(buffer, 0, Math.min(bufferSize, size));
							size -= bufferSize;
						}

						// In ra man hinh file
						newFile(sender, filename, file.toByteArray(), false, PaneReceiver);

					}

					else if (method.equals("Online users")) {
						// cap nhat danh sach user online
						String[] users = dis.readUTF().split(",");
						onlineUsers1.removeAllElements();

						String chatting = lbReceiver.getText();

						boolean isChattingOnline = false;

						for (String user : users) {
							if (user.equals(username) == false) {
								// onlineUsers.addItem(user);
								onlineUsers1.addElement(user);
								if (chatWindows.get(user) == null) {
									JTextPane temp = new JTextPane();
									temp.setFont(new Font("Arial", Font.PLAIN, 14));
									temp.setEditable(false);
									chatWindows.put(user, temp);
								}

							}
							if (chatting.equals(user)) {
								isChattingOnline = true;
							}
							if (chatting.charAt(0)== '#') {
								isChattingOnline = true; 
							}
						}

						if (isChattingOnline == false) {
							JOptionPane.showMessageDialog(null,
									chatting + " is offline!");
						}
//						} else {
//							onlineUsers.setSelectedItem(chatting);
						listUserOnline.validate();
					}

					else if (method.equals("Safe to leave")) {
						// ThÃ´ng bÃ¡o cÃ³ thá»ƒ thoÃ¡t
						break;
					}

				}

			} catch (IOException ex) {
				System.err.println(ex);
			} finally {
				try {
					if (dis != null) {
						dis.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * MouseListener cho cÃ¡c Ä‘Æ°á»�ng dáº«n táº£i file.
	 */
	class HyberlinkListener extends AbstractAction {
		String filename;
		byte[] file;

		public HyberlinkListener(String filename, byte[] file) {
			this.filename = filename;
			this.file = Arrays.copyOf(file, file.length);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			execute();
		}

		public void execute() {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setSelectedFile(new File(filename));
			int rVal = fileChooser.showSaveDialog(contentPane.getParent());
			if (rVal == JFileChooser.APPROVE_OPTION) {

				// Má»Ÿ file Ä‘Ã£ chá»�n sau Ä‘Ã³ lÆ°u thÃ´ng tin xuá»‘ng file Ä‘Ã³
				File saveFile = fileChooser.getSelectedFile();
				BufferedOutputStream bos = null;
				try {
					bos = new BufferedOutputStream(new FileOutputStream(saveFile));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}

				// Hiá»ƒn thá»‹ JOptionPane cho ngÆ°á»�i dÃ¹ng cÃ³ muá»‘n má»Ÿ file vá»«a táº£i
				// vá»� khÃ´ng
				int nextAction = JOptionPane.showConfirmDialog(null,
						"Saved file to " + saveFile.getAbsolutePath() + "\nDo you want to open this file?",
						"Successful", JOptionPane.YES_NO_OPTION);
				if (nextAction == JOptionPane.YES_OPTION) {
					try {
						Desktop.getDesktop().open(saveFile);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				if (bos != null) {
					try {
						bos.write(this.file);
						bos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * MouseAdapter cho cÃ¡c Emoji.
	 */
	class IconListener extends MouseAdapter {
		String emoji;

		public IconListener(String emoji) {
			this.emoji = emoji;
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (txtMessage.isEnabled() == true) {

				try {
					dos.writeUTF("Emoji");
					dos.writeUTF(lbReceiver.getText());
					dos.writeUTF(this.emoji);
					dos.flush();
				} catch (IOException e1) {
					e1.printStackTrace();
					newMessage("ERROR", "Network error!", true, "");
				}

				// In Emoji lÃªn mÃ n hÃ¬nh chat vá»›i ngÆ°á»�i nháº­n
				newEmoji(username, this.emoji, true, lbReceiver.getText());
			}
		}
	}
}
