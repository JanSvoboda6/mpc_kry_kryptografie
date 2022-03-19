package com.web.file;

import javax.validation.constraints.NotBlank;
import java.util.List;

public class FileRequest
{
    @NotBlank
    private List<String> keys;

    @NotBlank
    private List<String> files;

    public List<String> getKeys()
    {
        return keys;
    }

    public void setKeys(List<String> keys)
    {
        this.keys = keys;
    }

    public List<String> getFiles()
    {
        return files;
    }

    public void setFiles(List<String> files)
    {
        this.files = files;
    }
}
