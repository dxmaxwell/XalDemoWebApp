package xal.ext.servlet.cxmanager.service;

import org.epics.pvmanager.PVReader;
import org.epics.pvmanager.PVWriter;


public interface Channel {
		
	public PVReader<?> getReader();
	
	public boolean isReadable();
	
	public PVWriter<?> getWriter();
	
	public boolean isWritable();
	
	public void close();
	
	public boolean isClosed();
	
	public boolean isConnected();
}
