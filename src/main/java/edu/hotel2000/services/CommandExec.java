package edu.hotel2000.services;

public interface CommandExec{
	void evalCommand(String[] command) throws Exception;

	void onStart();
}
