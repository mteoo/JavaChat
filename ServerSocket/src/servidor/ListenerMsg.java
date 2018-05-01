package servidor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ListenerMsg implements Runnable {
	private Socket socket;
	private BufferedReader in;
	private PrintStream out;
	
	
	private boolean inicializado;
	private boolean executando;
	
	private Thread thread;
	private Server server;
	
	public ListenerMsg(Server server, Socket socket) throws Exception{
		this.socket = socket;
		this.server = server;
		inicializado = false;
		executando = false;
		
		open();
		
	}
	
	private void open() throws Exception {
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintStream(socket.getOutputStream());
		} catch (Exception e) {
			close();
			throw e;
		}
		inicializado = true;
	}
	
	private void close() {
		if(in != null) {
			try {
				in.close();
			} catch (Exception e) {
				System.out.println(e);
			}
		}
		
		if (out != null) {
			try {
				out.close();
			} catch (Exception e) {
				System.out.println(e);
			}
		}
		
		try {
			socket.close();
		} catch (Exception e) {
			System.out.println(e);
		}
		
		in = null;
		out = null;
		socket = null;
		
		inicializado = false;
		executando = false;
		
		thread = null;
		
				
				
		
		
	}
	
	public void start() {
		if(!inicializado || executando) {
			return;
		}
		
		executando = true;
		thread = new Thread(this);
		thread.start();
	}
	
	public void stop() throws Exception {
		executando = false;
		if(thread != null) {
			thread.join();
		}
	}
	
	
	@Override
	public void run() {		
		while (executando) {
			try {
				socket.setSoTimeout(2500);;
				
				String msg = in.readLine();
				
				if(msg == null) {
					break;
				}
				 
				System.out.println("Mensagem recebida do cliente ["
						+socket.getInetAddress().getHostName() 
						+" port: " + socket.getPort()
						+ " ]: "
						+ "Mensagem: "
						+ msg);
				
				if("FIM".equalsIgnoreCase(msg)) {
					break;
				}
				
				server.sendToAll(this, msg);
				
			} catch  (SocketTimeoutException ignore) {
				
			} catch (Exception e) {
				System.out.println(e);
				break;
			}
		}
		
		System.out.println("Encerrando conexão.");
		close();
		
		
	}

	public void sendMessage(String mensagem) {
		out.println(mensagem);		
	}
	
}

















