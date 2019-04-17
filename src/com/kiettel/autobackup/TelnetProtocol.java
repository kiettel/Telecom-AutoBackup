/*	Chuong trinh AutoBackup
 *  Tac gia: Truong Tuan Kiet
 *  Email: kiettel@gmail.com
 *  Mobile: 091.123.0123 hoac 098.68.68.498
 *  Ngay bat dau thuc hien: 08/06/2013
 */

package com.kiettel.autobackup;

import java.io.EOFException;
import java.io.IOException;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.Logger;


public class TelnetProtocol
{
	private static final Logger log4j = Logger.getLogger(TelnetProtocol.class);
	private final static byte SE = (byte) 240;
    //private final static byte NOP = (byte) 241;
    //private final static byte DATA_MARK = (byte) 242;
    //private final static byte BREAK = (byte) 243;
    //private final static byte INTERRUPT_PROCESS = (byte) 244;
    //private final static byte ABORT_OUTPUT = (byte) 245;
    //private final static byte ARE_YOU_THERE = (byte) 246;
    //private final static byte ERASE_CHARACTER = (byte) 247;
    //private final static byte ERASE_LINE = (byte) 248;
    //private final static byte GO_AHEAD = (byte) 249;
    private final static byte SB = (byte) 250;
    private final static byte WILL = (byte) 251;
    private final static byte WONT = (byte) 252;
    private final static byte DO = (byte) 253;
    private final static byte DONT = (byte) 254;
    private final static byte IAC = (byte) 255;
    private final static byte TERMINAL_TYPE = (byte) 24;
    private final static byte NAWS = (byte) 31;
    private final static byte XDISPLOC = (byte) 35;
    private final static byte NEW_ENVIRON = (byte) 39;
    private final static byte ENVIRON = (byte) 36;
    private final static byte SEND = (byte) 1;
    private final static byte IS = (byte) 0;
    private final static byte ECHO = (byte) 1;
    private final static byte SUPPRESS_GO_AHEAD = (byte) 3;
    private String terminalType = "vt100";
    
    private SendReceive sendReceive;
    
	public TelnetProtocol(SendReceive sendReceive)
	{
		PropertyConfigurator.configure("log4j.properties");
		this.sendReceive = sendReceive;
	}
	
	// Sau khi doc duoc byte IAC = 255, lay ra : DO, DONT, WILL, WONT, SB
		public void getCommandName()
		{
			try
			{
				int request = sendReceive.receive.in.read();
				byte i = (byte)request;
				
				switch(i)
				{
					case DO: 
					case DONT:
					case WILL:
					case WONT:
					case SB:
						getOption(request);
						break;
					default:
						getOption(request);
				}
			}
			catch(Exception e)
			{
				log4j.error("", e);
			}
		}
		
		// Sau khi lay duoc lenh yeu cau (@request: DO, DONT...)
		// Lay ra ten yeu cau (@i) nhu: ECHO, SUPPRESS GO AHEAD...
		private void getOption(int request)
		{
			try
			{
				int option = sendReceive.receive.in.read();
				
				switch(option)
				{
					case ECHO:
	                negoEcho(request);
	                break;
	                
					case SUPPRESS_GO_AHEAD:
		            negoSuppressGoAhead(request);
		            break;
	                
					case NAWS:
		            negoAboutWindowSize(request);
		            break;
		            
					case XDISPLOC:
		            negoXDisplayLocation(request);
		            break;
		                
					case TERMINAL_TYPE:
	                negoTerminalType(request);
	                break;

					case ENVIRON:
		            negoEnvironment(request);
		            break;

					case NEW_ENVIRON:
		            negoNewEnvironment(request);
		            break;
		            
					default:
		                
		            unknownCommand(request, option);
		            break;			
				}
			}
			catch(Exception e)
			{
				log4j.error("", e);
			} 
		}
		
		private void negoEcho(int request) throws IOException
		{
			switch ((byte)request) 
	        {
	            case WILL:
	                // WILL ECHO
	                System.out.println("Server SEND> IAC WILL ECHO");
	                synchronized (sendReceive.send.out) 
	                {
	                	sendReceive.send.out.write(IAC);
	                	sendReceive.send.out.write(DO);
	                	sendReceive.send.out.write(ECHO);
	                	sendReceive.send.out.flush();
	                }
	                System.out.println("Client SEND> IAC DO ECHO");
	                break;
	            /*  
	            case WONT:
	            	System.out.println("Server SEND> IAC WONT ECHO");
	            	synchronized (out) 
	                {
	                    out.write(IAC);
	                    out.write(DO);
	                    out.write(ECHO);
	                    out.flush();
	                }
	                System.out.println("Client SEND> IAC DO ECHO");
	            	break;
	            */
	            case DO:
	                // DO ECHO
	            	System.out.println("Server SEND> IAC DO ECHO");
	                 synchronized (sendReceive.send.out) 
	                {
	                	 sendReceive.send.out.write(IAC);
	                	 sendReceive.send.out.write(WILL);
	                	 sendReceive.send.out.write(ECHO);
	                	 sendReceive.send.out.flush();
	                }
	                System.out.println("Client SEND> IAC WILL ECHO");
	                break;
	            
	            case DONT:
	                // DON'T ECHO
	            	System.out.println("Server SEND> IAC DONT ECHO");
	                break;
	           
	            default:
	                System.out.println("Server SEND> " + request + " ECHO");
	        }
		}
		
		private void negoSuppressGoAhead(int request) throws IOException
		{
			switch ((byte)request) 
	        {
	            case WILL:
	                // WILL SUPPRESS-GO-AHEAD
	            	System.out.println("Server SEND> IAC WILL SUPPRESS-GO-AHEAD");
	                synchronized (sendReceive.send.out) 
	                {
	                	sendReceive.send.out.write(IAC);
	                	sendReceive.send.out.write(DO);
	                	sendReceive.send.out.write(SUPPRESS_GO_AHEAD);
	                	sendReceive.send.out.flush();
	                }
	                System.out.println("Client SEND> IAC DO SUPPRESS-GO-AHEAD");
	                break;
	                
	            case DO:
	            	System.out.println("Server SEND> IAC DO SUPPRESS-GO-AHEAD");
	            	synchronized (sendReceive.send.out) 
	                {
	            		sendReceive.send.out.write(IAC);
	            		sendReceive.send.out.write(WILL);
	            		sendReceive.send.out.write(SUPPRESS_GO_AHEAD);
	            		sendReceive.send.out.flush();
	                }
	                System.out.println("Client SEND> IAC WILL SUPPRESS-GO-AHEAD");
	            	break;
	            /* 
	            case WONT:
	                // WONT SUPPRESS-GO-AHEAD
	            	System.out.println("Server SEND> IAC WONT SUPPRESS-GO-AHEAD");
	                synchronized (out) 
	                {
	                    out.write(IAC);
	                    out.write(DO);
	                    out.write(SUPPRESS_GO_AHEAD);
	                    out.flush();
	                }
	                System.out.println("Client SEND> IAC DO SUPPRESS-GO-AHEAD");
	                break;
	            */

	            case DONT:
	            	System.out.println("Server SEND> IAC DONT SUPPRESS-GO-AHEAD");
	            	break;
	            
	            default:
	                System.out.println("Server SEND> IAC " +request+" SUPPRESS-GO-AHEAD");
	        }
		}
		
		private void negoAboutWindowSize(int request) throws IOException
		{
			switch ((byte)request) 
	        {
	        	case WILL:
	        		System.out.println("Server SEND> IAC WILL NAWS");
	            	synchronized (sendReceive.send.out) 
	                {
	            		sendReceive.send.out.write(IAC);
	            		sendReceive.send.out.write(DONT);
	            		sendReceive.send.out.write(NAWS);
	            		sendReceive.send.out.flush();
	                }
	                System.out.println("Client SEND> IAC DONT NAWS");
	                break;
	            
	        	case DO:
	                // DO NAWS
	            	System.out.println("Server SEND> IAC DO NAWS");
	                synchronized (sendReceive.send.out) 
	                {
	                	sendReceive.send.out.write(IAC);
	                	sendReceive.send.out.write(WONT); // Cu la WILL
	                	sendReceive.send.out.write(NAWS);
	                	sendReceive.send.out.flush();
	                    System.out.println("Client SEND> IAC WONT NAWS"); // Cu la WILL
	                    /*
	                    out.write(IAC);
	                    out.write(SB);
	                    out.write(NAWS);
	                    out.write(0);    // width1
	                    out.write(windowSize.width);    // width2
	                    out.write(0);    // height1
	                    out.write(windowSize.height);    // height2
	                    out.write(IAC);
	                    out.write(SE);
	                    out.flush();
	                    
	                    System.out.println("SEND>IAC SB NAWS 0 " + windowSize.width +
	                                       " 0 " + windowSize.height + " IAC SE");
	                    */
	                }
	                break;
	            /*
	        	case WONT:
	            	System.out.println("Server SEND> IAC WONT NAWS");
	            	synchronized (out) 
	                {
	                    out.write(IAC);
	                    out.write(DONT);
	                    out.write(NAWS);
	                    out.flush();
	                }
	                System.out.println("Client SEND> IAC DONT NAWS");
	        	break;
	        	*/
	                   
	            case DONT:
	            	System.out.println("Server SEND> IAC DONT NAWS");
	            	synchronized (sendReceive.send.out) 
		            {
		            	sendReceive.send.out.write(IAC);
		            	sendReceive.send.out.write(DONT); // Cu la WILL
		            	sendReceive.send.out.write(NAWS);
		            	sendReceive.send.out.flush();
		                System.out.println("Client SEND> IAC DONT NAWS"); // Cu la WONT
		            }
		            break;
	            
	            default:
	                System.out.println("Server SEND> IAC "+request +" NAWS ");
	        }
		}
		
		private void negoXDisplayLocation(int request) throws IOException
		{
			switch ((byte)request) 
			{
				case WILL:
					System.out.println("Server SEND> IAC WILL XDISPLOC");
		        	synchronized (sendReceive.send.out) 
		            {
		        		sendReceive.send.out.write(IAC);
		        		sendReceive.send.out.write(DONT); // Cu la DONT
		        		sendReceive.send.out.write(XDISPLOC);
		        		sendReceive.send.out.flush();
		            }
		            System.out.println("Client SEND> IAC DONT XDISPLOC"); // Cu la DONT
		            break;
	            
				case DO:
					System.out.println("Server SEND> IAC DO XDISPLOC");
		            synchronized (sendReceive.send.out) 
		            {
		            	sendReceive.send.out.write(IAC);
		            	sendReceive.send.out.write(WONT); // Cu la WONT
		            	sendReceive.send.out.write(XDISPLOC);
		            	sendReceive.send.out.flush();
		            }
		                System.out.println("Client SEND> IAC WONT XDISPLOC"); // Cu la WONT
		            break;
	            /*
				case WONT:
		        	System.out.println("Server SEND> IAC WONT XDISPLOC");
		        	synchronized (out) 
		            {
		                out.write(IAC);
		                out.write(DONT);
		                out.write(XDISPLOC);
		                out.flush();
		            }
		            System.out.println("Client SEND> IAC DONT XDISPLOC");
		            break;
		        */
				
	             
				case DONT:
	            	System.out.println("Server SEND> IAC DONT XDISPLOC");
	                break;
	            
	        	default:
	            System.out.println("Server SEND> IAC "+request + " XDISPLOC");
			}
		}
		
		private void negoTerminalType(int request) throws IOException
		{
			switch ((byte)request) 
	        {
	        	case WILL:
	        		// WILL TERMINAL-TYPE
	            	System.out.println("Server SEND> IAC WILL TERMINAL-TYPE");
	                synchronized (sendReceive.send.out) 
	                {
	                	sendReceive.send.out.write(IAC);
	                	sendReceive.send.out.write(DONT); // Cu la DO
	                	sendReceive.send.out.write(TERMINAL_TYPE);
	                	sendReceive.send.out.flush();
	                    System.out.println("Client SEND> IAC DONT TERMINAL-TYPE");
	                }
	                break;
	            
	        	case DO:
	        	// DO TERMINAL-TYPE
	            System.out.println("Server SEND> IAC DO TERMINAL-TYPE");
	            synchronized (sendReceive.send.out) 
	            {
	            	sendReceive.send.out.write(IAC);
	            	sendReceive.send.out.write(WILL); // Cu la WILL
	            	sendReceive.send.out.write(TERMINAL_TYPE);
	            	sendReceive.send.out.flush();
	                System.out.println("Client SEND> IAC DO TERMINAL-TYPE"); // Cu la WONT
	            }
	            break;
	            /*   
	        	case WONT:
	            // WONT TERMINAL-TYPE
	        	System.out.println("Server SEND> IAC WONT TERMINAL-TYPE");
	            synchronized (out) 
	            {
	                out.write(IAC);
	                out.write(DONT);
	                out.write(TERMINAL_TYPE);
	                out.flush();
	                System.out.println("Client SEND> IAC DONT TERMINAL-TYPE");
	            }
	            break;
	             */   
	      
	            case DONT:
	                // DONT TERMINAL-TYPE
	            	System.out.println("Server SEND> IAC DONT TERMINAL-TYPE");
	            	synchronized (sendReceive.send.out) 
		            {
		            	sendReceive.send.out.write(IAC);
		            	sendReceive.send.out.write(DONT); // Cu la WILL
		            	sendReceive.send.out.write(TERMINAL_TYPE);
		            	sendReceive.send.out.flush();
		                System.out.println("Client SEND> IAC DONT TERMINAL-TYPE"); // Cu la WONT
		            }
		            break;
	            
	            case SB:
	                // SB TERMINAL-TYPE
	                int data = sendReceive.receive.in.read();
	                if (data == -1) 
	                {
	                    throw new EOFException();
	                }
	                
	                switch ((byte)data) 
	                {
	                    case SEND:

	                        System.out.println("Server SEND> IAC SB TERMINAL-TYPE SEND IAC SE");

	                        synchronized (sendReceive.send.out) 
	                        {
	                        	sendReceive.send.out.write(IAC);
	                        	sendReceive.send.out.write(SB);
	                        	sendReceive.send.out.write(TERMINAL_TYPE);
	                        	sendReceive.send.out.write(IS);
	                        	sendReceive.send.out.write(terminalType.getBytes());
	                        	sendReceive.send.out.write(IAC);
	                        	sendReceive.send.out.write(SE);
	                        	sendReceive.send.out.flush();
	                            System.out.println("Client SEND> IAC SB TERMINAL-TYPE IS " + terminalType + " IAC SE");
	                        }
	                        break;

	                    default:
	                        System.out.println("Server SEND > IAC SB TERMINAL-TYPE " + data);
	                }
	                break;

	            default:
	                System.out.println("Server SEND> IAC "+request + " TERMINAL-TYPE");
	        }
		}
		
		private void negoEnvironment(int request) throws IOException
		{
			switch ((byte)request) 
	        {
	        	case WILL:
	        		// WILL ENVIRON
	                System.out.println("Server SEND> IAC WILL ENVIRONMENT");
	                synchronized (sendReceive.send.out) 
	                {
	                	sendReceive.send.out.write(IAC);
	                	sendReceive.send.out.write(DONT);
	                	sendReceive.send.out.write(ENVIRON);
	                	sendReceive.send.out.flush();
	                }
	                System.out.println("Client SEND> IAC DONT ENVIRON");
	                break;
	            
	            case DO:
	            // DO ENVIRON
	            System.out.println("Server SEND> IAC DO ENVIRONMENT");
	            synchronized (sendReceive.send.out) 
	            {
	            	sendReceive.send.out.write(IAC);
	            	sendReceive.send.out.write(WONT);
	            	sendReceive.send.out.write(ENVIRON);
	            	sendReceive.send.out.flush();
	            }
	            System.out.println("Client SEND> IAC WONT ENVIRON");
	            break;
	            
	            case DONT:
	                // DONT ENVIRON
	                System.out.println("Server SEND> IAC DONT ENVIRONMENT");
	                break;
	            
	            default:
	                System.out.println("Server SEND> "+request + " ENVIRON");
	        }
		}
		
		private void negoNewEnvironment(int request) throws IOException
		{
			switch ((byte)request) 
	        {
	        	case WILL:
	            // WILL NEW-ENVIRON
	            System.out.println("Server SEND> IAC WILL NEW-ENVIRON");
	            synchronized (sendReceive.send.out) 
	            {
	            	sendReceive.send.out.write(IAC);
	            	sendReceive.send.out.write(DONT); // Cu la DONT
	            	sendReceive.send.out.write(NEW_ENVIRON);
	            	sendReceive.send.out.flush();
	            }
	            System.out.println("Client SEND> IAC DONT NEW-ENVIRON"); // Cu la DONT
	            break;
	            
	            case DO:
	                // DO NEW-ENVIRON
	            	System.out.println("Server SEND> IAC DO NEW-ENVIRON");
	                synchronized (sendReceive.send.out) 
	                {
	                	sendReceive.send.out.write(IAC);
	                	sendReceive.send.out.write(WONT); // Cu la WONT
	                	sendReceive.send.out.write(NEW_ENVIRON);
	                	sendReceive.send.out.flush();
	                }
	                System.out.println("Client SEND> IAC WONT NEW-ENVIRON"); // Cu la WONT
	                break;
	     
	              
	            case DONT:
	                // DON'T NEW-ENVIRON
	            	System.out.println("Server SEND> IAC DONT NEW-ENVIRON");
	                break;
	            
	            default:
	                System.out.println("Server SEND> IAC "+request + " NEW-ENVIRON");
	        }
		}
		
		
		private void unknownCommand(int request, int option) throws IOException
		{
			
			System.out.println("Khong biet request va option:");
			System.out.println("--- Server SEND> IAC "+request + " " +option);

			switch((byte)request)
	    	{
	    		case WILL:
	    			System.out.println("--- Server SEND> IAC WILL "+option);
	    			synchronized(sendReceive.send.out)
	    			{
	    				sendReceive.send.out.write(IAC);
	    				sendReceive.send.out.write(DONT); // Cu la DONT
	    				sendReceive.send.out.write((byte)option);
	    				sendReceive.send.out.flush();
	    			}
	    			System.out.println("--- Client SEND> IAC DONT "+option); // Cu la DONT
	    			break;
	    		
	    		case DO:
	    			System.out.println("--- Server SEND> IAC DO "+option);
	    			synchronized(sendReceive.send.out)
	    			{
	    				sendReceive.send.out.write(IAC);
	    				sendReceive.send.out.write(WONT); // Cu la WONT
	    				sendReceive.send.out.write((byte)option);
	    				sendReceive.send.out.flush();
	    			}
	    			System.out.println("--- Client SEND> IAC WONT "+option); // Cu la WONT
	    			break;	
	    		case DONT:
	    			System.out.println("--- Server SEND> IAC DONT "+option);  			
	    	}
		}
}