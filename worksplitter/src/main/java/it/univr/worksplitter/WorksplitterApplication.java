package it.univr.worksplitter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@SpringBootApplication
@RestController
public class WorksplitterApplication {

	public static void main(String[] args) {
		SpringApplication.run(WorksplitterApplication.class, args);
	}

	/**
	 * Rispondi alle richieste HTTP su / con il messaggio "OK"
	 */
	@GetMapping("/")
	public String index(){
		return "OK";
	}
}
