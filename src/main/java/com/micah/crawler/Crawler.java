package com.micah.crawler;

import com.micah.crawler.config.Config;
import com.micah.crawler.event.CrawlerEvent;
import com.micah.crawler.event.EventManager;
import com.micah.crawler.spider.Spider;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Crawler Entry class
 *
 * @author Micah
 * @date 2020/10/3
 */
@Slf4j
@NoArgsConstructor
public class Crawler {

    List<Spider> spiders = new ArrayList<>();
    Config config;

    public static Crawler me(Spider spider) {
        return me(spider, Config.me());
    }

    public static Crawler me(Spider spider, Config config) {
        Crawler crawler = new Crawler();
        crawler.spiders.add(spider);
        crawler.config = config;
        return crawler;
    }

    public void start() {
        new CrawlerEngine(this).start();
    }

    public Crawler onStart(Consumer<Config> consumer) {
        EventManager.registerEvent(CrawlerEvent.GLOBAL_STARTED, consumer);
        return this;
    }

}
