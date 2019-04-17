/*	Chuong trinh AutoBackup
 *  Tac gia: Truong Tuan Kiet
 *  Email: kiettel@gmail.com
 *  Mobile: 091.123.0123 hoac 098.68.68.498
 *  Ngay bat dau thuc hien: 08/06/2013
 */

package com.kiettel.autobackup;
import java.io.IOException;
import java.net.Socket;

import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.Logger;


public class SendReceive
{
	private static final Logger log4j = Logger.getLogger(SendReceive.class);
	private Socket socket;
	Send send;
	Receive receive;
	TelnetProtocol protocol;
	boolean isRun = false,
			isSend = false,
			isConnected = false;
	ConnectDevice connectDevice;
    
	public SendReceive(ConnectDevice connectDevice)
	{
		PropertyConfigurator.configure("log4j.properties");
		this.connectDevice = connectDevice;
	}
	
	void connect(String hostname, int port)
	{	
		try
	    {
			socket = null;
			protocol = null;
			send = null;
			receive = null;
			
			if(socket==null)
			{
				socket = new Socket(hostname, port);  
				isConnected = true;  
			}
			   
			if(protocol==null)
			{
				protocol = new TelnetProtocol(this);
			}          
			
			if(send==null)
			{
				send = new Send(this, socket);
				send.start();
			}
			
			if(receive==null)
			{
				receive = new Receive(this, socket);
				receive.start();
			} 
	    }
	    catch (Exception e)
	    {
	    	isConnected = false;
	    	e.printStackTrace();
	    	log4j.error("", e);
	    }			
	}
	
	void disconnect()
	{
		isConnected = false;
		
		if(send!=null)
		{
			send.disconnect();
		}
		
		send = null;
		
		if(receive!=null)
		{
			receive.disconnect();
		}
		
		receive = null;
		
		if(socket!=null)
		{
			try 
			{
				socket.close();
			} 
			catch(IOException e) 
			{
				e.printStackTrace();
				log4j.error("", e);
			}	
		}
		
		socket = null;
	}
}