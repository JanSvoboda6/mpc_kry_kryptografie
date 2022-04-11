package com.web.file;

public class IndividualRequestFile
{
    private String key;
    private byte[] fileContent;

    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    public byte[] getFileContent()
    {
        return fileContent;
    }

    public void setFileContent(byte[] fileContent)
    {
        this.fileContent = fileContent;
    }
}
