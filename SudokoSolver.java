import java.io.*;
import java.math.*;
import java.security.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;

public class Solution {

    static public class Cell // Models a Sudoko cell 
    {
        Cell() {}
        Cell(int aRow, int aCol, int aNumPosValues)
        {
            row = aRow;
            col = aCol;
            numPosValues = aNumPosValues; 
        } 
        int numPosValues;
        int row;
        int col;
        int value;
    };

    // Convert string to matrix
    static Vector<Vector<Character>> convertStringToMatrix(String input)
    {
        Vector<Vector<Character>> arr = new Vector<Vector<Character>>();
        int ctr = 0;
        for (int i = 0; i < input.length(); i+=9)
        {
            Vector<Character> v = new Vector<Character>();
            for(int j = i; j < i+9; j++)
            {
                v.add(input.charAt(j));
            }
            arr.add(v);
        }
        return arr;
    }

    // Convert matrix to string
    static String convertMatrixToString(Vector<Vector<Character>> arr)
    {
        String output= new String();
        for (int i = 0; i < arr.size(); i++)
        {
            for (int j = 0; j < arr.get(i).size(); j++) output += (arr.get(i).get(j));
        }
        return output; 
    }

    // Get all "taken" numbers from row
    static int[] getAllRowPossibilities(int row, int col, Vector<Vector<Character>> arr, int v[])
    {
        for (int i = 0; i < arr.get(row).size(); i++)
        {
            Character c = arr.get(row).get(i);
            int pos = Character.getNumericValue(c); 
            if (c != '.') v[pos] = 1; 
        }
        return v;
    }

    // Get all "taken" numbers from column
    static int[] getAllColPossibilities(int row, int col, Vector<Vector<Character>> arr, int v[])
    {
        for (int i = 0; i < arr.size(); i++)
        {
            Character c = arr.get(i).get(col);
            int pos = Character.getNumericValue(c); 
            if (c != '.') v[pos] = 1;
        }
        return v;
    }

    // Get all "taken" numbers from square
    static int[] getAllSquarePossibilities(int row, int col, Vector<Vector<Character>> arr, int v[])
    {
        int startRow = (row / 3) * 3;
        int startCol = (col / 3) * 3;
        for (int i = startRow; i < startRow + 3; i++)
        {
            for (int j = startCol; j < startCol + 3; j++)
            {
                Character c = arr.get(i).get(j);
                int pos = Character.getNumericValue(c); 
                if (c != '.') v[pos] = 1;
            }
        }
        return v;
    }

    // Get all "taken" numbers from all rows, cols, and squares
    static int[] getAllPossibilities(int row, int col, Vector<Vector<Character>> arr)
    {
        int[] v = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        v = getAllRowPossibilities(row, col, arr, v);
        v = getAllColPossibilities(row, col, arr, v);
        v = getAllSquarePossibilities(row, col, arr, v);
        return v;
    }

    // Update cellList rows, cols, and squares as cells get filled with numbers
    static Vector<Cell> updateArray(int row, int col, Vector<Cell> cellList, int value)
    {
        int startRow = (row / 3) * 3;
        int startCol = (col / 3) * 3;
        for (int i = 0; i < cellList.size(); i++)
        {
            Cell cell = cellList.get(i); 
            if (cell.row != row && cell.col != col)
            {
                if (cell.row >= startRow && cell.row < startRow + 3 && cell.col >= startCol && cell.col < startCol + 3)
                {
                    cellList.get(i).numPosValues += value;
                }
                else if (col == cell.col || row == cell.row)
                {
                    cellList.get(i).numPosValues += value;
                }
            }
        }
        return cellList; 
    }

    // Get available cell with fewest number of possibilities
    static Cell getMinCell(Vector<Cell> cellList)
    {
        Cell minCell = new Cell();
        int minNum = 100;
        for(int i = 0; i < cellList.size(); i++)
        {
            if(cellList.get(i).numPosValues < minNum)
            {
                minNum = cellList.get(i).numPosValues;
                minCell = cellList.get(i); 
            }
        }
        return minCell; 
    }

    static boolean backtrack(Cell cell, Vector<Vector<Character>> arr, Vector<Cell> cellList)
    {
        int[] usedNumberList = getAllPossibilities(cell.row, cell.col, arr); 
            
        cellList = updateArray(cell.row, cell.col, cellList, -1);
        cellList.remove(cell);
        for(int i = 1; i <= 9; i++)
        {
            if (usedNumberList[i] == 0) // If number is not already present in row/col/square 
            {
                // Update arr with new number (guess)
                arr.get(cell.row).set(cell.col, (char)(i+'0')); 

                // Backtrack with new cell (the one with the least possible values) 
                if (cellList.size() > 0)
                {
                    Cell newCell = getMinCell(cellList);
                    if (backtrack(newCell, arr, cellList)) return true;
                }
                else return true; // Filled all available positions!
                 
                // If guess didn't work
                arr.get(cell.row).set(cell.col, '.'); 
            }
        }
        cellList = updateArray(cell.row, cell.col, cellList, 1);
        cellList.add(cell);
        return false; 
    }

    static int getSum(int[] v)
    {
        int sum = 0;
        for (int num : v) sum += num;
        return sum; 
    }

    // Create list of available cells 
    static Vector<Cell> createCellList(Vector<Vector<Character>> arr)
    {
        Vector<Cell> cellList = new Vector<Cell>();
        for (int i = 0; i < 9; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                if (arr.get(i).get(j) == '.') // If empty cell 
                {
                    int[] v = getAllPossibilities(i, j, arr);
                    int numPosValues = 9 - getSum(v); // Total numbers - total numbers used = total possible numbers left
                    cellList.add(new Cell(i, j, numPosValues));
                }
            }  
        }
        return cellList;
    }

    // Complete the solve function below.
    static String solve(String input) {

        // Convert string to 2D Character matrix
        Vector<Vector<Character>> arr = convertStringToMatrix(input); 

        // Create list of available cells 
        Vector<Cell> cellList = createCellList(arr);
        
        // Get available cell with fewest number of possibilities
        Cell firstCell = getMinCell(cellList);
        
        // Backtrack 
        backtrack(firstCell, arr, cellList);
        
        // Convert 2D array (now filled) to string 
        String output = convertMatrixToString(arr);
        
        return output;
    }

    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(System.getenv("OUTPUT_PATH")));

        String grid = bufferedReader.readLine();

        String res = solve(grid);

        bufferedWriter.write(res);
        bufferedWriter.newLine();

        bufferedReader.close();
        bufferedWriter.close();
    }
}
