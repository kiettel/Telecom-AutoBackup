/*	Chuong trinh AutoBackup
 *  Tac gia: Truong Tuan Kiet
 *  Email: kiettel@gmail.com
 *  Mobile: 091.123.0123 hoac 098.68.68.498
 *  Ngay bat dau thuc hien: 08/06/2013
 */

package com.kiettel.autobackup;

import java.io.File;

import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.Logger;


public class RenameFile implements Runnable
{
	private static final Logger log4j = Logger.getLogger(RenameFile.class);
	private Thread thread;
	private boolean isRun = false;
	private File fileOld,
	             fileNew;
	String sOldFileName = "",
		   sNewFileName = "";
	
	public RenameFile()
	{
		PropertyConfigurator.configure("log4j.properties");
	}
	
	public void run()
	{
		while(isRun)
		{
			try
			{
				fileRename(sOldFileName, sNewFileName);
				sleep(100);
			}
			catch(Exception e)
			{
				e.printStackTrace();
				log4j.error("", e);
			}	
		}
	}
	
	void fileRename(String oldName, String newName)
	{
		fileOld = null;
		fileNew = null;
		
		if(fileOld==null)
		{
			fileOld = new File(oldName);
		}
		
		if(fileOld!=null)
		{
			if(fileOld.exists())
			{
				if(fileNew==null)
				{
					fileNew = new File(newName);
				}
				
				if(fileNew!=null)
				{
					fileOld.renameTo(fileNew);
				}
			}
		}
	}
	
	private void sleep(int time)
	{
		try
		{
			Thread.sleep(time);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			log4j.error("", e);
		}
	}
	
	void start()
	{
		thread = null;
		isRun = true;
		
		if(thread==null)
		{
			thread = new Thread(this);
		}
		
		if(thread!=null)
		{
			thread.start();
		}
	}

	void stop()
	{
		isRun = false;
		
		try
		{
			if(thread!=null)
			{
				thread = null; 
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			log4j.error("", e);
		}
	}
}