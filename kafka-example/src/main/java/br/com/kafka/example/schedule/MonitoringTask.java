package br.com.kafka.example.schedule;

import br.com.kafka.example.metric.LagAnalyzerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Slf4j
@Component
@RequiredArgsConstructor
public class MonitoringTask {

    private final LagAnalyzerService lagAnalyzerService;


    @Scheduled(fixedDelay = 5000L)
    public void liveLagAnalysis() throws ExecutionException, InterruptedException {
        long lag = lagAnalyzerService.getLag("sales-group", "sales", 6);
        log.info("LAG: {}", lag);
        if (lag == 0) {
            log.info("------------------FIM----------------");
        }

    }

    private void countMessage() {
        Long count = lagAnalyzerService.getTotalNumberOfMessagesInATopic("sales");
        log.info("Count message: {}", count);
    }
}
