package com.micah.crawler.download;

import com.micah.crawler.request.Request;
import com.micah.crawler.response.Response;
import com.micah.crawler.scheduler.Scheduler;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;

/**
 * Download thread class
 *
 * @author Micah
 * @date 2020/10/3
 */
@Slf4j
public class Downloader implements Runnable {

    private final Scheduler scheduler;
    private final Request   request;

    public Downloader(Scheduler scheduler, Request request) {
        this.scheduler = scheduler;
        this.request = request;
    }

    @Override
    public void run() {
        log.debug("[{}] start to download...", request.getUrl());
        // use oh-my-request library
        io.github.biezhi.request.Request httpReq = null;
        if ("get".equalsIgnoreCase(request.method())) {
            httpReq = io.github.biezhi.request.Request.get(request.getUrl());
        }
        if ("post".equalsIgnoreCase(request.method())) {
            httpReq = io.github.biezhi.request.Request.post(request.getUrl());
        }

        InputStream result = httpReq.contentType(request.contentType()).headers(request.getHeaders())
                .connectTimeout(request.getSpider().getConfig().timeout())
                .readTimeout(request.getSpider().getConfig().timeout())
                .stream();

        log.debug("[{}] download finished...", request.getUrl());
        Response response = new Response(request, result);
        scheduler.addResponse(response);
    }

}
