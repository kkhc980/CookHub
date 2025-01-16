package com.dishcovery.project.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

// root-context.xml과 동일
@Configuration
@EnableTransactionManagement // 트랜잭션 어노테이션 활성화
@EnableScheduling
@ComponentScan(basePackages = {"com.dishcovery.project"})
@MapperScan(basePackages = {"com.dishcovery.project.persistence"})
public class RootConfig {

    @Bean // 스프링 bean으로 설정
    public DataSource dataSource() { // DataSource 객체 리턴 메서드
        HikariConfig config = new HikariConfig(); // 설정 객체
        config.setDriverClassName("oracle.jdbc.OracleDriver"); // jdbc 드라이버 정보
        config.setJdbcUrl("jdbc:oracle:thin:@192.168.0.139:1521:xe"); // DB 연결 url
        config.setUsername("cookhub"); // DB 사용자 아이디
        config.setPassword("1234"); // DB 사용자 비밀번호
        
        config.setMaximumPoolSize(10); // 최대 풀(Pool) 크기 설정
        config.setConnectionTimeout(30000); // Connection 타임 아웃 설정(30초)
        HikariDataSource ds = new HikariDataSource(config); // config 객체를 참조하여 DataSource 객체 생성
        return ds; // ds 객체 리턴
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception { 
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource());
        
        // MyBatis Mapper XML 위치 설정
        sqlSessionFactoryBean.setMapperLocations(
            new PathMatchingResourcePatternResolver().getResources("classpath:com/dishcovery/project/persistence/HashtagMapper.xml")
        );

        // MyBatis 설정 추가 (underscore to camel case)
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setMapUnderscoreToCamelCase(true); // snake_case -> camelCase 자동 매핑 활성화
        sqlSessionFactoryBean.setConfiguration(configuration);
        
        return (SqlSessionFactory) sqlSessionFactoryBean.getObject();
    }
    
    @Bean
    public DataSourceTransactionManager transactionManager() { // 트랜잭션 매니저 Bean
        return new DataSourceTransactionManager(dataSource());
    }

    @Bean
    public JavaMailSender getJavaMailSender() {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", true);
        properties.put("mail.transport.protocol", "smtp");
        properties.put("mail.smtp.starttls.enable", true);
        properties.put("mail.smtp.starttls.required", true);
        properties.put("mail.debug", true);

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername("ksh71192@gmail.com");
        mailSender.setPassword("fjeofjynknzvwaid");
        mailSender.setDefaultEncoding("utf-8");
        mailSender.setJavaMailProperties(properties);

        return mailSender;
    }
}
