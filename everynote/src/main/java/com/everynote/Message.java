package com.everynote;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.util.List;

import org.jooby.mvc.GET;
import org.jooby.mvc.Path;

import com.evernote.auth.EvernoteAuth;
import com.evernote.auth.EvernoteService;
import com.evernote.clients.ClientFactory;
import com.evernote.clients.NoteStoreClient;
import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.type.Data;
import com.evernote.edam.type.Notebook;
import com.evernote.edam.type.Resource;
import com.evernote.edam.type.ResourceAttributes;
import com.evernote.thrift.TException;

@Path("/message")
public class Message {

	private static final String dev_token = "S=s1:U=93538:E=1614664b97f:C=159eeb38ac8:P=1cd:A=en-devtoken:V=2:H=47816343df8fe4c138b10f1948cb448d";

	NoteStoreClient noteStore = null;

	@GET
	@Path("/")
	public String salute() throws EDAMUserException, EDAMSystemException, EDAMNotFoundException, TException {

		// Set up the NoteStore client
		EvernoteAuth evernoteAuth = new EvernoteAuth(EvernoteService.SANDBOX, dev_token);
		ClientFactory factory = new ClientFactory(evernoteAuth);

		// Make API calls, passing the developer token as the
		// authenticationToken param
		List<Notebook> notebooks = null;

		try {

			noteStore = factory.createNoteStoreClient();

			createNote();

		} catch (EDAMUserException | EDAMSystemException | TException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "hey jooby";
	}

	/**
	 * Create a new note containing a little text and the Evernote icon.
	 */
	private void createNote() throws Exception {
		// To create a new note, simply create a new Note object and fill in
		// attributes such as the note's title.

		com.evernote.edam.type.Note note = new com.evernote.edam.type.Note();
		note.setTitle("Test note from EDAMDemo.java");

		String fileName = "/everynote/src/main/java/com/everynote/enlogo.png";
		String mimeType = "text/plain";

		// To include an attachment such as an image in a note, first create a
		// Resource
		// for the attachment. At a minimum, the Resource contains the binary
		// attachment
		// data, an MD5 hash of the binary data, and the attachment MIME type.
		// It can also
		// include attributes such as filename and location.
		Resource resource = new Resource();

		resource.setData(readFileAsData(fileName));
		resource.setMime(mimeType);
		ResourceAttributes attributes = new ResourceAttributes();
		attributes.setFileName(fileName);
		resource.setAttributes(attributes);

		// Now, add the new Resource to the note's list of resources
		note.addToResources(resource);

		// To display the Resource as part of the note's content, include an
		// <en-media>
		// tag in the note's ENML content. The en-media tag identifies the
		// corresponding
		// Resource using the MD5 hash.
		String hashHex = bytesToHex(resource.getData().getBodyHash());

		// The content of an Evernote note is represented using Evernote Markup
		// Language
		// (ENML). The full ENML specification can be found in the Evernote API
		// Overview
		// at http://dev.evernote.com/documentation/cloud/chapters/ENML.php
		String content = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<!DOCTYPE en-note SYSTEM \"http://xml.evernote.com/pub/enml2.dtd\">" + "<en-note>"
				+ "<span style=\"color:green;\">Here's the Evernote logo:</span><br/>"
				+ "<en-media type=\"text/plain\" hash=\"" + hashHex + "\"/>" + "</en-note>";
		note.setContent(content);

		// Finally, send the new note to Evernote using the createNote method
		// The new Note object that is returned will contain server-generated
		// attributes such as the new note's unique GUID.
		com.evernote.edam.type.Note createdNote = noteStore.createNote(note);
		String newNoteGuid = createdNote.getGuid();

		System.out.println("Successfully created a new note with GUID: " + newNoteGuid);
		System.out.println();
	}

	/**
	 * Helper method to convert a byte array to a hexadecimal string.
	 */
	public static String bytesToHex(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (byte hashByte : bytes) {
			int intVal = 0xff & hashByte;
			if (intVal < 0x10) {
				sb.append('0');
			}
			sb.append(Integer.toHexString(intVal));
		}
		return sb.toString();
	}

	/**
	 * Helper method to read the contents of a file on disk and create a new
	 * Data object.
	 */
	private static Data readFileAsData(String fileName) throws Exception {

		String filePath = fileName;

		System.out.println(filePath);
		// Read the full binary contents of the file
		FileInputStream in = new FileInputStream(
				"/home/docnme/Dev/repository/everynote/src/main/java/com/everynote/sample.txt");
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		byte[] block = new byte[10240];
		int len;
		while ((len = in.read(block)) >= 0) {
			byteOut.write(block, 0, len);
		}
		in.close();
		byte[] body = byteOut.toByteArray();

		// Create a new Data object to contain the file contents
		Data data = new Data();
		data.setSize(body.length);
		data.setBodyHash(MessageDigest.getInstance("MD5").digest(body));
		data.setBody(body);

		return data;
	}

}
