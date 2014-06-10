package xal.ext.servlet.cxmanager.listeners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import xal.ext.servlet.cxmanager.service.ChannelService;

@Component
public class SessionDisconnectListener implements ApplicationListener<SessionDisconnectEvent> {

	@Autowired
	private ChannelService channelService;
		
	@Override
	public void onApplicationEvent(SessionDisconnectEvent event) {
		channelService.handleDisconnect(event.getSessionId());
	}
}
