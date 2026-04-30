package com.jobportal.config;

import com.jobportal.entity.Role;
import com.jobportal.entity.User;
import com.jobportal.repository.UserRepository;
import com.jobportal.entity.Job;
import com.jobportal.repository.JobRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UserRepository userRepository, JobRepository jobRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            User admin = userRepository.findByEmail("admin@example.com").orElseGet(() -> {
                User a = User.builder()
                        .name("Admin User")
                        .email("admin@example.com")
                        .password(passwordEncoder.encode("admin123"))
                        .role(Role.ROLE_ADMIN)
                        .build();
                return userRepository.save(a);
            });

            User employer = userRepository.findByEmail("employer@gmail.com").orElseGet(() -> {
                User e = User.builder()
                        .name("employer")
                        .email("employer@gmail.com")
                        .password(passwordEncoder.encode("employer"))
                        .role(Role.ROLE_EMPLOYER)
                        .build();
                return userRepository.save(e);
            });

            User user = userRepository.findByEmail("user@gmail.com").orElseGet(() -> {
                User u = User.builder()
                        .name("user")
                        .email("user@gmail.com")
                        .password(passwordEncoder.encode("user@123"))
                        .role(Role.ROLE_USER)
                        .resumeUrl("/uploads/resumes/dummy.pdf")
                        .build();
                return userRepository.save(u);
            });

            if (jobRepository.count() == 0) {
                Job[] jobs = {
                    Job.builder().title("Senior Java Developer").description("Design and develop scalable Java applications.").skills("Java, Spring Boot, Microservices").salary(120000.0).location("New York, NY").employer(employer).build(),
                    Job.builder().title("Frontend Engineer").description("Build responsive UIs with React.").skills("React, JavaScript, CSS").salary(105000.0).location("San Francisco, CA").employer(employer).build(),
                    Job.builder().title("DevOps Engineer").description("Manage cloud infrastructure and CI/CD pipelines.").skills("AWS, Docker, Kubernetes").salary(115000.0).location("Remote").employer(employer).build(),
                    Job.builder().title("Data Scientist").description("Analyze large datasets and build ML models.").skills("Python, Machine Learning, SQL").salary(130000.0).location("Boston, MA").employer(employer).build(),
                    Job.builder().title("Backend Developer").description("Develop robust APIs and backend services.").skills("Node.js, Express, MongoDB").salary(95000.0).location("Austin, TX").employer(employer).build(),
                    Job.builder().title("Full Stack Developer").description("End-to-end web application development.").skills("Vue.js, Django, PostgreSQL").salary(110000.0).location("Chicago, IL").employer(employer).build(),
                    Job.builder().title("Product Manager").description("Lead product strategy and execution.").skills("Agile, Scrum, Strategy").salary(140000.0).location("Seattle, WA").employer(employer).build(),
                    Job.builder().title("QA Tester").description("Ensure software quality through automated testing.").skills("Selenium, JUnit, Testing").salary(85000.0).location("Remote").employer(employer).build(),
                    Job.builder().title("UI/UX Designer").description("Create intuitive and beautiful user interfaces.").skills("Figma, Sketch, Adobe XD").salary(90000.0).location("Los Angeles, CA").employer(employer).build(),
                    Job.builder().title("Database Administrator").description("Optimize and maintain database systems.").skills("Oracle, MySQL, Performance Tuning").salary(100000.0).location("Dallas, TX").employer(employer).build()
                };
                for (Job j : jobs) {
                    jobRepository.save(j);
                }
            }
        };
    }
}
