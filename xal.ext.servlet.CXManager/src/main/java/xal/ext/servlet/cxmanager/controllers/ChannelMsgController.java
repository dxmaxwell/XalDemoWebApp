package xal.ext.servlet.cxmanager.controllers;

import java.security.Principal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import xal.ext.servlet.cxmanager.service.ChannelService;

@Controller
public class ChannelMsgController {

	@Autowired
	private ChannelService channelService;
	
	@MessageMapping("/read/{channelId}")
	public void read(Principal principal, @DestinationVariable String channelId, Map<String,Object> payload) {
		channelService.read(principal, channelId, payload);
	}
}
