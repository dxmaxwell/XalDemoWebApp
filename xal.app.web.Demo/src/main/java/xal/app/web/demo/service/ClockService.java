package xal.app.web.demo.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ClockService { 
	
	private static final String TIME_FORMAT = "HH:mm:ss";
	
	@Autowired
	private SimpMessagingTemplate client;
		
	public Map<String,Object> getTime() {
		SimpleDateFormat format = new SimpleDateFormat(TIME_FORMAT);
		return Collections.singletonMap("time", (Object)format.format(new Date()));
	}
	
	@Scheduled(fixedRate=1000)
	protected void publishClocks() {
		client.convertAndSend("/topic/clock", getTime());
	}
}
