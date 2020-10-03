package com.micah.crawler.request;

import com.micah.crawler.response.Result;
import com.micah.crawler.response.Response;

/**
 * html parser interface
 *
 * @author Micah
 * @date 2020/10/3
 */
public interface Parser<T> {

    Result<T> parse(Response response);

}
