package server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

public class MainServer {
	public static String SUCCESS = "OK";
	public static String EXIST = "Sudah ada tabel dengan nama tersebut";
	public static String NOT_FOUND = "Tabel tidak ditemukan";
	public static String FALSE = "input yang dimasukkan salah";
	public static ArrayList<Server> servers = new ArrayList<>();
	public static Database db;
	public static int OriginPort = 50000;
	public static HashMap<String, Integer> mapKeyHash = new HashMap();

	public static int hashDitanya(String key){
		Iterator it = mapKeyHash.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			if(((String)pairs.getKey()).equals(key)){
				return (Integer) pairs.getValue();
			}
		}
		return -1;
	}
	
	public static void loadDb(String fileDb) {
		File file = new File(fileDb);
		try {
			Scanner sc = new Scanner(file);
			while (sc.hasNext()) {
				String tabName = sc.next();
				db.createTable(tabName);
				String content = sc.next();
				while (!content.equals("--")) {
					String key = content;
					String value = sc.next();
					int timestamp = sc.nextInt();
					Data data = new Data(key, value, timestamp);
					db.insertData(tabName, data);
					content = sc.next();
				}
				// System.out.println(db.showTable(tabName));
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("database loaded");
	}

	public static void saveDb(String fileDb) {
		PrintWriter writer;
		try {
			writer = new PrintWriter(fileDb, "UTF-8");
			Iterator it = db.getTables().entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pairs = (Map.Entry) it.next();
				writer.println(pairs.getKey());
				for (int i = 0; i < ((ArrayList<Data>) pairs.getValue()).size(); i++) {
					System.out
							.println(((ArrayList<Data>) pairs.getValue())
									.get(i).key
									+ " "
									+ ((ArrayList<Data>) pairs.getValue())
											.get(i).value
									+ " "
									+ ((ArrayList<Data>) pairs.getValue())
											.get(i).timestamp);
					writer.println(((ArrayList<Data>) pairs.getValue()).get(i).key
							+ " "
							+ ((ArrayList<Data>) pairs.getValue()).get(i).value
							+ " "
							+ ((ArrayList<Data>) pairs.getValue()).get(i).timestamp);
				}
				writer.println("--");
			}
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String processRequest(String req) {
		String split[] = req.split("\\s+");
		if (split[0].isEmpty()) {
			return FALSE;
		} else {
			if (split[0].equals("create") && split[1].equals("table")
					&& split.length == 3) {
				// Create new table
				String tableName = split[2];
				int result = db.createTable(tableName);
				if (result == 1) {
					return SUCCESS;
				} else {
					return EXIST;
				}
			} else if (split[0].equals("insert") && split.length == 4) {
				// Insert row
				String tableName = split[1];
				String key = split[2];
				String value = split[3];
				if (db.getTables().get(tableName) == null) {
					return NOT_FOUND;
				} else {
					Data data = new Data(key, value, -1);
					db.insertData(tableName, data);
					return SUCCESS;
				}
			} else if (split[0].equals("display")) {
				// Display table
				String tableName = split[1];
				if (db.getTables().get(tableName) == null) {
					return NOT_FOUND;
				} else {
					// Show table
					return db.showTable(tableName);
				}
			} else {
				return FALSE;
			}
		}
	}

	public static boolean createNewServer(int portId) {
		final Server server = new Server();
		server.start();
		try {
			server.bind(OriginPort+portId, OriginPort+portId+2000);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		server.addListener(new Listener() {
			public void received(Connection connection, Object object) {
				if (object instanceof String) {
					String request = (String) object;
					if (request.equals("quit")) {
						String response = "quit";
						connection.sendTCP(response);
						// saveDb("Database.txt");
						server.stop();
						System.out.println("Server stopped");
					} else {
						String response = ""+hashDitanya(request);
						System.out.println(response);
						connection.sendTCP(response);
					}
				}
			}
		});
		servers.add(server);
		System.out.println("Server " + portId + " started");
		return true;
	}

	public static void main(String[] args) {
		mapKeyHash.put("xx.hali", 1);
		createNewServer(1);
	}
	/*
	 * public static void main(String[] args) { db = new Database(); final
	 * Server server = new Server(); server.start(); try { server.bind(54555,
	 * 54777); } catch (IOException e) { e.printStackTrace(); }
	 * server.addListener(new Listener() { public void received(Connection
	 * connection, Object object) { if (object instanceof String) { String
	 * request = (String) object; if (request.equals("quit")) { String response
	 * = "quit"; connection.sendTCP(response); //saveDb("Database.txt");
	 * server.stop(); System.out.println("Server stopped"); } else { String
	 * response = processRequest(request); System.out.println(response);
	 * connection.sendTCP(response); } } } });
	 * System.out.println("Server started"); //loadDb("Database.txt"); }
	 */
}
