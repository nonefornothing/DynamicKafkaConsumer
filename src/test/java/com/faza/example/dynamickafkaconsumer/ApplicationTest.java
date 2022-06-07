package com.faza.example.dynamickafkaconsumer;

import com.faza.example.dynamickafkaconsumer.model.CustomKafkaListenerProperty;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.ClassRule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Application.class)
@DirtiesContext
@Testcontainers
@AutoConfigureMockMvc
public class ApplicationTest {

    @ClassRule
    public static KafkaContainer kafka = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:latest"))
            .withEmbeddedZookeeper();

    @Autowired
    MockMvc mockMvc;

    private static AdminClient adminClient;





    @BeforeAll
    static void init(){
        kafka.start();
        var adminClientProps = new Properties();
        adminClientProps.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,kafka.getBootstrapServers());
        adminClient = AdminClient.create(adminClientProps);
    }

    @DynamicPropertySource
    public static void dynamicProperties(DynamicPropertyRegistry registry){
        registry.add("spring.kafka.bootstrap.servers",kafka::getBootstrapServers);
    }

    @Test
    public void testCreateSomeTopicIsGood() throws ExecutionException, InterruptedException {
            final var topicName = "a-topic";
            adminClient.createTopics(List.of(TopicBuilder.name(topicName).build()));
            var listTopicInBroker = adminClient.listTopics().names().get();

            Assertions.assertTrue(listTopicInBroker.contains(topicName));
    }

    @Test
    public void registry_create_should_create_new_consumer_for_specified_topic_on_broker() throws Exception {

        //Consumer and its topic
        var listDynamicConsumerToCreate = List.of(
                CustomKafkaListenerProperty.builder().consumerId("consumer-topic-2").topic("topic-2").listenerClass("MyCustomMessageListener").build(),
                CustomKafkaListenerProperty.builder().consumerId("consumer-topic-1").topic("topic-1").listenerClass("MyCustomMessageListener").build(),
                CustomKafkaListenerProperty.builder().consumerId("consumer-topic-3").topic("topic-3").listenerClass("MyCustomMessageListener").build()
        );

        //create topic-1,topic-2,topic-3 on broker
        adminClient.createTopics(listDynamicConsumerToCreate.stream().map(
                item-> TopicBuilder.name(item.getTopic()).build())
                .collect(Collectors.toList()));

        var listingOpts = new ListTopicsOptions();
        listingOpts.listInternal(true);

        for (var consumerToCreate : listDynamicConsumerToCreate) {
            mockMvc.perform(MockMvcRequestBuilders.post("/api/kafka/registry/create").contentType("application/json")
                            .queryParam("topic",consumerToCreate.getTopic())
                            .queryParam("consumerId",consumerToCreate.getConsumerId())
                    )
                    .andDo(print())
                    .andExpect(status().is2xxSuccessful())
                    .andDo((result)->{
                        //list semua groupd id di broker
                        var groupIds = adminClient.listConsumerGroups().all().get().stream().map(ConsumerGroupListing::groupId).collect(Collectors.toList());

                        var consumerGroupDesc  = adminClient.describeConsumerGroups(groupIds).all().get();

                        var consumerIds = groupIds.stream().flatMap(groupId->{
                            var desc = consumerGroupDesc.get(groupId);
                            return desc.members().stream().map(MemberDescription::consumerId);
                        }).collect(Collectors.toList());


                        //Assert that consumer is exist in the broker
                        Assertions.assertTrue(consumerIds.contains(consumerToCreate.getConsumerId()));

                    });
        }
    }

}
