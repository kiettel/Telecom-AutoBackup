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

import javax.swing.JCheckBox;

import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.Logger;


public class ConnectDevice implements Runnable
{
	private static final Logger log4j = Logger.getLogger(ConnectDevice.class);
	static int taskCount = 0;
	private int port = 23,
	            fileBackupOK = 0;
	boolean isRun = false,
			isSend = false,
			login = false,
			isNotFound = true;
	static boolean isBackup = false;
	private Thread thread;
	String sDeviceName = "",
		   sDeviceSystemName = "",
		   sDeviceIP = "",
		   sDeviceType = "",
		   sManufacturer = "",
		   sDeviceUser = "",
		   sDevicePass = "",
		   sEnablePass = "",
		   sServerIP = "",
		   sFileOnDevice = "",
		   sFileName1 = "",
		   sFileName2 = "";
	private File fileName;
	StringBuffer sReceivedBuffer;
	Backup backup;
	private SendReceive sendReceive;

	public ConnectDevice(Backup backup,
			             String sDeviceName,
			             String sDeviceSystemName,
			             String sDeviceIP,
			             String sDeviceType,
			             String sManufacturer,
			             String sDeviceUser,
			             String sDevicePass,
			             String sServerIP,
			             String sFileOnDevice,
			             String sEnablePass)
	{
		PropertyConfigurator.configure("log4j.properties");
		this.backup = backup;
		this.sDeviceName = sDeviceName;
		this.sDeviceSystemName = sDeviceSystemName;
		this.sDeviceIP = sDeviceIP;
		this.sDeviceType = sDeviceType;
		this.sManufacturer = sManufacturer;
		this.sDeviceUser = sDeviceUser;
		this.sDevicePass = sDevicePass;
		this.sServerIP = sServerIP;
		this.sFileOnDevice = sFileOnDevice;
		this.sEnablePass = sEnablePass;
	}
	
	public void run() 
	{
		while(isRun&&ConnectDevice.isBackup)
		{
			connect();
		}
	}

	private void createListFile()
	{
		String sTime = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
		sFileName1 = sDeviceSystemName+"_"+sTime;

		if((sDeviceType.equals("HuaweiNE40E-4"))||
		   (sDeviceType.equals("HuaweiNE40E-X8")))
		{
			sFileName2 = backup.frmMainMenu.sFolder + "\\" + sFileName1+".cfg";
			
			backup.vtFileName.addElement(new String[]{sDeviceSystemName,
                                                      sFileName2});
		}
		else if((sDeviceType.equals("Cisco7606"))||
				(sDeviceType.equals("Cisco7609")))
		{
			sFileName2 = backup.frmMainMenu.sFolder + "\\" + sFileName1+".cfg";
			
			backup.vtFileName.addElement(new String[]{sDeviceSystemName,
												      sFileName2});
		}		
		else if((sDeviceType.equals("HuaweiMA5600"))||
				(sDeviceType.equals("HuaweiMA5616"))||
				(sDeviceType.equals("SWZTE3928A")))
		{
			sFileName2 = backup.frmMainMenu.sFolder + "\\" + sFileName1+".cfg";
			
			backup.vtFileName.addElement(new String[]{sDeviceSystemName,
                                                      sFileName2});
		}			
		else if(sDeviceType.equals("SiemenshiX5630")||
			   (sDeviceType.equals("ZTE-9806H"))||
			   (sDeviceType.equals("SWRuby"))||
			   (sDeviceType.equals("SWAlcatel6400"))||
			   (sDeviceType.equals("SWAlcatel6200"))||
			   (sDeviceType.equals("SWVFTV-2224G-OP"))||
			   (sDeviceType.equals("SWHuaweiS2016HI"))||
			   (sDeviceType.equals("SWHuaweiS8502"))||
			   (sDeviceType.equals("SWHuaweiS6500"))||
			   (sDeviceType.equals("SWRaisecom"))||
			   (sDeviceType.equals("VolkTekMEN-6328"))||
			   (sDeviceType.equals("SWCisco3560")))
		{
			sFileName2 = backup.frmMainMenu.sFolder + "\\" + sFileName1+".cfg";
			
			backup.vtFileName.addElement(new String[]{sDeviceSystemName,
                    								  sFileName2});
		}		
		else if(sDeviceType.equals("SiemenshiD6615"))
		{
			sFileName2 = backup.frmMainMenu.sFolder + "\\" + sFileName1+".cfg";
			
			backup.vtFileName.addElement(new String[]{sDeviceSystemName,
                                                      sFileName2});
		}		
		else if(sDeviceType.equals("MSAN")||
				sDeviceType.equals("GPONAlcatel"))
		{
			sFileName2 = backup.frmMainMenu.sFolder + "\\" + sFileName1 +"\\"+"dm_complete.tar";
			
			backup.vtFileName.addElement(new String[]{sDeviceSystemName,
                                                      sFileName2});
		}																	
	}
	
	void connect() 
	{
		createListFile();
		
		String sTimeConnect = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
		
		System.out.println("\n");
		System.out.println(sTimeConnect+">>CONNECTING TO: "+sDeviceSystemName+"(IP:"+sDeviceIP+")...");
		System.out.println(">FILE NAME: "+sFileName2+"\n");
				
		try
		{
			backup.frmMainMenu.dispOutput("\n");
			backup.frmMainMenu.dispOutput(sTimeConnect+">>CONNECTING TO: "+sDeviceSystemName+"(IP:"+sDeviceIP+")...");
			setDeviceColor(sDeviceSystemName, Color.BLUE, Color.BLACK);
			
			sendReceive.connect(sDeviceIP, port);
							
			if(sendReceive.isConnected)
			{
				if(!sDeviceIP.equals("Unknown")&&
				   !sDeviceType.equals("Unknown"))
				{
					autoLogin();
				}
				else
				{
					unknownDeviceType();
				}
			}
			else 
			{
				connectFailed();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			log4j.error("", e);
			next();
		}
	}
	
	void autoLogin()
	{
		try
		{
			if((sDeviceType.equals("HuaweiNE40E-4"))||
			   (sDeviceType.equals("HuaweiNE40E-X8")))
			{
				login = waitForString("me:", 5);
				
				if(login)
				{
					sendCommand(sDeviceUser);
					login = waitForString("rd:", 5); 
				}	
				if(login)
				{
					sendCommand(sDevicePass);
					login = waitForString(">", 5);
				}
				
				if(login)
				{
					backupMANEHuaweiNE40E();
				}
				else
				{
					loginFailed();
				}
			}	
			else if((sDeviceType.equals("Cisco7606"))||
			        (sDeviceType.equals("Cisco7609")))
			{
				login = waitForString("me:", 5);
						
				if(login)
				{
					sendCommand(sDeviceUser);
					login = waitForString("rd:", 5); 
				}	
				if(login)
				{
					sendCommand(sDevicePass);
					login = waitForString("#", ">", 5);
				}
						
				if(login)
				{
					backupMANECisco7606();
				}
				else
				{
					loginFailed();
				}
			}	
			else if(sDeviceType.toString().equals("HuaweiMA5600"))
			{
				login = waitForString("ame:", 5);
				
				if(login)
				{
					sendCommand(sDeviceUser);
					login = waitForString("rd:", 5);
				}	
				
				if(login)
				{
					sendCommand(sDevicePass);
					System.out.println("\n\n");
					sendCommand("");
					sendCommand("");
					sendCommand("");
					sendCommand("");
					login = waitForString(">", 5);
				}
				
				if(login)
				{
					backupDSLAMHuaweiMA5600();
				}
				else
				{
					loginFailed();
				}
			}
			else if(sDeviceType.equals("HuaweiMA5616"))
			{
				login = waitForString("ame:", 5);
				
				if(login)
				{
					sendCommand(sDeviceUser);
					login = waitForString("rd:", 5);
				}	
				if(login)
				{
					sendCommand(sDevicePass);
					System.out.println("\n\n");
					sendCommand("");
					sendCommand("");
					sendCommand("");
					sendCommand("");
					login = waitForString(">", 5);
				}
				
				if(login)
				{
					backupDSLAMHuawei5616();
				}
				else
				{
					loginFailed();
				}
			}
			else if((sDeviceType.equals("SiemenshiX5630")))
			{
				login = waitForString("in:", 10);
				
				if(login)
				{
					sendCommand(sDeviceUser);
					login = waitForString("rd:", 10);
				}	
				if(login)
				{
					sendCommand(sDevicePass);
					login = waitForString(">", 10);
				}
				
				if(login)
				{
					backupDSLAMSiemenshiX5630();
				}
				else
				{
					loginFailed();
				}
			}
			else if((sDeviceType.equals("SiemenshiX5635")))
			{
				login = waitForString("in:", 10);
				
				if(login)
				{
					sendCommand(sDeviceUser);
					login = waitForString("rd:", 10);
				}	
				if(login)
				{
					sendCommand(sDevicePass);
					login = waitForString(">", 10);
				}
				
				if(login)
				{
					backupDSLAMSiemenshiX5635();
				}
				else
				{
					loginFailed();
				}
			}
			else if((sDeviceType.equals("SiemenshiD6615")))
			{
				login = waitForString("in:", 10);
				
				if(login)
				{
					sendCommand(sDeviceUser);
					login = waitForString("rd:", 10);
				}	
				if(login)
				{
					sendCommand(sDevicePass);
					login = waitForString(">", 10);
				}
				
				if(login)
				{
					backupDSLAMSiemenshiD6615();
				}
				else
				{
					loginFailed();
				}
			}
			else if(sDeviceType.equals("MSAN"))
			{
				login = waitForString("in:", 5);
				
				if(login)
				{
					sendCommand(sDeviceUser);
					login = waitForString("rd:", 5);
				}	
				
				if(login)
				{
					sendCommand(sDevicePass);
					login = waitForString("#", 5);
				}
				
				if(login)
				{
					backupDSLAMAlcatel();
				}
				else
				{
					loginFailed();
				}
			}
			else if(sDeviceType.equals("ZTE-9806H"))
			{
				sendCommand("");
				
				login = waitForString("in:", 5);
				
				if(login)
				{
					sendCommand(sDeviceUser);
					login = waitForString("rd:", 5);
				}	
				if(login)
				{
					sendCommand(sDevicePass);
					login = waitForString(">", 5);
				}
				
				if(login)
				{
					backupDSLAMZTE9806H();
				}
				else
				{
					loginFailed();
				}
			}	
			else if(sDeviceType.equals("SWRuby"))
			{
				login = waitForString("in:", 5);
				
				if(login)
				{
					sendCommand(sDeviceUser);
					login = waitForString("rd:", 5); 
				}	
				if(login)
				{
					sendCommand(sDevicePass);
					login = waitForString("#", 5);
				}
				
				if(login)
				{
					backupSWRuby();
				}
				else
				{
					loginFailed();
				}
			}	
			else if((sDeviceType.equals("SWHuaweiS5300")))
			{
				login = waitForString("ame:", 5);
				
				if(login)
				{
					sendCommand(sDeviceUser);
					login = waitForString("rd:", 5);
				}	
				if(login)
				{
					sendCommand(sDevicePass);
					login = waitForString(">", 5);
				}
				
				if(login)
				{
					backupSWHuaweiS5300();
				}
				else
				{
					loginFailed();
				}
			}	
			else if((sDeviceType.equals("SWHuaweiS3328")))
			{
				login = waitForString("ame:", 5);
				
				if(login)
				{
					sendCommand(sDeviceUser);
					login = waitForString("rd:", 5);
				}	
				if(login)
				{
					sendCommand(sDevicePass);
					login = waitForString(">", 5);
				}

				if(login)
				{
					backupSWHuaweiS3328();
				}
				else
				{
					loginFailed();
				}
			}	
			else if(sDeviceType.equals("SWHuaweiS2016HI"))
			{
				login = waitForString("rd:", 5);
				
				if(login)
				{
					sendCommand(sDevicePass);
					login = waitForString(">", "login", 5);
				}
				
				if(login)
				{
					backupSWHuaweiS2016HI();
				}
				else
				{
					loginFailed();
				}
			}	
			else if(sDeviceType.equals("SWHuaweiS8502"))
			{
				login = waitForString("rd:", 5);
				
				if(login)
				{
					sendCommand(sDevicePass);
					login = waitForString(">", "login", 5);
				}
				
				if(login)
				{
					backupSWHuaweiS8502();
				}
				else
				{
					loginFailed();
				}
			}	
			else if(sDeviceType.equals("SWHuaweiS6500"))
			{
				login = waitForString("rd:", 5);
				
				if(login)
				{
					sendCommand(sDevicePass);
					login = waitForString(">", "login", 5);
				}
				
				if(login)
				{
					backupSWHuaweiS6500();
				}
				else
				{
					loginFailed();
				}
			}	
			else if(sDeviceType.equals("SWAlcatel6200"))
			{
				login = waitForString("ame:", 5);
				
				if(login)
				{
					sendCommand(sDeviceUser);
					login = waitForString("rd:", 5);
				}	
				if(login)
				{
					sendCommand(sDevicePass);
					login = waitForString("#", ">", 5);
				}
				
				if(login)
				{
					backupSWAlcatel6200();
				}
				else
				{
					loginFailed();
				}
			}	
			else if(sDeviceType.equals("SWAlcatel6400"))
			{
				login = waitForString("in :", 5);
				
				if(login)
				{
					sendCommand(sDeviceUser);
					login = waitForString("rd :", 5);
				}	
				if(login)
				{
					sendCommand(sDevicePass);
					login = waitForString("#", ">", 5);
				}
				
				if(login)
				{
					backupSWAlcatel6400();
				}
				else
				{
					loginFailed();
				}
			}	
			else if(sDeviceType.equals("SWVFTV-2224G-OP"))
			{
				login = waitForString("in:", 5);
				
				if(login)
				{
					sendCommand(sDeviceUser);
					login = waitForString("rd:", 5);
				}	
				if(login)
				{
					sendCommand(sDevicePass);
					login = waitForString(">", 5);
				}
				
				if(login)
				{
					backupSWVFTV2224GOP();
				}
				else
				{
					loginFailed();
				}
			}	
			else if(sDeviceType.equals("SWZTE3928A"))
			{
				login = waitForString("me:", 5);
				
				if(login)
				{
					sendCommand(sDeviceUser);
					login = waitForString("rd:", 5);
				}	
				if(login)
				{
					sendCommand(sDevicePass);
					login = waitForString("#", ">", 5);
				}
				
				if(login)
				{
					backupSWZTE3928A();
				}
				else
				{
					loginFailed();
				}
			}	
			else if(sDeviceType.equals("SWRaisecom"))
			{
				login = waitForString("in:", 5);
				
				if(login)
				{
					sendCommand(sDeviceUser);
					login = waitForString("rd:", 5);
				}	
				if(login)
				{
					sendCommand(sDevicePass);
					login = waitForString("#", ">", 5);
				}
				
				if(login)
				{
					backupSWRaisecom();
				}
				else
				{
					loginFailed();
				}
			}
			else if(sDeviceType.equals("VolkTekMEN-6328"))
			{
				login = waitForString("in:", 5);
				
				if(login)
				{
					sendCommand(sDeviceUser);
					login = waitForString(">", 5);
				}	
				
				if(login)
				{
					sendCommand("enable");
					login = waitForString("er:", 5);
				}
				
				if(login)
				{
					sendCommand(sDeviceUser);
					login = waitForString("rd:", 5);
				}
				if(login)
				{
					sendCommand(sDevicePass);
					login = waitForString("#", 5);
				}
				if(login)
				{
					backupSWVolkTekMEN6328();
				}
				else
				{
					loginFailed();
				}
			}
			else if(sDeviceType.toString().equals("GPONZTEC320"))
			{
				login = waitForString("ame:", 5);
				
				if(login)
				{
					sendCommand(sDeviceUser);
					login = waitForString("rd:", 5);
				}	
				
				if(login)
				{
					sendCommand(sDevicePass);
					login = waitForString(">", "#", 5);
				}
				
				if(login)
				{
					backupGPONZTEC320();
				}
				else
				{
					loginFailed();
				}
			}
			else if(sDeviceType.equals("GPONAlcatel"))
			{
				login = waitForString("in:", 5);
				
				if(login)
				{
					sendCommand(sDeviceUser);
					login = waitForString("rd:", 5);
				}	
				
				if(login)
				{
					sendCommand(sDevicePass);
					login = waitForString("#", 5);
				}
				
				if(login)
				{
					backupGPONAlcatel();
				}
				else
				{
					loginFailed();
				}
			}
			else if(sDeviceType.equals("GPONHuaweiMA5608T"))
			{
				login = waitForString("ame:", 5);
				
				if(login)
				{
					sendCommand(sDeviceUser);
					login = waitForString("rd:", 5);
				}	
				
				if(login)
				{
					sendCommand(sDevicePass);
					login = waitForString(">", "#", 5);
				}
				
				if(login)
				{
					backupGPONHuaweiMA5608T();
				}
				else
				{
					loginFailed();
				}
			}
			else if(sDeviceType.equals("SWCisco3560"))
			{
				login = waitForString("rd:", 5);
				
				if(login)
				{
					sendCommand(sDevicePass);
					login = waitForString(">", 5);
				}
				
				if(login)
				{
					backupSWCisco3560();
				}
				else
				{
					loginFailed();
				}
			}
			else
			{
				unknownDeviceType();
			}
		} 
		catch(Exception e)
		{
			e.printStackTrace();
			log4j.error("", e);
			next();
		}
	}
	
	private void connectFailed()
	{
		setDeviceColor(sDeviceSystemName, Color.RED, Color.BLACK);
		backup.frmMainMenu.dispOutput("KHÔNG KẾT NỐI ĐƯỢC ĐẾN: "+sDeviceSystemName+".");
		next();
	}
	
	private void loginFailed()
	{
		setDeviceColor(sDeviceSystemName, Color.ORANGE, Color.BLACK);
		backup.frmMainMenu.dispOutput("ĐĂNG NHẬP KHÔNG THÀNH CÔNG.");
		next();
	}
	
	private void unknownDeviceType()
	{
		setDeviceColor(sDeviceSystemName, Color.BLACK, Color.WHITE);
		backup.frmMainMenu.dispOutput("KHÔNG BIẾT LOẠI THIẾT BỊ.");
		next();
	}
	
	private void setDeviceColor(String sDeviceName, Color c1, Color c2)
	{
		String sDvTmp = "";
		
		for(int d=0; d<backup.frmMainMenu.deviceNumber; d++)
		{
			sDvTmp = ((JCheckBox)backup.frmMainMenu.vtDevice.elementAt(d)).getText();
			
			if(sDeviceName.equals(sDvTmp)) 
			{
				((JCheckBox)backup.frmMainMenu.vtDevice.elementAt(d)).setBackground(c1);
				((JCheckBox)backup.frmMainMenu.vtDevice.elementAt(d)).setForeground(c2);
				break;
			}
		}	
	}
	
	private void statisticBackup()
	{
		fileName = null;
		int fileSize = 0;
		fileBackupOK = 0;
		
		fileSize = backup.vtFileName.size();

		for(int i=0; i<fileSize; i++)
		{
			try
			{
				fileName = null;
				
				if(fileName==null)
				{	
					fileName = new File(backup.vtFileName.elementAt(i)[1]);
				}

				if(fileName!=null)
				{
					if(fileName.exists())
					{							
						fileBackupOK++;
					}
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
				log4j.error("", e);
			}
		}
		
		backup.frmMainMenu.dispOutput("\n");
		backup.frmMainMenu.dispOutput(">BACKUP STATISTIC:");
		backup.frmMainMenu.dispOutput(">>>TOTAL FILES NEED TO BACKUP: "+backup.frmMainMenu.deviceSelected);
		backup.frmMainMenu.dispOutput(">>>NUMBER OF SUCCESSFUL FILES: "+fileBackupOK);
		backup.frmMainMenu.dispOutput(">>>NUMBER OF UNSUCCESSFUL FILES: "+(backup.frmMainMenu.deviceSelected-fileBackupOK));
		backup.frmMainMenu.dispOutput("\n");
		
		log4j.info(">BACKUP STATISTIC:");
		log4j.info(">>>TOTAL FILES NEED TO BACKUP: "+backup.frmMainMenu.deviceSelected);
		log4j.info(">>>NUMBER OF SUCCESSFUL FILES: "+fileBackupOK);
		log4j.info(">>>NUMBER OF UNSUCCESSFUL FILES: "+(backup.frmMainMenu.deviceSelected-fileBackupOK));
	}
	
	private void next()
	{
		String sDvTmp = "";
		
		for(int d=0; d<backup.frmMainMenu.deviceNumber; d++)
		{
			sDvTmp = ((JCheckBox)backup.frmMainMenu.vtDevice.elementAt(d)).getText();
			
			if(sDeviceName.equals(sDvTmp)) 
			{
				if(!(((JCheckBox)backup.frmMainMenu.vtDevice.elementAt(d)).getBackground()==Color.BLACK||
				     ((JCheckBox)backup.frmMainMenu.vtDevice.elementAt(d)).getBackground()==Color.ORANGE||
				     ((JCheckBox)backup.frmMainMenu.vtDevice.elementAt(d)).getBackground()==Color.RED||
				     ((JCheckBox)backup.frmMainMenu.vtDevice.elementAt(d)).getBackground()==Color.PINK))
				{
					setDeviceColor(sDeviceSystemName, Color.YELLOW, Color.BLACK);
				}
			}
		}
				
		taskCount++;
		taskBackup();
	}
	
	private void taskBackup()
	{
		if(taskCount>backup.connectDevice.length-1)
		{
			try
			{
				if(backup.connectDevice!=null&&backup.connectDevice[taskCount-1]!=null)
				{
					backup.connectDevice[taskCount-1].stop();
					backup.connectDevice[taskCount-1] = null;
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
				log4j.error("", e);
			}
			
			if(backup.frmMainMenu.isAutoBackup)
			{
				backup.frmMainMenu.dispOutput("PLEASE WAIT 60s...");
				sleep(60000);
				
				if(backup.renameFile!=null)
				{
					backup.renameFile.stop();
				}
				
				backup.isStartFinished = false;
								
				backup.sendTRAP();
				statisticBackup();
				
				backup.frmMainMenu.dispOutput("AUTO BACKUP FINISHED, WAITING...");
				backup.frmMainMenu.lbStatus.setText("STATUS: AUTO BACKUP FINISHED, WAITING...");
				log4j.info("AUTO BACKUP FINISHED, WAITING...");
			}
			else
			{
				backup.frmMainMenu.dispOutput("PLEASE WAIT 60s...");
				sleep(60000);
				
				if(backup.renameFile!=null)
				{
					backup.renameFile.stop();
				}
				
				taskCount = 0;
				
				backup.isStartFinished = false;
								
				backup.sendTRAP();
				statisticBackup();
				
				backup.frmMainMenu.disconnect2();
				backup.frmMainMenu.dispOutput("AUTO BACKUP FINISHED.");
				backup.frmMainMenu.lbStatus.setText("STATUS: AUTO BACKUP FINISHED.");
				log4j.info("AUTO BACKUP FINISHED.");
			}
		}
		else
		{
			try
			{
				if(backup.connectDevice!=null&&backup.connectDevice[taskCount-1]!=null)
				{
					backup.connectDevice[taskCount-1].stop();
					backup.connectDevice[taskCount-1] = null;
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
				log4j.error("", e);
			}
			
			try
			{
				if(backup.connectDevice!=null&&backup.connectDevice[taskCount]!=null)
				{
					backup.connectDevice[taskCount].start();
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
				log4j.error("", e);
				next();
			}
		}
	}

	private void backupMANEHuaweiNE40E()
	{
		sendCommand("tftp " +sServerIP+ " put "+ sFileOnDevice+ " " +sFileName1+".cfg");
		sleep(1000);
		next();
	}
	
	private void backupMANECisco7606()
	{
		sendCommand("");
		
		if(waitForString(">", 2))
		{
			sendCommand("enable");
			
			if(waitForString("rd:", 5))
			{
				sendCommand(sEnablePass);
			}
			else
			{
				strToWaitNotFound();
			}
		}
		
		sendCommand("");
		
		if(waitForString("#", 5))
		{
			sendCommand("write memory");
		}
		else
		{
			strToWaitNotFound();
		}
		
		sleep(30000);
		
		sendCommand("copy nvram:startup-config tftp:");
		
		if(waitForString("]?", 5)&&!isNotFound)
		{
			sendCommand(sServerIP);
		}
		else
		{
			strToWaitNotFound();
		}
		
		if(waitForString("g]?", 5)&&!isNotFound)
		{
			sendCommand(sFileName1+".cfg");
		}
		else
		{
			strToWaitNotFound();
		}

		sleep(1000);
		next();
	}
	
	private void backupDSLAMHuaweiMA5600()
	{
		sendCommand("enable");
		
		if(waitForString("#", 5))
		{
			sendCommand("conf");
		}
		else
		{
			strToWaitNotFound();
		}
		
		if(waitForString("#", 5)&&!isNotFound)
		{
			sendCommand("save");
		}
		else
		{
			strToWaitNotFound();
		}
		
		if(waitForString("}:", 5)&&!isNotFound)
		{
			sendCommand("");
		}
		
		sleep(50000);

		sendCommand("backup configuration tftp "+sServerIP+ " "+sFileName1+".cfg");
		
		if(waitForString("n]:", 5)&&!isNotFound)
		{
			sendCommand("y");
		}
		else
		{
			strToWaitNotFound();
		}
		
		sleep(30000);
		
		sendCommand("backup data tftp "+sServerIP+ " "+sFileName1+".dat");
		
		if(waitForString("n]:", 5)&&!isNotFound)
		{
			sendCommand("y");
		}
		else
		{
			strToWaitNotFound();
		}

		sleep(1000);
		next();
	}
	
	private void backupDSLAMHuawei5616()
	{	
		sendCommand("enable");
		
		if(waitForString("#", 5))
		{
			sendCommand("conf");
		}
		else
		{
			strToWaitNotFound();
		}
		
		if(waitForString("#", 5)&&!isNotFound)
		{
			sendCommand("save config");
		}
		else
		{
			strToWaitNotFound();
		}
		
		if(waitForString("#", 5)&&!isNotFound)
		{
			sendCommand("save data");
		}
		else
		{
			strToWaitNotFound();
		}
		
		sleep(30000);
		
		sendCommand("backup configuration tftp "+sServerIP+ " "+sFileName1+".cfg");
		
		if(waitForString("n]:", 5)&&!isNotFound)
		{
			sendCommand("y");
		}
		else
		{
			strToWaitNotFound();
		}
		
		sleep(30000);
		
		sendCommand("backup data tftp "+sServerIP+ " "+sFileName1+".dat");
		
		if(waitForString("n]:", 5)&&!isNotFound)
		{
			sendCommand("y");
		}
		else
		{
			strToWaitNotFound();
		}

		sleep(1000);
		next();
	}
	
	private void backupDSLAMSiemenshiX5630()
	{
		sendCommand("enable");

		if(waitForString("#", 5))
		{
			sendCommand("conf t");
		}
		else
		{
			strToWaitNotFound();
		}
				
		if(waitForString("#", 5)&&!isNotFound)
		{
			sendCommand("wr m");
		}
		else
		{
			strToWaitNotFound();
		}
		
		if(waitForString("#", 60)&&!isNotFound)
		{
			sendCommand("upload cxu config "+sServerIP+ " "+sFileName1+".cfg");
		}
		else
		{
			strToWaitNotFound();
		}
		
		if(waitForString("me:", 5)&&!isNotFound)
		{
			sendCommand("anonymous");
		}
		else
		{
			strToWaitNotFound();
		}
		
		if(waitForString("d :", 5)&&!isNotFound)
		{
			sendCommand("");
		}
		else
		{
			strToWaitNotFound();
		}
		
		if(waitForString("ve", 5)&&!isNotFound)
		{
			sendCommand("");
		}
		else
		{
			strToWaitNotFound();
		}

		sleep(1000);
		next();
	}
	
	private void backupDSLAMSiemenshiX5635()
	{
		sendCommand("enable");

		if(waitForString("#", 5))
		{
			sendCommand("conf t");
		}
		else
		{
			strToWaitNotFound();
		}
				
		if(waitForString("#", 5)&&!isNotFound)
		{
			sendCommand("wr m");
		}
		else
		{
			strToWaitNotFound();
		}
		
		if(waitForString("#", 60)&&!isNotFound)
		{
			sendCommand("upload cxu config "+sServerIP+ " "+sFileName1+".cfg");
		}
		else
		{
			strToWaitNotFound();
		}
		
		if(waitForString("me:", 5)&&!isNotFound)
		{
			sendCommand("anonymous");
		}
		else
		{
			strToWaitNotFound();
		}
		
		if(waitForString("d :", 5)&&!isNotFound)
		{
			sendCommand("");
		}
		else
		{
			strToWaitNotFound();
		}
		
		if(waitForString("ve", 5)&&!isNotFound)
		{
			sendCommand("");
		}
		else
		{
			strToWaitNotFound();
		}

		sleep(1000);
		next();
	}
	
	private void backupDSLAMSiemenshiD6615()
	{
		sendCommand("enable");

		if(waitForString("#", 5))
		{
			sendCommand("copy tftp config upload startup-config");
		}
		else
		{
			strToWaitNotFound();
		}
				
		if(waitForString("P):", 5)&&!isNotFound)
		{
			sendCommand(sServerIP);
		}
		else
		{
			strToWaitNotFound();
		}
		
		if(waitForString("g]:", 5)&&!isNotFound)
		{
			sendCommand(sFileName1);
		}
		else
		{
			strToWaitNotFound();
		}

		sleep(1000);
		next();
	}
	
	private void backupDSLAMAlcatel()
	{
		sendCommand("exit all");

		if(waitForString("#", 5))
		{
			sendCommand("admin software-mngt shub database save");
		}
		else
		{
			strToWaitNotFound();
		}

		sleep(5000);
		
		sendCommand("exit all");
		
		if(waitForString("#", 5))
		{
			sendCommand("admin software-mngt database upload actual-active:"+sServerIP+":/"+sFileName1+"/dm_complete.tar");
		}
		else
		{
			strToWaitNotFound();
		}
		
		sleep(1000);
		next();
	}
	
	void backupDSLAMZTE9806H()
	{
		sReceivedBuffer = null;
		sReceivedBuffer = new StringBuffer();
		
		sendCommand("enable");
		
		if(waitForString("rd:", 5))
		{
			sendCommand(sEnablePass);
		}
		else
		{
			strToWaitNotFound();
		}
		
		if(waitForString("#", 5)&&!isNotFound)
		{
			sendCommand("save");
		}
		else
		{
			strToWaitNotFound();
		}

		if(waitForString("#", 60)&&!isNotFound)
		{
			sendCommand("copy running-config network");
		}
		else
		{
			strToWaitNotFound();
		}

		if(waitForString("]", 5)&&!isNotFound)
		{
			if(sReceivedBuffer.toString().contains("FTP"))
			{
				sendCommand("FTP");
				
				if(waitForString("]", 5)&&!isNotFound)
				{
					sendCommand(sServerIP);
				}
				else
				{
					strToWaitNotFound();
				}
				
				if(waitForString("]", 5)&&!isNotFound)
				{
					sendCommand(sFileName1+".cfg");
				}
				else
				{
					strToWaitNotFound();
				}
				
				if(waitForString("]", 5)&&!isNotFound)
				{
					sendCommand("anonymous");
				}
				else
				{
					strToWaitNotFound();
				}
				
				if(waitForString("]", 5)&&!isNotFound)
				{
					sendCommand("admin");
				}
				else
				{
					strToWaitNotFound();
				}
			}
			else
			{
				sendCommand(sServerIP);
				
				if(waitForString("]", 5)&&!isNotFound)
				{
					sendCommand(sFileName1+".cfg");
				}
				else
				{
					strToWaitNotFound();
				}
				
				if(waitForString("]", 5)&&!isNotFound)
				{
					sendCommand("anonymous");
				}
				else
				{
					strToWaitNotFound();
				}
				
				if(waitForString("]", 5)&&!isNotFound)
				{
					sendCommand("admin");
				}
				else
				{
					strToWaitNotFound();
				}
			}
		}
		else
		{
			strToWaitNotFound();
		}
		
		sReceivedBuffer = null;

		sleep(1000);
		next();
	}

	private void backupSWRuby()
	{
		sendCommand("save start");
		
		if(waitForString("#", 60))
		{
			sendCommand("tftp");
		}
		else
		{
			strToWaitNotFound();
		}
		
		if(waitForString("#", 5))
		{
			sendCommand("set server "+sServerIP);
		}
		else
		{
			strToWaitNotFound();
		}
		
		if(waitForString("#", 5)&&!isNotFound)
		{
			sendCommand("exit");
		}
		else
		{
			strToWaitNotFound();
		}
		
		if(waitForString("#", 5)&&!isNotFound)
		{
			sendCommand("config-file");
		}
		else
		{
			strToWaitNotFound();
		}
		
		if(waitForString("#", 5)&&!isNotFound)
		{
			sendCommand("set export-path "+sFileName1+".cfg");
		}
		else
		{
			strToWaitNotFound();
		}
		
		if(waitForString("#", 5)&&!isNotFound)
		{
			sendCommand("exit");
		}
		else
		{
			strToWaitNotFound();
		}
		
		if(waitForString("#", 5)&&!isNotFound)
		{
			sendCommand("config-file");	
		}
		else
		{
			strToWaitNotFound();
		}
		
		if(waitForString("#", 5)&&!isNotFound)
		{
			sendCommand("export start");
		}
		else
		{
			strToWaitNotFound();
		}

		sleep(1000);
		next();
	}
	
	private void backupSWHuaweiS5300()
	{
		sReceivedBuffer = null;
		sReceivedBuffer = new StringBuffer();
		
		sendCommand("save");
		
		if(waitForString("N]", 5)&&!isNotFound)
		{
			sendCommand("Y");
		}
		else
		{
			strToWaitNotFound();
		}
		
		if(waitForString(">", 60)&&!isNotFound)
		{
			sendCommand("dir");
		}
		else
		{
			strToWaitNotFound();
		}

		if(waitForString(">", 5))
		{
			if(sReceivedBuffer.toString().contains("vrpcfg.cfg"))
			{
				backup.renameFile.sOldFileName = backup.frmMainMenu.sFolder + "\\" +"vrpcfg.cfg";
				backup.renameFile.sNewFileName = backup.frmMainMenu.sFolder + "\\" + sFileName1 + ".cfg";
				sFileName2 = backup.renameFile.sNewFileName;
				sendCommand("tftp "+sServerIP+" put vrpcfg.cfg");
			}
			else if(sReceivedBuffer.toString().contains("vrpcfg.zip"))
			{
				backup.renameFile.sOldFileName = backup.frmMainMenu.sFolder + "\\" +"vrpcfg.zip";
				backup.renameFile.sNewFileName = backup.frmMainMenu.sFolder + "\\" + sFileName1 + ".zip";
				sFileName2 = backup.renameFile.sNewFileName;
				sendCommand("tftp "+sServerIP+" put vrpcfg.zip");
			}
			else if(sReceivedBuffer.toString().contains("backup.cfg"))
			{
				backup.renameFile.sOldFileName = backup.frmMainMenu.sFolder + "\\" +"backup.cfg";
				backup.renameFile.sNewFileName = backup.frmMainMenu.sFolder + "\\" + sFileName1 + ".cfg";
				sFileName2 = backup.renameFile.sNewFileName;
				sendCommand("tftp "+sServerIP+" put backup.cfg");
			}
			else if(sReceivedBuffer.toString().contains("backup.zip"))
			{
				backup.renameFile.sOldFileName = backup.frmMainMenu.sFolder + "\\" +"backup.zip";
				backup.renameFile.sNewFileName = backup.frmMainMenu.sFolder + "\\" + sFileName1 + ".zip";
				sFileName2 = backup.renameFile.sNewFileName;
				sendCommand("tftp "+sServerIP+" put backup.zip");
			}
			else
			{
				sendCommand("tftp "+sServerIP+" put vrpcfg.cfg");
				sendCommand("tftp "+sServerIP+" put vrpcfg.zip");
				sendCommand("tftp "+sServerIP+" put backup.cfg");
				sendCommand("tftp "+sServerIP+" put backup.zip");
			}
			
			backup.vtFileName.addElement(new String[]{sDeviceSystemName,
	                                                  sFileName2});
			
			if(backup.renameFile!=null)
			{
				backup.renameFile.start();
			}
		}
		else
		{
			strToWaitNotFound();
		}
			
		sReceivedBuffer = null;
		
		sleep(1000);
		next();
	}
	
	private void backupSWHuaweiS3328()
	{
		sReceivedBuffer = null;
		sReceivedBuffer = new StringBuffer();
		
		sendCommand("save");
		
		if(waitForString(">", 60)&&!isNotFound)
		{
			sendCommand("dir");
		}
		else
		{
			strToWaitNotFound();
		}
		
		if(sReceivedBuffer.toString().contains("vrpcfg.cfg"))
		{
			backup.renameFile.sOldFileName = backup.frmMainMenu.sFolder + "\\" +"vrpcfg.cfg";
			backup.renameFile.sNewFileName = backup.frmMainMenu.sFolder + "\\" + sFileName1 + ".cfg";
			sFileName2 = backup.renameFile.sNewFileName;
			sendCommand("tftp "+sServerIP+" put vrpcfg.cfg");
		}
		else if(sReceivedBuffer.toString().contains("vrpcfg.zip"))
		{
			backup.renameFile.sOldFileName = backup.frmMainMenu.sFolder + "\\" +"vrpcfg.zip";
			backup.renameFile.sNewFileName = backup.frmMainMenu.sFolder + "\\" + sFileName1 + ".zip";
			sFileName2 = backup.renameFile.sNewFileName;
			sendCommand("tftp "+sServerIP+" put vrpcfg.zip");
		}
		else if(sReceivedBuffer.toString().contains("backup.cfg"))
		{
			backup.renameFile.sOldFileName = backup.frmMainMenu.sFolder + "\\" +"backup.cfg";
			backup.renameFile.sNewFileName = backup.frmMainMenu.sFolder + "\\" + sFileName1 + ".cfg";
			sFileName2 = backup.renameFile.sNewFileName;
			sendCommand("tftp "+sServerIP+" put backup.cfg");
		}
		else if(sReceivedBuffer.toString().contains("backup.zip"))
		{
			backup.renameFile.sOldFileName = backup.frmMainMenu.sFolder + "\\" +"backup.zip";
			backup.renameFile.sNewFileName = backup.frmMainMenu.sFolder + "\\" + sFileName1 + ".zip";
			sFileName2 = backup.renameFile.sNewFileName;
			sendCommand("tftp "+sServerIP+" put backup.zip");
		}
		else
		{
			sendCommand("tftp "+sServerIP+" put vrpcfg.cfg");
			sendCommand("tftp "+sServerIP+" put vrpcfg.zip");
			sendCommand("tftp "+sServerIP+" put backup.cfg");
			sendCommand("tftp "+sServerIP+" put backup.zip");
		}
		
		backup.vtFileName.addElement(new String[]{sDeviceSystemName,
                								  sFileName2});

		if(backup.renameFile!=null)
		{
			backup.renameFile.start();
		}

		sReceivedBuffer = null;
		sleep(1000);
		next();
	}
	
	void backupSWHuaweiS2016HI()
	{
		sendCommand("");
		
		if(waitForString(">", 5))
		{
			sendCommand("save");
		}
		else
		{
			strToWaitNotFound();
		}
		
		if(waitForString("N]", 5)&&!isNotFound)
		{
			sendCommand("Y");
		}
		else
		{
			strToWaitNotFound();
		}
		
		if(waitForString("):", 5)&&!isNotFound)
		{
			sendCommand("");
		}
		else
		{
			strToWaitNotFound();
		}
		
		if(waitForString("fully.", 60)&&!isNotFound)
		{
			sendCommand("");
		}
		
		if(waitForString(">", 5)&&!isNotFound)
		{  
			sendCommand("tftp "+sServerIP+" put vrpcfg.cfg "+sFileName1+".cfg");
		}
		else
		{
			strToWaitNotFound();
		}

		sleep(1000);
		next();
	}
	
	void backupSWHuaweiS8502()
	{
		sendCommand("save");

		if(waitForString("N]", 5))
		{
			sendCommand("Y");
		}
		else
		{
			strToWaitNotFound();
		}

		if(waitForString(">", 60)&&!isNotFound)
		{
			sendCommand("");
		}
		else
		{
			strToWaitNotFound();
		}
		
		if(waitForString(">", 60)&&!isNotFound)
		{  
			sendCommand("tftp "+sServerIP+" put vrpcfg.cfg "+sFileName1+".cfg");
		}
		else
		{
			strToWaitNotFound();
		}

		sleep(1000);
		next();
	}
	
	void backupSWHuaweiS6500()
	{
		sendCommand("save");

		if(waitForString("N]", 5))
		{
			sendCommand("Y");
		}
		else
		{
			strToWaitNotFound();
		}
		
		if(waitForString(">", 60)&&!isNotFound)
		{  
			sendCommand("tftp "+sServerIP+" put vrpcfg.cfg "+sFileName1+".cfg");
		}
		else
		{
			strToWaitNotFound();
		}

		sleep(1000);
		next();
	}
	
	void backupSWAlcatel6200()
	{
		sendCommand("copy running-config startup-config");
		
		if(waitForString("...", 5)&&!isNotFound)
		{  
			sendCommand("y");
		}
		else
		{
			strToWaitNotFound();
		}
		
		if(waitForString("#", 60)&&!isNotFound)
		{  
			sendCommand("copy running-config tftp://"+sServerIP+"/"+sFileName1+".cfg");
		}
		else
		{
			strToWaitNotFound();
		}

		sleep(1000);
		next();
	}
	
	void backupSWAlcatel6400()
	{
		sendCommand("write memory");

		if(waitForString("#", ">", 60))
		{
			sendCommand("copy running-config working");
		}
		else
		{
			strToWaitNotFound();
		}
		
		if(waitForString("#", ">", 60))
		{
			sendCommand("copy working certified");
		}
		else
		{
			strToWaitNotFound();
		}
		
		if(waitForString("#", ">", 60)&&!isNotFound)
		{  
			sendCommand("tftp "+sServerIP+" put source-file working/boot.cfg destination-file "+sFileName1+".cfg"+" ascii");
		}
		else
		{
			strToWaitNotFound();
		}

		sleep(1000);
		next();
	}
	
	void backupSWVFTV2224GOP()
	{
		sendCommand("enable");
		
		if(waitForString("#", 5))
		{
			sendCommand("wr m");
		}
		else
		{
			strToWaitNotFound();
		}
		
		if(waitForString("#", 60))
		{
			sendCommand("copy running-config startup-config");
		}
		else
		{
			strToWaitNotFound();
		}
		
		if(waitForString("#", 60))
		{
			sendCommand("copy running-config backup.cfg");
		}
		else
		{
			strToWaitNotFound();
		}
		
		if(waitForString("#", 60)&&!isNotFound)
		{
			sendCommand("copy tftp config upload backup.cfg");
		}
		else
		{
			strToWaitNotFound();
		}
		
		if(waitForString("):", 5)&&!isNotFound)
		{
			sendCommand(sServerIP);
		}
		else
		{
			strToWaitNotFound();
		}
		
		if(waitForString("g]:", 5)&&!isNotFound)
		{
			sendCommand(sFileName1+".cfg");
		}
		else
		{
			strToWaitNotFound();
		}

		sleep(1000);
		next();
	}
	
	void backupSWZTE3928A()
	{
		sReceivedBuffer = null;
		sReceivedBuffer = new StringBuffer();
		
		sendCommand("enable");
		
		if(waitForString("rd:","#", 5))
		{
			if(sReceivedBuffer.toString().contains("rd:"))
			{
				sendCommand(sEnablePass);
				
				if(waitForString("#", 5))
				{
					sendCommand("copy flash: /cfg/startrun.dat tftp: //"+sServerIP+"/"+sFileName1+".dat");
				}
				else
				{
					strToWaitNotFound();
				}
			}
			else
			{
				sendCommand("copy flash: /cfg/startrun.dat tftp: //"+sServerIP+"/"+sFileName1+".dat");
			}
		}
		else
		{
			strToWaitNotFound();
		}

		sReceivedBuffer = null;	
		sleep(1000);
		next();
	}
	
	void backupSWRaisecom()
	{
		sReceivedBuffer = null;
		sReceivedBuffer = new StringBuffer();
		
		sendCommand("enable");
		
		if(waitForString("rd:","#", 5))
		{
			if(sReceivedBuffer.toString().contains("rd:"))
			{
				sendCommand(sEnablePass);
				
				if(waitForString("#", 5))
				{
					sendCommand("write");
				}
				else
				{
					strToWaitNotFound();
				}
				
				if(waitForString("#", 60))
				{
					sendCommand("upload startup-config tftp "+sServerIP+" "+sFileName1+".cfg");
				}
				else
				{
					strToWaitNotFound();
				}
			}
			else
			{
				sendCommand("write");
				
				if(waitForString("#", 60))
				{
					sendCommand("upload startup-config tftp "+sServerIP+" "+sFileName1+".cfg");
				}
				else
				{
					strToWaitNotFound();
				}
			}
		}
		
		sReceivedBuffer = null;
		sleep(1000);
		next();
	}
	
	void backupSWVolkTekMEN6328()
	{
		sendCommand("configure terminal");
		
		if(waitForString("#", 60))
		{
			sendCommand("archive upload-config tftp://"+sServerIP+"/"+sFileName1+".cfg");
		}
		else
		{
			strToWaitNotFound();
		}

		sleep(1000);
		next();
	}
	
	void backupGPONZTEC320()
	{
		backup.renameFile.sOldFileName = backup.frmMainMenu.sFolder + "\\" +"startrun.dat";
		backup.renameFile.sNewFileName = backup.frmMainMenu.sFolder + "\\" + sFileName1 + ".dat";
		sFileName2 = backup.renameFile.sNewFileName;						
		backup.vtFileName.addElement(new String[]{sDeviceSystemName,
				  							      sFileName2});
		
		if(backup.renameFile!=null)
		{
			backup.renameFile.start();
		}
		
		sendCommand("enable");

		if(waitForString("rd:", 3))
		{
			sendCommand(sEnablePass);
		}
		
		sleep(1000);
		
		sendCommand("write");
				
		if(waitForString("#", 60))
		{
			sendCommand("file upload cfg-startup startrun.dat tftp ipaddress "+sServerIP);
		}
		else
		{
			strToWaitNotFound();
		}

		sleep(30000);
		next();
	}
	
	private void backupGPONAlcatel()
	{
		sendCommand("exit all");

		if(waitForString("#", 5))
		{
			sendCommand("admin software-mngt ihub database save-protected");
		}
		else
		{
			strToWaitNotFound();
		}

		sleep(5000);
		
		sendCommand("exit all");
		
		if(waitForString("#", 5))
		{
			sendCommand("configure system security filetransfer protocol tftp");
		}
		else
		{
			strToWaitNotFound();
		}
		
		sendCommand("exit all");
		
		if(waitForString("#", 5))
		{
			sendCommand("admin software-mngt database upload actual-active:"+sServerIP+":/"+sFileName1+"/dm_complete.tar");
		}
		else
		{
			strToWaitNotFound();
		}
		
		sleep(1000);
		next();
	}
		
	private void backupGPONHuaweiMA5608T()
	{		
		sendCommand("enable");

		if(waitForString("rd:", 3))
		{
			sendCommand(sEnablePass);
		}
		
		sendCommand("");
		
		if(waitForString("#", 5))
		{
			sendCommand("backup configuration tftp "+sServerIP+" "+sFileName1);
			
			if(waitForString("n]:", 5))
			{
				sendCommand("y");
			}
		}
		else
		{
			strToWaitNotFound();
		}
				
		sleep(1000);
		next();
	}
	
	private void backupSWCisco3560()
	{
		sendCommand("enable");

		if(waitForString("rd:", 5))
		{
			sendCommand(sEnablePass);
		}
		else
		{
			strToWaitNotFound();
		}

		if(waitForString("#", 5)&&!isNotFound)
		{
			sendCommand("copy startup-config tftp:");
		}
		else
		{
			strToWaitNotFound();
		}
		
		if(waitForString("]?", 5)&&!isNotFound)
		{
			sendCommand(sServerIP);
		}
		else
		{
			strToWaitNotFound();
		}
		
		if(waitForString("]?", 5)&&!isNotFound)
		{
			sendCommand(sFileName1+".cfg");
		}
		else
		{
			strToWaitNotFound();
		}
		
		sleep(1000);
		next();
	}
	
	private void sendCommand(String sCommand)
	{
		isSend = true;
		sendReceive.send.sendData(sCommand);
	}
	
	private void sleep(int t)
	{
		try 
		{
			Thread.sleep(t);
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
			log4j.error("", e);
		}
	}
	
	void start()
	{
		isRun = true;
		thread = null;
		sendReceive = null;
		
		backup.frmMainMenu.startTFTPServer();
		
		if(sendReceive==null)
		{
			sendReceive = new SendReceive(this);
		}
		
		if(thread==null)
		{
			thread = new Thread(this);
		}
		
		if(thread!=null)
		{
			try
			{
				thread.start();
			}
			catch(Exception e)
			{
				e.printStackTrace();
				log4j.error("", e);
				next();
			}
		}
	}
	
	void stop()
	{	
		isRun = false;
		
		if(sendReceive!=null)
		{
			if(sendReceive.receive!=null)
			{
				sendReceive.receive.isTimeOut = true;
			}
			
			sendReceive.disconnect();
		}

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
	
	private boolean waitForString(String sToWait,
								  int waitTimedOut)
	{		
		isNotFound = false;
		
		if((sendReceive!=null)&&
		   (sendReceive.send!=null)&&
		   (sendReceive.receive!=null))
		{
			sendReceive.receive.isWait1 = true;
			sendReceive.receive.waitForString(sToWait,
					                          waitTimedOut);
			
			synchronized(this)
			{
				try 
				{
					this.wait();
				} 
				catch (InterruptedException e) 
				{
					e.printStackTrace();
					log4j.error("", e);
					next();
				}
			}
		}
		
		if(sendReceive.receive.haveString)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	private boolean waitForString(String sToWait1,
			                      String sToWait2,
			                      int waitTimedOut)
	{		
		isNotFound = false;
		
		if((sendReceive!=null)&&
		   (sendReceive.send!=null)&&
		   (sendReceive.receive!=null))
		{
			sendReceive.receive.isWait1 = false;
			sendReceive.receive.waitForString(sToWait1,
					                          sToWait2,
					                          waitTimedOut);
			
			synchronized(this)
			{
				try 
				{
					this.wait();
				} 
				catch (InterruptedException e) 
				{
					e.printStackTrace();
					log4j.error("", e);
					next();
				}
			}
		}
		
		if(sendReceive.receive.haveString)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	private void strToWaitNotFound()
	{
		isNotFound = true;
		setDeviceColor(sDeviceSystemName, Color.PINK, Color.BLACK);
		backup.frmMainMenu.dispOutput(">>STRING TO WAIT NOT FOUND TO SEND COMMAND.");
		System.out.println(">>STRING TO WAIT NOT FOUND TO SEND COMMAND.");
	}
}