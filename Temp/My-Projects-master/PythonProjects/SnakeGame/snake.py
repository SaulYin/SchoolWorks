import sys
import pygame
import random
import time
BLOCK_SIZE=30
WIDTH=600
HEIGHT=600
INITIAL_SNAKE_LENGTH=3
FPS=5
DRAW_GRID=True

class snake:
   def __init__(self,number):
      self.number=number
      
      
   def get_initial_snake(self,snake_length,width,height,block_size):
      snake_list=[]
      if (width/block_size)%2 != 0:
         init_centerx=width/2
      else:
         init_centerx=block_size*((width/block_size)/2)-block_size/2
      if (height/block_size) %2 !=0:
         init_centery=height/2
      else:
         init_centery=block_size *((height/block_size)/2)-block_size/2
      init_center=(int(init_centerx),int(init_centery)+int(self.number*block_size) )
      snake_list.append(init_center)
      for i in range(snake_length-1):
         centerx=init_centerx-block_size*(i+1)
         centery=init_centery
         center=(int(centerx),int(centery+int(self.number*block_size)))
         snake_list.append(center)

      snake_list.reverse()
      return(snake_list)

   def pick_random_apple_position( self,snake_list, width, height, block_size ):
        # TODO TODO TODO
        allcenter=[]
        for i in range(int(block_size/2),int(width-block_size/2)+1,block_size):
                for j in range(int(block_size/2),int(height-int(block_size/2))+1,block_size):
                        allcenter.append((i,j))

        possi_center=[]
        for tup in allcenter:
                if tup not in snake_list:
                        possi_center.append(tup)

        apple_center=random.choice(possi_center)
        return apple_center

   def update_direction(self,current_direction,new_direction):
      if current_direction=="up":
         if new_direction=="right":
            return("right")
         elif new_direction=="left":
            return("left")
         else:
            return("up")

      elif current_direction=="down":
         if new_direction=="right":
            return("right")
         elif new_direction=="left":
            return("left")
         else:
            return("down")

      elif current_direction=="right":
         if new_direction=="up":
            return("up")
         elif new_direction=="down":
            return("down")
         else:
            return("right")
      else:
         if new_direction=="up":
            return "up"
         elif new_direction=="down":
            return "down"
         else:
            return "left"


   def update_snake( self,snake_list, direction, apple_position, block_size ):
      # TODO TODO TODO
      # TODO TODO TODO
      # TODO TODO TODO
      snake_head=snake_list[-1]
      if (direction=="up"):
         if (apple_position[0]==snake_head[0] and apple_position[1]==snake_head[1]-block_size):
            is_apple_eaten=True
            snake_list.append(apple_position)
         else:
            is_apple_eaten=False
            for i in range(0,len(snake_list)-1):
               snake_list[i]=snake_list[i+1]
            x,y=snake_list[-1]
            y=y-block_size
            newtup=(x,y)
            snake_list.remove(snake_list[-1])
            snake_list.append(newtup)
        


      elif (direction=="down"):
         if (apple_position[0]==snake_head[0] and apple_position[1]==snake_head[1]+block_size):
            is_apple_eaten=True
            snake_list.append(apple_position)
                        
         else:
            is_apple_eaten=False
            for i in range(0,len(snake_list)-1):
               snake_list[i]=snake_list[i+1]
            x,y=snake_list[-1]
            y=y+block_size
            newtup=(x,y)
            snake_list.remove(snake_list[-1])
            snake_list.append(newtup)

      elif (direction=="right"):
         if (apple_position[0]==snake_head[0]+block_size and apple_position[1]==snake_head[1]):
            is_apple_eaten=True
            snake_list.append(apple_position)
         else:
            is_apple_eaten=False
            for i in range(0,len(snake_list)-1):
               snake_list[i]=snake_list[i+1]
            x,y=snake_list[-1]
            x=x+block_size
            newtup=(x,y)
            snake_list.remove(snake_list[-1])
            snake_list.append(newtup)

      else:
         if (apple_position[0]==snake_head[0]-block_size and apple_position[1]==snake_head[1]):
            is_apple_eaten=True
            snake_list.append(apple_position)
         else:
            is_apple_eaten=False
            for i in range(0,len(snake_list)-1):
               snake_list[i]=snake_list[i+1]
            x,y=snake_list[-1]
            x=x-block_size
            newtup=(x,y)
            snake_list.remove(snake_list[-1])
            snake_list.append(newtup)

   
                        
                        
                
      return (snake_list,is_apple_eaten)

   def check_collision( self,snake_list1, snake_list2):
      # TODO TODO TODO
      # TODO TODO TODO
      # TODO TODO TODO
      snake_head=snake_list1[-1]
      if (snake_head in snake_list2):
         return(True)
            
        


     
        
   
                        
                        
                
      
   def is_snake_inside_window( self,snake_list, width, height ):
      # TODO TODO TODO
      # TODO TODO TODO
      # TODO TODO TODO
      x,y=snake_list[-1]
      if x>=width or y>=height:
         return False
      elif x<=0 or y<=0:
         return False
      else:
         return True

   def is_snake_hit_itself( self,snake_list ):
      # TODO TODO TODO
      # TODO TODO TODO
      # TODO TODO TODO
      check=[]
      for i in snake_list:
         if i not in check:
            check.append(i)
         else:
            return True

   def draw_grid( self,width, height, block_size, window):
      # TODO TODO TODO
      # TODO TODO TODO
      # TODO TODO TODO     
      for i in range(block_size,height+1,block_size):
               pygame.draw.line(window,(255,255,255),(0,i),(width,i))

      for j in range(block_size,width+1,block_size):
               pygame.draw.line(window,(255,255,255),(j,0),(j,height))

   def draw_snake( self,snake_list, block_size, window ):
      # TODO TODO TODO
      # TODO TODO TODO
      # TODO TODO TODO
   
      for tup in snake_list:
         r=random.randint(0,255)
         g=random.randint(0,255)
         b=random.randint(0,255)
  
         x,y=tup
         pygame.draw.rect(window,(r,g,b),(x-int(block_size/2),y-int(block_size/2),block_size,block_size))

   def draw_apple( self,apple_position, block_size, window ):
      r=random.randint(0,255)
      g=random.randint(0,255)
      b=random.randint(0,255)
      pygame.draw.circle(window,(r,g,b),apple_position,int(block_size/2))

def score1(window,score):
   myfont=pygame.font.SysFont("comicsansms",25)
   text=myfont.render("Score1: "+str(score), True, (255,0,0))
   window.blit(text,[0,0])
def score2(window,score):
   myfont=pygame.font.SysFont("comicsansms",25)
   text=myfont.render("Score2: "+str(score), True, (255,0,0))
   window.blit(text,[0,2*BLOCK_SIZE])


def message(window,messa,color):
   myfont=pygame.font.SysFont("comicsansms",20)
   mytext=myfont.render(messa,True,color)
   window.blit(mytext,[0,HEIGHT/2])
#create a gameloop to loop game
def gameloop(window,play,snake1,snake2,snake_list1,snake_list2,apple_position,current_direction1,current_direction2,new_direction1,new_direction2):
   snake_list=snake_list1+snake_list2
   clock = pygame.time.Clock()
   while play:
      for event in pygame.event.get():
         if event.type == pygame.QUIT:
            pygame.quit()
            sys.exit()
         if event.type == pygame.KEYDOWN:
            if event.key == pygame.K_LEFT:
               new_direction1 = 'left'
            if event.key == pygame.K_RIGHT:
               new_direction1 = 'right'
            if event.key == pygame.K_DOWN:
               new_direction1 = 'down'
            if event.key == pygame.K_UP:
               new_direction1 = 'up'
            if event.key == pygame.K_a:
               new_direction2 = 'left'
            if event.key == pygame.K_d:
               new_direction2 = 'right'
            if event.key == pygame.K_s:
               new_direction2 = 'down'
            if event.key == pygame.K_w:
               new_direction2 = 'up'   

      current_direction1 = snake1.update_direction( current_direction1, new_direction1 )
      current_direction2 = snake2.update_direction( current_direction2, new_direction2 )

      snake_list1, is_apple_eaten1 = snake1.update_snake( snake_list1, current_direction1, \
                                       apple_position, BLOCK_SIZE )
      snake_list2, is_apple_eaten2 = snake2.update_snake( snake_list2, current_direction2, \
                                                                                           apple_position, BLOCK_SIZE )

      if is_apple_eaten1:
         apple_position = snake1.pick_random_apple_position( snake_list, \
                                 WIDTH, HEIGHT, BLOCK_SIZE )
      if is_apple_eaten2:
         
         apple_position = snake2.pick_random_apple_position( snake_list, \
                                                                            WIDTH, HEIGHT, BLOCK_SIZE )



      if not (snake2.is_snake_inside_window( snake_list2, WIDTH, HEIGHT ) \
                                                                                        or snake2.is_snake_hit_itself( snake_list2 )):
                                      
         winner="snake1"                     
         check=True
         return(winner,check)

      if not (snake1.is_snake_inside_window( snake_list1, WIDTH, HEIGHT ) \
                                                                                        or snake1.is_snake_hit_itself( snake_list1 )):
                                      
         winner="snake2"                     
         check=True
         return(winner,check)
                   
      





      window.fill( (0,0,0) ) # black background
      #check for collision and decide who to win
      if snake2.check_collision(snake_list2,snake_list1 ):
         winner="snake1"
         check=True
         return(winner,check)
                     
         
      if snake1.check_collision(snake_list1, snake_list2 ):
         winner="snake2"
         check=True
         return(winner,check)
        








      if DRAW_GRID:
         snake1.draw_grid( WIDTH, HEIGHT, BLOCK_SIZE, window )
      snake1.draw_snake( snake_list1, BLOCK_SIZE, window )
      snake2.draw_snake( snake_list2, BLOCK_SIZE, window )
      snake1.draw_apple( apple_position, BLOCK_SIZE, window )

      # update the window with the last drawings
      #display scores
      score1(window,len(snake_list1)-INITIAL_SNAKE_LENGTH )
      score2(window,len(snake_list2)-INITIAL_SNAKE_LENGTH )

      pygame.display.update()
      if (len(snake_list1)-INITIAL_SNAKE_LENGTH)==10:
         winner="snake2"
         check=True
         return(winner,check)
      if (len(snake_list2)-INITIAL_SNAKE_LENGTH)==10:
         winner="snake2"
         check=True
         return(winner,check)
      # set fps (speed)
      clock.tick( FPS )
      
   

def main():
   #initializations
   pygame.init()

   window = pygame.display.set_mode( (WIDTH+1, HEIGHT+1) )
   pygame.display.set_caption( 'Snake game' )

   # current direction of the snake: right, left, up, down
   current_direction1 = 'right'
   current_direction2 = 'right'
   # new direction of the snake: right, left, up, down
   new_direction1 = 'right'
   new_direction2 = 'right'

   

   
   

   # the list of squares in the snake, (x,y) are the center
   # positions of the square.
   snake1=snake(0)
   snake2=snake(2)
   
   
   
      
      
      
   # loop the whole process, including checking for game over and play the game
   play=True
   while play:
      process=True
      check=False
      snake_list1 = snake1.get_initial_snake( INITIAL_SNAKE_LENGTH, WIDTH, HEIGHT, \
                                 BLOCK_SIZE )
      snake_list2 = snake2.get_initial_snake( INITIAL_SNAKE_LENGTH, WIDTH, HEIGHT, \
                                                                              BLOCK_SIZE )
      
      snake_list=snake_list1+snake_list2
         # center position of apple
      apple_position =snake1.pick_random_apple_position( snake_list, WIDTH, HEIGHT, \
                                           BLOCK_SIZE )
      winner,check=gameloop(window,process,snake1,snake2,snake_list1,snake_list2,apple_position,current_direction1,current_direction2,new_direction1,new_direction2)
      
      #check game over
      while check:
         the_text="the winner is "+winner+" press p to play again and q to quit"
         message(window,the_text,(255,0,0))
         pygame.display.update()
      
         for event in pygame.event.get():
            if event.type==pygame.KEYDOWN:
               if event.key==pygame.K_q:
                  pygame.quit()
                  sys.exit()
               if event.key==pygame.K_p:
                  
                  play=True
                  check=False
      
   
   
   
            
   
   


if __name__ == '__main__':
   main()

   
