package br.imd.aqueducte.services.implementations;

import br.imd.aqueducte.config.AMQPConfig;
import br.imd.aqueducte.models.enums.TaskStatus;
import br.imd.aqueducte.services.AMQPConsumer;
import br.imd.aqueducte.services.TaskStatusService;
import com.google.gson.Gson;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;


@Component
@Log4j2
public class TaskAMQPConsumerImpl implements AMQPConsumer {

    @Autowired
    private TaskStatusService taskStatusService;

    @Override
    @RabbitListener(queues = AMQPConfig.QUEUE)
    public void consumer(Message message) {
        try {
            Map<String, Object> taskMap = toMap(message.getBody());
            taskStatusService.sendTaskStatusProgress(
                    taskMap.get("id").toString(),
                    TaskStatus.valueOf(taskMap.get("status").toString()),
                    taskMap.get("description").toString(),
                    taskMap.get("topic").toString()
            );
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }

    private static Map<String, Object> toMap(byte[] data) throws IOException, ClassNotFoundException {
        String dataString = new String(data);
        log.info("Receiving task message: {}", dataString);
        Gson gson = new Gson();
        Map<String, Object> map = gson.fromJson(dataString, Map.class);
        return map;
    }
}
