package com.micah.crawler.event;

import com.micah.crawler.config.Config;

import java.util.*;
import java.util.function.Consumer;

/**
 * Event Manager class
 *
 * @author Micah
 * @date 2020/10/3
 */
public class EventManager {

    private static final Map<CrawlerEvent, List<Consumer<Config>>> crawlerEventConsumerMap = new HashMap<>();

    public static void registerEvent(CrawlerEvent crawlerEvent, Consumer<Config> consumer) {
        List<Consumer<Config>> consumers = crawlerEventConsumerMap.get(crawlerEvent);
        if (null == consumers) {
            consumers = new ArrayList<>();
        }
        consumers.add(consumer);
        crawlerEventConsumerMap.put(crawlerEvent, consumers);
    }

    public static void fireEvent(CrawlerEvent crawlerEvent, Config config) {
        Optional.ofNullable(crawlerEventConsumerMap.get(crawlerEvent)).ifPresent(consumers -> consumers.forEach(consumer -> consumer.accept(config)));
    }

}