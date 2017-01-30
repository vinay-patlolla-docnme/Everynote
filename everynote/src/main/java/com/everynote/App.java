package com.everynote;

import org.jooby.Jooby;

/**
 * @author jooby generator
 */
public class App extends Jooby {

	{
		get("/", () -> "Hello World!");
		use(Message.class);
	}

	public static void main(final String[] args) {
		run(App::new, args);
	}

}
