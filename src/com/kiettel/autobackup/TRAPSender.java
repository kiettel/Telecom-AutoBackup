/*	Chuong trinh AutoBackup
 *  Tac gia: Truong Tuan Kiet
 *  Email: kiettel@gmail.com
 *  Mobile: 091.123.0123 hoac 098.68.68.498
 *  Ngay bat dau thuc hien: 08/06/2013
 */

package com.kiettel.autobackup;


import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.PDUv1;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.IpAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.Logger;


public class TRAPSender 
{
	private static final Logger log4j = Logger.getLogger(TRAPSender.class);
	private CommunityTarget cTarget;
	private PDUv1 pdu;
	private Snmp snmp;
	private String sOID = ".1.3.6.1.2.1.1.8";
	
	public TRAPSender()
	{
		PropertyConfigurator.configure("log4j.properties");
	}
	
	void sendTRAP(String sAgentIP,
			      int[] oidInsertOrUpdate,
			      int insertOrUpdate,
			      int[] oidInformation,
			      String sInformation,
			      String sTargetIP,
			      int port,
			      String community) throws Exception 
	{
		@SuppressWarnings("rawtypes")
		TransportMapping transport = new DefaultUdpTransportMapping();
		transport.listen();

		cTarget = null;
		cTarget = new CommunityTarget();
		cTarget.setCommunity(new OctetString(community));
		cTarget.setVersion(SnmpConstants.version1);
		cTarget.setAddress(new UdpAddress(sTargetIP + "/" + port));
		cTarget.setTimeout(5000);
		cTarget.setRetries(2);

		pdu = null;
		pdu = new PDUv1();
		pdu.setType(PDU.V1TRAP);
		pdu.setEnterprise(new OID(sOID));
		pdu.setGenericTrap(PDUv1.ENTERPRISE_SPECIFIC);
		pdu.setSpecificTrap(1);
		pdu.setAgentAddress(new IpAddress(sAgentIP));
		
		/////////////////////////////////////GUI DATA//////////////////////////////////		
		pdu.add(new VariableBinding(new OID(oidInsertOrUpdate),
				                    new Integer32(insertOrUpdate)));
		pdu.add(new VariableBinding(new OID(oidInformation),
								    new OctetString(sInformation+" (OMCDLKAlarms)"))); 
		///////////////////////////////////////////////////////////////////////////////
		
		snmp = null;
		snmp = new Snmp(transport);
		snmp.send(pdu, cTarget);
		snmp.close();
		
		System.out.println("SEND TRAP FROM: "+sAgentIP+
		           	       ", VALUE="+pdu.getVariableBindings()+
		           	       " TO "+sTargetIP+" OK.");
	}
	
	void stop()
	{
		if(snmp!=null)
		{
			try
			{
				snmp.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
				log4j.error("", e);
			}
		}
	}
}