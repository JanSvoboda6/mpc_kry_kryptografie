package com.web.file;

import com.google.common.primitives.Bytes;
import com.web.security.user.User;
import com.web.security.user.UserRepository;
import com.web.security.utility.JsonWebTokenUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dataset")
public class FileController
{
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
            return fileService.getAllFiles(user.get().getId());
        }
        return Collections.emptyList();
    }

    @PostMapping(value = "createdirectory") //TODO Jan: rename to folder
    public ResponseEntity<?> createDirectory(@RequestHeader(name="Authorization") String token, @RequestBody Key directoryKey) //TODO Jan: is Key needed?
    {
        //TODO Jan: Filter keys with parent directory symbols . / ..

        Optional<User> user = userRepository.findByUsername(jsonWebTokenUtility.getUsernameFromJwtToken(token));
        if(user.isPresent())
        {
            fileService.createFolder(directoryKey.getKey(), user.get().getId());
            return ResponseEntity.ok("OK.");
        }
        return ResponseEntity.badRequest().body("User was not found!");
    }

    @PostMapping(value = "/upload")
    public ResponseEntity<?> uploadFiles(@RequestHeader(name="Authorization") String token, @RequestBody FileRequest request)
    {
        Optional<User> user = userRepository.findByUsername(jsonWebTokenUtility.getUsernameFromJwtToken(token));
        if(user.isPresent())
        {
            fileService.uploadFiles(request.getKeys(), request.getFiles().stream().map(String::getBytes).collect(Collectors.toList()), user.get().getId());
            return ResponseEntity.ok("OK.");
        }
        return ResponseEntity.badRequest().body("User was not found!");
    }

    @PostMapping(value = "/folders/delete")
    public ResponseEntity<?> batchDeleteFolders(@RequestHeader(name="Authorization") String token, @RequestBody List<String> keys)
    {
        Optional<User> user = userRepository.findByUsername(jsonWebTokenUtility.getUsernameFromJwtToken(token));
        if(user.isPresent())
        {
            fileService.deleteFolders(keys, user.get().getId());
            return ResponseEntity.ok("OK.");
        }
        return ResponseEntity.badRequest().body("User was not found!");
    }

    @PostMapping(value = "/files/delete")
    public ResponseEntity<?> batchDeleteFiles(@RequestHeader(name="Authorization") String token, @RequestBody List<String> keys)
    {
        Optional<User> user = userRepository.findByUsername(jsonWebTokenUtility.getUsernameFromJwtToken(token));
        if(user.isPresent())
        {
            fileService.deleteFiles(keys, user.get().getId());
            return ResponseEntity.ok("OK.");
        }
        return ResponseEntity.badRequest().body("User was not found!");
    }

    @PostMapping(value = "/files/move")
    public ResponseEntity<?> moveFile(@RequestHeader(name="Authorization") String token, @RequestBody MoveRequest request)
    {
        Optional<User> user = userRepository.findByUsername(jsonWebTokenUtility.getUsernameFromJwtToken(token));
        if(user.isPresent())
        {
            fileService.moveFile(request.getOldKey(), request.getNewKey(), user.get().getId());
            return ResponseEntity.ok("OK.");
        }
        return ResponseEntity.badRequest().body("User was not found!");
    }

    @PostMapping(value = "/folders/move")
    public ResponseEntity<?> moveFolder(@RequestHeader(name="Authorization") String token, @RequestBody MoveRequest request)
    {
        Optional<User> user = userRepository.findByUsername(jsonWebTokenUtility.getUsernameFromJwtToken(token));
        if(user.isPresent())
        {
            fileService.moveFolder(request.getOldKey(), request.getNewKey(), user.get().getId());
            return ResponseEntity.ok("OK.");
        }
        return ResponseEntity.badRequest().body("User was not found!");

    }

    @PostMapping(value = "/download")
    public ResponseEntity<?> download(@RequestHeader(name="Authorization") String token, @RequestBody List<String> keys)
    {
        Optional<User> user = userRepository.findByUsername(jsonWebTokenUtility.getUsernameFromJwtToken(token));
        if(user.isPresent())
        {
            File file = fileService.download(keys, user.get().getId());
//            ByteArrayResource resource = new ByteArrayResource(Objects.requireNonNull(file.getFileContent()));
//            MediaType mediaType = MediaTypeFactory
//                    .getMediaType(resource)
//                    .orElse(MediaType.APPLICATION_JSON);
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(mediaType);
//            ContentDisposition disposition = ContentDisposition
//                    .attachment()
//                    .build();
//            headers.setContentDisposition(disposition);

            String data = new String(file.getFileContent());
            return ResponseEntity.ok().body(new FileResponse(data));
        }
        return new ResponseEntity<>(null, null, HttpStatus.BAD_REQUEST);
    }
}
