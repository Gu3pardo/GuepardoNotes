package guepardoapps.guepardonotes.controller;

import java.util.ArrayList;

import android.content.Context;

import guepardoapps.guepardonotes.database.Database;
import guepardoapps.guepardonotes.model.Note;

public class DatabaseController {

	private Context _context;
	private static Database _database;

	public DatabaseController(Context context) {
		_context = context;
		_database = new Database(_context);
	}

	public ArrayList<Note> GetNotes() {
		_database.Open();
		ArrayList<Note> notes = _database.GetNotes();
		_database.Close();

		return notes;
	}

	public void SaveNote(Note newNote) {
		_database.Open();
		_database.CreateEntry(newNote);
		_database.Close();
	}

	public void UpdateNote(Note updateNote) {
		_database.Open();
		_database.Update(updateNote);
		_database.Close();
	}

	public void DeleteNote(Note deleteNote) {
		_database.Open();
		_database.Delete(deleteNote);
		_database.Close();
	}

	public void ClearNotes() {
		_database.Open();
		_database.Close();
		_database.Remove();
	}
}
