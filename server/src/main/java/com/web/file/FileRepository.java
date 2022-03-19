package com.web.file;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<File, Long>
{
    List<File> findAllByUserId(Long userId);
    Optional<File> findByNameAndUserId(String name, Long userId);
    List<NameAndSizeAndModified> findAllNameSizeModifiedByUserId(Long userId);

    interface NameAndSizeAndModified
    {
        String getName();
        long getSize();
        long getModified();
    }
}
