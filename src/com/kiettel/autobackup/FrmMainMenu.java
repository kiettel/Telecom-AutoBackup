/*	Chuong trinh AutoBackup
 *  Tac gia: Truong Tuan Kiet
 *  Email: kiettel@gmail.com
 *  Mobile: 091.123.0123 hoac 098.68.68.498
 *  Ngay bat dau thuc hien: 08/06/2013
 */

package com.kiettel.autobackup;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.Vector;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.Logger;


public class FrmMainMenu extends JFrame implements ActionListener, WindowListener, MouseListener
{
	private static final long serialVersionUID = 1L;
	private static final Logger log4j = Logger.getLogger(FrmMainMenu.class);
	JPanel p0,
	       p1,
	       p11,
	       p2,
	       p3,
	       p4;
	private JMenuBar mb;
	private JMenu mnFile, 
	              mnHelp,
	              mnDonate;
	private JMenuItem miLock, 
                      miDoiPass,
	                  miExit,
			          miHelp, 
			          miAbout,
			          miDonate;
	private MenuItem miAbout2,
	                 miHelp2,
	                 miAutoBackup,
	                 miQuit,
	                 miDonate2;
	private PopupMenu menu;
	private JToolBar tb;
	private JButton [] btTB; 
	private TrayIcon trayIcon;
	private SystemTray sysTray;
	static int xSize = 0,
	           ySize = 0;
	boolean isAutoBackup = false,
			isTFTPStarted = false;
	int	deviceNumber = 0,
		deviceSelected = 0;
	private Image img;
	JDialog d1,
			d2;
	private JScrollPane scrDevice,
				        scrOutput;
	private JButton btAuto,
	                btStop,
	                btManual,
	                btFolderChose,
	                btDonate;    
	private JTextField tfNumBack,
			           tfDirectory;
	private JComboBox<String> cbHour,
		                      cbMinute,
		                      cbSecond;
	JLabel lbTime,
	       lbHour,
	       lbMinute,
	       lbSecond,
	       lbStatus;
	private JCheckBox cbAllTB; 
	JCheckBox [] cbDevice;
	JTextArea taOutput;
	Vector<Object> vtDevice;
	String sFolder = "",
		   sH = "0",
		   sM = "0",
		   sS = "0",
		   sVersion = "",
		   sTRAPReceiverIP = "";
	ConnectDatabase connectDatabase;
	private GetInput getInput;
	private FrmLogin frmLogin;
	FrmChangePass frmChangePass;
	private Backup backup;
	TRAPSender trapSender;
	
	public FrmMainMenu(String sVersion) 
	{
		PropertyConfigurator.configure("log4j.properties");
		this.sVersion = sVersion;
		connectDatabase = new ConnectDatabase(this);
		connectDatabase.connect();
		getInput = new GetInput(this);
		vtDevice = new Vector<Object>();
				
		initGui(sVersion);
		
		int deviceCount = vtDevice.size();
		
		if(deviceCount==0)
		{
			cbAllTB.setEnabled(false);
			JOptionPane.showMessageDialog(this,
										  "NO DEVICE FOUND IN DATABASE.",
										  "ERROR",
										  JOptionPane.ERROR_MESSAGE);
		}
		else
		{
			cbAllTB.setEnabled(true);
		}
				
		startTFTPServer();
	}
	
	private void initGui(String s)
	{
		this.setTitle(s);
		this.addWindowListener(this);
		this.setVisible(true);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension dimension = tk.getScreenSize();
		xSize = dimension.width;
		ySize = dimension.height;
		
		this.setSize(xSize,ySize-25);
		img = Toolkit.getDefaultToolkit().getImage("res/icon.jpg");
		
		if(SystemTray.isSupported())
		{
			menu = new PopupMenu();
			
			miAutoBackup = new MenuItem(sVersion);
			miAutoBackup.addActionListener(this);
			
			miAbout2 = new MenuItem("About");
			miAbout2.addActionListener(this);

			miHelp2 = new MenuItem("Help");
			miHelp2.addActionListener(this);
			
			miDonate2 = new MenuItem("Ủng hộ");
			miDonate2.addActionListener(this);
			
			miQuit = new MenuItem("Exit");
			miQuit.addActionListener(this);		

			menu.add(miAutoBackup);
			menu.addSeparator();
			menu.add(miAbout2);
			menu.addSeparator();
			menu.add(miHelp2);
			menu.addSeparator();
			menu.add(miDonate2);
			menu.addSeparator();
			menu.add(miQuit);
			
			sysTray = SystemTray.getSystemTray();
			trayIcon = new TrayIcon(img, sVersion, menu);
			trayIcon.addActionListener(this);
			trayIcon.setImageAutoSize(true);
			trayIcon.addMouseListener(this);
			
			try
			{
				sysTray.add(trayIcon);
			}
			catch(AWTException e)
			{
				System.out.println("Add Tray Icon Error.");
				log4j.error("", e);
			}
		}
		else
		{
			System.out.println("System not support SystemTray.");
			log4j.error("System not support SystemTray.");
		}
		
		this.setIconImage(img);
		this.setLocation(0, 0);
		this.setLayout(new BorderLayout());
	
		p0 = new JPanel();
		p0.setLayout(new FlowLayout());
		p0.setBorder(new EtchedBorder(EtchedBorder.RAISED));
		
		TitledBorder t1 = new TitledBorder("Choose Devices:");
		t1.setTitleJustification(TitledBorder.CENTER);
		t1.setTitleColor(Color.BLUE);
		t1.setTitleFont(new Font("Times New Roman", Font.BOLD, 16));
			
		p1 = new JPanel();
		p1.setLayout(new FlowLayout());
		p1.setBorder(t1);

		cbAllTB = new JCheckBox("ROOT");
		cbAllTB.setForeground(Color.RED);
		cbAllTB.setBackground(Color.WHITE);
		cbAllTB.addActionListener(this);
		cbAllTB.setEnabled(false);
		p11 = new JPanel();
		p11.add(cbAllTB);
				
		getInput.addDeviceToMainMenu();
		getInput.getTRAPReceiverIP();
	
		TitledBorder t2 = new TitledBorder("Progress:");
		t2.setTitleJustification(TitledBorder.CENTER);
		t2.setTitleColor(Color.BLUE);
		t2.setTitleFont(new Font("Times New Roman", Font.BOLD, 16));
		
		scrDevice = new JScrollPane(p11,
                                    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                                    JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS); 
		scrDevice.setPreferredSize(new Dimension(FrmMainMenu.xSize/6, FrmMainMenu.ySize-250));
				
		p1.add(scrDevice);
	
		taOutput = new JTextArea();
		taOutput.setAutoscrolls(true);
		taOutput.setForeground(Color.GREEN);
		taOutput.setBackground(Color.BLACK);
		taOutput.setFont(new Font("Times New Roman", Font.BOLD, 14));
		taOutput.setCaretColor(Color.WHITE);
		taOutput.setEditable(false);
		taOutput.setWrapStyleWord(true);
		taOutput.setLineWrap(true);
		
		scrOutput = new JScrollPane(taOutput,
                                    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                                    JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS); 
		scrOutput.setPreferredSize(new Dimension(FrmMainMenu.xSize-(FrmMainMenu.xSize/4), FrmMainMenu.ySize-250));
		
		p2 = new JPanel();
		p2.setLayout(new FlowLayout());
		p2.setBorder(t2);
		p2.add(scrOutput);
		
		lbTime = new JLabel("Backup Time:");
		lbTime.setForeground(Color.BLUE);
		
		lbHour = new JLabel("Hour:");
		lbHour.setForeground(Color.BLUE);
		
		lbMinute = new JLabel("Minute:");
		lbMinute.setForeground(Color.BLUE);
		
		lbSecond = new JLabel("Second:");
		lbSecond.setForeground(Color.BLUE);
		
		tfDirectory = new JTextField("Destination folder: "+sFolder);
		tfDirectory.setForeground(Color.BLUE);
		tfDirectory.setFont(new Font("Times New Roman", Font.BOLD, 14));
		tfDirectory.setPreferredSize(new Dimension(FrmMainMenu.xSize/5, 32));
		tfDirectory.setEditable(false);
		
		btFolderChose = new JButton("Destination folder");
		btFolderChose.setFont(new Font("Times New Roman", Font.PLAIN, 14));
		btFolderChose.addActionListener(this);	
		
		tfNumBack = new JTextField("Devices Chosen: 0/"+deviceNumber);
		tfNumBack.setForeground(Color.BLUE);
		tfNumBack.setFont(new Font("Times New Roman", Font.BOLD, 14));
		tfNumBack.setPreferredSize(new Dimension(FrmMainMenu.xSize/6-10, 32));
		tfNumBack.setEditable(false);
		
		cbHour = new JComboBox<String>();
		cbHour.setForeground(Color.RED);
		cbHour.setEditable(false);
		cbHour.setEnabled(false);
		
		String[] sHour = new String[24];
		
		for(int h=0; h<24; h++)
		{
			sHour[h] = String.valueOf(h);
			cbHour.addItem(sHour[h]);
		}
		
        cbMinute = new JComboBox<String>();
        cbMinute.setForeground(Color.RED);
        cbMinute.setEditable(false);
        cbMinute.setEnabled(false);
        
        String[] sMinute = new String[60];
		
		for(int m=0; m<60; m++)
		{
			sMinute[m] = String.valueOf(m);
			cbMinute.addItem(sMinute[m]);
		}

		cbSecond = new JComboBox<String>();
		cbSecond.setForeground(Color.RED);
		cbSecond.setEditable(false);
		cbSecond.setEnabled(false);
		
		String[] sSecond = new String[60];
		
		for(int se=0; se<60; se++)
		{
			sSecond[se] = String.valueOf(se);
			cbSecond.addItem(sSecond[se]);
		}

		btAuto = new JButton("Auto Backup");
		btAuto.setFont(new Font("Times New Roman", Font.PLAIN, 14));
		btAuto.setEnabled(false);
		btAuto.addActionListener(this);
		
		btManual = new JButton("Manual Backup");
		btManual.setFont(new Font("Times New Roman", Font.PLAIN, 14));
		btManual.setEnabled(false);
		btManual.addActionListener(this);	

		Icon donateImg = null;
		
		try 
		{
			donateImg = new ImageIcon("res/icon/donate.png");
		} 
		catch (Exception ex) 
		{
			ex.printStackTrace();
			log4j.error("", ex);
		}
		
		btDonate = new JButton("Donate", donateImg);
		btDonate.setFont(new Font("Times New Roman", Font.PLAIN, 14));
		btDonate.setEnabled(true);
		btDonate.addActionListener(this);	
		
		btStop = new JButton("Stop");
		btStop.setFont(new Font("Times New Roman", Font.PLAIN, 14));
		btStop.setEnabled(false);
		btStop.addActionListener(this);	
		
		lbStatus = new JLabel("");
		lbStatus.setForeground(Color.BLUE);
		
		p3 = new JPanel();
		p3.setLayout(new FlowLayout());
		
		p3.add(tfDirectory);
		p3.add(btFolderChose);
		p3.add(tfNumBack);
		p3.add(lbTime);
		p3.add(lbHour);
		p3.add(cbHour);
		p3.add(lbMinute);
		p3.add(cbMinute);
		p3.add(lbSecond);
		p3.add(cbSecond);
		p3.add(btAuto);
		p3.add(btManual);
		p3.add(btStop);
		p3.add(btDonate);
		
		p4 = new JPanel();
		p4.setLayout(new FlowLayout());
		
		p4.add(lbStatus);
		
		p0.add(p1);
		p0.add(p2);
		p0.add(p3);
		p0.add(p4);
		
		this.getContentPane().add("Center", p0);
		
		menuBar();
		this.setJMenuBar(mb);
		
		toolBar();	
		
		cbHour.addActionListener(this);
        cbMinute.addActionListener(this);
		cbSecond.addActionListener(this);
		
		addLoginFrm();
		addLoginDialog();
	}
	
	private void menuBar()
	{
		mb = new JMenuBar();
		mb.setVisible(true);
		
		mnFile = new JMenu("File");
		mnFile.setFont(new Font("Times New Roman", Font.PLAIN, 14));
		
		miLock = new JMenuItem("Lock", new ImageIcon("res/icon/khoa.png"));
		miLock.setFont(new Font("Times New Roman", Font.PLAIN, 14));
		miLock.addActionListener(this);
		
		miDoiPass = new JMenuItem("Change Password", new ImageIcon("res/icon/doipass.png"));
		miDoiPass.setFont(new Font("Times New Roman", Font.PLAIN, 14));
		miDoiPass.addActionListener(this);
		
		mnHelp = new JMenu("Help");
		mnHelp.setFont(new Font("Times New Roman", Font.PLAIN, 14));

		miExit = new JMenuItem("Exit", new ImageIcon("res/icon/exit.jpg"));
		miExit.setFont(new Font("Times New Roman", Font.PLAIN, 14));
		miExit.addActionListener(this);
		
		miHelp = new JMenuItem("Help", new ImageIcon("res/icon/help.jpg"));		
		miHelp.setFont(new Font("Times New Roman", Font.PLAIN, 14));
		miHelp.addActionListener(this);
		
		miAbout = new JMenuItem("About", new ImageIcon("res/icon/about.jpg"));
		miAbout.setFont(new Font("Times New Roman", Font.PLAIN, 14));
		miAbout.addActionListener(this);
		
		mnDonate = new JMenu("Donate");
		mnDonate.setFont(new Font("Times New Roman", Font.PLAIN, 14));
		
		miDonate = new JMenuItem("Donate", new ImageIcon("res/icon/donate.png"));
		miDonate.setFont(new Font("Times New Roman", Font.PLAIN, 14));
		miDonate.addActionListener(this);

		mnFile.add(miLock);
		mnFile.add(miDoiPass);
		mnFile.add(miExit);

		mnHelp.add(miHelp);
		mnHelp.add(miAbout);
		
		mnDonate.add(miDonate);
		
		mb.add(mnFile);
		mb.add(mnHelp);
		mb.add(mnDonate);
	}
	
	private void toolBar()
	{
		tb = new JToolBar();
		tb.setBounds(new Rectangle(xSize, 50));
		this.add(tb, "North");
		tb.setVisible(true);
		
		String [] buttonLabel = {"Lock",
				                 "Change Password",
				                 "Help",
				                 "About",
				                 "Donate",
				                 "Exit"};
		String [] buttonIcon = {"res/icon/khoa.png",
				                "res/icon/doipass.png", 
				                "res/icon/help.jpg",
				                "res/icon/about.jpg",
				                "res/icon/donate.png",
				                "res/icon/exit.jpg"};
		
		try
		{
			ImageIcon [] imageIcon = new ImageIcon[buttonIcon.length];
			btTB = new JButton[buttonLabel.length];
			
			for(int i=0; i<buttonLabel.length; i++)
			{
				imageIcon[i] = new ImageIcon(buttonIcon[i]);
				btTB[i] = new JButton(imageIcon[i]);
				btTB[i].setToolTipText(buttonLabel[i]);
				btTB[i].addActionListener(this);
				tb.add(btTB[i]);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			log4j.error("", e);
		}
    }
	
	private void addLoginFrm()
	{
		if(frmLogin==null)
		{
			frmLogin = new FrmLogin(this);
		}
	}
	
	void setLoginView(boolean visible)
	{
		d1.setVisible(visible);
	}
	
	private void addLoginDialog()
	{
		if(d1==null)
		{
			d1 = new JDialog(this, "Login", true);
			d1.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			Image icon = Toolkit.getDefaultToolkit().getImage("res/icon/khoa.png");
			d1.setIconImage(icon);
		}
	
		d1.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				exit();
			}
		});
		
		d1.add(frmLogin);
		d1.pack();
		d1.setBounds(0, 0, 296, 170);
		d1.setResizable(false);
		d1.setLocationRelativeTo(this);
		d1.setVisible(true);	
	}
	
	private void frmDoiPass()
	{
		if(frmChangePass==null)
		{
			frmChangePass = new FrmChangePass(this);
		}
		else
		{
			frmChangePass.txtOldPass.setText("");
			frmChangePass.txtNewPass.setText("");
			frmChangePass.txtReTypePass.setText("");
		}

		if(d2==null)
		{
			d2 = new JDialog(this, "Change Password", true);
			d2.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			Image icon = Toolkit.getDefaultToolkit().getImage("res/icon/doipass.png");
			d2.setIconImage(icon);
		}
	
		d2.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				d2.setVisible(false);
			}
		});
		
		d2.add(frmChangePass);
		d2.pack();
		d2.setBounds(0, 0, 315, 210);
		d2.setResizable(false);
		d2.setLocationRelativeTo(this);
		d2.setVisible(true);	
	}
	
	private void lock()
	{
		frmLogin.txtUser.setText("");
		frmLogin.txtPass.setText("");
		d1.setVisible(true);
	}
	
	void startTFTPServer() 
	{
		try
		{
			File fTFTPServerLogFile = new File("C:/syslog.log");
			
			if(fTFTPServerLogFile.exists())
			{
				fTFTPServerLogFile.delete();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		isTFTPStarted = false;
		
		try 
		{
			String sProcessList = "";
			Process process = Runtime.getRuntime().exec(System.getenv("windir") +"\\system32\\"+"tasklist.exe");
		    BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
		                
		    while((sProcessList=input.readLine())!=null) 
		    {
		    	if(sProcessList.contains("3CDaemon.exe"))
		    	{
		    		isTFTPStarted = true;
		    		break;
		    	}
		    }
		     
		    input.close();
		} 
		catch(Exception e)
		{
			log4j.error("", e);
		}
		 
		try
		{
			if(!isTFTPStarted)
			{
				Runtime.getRuntime().exec("3CDaemon\\3CDaemon.exe"); 
			}
		}
		catch(Exception e)
		{
			log4j.error("", e);
			dispOutput("UNNABLE TO OPEN TFTP SERVER OR TFTP SERVER IS OPENING.");
		}
	}
	
	private void connect()
	{
		Thread t = new Thread()
		{
			public void run()
			{
				connect2();
			}
		};
		
		t.start();
	}
	
	void connect2()
	{
		miExit.setEnabled(false);
		miQuit.setEnabled(false);
		btAuto.setEnabled(false);
		cbAllTB.setEnabled(false);
		btStop.setEnabled(true);
		cbHour.setEnabled(false);
		cbMinute.setEnabled(false);
		cbSecond.setEnabled(false);
		btFolderChose.setEnabled(false);
		btManual.setEnabled(false);
		
		for(int i=0; i<deviceNumber; i++)
		{
			((JCheckBox)vtDevice.elementAt(i)).setEnabled(false);
			((JCheckBox)vtDevice.elementAt(i)).setBackground(Color.WHITE);
			((JCheckBox)vtDevice.elementAt(i)).setForeground(Color.BLACK);
		}
		
		if(isAutoBackup)
		{
			dispOutput("AUTO BAKCUP: WAITING...");
			lbStatus.setText("STATUS: AUTO BACKUP, WAITING...");
			log4j.info("AUTO BAKCUP: WAITING...");
		}
		else
		{
			dispOutput("MANUAL BACKUP: BACKUPING...");
			lbStatus.setText("STATUS: MANUAL BACKUP: BACKUPING...");
			log4j.info("MANUAL BACKUP: BACKUPING...");
		}
		
		if(trapSender==null)
		{
			trapSender = new TRAPSender();
		}

		if(backup==null)
		{
			backup = new Backup(this);
		}
		
		if(backup!=null)
		{
			backup.start();
		}
	}

	void disconnect()
	{
		ConnectDevice.isBackup = false;
		dispOutput("BACKUP STOPPED.");
		lbStatus.setText("STATUS: BACKUP STOPPED.");
		log4j.info("BACKUP STOPPED.");
		btStop.setEnabled(false);

		if(backup!=null)
		{
			backup.stop();
			backup = null;
		}
		
		setEnDis();
	}
	
	void disconnect2()
	{
		ConnectDevice.isBackup = false;
		btStop.setEnabled(false);

		if(backup!=null)
		{
			backup.stop();
			backup = null;
		}
		
		setEnDis();
	}
	
	private void setEnDis()
	{
		miExit.setEnabled(true);
		miQuit.setEnabled(true);
		btManual.setEnabled(true);
		cbAllTB.setEnabled(true);
		cbAllTB.setSelected(true);
		btAuto.setEnabled(true);
		btStop.setEnabled(false);
		cbHour.setEnabled(true);
		cbMinute.setEnabled(true);
		cbSecond.setEnabled(true);
		btFolderChose.setEnabled(true);
		
		for(int i=0; i<deviceNumber; i++)
		{
			((JCheckBox)vtDevice.elementAt(i)).setEnabled(true);
		}
	}
	
	void modTFTPServerFile()
	{
		try
		{	
			// CHINH SUA FILE 3CDaemon/3CDaemon.ini
			StringBuffer sbf = null;
			String newLine = System.getProperty("line.separator");
			FileInputStream f = new FileInputStream("3CDaemon/3CDaemon.ini");
			sbf = new StringBuffer();
			int i = 0, index = 0, index2 = 0, length = 0; 			

			while((i=f.read())!=-1)
			{	
				char c = (char)i;
				String s = String.valueOf(c);
				sbf.append(s);
			}
			
			f.close();

			File file = new File("3CDaemon/3CDaemon.ini");
			BufferedWriter fileOut = new BufferedWriter(new FileWriter(file));
			
			String sTemp = "";
			String sTemp2 = "";
			length = sbf.length();
			index = sbf.indexOf("TftpDir");
			index2 = sbf.indexOf("AllowOverwrite");
			sTemp2 = sbf.substring(index2, length);
			sTemp = "TftpDir = " + sFolder;
	
			sbf.delete(index, length);
			sbf.append(sTemp);
			sbf.append(newLine);
			sbf.append(sTemp2);
			
			fileOut.write(sbf.toString());
			fileOut.close();
			
			sbf = null;
			sTemp = "";
			i = 0;
			length = 0;
			index = 0;
			
			f = null;
			file = null;
			fileOut = null;
			
			// CHINH SUA FILE 3CDaemon/Profiles.ftp

			f = new FileInputStream("3CDaemon/Profiles.ftp");
			sbf = new StringBuffer();
			
			while((i=f.read())!=-1)
			{	
				char c = (char)i;
				String s = String.valueOf(c);
				sbf.append(s);
			}
			
			f.close();
			sTemp = "";
			
			sTemp = "UserDir = " + sFolder;
			length = sbf.length();
			index = sbf.indexOf("UserDir");
			sbf.delete(index, length);
			sbf.append(sTemp);
			
			file = new File("3CDaemon/Profiles.ftp");
			fileOut = new BufferedWriter(new FileWriter(file));
			fileOut.write(sbf.toString());
			fileOut.close();
			
			sbf = null;
			sTemp = "";
			i = 0;
			length = 0;
			index = 0;
			
			f = null;
			file = null;
			fileOut = null;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			log4j.error("", e);
		}
	}
	
	private void selectFolder()
	{
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); 
		
		int i = fileChooser.showSaveDialog(this);
		
		if(i==JFileChooser.APPROVE_OPTION)
		{
			sFolder = fileChooser.getSelectedFile().toString();
			tfDirectory.setText("Destination folder: "+sFolder);
			modTFTPServerFile();
			connectDatabase.changeDirectory();
		}
	}
	
	boolean isFolderExist()
	{
		boolean isExist = false;

		if(sFolder==null)
		{
			isExist = false;
		}
		else if(sFolder.isEmpty())
		{
			isExist = false;
		}
		else
		{
			File checkFile = new File(sFolder);
			
			if(checkFile.exists())
			{
				isExist = true;
			}
			else
			{
				isExist = false;
			}
			
			checkFile = null;
		}
		
		return isExist;
	}
	
	void shutdownTFTPServer()
	{
		try 
		{
			Runtime.getRuntime().exec("taskkill /F /IM 3CDaemon.exe");
		} 
		catch(Exception e) 
		{
			e.printStackTrace();
			log4j.error("", e);
		}
		
		try
		{
			Thread.sleep(2000);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			log4j.error("", e);
		}
	}
	
	void dispOutput(String sOutput)
	{
		taOutput.append("\n");
		taOutput.append(sOutput);
		taOutput.setCaretPosition(taOutput.getText().length());
	}
	
	private void donate()
	{
		try 
		{
			final JEditorPane editorPane = new JEditorPane();

	        editorPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);  
	        editorPane.setFont(new Font("Lucida Console", Font.BOLD, 16));
	        editorPane.setForeground(Color.GREEN);
	        editorPane.setBackground(Color.BLACK);
	        editorPane.setPreferredSize(new Dimension(900,600));
	        editorPane.setEditable(false);
	        editorPane.setContentType("text/html");
	        editorPane.setText(
				        		"<html>"+
				        		"<p align='center'>Donate <a href='http://microcode.vn' target='_blank'><em>http://microcode.vn</em></a></p>"+
				        		"<p align='left'>	Developing softwares was a lot of fun, it also consumed many months of my life. \n"+
				        		                   "It actually continues to cost me a significant amount of money to host the <a href='http://microcode.vn' target='_blank'><em>http://microcode.vn</em></a>. \n"+
				        		                   "If you like my softs, you can make a donation. Your donation will help me to continue my projects development.</p>"+
				        		"<blockquote>"+
				        			"<p align='left'><strong>If you living in Viet Nam, you can make donates via bank transfer:</strong></p>"+
				        		"</blockquote>"+
				        			
								"<blockquote>"+
									"<p align='left'><strong>1) BIDV:</strong></p>"+
									"<p align='left'> Account No: <em><strong>63610000096960</strong></em>, account name: <strong>Truong Tuan Kiet.</strong></strong></p>"+
									"<p align='left'><strong>2) ACB: </strong></p>"+
									"<p align='left'> Account No: <em><strong>4214945807039367</strong></em>, account name: <strong>Truong Tuan Kiet.</strong></strong></p>"+
								"</blockquote>"+
				        		  
								"<blockquote>"+
									"<p align='left'><strong>You can donate via PayPal:</strong></p>"+
								"</blockquote>"+
				        		
				        		"<blockquote>"+
				        		  "<p ><a href='https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=X827C3T4PJLQA' target='_blank'><em>https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=X827C3T4PJLQA</em></a></p>"+
				        		  "<p ><strong>Or</strong></p>"+
				        		  "<p ><a href='https://www.paypal.me/kiettel' target='_blank'><em>https://www.paypal.me/kiettel</em></a></p>"+
				        		"</blockquote>"+
				        		  
								"<blockquote>"+
									"<p ><strong>Contact me:</strong></p>"+
									"<p ><strong>-Email</strong>:<em> <span id='cloak96649'><a href='mailto:kiettel@gmail.com'>kiettel@gmail.com</a></span> </em>, <em> <span id='cloak63647'><a href='mailto:kiettel@yahoo.com'>kiettel@yahoo.com</a></span></em></p>"+
									"<p ><strong>-Facebook</strong>: <a href='https://www.facebook.com/kietteldotcom' target='_blank'><em>https://www.facebook.com/kietteldotcom</em></a></p>"+
								"</blockquote>"+
				        		"</html>"
	                		   );
	        
	        final HyperlinkListener hyperLinkListener = new HyperlinkListener()
	        {
	    		public void hyperlinkUpdate(final HyperlinkEvent e) 
	    		{
	    			if(e.getEventType()== HyperlinkEvent.EventType.ACTIVATED)
	    			{
	    				try 
	    				{
	    			        Desktop.getDesktop().browse(e.getURL().toURI());
	    			    } 
	    				catch (Exception ex) 
	    				{
	    			        ex.printStackTrace();
	    			    }
	    			}
	    		}
	    	};
	    	
	    	editorPane.addHyperlinkListener(hyperLinkListener);
	        
	        JOptionPane.showMessageDialog(this,
	                					  new JScrollPane(editorPane),
	                					  "Donate MicroCode.VN",
	                					  JOptionPane.PLAIN_MESSAGE);
	    } 
		catch (Exception e) 
		{
	        e.printStackTrace();
	    }
	}
	
	private void exit()
	{
		int i = JOptionPane.showConfirmDialog(this,
				                              "    DO YOU WANT TO EXIT ?",
				                              "EXIT",
				                              JOptionPane.YES_NO_OPTION);
		
		if(i==0)
		{	
			connectDatabase.close();
			shutdownTFTPServer();
			log4j.info("EXIT AutoBackup.");
			System.exit(0);
		}		
	}
	
	public void actionPerformed(ActionEvent ae) 
	{	
		if(ae.getSource()==miHelp||
		   ae.getSource()==miHelp2||
		   ae.getSource()==btTB[2])
		{
			JOptionPane.showMessageDialog(this,
					                      "    "+sVersion,
					                      "Help", JOptionPane.INFORMATION_MESSAGE);
			
			try 
			{
				Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler Help\\Help.pdf");
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				log4j.error("", e);
			} 
		}
		
		if(ae.getSource()==miAbout||
		   ae.getSource()==miAbout2||
		   ae.getSource()==btTB[3])
		{
			JOptionPane.showMessageDialog(this,
					 					  "Author: Trương Tuấn Kiệt \n" +
										  "Company: VNPT ĐắkLắk\n" +
										  "Email: kiettel@gmail.com \n" +
										  "Website: http://microcode.vn, http://kiettel.com \n" +
										  "Mobile phone: 091.123.0123 or 098.68.68.498",
										  "About",
										  JOptionPane.INFORMATION_MESSAGE);
		}
		
		if(ae.getSource()==btDonate||
		   ae.getSource()==miDonate||
		   ae.getSource()==miDonate2||
		   ae.getSource()==btTB[4])
		{
			donate();
		}
		
		if(ae.getSource()==miExit||
		   ae.getSource()==miQuit||
		   ae.getSource()==btTB[5])
		{
			exit();
		}
		
		if(ae.getSource()==miAutoBackup)
		{
			this.setVisible(true);
		}
		
		if(ae.getSource()==cbHour)
		{
			sH = cbHour.getSelectedItem().toString();
		}
		
		if(ae.getSource()==cbMinute)
		{
			sM = cbMinute.getSelectedItem().toString();
		}
		
		if(ae.getSource()==cbSecond)
		{
			sS = cbSecond.getSelectedItem().toString();
		}
		
		if(ae.getSource()==btFolderChose)
		{
			selectFolder();
		}
		
		if(ae.getSource()==miLock||
		   ae.getSource()==btTB[0])
		{
			lock();
		}
		if(ae.getSource()==miDoiPass||
		   ae.getSource()==btTB[1])
		{
			frmDoiPass();
		}	
		
		if(ae.getSource()==btAuto)
		{
			if(isFolderExist())
			{
				isAutoBackup = true;
				ConnectDevice.isBackup = true;
				miExit.setEnabled(false);
				miQuit.setEnabled(false);
				btManual.setEnabled(false);
				cbHour.setEnabled(false);
				cbMinute.setEnabled(false);
				cbSecond.setEnabled(false);
				btFolderChose.setEnabled(false);
				btStop.setEnabled(true);
				btAuto.setEnabled(false);
				cbAllTB.setEnabled(false);
				taOutput.setText("");
								
				for(int i=0; i<deviceNumber; i++)
				{
					((JCheckBox)vtDevice.elementAt(i)).setEnabled(false);
				}
				
				connect();
			}
			else
			{
				JOptionPane.showMessageDialog(this,
						                      "DESTINATION FOLDER DO NOT EXIST.",
						                      "ERROR",
						                      JOptionPane.ERROR_MESSAGE);
			}
		}
		
		if(ae.getSource()==btManual)
		{	
			if(isFolderExist())
			{
				isAutoBackup = false;
				ConnectDevice.isBackup = true;
				taOutput.setText("");
				connect();
			}
			else
			{
				JOptionPane.showMessageDialog(this,
						                      "DESTINATION FOLDER DO NOT EXIST.",
						                      "ERROR",
						                      JOptionPane.ERROR_MESSAGE);
			}
		}
		
		if(ae.getSource()==btStop)
		{
			int i = JOptionPane.showConfirmDialog(this,
                                                  "DO YOU WANT TO STOP BACKUP ?",
                                                  "EXIT",
                                                  JOptionPane.YES_NO_OPTION);

			if(i==0)
			{	
				disconnect();
			}
		}
		
		if(ae.getSource()==cbAllTB)
		{	
			if(cbAllTB.isSelected())
			{
				btAuto.setEnabled(true);
				btManual.setEnabled(true);
				cbHour.setEnabled(true);
				cbMinute.setEnabled(true);
				cbSecond.setEnabled(true);
				btFolderChose.setEnabled(true);
				
				for(int i=0; i<deviceNumber; i++)
				{				
					((JCheckBox)vtDevice.elementAt(i)).setSelected(true);
				}
			}
			else
			{	
				btAuto.setEnabled(false);
				btManual.setEnabled(false);
				cbHour.setEnabled(false);
				cbMinute.setEnabled(false);
				cbSecond.setEnabled(false);
				btFolderChose.setEnabled(false);
				
				for(int i=0; i<deviceNumber; i++)
				{
					((JCheckBox)vtDevice.elementAt(i)).setSelected(false);
				}
			}
			
			getInput.getSelectedDevice();
			tfNumBack.setText("Selected Devices: "+deviceSelected+"/"+deviceNumber);
		}

		for(int i=0; i<deviceNumber; i++)
		{
			if(ae.getSource()==(JCheckBox)vtDevice.elementAt(i))
			{
				getInput.getSelectedDevice();
				tfNumBack.setText("Selected Devices: "+deviceSelected+"/"+deviceNumber);
				
				if(((JCheckBox)vtDevice.elementAt(i)).isSelected())
				{
					cbAllTB.setSelected(true);
					btAuto.setEnabled(true);
					btManual.setEnabled(true);
					cbHour.setEnabled(true);
					cbMinute.setEnabled(true);
					cbSecond.setEnabled(true);
					btFolderChose.setEnabled(true);
					btStop.setEnabled(false);
					break;
				}
				else
				{
					for(int x=0; x<deviceNumber; x++)
					{
						if(((JCheckBox)vtDevice.elementAt(x)).isSelected())
						{							
							cbAllTB.setSelected(true);
							btAuto.setEnabled(true);
							btManual.setEnabled(true);
							cbHour.setEnabled(true);
							cbMinute.setEnabled(true);
							cbSecond.setEnabled(true);
							btFolderChose.setEnabled(true);
							btStop.setEnabled(false);
							break;
						}
						else
						{
							cbAllTB.setSelected(false);
							btAuto.setEnabled(false);
							btManual.setEnabled(false);
							cbHour.setEnabled(false);
							cbMinute.setEnabled(false);
							cbSecond.setEnabled(false);
							btFolderChose.setEnabled(false);
						}
					}
				}
			}
		}
	}
	
	public void windowClosing(WindowEvent e)
	{
		this.setVisible(false);
	}

	public void windowActivated(WindowEvent e) 
	{

	}

	public void windowClosed(WindowEvent e) 
	{
	
	}

	public void windowDeactivated(WindowEvent e) 
	{
		
	}

	public void windowDeiconified(WindowEvent e) 
	{

	}

	public void windowIconified(WindowEvent e) 
	{
	
	}

	public void windowOpened(WindowEvent e) 
	{

	}

	public void mouseClicked(MouseEvent e) 
	{
		if(e.getSource()==trayIcon)
		{
			if(e.getButton()==MouseEvent.BUTTON1)
			{
				this.setVisible(true);
			}
		}
	}
				
	public void mouseEntered(MouseEvent arg0) 
	{
		
	}

	public void mouseExited(MouseEvent arg0) 
	{

	}

	public void mousePressed(MouseEvent arg0) 
	{

	}

	public void mouseReleased(MouseEvent e) 
	{
		if(e.isPopupTrigger())
		{
			
		}		
	}
}