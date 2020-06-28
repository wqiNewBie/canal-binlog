package cn.lubo.binlog.receiver;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.Date;


@Component
public class RabbitMQConsumer {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Value("${elasticsearch.index}")
    String index;

    @RabbitListener(queues = "${rabbitmq.queue}")
    public void Receiver(Message message) throws UnsupportedEncodingException {
        byte[] body = message.getBody();
        String string = new String(body, "UTF-8");
        JSONObject jsonObject = JSONObject.parseObject(string);
        String ts = jsonObject.getString("ts");
        jsonObject.remove("ts");
        jsonObject.put("ts", new Date(Long.parseLong(ts)));
        insertOrUpdateOne(jsonObject);
    }


    public void insertOrUpdateOne(JSONObject msg) {
        IndexRequest request = new IndexRequest(index);
        request.id(System.currentTimeMillis() + Math.random() * (100) + "");
        request.source(JSON.toJSONString(msg), XContentType.JSON);
        try {
            restHighLevelClient.index(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}