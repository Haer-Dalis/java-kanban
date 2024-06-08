import manager.InMemoryTaskManager;
import manager.Managers;
import manager.TaskManager;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

public class Main {

    public static void main(String[] args) {

        System.out.println("Поехали!");
        TaskManager inMemoryTaskManager = Managers.getDefault();
        inMemoryTaskManager.addTask(new Task("Написать расписание", "Самое основное дело", Status.NEW));
        inMemoryTaskManager.addTask(new Task("Выпить чашечку кофе", "Без этого - никуда", Status.IN_PROGRESS));
        inMemoryTaskManager.addEpic(new Epic("Написать программу", "Непреодолимое дело"));
        inMemoryTaskManager.addSubtask(new Subtask("Прочитать техзадание", "От этого многое зависит", Status.DONE, 3));
        inMemoryTaskManager.addSubtask(new Subtask("Пробовать писать код", "Это сложно", Status.IN_PROGRESS, 3));
        inMemoryTaskManager.addEpic(new Epic("Пойти спать", "Очень важное дело"));
        inMemoryTaskManager.addSubtask(new Subtask("Почистить зубы перед сном", "Главная составляющая", Status.DONE, 6));
        System.out.println("Список эпиков " + inMemoryTaskManager.getEpicList());
        System.out.println("Список задач " + inMemoryTaskManager.getTaskList());
        System.out.println("Список подзадач " + inMemoryTaskManager.getSubtaskList());

        inMemoryTaskManager.deleteTask(1);
        inMemoryTaskManager.deleteEpic(3);

        System.out.println("\n");
        System.out.println("После удаления одной задачи и одного эпика ");
        System.out.println("\n");
        System.out.println("Список эпиков " + inMemoryTaskManager.getEpicList());
        System.out.println("Список задач " + inMemoryTaskManager.getTaskList());
        System.out.println("Список подзадач " + inMemoryTaskManager.getSubtaskList());


    }
}
