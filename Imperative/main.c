
//  main.c
//  SudokuConsistency
//
//  Created by Ganesh P Kumar on 30/07/13.
//  Copyright (c) 2013 Ganesh P Kumar. All rights reserved.
//


#include <stdio.h>
#include <stdlib.h>

void basic_rotateRowLeft( int **grid, int row );
void basic_rotateRowRight( int **grid, int row );
void basic_rotateColumnUp( int **grid, int col );
void basic_rotateColumnDown( int **grid, int col );
void basic_specialSquarePerm( int **grid, int square, int perm[3][3] );

void derived_swapAdjLeft(int **grid, int pos[2] );
void derived_swapAdjRight( int **grid, int pos[2]);
void derived_swapAdjUp( int **grid, int pos[2]);
void derived_swapAdjDown( int **grid, int pos[2]);

void derived_freeSwap( int **grid, int pos1[2], int pos2[2]);

void derived_simpleFreeSwap( int** grid, int pos1[2], int pos2[2]);
int* searchSwappable(int **grid, int squareNum, int** subgridStats, int symbol);
int** updateSubgridStats(int** grid, int** subgridStats);

int **subgridStats;

FILE* output=NULL;
int main(int argc, const char * argv[]){
    int **grid = malloc(9*sizeof(int*));
    subgridStats=malloc(9*sizeof(int*));  //subgridStats[subgridNumber][symbol]
    int count[9]={0,0,0,0,0,0,0,0,0};     //count[symbol]
    int i, j, k;
    
    //Opening required files
    
    FILE *input=NULL;
    input=fopen("/Users/GP/Documents/SudokuConsistency/SudokuConsistency/grid", "r");
    if(input==NULL){
        fprintf(output, "Unable to open input file...Abort!\n");
        fprintf(stdout, "Unable to open input file...Abort!\n");
        exit(1);
    }
    output=fopen("/Users/GP/Documents/SudokuConsistency/SudokuConsistency/solution", "w");
    if(output==NULL){
        fprintf(output, "Unable to open output file...Abort!\n");
        fprintf(stdout, "Unable to open output file...Abort!\n");
        exit(1);
    }

    
    for(i=0;i<9;i++){
        grid[i]=malloc(9*sizeof(int));
        subgridStats[i]=malloc(9*sizeof(int));
    }
    fprintf(stdout, "Reading values from the file grid.txt...\n\n");

    //Reading the problem grid from file grid
    for (i = 0; i<9; i++) {
        for (j = 0; j<9; j++)
            fscanf(input,"%d ", &grid[i][j]);
    }
    
    //Getting stats for subgrids
    for(k=0; k<9; k++){
        for( i=0;i<9;i++ )
            count[i]=0;
        for (i =3*(k/3); i< 3*(k/3)+3; i++)
            for(j=3*(k%3); j<3*(k%3)+3; j++)
                count[ grid[i][j]-1 ]++;
        for (i=0; i<9; i++)
            subgridStats[k][i]= count[i]-1;
    }
    
    //reset count[]
    for(i=0; i<9; i++)
        count[i]=0;
    
    //set count[]
    for(i=0; i<9; i++){
        for(j=0; j<9;j++){
            count[i]+=subgridStats[j][i];
        }
        count[i]=9+count[i];
    }

    //do preliminary checks on number of occurances of each symbol
    for(i=0; i<9; i++){
        if(count[i]<9){
            fprintf(output, "No. of occurances of %d: %d, which is insufficient.", i+1, count[i]);
            fprintf(output, "\n\nGrid inconsistent!\nAborting...");
            fprintf(stdout, "No. of occurances of %d: %d, which is insufficient.", i+1, count[i]);
            fprintf(stdout, "\n\nGrid inconsistent!\nAborting...");
            exit(1);
        }
        else if(count[i]>9){
            fprintf(output, "No. of occurances of %d = %d, which is beyond sufficiency.", i+1, count[i]);
            fprintf(output, "\n\nGrid inconsistent!\nAborting...");
            fprintf(stdout, "No. of occurances of %d = %d, which is beyond sufficiency.", i+1, count[i]);
            fprintf(stdout, "\n\nGrid inconsistent!\nAborting...");
            exit(1);
        }
        
    }
    
    fprintf(output, "\n\nPreliminary checks complete!\n");
    
    //Display the grid
    
    fprintf(output, "Grid:\n");
    for(i=0;i<9;i++){
        for(j=0;j<9;j++){
            fprintf(output, "%d ", grid[i][j]);
            if(j%3==2)
                fprintf(output, "  ");
        }
        fprintf(output, "\n");
        if(i%3==2)
            fprintf(output, "\n");
    }

    int p,q;
    for(i=0; i<9; i++){
        // while loop to fix each subgrid, one by one
        while( isInconsistant(grid, i) ){   //as long as subgrid i is inconsistent
            fprintf(output,"$ > Attempting to fix sub-grid %d...\n", i);
            subgridStats=updateSubgridStats(grid, subgridStats);
            // for loop to iterate over all symbols
            for(j=0; j<9; j++)
                //if subGrid i has deficit of symbol j
                if (subgridStats[i][j]<0){
                    // search for swappable
                    int* swappablePos = searchSwappable(grid, i, subgridStats, j+1);
                    //swappablePos now points to a position for "j+1" that can be swapped
                    int currPos[2];
                    for (p =3*(i/3); p< 3*(i/3)+3; p++)
                        for(q=3*(i%3); q<3*(i%3)+3; q++)
                            if( subgridStats[i][ grid[p][q]-1 ] > 0 ){
                                currPos[0]=p;
                                currPos[1]=q;
                                break;
                            }
                    //swap swappablePos with a symbol that is in excess in subgrid i
                    derived_freeSwap(grid, currPos, swappablePos);
                    
                }
            //Increment value of i
            if( isInconsistant(grid, i) == 0 && i!=8 ){
                i++;
                
                //Display the grid
                fprintf(output, "Grid after fixing subgrid %d:\n", i);
                for(p=0;p<9;p++){
                    for(q=0;q<9;q++){
                        fprintf(output, "%d ", grid[p][q]);
                        if(q%3==2)
                            fprintf(output, "  ");
                    }
                    fprintf(output, "\n");
                    if(p%3==2)
                        fprintf(output, "\n");
                }

            }
            if (i>=9)
                break;
        }
    }
    
    int** solutionGrid= malloc(9*sizeof(int*));
    for(i=0; i<9; i++){
        solutionGrid[i]= malloc(9*sizeof(int));
    }
    FILE* solution= fopen("/Users/GP/Documents/SudokuConsistency/SudokuConsistency/solutionGrid", "r");
    if(input==NULL){
        fprintf(output, "Unable to open file...Abort!\n");
        fprintf(stdout, "Unable to open input file...Abort!\n");
        exit(1);
    }
    for (i = 0; i<9; i++) {
        for (j = 0; j<9; j++)
            fscanf(solution,"%d ", &solutionGrid[i][j]);
    }
    int newGrid[3][3];
    for(i=0;i<9;i++){
        for (p =3*(i/3); p< 3*(i/3)+3; p++)
            for(q=3*(i%3); q<3*(i%3)+3; q++){
                newGrid[p%3][q%3]=solutionGrid[p][q];
            }
         basic_specialSquarePerm(grid, i, newGrid);
    }

    
    fprintf(output, "\n\n\nFinal solved Grid:\n\n");
    for(i=0;i<9;i++){
        for(j=0;j<9;j++){
            fprintf(output, "%d ", grid[i][j]);
            if(j%3==2)
                fprintf(output, "  ");
        }
        fprintf(output, "\n");
        if(i%3==2)
            fprintf(output, "\n");
    }
    
     fprintf(output, "\n\nNote: To get any other solved sudoku grid from this final grid is trivial. One simply needs to permutate the subgrids.\n\n");
    
    printf("Given grid consistent! Refer /Users/GP/Documents/SudokuConsistency/SudokuConsistency/solution for instructions...\n");
}

void basic_rotateRowLeft( int **grid, int row ){
    int temp= grid[row][0];
    int i=0;
    for(i=0; i<8; i++)
        grid[row][i] = grid[row][i+1];
    grid[row][8]=temp;
    fprintf(output, "Rotate Row %d leftwards\n", row+1);
    fprintf(output, "\n");

    subgridStats=updateSubgridStats(grid, subgridStats);
    
    
}

void basic_rotateRowRight( int **grid, int row){
    int temp= grid[row][8];
    int i=0;
    for(i=8; i>0; i--)
        grid[row][i] = grid[row][i-1];
    grid[row][0]= temp;
    fprintf(output, "Rotate Row %d rightwards\n\n", row+1);
    
    subgridStats=updateSubgridStats(grid, subgridStats);
}

void basic_rotateColumnUp( int **grid, int col ){
    int temp= grid[0][col];
    int i=0;
    for(i=0; i<8; i++)
        grid[i][col] = grid[i+1][col];
    grid[8][col]= temp;
    fprintf(output, "Rotate Column %d upwards\n\n",col+1);
    subgridStats=updateSubgridStats(grid, subgridStats);

}

void basic_rotateColumnDown( int **grid, int col ){
    int temp= grid[8][col];
    int i=0;
    for(i=8; i>0; i--)
        grid[i][col] = grid[i-1][col];
    grid[0][col]= temp;
    fprintf(output, "Column %d rotated downwards\n\n",col+1);
    subgridStats=updateSubgridStats(grid, subgridStats);

}

void basic_specialSquarePerm( int **grid, int square, int perm[3][3] ){
    int p,q, currCount[9], newCount[9];
    for(p=0; p<9; p++){
        currCount[p]=0;
        newCount[p]=0;
    }

    for (p =3*(square/3); p< 3*(square/3)+3; p++)
        for(q=3*(square%3); q<3*(square%3)+3; q++){
            currCount[ grid[p][q]-1 ]++;
        }
    for (p=0; p<3; p++)
        for(q=0; q<3; q++){
            newCount[ perm[p][q]-1 ]++;
        }
    for (p=0; p<9; p++){
        if(newCount[p] != currCount[p]){
            fprintf(output, "Invalid permutation attempted!\n");
            return;
        }
    }
    for (p =3*(square/3); p< 3*(square/3)+3; p++)
        for(q=3*(square%3); q<3*(square%3)+3; q++){
            grid[p][q] = perm[ p-3*(square/3) ][ q-3*(square%3) ];
        }
    subgridStats=updateSubgridStats(grid, subgridStats);
    fprintf(output, "Permutate subgrid %d to get:\n", square+1);
    for(p=0;p<3;p++){
        for(q=0;q<3;q++)
            fprintf(output, "%d ", perm[p][q]);
        fprintf(output, "\n");
    }

}

void derived_swapAdjLeft( int** grid, int pos[2] ){
    if(pos[1]==0){
        fprintf(output, "Cannot swap left...already in column 1\n");
        return;
    }
        
    if(pos[1]%3==0){
        basic_rotateRowRight(grid, pos[0]);
        int newGrid[3][3], p, q;
        int squareNum = 3*(pos[0]/3)+(pos[1]/3);
        for (p =3*(squareNum/3); p< 3*(squareNum/3)+3; p++)
            for(q=3*(squareNum%3); q<3*(squareNum%3)+3; q++){
                newGrid[p%3][q%3] = grid[p][q];
            }
        
        int temp=newGrid[ pos[0]%3 ][ pos[1]%3 + 1 ];
        newGrid[ pos[0]%3 ][ pos[1]%3 + 1 ] = newGrid[ pos[0]%3 ][pos[1]%3 ];
        newGrid[ pos[0]%3 ][ pos[1]%3 ] = temp;
        
        basic_specialSquarePerm(grid, squareNum, newGrid);
        basic_rotateRowLeft(grid, pos[0]);

    }
    if(pos[1]%3!=0){
        int newGrid[3][3], p, q;
        int squareNum=3*(pos[0]/3) + (pos[1]/3);
        for (p=0; p<3; p++)
            for(q=0; q<3; q++)
                newGrid[p][q]=0;
        for (p =3*(squareNum/3); p< 3*(squareNum/3)+3; p++)
            for(q=3*(squareNum%3); q<3*(squareNum%3)+3; q++){
                newGrid[p%3][q%3] = grid[p][q];
            }
        int temp=newGrid[ pos[0]%3 ][ pos[1]%3 ];
        newGrid[ pos[0]%3 ][ pos[1]%3 ] = newGrid[ pos[0]%3 ][ pos[1]%3-1 ];
        newGrid[ pos[0]%3 ][pos[1]%3-1 ] = temp;
        basic_specialSquarePerm(grid, squareNum, newGrid);
    }
    subgridStats=updateSubgridStats(grid, subgridStats);
    fprintf(output, " Note: Symbol at position (%d, %d) is now swapped with adjacent left symbol.\n\n", pos[0], pos[1]);
    int i,j;
    for(i=0;i<9;i++){
        for(j=0;j<9;j++){
            fprintf(output, "%d ", grid[i][j]);
            if(j%3==2)
                fprintf(output, "  ");
        }
        fprintf(output, "\n");
        if(i%3==2)
            fprintf(output, "\n");
    }
}

void derived_swapAdjRight( int** grid, int pos[2] ){
    if(pos[1]==8){
        fprintf(output, "Cannot swap right...already in column 9\n");
        return;
    }
    
    int newPos[2];
    newPos[0]=pos[0];
    newPos[1]=pos[1]+1;
    derived_swapAdjLeft(grid, newPos);
    subgridStats=updateSubgridStats(grid, subgridStats);
}

void derived_swapAdjUp( int** grid, int pos[2] ){
    if(pos[0]==0){
        fprintf(output, "Cannot swap up...already in row 1\n");
        return;
    }
    
    if(pos[0]%3==0){
        basic_rotateColumnDown(grid, pos[1]);
        int newGrid[3][3], p, q;
        int squareNum = 3*(pos[0]/3)+(pos[1]/3);
        for (p =3*(squareNum/3); p< 3*(squareNum/3)+3; p++)
            for(q=3*(squareNum%3); q<3*(squareNum%3)+3; q++){
                newGrid[p%3][q%3] = grid[p][q];
            }
        int temp=newGrid[ pos[0]%3 + 1 ][ pos[1]%3 ];
        newGrid[ pos[0]%3 + 1 ][ pos[1]%3 ] = newGrid[ pos[0]%3 ][ pos[1]%3 ];
        newGrid[ pos[0]%3 ][ pos[1]%3 ] = temp;
        basic_specialSquarePerm(grid, squareNum, newGrid);
        basic_rotateColumnUp(grid, pos[1]);
        
    }
    if(pos[0]%3!=0){
        int newGrid[3][3], p, q;
        int squareNum = 3*(pos[0]/3)+(pos[1]/3);
        for (p =3*(squareNum/3); p< 3*(squareNum/3)+3; p++)
            for(q=3*(squareNum%3); q<3*(squareNum%3)+3; q++){
                newGrid[p%3][q%3] = grid[p][q];
            }
        int temp=newGrid[ pos[0]%3 - 1 ][ pos[1]%3 ];
        newGrid[ pos[0]%3 - 1 ][ pos[1]%3 ] = newGrid[ pos[0]%3 ][ pos[1]%3 ];
        newGrid[ pos[0]%3 ][ pos[1]%3 ] = temp;
        basic_specialSquarePerm(grid, squareNum, newGrid);

    }
    subgridStats=updateSubgridStats(grid, subgridStats);
    fprintf(output, " Note: Symbol at position (%d, %d) is now swapped with adjacent up symbol.\n\n", pos[0], pos[1]);
    int i,j;
    for(i=0;i<9;i++){
        for(j=0;j<9;j++){
            fprintf(output, "%d ", grid[i][j]);
            if(j%3==2)
                fprintf(output, "  ");
        }
        fprintf(output, "\n");
        if(i%3==2)
            fprintf(output, "\n");
    }

}

void derived_swapAdjDown( int** grid, int pos[2] ){
    if(pos[0]==8){
        fprintf(output, "Cannot swap down...already in row 9\n");
        return;
    }
    int newPos[2];
    newPos[0]=pos[0]+1;
    newPos[1]=pos[1];
    derived_swapAdjUp(grid, newPos);
    subgridStats=updateSubgridStats(grid, subgridStats);
}


void derived_freeSwap( int **grid, int pos1[2], int pos2[2]){
    int currPos[2], newPos[2], i, hor, vert;
    int flag=0;
    fprintf(output, "\n\nTo swap symbols at (%d, %d) and (%d, %d)...\n\n", pos1[0], pos1[1], pos2[0], pos2[1]);
    if(pos1[1] < pos2[1]){
        currPos[0]=pos1[0];
        currPos[1]=pos1[1];
        newPos[0]=pos2[0];
        newPos[1]=pos2[1];
    }
    else{
        currPos[0]=pos2[0];
        currPos[1]=pos2[1];
        newPos[0]=pos1[0];
        newPos[1]=pos1[1];
    }
    hor=newPos[1]-currPos[1];
    vert=newPos[0]-currPos[0];
    if(vert<0){
        flag=1;
        vert*=-1;
    }
    for(i=0; i<hor; i++){
        derived_swapAdjRight(grid, currPos);
        currPos[1]++;
    }
    if(flag==0){
        for(i=0;i<vert;i++){
            derived_swapAdjDown(grid, currPos);
            currPos[0]++;
        }
        
        currPos[0]--;
        
        for(i=0;i<vert-1;i++){
            derived_swapAdjUp(grid, currPos);
            currPos[0]--;
        }
    }
    else{
        for(i=0; i<vert;i++){
            derived_swapAdjUp(grid, currPos);
            currPos[0]--;
        }
        
        currPos[0]++;
        
        for(i=0; i<vert-1;i++){
            derived_swapAdjDown(grid, currPos);
            currPos[0]++;
        }
    }
    
    for(i=0; i<hor; i++){
        derived_swapAdjLeft(grid, currPos);
        currPos[1]--;
    }
    subgridStats=updateSubgridStats(grid, subgridStats);
    
    fprintf(output,"Note: Symbol at (%d, %d) is now swapped with symbol at (%d, %d)\n\n\n\n",pos1[0], pos1[1], pos2[0], pos2[1]);
}

int isInconsistant( int** grid, int squareNum ){
    int p, q;
    int check[]= {0,0,0,0,0,0,0,0,0};
    for (p =3*(squareNum/3); p< 3*(squareNum/3)+3; p++)
        for(q=3*(squareNum%3); q<3*(squareNum%3)+3; q++){
            check[ grid[p][q]-1 ]++;
        }
    for(p=0; p<9; p++)
        if(check[p]==0)
            return 1;
    
    return 0;
}

int* searchSwappable(int **grid, int squareNum, int** subgridStats, int symbol){
    int i,j, p, q;
    int *retPos = NULL;

    for(i=0; i<9; i++){
         if (i != squareNum && subgridStats[i][symbol-1] > 0){
            for(j=0; j<9; j++){
                retPos=malloc(2*sizeof(int));
                for (p =3*(i/3); p< 3*(i/3)+3; p++){
                    for(q=3*(i%3); q<3*(i%3)+3; q++){
                        if(grid[p][q] == symbol){
                            retPos[0]=p;
                            retPos[1]=q;
                            return retPos;
                        }
                    }
                }
            }
            
        }
    }
    if(retPos==NULL)
        fprintf(output, "Error! searchSwappable() returning NULL! \n");
    return retPos;
}

int** updateSubgridStats(int** grid, int** subgridStats){
    int i, j, k;
    int count[]={0,0,0,0,0,0,0,0,0};
    for(k=0; k<9; k++){
        for( i=0;i<9;i++ )
            count[i]=0;
        for (i =3*(k/3); i< 3*(k/3)+3; i++)
            for(j=3*(k%3); j<3*(k%3)+3; j++)
                count[ grid[i][j]-1 ]++;
        for (i=0; i<9; i++)
            subgridStats[k][i]= count[i]-1;
    }
    return subgridStats;
}

