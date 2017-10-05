/*
 * CS354: Operating Systems. 
 * Purdue University
 * Example that shows how to read one line with simple editing
 * using raw terminal.
 */

#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#define MAX_BUFFER_LINE 2048
#define INIT_HIS_SIZE 100
#define cursorforward(x) printf("\033[%dC", (x))
#define cursorbackward(x) printf("\033[%dD", (x))

// Buffer where line is stored
int line_length;;
char line_buffer[MAX_BUFFER_LINE];

// Simple history array
// This history does not change. 
// Yours have to be updated.
int history_index = 0;
int curr;
int pos = 0;
char **history;
int history_length = 0;
void init() {
	history = (char**)malloc(sizeof(char**)*100);
}

void read_line_print_usage()
{
  char * usage = "\n"
    " ctrl-?       Print usage\n"
    " Backspace    Deletes last character\n"
    " up arrow     See last command in the history\n";

  write(1, usage, strlen(usage));
}

/* 
 * Input a line with some basic editing.
 */
char * read_line() {

  // Set terminal in raw mode
  tty_raw_mode();

  line_length = 0;

  // Read one line until enter is typed
  while (1) {

    // Read one character in raw mode.
    char ch;
    //printf("%d",ch);
    read(0, &ch, 1);
    //printf("%c\n",ch);
    if (ch>=32) {
      // It is a printable character. 

      // Do echo

      // If max number of character reached return.
      if (line_length==MAX_BUFFER_LINE-2) break; 

      // add char to buffer.
      if (curr != line_length) {
	int i = line_length;
	while (i>curr) {
		line_buffer[i] = line_buffer[i-1];
		i--;
	}


      }
      line_buffer[curr]=ch;
      line_length++;
      //char towrite[line_length-curr];
      int i = 0;
      //int i = 0;
      for (i = curr;i < line_length;i++) {
	write(1,&line_buffer[i],1);	
      }
      curr++;
      for (i = curr;i < line_length;i++) {
	char tm = 8;
	write(1,&tm,1);
      }
      
    }
    else if (ch==10) {
      // <Enter> was typed. Return line
      
      // Print newline
      write(1,&ch,1);

      break;
    }
    else if (ch == 31) {
      // ctrl-?
      read_line_print_usage();
      line_buffer[0]=0;
      break;
    }
    else if (ch == 4) {
	//printf("%d\n",curr);
	if (!(curr >= line_length)) {
		ch =' ';
		write(1,&ch,1);
		int i = 0;
		line_length--;
	//	printf("%d\n",line_length);
		ch = 8;
		write(1,&ch,1);
		for (i = curr;i < line_length;i++) {
			line_buffer[i] = line_buffer[i+1];
			write(1,&line_buffer[i],1);
		}
		ch = ' ';
		write(1,&ch,1);
		ch = 8;
		for (i = line_length;i >= curr;i--) {
			write(1,&ch,1);
		}
	}
    }
    else if (ch == 1) {
		//home key
		int i = 0;
		for (i = 0;i < curr;i++) {
			ch = 8;
			write(1,&ch,1);
		}
		curr = 0;
    }
    else if (ch == 5) {
		//end key
		while(curr != line_length) {
			ch = line_buffer[curr];
			curr++;
			write(1,&ch,1);
		
		}
    }
    else if (ch == 8) {
      // <backspace> was typed. Remove previous character read.

      // Go back one character
      if (line_length>0) {
	ch = 8;
      	write(1,&ch,1);

      // Write a space to erase the last character read
      	ch = ' ';
      	write(1,&ch,1);

     // Go back one character
      	ch = 8;
      	write(1,&ch,1);
	curr--;
	if (curr != line_length) {
		int i = 0;
		for ( i = curr;i < line_length-1;i++) {
			line_buffer[i] = line_buffer[i+1];
			write(1,&line_buffer[i+1],1);
		}
		ch = ' ';
		write(1,&ch,1);
		for (i = curr;i < line_length;i++) {
			ch = 8;
			write(1,&ch,1);
		}
	}
      // Remove one character from buffer
        line_length--;
      	line_buffer[line_length] = '\0';
      }
    }
    else if (ch==27) {
      // Escape sequence. Read two chars more
      //
      // HINT: Use the program "keyboard-example" to
      // see the ascii code for the different chars typed.
      //
      char ch1; 
      char ch2;
      read(0, &ch1, 1);
      read(0, &ch2, 1);
      if (ch1==91 && ch2==65) {
	// Up arrow. Print next line in history.

	// Erase old line
	// Print backspaces
	if (history_index >= 0) {
		int i = 0;
	
		for (i =0; i < line_length; i++) {
	  		ch = 8;
	  		write(1,&ch,1);
		}

		// Print spaces on top
		for (i =0; i < line_length; i++) {
	  		ch = ' ';
	 		 write(1,&ch,1);
		}

		// Print backspaces
		for (i =0; i < line_length; i++) {
	  		ch = 8;
	  		write(1,&ch,1);
		}	

		// Copy line from history
		//printf("%s\n",history[0]);
		strcpy(line_buffer, history[history_index]);
		line_length = strlen(line_buffer);
		history_index--;
		if (history_index < 0) {
			history_index = 0;
		}
		//printf("%s,%d\n",line_buffer,line_length);
		write(1, line_buffer, line_length);
		curr = line_length;
     	}
      }
      else if (ch1 == 91 && ch2 ==66) {
		//down arraow,show the next command
		
		int i = 0;
	
		for (i =0; i < line_length; i++) {
	  		ch = 8;
	  		write(1,&ch,1);
		}

		// Print spaces on top
		for (i =0; i < line_length; i++) {
	  		ch = ' ';
	 	 	write(1,&ch,1);
		}

		// Print backspaces
		for (i =0; i < line_length; i++) {
 			ch = 8;
  			write(1,&ch,1);
		}	
		// Copy line from history
		//printf("%d\n",history_index);
		if (history_index < history_length-1) {
			history_index++;
			strcpy(line_buffer, history[history_index]);
			line_length = strlen(line_buffer);
			
			write(1, line_buffer, line_length);
			curr = line_length;
		}
		else {
			line_length = 0;
			curr = line_length;
		}
      }
      else if (ch1==91 && ch2==68) {
	//left arrow,move the cursor to the left
      	//cursorforward(1);
	if (line_length != 0 && curr > 0) {
		ch = 8;	
		write(1,&ch,1);
		curr--;
	}
      }
      else if (ch1==91 && ch2==67) {
	//right arrow,move the cursor to the right
	if(curr != line_length) {
		ch = line_buffer[curr];
		curr++;
		write(1,&ch,1);
		
	}
      }

      
    }

  }

  // Add eol and null char at the end of string
  //printf("%d,%d\n",line_length,curr);
  line_buffer[line_length]=10;
  line_length++;
  line_buffer[line_length]=0;
  //history[pos] = (char*)malloc(sizeof(char)*line_length);
  history[pos] = strndup(line_buffer,line_length-1);
  //printf("%s,%d,%d\n",history[pos],line_length,pos);
  pos++;
  history_length++;
  history_index = history_length - 1;
  curr = 0;
  return line_buffer;
}
void
freeAll() {
	int i = 0;
	for (i = 0;i < history_length;i++) {
		free(history[i]);
	}
	free(history);
}
void 
setLine() {
	line_length = 0;
	curr = 0;
}

