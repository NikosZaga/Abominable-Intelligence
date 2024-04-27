public class NewMinimax implements EvaluationFunction {
    @Override
    public int evaluate(ReversiGame game) {
        return game.heuristic();
    }
}