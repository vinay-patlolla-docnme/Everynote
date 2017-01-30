package com.everynote;

import org.jooby.mvc.GET;
import org.jooby.mvc.Path;

@Path("/note")
public class Note {

	@GET
	@Path("/add")
	public String addNewNote() {

		return "hey jooby";
	}

}
