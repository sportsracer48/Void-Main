package computer.program;

import state.programming.AppendOnlyBuffer;

import computer.system.Computer;

public class Help implements WriteOnlyExecutable
{
	public void run(String[] args, AppendOnlyBuffer out, Computer system)
	{
		out.appendln("CONSOLE COMMANDS:\n"
				   + "cons          : repeatedly echo from prompt\n"
				   + "echo [arg]    : console repeats arg at you\n"
				   + "exit          : return to workbench\n"
				   + "help          : display this message\n"
				   + "              : aliases: ?, info\n"
				   + "python        : open python prompt\n"
				   + "upload [file] : upload file to connected microcontroller\n"
				   + "vi [file]     : edit file, or create new file and begin editing. Note: not actually vi or vim. See editor controls\n"
				   + "\n"
				   + "CONSOLE CONTROLS:\n"
				   + "page up / page down / scroll wheel : scroll console\n"
				   + "end                                : terminate running executable\n"
				   + "esc                                : exit console, return to workbench\n"
				   + "\n"
				   + "EDITOR CONTROLS:\n"
				   + "up / down / left / right           : move cursor\n"
				   + "ctrl+a                             : select all\n"
				   + "ctrl+c                             : copy\n"
				   + "ctrl+v                             : paste\n"
				   + "ctrl+x                             : cut\n"
				   + "ctrl+s                             : save\n"
				   + "\n"
				   + "WORKSPACE CONTROLS:\n"
				   + "wasd                               : move camera\n"
				   + "+ / -                              : zoom camera\n"
				   + "q                                  : edit mode\n"
				   + "e                                  : wiring mode\n"
				   + "r                                  : programming mode\n"
				   + "esc                                : return to edit mode, quit game if in edit mode\n"
				   + "left click:\n"
				   + "            (held) pick up / put down item (edit mode)\n"
				   + "            open wiring ui on item (wiring mode)\n"
				   + "            place / pick up wire end (wiring ui)\n"
				   + "            connect to laptop and enter console (programming mode)\n"
				   + "right click:\n"
				   + "            delete wire (wiring ui)\n");
	}
}
