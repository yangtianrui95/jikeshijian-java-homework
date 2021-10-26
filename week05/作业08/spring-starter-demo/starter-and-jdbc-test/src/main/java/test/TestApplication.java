package test;

import com.example.starter.EnableGetStudent;
import com.example.starter.mybean.Klass;
import com.example.starter.mybean.Student;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@Slf4j
@SpringBootApplication
@EnableGetStudent
public class TestApplication {

    public static void main(String[] args) {
        final ApplicationContext application = SpringApplication.run(TestApplication.class, args);
        // klass默认配置的
        final Klass bean = application.getBean(Klass.class);
        final Student student = application.getBean(Student.class);
        log.info("app: {}\n, klass: {}\n, student: {}", application, bean, student);
    }
}
