package com.micah.crawler.examples;

import com.micah.crawler.Crawler;
import com.micah.crawler.config.Config;
import com.micah.crawler.pipeline.Pipeline;
import com.micah.crawler.response.Response;
import com.micah.crawler.response.Result;
import com.micah.crawler.spider.Spider;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Element;

import java.util.List;
import java.util.stream.Collectors;

/**
 * BBC News Demo
 *
 * @author Micah
 * @date 2020/10/3
 */
public class BBCNewsExample {

    @Slf4j
    static class News163Spider extends Spider {
        public News163Spider(String name) {
            super(name);
            this.startUrls(
                    "https://www.bbc.com/news/election/us2020",
                    "https://www.bbc.com/news/world",
                    "https://www.bbc.com/news/business",
                    "https://www.bbc.com/news/technology",
                    "https://www.bbc.com/news/entertainment_and_arts",
                    "https://www.bbc.com/news/health"); 
            this.keyWords("Trump", "Covid");
        }

        @Override
        public void onStart(Config config) {
            this.addPipeline((Pipeline<List<String>>) (item, request) -> {
                item.forEach(title -> {
                    for (String word : this.keyWords) {
                        if (title.contains(word)) {
                            System.out.println(title);
                            break;
                        }
                    }
                });
            });
            this.requests.forEach(request -> {
                request.contentType("text/html; charset=utf-8");
                request.charset("utf-8");
            });
        }

        @Override
        protected Result parse(Response response) {
            List<String> titles = response.body().css("a.gs-c-promo-heading > h3").stream()
                    .map(Element::text)
                    .collect(Collectors.toList());

            return new Result(titles);
        }
    }

    public static void main(String[] args) {
        Crawler.me(new News163Spider("BBC News")).start();
    }

}
