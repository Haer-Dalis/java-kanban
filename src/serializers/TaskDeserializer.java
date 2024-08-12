package serializers;

import com.google.gson.*;
import task.Status;
import task.Task;

import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;

public class TaskDeserializer implements JsonDeserializer<Task> {
    @Override
    public Task deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        int id = 0;

        if (jsonObject.has("id")) {
            id = jsonObject.get("id").getAsInt();
        }
        String nameOfTask = jsonObject.get("nameOfTask").getAsString();
        String descriptionOfTask = jsonObject.get("descriptionOfTask").getAsString();
        String statusOfTask = jsonObject.get("statusOfTask").getAsString();
        int year = jsonObject.get("year").getAsInt();
        int month = jsonObject.get("month").getAsInt();
        int day = jsonObject.get("day").getAsInt();
        int hour = jsonObject.get("hour").getAsInt();
        int minute = jsonObject.get("minute").getAsInt();
        int seconds = jsonObject.get("duration").getAsInt();
        Status status = Serializer.defineStatusOfTask(statusOfTask);

        Task task = new Task(nameOfTask, descriptionOfTask, status, LocalDateTime.of(year, month, day, hour, minute), Duration.ofSeconds(seconds));
        if (id != 0) {
            task.setId(id);
        }

        return task;
    }
}
