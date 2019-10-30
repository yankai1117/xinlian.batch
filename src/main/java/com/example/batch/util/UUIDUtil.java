package com.example.batch.util;

import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;

import java.util.UUID;

public class UUIDUtil {
	//修改成constructMulticastAddress ,防止jmv多个实例在一台服务器上
	private static EthernetAddress nic = EthernetAddress.constructMulticastAddress();
	// need to pass Ethernet address; can either use real one (shown here)
	public static  UUID getTimebaseUUID(){
	  // or bogus which would be gotten with: EthernetAddress.constructMulticastAddress()
	  TimeBasedGenerator uuidGenerator = Generators.timeBasedGenerator(nic);
	  // also: we don't specify synchronizer, getting an intra-JVM syncer; there is
	  // also external file-locking-based synchronizer if multiple JVMs run JUG
	  return uuidGenerator.generate();
	  }
	
	public static String getbatchno(){
	     java.util.Calendar c=java.util.Calendar.getInstance();   
	     java.text.SimpleDateFormat f=new java.text.SimpleDateFormat("yyMMdd");
	     return f.format(c.getTime());
	}
	
	public static String gettime(){
	     java.util.Calendar c=java.util.Calendar.getInstance();   
	     java.text.SimpleDateFormat f=new java.text.SimpleDateFormat("yyMMddHHmmss");
	     return f.format(c.getTime());
	}
}
