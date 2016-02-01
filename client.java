import java.io.*;
import java.net.*;

public class client {
	/* variables */
	private static final int maxBsize = 65535; // maximum byte allowed for java
	private static int n_port;
	private static int r_port;
	private static InetAddress server_address = null;
	private static Socket TCPsocket = null;
	private static DatagramSocket UDPsocket = null;
	private static BufferedReader inFromServer = null;
	private static DataOutputStream outToServer = null;
	private static final String initRequest = "13";
	private static byte[] dataSend;
	private static byte[] dataReceive;
	private static DatagramPacket packetSend;
	private static DatagramPacket packetReceive;
	private static String msg;
	
	/* main */
	public static void main(String argv[]) throws IOException{
		try {
			// arguments check & initiation
			// number of arguments can be only 2 or 3
			if(argv.length !=2 && argv.length !=3){
				System.err.println("USAGE: client <server_address> <n_port> <msg>");
				System.exit(1);
			}
			server_address = InetAddress.getByName(argv[0]);
			n_port = Integer.parseInt(argv[1]);
			
			// if there's no message argument, treat as ""
			if(argv.length == 2) {
				msg = "";
			}
			else {
				msg = argv[2];
			}
		}
		catch(UnknownHostException e){
			System.err.println("ERROR[CLIENT]: "+e.getMessage());
			System.exit(1);
		}
		
		// Stage 1. Negotiation using TCP sockets
		negotiation();
		
		// Stage 2. Transaction using UDP sockets
		transaction();
		
	}
	
	/* negotiation */
	public static void negotiation() throws IOException {
		try {
			// creates a TCP connection
			TCPsocket = new Socket(server_address, n_port);
			
			// sends a request to server
			outToServer = new DataOutputStream(TCPsocket.getOutputStream());
			outToServer.writeBytes(initRequest+'\n');
			
			// gets a random port number from server
			inFromServer = new BufferedReader(new InputStreamReader(TCPsocket.getInputStream()));
			r_port = Integer.parseInt(inFromServer.readLine());
		}
		catch(IOException e) {
			System.err.println("ERROR[CLIENT]: "+e.getMessage());
			System.exit(1);
		}
		finally{
			TCPsocket.close();
			outToServer.close();
			inFromServer.close();
		}
	}
	
	/* transaction */
	public static void transaction() {
		dataSend = new byte[maxBsize];
		dataReceive = new byte[maxBsize];
		try {
			// creates UDP connection
			UDPsocket = new DatagramSocket();
			
			// creates packet for sending
			dataSend = msg.getBytes();
			packetSend = new DatagramPacket(dataSend, dataSend.length, server_address, r_port);
			
			// sends message to server
			UDPsocket.send(packetSend);
			
			// creates packet for receiving
			packetReceive = new DatagramPacket(dataReceive, dataReceive.length);
			
			// receives reversed message from server 
			UDPsocket.receive(packetReceive);
			
			// print the result
			System.out.println("reverse!: "+ new String(packetReceive.getData()));			
		}
		catch(IOException e){
			System.err.println("ERROR[CLIENT]: "+e.getMessage());
			System.exit(1);
		}
		finally{
			UDPsocket.close();
		}
	}
}
