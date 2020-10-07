package com;

import com.dao.BankTransaction;
import com.dao.BankTransactionItemAnalyticsProcessor;
import com.dao.BankTransactionRepository;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class JobRestController {

    @Autowired
    private BankTransactionRepository bankTransactionRepository ;
    @Autowired
    private BankTransactionItemAnalyticsProcessor analyticsProcessor ;

    @Autowired
    private JobLauncher jobLauncher ;
    @Autowired
    private Job job ;
    @GetMapping(value="/startJob")
    public BatchStatus load() throws  Exception{
        Map<String , JobParameter> params = new HashMap<>() ;
        params.put("time" ,new JobParameter(System.currentTimeMillis())) ;
        JobParameters jobParameters = new JobParameters(params) ;
        JobExecution jobExecution =jobLauncher.run(job ,jobParameters ) ;
        while(jobExecution.isRunning()){
            System.out.println("................");
        }

        return jobExecution.getStatus() ;
    }


    @GetMapping("/analytics")
    public  Map<String, Double> analytics(){

        Map<String, Double> map = new HashMap<>() ;
        map.put("totalCredits", analyticsProcessor.getTotalCredit()) ;
        map.put("totalDebits" , analyticsProcessor.getTotalDebit()) ;

        return map ;

    }




}
