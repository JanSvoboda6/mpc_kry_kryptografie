package com.web.file;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService
{
    List<FileInformation> getAllFiles(long userId);
    void uploadFiles(List<String> key, List<byte[]> files, long userId);
    void createFolder(String key, long userId);
    void deleteFolders(List<String> keys, long userId);
    void deleteFiles(List<String> keys, long userId);
    void moveFile(String oldKey, String newKey, long userId);
    void moveFolder(String oldKey, String newKey, long userId);
    File download(List<String> keys, long userId);
}
