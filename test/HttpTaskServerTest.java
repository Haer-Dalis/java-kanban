import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import http.HttpTaskServer;
import org.junit.jupiter.api.*;
import serializers.*;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import com.google.gson.Gson;

class HttpTaskServerTest {
    @Test
    @DisplayName("добавление и получение заданий...")
    protected void addTasks() throws IOException, InterruptedException {
        HttpTaskServer httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();
        HttpClient httpClient = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/tasks/");
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.registerTypeAdapter(Task.class, new TaskSerializer()).create();
        Task task1 = new Task("Таск 1", "Доделать эту ужасающую задачу по программированию",
                Status.NEW, LocalDateTime.of(2027, 4, 1, 2, 5), Duration.ofHours(5));
        String taskString1 = gson.toJson(task1, Task.class);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(taskString1);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response);

        URI url51 = URI.create("http://localhost:8080/tasks/1/");
        request = HttpRequest.newBuilder().uri(url51).GET().build();
        response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response);
        System.out.println(response.body());


        gson = gsonBuilder.registerTypeAdapter(Task.class, new TaskDeserializer()).create();
        Task currentTask = gson.fromJson(response.body(), Task.class);
        System.out.println(currentTask);

        Assertions.assertEquals(task1.getName(), currentTask.getName(),
                "У таски, полученной от сервера, другое имя");
        Assertions.assertEquals(task1.getDescription(), currentTask.getDescription(),
                "У таски другое описание");

        httpTaskServer.stop();
    }

    @Test
    @DisplayName("добавление и получение подзадач и эпиков...")
    protected void addSubtasks() throws IOException, InterruptedException {

        HttpTaskServer httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();
        HttpClient httpClient = HttpClient.newHttpClient();


        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.registerTypeAdapter(Epic.class, new EpicSerializer()).create();
        Epic epic1 = new Epic("Эпик 1", "Надо добить спринт 9, пожалуйста");
        String epicString1 = gson.toJson(epic1, Epic.class);
        URI url2 = URI.create("http://localhost:8080/epics/");
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(epicString1);
        HttpRequest request = HttpRequest.newBuilder().uri(url2).POST(body).build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response);

        gson = gsonBuilder.registerTypeAdapter(Subtask.class, new SubtaskSerializer()).create();
        Subtask subtask1 = new Subtask("Сабтаск 1", "Внимательно прочитать ТЗ",
                Status.NEW, 1, LocalDateTime.of(2024, 4, 1, 2, 5), Duration.ofHours(5));
        String subtaskString1 = gson.toJson(subtask1, Subtask.class);
        URI url5 = URI.create("http://localhost:8080/subtasks/");
        body = HttpRequest.BodyPublishers.ofString(subtaskString1);
        request = HttpRequest.newBuilder().uri(url5).POST(body).build();
        response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response);

        Subtask subtask2 = new Subtask("Сабтаск 2", "Писать код до 3-х утра",
                Status.IN_PROGRESS, 1, LocalDateTime.of(2025, 4, 1, 2, 5), Duration.ofHours(5));
        String subtaskString2 = gson.toJson(subtask2, Subtask.class);
        URI url61 = URI.create("http://localhost:8080/subtasks/");
        body = HttpRequest.BodyPublishers.ofString(subtaskString2);
        request = HttpRequest.newBuilder().uri(url61).POST(body).build();
        response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response);

        URI url56 = URI.create("http://localhost:8080/epics/1/");
        request = HttpRequest.newBuilder().uri(url56).GET().build();
        response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response);


        URI url55 = URI.create("http://localhost:8080/epics/1/");
        request = HttpRequest.newBuilder().uri(url55).GET().build();
        response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        gson = gsonBuilder.registerTypeAdapter(Epic.class, new EpicDeserializer()).create();
        Epic currentEpic = gson.fromJson(response.body(), Epic.class);

        URI url57 = URI.create("http://localhost:8080/subtasks/2/");
        request = HttpRequest.newBuilder().uri(url57).GET().build();
        response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response);
        gson = gsonBuilder.registerTypeAdapter(Subtask.class, new SubtaskDeserializer()).create();
        Subtask currentSubtask = gson.fromJson(response.body(), Subtask.class);
        System.out.println(currentSubtask);

        URI url70 = URI.create("http://localhost:8080/epics/1/subtasks/");
        request = HttpRequest.newBuilder().uri(url70).GET().build();
        response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response);


        Assertions.assertEquals(subtask1.getName(), currentSubtask.getName(),
                "У сабстаска с сервера другое имя.");
        Assertions.assertEquals(epic1.getDescription(), currentEpic.getDescription(),
                "И другое описание ‿︵‿ヽ(°□° )ノ︵‿︵");

        httpTaskServer.stop();
    }

    @Test
    @DisplayName("получение списка заданий и удаление...")
    protected void gettingListTasks() throws IOException, InterruptedException {
        HttpTaskServer httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();
        HttpClient httpClient = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/tasks/");
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.registerTypeAdapter(Task.class, new TaskSerializer()).create();
        Task task1 = new Task("Таск 1", "Придумать много тестов",
                Status.NEW, LocalDateTime.of(2027, 4, 1, 2, 5), Duration.ofHours(5));
        String taskString1 = gson.toJson(task1, Task.class);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(taskString1);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response);

        gson = gsonBuilder.registerTypeAdapter(Task.class, new TaskSerializer()).create();
        task1 = new Task("Таск 2", "Смотреть, как они все проваливаются :'(",
                Status.IN_PROGRESS, LocalDateTime.of(2028, 4, 1, 2, 5), Duration.ofHours(5));
        taskString1 = gson.toJson(task1, Task.class);

        body = HttpRequest.BodyPublishers.ofString(taskString1);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response);

        URI url51 = URI.create("http://localhost:8080/tasks/1/");
        request = HttpRequest.newBuilder().uri(url51).DELETE().build();
        response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response);

        URI url9 = URI.create("http://localhost:8080/tasks/");
        request = HttpRequest.newBuilder().uri(url9).GET().build();
        response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response);
        System.out.println(response.body());

        httpTaskServer.stop();
    }

    @Test
    @DisplayName("получение истории...")
    protected void gettingHistory() throws IOException, InterruptedException {

        HttpTaskServer httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();
        HttpClient httpClient = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/tasks/");
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.registerTypeAdapter(Task.class, new TaskSerializer()).create();
        Task task1 = new Task("Таск 1", "И его описание",
                Status.NEW, LocalDateTime.of(2027, 4, 1, 2, 5), Duration.ofHours(5));
        String taskString1 = gson.toJson(task1, Task.class);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(taskString1);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response);

        gson = gsonBuilder.registerTypeAdapter(Task.class, new TaskSerializer()).create();
        task1 = new Task("Таск 2", "И его не менее интересное описание",
                Status.IN_PROGRESS, LocalDateTime.of(2028, 4, 1, 2, 5), Duration.ofHours(5));
        taskString1 = gson.toJson(task1, Task.class);
        body = HttpRequest.BodyPublishers.ofString(taskString1);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response);

        gson = gsonBuilder.registerTypeAdapter(Epic.class, new EpicSerializer()).create();
        Epic epic1 = new Epic("Эпик 1", "И его эпическое описание");
        String epicString1 = gson.toJson(epic1, Epic.class);
        URI url2 = URI.create("http://localhost:8080/epics/");
        body = HttpRequest.BodyPublishers.ofString(epicString1);
        request = HttpRequest.newBuilder().uri(url2).POST(body).build();
        response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response);

        URI url55 = URI.create("http://localhost:8080/epics/3/");
        request = HttpRequest.newBuilder().uri(url55).GET().build();
        response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        URI url51 = URI.create("http://localhost:8080/tasks/1/");
        request = HttpRequest.newBuilder().uri(url51).GET().build();
        response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        URI url52 = URI.create("http://localhost:8080/tasks/2/");
        request = HttpRequest.newBuilder().uri(url52).GET().build();
        response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        URI url7 = URI.create("http://localhost:8080/history/");
        request = HttpRequest.newBuilder().uri(url7).GET().build();
        response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response);
        System.out.println(response.body());

        Gson gson12 = new Gson();
        List<Integer> history = gson12.fromJson(response.body(), new TypeToken<List<Integer>>() {}.getType());
        Assertions.assertEquals(List.of(3, 1, 2), history, "История была грустна");

        httpTaskServer.stop();
    }

}