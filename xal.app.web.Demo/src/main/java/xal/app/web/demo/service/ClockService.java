package xal.app.web.demo.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
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
	
	private Set<String> timezones = Collections.synchronizedSet(new HashSet<String>());
	
	
	public Map<String,Object> getTime(String tz) {
		for(String timezone : TimeZone.getAvailableIDs()) {
			if(timezone.equalsIgnoreCase(tz)) {
				timezones.add(tz);
				return getTime(TimeZone.getTimeZone(tz));
			}
		}
		return Collections.singletonMap("error", (Object)("Unknown TimeZone: " + tz));
	}
	
	protected Map<String,Object> getTime(TimeZone timezone) {
		SimpleDateFormat format = new SimpleDateFormat(TIME_FORMAT);
		format.setTimeZone(timezone);
		return Collections.singletonMap("time", (Object)format.format(new Date()));
	}
	
	@Scheduled(fixedRate=1000)
	protected void publishClocks() {
		for(String timezone : timezones) {
			client.convertAndSend("/topic/clock/"+timezone, getTime(TimeZone.getTimeZone(timezone)));
		}
	}
}
