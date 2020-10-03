package com.micah.crawler.response;

import com.micah.crawler.request.Request;
import com.micah.crawler.utils.CrawlerUtils;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Response Result Class
 *
 * @author Micah
 * @date 2020/10/3
 */
@Data
@NoArgsConstructor
public class Result<T> {

    private List<Request> requests = new ArrayList<>();
    private T item;

    public Result(T item) {
        this.item = item;
    }

    public Result addRequest(Request request) {
        this.requests.add(request);
        return this;
    }

    public Result addRequests(List<Request> requests) {
        if (!CrawlerUtils.isEmpty(requests)) {
            this.requests.addAll(requests);
        }
        return this;
    }

}
