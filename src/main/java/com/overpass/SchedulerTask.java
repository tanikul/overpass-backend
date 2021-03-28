package com.overpass;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.overpass.model.Payload;
import com.overpass.service.DashboardService;

@Component
public class SchedulerTask {

    private SimpMessagingTemplate template;
    
    @Autowired
    private DashboardService dashboardService;

    @Autowired
    public SchedulerTask(SimpMessagingTemplate template) {
        this.template = template;
    }

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Scheduled(fixedRate = 30000)
    public void sendMessageToClient() {
    	List<Integer> list = dashboardService.validateOverpass();
    	if(!list.isEmpty()) {
    		for(Integer i : list) {
    			this.template.convertAndSend("/topic/greetings/" + i, dashboardService.getDataDashBoard(i));
    		}
    		
    	}
    }
    
}