
/*
 *
 * CS-252 Fall 2013
 * shell.l: lexical analyzer for shell
 * You have to extend it.
 *
 */

%{

#include <string.h>
#include "y.tab.h"
#include <regex.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <signal.h>
#include "command.h"
#include <sys/stat.h>
#include <fcntl.h>
#include <pwd.h>

//extern yy_buffer_state;
//typedef yy_buffer_state *YY_BUFFER_STATE;
extern int yyparse();
extern YY_BUFFER_STATE yy_scan_buffer(char*,size_t);
static  void yyunput (int c,char *buf_ptr  );

void myunputc(int c) {
    unput(c);
}

////////////  Start added code ///////////

extern "C" char * read_line();
extern "C" void init();
int mygetc(FILE * f) {

	static char *p;

	char ch;
	if (!isatty(0)) {

  	// stdin is not a tty. Call real getc

    		return getc(f);

	}

    // stdin is a tty. Call our read_line.

    	if (p==NULL || *p == 0) {

      		char * s = read_line();
		//printf("%s\n",s);
        	p = s;

	}

	ch = *p;

	p++;

	return ch;

}

	#undef getc

	#define getc(f) mygetc(f)

	/////////// End added code ///////////
%}
%%
[\\]*`[^\n\`]*[\\]*` 	{
			char *com = strdup(yytext);
			int len = yyleng;
			//printf("%s\n",yytext);
			if (strchr(com,'\\')) {
				com += 2;
				com[yyleng-4] = '\0';
			}
			else {
				com++;
				com[yyleng-2] = '\0';
				len -= 3;
			}
			printf("%s,%d",com,(int)strlen(com));
			fflush(stdout);
			//int temperr = dup(2);
			int tempin = dup(0);
			int tempout = dup(1);
			int fdpipein[2];
			int fdpipeout[2];
			pipe(fdpipein);
			pipe(fdpipeout);
			//int flags = fcntl(fdpipeout[0],F_GETFL);
		//	fcntl(fdpipeout[0],F_SETFL,flags|O_NONBLOCK);
			write(fdpipein[1],com,(int)strlen(com));
			write(fdpipein[1],'\0',1);
		//	const char *toExit = "\nsilent_exit\n";
		//	printf("%s,%d\n",(char*)toExit,(int)strlen((char*)toExit));
			write(fdpipein[1],"\nsilent_exit\n",13);
			//char ch = 10;
			//write(fdpipein[1],&ch,1);
		//	write(fdpipein[1],'\0',1);
			//write(fdpipein[1],NULL,1);
			close(fdpipein[1]);
			//printf("%s\n",com);
			 dup2(fdpipeout[1],1);
			 dup2(fdpipein[0],0);
			 fflush(stdout);
			int ret = fork();
			if (ret == 0) {
			//	dup2(fdpipeout[1],1);
			//	dup2(fdpipein[0],0);	
				close(fdpipeout[1]);
				close(fdpipein[0]);
			//	close(fdpipeout[0]);
			//	_exit(1);
				//perror("something");
			}
			else if (ret < 0) {
				perror("fork");
				_exit(1);
			}
			else {
				//perror("an error");

				waitpid(ret,NULL,0);
				close(fdpipeout[1]);
				close(fdpipein[0]);
				dup2(tempin,0);
				dup2(tempout,1);
				close(tempin);
				close(tempout);
				int maxSize = 256;
				char*buffer = (char*)malloc(sizeof(char)*maxSize);
				char p;
				int i = 0;
				while (read(fdpipeout[0],&p,1) > 0) {
				//	printf("%d: %d\n",i, p);
					if (i == maxSize) {
						maxSize *= 2;
						buffer = (char*)realloc(buffer,sizeof(char)*maxSize);
					}
						buffer[i] = p;
						i++;
				}
				close(fdpipeout[0]);
				if (i != 0) {
					
					//buffer[0] = 34;
					//buffer[--i] = 34;
					buffer[i] = '\0';
					printf("this is the buffer %s, with length %d\n",buffer,strlen(buffer));
				 	//while (i>=0) {
				 	     	//myunputc(buffer[i--]);
					//}
					yylval.string_val = strdup(buffer);
				//printf("%s\n",yylval.string_val);
				//return WORD;
				}
			}
		}
\"[^\n\"]*\" {
		int i = 0;
		for (i = 0;i < yyleng-2;i++) {
			yytext[i] = yytext[i+1];
		}
		yytext[i] = '\0';
		yylval.string_val = strdup(yytext);
		return WORD;
	}
[^ \n\t]*[\\][^ \n\t]* {
		//printf("%s\n",yytext);
		int i = 0;
		int p = 0;
		char temp[1024];
		while (yytext[i] != '\0') {
			if (yytext[i] == '\\') {
				i++;
			}
			temp[p] = yytext[i];
			p++;
			i++;
		}
		temp[p] = '\0';
		yylval.string_val = strdup(temp);
		return WORD;
	}
\n 	{
		return NEWLINE;
	}

[ \t] 	{
		/* Discard spaces and tabs */
     	}

">" 	{
		return GREAT;
	}

">>"    {
		return GREATGREAT;
        }

"|"	{	
		return PIPE;
	}

"&"	{
		return AMPERSAND;
	}

">>&"   {
		return GREATGREATAMPERSAND;
	}

">&"	{
		return GREATAMPERSAND;
	}

"<"	{
		return LESS;
	}
[^ \t\n][^ \t\n]*  {
		/* Assume that file names have only alpha chars */
		char *pat = "[<|>|\\&|\\|]";
		char *temp = strdup(yytext);
		regex_t reg;
		size_t nmatch=2;
		regmatch_t match[2];
		if (regcomp(&reg,pat,0)) {
			perror("compile");
			exit(1);
		}
		  //printf("%s\n",yytext);

		if (!regexec(&reg,yytext,nmatch,match,0)) {
			int i = 0;
			int j = 0;
			temp = new char[strlen(yytext)+2];
			//printf("%s\n",yytext);
			while (yytext[i]) {
				if (i != match[0].rm_so) {
					temp[j++] = yytext[i++];
				}
				else {
					temp[j++] = ' ';
					temp[j++] = yytext[i++];
					temp[j++] = ' ';
				}
			}
			temp[j] = '\0';
			//fprintf(stderr,"CHECK-> %s\n",temp);
			j = strlen(temp)-1;
			while (j >=0) {
				myunputc(temp[j]);
				j--;
			}
			yylval.string_val = strdup(temp);
			

		}	
		else {
			yylval.string_val = strdup(temp);
			return WORD;
		}
	}

