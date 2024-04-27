import numpy as np


class MinimaxPlayer:
    def __init__(self, game):
        self.game = game

    def play(self, state):
        player = self.game.to_move(state)

        def max_value(state):
            if self.game.terminal_test(state):
                return self.game.utility(state, player)

            v = -np.inf
            for action in self.game.actions(state):
                v = max(v, min_value(self.game.result(state, action)))

            return v

        def min_value(state):
            if self.game.terminal_test(state):
                return self.game.utility(state, player)

            v = np.inf
            for action in self.game.actions(state):
                v = min(v, max_value(self.game.result(state, action)))

            return v

        return max(self.game.actions(state), key=lambda a: min_value(self.game.result(state, a)))

    def __call__(self,game,state):
        return self.play(state)


