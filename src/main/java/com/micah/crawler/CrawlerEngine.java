package com.micah.crawler;

import com.micah.crawler.config.Config;
import com.micah.crawler.download.Downloader;
import com.micah.crawler.event.CrawlerEvent;
import com.micah.crawler.event.EventManager;
import com.micah.crawler.pipeline.Pipeline;
import com.micah.crawler.request.Parser;
import com.micah.crawler.request.Request;
import com.micah.crawler.response.Response;
import com.micah.crawler.response.Result;
import com.micah.crawler.scheduler.Scheduler;
import com.micah.crawler.spider.Spider;
import com.micah.crawler.utils.CrawlerUtils;
import com.micah.crawler.utils.NamedThreadFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Crawler Engine, core function
 *
 * @author Micah
 * @date 2020/10/3
 */
@Slf4j
public class CrawlerEngine {

    private List<Spider>    spiders;
    private Config          config;
    private boolean         isRunning;
    private Scheduler       scheduler;
    private ExecutorService executorService;

    CrawlerEngine(Crawler crawler) {
        this.scheduler = new Scheduler();
        this.spiders = crawler.spiders;
        this.config = crawler.config;
        this.executorService = new ThreadPoolExecutor(config.parallelThreads(), config.parallelThreads(), 0, TimeUnit.MILLISECONDS,
                config.queueSize() == 0 ? new SynchronousQueue<>()
                        : (config.queueSize() < 0 ? new LinkedBlockingQueue<>()
                        : new LinkedBlockingQueue<>(config.queueSize())), new NamedThreadFactory("task"));
    }

    // core function for the whole process
    public void start() {
        if (isRunning) {
            throw new RuntimeException("Crawler already started");
        }

        isRunning = true;
        // global event start
        EventManager.fireEvent(CrawlerEvent.GLOBAL_STARTED, config);

        spiders.forEach(spider -> {

            Config conf = config.clone();

            log.info("Spider [{}] start...", spider.getName());
            log.info("Spider [{}] configure [{}]", spider.getName(), conf);
            spider.setConfig(conf);

            List<Request> requests = spider.getStartUrls().stream()
                    .map(spider::makeRequest).collect(Collectors.toList());

            spider.getRequests().addAll(requests);
            scheduler.addRequests(requests);

            EventManager.fireEvent(CrawlerEvent.SPIDER_STARTED, conf);

        });

        // produce in backend
        Thread downloadTread = new Thread(() -> {
            while (isRunning) {
                if (!scheduler.hasRequest()) {
                    CrawlerUtils.sleep(100);
                    continue;
                }
                Request request = scheduler.nextRequest();
                executorService.submit(new Downloader(scheduler, request));
                CrawlerUtils.sleep(request.getSpider().getConfig().delay());
            }
        });
        downloadTread.setDaemon(true);
        downloadTread.setName("download-thread");
        downloadTread.start();
        // consume
        this.complete();
    }

    private void complete() {
        while (isRunning) {
            if (!scheduler.hasResponse()) {
                CrawlerUtils.sleep(100);
                continue;
            }
            Response response = scheduler.nextResponse();
            Parser   parser   = response.getRequest().getParser();
            if (null != parser) {
                Result<?>     result   = parser.parse(response);
                List<Request> requests = result.getRequests();
                if (!CrawlerUtils.isEmpty(requests)) {
                    requests.forEach(scheduler::addRequest);
                }
                if (null != result.getItem()) {
                    List<Pipeline> pipelines = response.getRequest().getSpider().getPipelines();
                    pipelines.forEach(pipeline -> pipeline.process(result.getItem(), response.getRequest()));
                }
            }
        }
    }

    public void stop(){
        isRunning = false;
        scheduler.clear();
        log.info("Crawler Stop...");
    }

}
