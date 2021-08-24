package io.boodskap.iot.tutorial.input.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.UUID;

public class UdpHeartbeatClient implements Runnable {
	
	private static final UdpHeartbeatClient instance = new UdpHeartbeatClient();
	
	static {
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			
			@Override
			public void run() {
				instance.stop();
			}
		}));
	}
	
	private Thread _thread;
	private boolean _stopped;
	
	private String id = UUID.randomUUID().toString();
	private String host = "boodskap.xyz";
	private int port = 5656;
	private long sleep = 30000;
	
	private UdpHeartbeatClient() {
	}
	
	public static final UdpHeartbeatClient get() {
		return instance;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public long getSleep() {
		return sleep;
	}

	public void setSleep(long sleep) {
		this.sleep = sleep;
	}

	public void start() {
		
		if(null != _thread) return;
		
		_stopped = false;
		
		_thread = new Thread(this);
		
		_thread.start();
	}
	
	public void stop() {
		
		if(null == _thread) return;
		
		_stopped = true;
		
		_thread.interrupt();
	}
	
	public void run() {
		
		
		DatagramSocket _socket = null;
		
		try {
			
			_socket = new DatagramSocket();
			
			while(!Thread.currentThread().isInterrupted()) {
				
				String data = String.format("%s,%d,%d", id, System.currentTimeMillis(), Runtime.getRuntime().freeMemory());
				
				byte[] bdata = data.getBytes();
				
				DatagramPacket packet = new DatagramPacket(bdata, bdata.length, new InetSocketAddress(host, port));
				
				_socket.send(packet);
				
				System.out.format("Heartbeat sent to %s:%d\n", host, port);
				
				Thread.sleep(sleep);
			}
			
		}catch(Exception ex) {
			if(!_stopped) {
				ex.printStackTrace();
			}
		}finally {
			
		}
	}
	
	/**
	 * args[0] -> Unique ID
	 * args[1] -> UDP Server Host / IP
	 * args[2] -> UDP Server Port
	 * args[3] -> Sleep Interval
	 * @param args
	 */
	public static void main(String[] args) {
		
		UdpHeartbeatClient client = UdpHeartbeatClient.get();
		
		if(args.length >= 1) {
			client.setId(args[0]);
		}
		
		if(args.length >= 2) {
			client.setHost(args[1]);
		}
		
		if(args.length >= 3) {
			client.setPort(Integer.valueOf(args[2]));
		}
		
		if(args.length >= 4) {
			client.setSleep(Long.valueOf(args[3]));
		}
		
		client.start();
		
	}
}
