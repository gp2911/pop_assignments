%count number of occurances of element in list
count([],_,0).
count([X|Y], X, N) :- count(Y, X, W),
					  N is W+1.
count([X|Y], Z, N) :- count(Y, Z, N),
					  X\=Z.  

%elemCounts
elemCounts(A, N) :- count(A, 1, N1), 
					count(A, 2, N2),
					count(A, 3, N3),
					count(A, 4, N4), 
					N=[N1, N2, N3, N4]. 
boardElemCounts(A, N) :-  A = [P,Q,R,S], elemCounts(P, [W1, W2, W3, W4]), 
						  elemCounts(Q, [X1, X2, X3, X4]),
						  elemCounts(R, [Y1, Y2, Y3, Y4]), 
						  elemCounts(S, [Z1, Z2, Z3, Z4]),
						  N1 is W1+X1+Y1+Z1,
						  N2 is W2+X2+Y2+Z2,
						  N3 is W3+X3+Y3+Z3,
						  N4 is W4+X4+Y4+Z4,
						  N = [N1, N2, N3, N4].
%getCols
getCols(A, B) :-  A=[[E,F,G,H],[I,J,K,L],[M,N,O,P],[Q,R,S,T]], 
				  B=[[E,I,M,Q],[F,J,N,R],[G,K,O,S],[H,L,P,T]].

%getSubgrids
getSubgrids(A, B) :-  A=[[E,F,G,H],[I,J,K,L],[M,N,O,P],[Q,R,S,T]], 
					  B=[[E,F,I,J],[G,H,K,L],[M,N,Q,R],[O,P,S,T]]. 

%column functions
checkCols(A) :- getCols(A,B), 
				B=[P,Q,R,S],  
				validRow(P), 
				validRow(Q), 
				validRow(R), 
				validRow(S).

%subgrid functions
checkSubgrids(A) :- getSubgrids(A,B), 
					B=[P,Q,R,S],  
					validRow(P), 
					validRow(Q), 
					validRow(R), 
					validRow(S).

%necessary tests
necessaryRowTest(A) :- A=[A1, A2, A3, A4], 
					   between(0, 4, A1), 
					   between(0, 4, A2), 
					   between(0, 4, A3), 
					   between(0, 4, A4).
necessaryBoardTest(A) :- A=[P,Q,R,S], 
						 necessaryRowTest(P), 
						 necessaryRowTest(Q), 
						 necessaryRowTest(R), 
						 necessaryRowTest(S), 
						 boardElemCounts(A,N), 
						 N=[4,4,4,4].

%goalTest
countValid(A) :- elemCounts(A, N), 
				 N = [1,1,1,1].
validRow(A) :- A=[A1, A2, A3, A4],
			   between(0, 4, A1),
			   between(0, 4, A2), 
			   between(0, 4, A3), 
			   between(0, 4, A4), 
			   countValid(A).
validBoard(A) :- A=[P,Q,R,S], 
				 validRow(P), 
				 validRow(Q), 
				 validRow(R), 
				 validRow(S), 
				 checkCols(A), 
				 checkSubgrids(A).

%moveGen()
rotate(A,B) :- A=[W,X,Y,Z], 
			   B=[X,Y,Z,W].
rowRotate(A,B) :- A=[P,Q,R,S],
				  rotate(P,T), 
				  B=[T,Q,R,S].
%				  row(Q), 
%				  row(R), 
%				  row(S), 
				  
rowRotate(A,B) :- A=[P,Q,R,S],
				  rotate(Q,T), 
				  B=[P,T,R,S]. 
%				  row(P), 
%				  row(R), 
%				  row(S), 
rowRotate(A,B) :- A=[P,Q,R,S], 
				  rotate(R,T),
				  B=[P,Q,T,S]. 
%				  row(Q), 
%				  row(P), 
%				  row(S), 
				  
rowRotate(A,B) :- A=[P,Q,R,S], 
				  rotate(S,T),
				  B=[P,Q,R,T]. 
%				  row(Q), 
%				  row(R), 
%				  row(S), 
				  
colRotate(A,B) :- A=[[E,F,G,H],[I,J,K,L],[M,N,O,P],[Q,R,S,T]],
				  B=[[I,F,G,H],[M,J,K,L],[Q,N,O,P],[E,R,S,T]].
colRotate(A,B) :- A=[[E,F,G,H],[I,J,K,L],[M,N,O,P],[Q,R,S,T]],
				  B=[[E,J,G,H],[I,N,K,L],[M,R,O,P],[Q,F,S,T]].
colRotate(A,B) :- A=[[E,F,G,H],[I,J,K,L],[M,N,O,P],[Q,R,S,T]],
				  B=[[E,F,K,H],[I,J,O,L],[M,N,S,P],[Q,R,G,T]].
colRotate(A,B) :- A=[[E,F,G,H],[I,J,K,L],[M,N,O,P],[Q,R,S,T]], 
				  B=[[E,F,G,L],[I,J,K,P],[M,N,O,T],[Q,R,S,H]].

rowHeuristic(A, H) :- elemCounts(A, N),
					  N = [P,Q,R,S],
					  H is abs(P-1)+abs(Q-1)+abs(R-1)+abs(S-1).
heuristic(A,B) :- A = [P,Q,R,S],
				  rowHeuristic(P, H1),
				  rowHeuristic(Q, H2),
				  rowHeuristic(R, H3),
				  rowHeuristic(S, H4),
				  getCols(A,C),
				  C = [E,F,G,H],
				  rowHeuristic(E, H5),
				  rowHeuristic(F, H6),
				  rowHeuristic(G, H7),
				  rowHeuristic(H, H8),
				  B is H1+H2+H3+H4-H5-H6-H7-H8.	 




/*% Random walk
moveRandom(A,B,S) :- I is random(2),
			   I=0
			   -> (
			   		%write('-------R------'),
			   		nl,
			   		rowRotate(A,B),
			   	  	\+member(B,S)
			   	  )
			   ;  (
			   		%write('-------C------'),
			   		nl,
			   		colRotate(A,B),
			   		\+member(B,S)
			   ).
*/

% Row-preferred
move(A,B,S) :- rowRotate(A,B),
			   \+member(B,S).
move(A,B,S) :- colRotate(A,B),
			   \+member(B,S).

% Heuristic
/*
move(A,B,S) :- heuristic(A,H),
			   H =:= 0
			   ->	(	
			   			I is random(2),
			   			I =:= 0
			   			-> (
			   				write('-------R------'),
			   				nl,
			   				rowRotate(A,B),
			   	  			\+member(B,S)
			   	  			)
			   			;  (
			   				write('-------C------'),
			   				nl,
			   				colRotate(A,B),
			   				\+member(B,S)
			   				)
			   		)
			    ;	( 
			   			H < 0
			   			->	(
			   				colRotate(A,B),
			   				\+member(B,S)
			   				)
			   			;	(
			   				rowRotate(A,B),
			   				\+member(B,S)
			   				)
			   		).
*/

/*move(A,B,S) :- heuristic(A,H),
				 write(H),
				 nl,
				 H = 0
				 -> moveRandom(A,B,S)
				 ; moveClear(A,B,S).

moveClear(A,B,S) :- heuristic(A,H),
					H < 0
			   		->	(
							colRotate(A,B),
			   				\+member(B,S)
			   			)
			   		;	(
			   				rowRotate(A,B),
			   				\+member(B,S)
			   			).  

%move(A,B,S) :- moveRandom(A,B,S).
moveGen(A, S, M) :- findall(X, ( move(A,X), \+member(X, S) ), M).
*/
%isConsistent
/*
seach(A,S) :- validBoard(A)
			  ->	true
			  ;		(	moveGen(A,S,M),
			  			searchList(M,S)
			  		).
searchList([], S) :- false.
searchList([X|Y],S) :- 
*/

search(A,S,I) :- 
					validBoard(A)
				-> 	true
				;	(	
						move(A,B,S),
						append([A],S,NewSeen),
						%write('Iteration '),
						write(I),
						nl,
						J is I+1,
						search(B,NewSeen,J)

					).

isConsistent(A) :- search(A,[]).
%checkConsistency
checkConsistency(A) :- necessaryBoardTest(A)
					   ->	isConsistent(A)
					   ;	false.