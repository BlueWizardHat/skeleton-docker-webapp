package net.bluewizardhat.dockerwebapp.domain.logic;

import org.springframework.stereotype.Component;

@Component
public class AddingService {
	public int add(int a, int b) {
		return a + b;
	}
}
