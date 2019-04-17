/*	Chuong trinh AutoBackup
 *  Tac gia: Truong Tuan Kiet
 *  Email: kiettel@gmail.com
 *  Mobile: 091.123.0123 hoac 098.68.68.498
 *  Ngay bat dau thuc hien: 08/06/2013
 */

package com.kiettel.autobackup;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.Logger;


public class Receive implements Runnable
{
	private static final Logger log4j = Logger.getLogger(Receive.class);
	BufferedInputStream in;
	private Thread thread;
	private SendReceive sendReceive;
	private String receive = "",
		           receive2 = "",
		           sToWait1 = "",
		           sToWait2 = "";
	boolean isWaitForString = false,
			haveString = false,
			isTimeOut = false,
			isWait1 = false;
	private int waitTimedOut = 0;
	private StringBuffer sb;
	private Socket socket;
	private Timer timer;
	private Task task;
	
	public Receive(SendReceive sendReceive, Socket socket)
	{
		PropertyConfigurator.configure("log4j.properties");
		this.sendReceive = sendReceive;
		this.socket = socket;
	}
	
	public void run()
	{
		getData();	
		disconnect();
	}
	
	private void getData()
	{
		try 
		{
			int data = 0;
			
			while((data=in.read())!=-1&&
				   sendReceive.isConnected&&
				   ConnectDevice.isBackup)
			{
				if(data>127)
			    {
			    	if(data==255)
			    	{
			    		sendReceive.protocol.getCommandName();
			    	}	
			    	else if(data==247) // EC
			    	{
			    		//sr.configBackup.m.taOutput.setOutput(String.valueOf((char)i).replaceAll(String.valueOf((char)i), ""));
			    	}
			    }
			    else
			    {
			    	receive = String.valueOf((char)data);
			    	System.out.print(receive);
			    	sb.append(receive);
		    	
			    	sendReceive.connectDevice.backup.frmMainMenu.taOutput.append(receive);
			    	sendReceive.connectDevice.backup.frmMainMenu.taOutput.setCaretPosition(sendReceive.connectDevice.backup.frmMainMenu.taOutput.getText().length());
			    	
			    	if(sendReceive.connectDevice.sReceivedBuffer!=null)
			    	{
			    		sendReceive.connectDevice.sReceivedBuffer.append(receive);
			    	}
			    	
			    	if(isWaitForString)
			    	{
			    		if(isWait1)
			    		{
			    			waitString(sToWait1);
			    		}
			    		else
			    		{
			    			waitString(sToWait1, sToWait2);
			    		}
			    	}
			    }
			}
		} 
		catch(IOException e) 
		{		
			e.printStackTrace();
			//log4j.error("", e);
			notifyFailed();
			disconnect();
		}
	}

	private void waitStringProcess()
	{
		try 
		{	
			int avai = in.available();
			//System.out.println("Stream Availabe: "+avai);	
			
			if(avai==0)
			{
				int x = sb.length();				

				if(x>5)
		    	{
		    		receive2 = sb.substring(x-6, x);
		    		//System.out.println("String received: "+receive2);
		    		
		    		int index1 = -1,
		    			index2 = -1;
		    		
		    		if(isWait1)
		    		{
		    			index1 = receive2.indexOf(sToWait1);
		    		}
		    		else
		    		{
		    			index1 = receive2.indexOf(sToWait1);
		    			index2 = receive2.indexOf(sToWait2);
		    		}
		    		
		    		if((index1>-1)||
		    		   (index2>-1))
		    		{
		    			sb.delete(0, sb.length());
		    			//System.out.println("String Found: "+isWait1);
		    					    					    			
		    			cancelTask();
		    			isWaitForString = false;
		    			isTimeOut = false;
		    			haveString = true;
		    			
		    			noticeAll();
		    		}
		    		else
		    		{
		    			haveString = false;	
		    		}
		    	}
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			log4j.error("", e);
			notifyFailed();
		}
	}
	
	private void waitString(String sWait)
	{
		waitStringProcess();
	}
	
	private void waitString(String sWait1, String sWait2)
	{
		waitStringProcess();
	}
	
	void waitForString(String sToWait1,
			           int waitTimedOut)
	{	
		//System.out.println("Waiting for: "+sToWait);
		isTimeOut = false;
		haveString = false;
		this.sToWait1 = sToWait1;
		this.waitTimedOut = waitTimedOut*1000;

		scheduleTask();
	}
	
	void waitForString(String sToWait1,
			           String sToWait2,
			           int waitTimedOut)
	{	
		//System.out.println("Waiting for: "+sToWait);
		isTimeOut = false;
		haveString = false;
		this.sToWait1 = sToWait1;
		this.sToWait2 = sToWait2;
		this.waitTimedOut = waitTimedOut*1000;
		
		scheduleTask();
	}

	class Task extends TimerTask
	{
		public void run() 
		{
			cancelTask();
			//System.out.println("Timed Out !");
			
			isTimeOut = true;
			haveString = false;
			isWaitForString = false;
			
			noticeAll();
		}
	}
	
	private void scheduleTask()
	{
		cancelTask();
		
		if(timer==null)
		{
			timer = new Timer();
		}
		
		if(task==null)
		{
			task = new Task();
		}

		try
		{
			if(timer!=null&&task!=null)
			{
				isWaitForString = true;
				timer.schedule(task, waitTimedOut);
				//System.out.println("ScheduleTask.java: Scheduled to wait for string.");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			log4j.error("", e);
			notifyFailed();
		}
	}
	
	private void cancelTask()
	{
		if(timer!=null)
		{
			try
			{
				timer.cancel();
			}
			catch(Exception e)
			{
				log4j.error("", e);
				notifyFailed();
			}
		}
		
		timer = null;
		
		if(task!=null)
		{
			task = null;
		}
	}
	
	private void notifyFailed()
	{
		isWaitForString = false;
		isTimeOut = true;
		haveString = false;
		noticeAll();
		disconnect();
	}
	
	private void noticeAll()
	{
		try
		{
			synchronized(sendReceive.connectDevice)
			{
				sendReceive.connectDevice.notifyAll();
			}
		}
		catch(Exception e)
		{
			log4j.error("", e);
			e.printStackTrace();
		}
	}
	
	void start()
	{	
		thread = null;
		sb = null;
		in = null;
		sb = new StringBuffer();
		
		if(thread==null)
		{
			thread = new Thread(this);
		}

		try 
		{
			in = new BufferedInputStream(socket.getInputStream());
		} 
		catch (IOException e) 
		{
			log4j.error("", e);
			e.printStackTrace();
		}
		
		thread.start();
	}
	
	void disconnect()
	{
		try
		{
			if(in!=null)
			{
				in.close();
			}	
		}
		catch(Exception e)
		{
			log4j.error("", e);
			e.printStackTrace();
		}	
		
		in = null;
	
		try
		{
			if(thread!=null)
			{
				thread = null; 
			}
		}
		catch(Exception e)
		{
			log4j.error("", e);
			e.printStackTrace();
		}	
				
		try
		{
			if(!isWaitForString)
			{
				cancelTask();
			}
		}
		catch(Exception e)
		{
			log4j.error("", e);
			e.printStackTrace();
		}	
				
		sb = null;
		sendReceive.isConnected = false;
	}
}