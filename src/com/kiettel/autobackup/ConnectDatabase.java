/*	Chuong trinh AutoBackup
 *  Tac gia: Truong Tuan Kiet
 *  Email: kiettel@gmail.com
 *  Mobile: 091.123.0123 hoac 098.68.68.498
 *  Ngay bat dau thuc hien: 08/06/2013
 */

package com.kiettel.autobackup;
import java.sql.*;

import javax.swing.JOptionPane;

import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.Logger;


public class ConnectDatabase
{
	private static final Logger log4j = Logger.getLogger(ConnectDatabase.class);
	static Connection con;
	static boolean isConnect = false;
	static Statement st;
	static String sDriver = "",
			      sURL = "",
			      query = "";
	static ResultSet rs;
	private FrmMainMenu frmMainMenu;
	
	public ConnectDatabase(FrmMainMenu frmMainMenu)
	{
		PropertyConfigurator.configure("log4j.properties");
		this.frmMainMenu = frmMainMenu;
		sDriver = "sun.jdbc.odbc.JdbcOdbcDriver";
		sURL = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=AutoBackup-Database.mdb";
	}
	
	void connect()
	{
		try 
		{
			Class.forName(sDriver);
			System.out.println("ConnectDB.java > Load Driver OK");
			
			if(con==null)
			{
				con = DriverManager.getConnection(sURL, "", "");
			}
			
			System.out.println("Connect.java > Get Connection OK");
			isConnect = true;		
			
			try
			{			
				if(st==null)
				{
					st = con.createStatement();
				}
				
				query = "SELECT * FROM Directory";
				
				rs = st.executeQuery(query);
				
				if(rs.next())
				{	
					frmMainMenu.sFolder = rs.getString("Directory");
					frmMainMenu.modTFTPServerFile();
				}
			}		
			catch(Exception e)
			{
				e.printStackTrace();
				log4j.error("", e);
				JOptionPane.showMessageDialog(frmMainMenu,
						                      "LỖI KẾT NỐI ĐẾN CƠ SỞ DỮ LIỆU:\n"+e.toString(),
						                      "ERROR", 
						                      JOptionPane.ERROR_MESSAGE);
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			isConnect = false;
		}
	}
	
	void changeDirectory()
	{
		if(isConnect)
		{
			try 
			{
				String query = "SELECT * FROM Directory ";
				
				rs = st.executeQuery(query);
				
				if (rs.next()) 
				{
					query = "UPDATE Directory "+
							"SET Directory='"+frmMainMenu.sFolder+"'";
					
					int aff = st.executeUpdate(query);
					
					if(aff!=0)
					{
						
					}
				} 
				else 
				{
				
				}
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				log4j.error("", e);
				JOptionPane.showMessageDialog(frmMainMenu,
										      "LỖI KẾT NỐI ĐẾN CƠ SỞ DỮ LIỆU:\n"+e.toString(),
						                      "ERROR",
						                      JOptionPane.ERROR_MESSAGE);
			}		
		}
	}

	void close()
	{
		try
		{
			if(con!=null)
			{
				con.close();
			}
			if(st!=null)
			{
				st.close();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			log4j.error("", e);
		}
	}
}