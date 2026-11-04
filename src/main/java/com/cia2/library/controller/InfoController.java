package com.cia2.library.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

/** Reports the running version and host so a redeploy is visible from the browser (host = Kubernetes pod name). */
@RestController
public class InfoController {

    private final String version;

    public InfoController(@Value("${app.version}") String version) {
        this.version = version;
    }

    @GetMapping("/api/info")
    public Map<String, String> info() {
        return Map.of("app", "Library Book Manager", "version", version, "host", hostname());
    }

    private static String hostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "unknown";
        }
    }
}
