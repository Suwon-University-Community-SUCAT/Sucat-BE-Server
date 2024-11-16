package com.Sucat.domain.image.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

@Slf4j
@Service
public class S3Uploader {

    private final AmazonS3 amazonS3;
    private final String bucket;
    private final String dirName = "image";


    public S3Uploader(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
        this.bucket = "sucat-file";
    }

    public Map<String, String> upload(MultipartFile multipartFile) throws IOException {
        // 파일 이름에서 공백을 제거한 새로운 파일 이름 생성
        String originalFileName = multipartFile.getOriginalFilename();

        // UUID를 파일명에 추가
        String uuid = UUID.randomUUID().toString();
        String uniqueFileName = uuid + "_" + originalFileName.replaceAll("\\s", "_");

        String fileName = dirName + "/" + uniqueFileName;
        log.info("fileName: " + fileName);
        File uploadFile = convert(multipartFile);

        String uploadImageUrl = putS3(uploadFile, fileName);
        removeNewFile(uploadFile);

        // URL과 이미지 이름을 반환하는 Map 생성
        Map<String, String> response = new HashMap<>();
        response.put("imageUrl", uploadImageUrl);
        response.put("imageName", uniqueFileName); // 고유한 이미지 이름 추가

        return response;
    }

    public List<Map<String, String>> uploadMultiple(List<MultipartFile> multipartFiles) throws IOException {
        List<Map<String, String>> uploadImageInfos = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFiles) {
            Map<String, String> imageInfo = upload(multipartFile);
            uploadImageInfos.add(imageInfo); // 각 이미지의 정보(Map)를 리스트에 추가
        }

        return uploadImageInfos; // 이미지 URL과 이름을 포함한 리스트 반환
    }


    private File convert(MultipartFile file) throws IOException {
        String originalFileName = file.getOriginalFilename();
        String uuid = UUID.randomUUID().toString();
        String uniqueFileName = uuid + "_" + originalFileName.replaceAll("\\s", "_");

        File convertFile = new File(uniqueFileName);
        if (convertFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(file.getBytes());
            } catch (IOException e) {
                log.error("파일 변환 중 오류 발생: {}", e.getMessage());
                throw e;
            }
            return convertFile;
        }
        throw new IllegalArgumentException(String.format("파일 변환에 실패했습니다. %s", originalFileName));
    }

    private String putS3(File uploadFile, String fileName) {
        amazonS3.putObject(new PutObjectRequest(bucket, fileName, uploadFile)
                .withCannedAcl(CannedAccessControlList.PublicRead));
        return amazonS3.getUrl(bucket, fileName).toString();
    }

    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            log.info("파일이 삭제되었습니다.");
        } else {
            log.info("파일이 삭제되지 못했습니다.");
        }
    }

    public void deleteFile(String fileName) {
        try {
            // URL 디코딩을 통해 원래의 파일 이름을 가져옵니다.
            String decodedFileName = URLDecoder.decode(fileName, "UTF-8");
            log.info("Deleting file from S3: " + decodedFileName);
            amazonS3.deleteObject(bucket, decodedFileName);
        } catch (UnsupportedEncodingException e) {
            log.error("Error while decoding the file name: {}", e.getMessage());
        }
    }

    public Map<String, String> updateFile(MultipartFile newFile, String oldFileName) throws IOException {
        // 기존 파일 삭제
        log.info("S3 oldFileName: " + oldFileName);
        deleteFile(oldFileName);
        // 새 파일 업로드
        return upload(newFile);
    }
}