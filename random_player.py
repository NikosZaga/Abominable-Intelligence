import random
from game import Game

class RandomizedPlayer:
    def __init__(self,game):
       self.game = game
    
    def play(self, state):
        available_moves = self.game.actions(state)
        if available_moves:
            return random.choice(self.game.actions(state))
        else:
            raise Exception('No legal moves')

    def __call__(self,game,state):
        return self.play(state)