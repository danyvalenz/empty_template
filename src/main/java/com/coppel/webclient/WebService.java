package com.coppel.webclient;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class WebService {

    private final String name;
    private final String url;
    private final Boolean useProxy;
}
