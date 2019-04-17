/*	Chuong trinh AutoBackup
 *  Tac gia: Truong Tuan Kiet
 *  Email: kiettel@gmail.com
 *  Mobile: 091.123.0123 hoac 098.68.68.498
 *  Ngay bat dau thuc hien: 08/06/2013
 */

package com.kiettel.autobackup;

import java.awt.Color;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Vector;
import javax.swing.JCheckBox;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.Logger;


public class Backup implements Runnable
{
	private static final Logger log4j = Logger.getLogger(Backup.class);
	private String tempH = "",
		           tempM = "",
		           tempS = "";
	private int hh = 0,
				mm = 0,
				ss = 0,
				hh2 = 0,
				mm2 = 0,
				ss2 = 0;
	boolean isRun = false,
			isStartFinished = false;
	private Thread thread;
	private File fileName;
	Vector<String[]> vtFileName;
	FrmMainMenu frmMainMenu;
	ConnectDevice[] connectDevice;
	RenameFile renameFile;
	
	public Backup(FrmMainMenu frmMainMenu)
	{
		PropertyConfigurator.configure("log4j.properties");
		this.frmMainMenu = frmMainMenu;
	}
	
	public void run()
	{
		while(isRun)
		{
			frmMainMenu.startTFTPServer();
			
			if(!isStartFinished)
			{
				if(frmMainMenu.isAutoBackup)
				{
					scheduleBackup();
				}
				else
				{
					connect();
				}
			}
			else
			{
				checkFile();
			}
	
			sleep(2000);
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
	
	void initConnectDevice()
	{
		vtFileName = null;
		renameFile = null;
		vtFileName = new Vector<String[]>();
		renameFile = new RenameFile();
		
		if(connectDevice!=null)
		{
			for(int i=0; i<connectDevice.length; i++)
			{
				try
				{
					if(connectDevice[i]!=null)
					{
						connectDevice[i].stop();
						connectDevice[i] = null;
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
					log4j.error("", e);
				}
			}
		}
		
		connectDevice = null;
		
		if(frmMainMenu.deviceSelected>0)
		{
			connectDevice = new ConnectDevice[frmMainMenu.deviceSelected];
			
			for(int i=0; i<frmMainMenu.deviceSelected; i++)
			{
				connectDevice[i] = new ConnectDevice(this,
													 GetInput.vtDevice.elementAt(i)[0],
													 GetInput.vtDevice.elementAt(i)[1],
													 GetInput.vtDevice.elementAt(i)[2],
													 GetInput.vtDevice.elementAt(i)[3],
													 GetInput.vtDevice.elementAt(i)[4],
													 GetInput.vtDevice.elementAt(i)[5],
													 GetInput.vtDevice.elementAt(i)[6],
													 GetInput.vtDevice.elementAt(i)[7],
													 GetInput.vtDevice.elementAt(i)[8],
													 GetInput.vtDevice.elementAt(i)[9]);
			}
		}
	}
	
	void connect()
	{
		if(connectDevice.length>0)
		{
			try
			{
				if(connectDevice[0]!=null)
				{
					connectDevice[0].start();
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
				log4j.error("", e);
			}
		}
		
		isStartFinished = true;
	}
	
	void scheduleBackup()
	{	
		Calendar c = Calendar.getInstance();
    	c.getTime();
    	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    	String time = sdf.format(c.getTime());
    	
    	tempH = time.substring(0, 2);
    	tempM = time.substring(3, 5);
    	tempS = time.substring(6);
    	hh = Integer.parseInt(tempH);
    	mm = Integer.parseInt(tempM);
    	ss = Integer.parseInt(tempS);
    	
    	hh2 = Integer.parseInt(frmMainMenu.sH);
    	mm2 = Integer.parseInt(frmMainMenu.sM);
    	ss2 = Integer.parseInt(frmMainMenu.sS);
   
    	if((hh==hh2)&&
    	   (mm==mm2)&&
    	   (ss==ss2))
    	{ 
    		isStartFinished = true;

    		for(int i=0; i<frmMainMenu.deviceNumber; i++)
    		{
    			((JCheckBox)frmMainMenu.vtDevice.elementAt(i)).setEnabled(false);
    			((JCheckBox)frmMainMenu.vtDevice.elementAt(i)).setBackground(Color.WHITE);
    			((JCheckBox)frmMainMenu.vtDevice.elementAt(i)).setForeground(Color.BLACK);
    		}
    		
    		frmMainMenu.taOutput.setText("");
    		frmMainMenu.dispOutput("BACKUP TỰ ĐỘNG: ĐANG BACKUP...");
    		frmMainMenu.lbStatus.setText("TRẠNG THÁI: BACKUP TỰ ĐỘNG, ĐANG BACKUP...");
    		ConnectDevice.taskCount = 0;
    		initConnectDevice();
    		connect();
    	}
	}
	
	private void checkFile()
	{
		fileName = null;
		int fileSize = 0;
		
		fileSize = vtFileName.size();
		
		for(int i=0; i<fileSize; i++)
		{
			try
			{
				fileName = null;
				
				if(fileName==null)
				{	
					fileName = new File(vtFileName.elementAt(i)[1]);
				}

				if(fileName!=null)
				{
					if(fileName.exists())
					{							
						for(int i2=0; i2<frmMainMenu.deviceNumber; i2++)
						{
							if(((JCheckBox)frmMainMenu.vtDevice.elementAt(i2)).getText().equals(vtFileName.elementAt(i)[0]))
							{
								((JCheckBox)frmMainMenu.vtDevice.elementAt(i2)).setBackground(Color.GREEN);
								
								break;
							}
						}
					}
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
				log4j.error("", e);
			}
		}
	}
	
	void sendTRAP()
	{
		fileName = null;
		int fileSize = 0;
		
		fileSize = vtFileName.size();
		
		for(int i=0; i<fileSize; i++)
		{
			System.out.println("Send recovery TRAP:\n");
			
			for(int i2=0; i2<frmMainMenu.deviceNumber; i2++)
			{
				if(((JCheckBox)frmMainMenu.vtDevice.elementAt(i2)).getText().equals(vtFileName.elementAt(i)[0]))
				{
					String sIP = ((JCheckBox)frmMainMenu.vtDevice.elementAt(i2)).getToolTipText();
					String sHostName = ((JCheckBox)frmMainMenu.vtDevice.elementAt(i2)).getText();
					String sInfoToSend = sHostName+"(IP:"+sIP+"): "+"KHONG BACKUP DUOC FILE CAU HINH.";

					for(int st=0; st<GetInput.vtTRAPReceiver.size(); st++)
					{
						try
						{
							frmMainMenu.trapSender.sendTRAP(sIP,
															new int[]{1, 3, 6, 1, 2, 1, 2, 2, 1, 8},
															1,
															new int[]{1, 3, 6, 1, 2, 1, 2, 2, 1, 9},
															sInfoToSend,
															GetInput.vtTRAPReceiver.elementAt(st),
															162,
															"public");
						}
						catch(Exception e)
						{
							e.printStackTrace();
							log4j.error("", e);
							frmMainMenu.dispOutput("GỬI TRAP ĐỂ XÓA CẢNH BÁO BỊ LỖI:\n"+e.toString());
						}
						
						sleep(1000);
					}
					
					break;
				}
			}
		}

		sleep(1000);
		
		for(int i=0; i<fileSize; i++)
		{
			try
			{
				fileName = null;
				
				if(fileName==null)
				{	
					fileName = new File(vtFileName.elementAt(i)[1]);
				}

				if(fileName!=null)
				{
					if(!fileName.exists())
					{							
						for(int i2=0; i2<frmMainMenu.deviceNumber; i2++)
						{
							if(((JCheckBox)frmMainMenu.vtDevice.elementAt(i2)).getText().equals(vtFileName.elementAt(i)[0]))
							{
								String sIP = ((JCheckBox)frmMainMenu.vtDevice.elementAt(i2)).getToolTipText();
								String sHostName = ((JCheckBox)frmMainMenu.vtDevice.elementAt(i2)).getText();
								String sInfoToSend = sHostName+"(IP:"+sIP+"): "+"KHONG BACKUP DUOC FILE CAU HINH.";
								
								System.out.println("Send TRAP:\n"+sInfoToSend);
								
								frmMainMenu.dispOutput("\n");
								frmMainMenu.dispOutput("ĐANG GỬI TRAP ĐẾN ĐỊA CHỈ IP:");
								
								for(int st=0; st<GetInput.vtTRAPReceiver.size(); st++)
								{
									frmMainMenu.dispOutput(">>>"+GetInput.vtTRAPReceiver.elementAt(st));
								}
																
								frmMainMenu.dispOutput(">>>VarBind content:"+
								                       " 1.3.6.1.2.1.2.2.1.8 "+"Integer32: "+
								                       0+
								                       " 1.3.6.1.2.1.2.2.1.9 "+"OctetString: "+
								                       sInfoToSend);
								
								for(int st=0; st<GetInput.vtTRAPReceiver.size(); st++)
								{
									try
									{
										frmMainMenu.trapSender.sendTRAP(sIP,
																		new int[]{1, 3, 6, 1, 2, 1, 2, 2, 1, 8},
																		0,
																		new int[]{1, 3, 6, 1, 2, 1, 2, 2, 1, 9},
																		sInfoToSend,
																		GetInput.vtTRAPReceiver.elementAt(st),
																		162,
																		"public");
									}
									catch(Exception e)
									{
										e.printStackTrace();
										log4j.error("", e);
										frmMainMenu.dispOutput("GỬI TRAP ĐỂ TẠO CẢNH BÁO BỊ LỖI:\n"+e.toString());
									}
								}
								
								break;
							}
						}
					}
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
				log4j.error("", e);
			}
		}
	}
	
	void start()
	{
		isRun = true;
		isStartFinished = false;
		thread = null;
		ConnectDevice.taskCount = 0;
		
		initConnectDevice();
		
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
		isStartFinished = true;
		ConnectDevice.taskCount = 0;
		
		if(connectDevice!=null)
		{
			for(int i=0; i<connectDevice.length; i++)
			{
				try
				{
					if(connectDevice[i]!=null)
					{
						connectDevice[i].stop();
						connectDevice[i] = null;
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
					log4j.error("", e);
				}
			}
		}
		
		connectDevice = null;
		
		if(renameFile!=null)
		{
			renameFile.stop();
		}
		
		renameFile = null;

		try
		{
			thread = null;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			log4j.error("", e);
		}
	}
}