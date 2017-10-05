
/*
 * CS-252
 * shell.y: parser for shell
 *
 * This parser compiles the following grammar:
 *
 *	cmd [arg]* [> filename]
 *
 * you must extend it to understand the complete shell grammar
 *
 */

%token	<string_val> WORD

%token 	NOTOKEN GREAT NEWLINE GREATGREAT PIPE AMPERSAND GREATGREATAMPERSAND GREATAMPERSAND LESS

%union	{
		char   *string_val;
	}

%{
//#define yylex yylex
#include <stdio.h>
#include "command.h"
#include <cstring>
#include <regex.h>
#include <dirent.h>
#include <assert.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>
#define MAXFILENAME 1024
int maxEntries = 20;
int nEntries = 0;
char ** array;

void yyerror(const char * s);
int yylex();
void expandWildcardsIfNecessary(char *prefix,char * arg);
int
compar(const void *a,const void *b) {
     const char **first = (const char**)a;
     const char **secon = (const char**)b;
     return strcmp(*first,*secon);
}
int 
is_regular_file(const char *path) {
	struct stat path_stat;
	stat(path,&path_stat);
	return S_ISREG(path_stat.st_mode);
}
%}

%%

goal:	
	commands
	;

commands: 
	command
	| commands command 
	;

command: simple_command
        ;

simple_command:	
	pipe_list iomodifier_list background_opt NEWLINE {
		/*printf("   Yacc: Execute command\n");*/
		Command::_currentCommand.execute();
	}
	| NEWLINE 
	| error NEWLINE { yyerrok; }
	;
command_and_args:
	command_word argument_list {
		Command::_currentCommand.
			insertSimpleCommand( Command::_currentSimpleCommand );
	}
	;

argument_list:
	argument_list argument
	| /* can be empty */
	;

argument:
	WORD {
               /*printf("   Yacc: insert argument \"%s\"\n", $1);*/

	       /*Command::_currentSimpleCommand->insertArgument( $1 );*/
              
		expandWildcardsIfNecessary(NULL,$1);
		qsort(array,nEntries,sizeof(char*),compar);
                for (int i = 0;i < nEntries;i++) {
                   Command::_currentSimpleCommand->insertArgument(array[i]);
                }
		free(array);
		array=NULL;
		nEntries = 0;
	}
	;

command_word:
	WORD {
               /*printf("   Yacc: insert command \"%s\"\n", $1);*/
	       
	       Command::_currentSimpleCommand = new SimpleCommand();
	       Command::_currentSimpleCommand->insertArgument( $1 );
	}
	;
iomodifier_opt:
	GREAT WORD {
		/*printf("   Yacc: insert output \"%s\"\n", $2);*/
 		if (Command::_currentCommand._outFile) {
			printf("Ambiguous output redirect");
			
		}
		else {
		        Command::_currentCommand._outFile = $2;
		}
	}
	| GREATGREAT WORD{
		/*printf("   Yacc: append output \"%s\"\n", $2);*/
		if (Command::_currentCommand._outFile) {
			printf("Ambiguous output redirect");
		}
		else {
                	Command::_currentCommand._outFile = $2;
			Command::_currentCommand._append = 1;
		}
	}
 	| GREATAMPERSAND WORD {
		/*printf("   Yacc: insert output and input \"%s\"\n",$2);*/
		if (Command::_currentCommand._outFile) {
			printf("Ambiguous output redirect");
		}
		else {
			Command::_currentCommand._outFile = $2;
			Command::_currentCommand._errFile = $2;
		}
	}
	| GREATGREATAMPERSAND WORD{
		/*printf("   Yacc: append output and error \"%s\"\n",$2);*/
		if (Command::_currentCommand._outFile) {
			printf("Ambiguous output redirect");
		}
		else {
			Command::_currentCommand._outFile = $2;
	       		Command::_currentCommand._errFile = $2;
                	Command::_currentCommand._append = 1;
		}
	}
	| LESS WORD{
		/*printf("   Yacc: insert input \"%s\"\n", $2);*/
		if (Command::_currentCommand._inFile) {
			printf("Ambiguous output redirect");
		}
		else {
			Command::_currentCommand._inFile = $2;
		}
	}
	 
	;
iomodifier_list:
	iomodifier_list iomodifier_opt
	| /*empty*/
	;
pipe_list:
        pipe_list PIPE command_and_args
	| command_and_args
 	;
background_opt:
	AMPERSAND{
            Command::_currentCommand._background = 1;
	}
	| /*empty*/
	;
%%
void
expandWildcardsIfNecessary(char * prefix, char * arg) {
        if (array == NULL) {
                  array=(char**)malloc(maxEntries*sizeof(char*));

        }
	if (arg[0] == 0) {
		if (nEntries == maxEntries) {
                        maxEntries *=2 ;
                        array = (char**)realloc(array,maxEntries*sizeof(char*));
                        assert(array!=NULL);
                }
		if (prefix != NULL) {
			array[nEntries] = strdup(prefix);
		}
		else {
			array[nEntries] = strdup("");
		}
                nEntries++;

		return;
	}
 	//printf("%s\n",prefix);
	//obtain the next component in the suffix
	//also advance suffix
	char *s = strchr(arg,'/');
	char component[MAXFILENAME] = "";
	if (s != NULL) {
		strncpy(component,arg,s-arg);
		arg = s+1;
	}
	else {
		strcpy(component,arg);
		arg = arg +strlen(arg);
	}
	char newPrefix[MAXFILENAME];
    	if (!(strchr(component,'*') || strchr(component,'?'))) {
		//Command::_currentSimpleCommand->insertArgument(arg);
		if (prefix != NULL) {
			sprintf(newPrefix,"%s/%s",prefix,component);
		}
		else {
			sprintf(newPrefix,"%s",component);
		}
		expandWildcardsIfNecessary(newPrefix,arg);
		return;
	}
	/*allocate enough space for regular expression*/
	char * reg = (char*)malloc(2*strlen(arg)+10);
	char * a = component;
	char * r = reg;
	*r = '^'; /*match beginning of line*/
	r++;
	/*convert wildcard to regular expression*/
	while (*a) {
		if (*a == '*') {
			*r='.';
			r++;
			*r='*';
			r++;
		}
		else if (*a == '?') {
			*r='.';
			r++;
		}
		else if (*a == '.') {
			*r='\\';
			r++;
			*r='.';
			r++;
		}
		else {
			*r=*a;
			r++;
		}
		a++;
	}
	*r='$';
	r++;
	*r=0;
	regex_t re;
	/*compile regular expression*/
	int result = regcomp(&re,reg,REG_EXTENDED|REG_NOSUB);
	if (result != 0) {
		perror("compile");
		return;
	}
	/*list directory and add as arguments and entries taht match the regualr expression*/
	//printf("%s\n",prefix);
	char d[MAXFILENAME];
	if (prefix == NULL) {
		strcpy(d,".");
	}
	else {
		if (is_regular_file(prefix)) return;
		strcpy(d,prefix);
		d[strlen(prefix)] = '/';
		d[strlen(prefix)+1] = '\0';
	}
	DIR *dir = opendir(d);
	//printf("%s\n",d);
	if (dir == NULL) {
		perror("opendir");
		return;
	}
	struct dirent *ent;
	while ((ent = readdir(dir)) != NULL) {
		/*if name matches*/
		regmatch_t match;
		if (regexec(&re,ent->d_name,1,&match,0) == 0) {
		  
		   if (ent->d_name[0]=='.') {
			if (component[0] == '.') {
		           if(prefix == NULL){
                                sprintf(newPrefix,"%s",ent->d_name);
                           }
                   	   else{
                                sprintf(newPrefix,"%s/%s", prefix, ent->d_name);
                           }

                           expandWildcardsIfNecessary(newPrefix,arg);
			}
		   }
		   else {
		   
                        if(prefix == NULL){
				sprintf(newPrefix,"%s",ent->d_name);
		        }
        	        else{
				sprintf(newPrefix,"%s/%s", prefix, ent->d_name);
		        }
			
		       expandWildcardsIfNecessary(newPrefix,arg);
		  }
		}	
	}
	closedir(dir);
	
}
void
yyerror(const char * s)
{
	fprintf(stderr,"%s", s);
}

#if 0
main()
{
	yyparse();
}
#endif
