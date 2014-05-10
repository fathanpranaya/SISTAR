package client;

import java.io.IOException;
import java.util.Scanner;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class MainClient {
	public static Client client;
	
	public static String command; // String yang menampung perintah dari client
	public static String response; // String kembalian dari server

	public static void main(String[] args) {
		// Inisialisiai
		client = new Client();
		command = "";
		response = "";

		new Thread(client).start();

		final Scanner reader = new Scanner(System.in);
		
		System.out.print("Masukkan ip tujuan : ");
		String ip = reader.nextLine();
		System.out.print("Masukkan port: ");
		int port = reader.nextInt();
		try {
			// Connect ke server
			client.connect(100000, ip, port, port+2000);
			System.out.println("Terhubung ke server "+port);
			// Listener untuk menerima respon server
			client.addListener(new Listener() {
				public void received(Connection connection, Object object) {
					if (object instanceof String) {
						response = (String) object;
						if (!response.equals("quit")) {
							if (!response.equals("OK")) {
								System.out.println(response);
							}
						} else {
							// Menutup aplikasi
							reader.close();
							client.stop();
							System.out.println("Client closed");
						}
					}
				}
			});
			
			// Meminta input dari user
			while (!response.equals("quit")){
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					System.out.println("Sleep gagal");
				}
				System.out.print("DB> ");
				command = reader.nextLine();
				String request = command;
				client.sendTCP(request);
			}
		} catch (IOException e) {
			System.out.println("Tidak dapat menghubungi server");
			client.stop();
		}
	}

}
