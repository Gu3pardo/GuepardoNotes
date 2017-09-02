package guepardoapps.mynote.controller;

import java.util.ArrayList;

import android.content.Context;
import android.support.annotation.NonNull;

import guepardoapps.mynote.database.Database;
import guepardoapps.mynote.model.Note;

public class DatabaseController {
    // private static final String TAG = DatabaseController.class.getSimpleName();

    private boolean _initialized;
    private Context _context;

    private static final DatabaseController DATABASE_CONTROLLER_SINGLETON = new DatabaseController();
    private static Database _database;

    private DatabaseController() {
    }

    public static DatabaseController getInstance() {
        return DATABASE_CONTROLLER_SINGLETON;
    }

    public boolean Initialize(@NonNull Context context) {
        if (_initialized) {
            return false;
        }

        _context = context;
        _database = new Database(_context);
        _database.Open();

        _initialized = true;

        return true;
    }

    public ArrayList<Note> GetNotes() {
        if (!_initialized) {
            return new ArrayList<>();
        }

        return _database.GetNotes();
    }

    public boolean SaveNote(@NonNull Note newNote) {
        if (!_initialized) {
            return false;
        }

        long result = _database.CreateEntry(newNote);
        return result != -1;
    }

    public boolean UpdateNote(@NonNull Note updateNote) {
        if (!_initialized) {
            return false;
        }

        return _database.Update(updateNote);
    }

    public boolean DeleteNote(@NonNull Note deleteNote) {
        if (!_initialized) {
            return false;
        }

        return _database.Delete(deleteNote);
    }

    public void Dispose() {
        if (_database != null) {
            _database.Close();
        }

        _database = null;
        _context = null;
        _initialized = false;
    }
}
