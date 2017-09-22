package com.github.vaerys.pogos;

import com.github.vaerys.objects.setup.GuildFile;

public class GuildData extends GuildFile{
    public static final String FILE_PATH = "Guild_Data.json";
    private double fileVersion = 1.0;
    private String prefixCommand = "!";

    public String getPrefixCommand() {
        return prefixCommand;
    }
}

