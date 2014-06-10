package xal.app.web.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

import xal.app.web.demo.service.ClockService;

@Controller
public class ClockController {

	@Autowired
	private ClockService clockService;
	
	@ResponseBody
	@SubscribeMapping("/clock")
	public Object clock() {
		return clockService.getTime();
	}
}
