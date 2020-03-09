package com.example.servingwebcontent;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicLong;

@Controller
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
                int count = 0;
                for (int i = 0; i < timeStamps.size(); i++){
                    if ((long)timeStamps.get(i) > new Date().getTime() - (10000)) {
                        count++;
                        System.out.println("Happened within the last 10 seconds");
                        System.out.println("Captured time: "+(long)timeStamps.get(i));
                        System.out.println("Cutoff time:   "+(new Date().getTime() - (10000)));
                    };
                    AtomicLong customGauge = registry.gauge("custom", customRegistry);
                    customGauge.set(count);
                }
                System.out.println("Hits:"+count);
        }}, 1000, 1000);
    }

    private void addTimestamp(Long timeStamp){
        this.timeStamps.add(timeStamp);
    }

//    private void updateRegistry(){
////        long currentTime = new Date().getTime();
////        long l1 = new Date().getTime();
////        long l2 = new Date().getTime();
////        System.out.println(l1);
////        System.out.println(l2);
////        if (l2 > l1)
////            System.out.println("L2 is create than L1");
////        else if (l1 > l2)
////            System.out.println("L1 is create than L2");
////        else
////            System.out.println("Something else happend");
////        List<Timestamp> results = timeStamps
////                .stream()
//////                .forEach(System.out::println)
////                .filter(a -> (Timestamp)a > new Timestamp(System.currentTimeMillis()));
////                .collect(Collectors.toList());
////        System.out.println("Start:");
////        results.forEach(System.out::println);
//
////        System.out.println("");
////        System.out.println("----New webpage hit----");
////        int count = 0;
////        for (int i = 0; i < this.timeStamps.size(); i++){
////            if ((long)this.timeStamps.get(i) > new Date().getTime() - (10000)) {
////                count++;
////                System.out.println("Happened within the last 10 seconds");
////                System.out.println("Captured time: "+(long)this.timeStamps.get(i));
////                System.out.println("Cutoff time:   "+(new Date().getTime() - (10000)));
////            };
////        System.out.println("Hits:"+count);
////        AtomicLong customGauge = registry.gauge("custom", this.customRegistry);
////        customGauge.set(count);
//
////            System.out.println(i);
////            System.out.println(this.timeStamps.get(i));
//        }
//    }

    @GetMapping("/greeting")
    public String greeting(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
        model.addAttribute("name", name);
//        AtomicLong customGauge = registry.gauge("custom", this.custom);
//        customGauge.incrementAndGet();
        this.addTimestamp(new Date().getTime());
//        this.updateRegistry();
//        System.out.println(timeStamps);
        return "greeting";
    }
}