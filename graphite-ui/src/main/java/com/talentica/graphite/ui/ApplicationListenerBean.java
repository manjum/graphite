package com.talentica.graphite.ui;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationContextEvent;

import com.talentica.graphite.ui.utils.Utils;

public class ApplicationListenerBean implements ApplicationListener<ApplicationContextEvent> {
	private static boolean init = false;
	@Override
	public void onApplicationEvent(ApplicationContextEvent arg0) {
		if(!init){
			System.out.println("event recieved");
			try {
				Utils.init();
				init = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}