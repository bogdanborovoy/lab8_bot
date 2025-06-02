package commands;

import models.SpaceMarine;

public class ChangeLanguageCommand implements Command {
    @Override
    public void execute() {

    }

    @Override
    public String descr() {
        return "change_language : изменить язык интерфейса (Русский \uD83C\uDDF7\uD83C\uDDFA / Македонский \uD83C\uDDF2\uD83C\uDDF0 / Венгерский \uD83C\uDDED\uD83C\uDDFA / Испанский(Коста-Рика) \uD83C\uDDE8\uD83C\uDDF7)";
    }

    @Override
    public void setInteractive(boolean b) {

    }

    @Override
    public void passValue(String[] value) {

    }

    @Override
    public void passSpaceMarine(SpaceMarine spaceMarine) {

    }
}

