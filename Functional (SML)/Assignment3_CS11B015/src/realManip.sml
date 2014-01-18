(* test for divisibility *)
fun divides(i : real, j : real)=
    if Real.==(Real.rem(i,j),0.0) then true
    else false;

fun checkPrime(i : real, j : real)=
    if j > i-1.0 then true
    else if divides(i,j)=true then false
    else checkPrime(i,j+1.0);

fun isPrime(i : real) = checkPrime(i,2.0);

(* factors of a real number *)
fun factors(i : real, j : real) = 
    if j > (i/2.0) then [i]
    else if divides(i,j) then j::factors(i,j+1.0)
    else factors(i, j+1.0);

fun getFactors(i: real) = factors(i, 1.0);

fun roundToDecimalPlaces(x: real) = 
    let
        val x1 = x*Math.pow(10.0, 6.0)
        val x2 = Real.realRound(x1);
        val x3 = x2/Math.pow(10.0, 6.0)
    in
        x3
    end;