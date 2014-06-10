package xal.ext.servlet.cxmanager.service;

import java.security.Principal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.DestinationUserNameProvider;
import org.springframework.stereotype.Service;
import org.epics.pvmanager.CompositeDataSource;
import org.epics.pvmanager.PVConfiguration;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVReaderEvent;
import org.epics.pvmanager.PVReaderListener;
import org.epics.pvmanager.sim.SimulationDataSource;

import static org.epics.util.time.TimeDuration.ofMillis;
import static org.epics.pvmanager.ExpressionLanguage.channel;


@Service
public class ChannelService {

	@Autowired
	private SimpMessagingTemplate client;
		
	private Map<String,String> sessions = new ConcurrentHashMap<String,String>();
	
	private Map<String,Map<String,Channel>> connections = new ConcurrentHashMap<String,Map<String,Channel>>();
	
	private Log log = LogFactory.getLog(ChannelService.class);
	
	static {
		CompositeDataSource defaultDataSource = new CompositeDataSource();
		defaultDataSource.putDataSource("sim", new SimulationDataSource());
		PVManager.setDefaultDataSource(defaultDataSource);
	}
	
	public void read(Principal principal, String channelId, Map<String,Object> configuration) {
		
		if( !(principal instanceof DestinationUserNameProvider) ) {
			log.warn("Principal does not implement DestinationUserNameProvider as required");
			return;
		}
		String user = ((DestinationUserNameProvider)principal).getDestinationUserName();
		
		if( !connections.containsKey(user) ) {
			log.warn("Connection does not exist for user: " + user);
			return;
		}
		Map<String,Channel> channels = connections.get(user);
		
		
		if(( channelId == null ) || ( "".equals(channelId) )) {
			//Log!
			log.warn("Channel ID must be not Null and not empty");
			return;
		}
		final String userDataDest = "/user/"+user+"/topic/data/"+channelId;
		final String userErrorDest = "/user/"+user+"/topic/error/"+channelId;
		
		Channel channel;
		if( channels.containsKey(channelId) ) {
			channel = channels.get(channelId);
			if( channel.isReadable() ) {
				client.convertAndSend(userDataDest, channel.getReader().getValue());
			}
			return;
		}
		
		try {
			channel = buildChannel(configuration);
		} catch(IllegalArgumentException e) {
			log.debug("Channel could not be built with specified configuration", e);
			// Send Error? //
			return;
		}
		
		if( channel.isReadable() ) {
			channel.getReader().addPVReaderListener(new PVReaderListener<Object>() {
				@Override
				public void pvChanged(PVReaderEvent<Object> event) {
					if( event.isConnectionChanged() && event.getPvReader().isConnected() ) {
						if( event.getPvReader().getValue() != null ) {
							client.convertAndSend(userDataDest, event.getPvReader().getValue());
						}
					} else if( event.isValueChanged() ) {
						if( event.getPvReader().getValue() != null ) {
							client.convertAndSend(userDataDest, event.getPvReader().getValue());
						}
					} else if( event.isExceptionChanged() ) {
						if(  event.getPvReader().lastException() != null ) {
							client.convertAndSend(userErrorDest, event.getPvReader().lastException().getMessage());
						}
					}
				}
			});
		}
		
		channels.put(channelId, channel);
	}
	
	public void handleConnect(Principal principal, String sessionId) {
		if(( sessionId == null ) || ( "".equals(sessionId) )) {
			log.warn("Session ID must be not Null and not empty");
			return;
		}
		
		if( !(principal instanceof DestinationUserNameProvider) ) {
			log.warn("Principal provided does not implement DestinationUserNameProvider as required");
			return;
		}
		String user = ((DestinationUserNameProvider)principal).getDestinationUserName();
		
		if( sessions.containsKey(sessionId) ) {
			log.warn("Session already exists for ID: " + sessionId);
			return;
		}
		
		if( connections.containsKey(user) ) {
			log.warn("Connection already exists for user: " + user);
			return;
		}
		
		sessions.put(sessionId, user);
		connections.put(user, new ConcurrentHashMap<String,Channel>());
	}
	
	public void handleDisconnect(String sessionId) {
		
		if( !sessions.containsKey(sessionId) ) {
			log.warn("Session does not exist with ID: " + sessionId); 
			return;
		}
		String user = sessions.remove(sessionId);
		
		if( !connections.containsKey(user) ) {
			log.warn("Connection does not exist for user: " + user);
			return;
		}
		Map<String,Channel> channels = connections.remove(user);
		
		int count = 0;
		for( Channel channel : channels.values() ) {
			if( !channel.isClosed() ) {
				channel.close();
				count++;
			}
		}
		
		if( log.isInfoEnabled() ) {
			log.info("Disconnect session " + sessionId + ": Channels closed: " + count);
		}
	}
	
	private Channel buildChannel(Map<String,Object> configuration) {
		
		if( !configuration.containsKey("uri") ) {
			throw new IllegalArgumentException("Channel configuration must specify URI");
		}
		String uri = configuration.get("uri").toString();
		
		if(( uri == null ) || ( "".equals(uri) )) {
			throw new IllegalArgumentException("URI must be not Null and not empty");
		}
		PVConfiguration<Object,Object> pvconfig = PVManager.readAndWrite(channel(uri));
		
		int rate = 1000;
		if( configuration.containsKey("rate") ) {
			try {
				rate = Integer.parseInt(configuration.get("rate").toString());
			} catch(NumberFormatException e) {
				// ignore exception //
			}
		}
				
		return new PVChannel<Object,Object>(pvconfig.asynchWriteAndMaxReadRate(ofMillis(rate)));
	}
}
