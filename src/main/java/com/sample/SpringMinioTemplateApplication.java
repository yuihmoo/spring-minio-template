package com.sample;

import com.sample.service.MinioBucketService;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class SpringMinioTemplateApplication {

    public static void main(String[] args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(SpringMinioTemplateApplication.class);
        builder.headless(false);
        ConfigurableApplicationContext context = builder.run(args);

//        MinioBucketService minioBucketService = context.getBean(MinioBucketService.class);
//        minioBucketService.createBucket("default");
//        minioBucketService.setWebHookNotificationOfBucket("default");
    }
}
//    /**
//     * Minio 서버 초기화 시 동작 시켜야 할 코드가 있다면 스프링 프레임워크에서 Bean을 이용한다.
//     * @param args
//     */
//    public static void main(String[] args) {
//        SpringApplicationBuilder builder = new SpringApplicationBuilder(SocialVentureServerApplication.class);
//        builder.headless(false);
//        ConfigurableApplicationContext context = builder.run(args);
//
//        UserService userService = context.getBean(UserService.class);
//    }
//}
