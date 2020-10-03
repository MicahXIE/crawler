package com.micah.crawler.scheduler;

import com.micah.crawler.request.Request;
import com.micah.crawler.response.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Web Crawler Scheduler
 *
 * @author Micah
 * @date 2020/10/3
 */
@Slf4j
public class Scheduler {

    private BlockingQueue<Request>  pending = new LinkedBlockingQueue<>();
    private BlockingQueue<Response> result  = new LinkedBlockingQueue<>();

    public void addRequest(Request request) {
        try {
            this.pending.put(request);
        } catch (InterruptedException e) {
            log.error("Scheduler add Request error", e);
        }
    }

    public void addResponse(Response response) {
        try {
            this.result.put(response);
        } catch (InterruptedException e) {
            log.error("Scheduler add Response error", e);
        }
    }

    public boolean hasRequest() {
        return pending.size() > 0;
    }

    public Request nextRequest() {
        try {
            return pending.take();
        } catch (InterruptedException e) {
            log.error("Scheduler get Request error", e);
            return null;
        }
    }

    public boolean hasResponse() {
        return result.size() > 0;
    }

    public Response nextResponse() {
        try {
            return result.take();
        } catch (InterruptedException e) {
            log.error("Scheduler get Response error", e);
            return null;
        }
    }

    public void addRequests(List<Request> requests) {
        requests.forEach(this::addRequest);
    }

    public void clear() {
        pending.clear();
    }

}
