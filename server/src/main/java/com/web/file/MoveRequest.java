package com.web.file;

import javax.validation.constraints.NotBlank;

public class MoveRequest
{
    @NotBlank
    private String oldKey;

    @NotBlank
    private String newKey;

    public String getOldKey()
    {
        return oldKey;
    }

    public String getNewKey()
    {
        return newKey;
    }
}
