package xal.ext.servlet.cxmanager.service;

import org.epics.pvmanager.PV;
import org.epics.pvmanager.PVReader;
import org.epics.pvmanager.PVWriter;

public class PVChannel<R,W> implements Channel {
	
	private PV<R,W> pv;
	
	public PVChannel(PV<R,W> pv) {
		if( pv == null ) {
			throw new IllegalArgumentException("PV must be not Null");
		}
		this.pv = pv;
	}

	@Override
	public PVReader<?> getReader() {
		return (PVReader<R>)pv;
	}

	@Override
	public boolean isReadable() {
		return true;
	}

	@Override
	public PVWriter<?> getWriter() {
		return (PVWriter<W>)pv;
	}

	@Override
	public boolean isWritable() {
		return true;
	}
	
	@Override
	public void close() {
		pv.close();
	}

	@Override
	public boolean isClosed() {
		return pv.isClosed();
	}

	@Override
	public boolean isConnected() {
		return pv.isConnected();
	}
}
