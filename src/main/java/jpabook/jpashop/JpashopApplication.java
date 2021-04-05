package jpabook.jpashop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JpashopApplication {

	public static void main(String[] args) {
		Hello hello = new Hello();
		hello.setHello("hello hi");
		System.out.println(hello.getHello());
		SpringApplication.run(JpashopApplication.class, args);
	}

}
