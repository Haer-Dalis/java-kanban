import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

public class Main {

    public static void main(String[] args) {

        System.out.println("Поехали!");
        Manager manager = new Manager();
        manager.addTask(new Task("Написать расписание", "Самое основное дело", Status.NEW));
        manager.addTask(new Task("Выпить чашечку кофе", "Без этого - никуда", Status.IN_PROGRESS));
        manager.addEpic(new Epic("Написать программу", "Непреодолимое дело"));
        manager.addSubtask(new Subtask("Прочитать техзадание", "От этого многое зависит", Status.DONE, 3));
        manager.addSubtask(new Subtask("Пробовать писать код", "Это сложно", Status.IN_PROGRESS, 3));
        manager.addEpic(new Epic("Пойти спать", "Очень важное дело"));
        manager.addSubtask(new Subtask("Почистить зубы перед сном", "Главная составляющая", Status.DONE, 6));
        System.out.println("Список эпиков " + manager.getEpicList());
        System.out.println("Список задач " + manager.getTaskList());
        System.out.println("Список подзадач " + manager.getSubtaskList());

        manager.deleteTask(1);
        manager.deleteEpic(3);

        System.out.println("\n");
        System.out.println("После удаления одной задачи и одного эпика ");
        System.out.println("\n");
        System.out.println("Список эпиков " + manager.getEpicList());
        System.out.println("Список задач " + manager.getTaskList());
        System.out.println("Список подзадач " + manager.getSubtaskList());
    }
}
