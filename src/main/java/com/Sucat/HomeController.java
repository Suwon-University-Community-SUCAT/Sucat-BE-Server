package com.Sucat;

import com.Sucat.domain.image.service.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class HomeController {

    private final S3Uploader s3Uploader;

    @GetMapping("/home")
    public String home() {
        return "test";
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/healthcheck")
    public String healthcheck() {
        return "OK";
    }

    @PostMapping("/upload")
    public Map<String, String> upload(@RequestParam("data") MultipartFile multipartFile) throws IOException {
        return s3Uploader.upload(multipartFile);
    }

}
