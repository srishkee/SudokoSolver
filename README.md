# SudokoSolver
Current Algorithm:

1. Alpha-beta pruning - only those values are considered which are not already present in the corresponding row, column, or 3x3 square

2. MRV Heuristic - square are evaluated based on their number of possibilities. The fewer possibilities, the earlier they are evaluated.

I initially began with the brute-force solution (evaluate all numbers from 1->9 sequentially) and worked my way through the optimizations. 

A Sudoko puzzle that initially took 59seconds to solve is now solvable in 1 sec :) but I am working on more optimizations using constraint propagation.
