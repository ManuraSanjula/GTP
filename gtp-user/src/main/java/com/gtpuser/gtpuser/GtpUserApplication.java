package com.gtpuser.gtpuser;

import com.gtpuser.gtpuser.repo.*;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.kafka.config.TopicBuilder;

@SpringBootApplication(scanBasePackages={
		"com.gtpuser.gtpuser","com.gtpuser.gtpuser.GtpUserApplication"})
@EnableReactiveMongoRepositories(basePackageClasses  = {
		UserRepo.class, UserForReqRepo.class, FriendReqRepo.class, FriendsRepo.class, FansRepo.class
})
@Configuration
public class GtpUserApplication {

	public static void main(String[] args) {
		SpringApplication.run(GtpUserApplication.class, args);
	}
	@Bean
	public NewTopic topicUserCreated() {
		return TopicBuilder.name("user-created")
				.partitions(3)
				.replicas(3)
				.build();
	}

}
