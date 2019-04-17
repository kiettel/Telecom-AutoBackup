/*	Chuong trinh AutoBackup
 *  Tac gia: Truong Tuan Kiet
 *  Email: kiettel@gmail.com
 *  Mobile: 091.123.0123 hoac 098.68.68.498
 *  Ngay bat dau thuc hien: 08/06/2013
 */

package com.kiettel.autobackup;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;


public class Main 
{	
	private static final Logger log4j = Logger.getLogger(Main.class);
	public static FrmMainMenu mm;
	private static final String VERSION = "AutoBackup version 8.0.0.0";

	public static void main(String[] args) 
	{
		PropertyConfigurator.configure("log4j.properties");
		
		SwingUtilities.invokeLater
		(
			new Runnable() 
			{  
				public void run() 
				{
					log4j.info(VERSION);
					mm = new FrmMainMenu(VERSION);
				}
			}
		);
	}
}