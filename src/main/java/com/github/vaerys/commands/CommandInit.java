package com.github.vaerys.commands;

import com.github.vaerys.commands.creator.SetAvatar;
import com.github.vaerys.commands.creator.SetUsername;
import com.github.vaerys.commands.creator.Shutdown;
import com.github.vaerys.commands.general.Hello;
import com.github.vaerys.objects.setup.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class CommandInit {

    final static Logger logger = LoggerFactory.getLogger(CommandInit.class);

    public static List<Command> get() {
        List<Command> commands = new ArrayList();

        commands.add(new Hello());


        checkList(commands);
        return commands;
    }

    public static List<Command> getCreator() {
        List<Command> commands = new ArrayList();

        commands.add(new SetAvatar());
        commands.add(new SetUsername());
        commands.add(new Shutdown());

        checkList(commands);
        return commands;
    }


    private static void checkList(List<Command> commands) {
        //validate commands
        List<String> errors = new ArrayList<>();
        commands.forEach(o -> {
            String error = o.validate();
            if (error != null) {
                errors.add(error);
            }
        });
        if (errors.size() != 0) {
            for (String s : errors) {
                logger.error(s);
            }
            System.exit(0);
        }
    }
}
