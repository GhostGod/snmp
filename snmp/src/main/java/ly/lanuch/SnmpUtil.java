package ly.lanuch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

/**
 * 通过SNMP监控Windows主机需要在被监控的服务器上安装简单网络管理协议（SNMP）的Windows组件，以Windows 7系统为例：
 * 1、首先，在控制面板中找到“卸载程序”
 * 2、单击“打开或关闭Windows功能”
 * 3、勾选弹出窗口中的“简单网络管理协议（SNMP）”项后单击“确定”并根据提示完成安装即可。
 * 4、完成SNMP服务的安装并重启计算机后，右键单击“计算机”选择“管理”
 * 5、在弹出的“计算机管理”窗口中左侧导航栏中找到“服务”，并在右侧找到“SNMP Service”项
 * 6、鼠标双击“SNMP Service”选项，在弹出的窗口中切换到“安全”选项卡中，如上图添加“接受的社区名称”和接收哪些主机发出的SNMP数据包，在本例中，为了方便测试，可以添加localhost。
 * 
 * “接受的社区名称”是自己定义的任意字符都可以，接收那些主机发出的SNMP数据包定义成你的Nagios服务器即可。
 * 到这里被监控端的Windows主机的SNMP服务就配置完成了。
 * @author liuyang
 *
 */
public class SnmpUtil {
	public static void main(String[] args) throws IOException {
		System.out.println(" --------------- SNMPExample start ---------------");
		//该接口代表了SNMP4J所使用的传输层协议。这也是SNMP4J一大特色的地方。按照RFC的规定，SNMP是只使用UDP作为传输层协议的。而SNMP4J支持管理端和代理端使用UDP或者TCP进行传输。该接口有两个子接口。
		TransportMapping<UdpAddress> transportMapping = new DefaultUdpTransportMapping();
		//该类是SNMP4J中最为核心的类。负责SNMP报文的接受和发送。它提供了发送和接收PDU的方法，所有的PDU类型都可以采用同步或者异步的方式被发送
		Snmp snmp = new Snmp(transportMapping);

		transportMapping.listen();

		ResponseEvent response = null;
		//该类是SNMP报文单元的抽象，其中PDU类适用于SNMPv1和SNMPv2c。ScopedPDU类继承于PDU类，适用于SNMPv3。
		PDU pdu = new PDU();
		//1.3.6.1.2.1.1.2.0
		//内存 1.3.6.1.2.1.25.2.2.0
		//硬盘 1.3.6.1.2.1.25.2.1.4
		//cpu  1.3.6.1.2.1.25.3.3.1.1
		List<VariableBinding> list = new ArrayList<>();
		list.add(new VariableBinding(new OID("1.3.6.1.2.1.1.2.0")));
		list.add(new VariableBinding(new OID("1.3.6.1.2.1.25.2.2.0")));
		pdu.addAll(list);

		pdu.setType(PDU.GET);

		String address = "localhost" + "/" + 161;

		Address targetAddress = new UdpAddress(address);
		//对应于SNMP代理的地址信息，包括IP地址和端口号（161）。其中Target接口适用于SNMPv1和SNMPv2c。CommunityTarget类实现了Target接口，用于SNMPv1和SNMPv2c这两个版本，UserTarget类实现了Target接口，适用于SNMPv3。
		CommunityTarget target = new CommunityTarget();

		target.setCommunity(new OctetString("public")); // 该字符串是我们在上面配置的

		target.setAddress(targetAddress);

		target.setRetries(2);

		target.setTimeout(3000);

		target.setVersion(SnmpConstants.version2c);

		response = snmp.send(pdu, target);

		System.out.println("result: " + response.getResponse());
		PDU p = response.getResponse();
		Iterator<? extends VariableBinding> i = p.getVariableBindings().iterator();
		while (i.hasNext()) {
			VariableBinding v = i.next();
			System.out.println(v);
		}
		System.out.println(" --------------- SNMPExample ended ---------------");

		/*//----------------------------------------------------------------------------------------
		//windows     
		//      String cmd = "F:\\apache-tomcat-6.0.20.exe";     
		//      String cmd = "D:\\Program Files\\Microsoft Office\\OFFICE11\\WINWORD.EXE F:\\test.doc";     
		//      String cmd = "cmd.exe /c start F:\\test.doc";     
		String cmd = "ping www.baidu.com";

		//linux     
		//      String cmd = "./fork_wait";     
		//      String cmd = "ls -l";     
		//      String[] cmd=new String[3];     
		//      cmd[0]="/bin/sh";     
		//      cmd[1]="-c";     
		//      cmd[2]="ls -l ./";     
		Runtime run = Runtime.getRuntime();//返回与当前 Java 应用程序相关的运行时对象     
		try {
			Process p = run.exec(cmd);// 启动另一个进程来执行命令     
			BufferedInputStream in = new BufferedInputStream(p.getInputStream());
			BufferedReader inBr = new BufferedReader(new InputStreamReader(in, Charset.forName("GBK")));
			String lineStr;
			while ((lineStr = inBr.readLine()) != null)
				//获得命令执行后在控制台的输出信息     
				System.out.println(lineStr);// 打印输出信息     
			//检查命令是否执行失败。     
			if (p.waitFor() != 0) {
				if (p.exitValue() == 1)//p.exitValue()==0表示正常结束，1：非正常结束     
					System.err.println("命令执行失败!");
			}
			inBr.close();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}*/
	}
}
