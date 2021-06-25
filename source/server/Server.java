package Server;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;

public class Server {
	private Object lock;

	private ServerSocket s;
	private Socket socket;
	static ArrayList<Handler> clients = new ArrayList<Handler>();
	private String dataFile = "data\\accounts.txt";

	// load danh sach tai khoan
	private void loadAccounts() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(dataFile), "utf8"));

			String info = br.readLine();
			while (info != null && !(info.isEmpty())) {
				clients.add(new Handler(info.split(",")[0], info.split(",")[1], false, lock));
				info = br.readLine();
			}

			br.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// luu tai khoan 
	private void saveAccounts() {
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new File(dataFile), "utf8");
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		for (Handler client : clients) {
			pw.print(client.getUsername() + "," + client.getPassword() + "\n");
		}
		pw.println("");
		if (pw != null) {
			pw.close();
		}
	}

	public Server() throws IOException {
		try {
			//Oject de dong bo tai khoan nguoi dung
			lock = new Object();

			// tai len danh sach tai khoan tu file
			this.loadAccounts();
			// tao serversocket 9999 cho client ket noi
			s = new ServerSocket(9999);

			while (true) {
				// doi client ket noi
				socket = s.accept();

				DataInputStream dis = new DataInputStream(socket.getInputStream());
				DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

				// xac nhan cac yeu cau dagn nhap dang xuat
				String request = dis.readUTF();

				if (request.equals("Sign up")) {
					// yeu cau dang ky user

					String username = dis.readUTF();
					String password = dis.readUTF();

					// kiem tra ten dang nhap co hay chua
					if (isExisted(username) == false) {

						// Tao handler cho user
						Handler newHandler = new Handler(socket, username, password, true, lock);
						clients.add(newHandler);

						// luu danh sach dang nhap xuong file va thong bao
						this.saveAccounts();
						dos.writeUTF("Sign up successful");
						dos.flush();

						// tao 1 thread de giao tiep voi user
						Thread t = new Thread(newHandler);
						t.start();

						// gui thong bao online cho cac user khac
						updateOnlineUsers();
					} else {

						// nguoc lai thong bao dang that bai
						dos.writeUTF("This username is being used");
						dos.flush();
					}
				} else if (request.equals("Log in")) {
					// yeu cau dang nhap

					String username = dis.readUTF();
					String password = dis.readUTF();

					// kiem tra ten dang nhap co trong danh sach hay khong
					if (isExisted(username) == true) {
						for (Handler client : clients) {
							if (client.getUsername().equals(username)) { // trung ten
								if (password.equals(client.getPassword())) { // trung pass

									// tao hander cho user
									Handler newHandler = client;
									newHandler.setSocket(socket); // set socket dang nhap
									newHandler.setIsLoggedIn(true); // cap nhat trang thai onlibe

									// thong bao dang nhap thanh cong
									dos.writeUTF("Log in successful");
									dos.flush();

									// tao luong giao thiep
									Thread t = new Thread(newHandler);
									t.start();

									// gui thong bao cho cac user
									updateOnlineUsers();
								} else {
									dos.writeUTF("Password is not correct");
									dos.flush();
								}
								break;
							}
						}

					} else {
						dos.writeUTF("This username is not exist");
						dos.flush();
					}
				}

			}

		} catch (Exception ex) {
			System.err.println(ex);
		} finally {
			if (s != null) {
				s.close();
			}
		}
	}

	// kierm tra ten nguoi dung co ton tai hay khong
	public boolean isExisted(String name) {
		for (Handler client : clients) {
			if (client.getUsername().equals(name)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * gui danh danh sach cac user on off cho cac user dang truc tuyen cap nhat khi
	 * cac user on hoac off
	 */
	public static void updateOnlineUsers() {
		String message = " ";
		for (Handler client : clients) {
			if (client.getIsLoggedIn() == true) {
				message += ",";
				message += client.getUsername();
				
			}
		}
		for (Handler client : clients) {
			if (client.getIsLoggedIn() == true) {
				try {
					client.getDos().writeUTF("Online users");
					client.getDos().writeUTF(message);
					client.getDos().flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}

// luong rieng de tao tiep voi user-client
class Handler implements Runnable {
	// dong bo object
	private Object lock;

	private Socket socket;
	private DataInputStream dis;
	private DataOutputStream dos;
	private String username;
	private String password;
	private boolean isLoggedIn;
	// Mang chua danh sach nhom
	private HashSet<String> topicSet = new HashSet<>();

	// kiem tra co phai la thanh viên cua nhom hay khong
	private boolean isMember(String name) {
		return topicSet.contains(name);
	}
	// them nhom vao danh sach nhom
	public void addTopic(String TopicName) {
		topicSet.add(TopicName);
	}

	public Handler(Socket socket, String username, String password, boolean isLoggedIn, Object lock)
			throws IOException {
		this.socket = socket;
		this.username = username;
		this.password = password;
		this.dis = new DataInputStream(socket.getInputStream());
		this.dos = new DataOutputStream(socket.getOutputStream());
		this.isLoggedIn = isLoggedIn;
		this.lock = lock;
	}

	public Handler(String username, String password, boolean isLoggedIn, Object lock) {
		this.username = username;
		this.password = password;
		this.isLoggedIn = isLoggedIn;
		this.lock = lock;
	}

	public void setIsLoggedIn(boolean IsLoggedIn) {
		this.isLoggedIn = IsLoggedIn;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
		try {
			this.dis = new DataInputStream(socket.getInputStream());
			this.dos = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * dong ket noi socket voi client
	 */
	public void closeSocket() {
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean getIsLoggedIn() {
		return this.isLoggedIn;
	}

	public String getUsername() {
		return this.username;
	}

	public String getPassword() {
		return this.password;
	}

	public DataOutputStream getDos() {
		return this.dos;
	}

	@Override
	public void run() {

		while (true) {
			try {
				String message = null;

				// doc yeu cau tu user
				message = dis.readUTF();

				// yeu cau dang xuat
				if (message.equals("Log out")) {

					// thong bao
					dos.writeUTF("Safe to leave");
					dos.flush();

					// dong socket va chuyen stt ve off
					socket.close();
					this.isLoggedIn = false;

					// cap nhan online
					Server.updateOnlineUsers();
					break;
				}

				// Yeu cai gui tin nhan tu client nay den client khac
				else if (message.equals("Text")) {
					String receiver = dis.readUTF();
					String content = dis.readUTF();
					if (receiver.charAt(0) == '#') {  // neu la tin nhan gui cho nhom
						for (Handler client : Server.clients) {
							if (client.isMember(receiver)) {
								if (!(client.getUsername() == this.username)) {
									synchronized (lock) {
										client.getDos().writeUTF("Text");
										client.getDos().writeUTF(this.username);
										client.getDos().writeUTF(content);
										client.getDos().writeUTF(receiver);
										client.getDos().flush();
									}
								}
							}
						}
					} else {  // tin nhan rieng gui cho user
						for (Handler client : Server.clients) {
							if (client.getUsername().equals(receiver)) {
								synchronized (lock) {
									client.getDos().writeUTF("Text");
									client.getDos().writeUTF(this.username);
									client.getDos().writeUTF(content);
									client.getDos().writeUTF(this.username);
									client.getDos().flush();
									break;
								}
							}
						}

					}
				}
				// Tao group Chat
				else if (message.equals("CreatGroup")) {
					String receiver = dis.readUTF();
					String topicName = dis.readUTF();
					String[] tokens = receiver.split(" ");
					for (Handler client : Server.clients) {
						for (String s : tokens) {
							if (client.getUsername().equals(s)) {
								synchronized (lock) {
									client.addTopic(topicName);
									client.getDos().writeUTF("CreatGroup");
									client.getDos().writeUTF(topicName);
									client.getDos().flush();
									
								}
							}
						}
					}
				}

				// yeu cau gui tin nhan co emoji
				else if (message.equals("Emoji")) {
					String receiver = dis.readUTF();
					String emoji = dis.readUTF();
					if (receiver.charAt(0) == '#') {
						for (Handler client : Server.clients) {
							if (client.isMember(receiver)) {
								if (!(client.getUsername() == this.username)) {
									synchronized (lock) {
										client.getDos().writeUTF("Emoji");
										client.getDos().writeUTF(this.username);
										client.getDos().writeUTF(emoji);
										client.getDos().writeUTF(receiver);
										client.getDos().flush();
									}
								}
							}
						}
					} else {
						for (Handler client : Server.clients) {
							if (client.getUsername().equals(receiver)) {
								synchronized (lock) {
									client.getDos().writeUTF("Emoji");
									client.getDos().writeUTF(this.username);
									client.getDos().writeUTF(emoji);
									client.getDos().writeUTF(this.username);
									client.getDos().flush();
									break;
								}
							}
						}
					}
				}

				// yeu cau gui file
				else if (message.equals("File")) {

					
					String receiver = dis.readUTF();
					String filename = dis.readUTF();
					int size = Integer.parseInt(dis.readUTF());
					int bufferSize = 2048;
					byte[] buffer = new byte[bufferSize];
					if (receiver.charAt(0) == '#') {
						for (Handler client : Server.clients) {
							if (client.isMember(receiver)) {
								if (!(client.getUsername() == this.username)) {
									synchronized (lock) {
										client.getDos().writeUTF("File");
										client.getDos().writeUTF(this.username);
										client.getDos().writeUTF(filename);
										client.getDos().writeUTF(receiver);
										client.getDos().writeUTF(String.valueOf(size));
										while (size > 0) {
											//gui file theo tung buffered
											dis.read(buffer, 0, Math.min(size, bufferSize));
											client.getDos().write(buffer, 0, Math.min(size, bufferSize));
											size -= bufferSize;
										}
										client.getDos().flush();
									}
								}
							}
						}
					} else {
						for (Handler client : Server.clients) {
							if (client.getUsername().equals(receiver)) {
								synchronized (lock) {
									client.getDos().writeUTF("File");
									client.getDos().writeUTF(this.username);
									client.getDos().writeUTF(filename);
									client.getDos().writeUTF(this.username);
									client.getDos().writeUTF(String.valueOf(size));
									while (size > 0) {
										dis.read(buffer, 0, Math.min(size, bufferSize));
										client.getDos().write(buffer, 0, Math.min(size, bufferSize));
										size -= bufferSize;
									}
									client.getDos().flush();
									break;
								}
							}
						}
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}
}