package servidor;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class Server implements Runnable {

	private ServerSocket server;

	private List<ListenerMsg> listeners;
	private boolean inicializado;
	private boolean executando;
	private Thread thread;

	public Server(int porta) throws Exception {
		listeners = new ArrayList<ListenerMsg>();

		inicializado = false;
		executando = false;

		open(porta);
	}

	private void open(int porta) throws Exception {
		server = new ServerSocket(porta);
		inicializado = true;
	}

	private void close() {
		for (ListenerMsg listener : listeners) {
			try {
				listener.stop();
			} catch (Exception e) {
				System.out.println(e);
			}
		}

		try {
			server.close();
		} catch (Exception e) {
			System.out.println(e);
		}

		server = null;
		inicializado = false;
		executando = false;
		thread = null;
	}

	public void start() {
		if (!inicializado || executando) {
			return;
		}

		executando = true;
		thread = new Thread(this);
		thread.start();
	}

	public void stop() throws Exception {
		executando = false;
		if (thread != null) {
			thread.join();
		}
	}

	@Override
	public void run() {

		System.out.println("Aguardando conexão...");
		while (executando) {
			try {
				server.setSoTimeout(2500);

				Socket socket = server.accept();

				System.out.println("Conexão estabelecida.");

				ListenerMsg listener = new ListenerMsg(this, socket);
				listener.start();

				listeners.add(listener);
			} catch (SocketTimeoutException ignore) {

			} catch (Exception e) {
				System.out.println(e);
			}
		}

		close();
	}

	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {
		System.out.println("Iniciando servidor...");

		Server server = new Server(2525);
		server.start();

		System.out.println("Pressione ENTER para encerrar o servidor.");
		new Scanner(System.in).nextLine();

		System.out.println("Encerrando conexão...");
		server.stop();

	}

	public void sendToAll(ListenerMsg origem, String mensagem) throws Exception {
		Iterator<ListenerMsg> i = listeners.iterator();
		
		while(i.hasNext()) {
			ListenerMsg listener = i.next();
			
			if (listener != origem) {
				try {
					listener.sendMessage(mensagem);
				} catch (Exception e) {
					listener.stop();
					i.remove();
				}
			}
		}
		
	}

}
