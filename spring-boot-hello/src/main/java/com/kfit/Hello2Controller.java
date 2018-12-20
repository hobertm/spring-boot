package com.kfit;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Hello2Controller {

	
	@RequestMapping("/helloNew")
	public String helloNew(){
		return "helloNew";
	}

    @RequestMapping("/helloNew2")
    public String helloNew2(){
        return "helloNew";
    }
	
}
