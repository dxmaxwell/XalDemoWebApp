package xal.ext.servlet.cxmanager.listeners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;

import xal.ext.servlet.cxmanager.service.ChannelService;

@Component
public class SessionConnectionListener implements ApplicationListener<SessionConnectEvent>  {

	@Autowired
	private ChannelService channelService;
	
	@Override
	public void onApplicationEvent(SessionConnectEvent event) {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
		channelService.handleConnect(accessor.getUser(), accessor.getSessionId());
	}
}
