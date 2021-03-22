package com.estethapp.media.covid.selfassessment;

public class Response
{
    private boolean Answered = false;
    private boolean Value = false;

    public boolean getAnswer()
    {
        return Value;
    }

    public void setAnswer(boolean value)
    {
        Answered = true;
        Value = value;
    }

    public boolean getIsAnswered()
    {
        return Answered;
    }
}
