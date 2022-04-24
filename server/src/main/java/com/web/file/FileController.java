package com.web.file;

import com.web.security.ValidationException;
import com.web.security.user.User;
import com.web.security.user.UserRepository;
import com.web.security.utility.JsonWebTokenUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 *  Controller class defining set of methods for creation and deletion of {@link File} owned by {@link User}.
 */
@RestController
@RequestMapping("/api")
public class FileController
{
    private static final Logger LOGGER = LoggerFactory.getLogger(FileController.class);

    private final FileService fileService;
    private final JsonWebTokenUtility jsonWebTokenUtility;
    private final UserRepository userRepository;

    @Autowired
    public FileController(FileService fileService, JsonWebTokenUtility jsonWebTokenUtility, UserRepository userRepository)
    {
        this.fileService = fileService;
        this.jsonWebTokenUtility = jsonWebTokenUtility;
        this.userRepository = userRepository;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<FileInformation> getAllFiles(@RequestHeader(name="Authorization") String token)
    {
        Optional<User> user = userRepository.findByUsername(jsonWebTokenUtility.getUsernameFromJwtToken(token));
        if(user.isPresent())
        {
            LOGGER.info("Request for all files by user: {}", user.get().getUsername());
            return fileService.getAllFiles(user.get().getId());
        }
        return Collections.emptyList();
    }

    @PostMapping(value = "/folder/create")
    public ResponseEntity<?> createFolder(@RequestHeader(name="Authorization") String token, @RequestBody Key folderKey)
    {
        Optional<User> user = userRepository.findByUsername(jsonWebTokenUtility.getUsernameFromJwtToken(token));
        if(user.isPresent())
        {
            LOGGER.info("Folder creation request by user: {}, folder: {}", user.get().getUsername(), folderKey.getKey());
            fileService.createFolder(folderKey.getKey(), user.get().getId());
            return ResponseEntity.ok("OK.");
        }
        throw new ValidationException("User was not found!");
    }

    @PostMapping(value = "/file/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFiles(
            @RequestHeader(name="Authorization") String token,
            @RequestPart("keys") Keys keys,
            @RequestPart("files") List<MultipartFile> files)
    {
        Optional<User> user = userRepository.findByUsername(jsonWebTokenUtility.getUsernameFromJwtToken(token));
        if(user.isPresent())
        {
            try
            {
                keys.getKeys().forEach(key -> LOGGER.info("File uploading request by user: {}, file: {}", user.get().getUsername(), key));
                List<byte[]> list = new ArrayList<>();
                for (MultipartFile file : files)
                {
                    byte[] bytes = file.getBytes();
                    list.add(bytes);
                }
                fileService.uploadFiles(
                        keys.getKeys(),
                        list,
                        user.get().getId());
            } catch (Exception exception)
            {
                LOGGER.info("File uploading request failed for user: {}, exception: {}", user.get().getUsername(), exception.getMessage());
                return ResponseEntity.badRequest().body("Exception occurred when uploading files!");
            }
            return ResponseEntity.ok("OK.");
        }
        throw new ValidationException("User was not found!");
    }

    @PostMapping(value = "/folder/delete")
    public ResponseEntity<?> batchDeleteFolders(@RequestHeader(name="Authorization") String token, @RequestBody List<String> keys)
    {
        Optional<User> user = userRepository.findByUsername(jsonWebTokenUtility.getUsernameFromJwtToken(token));
        if(user.isPresent())
        {
            keys.forEach(key -> LOGGER.info("Folder deletion request for user: {}, folder: {}", user.get().getUsername(), key));
            fileService.delete(keys, user.get().getId());
            return ResponseEntity.ok("OK.");
        }
        throw new ValidationException("User was not found!");
    }

    @PostMapping(value = "/file/delete")
    public ResponseEntity<?> batchDeleteFiles(@RequestHeader(name="Authorization") String token, @RequestBody List<String> keys)
    {
        Optional<User> user = userRepository.findByUsername(jsonWebTokenUtility.getUsernameFromJwtToken(token));
        if(user.isPresent())
        {
            keys.forEach(key -> LOGGER.info("File deletion request for user: {}, file: {}", user.get().getUsername(), key));

            fileService.delete(keys, user.get().getId());
            return ResponseEntity.ok("OK.");
        }
        throw new ValidationException("User was not found!");
    }

    @PostMapping(value = "file/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> download(@RequestHeader(name="Authorization") String token, @RequestBody String key)
    {
        Optional<User> user = userRepository.findByUsername(jsonWebTokenUtility.getUsernameFromJwtToken(token));
        if(user.isPresent())
        {
            File file = fileService.download(key, user.get().getId());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentLength(file.getSize());
            ContentDisposition disposition = ContentDisposition
                    .inline()
                    .build();
            headers.setContentDisposition(disposition);
            LOGGER.info("File download request for user: {}, file: {}", user.get().getUsername(), file.getName());

            return new ResponseEntity<>(file.getFileContent(), headers, HttpStatus.OK);
        }
        throw new ValidationException("User was not found!");
    }
}
