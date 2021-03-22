package com.estethapp.media.covid.selfassessment;

public class Question
{
    private String Title;
    private String Content;

    public Question(String title, String content)
    {
        Title = title;
        Content = content;
    }

    public String getTitle()
    {
        return Title;
    }

    public String getContent()
    {
        return Content;
    }
}
