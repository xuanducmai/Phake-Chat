package Client;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import java.awt.Color;
import javax.swing.JButton;
import java.awt.Font;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.SwingConstants;
import java.awt.SystemColor;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;

public class Login extends JFrame {

	private JPanel contentPane;
	private JTextField txtUsername;
	private JPasswordField txtPassword;

	private String host = "localhost";
	private int port = 9999;
	private Socket socket;

	private DataInputStream dis;
	private DataOutputStream dos;

	private String username;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Login frame = new Login();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Login() {
		setTitle("PROJECT TEAM 1");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 383, 335);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

	
		//JLabel notification = new JLabel("");
		JLabel notification = new JLabel("");
		notification.setHorizontalAlignment(SwingConstants.CENTER);
		notification.setBounds(58, 272, 251, 14);
		notification.setForeground(Color.RED);
		notification.setFont(new Font("Times New Roman", Font.PLAIN, 13));
		contentPane.add(notification);

		JPanel panel_2 = new JPanel();
		panel_2.setBackground(Color.WHITE);
		panel_2.setBounds(68, 11, 222, 102);
		contentPane.add(panel_2);

		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setIcon(new ImageIcon("data\\icon\\component\\LogoChat.png"));
		lblNewLabel.setForeground(new Color(220, 20, 60));
		lblNewLabel.setFont(new Font("Lucida Calligraphy", Font.BOLD, 16));
		panel_2.add(lblNewLabel);

		txtUsername = new JTextField();
		txtUsername.setColumns(10);
		txtUsername.setBorder(
				new BevelBorder(BevelBorder.LOWERED, new Color(255, 0, 255), new Color(0, 255, 255), new Color(0, 0, 0), new Color(192, 192, 192)));
		txtUsername.setBounds(100, 138, 209, 20);
		contentPane.add(txtUsername);

		JLabel lbUsername = new JLabel("");
		lbUsername.setIcon(new ImageIcon(
				"data\\icon\\component\\icon-user-1.png"));
		lbUsername.setIgnoreRepaint(true);
		lbUsername.setHorizontalAlignment(SwingConstants.CENTER);
		lbUsername.setFont(new Font("Arial", Font.BOLD, 14));
		lbUsername.setBounds(58, 138, 34, 20);
		contentPane.add(lbUsername);

		JLabel lbPassword = new JLabel("");
		lbPassword.setIcon(new ImageIcon(
				"data\\icon\\component\\icon-pass-1.png"));
		lbPassword.setHorizontalAlignment(SwingConstants.CENTER);
		lbPassword.setFont(new Font("Arial", Font.BOLD, 14));
		lbPassword.setBounds(58, 176, 34, 17);
		contentPane.add(lbPassword);

		JButton login = new JButton("Log in");
		JButton signup = new JButton("Sign up");
		login.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String response = LoginUser(txtUsername.getText(), String.copyValueOf(txtPassword.getPassword()));

				// "Log in successful"
				if (response.equals("Log in successful")) {
					username = txtUsername.getText();
					EventQueue.invokeLater(new Runnable() {
						public void run() {
							try {
								ChatFrame frame = new ChatFrame(username, dis, dos);
								//Chat frame = new Chat(username, dis, dos);
								frame.setVisible(true);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
					dispose();
				} else {
					login.setEnabled(false);
					signup.setEnabled(false);
					txtPassword.setText("");
					notification.setText(response);
				}
			}
		});
		login.setEnabled(false);
		login.setBounds(58, 204, 251, 23);
		contentPane.add(login);

		signup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JPasswordField confirm = new JPasswordField();

				// Hiá»ƒn thá»‹ há»™p thoáº¡i xÃ¡c nháº­n password

				int action = JOptionPane.showConfirmDialog(null, confirm, "Comfirm your password",
						JOptionPane.OK_CANCEL_OPTION);
				if (action == JOptionPane.OK_OPTION) {
					if (String.copyValueOf(confirm.getPassword())
							.equals(String.copyValueOf(txtPassword.getPassword()))) {
						String response = Signup(txtUsername.getText(), String.copyValueOf(txtPassword.getPassword()));

						// Nhan phan hoi tu sever cho cmd signup
						// successful"
						if (response.equals("Sign up successful")) {
							username = txtUsername.getText();
							EventQueue.invokeLater(new Runnable() {
								public void run() {
									try {
										// in ra thong bao signup thanh cong
										int confirm = JOptionPane.showConfirmDialog(null,
												"Sign up successful\nWelcome to PHAKE CHAT", "Sign up successful",
												JOptionPane.DEFAULT_OPTION);

										ChatFrame frame = new ChatFrame(username, dis, dos);
										frame.setVisible(true);
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							});
							dispose();
						} else {
							login.setEnabled(false);
							signup.setEnabled(false);
							txtPassword.setText("");
							notification.setText(response);
						}
					} else {
						notification.setText("Confirm password does not match");
					}
				}
			}

		});
		signup.setEnabled(false);
		signup.setBounds(58, 238, 251, 23);
		contentPane.add(signup);

		txtPassword = new JPasswordField();
		txtPassword.setColumns(10);
		txtPassword.setBorder(
				new BevelBorder(BevelBorder.LOWERED, Color.MAGENTA, Color.CYAN, Color.BLACK, Color.LIGHT_GRAY));
		txtPassword.setBounds(100, 169, 209, 20);
		contentPane.add(txtPassword);
		
		txtUsername.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (txtUsername.getText().isBlank() || String.copyValueOf(txtPassword.getPassword()).isBlank()) {
					login.setEnabled(false);
					signup.setEnabled(false);
				} else {
					login.setEnabled(true);
					signup.setEnabled(true);
				}
			}
		});
		
		txtPassword.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (txtUsername.getText().isBlank() || String.copyValueOf(txtPassword.getPassword()).isBlank()) {
					login.setEnabled(false);
					signup.setEnabled(false);
				} else {
					login.setEnabled(true);
					signup.setEnabled(true);
				}
			}
		});

		JLabel lblNewLabel_1 = new JLabel("");
		lblNewLabel_1.setIcon(new ImageIcon("data\\icon\\component\\BGLogin.png"));
		lblNewLabel_1.setBounds(0, 0, 367, 296);
		contentPane.add(lblNewLabel_1);
		
		
		
	}

	public String LoginUser(String username, String password) {
		try {
			Connect();

			dos.writeUTF("Log in");
			dos.writeUTF(username);
			dos.writeUTF(password);
			dos.flush();

			String response = dis.readUTF();
			return response;

		} catch (IOException e) {
			e.printStackTrace();
			return "Network error: Log in fail";
		}
	}

	/**
	 * 
	 */
	public String Signup(String username, String password) {
		try {
			Connect();

			dos.writeUTF("Sign up");
			dos.writeUTF(username);
			dos.writeUTF(password);
			dos.flush();

			String response = dis.readUTF();
			return response;

		} catch (IOException e) {
			e.printStackTrace();
			return "Network error: Sign up fail";
		}
	}

	/**
	 * 
	 */
	public void Connect() {
		try {
			if (socket != null) {
				socket.close();
			}
			socket = new Socket(host, port);
			this.dis = new DataInputStream(socket.getInputStream());
			this.dos = new DataOutputStream(socket.getOutputStream());
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public String getUsername() {
		return this.username;
	}
}
