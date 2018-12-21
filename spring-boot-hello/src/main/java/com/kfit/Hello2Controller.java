package com.kfit;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Hello2Controller {

	
	@RequestMapping("/helloNew5")
	public String helloNew5(){
		return "helloNew";
	}

    @RequestMapping("/helloNew3")
    public String helloNew3(){
        return "helloNew";
    }
	
}
