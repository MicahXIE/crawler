package com.micah.crawler.event;

import com.micah.crawler.Crawler;
import com.micah.crawler.config.Config;
import com.micah.crawler.response.Response;
import com.micah.crawler.response.Result;
import com.micah.crawler.spider.Spider;

/**
 * Crawler Event test
 *
 * @author Micah
 * @date 2020/10/3
 */
public class CrawlerEventTest {

    public static void main(String[] args) {
        Crawler.me(new Spider("test") {
            @Override
            public Result<String> parse(Response response) {
                return new Result<>(response.body().toString());
            }
        }, Config.me()).onStart(config -> System.out.println("asasas")).start();
    }

}
