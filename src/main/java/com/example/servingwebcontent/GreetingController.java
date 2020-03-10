package com.example.servingwebcontent;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class GreetingController {

    private MeterRegistry registry;
    private AtomicLong customRegistry;
    private List timeStamps;

    public GreetingController(MeterRegistry registry, List timeStamps) {
        this.registry = registry;
        this.timeStamps = timeStamps;
        this.customRegistry = new AtomicLong(0L);

        Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask(){
            public void run(){
                long count = 0;
                //todo: Sometimes throws java.lang.NullPointerException under very high load
                try {
                    for (int i = 0; i < timeStamps.size(); i++){
                        if ((long)timeStamps.get(i) > new Date().getTime() - (10000)) {
                            count++;
                        } else{
                            timeStamps.remove(i);
                        }
                        AtomicLong customGauge = registry.gauge("customGauge", customRegistry);
                        customGauge.set(count/10);
                    }
                    System.out.println("Hits in the past 10 seconds: " + count);
                    if (count < 10 && count != 0)
                    {
                        count = 10;
                    };
                    System.out.println("Hits per second (10 second window): " + count/10);
                } catch (Exception e){
                    System.out.println(e);
                }
        }}, 3000, 3000);
    }

    private void addTimestamp(Long timeStamp){
        this.timeStamps.add(timeStamp);
    }

    @GetMapping("/greeting")
    public String greeting(@RequestParam(name="name", required=false, defaultValue="World") String name) {
        this.addTimestamp(new Date().getTime());
        return "Hello "+name+"!";
    }
}