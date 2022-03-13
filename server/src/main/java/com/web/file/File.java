package com.web.file;

import com.web.security.user.User;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
public class File
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Lob
    private byte[] fileContent;

    private String name;
    private long modified;
    private long size;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String fileName)
    {
        this.name = fileName;
    }

    public byte[] getFileContent()
    {
        return fileContent;
    }

    public void setFileContent(byte[] fileContent)
    {
        this.fileContent = fileContent;
    }

    public long getModified()
    {
        return modified;
    }

    public void setModified(long modified)
    {
        this.modified = modified;
    }

    public long getSize()
    {
        return size;
    }

    public void setSize(long size)
    {
        this.size = size;
    }
}
