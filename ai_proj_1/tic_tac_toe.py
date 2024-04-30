from collections import namedtuple
from game import Game
from random_player import RandomizedPlayer
from minimax import MinimaxPlayer


GameState = namedtuple('GameState', 'to_move, utility, board, moves')

class TicTacToe(Game):
    """Play TicTacToe on an h x v board, with Max (first player) playing 'X'.
    A state has the player to move, a cached utility, a list of moves in
    the form of a list of (x, y) positions, and a board, in the form of
    a dict of {(x, y): Player} entries, where Player is 'X' or 'O'."""

    def __init__(self, h=3, v=3, k=3):
        self.h = h
        self.v = v
        self.k = k
        moves = [(x, y) for x in range(1, h + 1)
                 for y in range(1, v + 1)]
        self.initial = GameState(to_move='X', utility=0, board={}, moves=moves)

    def actions(self, state):
        """Legal moves are any square not yet taken."""
        return state.moves

    def result(self, state, move):
        if move not in state.moves:
            return state  # Illegal move has no effect
        board = state.board.copy()
        board[move] = state.to_move
        moves = list(state.moves)
        moves.remove(move)
        return GameState(to_move=('O' if state.to_move == 'X' else 'X'),
                         utility=self.compute_utility(board, move, state.to_move),
                         board=board, moves=moves)

    def utility(self, state, player):
        """Return the value to player; 1 for win, -1 for loss, 0 otherwise."""
        return state.utility if player == 'X' else -state.utility

    def terminal_test(self, state):
        """A state is terminal if it is won or there are no empty squares."""
        return state.utility != 0 or len(state.moves) == 0

    def display(self, state):
        board = state.board
        print("  1 2 3")
        for x in range(1, self.h + 1):
            print(x, end=' ')
            for y in range(1, self.v + 1):
                print(board.get((x, y), '.'), end=' ')
            print()

    def compute_utility(self, board, move, player):
        """If 'X' wins with this move, return 1; if 'O' wins return -1; else return 0."""
        if (self.k_in_row(board, move, player, (0, 1)) or
                self.k_in_row(board, move, player, (1, 0)) or
                self.k_in_row(board, move, player, (1, -1)) or
                self.k_in_row(board, move, player, (1, 1))):
            return +1 if player == 'X' else -1
        else:
            return 0

    def k_in_row(self, board, move, player, delta_x_y):
        """Return true if there is a line through move on board for player."""
        (delta_x, delta_y) = delta_x_y
        x, y = move
        n = 0  # n is number of moves in row
        while board.get((x, y)) == player:
            n += 1
            x, y = x + delta_x, y + delta_y
        x, y = move
        while board.get((x, y)) == player:
            n += 1
            x, y = x - delta_x, y - delta_y
        n -= 1  # Because we counted move itself twice
        return n >= self.k
    
    
def manual_player(game, state):
    """A manual player."""
    game.display(state)
    actions = game.actions(state)
    while True:
        action = input("Enter your move (e.g., 2,3): ")
        try:
            action = action.split(',')
            action = [int(v) for v in action]
            action = tuple(action)
            if action not in actions:
                print('invalid action!!')
            else:
                return action
        except:
            print('invalid action!!')

def play_game(game, *players):
    """Play an n-person, move-alternating game."""
    state = game.initial
    while True:
        for player in players:
            move = player(game,state)
            state = game.result(state, move)
            if game.terminal_test(state):
                game.display(state)
                result =  game.utility(state, game.to_move(game.initial))
                if result == 1:
                    print("'X' won!")
                elif result == -1:
                    print("'O' won!")
                else:
                    print('Tie!')
                return result

def game_start(self):
    game_mode = 0
    while game_mode not in ['1', '2', '3', '4', '5']:
        game_mode = input("Select Game Mode\n1.PvP\n2.PvC (minmax) \n3.PvC (randomized)\n4.CvC (minmax v randomized):\n5.CvC (randomized v minmax):\n")
        if game_mode == '1':
            play_game(self, manual_player, manual_player)
        elif game_mode == '2':
            play_game(self, manual_player, MinimaxPlayer(game))
        elif game_mode == '3':
            play_game(self, manual_player, RandomizedPlayer(game))
        elif game_mode == '4':
            play_game(self, MinimaxPlayer(game), RandomizedPlayer(game))
        elif game_mode == '5':
            play_game(self, RandomizedPlayer(game), MinimaxPlayer(game))
        else:
            print('Invalid input!!Try again.')



if __name__ == "__main__":
    game = TicTacToe(h=3, v=3, k=3)
    game_start(game)
