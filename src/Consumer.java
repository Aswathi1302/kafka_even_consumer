import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Arrays;
import java.util.Properties;

public class Consumer {
    public static void main(String[] args) {
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection con= DriverManager.getConnection("jdbc:mysql://localhost:3306/evenkafka","root","");
        }
        catch (Exception e){
            System.out.println((e));
        }
        KafkaConsumer consumer;
        String topic="naturalnumber";
        String broker="localhost:9092";
        Properties props=new Properties();
        props.put("bootstrap.servers",broker);
        props.put("group.id","test.group");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        consumer =new KafkaConsumer(props);
        consumer.subscribe(Arrays.asList(topic));
        while (true)
        {
            ConsumerRecords<String,String> records =consumer.poll(100);
            for(ConsumerRecord <String,String> record:records)
            {
                System.out.println(record.value());
                int num = Integer.parseInt(record.value());
                if (num %2 ==0) {
                    try{
                        Class.forName("com.mysql.jdbc.Driver");
                        Connection con= DriverManager.getConnection("jdbc:mysql://localhost:3306/evenkafka","root","");
                        String sql="INSERT INTO `even`(`evennmbr`) VALUES (?)";
                        PreparedStatement stmt=con.prepareStatement((sql));
                        stmt.setInt(1,num);
                        stmt.executeUpdate();
                    }
                    catch (Exception e){
                        System.out.println((e));
                    }

                }
            }
        }
    }
}
