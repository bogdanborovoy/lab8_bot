package commands;

import helpers.CollectionManager;
import models.SpaceMarine;

/**
 * Класс RemoveGreaterCommand
 * удаляет из коллекции все элементы, превышающие заданный
 * @author bogdanborovoy
 */
public class RemoveGreaterCommand implements Command {
    CollectionManager cm;
    private boolean interactive;
    String[] value;
    SpaceMarine spaceMarine;
    public boolean isInteractive() {
        return interactive;
    }
    public void setInteractive(boolean interactive) {
        this.interactive = interactive;
    }

    @Override
    public void passValue(String[] value) {

    }
    public void passSpaceMarine(SpaceMarine spaceMarine) {
        this.spaceMarine = spaceMarine;
    }
    /**
     * Конструктор класса RemoveGreaterCommand.
     *
     * @param cm Менеджер коллекции, который будет использоваться для удаления элементов
     */
    public RemoveGreaterCommand(CollectionManager cm) {
        this.cm = cm;
    }

    /**
     * Метод для выполнения команды удаления элементов, превышающих заданный.
     */
    public void execute() {
//        Scanner sc = new Scanner(System.in);
//        String[] args = sc.nextLine().split(", ");
//        if (interactive) {
//            System.out.println("Создание элемента");
        cm.removeGreater(spaceMarine);
//        }
//        else {
//            cm.removeGreater(cm.add(args));
//        }
    }

    /**
     * Метод для получения описания команды.
     *
     * @return Описание команды в виде строки
     */
    public String descr() {
        return "remove_greater : удалить из коллекции все элементы, превышающие заданный";
    }
}