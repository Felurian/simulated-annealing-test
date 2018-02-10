# simulated-annealing-test
solving a board game using simulated annealing

![Image of Yaktocat](https://raw.githubusercontent.com/Felurian/simulated-annealing-test/master/result.png)

### Game Details
 * The board is a 31 by 31 chess-like grid
 * Up to a single piece can be placed on each square
 * Pieces grant [16 - taxicab_distance_from_middle_square] score
 * Each piece loses x/24 of its score where x is every other piece that is inside a 5 by 5 square centered on the piece
     * A completely surrounded piece grants no score
     * A piece surrounded by 12 other pieces grants half its original score
 * The goal is to find the arrangement of pieces that yields the highest score
