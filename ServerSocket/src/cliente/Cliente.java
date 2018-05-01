package cliente;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Scanner;

public class Cliente implements Runnable{
	
	private Socket socket;
	
	private BufferedReader in;
	private PrintStream out;
	
	private boolean inicializado;
	private boolean executando;
	
	private Thread thread;
	
	public Cliente(String endereco, int porta) throws Exception{
		inicializado = false;
		executando = false;
		
		open(endereco, porta);
	}
	
	private void open(String endereco, int porta) throws Exception {
		try {
			socket = new Socket(endereco, porta);
			
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintStream(socket.getOutputStream());
			
			inicializado = true;
		} catch (Exception e) {
			System.out.println(e);
			close();
			throw e;
		}
	}
	
	private void close() {
		if (in != null) {
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
		
		if (socket != null) {
			try {
				socket.close();
			} catch (Exception e) {
				System.out.println(e);
			}
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
			
		}
	}
	
	@Override
	public void run() {
		while(executando) {
			try {
				socket.setSoTimeout(2500);
				
				String mensagem = in.readLine();
				
				System.out.println("Mensagem enviada pelo server: "
						+ mensagem);
			} catch (SocketTimeoutException ignore) {
				
			} catch (Exception e) {
				System.out.println(e);
				break;
			}
			
		}
		close();
		
	}
	
	public void send(String mensagem) {
		out.println(mensagem);
	}
	
	public boolean isExecutando() {
		return executando;
	}
	
	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception{
		System.out.println("Iniciando cliente...");
		System.out.println("Conectando com o servidor...");
		Cliente cliente = new Cliente("localhost", 2525);
		System.out.println("Conectado!");
		cliente.start();		
		
		Scanner scanner = new Scanner(System.in);
		
		while (true) {
			System.out.println("Digite uma mensagem: ");
			String mensagem = scanner.nextLine();
			
			if(!cliente.isExecutando()) {
				break;
			}
			cliente.send(mensagem);
			
			if("FIM".equalsIgnoreCase(mensagem)) {
				break;				
			}
			
		}
		System.out.println("Conexão encerrada!");
		cliente.stop();
	}
}


















