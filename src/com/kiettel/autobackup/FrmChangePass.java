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


public class FrmChangePass extends JPanel implements ActionListener, KeyListener
{
	private static final Logger log4j = Logger.getLogger(FrmChangePass.class);
	private static final long serialVersionUID = 1L;
	private JPanel p0,
	               p1,
	               p2;
	private JLabel lbOldPass,
	       		   lbNewPass,
	       		   lbReTypePass;
	JTextField txtOldPass,
	           txtNewPass,
	           txtReTypePass;	
	private JButton btOK,
	                btCancel;
	private int x,
	            y;
	private FrmMainMenu frmMainMenu;
	
	public FrmChangePass(FrmMainMenu frmMainMenu)
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
		p0.setLayout(null);
		p0.setBounds(5, 5, 300, 180);
		p0.setFont(new Font("Times New Roman", Font.PLAIN, 14));
		p0.setBorder(etchedBorder);
		
		p1 = new JPanel();
		p1.setBounds(5, 5, 280, 100);
		p1.setLayout(new GridLayout(3,2));
		
		p2 = new JPanel();
		p2.setBounds(5, 120, 280, 40);
		p2.setLayout(new FlowLayout());
		
		lbOldPass = new JLabel("Old Password:");
		lbOldPass.setFont(new Font("Times New Roman", Font.PLAIN, 14));
		lbOldPass.setForeground(Color.BLUE);
		txtOldPass = new JPasswordField(10);
		txtOldPass.setFont(new Font("Times New Roman", Font.PLAIN, 14));
		txtOldPass.setForeground(Color.BLUE);
		txtOldPass.addKeyListener(this);

		lbNewPass = new JLabel("New Password:");
		lbNewPass.setFont(new Font("Times New Roman", Font.PLAIN, 14));
		lbNewPass.setForeground(Color.BLUE);
		txtNewPass = new JPasswordField(10);
		txtNewPass.setFont(new Font("Times New Roman", Font.PLAIN, 14));
		txtNewPass.setForeground(Color.BLUE);
		txtNewPass.addKeyListener(this);

		lbReTypePass = new JLabel("Retype new Password:");
		lbReTypePass.setFont(new Font("Times New Roman", Font.PLAIN, 14));
		lbReTypePass.setForeground(Color.BLUE);
		txtReTypePass = new JPasswordField(10);
		txtReTypePass.setFont(new Font("Times New Roman", Font.PLAIN, 14));
		txtReTypePass.setForeground(Color.BLUE);
		txtReTypePass.addKeyListener(this);
		
		btOK = new JButton("  OK  ");
		btOK.setFont(new Font("Times New Roman", Font.PLAIN, 14));
		btOK.addActionListener(this);
		btOK.addKeyListener(this);

		btCancel = new JButton("Exit");
		btCancel.setFont(new Font("Times New Roman", Font.PLAIN, 14));
		btCancel.addActionListener(this);

		p1.add(lbOldPass);
		p1.add(txtOldPass);
		p1.add(lbNewPass);
		p1.add(txtNewPass);
		p1.add(lbReTypePass);
		p1.add(txtReTypePass);
		
		p2.add(btOK);
		p2.add(btCancel);
		
		p0.add(p1);
		p0.add(p2);
		
		this.add("Center", p0);
	}
	
	private void doiPass() 
	{
		if(ConnectDatabase.isConnect)
		{
			String sOldPass = txtOldPass.getText();
			String sNewPass = txtNewPass.getText();
			String sReTypePass = txtReTypePass.getText();
				
			if(sNewPass.isEmpty()||
			   sReTypePass.isEmpty()||
			   sOldPass.isEmpty())
			{
				JOptionPane.showMessageDialog(this,
						                      "PLEASE INPUT FULL INFORMATIONS.",
						                      "ERROR",
						                      JOptionPane.ERROR_MESSAGE);
			}
			
			else if(!sNewPass.equals(sReTypePass))
			{
				JOptionPane.showMessageDialog(this,
						                      "NEW PASSWORD DO NOT MATCH.",
						                      "ERROR",
						                      JOptionPane.ERROR_MESSAGE);
			}
			
			else if((!sNewPass.isEmpty())&&(sNewPass.equals(sReTypePass)))
			{
				try 
				{
					String query = "SELECT * FROM User " +
							       "WHERE UserName='admin' AND UserPass='"+sOldPass+"'";
					
					ConnectDatabase.rs = ConnectDatabase.st.executeQuery(query);
					
					if (ConnectDatabase.rs.next()) 
					{
						query = "UPDATE User "+
						        "SET UserPass='"+sNewPass+"' "+
							    "WHERE UserName='admin'";
						
						int aff = ConnectDatabase.st.executeUpdate(query);
						
						if(aff!=0)
						{
							JOptionPane.showMessageDialog(frmMainMenu.frmChangePass,
									       				  "	      CHANGE PASSWORD SUCCESSFUL.",
									       				  "", JOptionPane.PLAIN_MESSAGE);

							frmMainMenu.d2.setVisible(false);
						}
					} 
					else 
					{
						JOptionPane.showMessageDialog(frmMainMenu.frmChangePass,
								                      "OLD PASSWORD INCORRECT.",
								                      "ERROR",
								                      JOptionPane.ERROR_MESSAGE);
					}
				}
				catch (Exception e) 
				{
					log4j.error("", e);
					JOptionPane.showMessageDialog(frmMainMenu,
							                      "CONNECT TO DATABASE ERROR:\n"+e.toString(),
							                      "ERROR",
							                      JOptionPane.ERROR_MESSAGE);
				}		
			}
		}		
		else
		{
			JOptionPane.showMessageDialog(this,
					                      "CONNECT TO DATABASE ERROR.",
					                      "ERROR",
					                      JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void btThoat()
	{
		int i = JOptionPane.showConfirmDialog(this,
				                              "DO YOU WANT TO EXIT ?" ,
				                              "EXIT",
				                              JOptionPane.YES_NO_OPTION);
		if(i==0)
		{
			frmMainMenu.d2.setVisible(false);
		}
	}
	
	public void actionPerformed(ActionEvent ae) 
	{
		if(ae.getSource()==btOK)
		{
			doiPass();
		}
		else if(ae.getSource()==btCancel)
		{
			btThoat();
		}
	}
	
	public void keyPressed(KeyEvent e) 
	{
		 int key = e.getKeyCode();
	       
		 if(((e.getSource()==btOK)&&(key==KeyEvent.VK_ENTER))||
		    ((e.getSource()==txtOldPass)&&(key==KeyEvent.VK_ENTER))||
		    ((e.getSource()==txtNewPass)&&(key==KeyEvent.VK_ENTER))||	    	
			((e.getSource()==txtReTypePass)&&(key==KeyEvent.VK_ENTER)))
		 {
			 doiPass();
		 }
		 
		 else if((e.getSource()==btCancel)&&(key==KeyEvent.VK_ENTER))
		 {
			 btThoat();
		 }
	}

	public void keyReleased(KeyEvent e) 
	{

	}

	public void keyTyped(KeyEvent e) 
	{
	
	}
}