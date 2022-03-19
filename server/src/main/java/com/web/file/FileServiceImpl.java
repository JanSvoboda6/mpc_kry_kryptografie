package com.web.file;

import com.google.common.primitives.Bytes;
import com.web.security.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class FileServiceImpl implements FileService
{
    private final FileRepository fileRepository;
    private final UserRepository userRepository;

    @Autowired
    public FileServiceImpl(FileRepository fileRepository, UserRepository userRepository)
    {
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<FileInformation> getAllFiles(long userId)
    {
//        return fileRepository.findAllByUserId(userId).stream().map(file -> new FileInformation(file.getName(), file.getSize(), file.getModified())).collect(Collectors.toList());
        return fileRepository.findAllNameSizeModifiedByUserId(userId).stream().map(file -> new FileInformation(file.getName(), file.getSize(), file.getModified())).collect(Collectors.toList());
    }

    @Override
    public void uploadFiles(List<String> keys, List<byte[]> files, long userId)
    {
        for (int i = 0; i < files.size(); i++)
        {
            try
            {
                File file = new File();
                file.setUser(userRepository.getById(userId));
                file.setName(keys.get(i));
                file.setModified(Instant.now().getEpochSecond());
                file.setSize(files.get(i).length);
                file.setFileContent(files.get(i));
                fileRepository.save(file);
            } catch (Exception exception)
            {
                throw new RuntimeException("Problem occurred when uploading file.");
            }
        }
    }

    @Override
    public void createFolder(String key, long userId)
    {
        File folder = new File();
        folder.setUser(userRepository.getById(userId));
        folder.setName(key);
        fileRepository.save(folder);
    }

    @Override
    public void deleteFolders(List<String> keys, long userId)
    {
        for (String key: keys)
        {
            Optional<File> file = fileRepository.findByNameAndUserId(key, userId);
            file.ifPresent(fileRepository::delete);
        }

    }

    @Override
    public void deleteFiles(List<String> keys, long userId)
    {
        for (String key: keys)
        {
            Optional<File> file = fileRepository.findByNameAndUserId(key, userId);
            file.ifPresent(fileRepository::delete);
        }
    }

    @Override
    public void moveFile(String oldKey, String newKey, long userId)
    {

    }

    @Override
    public void moveFolder(String oldKey, String newKey, long userId)
    {

    }

    @Override
    public File download(List<String> keys, long userId)
    {
        return fileRepository.findByNameAndUserId(keys.get(0), userId).get();
    }
}
