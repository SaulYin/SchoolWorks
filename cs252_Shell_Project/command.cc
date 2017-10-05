
/*
 * CS252: Shell project
 *
 * Template file.
 * You will need to add more code here to execute the command table.
 *
 * NOTE: You are responsible for fixing any bugs this code may have!
 *
 */

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <string.h>
#include <signal.h>
#include "command.h"
#include <sys/stat.h>
#include <fcntl.h>
#include <regex.h>
#include <pwd.h>
#include <termios.h>
extern char **environ;
extern "C" void init();
extern "C" void freeAll();
extern "C" void setLine();
pid_t shell_pgid;
struct termios shell_tmodes;
int shell_terminal;
int shell_is_interactive;
void
init_shell ()
{

  /* See if we are running interactively.  */
  shell_terminal = STDIN_FILENO;
  shell_is_interactive = isatty (shell_terminal);

  if (shell_is_interactive)
    {
      /* Loop until we are in the foreground.  */
      while (tcgetpgrp (shell_terminal) != (shell_pgid = getpgrp ()))
        kill (- shell_pgid, SIGTTIN);

      /* Ignore interactive and job-control signals.  */
      signal (SIGINT, SIG_IGN);
      signal (SIGQUIT, SIG_IGN);
      signal (SIGTSTP, SIG_IGN);
      signal (SIGTTIN, SIG_IGN);
      signal (SIGTTOU, SIG_IGN);
      signal (SIGCHLD, SIG_IGN);

      /* Put ourselves in our own process group.  */
      shell_pgid = getpid ();
      if (setpgid (shell_pgid, shell_pgid) < 0)
        {
          perror ("Couldn't put the shell in its own process group");
          exit (1);
        }

      /* Grab control of the terminal.  */
      tcsetpgrp (shell_terminal, shell_pgid);

      /* Save default terminal attributes for shell.  */
      tcgetattr (shell_terminal, &shell_tmodes);
    }
}
SimpleCommand::SimpleCommand()
{
	// Create available space for 5 arguments
	_numOfAvailableArguments = 5;
	_numOfArguments = 0;
	_arguments = (char **) malloc( _numOfAvailableArguments * sizeof( char * ) );
}

void
SimpleCommand::insertArgument( char * argument )
{
	//checking for expansion
	const char *buffer = "^.*${[^}][^}]*}.*$";
	buffer = (char*)buffer;
	 //compile
	regex_t re;
	int result = regcomp(&re,buffer,0);
	if (result != 0) {
		perror("compile");
		return;
	}
	regmatch_t match;
	char *newArg = new char[1024];
	int p = 0;//for newArg
	int q= 0;//for arg
	int i = 0;//for theStr
        //printf("%s\n",argument);
	if (regexec(&re,argument,1,&match,0) == 0) {
		//printf("%s\n",argument);
		while (argument[q] != '\0') {
			if(argument[q] != '$') {
				newArg[p] = argument[q];
				p++;
				q++;
			}
			else{
				i = 0;
				char *theStr = (char*)malloc(sizeof(char)*1024);
				q += 2;
				while (argument[q] != '}') {
					theStr[i] = argument[q];
					q++;
					i++;
				}
				theStr[i] = '\0';
				q++;
				if (!strcmp(theStr,"$")) {
					sprintf(theStr,"%d",getpid());
				}
				
				else {
					theStr = getenv(theStr);
					if (theStr == NULL) {
						p = 0;
						break;
					}
				}
				i = 0;
				while (theStr[i] != '\0') {
					newArg[p] = theStr[i];
					p++;
					i++;
				}
				//free(theStr);
			}
		}
		newArg[p] = '\0';
	}
	if (p > 0) {
		argument = strdup(newArg);
	}
	regfree(&re);
	delete[] newArg;
	//tilde
	char*temp = strdup(argument);
	//printf("%s\n",temp);
	char* startOfTilde = strchr(argument,'~');
	char* startOfSlash = strchr(argument,'/');
	if (startOfTilde) {
		free(temp);
		if (strlen(argument) == 1) {
			temp = strdup(getenv("HOME"));
		}
		else {
			char *temp2;
			if (startOfSlash) {
				i = 1;
				while(argument[i] != '/') i++;
				temp2 = (char*)malloc(sizeof(char)*(i-1));
				i = 1;
				while(argument[i] != '/') {
					temp2[i-1] = argument[i];
					i++;
				}
				temp2[i-1] = '\0';	
			}
			else {
				temp2=(char*)malloc(sizeof(char)*(strlen(argument)-1));
				i = 1;
				while (argument[i]) {
					temp2[i-1]=argument[i];
					i++;
				}
				temp2[i-1] = '\0';
			}
			if (getpwnam(temp2) == NULL) {
				printf("Bad directory name\n");
			        free(temp2);	
				return;
			}
			temp = strdup(getpwnam(temp2)->pw_dir);
			free(temp2);
		}
		if (startOfSlash) {
			argument = startOfSlash;
			//printf("%s\n",argument);
			temp = (char*)realloc(temp,sizeof(char)*(strlen(temp)+strlen(argument)));
			temp = strcat(temp,argument);
			argument = strdup(temp);
		}
		else {
			argument = strdup(temp);
		}
	
	}
	free(temp);
	//printf("%d,%d\n",_numOfAvailableArguments,_numOfArguments);
	if ( _numOfAvailableArguments == _numOfArguments  + 1 ) {
		// Double the available space
		_numOfAvailableArguments *= 2;
		_arguments = (char **) realloc( _arguments,
				  _numOfAvailableArguments * sizeof( char * ) );
	}
	
	_arguments[ _numOfArguments ] = argument;
	//printf("%s\n",argument);

	// Add NULL argument at the end
	_arguments[ _numOfArguments + 1] = NULL;
	
	_numOfArguments++;
}

Command::Command()
{
	// Create available space for one simple command
	_numOfAvailableSimpleCommands = 1;
	_simpleCommands = (SimpleCommand **)
		malloc( 1 * sizeof( SimpleCommand * ) );

	_numOfSimpleCommands = 0;
	_outFile = 0;
	_inFile = 0;
	_errFile = 0;
	_background = 0;
	_append = 0;
}

void
Command::insertSimpleCommand( SimpleCommand * simpleCommand )
{
	//if (_simpleCommands == NULL) {
	//	_simpleCommands = (SimpleCommand**)malloc(1*sizeof(SimpleCommand*));
	//}
	if ( _numOfAvailableSimpleCommands == _numOfSimpleCommands ) {
		_numOfAvailableSimpleCommands *= 2;
		_simpleCommands = (SimpleCommand **) realloc( _simpleCommands,
			 _numOfAvailableSimpleCommands * sizeof( SimpleCommand * ) );
	}
	_simpleCommands[ _numOfSimpleCommands ] = simpleCommand;
	_numOfSimpleCommands++;
}

void
Command:: clear()
{
	for ( int i = 0; i < _numOfSimpleCommands; i++ ) {
		for ( int j = 0; j < _simpleCommands[ i ]->_numOfArguments; j ++ ) {
			//printf("%d.%d,%s\n",i,j, _simpleCommands[ i ]->_arguments[ j ] );
			free ( _simpleCommands[ i ]->_arguments[ j ] );
		}
		
		free ( _simpleCommands[ i ]->_arguments );
		delete ( _simpleCommands[ i ] );
	}
//	free(_simpleCommands);
        if (_outFile || _errFile) {
            if (_outFile && _errFile) {
                if (_outFile == _errFile) {
                   free(_outFile);
		}
		else {
                   free(_outFile);
		   free(_errFile);
		}
	    }
	    else if (_outFile) {
                free(_outFile);
	    }
	    else if (_errFile) {
                free(_errFile);
	    }
	}
	if ( _inFile ) {
		free( _inFile );
	}

	_numOfSimpleCommands = 0;
	_outFile = 0;
	_inFile = 0;
	_errFile = 0;
	_background = 0;
	_append = 0;
}

void
Command::print()
{
	printf("\n\n");
	printf("              COMMAND TABLE                \n");
	printf("\n");
	printf("  #   Simple Commands\n");
	printf("  --- ----------------------------------------------------------\n");
	
	for ( int i = 0; i < _numOfSimpleCommands; i++ ) {
		printf("  %-3d ", i );
		for ( int j = 0; j < _simpleCommands[i]->_numOfArguments; j++ ) {
			printf("\"%s\" \t", _simpleCommands[i]->_arguments[ j ] );
		}
	}

	printf( "\n\n" );
	printf( "  Output       Input        Error        Background\n" );
	printf( "  ------------ ------------ ------------ ------------\n" );
	printf( "  %-12s %-12s %-12s %-12s\n", _outFile?_outFile:"default",
		_inFile?_inFile:"default", _errFile?_errFile:"default",
		_background?"YES":"NO");
	printf( "\n\n" );
	
}
enum cmds {
        myExit,
	myCd,
	mySetenv,
	myUnsetenv,
	myJobs,
	myFg,
	myBg,
	mySource,
	myTouch,
	mySilent,
};
cmds find(char* inStr) {
        if (strcmp(inStr,"exit")==0) return myExit;
	if (strcmp(inStr,"silent_exit")==0) return mySilent;
	if (strcmp(inStr,"cd")==0) return myCd;
	if (strcmp(inStr,"setenv")==0) return mySetenv;
	if (strcmp(inStr,"unsetenv")==0) return myUnsetenv;
	if (strcmp(inStr,"jobs")==0) return myJobs;
	if (strcmp(inStr,"fg")==0) return myFg;
	if (strcmp(inStr,"bg")==0) return myBg;
	if (strcmp(inStr,"source") == 0) return mySource;
	if (strcmp(inStr,"touch")==0) return myTouch;
}
void
Command::execute()
{
	// Don't do anything if there are no simple commands
	if ( _numOfSimpleCommands == 0 ) {
		prompt();
		return;
	}

	// Print contents of Command data structure
//	print();

	// Add execution here
	// For every simple command fork a new process
	// Setup i/o redirection
	// and call exec
	
	//save stdin/stdout/stderr
	int tempin = dup(0);
	int tempout = dup(1);
	int temperr = dup(2);
	//save the initial input
	int fdin;
	int ferr;
	if (_inFile) {
           fdin = open(_inFile,O_RDONLY);
	} 
	else {
           fdin = dup(tempin);
	}
	if (_errFile) {
	  if (_append) {
            ferr = open(_errFile,O_CREAT|O_RDWR|O_APPEND,0644);
	  }
	  else {
            ferr = open(_errFile,O_CREAT|O_RDWR|O_TRUNC,0644);
	  }
	}
	else {
          ferr=dup(temperr);
	}
        int ret;
	int fdout=1;
	char* temp = _simpleCommands[0]->_arguments[0];
	
	//printf("we got command %s with %d simcom\n",temp,_numOfSimpleCommands);	
	switch(find(temp)) {
	   case myExit:{
		struct termios tty_attr;
		tcgetattr(0,&tty_attr);
		tty_attr.c_lflag |= (ICANON|ECHO);

        	tcsetattr(0,TCSAFLUSH,&tty_attr);
		printf("\n Good bye!!\n\n");
		freeAll();
		dup2(tempin,0);
		dup2(tempout,1);
		dup2(temperr,2);
		//fflush(stdout);
		close(tempin);
		close(tempout);
		close(temperr);
		close(fdin);
		close(fdout);
		close(ferr);
		fflush(stdout);
		clear();
		exit(1);
		       }break;
           case mySilent:{
	   	struct termios tty_attr;
		tcgetattr(0,&tty_attr);
		tty_attr.c_lflag |= (ICANON|ECHO);

		tcsetattr(0,TCSAFLUSH,&tty_attr);
		freeAll();
		//dup2(tempin,0);
		//dup2(tempout,1);
		//dup2(temperr,2);
		close(tempin);
		close(tempout);
		close(temperr);
		close(fdin);
		close(fdout);
		close(ferr);
		fflush(stdout);						  
		_exit(1);

	   }break;
	   case mySource:{
		FILE *f = fopen(_simpleCommands[0]->_arguments[1],"r");
		char line[256];
		int i = 0;
		while (fgets(line,sizeof(line),f)) {
			  //printf("%s\n",line);

			if (strchr(line,'#')) continue;
			char *temp1= strtok(line,"\n");

			//char*temp1 = strtok(line," ");
			//printf("%s\n",line);
			Command *thisOne = new Command();
			SimpleCommand *sc = new SimpleCommand();
			if (strchr(temp1,'=')) {
				    char*arg1= (char*)malloc(sizeof(char)*7);
				    strcpy(arg1,"setenv");
                                    sc->insertArgument(arg1);
				    char*t = strtok(temp1,"=");
					char *arg2 = (char*)malloc(strlen(t)+3*sizeof(char));
				    //printf("%s\n",temp1);
				    if (strchr(t,'$')) {
					int i = 0;
					int j = 0;
					//todo: split $
				    }
				    sc->insertArgument(arg2);
				    //printf("%s\n",arg2);
				    char *arg3 = strdup(strtok(NULL,"="));
				    sc->insertArgument(arg3);
				    //printf("%s\n",arg3);
                        }
			else {
				temp1 = strtok(temp1," ");
				while (!(temp1 == NULL || temp1 == ""))  {
			        	//printf("%s\n",temp);	
					char*toput = strdup(temp1);
					//printf("%s\n",toput);
					sc->insertArgument(toput);
					//printf("%s\n",temp);
					
					temp1 = strtok(NULL," ");
				}
			}
			thisOne->insertSimpleCommand(sc);
			//printf("%s\n",sc->_arguments[0]);
			thisOne->execute();
		}
		fclose(f);
		//char command[1024]="chmod u+x source.sh";
		//system(command);
		//sprintf(command,"./source.sh %s",_simpleCommands[0]->_arguments[1]);
		//execute();
		clear();
		prompt();
		return;
 		
	   }break;
	   case myTouch: {
		int i = 1;
		while (_simpleCommands[0]->_arguments[i]) {
			fopen(_simpleCommands[0]->_arguments[i],"w");
			i++;
		}
		
		clear();
		prompt();
		return;
	   }break;
	   case myCd: {
		if (_simpleCommands[0]->_arguments[1] == NULL) {
		   chdir(getenv("HOME"));
		}
		else {
			if (chdir(_simpleCommands[0]->_arguments[1]) == -1){
				const char *mes = "No such file or directory\n";
				write(ferr,(char*)mes,strlen(mes));
				//printf("No such file or directory");
				return;
				
			}
		}
	        clear();
		prompt();
		return;
		      }break;
	    case mySetenv:{	
		char *envname = _simpleCommands[0]->_arguments[1];
		char *envval = _simpleCommands[0]->_arguments[2];
		char *overwrite = _simpleCommands[0]->_arguments[3];
		int len = strlen(envname) + strlen(envval);
		len++;
		char result[len];
		sprintf(result,"%s=%s",envname,envval);
		//printf("got it\n");
		char *str = strdup(result);
		putenv(str);
		clear();
		prompt();
		return;} break;
	    case myUnsetenv:{
		char *envname = _simpleCommands[0]->_arguments[1];
		int i = 0;
		
		while (environ[i] != NULL) {
			//printf("%s\n",environ[i]);
			if (strstr(environ[i],envname)) {
				int j = i+1;
				int z = i;
				while (environ[j] != NULL){
					environ[z] = strdup(environ[j]);
					j++;
					z++;
				}
				environ[z] = NULL;

			}
			i++;
		}
		clear();
		prompt();
		return;
	        }break;
	}

	for (int i = 0;i < _numOfSimpleCommands;i++) {
            //redirect input
	    dup2(fdin,0);
	    close(fdin);
	    //setup output
	    if (i == _numOfSimpleCommands-1) {
               //Last simple command
	       if (_outFile) {
	          if (_append == 1) {
	             fdout=open(_outFile,O_CREAT|O_RDWR|O_APPEND,0644);	    
		  }
		  else {
                     fdout=open(_outFile,O_CREAT|O_RDWR|O_TRUNC,0644);
	          }
	       }
	       else {
                  //use default output
		  fdout=dup(tempout);
	       }
	       if (_errFile) {
                 if (_append == 1) {
                   ferr = open(_errFile,O_CREAT|O_RDWR|O_APPEND,0644);
		 }
		 else {
 		   ferr=open(_errFile,O_CREAT|O_RDWR|O_TRUNC,0644);
           	 }
	       }
	       else {
                 ferr=dup(temperr);
	       }
	    }
	    else {
               //not last simple command, create pipe
	       int fdpipe[2];
	       pipe(fdpipe);
	       fdout=fdpipe[1];
	       fdin=fdpipe[0];
	    }
	    //redirect output
	    dup2(fdout,1);
	    close(fdout);
	    //redirect err
	    dup2(ferr,2);
	    close(ferr);
	    //create child process
	    ret = fork();
	    if (ret == 0) {
               //child
	       if (!strcmp(_simpleCommands[i]->_arguments[0],"printenv")) {
			char **p = environ;
			while (*p != NULL) {
				printf("%s\n",*p);
				p++;
			}
			exit(0);
	       }
	       //int tf = open("./tf.txt",O_CREAT|O_TRUNC);
	       //char *towrite;
	       //sprintf(towrite,"this is the command:%s\n",_simpleCommands[i]->_arguments[0]);
	       //write(tf,towrite,strlen(towrite));
	       
	       execvp(_simpleCommands[i]->_arguments[0],_simpleCommands[i]->_arguments);
	       //printf("this is the command:%s\n",_simpleCommands[i]->_arguments[0]);
		perror("execvp");
	       _exit(1);
	    }
	    else if (ret < 0) {
               perror("fork");
	       //clear();
               //prompt();
	       return;
	    }
	    //parent shell continue
	}
	//restore in/out defaults
	dup2(tempin,0);
	dup2(tempout,1);
        dup2(temperr,2);
	close(ferr);
	close(fdout);
	close(fdin);
	close(tempin);
	close(tempout);
 	close(temperr);
	if (!_background) {
           //wait for last process
	   waitpid(ret,NULL,0);
           //clear();
           //prompt();
	}//execute
	// Clear to prepare for next command
	clear();
	prompt();
   
}
// Shell implementation
extern "C" void disp(int sig) {
	fprintf(stderr,"\n");
	setLine();
	Command::_currentCommand.clear();
	Command::_currentCommand.prompt();
}
void 
killzombie(int sig) {
	while(waitpid(-1,NULL,WNOHANG)>0);
}
void
Command::prompt()
{
	if (isatty(0)){
  		printf("myshell>");
		fflush(stdout);
	}
}

Command Command::_currentCommand;
SimpleCommand * Command::_currentSimpleCommand;

int yyparse(void);
//extern yy_buffer_state;
//typedef yy_buffer_state *YY_BUFFER_STATE;
//extern YY_BUFFER_SATE yy_scan_buffer(char*,size_t);
main()
{
	//init_shell();
	struct sigaction sa,sa1;
	sa.sa_handler = disp;
	sa1.sa_handler = killzombie;
	sigemptyset(&sa.sa_mask);
	sigemptyset(&sa1.sa_mask);
 	sa.sa_flags = SA_RESTART;
	sa1.sa_flags = SA_RESTART;
	if (sigaction(SIGINT,&sa,NULL) || sigaction(SIGCHLD,&sa1,NULL)) {
		perror("sigaction");
		exit(-1);
	}
	//Command::_currentCommand.clear();
	//yy_scan_buffer(const char *str)
	Command::_currentCommand.prompt();
 	init();
	yyparse();
		
}

