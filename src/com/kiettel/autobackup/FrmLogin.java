/*	Chuong trinh AutoBackup
 *  Tac gia: Truong Tuan Kiet
 *  Email: kiettel@gmail.com
 *  Mobile: 091.123.0123 hoac 098.68.68.498
 *  Ngay bat dau thuc hien: 08/06/2013
 */

package com.kiettel.autobackup;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.Logger;


public class FrmLogin extends JPanel implements ActionListener, KeyListener
{
	private static final Logger log4j = Logger.getLogger(FrmLogin.class);
	private static final long serialVersionUID = 1L;
	private JPanel p0,
	               p1,
	               p2;
	private JLabel lbUser,
	               lbPass;
	JTextField txtUser,
	           txtPass;	
	private JButton btOK,
	        	    btCancel;
	static boolean isLog = false;
	private int x,
	            y;
	private FrmMainMenu frmMainMenu;
	
	public FrmLogin(FrmMainMenu frmMainMenu)
	{
		PropertyConfigurator.configure("log4j.properties");
		this.frmMainMenu = frmMainMenu;
		x = FrmMainMenu.xSize;
		y = FrmMainMenu.ySize;
		this.setSize(300, 150);
		this.setLocation(x/2-150, y/2-100);
		initGui();
	}
	
	private void initGui()
	{
		this.setVisible(true);
		this.setLayout(null);
		EtchedBorder etchedBorder = new EtchedBorder(EtchedBorder.RAISED);
		
		p0 = new JPanel();
		p0.setBounds(5, 5, 280, 130);
		p0.setLayout(null);
		p0.setBorder(etchedBorder);
		
		p1 = new JPanel();
		p1.setBounds(20, 10, 250, 60);
		p1.setLayout(new GridLayout(2,2));
		
		p2 = new JPanel();
		p2.setBounds(20, 80, 250, 40);
		p2.setLayout(new FlowLayout());
		
		this.add("Center", p0);
		
		lbUser = new JLabel("User name:");
		lbUser.setFont(new Font("Times New Roman", Font.PLAIN, 15));
		lbUser.setForeground(Color.BLUE);
		txtUser = new JTextField(10);
		txtUser.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		txtUser.setForeground(Color.BLUE);
		txtUser.addKeyListener(this);
		
		lbPass = new JLabel("Password:");
		lbPass.setFont(new Font("Times New Roman", Font.PLAIN, 15));
		lbPass.setForeground(Color.BLUE);
		txtPass = new JPasswordField(10);
		txtPass.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		txtPass.setForeground(Color.BLUE);
		txtPass.addKeyListener(this);
		
		btOK = new JButton("Login");
		btOK.setFont(new Font("Times New Roman", Font.PLAIN, 14));
		btOK.addKeyListener(this);
		btOK.addActionListener(this);

		btCancel = new JButton("   Exit  ");
		btCancel.setFont(new Font("Times New Roman", Font.PLAIN, 14));
		btCancel.addActionListener(this);
		
		p1.add(lbUser);
		p1.add(txtUser);
		p1.add(lbPass);
		p1.add(txtPass);
		p2.add(btOK);
		p2.add(btCancel);
		
		p0.add(p1);
		p0.add(p2);
	}
	
	private boolean checkUser(String user, String pass) 
	{
		try 
		{
			String query = "SELECT * FROM User WHERE UserName='" + user+
					       "' AND UserPass='" + pass + "'";
			ConnectDatabase.rs = ConnectDatabase.st.executeQuery(query);
			
			if (ConnectDatabase.rs.next()) 
			{
				return true;				
			} 
			else 
			{
				return false;
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			log4j.error("", e);
			JOptionPane.showMessageDialog(frmMainMenu,
					                      "CONNECT TO DATABASE ERROR:\n"+e.toString(),
					                      "ERROR",
					                      JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}
	
	private void login()
	{
		String u = txtUser.getText();
		String p = txtPass.getText();
		
		if((u.isEmpty())||(p.isEmpty()))
		{
			JOptionPane.showMessageDialog(frmMainMenu,
					                     "PLEASE INPUT YOUR USER NAME AND PASSWORD.",
					                     "ERROR",
					                     JOptionPane.ERROR_MESSAGE);
		}
		else
		{
			if(ConnectDatabase.isConnect)
			{
				u = u.replaceAll("\\s+","");
				
				if(checkUser(u, p)) 
				{
					isLog = true;
					frmMainMenu.setLoginView(false);
				} 	
				else 
				{
					isLog = false;
					frmMainMenu.setLoginView(true);
					JOptionPane.showMessageDialog(frmMainMenu,
							                      "USERNAME OR PASSWORD INCORRECT.",
							                      "ERROR",
							                      JOptionPane.ERROR_MESSAGE);
				}
			}			
			else
			{
				JOptionPane.showMessageDialog(frmMainMenu,
						                      "CONECT TO DATABASE FAILED.",
						                      "ERROR",
						                      JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	private void btThoat()
	{
		int i = JOptionPane.showConfirmDialog(this,
				                              "    DO YOU WANT TO EXIT ?" ,
				                              "EXIT",
				                              JOptionPane.YES_NO_OPTION);
		
		if(i==0)
		{
			frmMainMenu.connectDatabase.close();
			System.exit(0);
		}	
	}
	
	public void actionPerformed(ActionEvent ae) 
	{
		if(ae.getSource()==btOK)
		{
			login();
		}
		else if(ae.getSource()==btCancel)
		{
			btThoat();
		}
	}
	
	 public void keyPressed(KeyEvent e) 
	 {
		 int key = e.getKeyCode();
	      
		 if(key==KeyEvent.VK_ENTER)
		 {
			 if((e.getSource()==btOK)||
			    (e.getSource()==txtUser)||
			    (e.getSource()==txtPass))	    		
			 {
			 	login();
			 }
			       
			 else if(e.getSource()==btCancel)
			 {
				 btThoat();
			 } 
		 }
	 }

	public void keyReleased(KeyEvent arg0) 
	{
		
	}

	public void keyTyped(KeyEvent arg0) 
	{
		
	}
}