/*	Chuong trinh AutoBackup
 *  Tac gia: Truong Tuan Kiet
 *  Email: kiettel@gmail.com
 *  Mobile: 091.123.0123 hoac 098.68.68.498
 *  Ngay bat dau thuc hien: 08/06/2013
 */

package com.kiettel.autobackup;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.Logger;


public class Send
{
	private static final Logger log4j = Logger.getLogger(Send.class);
	BufferedOutputStream out;
	private Socket socket;
	private SendReceive sendReceive;
	
	public Send(SendReceive sendReceive, Socket socket)
	{	
		PropertyConfigurator.configure("log4j.properties");
		this.sendReceive = sendReceive;
		this.socket = socket;
	}
	
	void sendData(String send)
	{	
		if(sendReceive.connectDevice.isSend)
		{
			try
			{
				if(send.isEmpty())
				{
					synchronized(out)
					{
						out.write(("\r\n").getBytes());
					}
				}
				else
				{
					synchronized(out)
					{
						out.write((send+"\r\n").getBytes());
					}
				}
				
				out.flush();
			}
			catch(Exception e)
			{	
				disconnect();
				log4j.error("", e);
			}	
		}
		
		sendReceive.isSend = false;
	}
	
	void start()
	{
		out = null;
		sendReceive.isSend = false;
		
		try 
		{
			out = new BufferedOutputStream(socket.getOutputStream());
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			log4j.error("", e);
		}
	}
	
	void disconnect()
	{
		sendReceive.isSend = false;
		
		try
		{
			if(out!=null)
			{
				out.close();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			log4j.error("", e);
		}	
		
		out = null;
	}
}