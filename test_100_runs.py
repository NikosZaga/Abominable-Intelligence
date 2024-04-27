import multiprocessing
from collections import defaultdict
from tic_tac_toe import TicTacToe
from minimax import MinimaxPlayer
from random_player import RandomizedPlayer
import os
import re
from collections import Counter

def play_game_and_record_results(game, *players):
    """Play an n-person, move-alternating game."""
    state = game.initial
    result_log = defaultdict(int)
    result_log = {'X': 0, 'O': 0, 'Tie': 0} 
    while True:
        for player in players:
            move = player(game, state)
            state = game.result(state, move)
            if game.terminal_test(state):
                result = game.utility(state, game.to_move(game.initial))
                if result == 1:
                    result_log['X'] += 1
                elif result == -1:
                    result_log['O'] += 1
                else:
                    result_log['Tie'] += 1
                return result_log

def run_multiple_games(game, num_games, rando_first):
    result_summary = defaultdict(int)
    num_replicas = 5

    with multiprocessing.Pool(processes=num_replicas) as pool:
        results = pool.starmap(worker, [(game, i, rando_first) for i in range(num_games)])
    
    for result_log in results:
        for key, value in result_log.items():
            result_summary[key] += value
            
    return result_summary

def worker(game, replica_num, rando_first):
    result_log = {'X': 0, 'O': 0, 'Tie': 0} 
    if rando_first:
        result_log = play_game_and_record_results(game, RandomizedPlayer(game), MinimaxPlayer(game))
    else:
        result_log = play_game_and_record_results(game, MinimaxPlayer(game), RandomizedPlayer(game))
    return result_log

def get_last_run_number(file_path, lines_to_read=10):
    try:
        with open(file_path, 'rb') as file:
            file.seek(0, os.SEEK_END)
            file_size = file.tell()
            lines = []
            newline_count = 0
            for offset in range(1, file_size + 1):
                file.seek(-offset, os.SEEK_END)
                char = file.read(1)
                if char == b'\n':
                    newline_count += 1
                    if newline_count == 4:
                        break
                lines.append(char.decode('utf-8'))
            last_lines = ''.join(reversed(lines)).split('\n')
            
            for line in reversed(last_lines):
                match = re.search(r'run #(\d+)', line)
                if match:
                    return int(match.group(1))
            return 0  # If no run number found, start from 0
    except FileNotFoundError:
        return 0  # If file doesn't exist, start from 0

if __name__ == "__main__":
    game = TicTacToe(h=3, v=3, k=3)
    run_count = 100
    
    #> Run 100 games with randomized player playing first
    result_summary_rando_first = run_multiple_games(game, run_count, rando_first=True)
    print("Results with randomized player playing first after 100 games:")
    print(result_summary_rando_first)

    #> Run 100 games with minimax player playing first
    result_summary_minimax_first = run_multiple_games(game, run_count, rando_first=False)
    print("Results with minimax player playing first after 100 games:")
    print(result_summary_minimax_first)

    # Output:
    script_dir = os.path.dirname(os.path.abspath(__file__))
    log_file_path = os.path.join(script_dir, "log.txt")
    last_run_number = get_last_run_number(log_file_path)
    with open(log_file_path, "a") as file:
        file.write(f"run #{last_run_number + 1}\n")
        file.write("random first: " + str(result_summary_rando_first)+ "\n"+ "minimax first: " + str(result_summary_minimax_first)+"\n")
