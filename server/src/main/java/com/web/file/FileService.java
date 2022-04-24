package com.web.file;

import java.util.List;

public interface FileService
{
    List<FileInformation> getAllFiles(long userId);
    void uploadFiles(List<String> key, List<byte[]> files, long userId);
    void createFolder(String key, long userId);
    void delete(List<String> keys, long userId);
    File download(String key, long userId);
}
