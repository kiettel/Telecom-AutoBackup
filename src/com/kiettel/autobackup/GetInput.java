/*	Chuong trinh AutoBackup
 *  Tac gia: Truong Tuan Kiet
 *  Email: kiettel@gmail.com
 *  Mobile: 091.123.0123 hoac 098.68.68.498
 *  Ngay bat dau thuc hien: 08/06/2013
 */

package com.kiettel.autobackup;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JOptionPane;

import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.Logger;


public class GetInput 
{
	private static final Logger log4j = Logger.getLogger(GetInput.class);
	private String sDeviceSystemName = "",
		           sDeviceName = "",
		           sDeviceIP = "",
		           sDeviceType = "",
		           sManufacturer = "",
		           sDeviceUser = "",
		           sDevicePass = "",
		           sEnablePass = "",
		           sServerIP = "",
		           sFileOnDevice = "",
		           sTRAPReceiverIP = "";
	static Vector<String[]> vtDevice;
	static Vector<String> vtTRAPReceiver;
	private FrmMainMenu frmMainMenu;
	
	public GetInput(FrmMainMenu frmMainMenu)
	{
		PropertyConfigurator.configure("log4j.properties");
		this.frmMainMenu = frmMainMenu;
		vtDevice = new Vector<String[]>();
		vtTRAPReceiver = new Vector<String>();
	}

	void addDeviceToMainMenu()
	{
		if(ConnectDatabase.isConnect)
		{
			try
			{
				String query = "SELECT * FROM Devices ORDER BY DeviceSystemName";
				ConnectDatabase.rs = ConnectDatabase.st.executeQuery(query);
				
				int line = 0;
				Vector<String> vt = new Vector<String>();
				Vector<String> vt2 = new Vector<String>();
				
				while(ConnectDatabase.rs.next())
				{	
					line++;
					sDeviceSystemName = ConnectDatabase.rs.getString("DeviceSystemName");
					
					if((sDeviceSystemName==null)||
					   (sDeviceSystemName.isEmpty()))
					{
						vt.addElement("Unknown");
					}
					else
					{
						vt.addElement(sDeviceSystemName);
					}
					
					sDeviceIP = ConnectDatabase.rs.getString("DeviceIP");
					
					if((sDeviceIP==null)||
					   (sDeviceIP.isEmpty()))
					{
						vt2.addElement("Unknown");
					}
					else
					{
						vt2.addElement(sDeviceIP);
					}
				}
				
				frmMainMenu.deviceNumber = line;
				frmMainMenu.cbDevice = new JCheckBox[line];
				
				for(int x=0; x<line; x++)
				{
					frmMainMenu.cbDevice[x] = new JCheckBox(vt.elementAt(x).toString());
					frmMainMenu.cbDevice[x].setBackground(Color.WHITE);
					frmMainMenu.cbDevice[x].addActionListener(frmMainMenu);
					frmMainMenu.cbDevice[x].setToolTipText(vt2.elementAt(x).toString());
					frmMainMenu.p11.add(frmMainMenu.cbDevice[x]);
					frmMainMenu.vtDevice.addElement(frmMainMenu.cbDevice[x]);
				}
				
				frmMainMenu.p11.setLayout(new GridLayout(line+1,1));
				
				vt = null;
				vt2 = null;
			}		
			catch(Exception e)
			{
				e.printStackTrace();
				log4j.error("", e);
				JOptionPane.showMessageDialog(frmMainMenu,
						                      "CONNECT TO DATABASE ERROR:\n"+e.toString(),
						                      "ERROR",
						                      JOptionPane.ERROR_MESSAGE);
			}
		}
		else
		{
			JOptionPane.showMessageDialog(frmMainMenu,
					                      "CONNECT TO DATABASE ERROR.",
					                      "ERROR",
					                      JOptionPane.ERROR_MESSAGE);
		}	
	}
	
	void getSelectedDevice()
	{
		vtDevice.removeAllElements();
		
		if(ConnectDatabase.isConnect)
		{	
			for(int i=0; i<frmMainMenu.deviceNumber; i++)
			{
				if(((JCheckBox)frmMainMenu.vtDevice.elementAt(i)).isSelected())
				{
					String sDevName = ((JCheckBox)frmMainMenu.vtDevice.elementAt(i)).getText();
					
					try
					{			
						String query = "SELECT * FROM Devices " +
						               "WHERE DeviceSystemName='"+sDevName+"'";
						
						ConnectDatabase.rs = ConnectDatabase.st.executeQuery(query);
						
						if(ConnectDatabase.rs.next())
						{	
							sDeviceName = ConnectDatabase.rs.getString("DeviceName");
							sDeviceSystemName = ConnectDatabase.rs.getString("DeviceSystemName");
							sDeviceIP = ConnectDatabase.rs.getString("DeviceIP");
							sDeviceType = ConnectDatabase.rs.getString("Type");
							sManufacturer = ConnectDatabase.rs.getString("Manufacturer");
							sDeviceUser = ConnectDatabase.rs.getString("User");
							sDevicePass = ConnectDatabase.rs.getString("Pass");
							sEnablePass = ConnectDatabase.rs.getString("EnablePass");
							sServerIP = ConnectDatabase.rs.getString("ServerIP");
							sFileOnDevice = ConnectDatabase.rs.getString("FileName");
							
							if(sDeviceName!=null&&
							  !sDeviceName.isEmpty())
							{
								sDeviceName = sDeviceName.replaceAll("\\s+","");
							}
							else
							{
								sDeviceName = "Unknown";
							}
							
							if(sDeviceSystemName!=null&&
							   !sDeviceSystemName.isEmpty())
							{
								sDeviceSystemName = sDeviceSystemName.replaceAll("\\s+","");
							}
							else
							{
								sDeviceSystemName = "Unknown";
							}
							
							if(sDeviceIP!=null&&
							   !sDeviceIP.isEmpty())
							{
								sDeviceIP = sDeviceIP.replaceAll("\\s+","");
							}
							else
							{
								sDeviceIP = "Unknown";
							}
							
							if(sDeviceType!=null&&
							   !sDeviceType.isEmpty())
							{
								sDeviceType = sDeviceType.replaceAll("\\s+","");
							}
							else
							{
								sDeviceType = "Unknown";
							}
							
							if(sManufacturer!=null&&
							   !sManufacturer.isEmpty())
							{
								sManufacturer = sManufacturer.replaceAll("\\s+","");
							}
							else
							{
								sManufacturer = "Unknown";
							}
							
							if(sDeviceUser!=null&&
							   !sDeviceUser.isEmpty())
							{
								sDeviceUser = sDeviceUser.replaceAll("\\s+","");
							}
							else
							{
								sDeviceUser = "Unknown";
							}
							
							if(sDevicePass!=null&&
							   !sDevicePass.isEmpty())
							{
								sDevicePass = sDevicePass.replaceAll("\\s+","");
							}
							else
							{
								sDevicePass = "Unknown";
							}
							
							if(sEnablePass!=null&&
							   !sEnablePass.isEmpty())
							{
								sEnablePass = sEnablePass.replaceAll("\\s+","");
							}
							else
							{
								sEnablePass = "Unknown";
							}
							
							if(sServerIP!=null&&
							   !sServerIP.isEmpty())
							{
								sServerIP = sServerIP.replaceAll("\\s+","");
							}
							else
							{
								sServerIP = "Unknown";
							}
							
							if(sFileOnDevice!=null&&
							   !sFileOnDevice.isEmpty())
							{
								sFileOnDevice = sFileOnDevice.replaceAll("\\s+","");
							}
							else
							{
								sFileOnDevice = "Unknown";
							}
							
							vtDevice.addElement(new String[]{sDeviceName,
									                         sDeviceSystemName,
									                         sDeviceIP,
									                         sDeviceType,
									                         sManufacturer,
									                         sDeviceUser,
									                         sDevicePass,
									                         sServerIP,
									                         sFileOnDevice,
									                         sEnablePass});
						}
					}		
					catch(Exception e)
					{
						e.printStackTrace();
						log4j.error("", e);
						JOptionPane.showMessageDialog(frmMainMenu,
								                      "CONNECT TO DATABASE ERROR:\n"+e.toString(),
								                      "ERROR",
								                      JOptionPane.ERROR_MESSAGE);
					}
				}
			} 
			
			frmMainMenu.deviceSelected = vtDevice.size();
		}
		else
		{
			JOptionPane.showMessageDialog(frmMainMenu,
					                      "CONNECT TO DATABASE ERROR.",
					                      "ERROR",
					                      JOptionPane.ERROR_MESSAGE);
		}
	}
	
	void getTRAPReceiverIP()
	{
		vtTRAPReceiver.removeAllElements();
		
		if(ConnectDatabase.isConnect)
		{
			try
			{
				String query = "SELECT * FROM TRAPReceiver ORDER BY TRAPReceiverIP";
				ConnectDatabase.rs = ConnectDatabase.st.executeQuery(query);
				
				while(ConnectDatabase.rs.next())
				{	
					sTRAPReceiverIP = ConnectDatabase.rs.getString("TRAPReceiverIP");
										
					if((sTRAPReceiverIP==null)||
					   (sTRAPReceiverIP.isEmpty()))
					{
						sTRAPReceiverIP = "127.0.0.1";
					}
					else
					{
						sTRAPReceiverIP = sTRAPReceiverIP.replaceAll("\\s+","");
						vtTRAPReceiver.addElement(sTRAPReceiverIP);
					}
				}
			}		
			catch(Exception e)
			{
				e.printStackTrace();
				log4j.error("", e);
				JOptionPane.showMessageDialog(frmMainMenu,
						                      "CONNECT TO DATABASE ERROR:\n"+e.toString(),
						                      "ERROR",
						                      JOptionPane.ERROR_MESSAGE);
			}
		}
		else
		{
			JOptionPane.showMessageDialog(frmMainMenu,
					                      "CONNECT TO DATABASE ERROR.",
					                      "",
					                      JOptionPane.ERROR_MESSAGE);
		}	
	}
}