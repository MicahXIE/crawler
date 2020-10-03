package com.micah.crawler.pipeline;

import com.micah.crawler.request.Request;
import com.micah.crawler.spider.Spider;

/**
 * Result Item Process interface
 *
 * @author Micah
 * @date 2020/10/3
 */
public interface Pipeline<T> {

    void process(T item, Request request);

}
