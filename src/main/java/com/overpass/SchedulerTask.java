package com.overpass;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
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
    	dashboardService.validateOverpass();
    	this.template.convertAndSend("/topic/greetings", dashboardService.getDataDashBoard());
    }
}