package http;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import exception.ManagerSaveException;
import manager.InMemoryTaskManager;
import serializers.*;
import task.Epic;
import task.Subtask;
import task.Task;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpTaskServer {
    public static final int PORT = 8080;
    private final HttpServer server;

    public HttpTaskServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/tasks/", this::chooseMethodTasks);
        server.createContext("/subtasks/", this::chooseMethodSubtasks);
        server.createContext("/epics/", this::chooseMethodEpics);
        server.createContext("/history/", this::loadHistory);
        server.createContext("/prioritized/", this::prioritizedTasks);
    }


    GsonBuilder gsonBuilder = new GsonBuilder();
    Gson gson = gsonBuilder.create();
    InMemoryTaskManager taskManager = new InMemoryTaskManager();

    public static void main(String[] args) throws IOException {
        HttpTaskServer httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();
    }

    public void start() {
        System.out.println("Запущен сервер на порту " + PORT);
        server.start();
    }

    private void chooseMethodTasks(HttpExchange httpExchange) throws IOException {
        try {
            String path = httpExchange.getRequestURI().getPath();
            String[] parts = path.split("/");
            if (parts.length > 2 && parts[1].equals("tasks")) {
                int query = Integer.parseInt(parts[2]);
                if ("GET".equals(httpExchange.getRequestMethod())) {
                    Task currentTask = taskManager.getTask(query);
                    gson = gsonBuilder.registerTypeAdapter(Task.class, new TaskSerializer()).create();
                    String gsonString = gson.toJson(currentTask, Task.class);
                    sendText(gsonString, httpExchange);
                    httpExchange.sendResponseHeaders(200, 0);
                } else if ("DELETE".equals(httpExchange.getRequestMethod())) {
                    if (taskManager.getTasks().containsKey(query)) {
                        taskManager.deleteTask(query);
                        httpExchange.sendResponseHeaders(200, 0);
                        System.out.println("Таск № " + query + " успешно удален");
                    } else {
                        httpExchange.sendResponseHeaders(404, 0);
                    }
                } else {
                    System.out.println("/tasks/{id} принимает GET-запрос или DELETE-запрос, " +
                            "а получил: " + httpExchange.getRequestMethod());
                    httpExchange.sendResponseHeaders(405, 0);
                }
                return;
            }
            if ("GET".equals(httpExchange.getRequestMethod())) {
                GsonBuilder gsonBuilder = new GsonBuilder();
                Gson gson = gsonBuilder.registerTypeAdapter(HashMap.class, new HashMapTasksSerializer()).create();
                String gsonString = gson.toJson(taskManager.getTasks(), HashMap.class);
                sendText(gsonString, httpExchange);
                httpExchange.sendResponseHeaders(200, 0);
            } else if ("POST".equals(httpExchange.getRequestMethod())) {
                GsonBuilder gsonBuilder2 = new GsonBuilder();
                Gson gson2 = gsonBuilder2.registerTypeAdapter(Task.class, new TaskDeserializer()).create();
                String jsonString = new String(httpExchange.getRequestBody().readAllBytes(), UTF_8);
                Task currentTask = gson2.fromJson(jsonString, Task.class);
                if (taskManager.getTasks().containsKey(currentTask.getId())) {
                    taskManager.updateTask(currentTask);
                    httpExchange.sendResponseHeaders(201, 0);
                    System.out.println("Таск успешно обновлен");
                } else {
                    taskManager.addTask(currentTask);
                    httpExchange.sendResponseHeaders(201, 0);
                    System.out.println("Таск успешно добавлен");
                }

            } else {
                System.out.println("/tasks/ принимает GET-запрос или POST-запрос, " +
                        "а получил: " + httpExchange.getRequestMethod());
                httpExchange.sendResponseHeaders(405, 0);
            }
        } catch (NullPointerException | IOException e) {
            throw new ManagerSaveException("Что-то пошло не совсем так");
        } finally {
            httpExchange.close();
        }
    }


    private void chooseMethodSubtasks(HttpExchange httpExchange) {
        try {
            String path = httpExchange.getRequestURI().getPath();
            String[] parts = path.split("/");
            if (parts.length > 2 && parts[1].equals("subtasks")) {
                int query = Integer.parseInt(parts[2]);
                if ("GET".equals(httpExchange.getRequestMethod())) {
                    if (taskManager.getSubtasks().containsKey(query)) {
                        gson = gsonBuilder.registerTypeAdapter(Subtask.class, new SubtaskSerializer()).create();
                        String gsonString = gson.toJson(taskManager.getSubtask(query), Subtask.class);
                        sendText(gsonString, httpExchange);
                        httpExchange.sendResponseHeaders(200, 0);
                        System.out.println(taskManager.getSubtask(query).toString());
                    } else {
                        httpExchange.sendResponseHeaders(404, 0);
                    }
                } else if ("DELETE".equals(httpExchange.getRequestMethod())) {
                    taskManager.deleteSubtask(query);
                    httpExchange.sendResponseHeaders(200, 0);
                    System.out.println("Субтаск № " + query + " успешно удален");
                } else {
                    System.out.println("/subtask/{id} принимает GET-запрос или DELETE-запрос, " +
                            "а получил: " + httpExchange.getRequestMethod());
                    httpExchange.sendResponseHeaders(405, 0);
                }
                return;
            }

            if ("GET".equals(httpExchange.getRequestMethod())) {
                GsonBuilder gsonBuilder = new GsonBuilder();
                Gson gson = gsonBuilder.registerTypeAdapter(HashMap.class, new HashMapSubtasksSerializer()).create();
                String gsonString = gson.toJson(taskManager.getSubtasks(), HashMap.class);
                sendText(gsonString, httpExchange);
                httpExchange.sendResponseHeaders(200, 0);
            } else if ("POST".equals(httpExchange.getRequestMethod())) {
                GsonBuilder gsonBuilder2 = new GsonBuilder();
                Gson gson2 = gsonBuilder2.registerTypeAdapter(Subtask.class, new SubtaskDeserializer()).create();
                String jsonString = new String(httpExchange.getRequestBody().readAllBytes(), UTF_8);
                Subtask currentSubtask = gson2.fromJson(jsonString, Subtask.class);
                if (taskManager.getSubtasks().containsKey(currentSubtask.getId())) {
                    taskManager.updateSubtask(currentSubtask);
                    httpExchange.sendResponseHeaders(201, 0);
                    System.out.println("Субтаск успешно обновлен");
                } else {
                    taskManager.addSubtask(currentSubtask);
                    httpExchange.sendResponseHeaders(201, 0);
                    System.out.println("Субтаск успешно добавлен");
                }
            } else {
                System.out.println("/subtask/ принимает GET-запрос или POST-запрос, " +
                        "а получил: " + httpExchange.getRequestMethod());
                httpExchange.sendResponseHeaders(405, 0);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            httpExchange.close();
        }
    }


    private void chooseMethodEpics(HttpExchange httpExchange) throws IOException {
        try {
            String path = httpExchange.getRequestURI().getPath();
            String[] parts = path.split("/");

            if (parts[1].equals("epics") && parts.length == 3) {
                int query = Integer.parseInt(parts[2]);
                if ("GET".equals(httpExchange.getRequestMethod())) {
                    if (taskManager.getEpics().containsKey(query)) {
                        gson = gsonBuilder.registerTypeAdapter(Epic.class, new EpicSerializer()).create();
                        String gsonString = gson.toJson(taskManager.getEpic(query), Epic.class);
                        sendText(gsonString, httpExchange);
                        httpExchange.sendResponseHeaders(200, 0);
                        System.out.println(taskManager.getEpic(query).toString());
                    } else {
                        httpExchange.sendResponseHeaders(404, 0);
                    }

                } else if ("DELETE".equals(httpExchange.getRequestMethod())) {
                    taskManager.deleteEpic(query);
                    httpExchange.sendResponseHeaders(200, 0);
                    System.out.println("Epic № " + query + " has been successfully deleted");
                } else {
                    System.out.println("/epics/{id} ждёт GET-запрос или DELETE-запрос, " +
                            "а получил: " + httpExchange.getRequestMethod());
                    httpExchange.sendResponseHeaders(405, 0);
                }
                return;
            } else if (parts.length == 4 && parts[3].equals("subtasks")
                    && "GET".equals(httpExchange.getRequestMethod())) {
                int epicId = Integer.parseInt(parts[2]);
                Gson gson = gsonBuilder.registerTypeAdapter(HashMap.class, new ListSubtasksSerializer()).create();
                String gsonString = gson.toJson(taskManager.getSubtasksFromEpic(epicId), HashMap.class);
                sendText(gsonString, httpExchange);
                httpExchange.sendResponseHeaders(200, 0);
            }

            if ("GET".equals(httpExchange.getRequestMethod())) {
                GsonBuilder gsonBuilder = new GsonBuilder();
                Gson gson = gsonBuilder.registerTypeAdapter(HashMap.class, new HashMapEpicsSerializer()).create();
                String gsonString = gson.toJson(taskManager.getEpics(), HashMap.class);
                sendText(gsonString, httpExchange);
                httpExchange.sendResponseHeaders(200, 0);
            } else if ("POST".equals(httpExchange.getRequestMethod())) {
                GsonBuilder gsonBuilder2 = new GsonBuilder();
                Gson gson2 = gsonBuilder2.registerTypeAdapter(Epic.class, new EpicDeserializer()).create();
                String jsonString = new String(httpExchange.getRequestBody().readAllBytes(), UTF_8);
                Epic currentEpic = gson2.fromJson(jsonString, Epic.class);
                taskManager.addEpic(currentEpic);
                httpExchange.sendResponseHeaders(201, 0);
                System.out.println("Эпик был успешно добавлен");
            } else {
                System.out.println("/epics/ ждёт GET-запрос или DELETE-запрос, " +
                        "а получил: " + httpExchange.getRequestMethod());
                httpExchange.sendResponseHeaders(405, 0);
            }

        } finally {
            httpExchange.close();
        }
    }

    private void prioritizedTasks(HttpExchange httpExchange) {
        try {
            String path = httpExchange.getRequestURI().getPath();
            String[] parts = path.split("/");
            if (parts[1].equals("prioritized") && "GET".equals(httpExchange.getRequestMethod())) {
                Gson gson = new Gson();
                Type gsonType = new TypeToken<TreeSet>() {
                }.getType();
                String gsonString = gson.toJson(taskManager.getPrioritizedTasks(), gsonType);
                sendText(gsonString, httpExchange);
                httpExchange.sendResponseHeaders(200, 0);
                System.out.println(taskManager.getPrioritizedTasks().toString());
            } else {
                System.out.println("/prioritized/ ждёт GET-запрос, " +
                        "а получил: " + httpExchange.getRequestMethod());
                httpExchange.sendResponseHeaders(405, 0);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            httpExchange.close();
        }
    }

    private void loadHistory(HttpExchange httpExchange) {
        try {
            String path = httpExchange.getRequestURI().getPath();
            String[] parts = path.split("/");

            if (parts[1].equals("history") && "GET".equals(httpExchange.getRequestMethod())) {
                List<Task> history = taskManager.getHistory();
                List<Integer> taskIds = new ArrayList<>();
                for (Task task : history) {
                    taskIds.add(task.getId());
                }
                Gson gson = new Gson();
                String gsonString = gson.toJson(taskIds);
                sendText(gsonString, httpExchange);
                httpExchange.sendResponseHeaders(200, 0);
            } else {
                System.out.println("/history ждёт GET-запрос, " +
                        "а получил: " + httpExchange.getRequestMethod());
                httpExchange.sendResponseHeaders(405, 0);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            httpExchange.close();
        }
    }

    private void sendText(String gsonString, HttpExchange httpExchange) throws IOException {
        byte[] bytes = gsonString.getBytes(StandardCharsets.UTF_8);
        httpExchange.sendResponseHeaders(201, bytes.length);
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    public void stop() {
        server.stop(1);
        System.out.println("HttpTaskServer остановлен");
    }

}
