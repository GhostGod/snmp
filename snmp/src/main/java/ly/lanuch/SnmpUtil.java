package ly.lanuch;

import java.io.IOException;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

public class SnmpUtil {
	public static void main(String[] args) throws IOException {
		System.out.println(" --------------- SNMPExample start ---------------");

		TransportMapping<UdpAddress> transportMapping = new DefaultUdpTransportMapping();

		Snmp snmp = new Snmp(transportMapping);

		transportMapping.listen();

		ResponseEvent response = null;

		PDU pdu = new PDU();

		pdu.add(new VariableBinding(new OID("1.3.6.1.2.1.1.2.0")));

		pdu.setType(PDU.GET);

		String address = "localhost" + "/" + 161;

		Address targetAddress = new UdpAddress(address);

		CommunityTarget target = new CommunityTarget();

		target.setCommunity(new OctetString("public")); // 改字符串是我们在上面配置的

		target.setAddress(targetAddress);

		target.setRetries(2);

		target.setTimeout(3000);

		target.setVersion(SnmpConstants.version2c);

		response = snmp.get(pdu, target);

		System.out.println("result: " + response.getResponse());

		System.out.println(" --------------- SNMPExample ended ---------------");

	}
}
