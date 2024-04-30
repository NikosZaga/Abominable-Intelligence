import matplotlib.pyplot as plt
import numpy as np
import re

def parse_log_file(input_file):
    results = {'X': [], 'O': [], 'Tie': []}
    with open(input_file, 'r') as f:
        for line in f:
            match = re.search(r"{\'X\': (\d+), \'O\': (\d+), \'Tie\': (\d+)}", line)
            if match:
                results['X'].append(int(match.group(1)))
                results['O'].append(int(match.group(2)))
                results['Tie'].append(int(match.group(3)))
    return results

def plot_results(results):
    # Create an array for the x values
    x = np.arange(len(results['X']))

    # Create a bar plot for each result type
    fig, ax = plt.subplots()
    ax.bar(x - 0.2, results['X'], 0.2, label='X Wins')
    ax.bar(x, results['O'], 0.2, label='O Wins')
    ax.bar(x + 0.2, results['Tie'], 0.2, label='Ties')

    # Add labels and title
    ax.set_xlabel('Run Number')
    ax.set_ylabel('Count')
    ax.set_title('Results Over Time')
    ax.legend()

    # Display the plot
    plt.show()

if __name__ == "__main__":
    results = parse_log_file('ai_proj_1/log.txt')
    plot_results(results)