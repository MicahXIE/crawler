package com.micah.crawler.spider;

import com.micah.crawler.config.Config;
import com.micah.crawler.event.CrawlerEvent;
import com.micah.crawler.event.EventManager;
import com.micah.crawler.pipeline.Pipeline;
import com.micah.crawler.request.Parser;
import com.micah.crawler.request.Request;
import com.micah.crawler.response.Response;
import com.micah.crawler.response.Result;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * abstract spider class for user to extend
 *
 * @author Micah
 * @date 2020/10/3
 */
@Data
public abstract class Spider {

    protected String name;
    protected Config config;
    protected List<String>   startUrls = new ArrayList<>();
    protected List<String>   keyWords  = new ArrayList<>();
    protected List<Pipeline> pipelines = new ArrayList<>();
    protected List<Request>  requests  = new ArrayList<>();

    public Spider(String name) {
        this.name = name;
        EventManager.registerEvent(CrawlerEvent.SPIDER_STARTED, this::onStart);
    }

    public Spider startUrls(String... urls) {
        this.startUrls.addAll(Arrays.asList(urls));
        return this;
    }

    public Spider keyWords(String... words) {
        this.keyWords.addAll(Arrays.asList(words));
        return this;
    }

    public void onStart(Config config) {
    }

    // add Pipeline
    protected <T> Spider addPipeline(Pipeline<T> pipeline) {
        this.pipelines.add(pipeline);
        return this;
    }

    // construct request from given url
    public <T> Request<T> makeRequest(String url) {
        return makeRequest(url, this::parse);
    }

    public <T> Request<T> makeRequest(String url, Parser<T> parser) {
        return new Request(this, url, parser);
    }

    // parse DOM and need user to rewrite
    protected abstract <T> Result<T> parse(Response response);

    protected void resetRequest(Consumer<Request> requestConsumer) {
        this.resetRequest(this.requests, requestConsumer);
    }

    protected void resetRequest(List<Request> requests, Consumer<Request> requestConsumer) {
        requests.forEach(requestConsumer::accept);
    }

}
