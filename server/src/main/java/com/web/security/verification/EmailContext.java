package com.web.security.verification;

import java.util.Map;

public class EmailContext
{
    private String to;
    private String subject;
    private String displayName;
    private String templateLocation;
    private Map<String, Object> context;

    public String getTo()
    {
        return to;
    }

    public void setTo(String to)
    {
        this.to = to;
    }

    public String getSubject()
    {
        return subject;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public void setDisplayName(String displayName)
    {
        this.displayName = displayName;
    }

    public String getTemplateLocation()
    {
        return templateLocation;
    }

    public void setTemplateLocation(String templateLocation)
    {
        this.templateLocation = templateLocation;
    }

    public Map<String, Object> getContext()
    {
        return context;
    }

    public void setContext(Map<String, Object> context)
    {
        this.context = context;
    }
}
